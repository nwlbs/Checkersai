package edu.upc.epsevg.prop.checkers.players;

import edu.upc.epsevg.prop.checkers.*;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;
import static java.lang.Math.max;
import static java.lang.Math.min;
import java.util.HashMap;
import java.util.LinkedList;

/**
 * Jugador minimaxIDS
 * @author Ferran, Nawal
 */

public class MyPlayerIDS implements IPlayer, IAuto {
    private final int MAX = Integer.MAX_VALUE; // ∞
    private final int MIN = Integer.MIN_VALUE; // -∞
    private int mDepth=1;
    private int nNodesExplorats=0;
    private boolean timeIsOut=false;
    private final boolean optimitzat=true;
    private String name;
    private final boolean _ambPoda=true;
    private long TAULERS_EXAMINATS=0; 
    private PlayerType Nosaltres;
    private PlayerType Oponent;
    private HashMap<ElMeuStatus, GameInfo> tTransp;
    private int _taulerspartida=0;
    private int nivellspartida=0;
    private double tempstotal=0;
    //Taula utilitzada per l'heuristica en les fitxes negres
    private static final int[][] VALUES_TABLEN = {
    { 0, 80, 0, 55, 0, 80, 0, 1 },
    { 1, 0, 7, 0, 7, 0, 5, 0 },
    { 0, 40, 0, 30, 0, 40, 0, 1 },
    { 1, 0, 10, 0, 10, 0, 5, 0 },
    { 0, 7, 0, 12, 0, 12, 0, 6 },
    { 8, 0, 13, 0, 13, 0, 13, 0 },
    { 9, 10, 14, 14, 14, 14, 10, 9 },
    { 10, 10, 15, 15, 15, 15, 10, 10}
    };
    //Taula utilitzada per l'heuristica en les fitxes queen ngeres
    private static final int[][] VALUES_TABLENF = {
    { 10, 10, 10, 10, 10, 10, 10, 10},
    { 5, 5, 5, 5, 5, 5, 5, 5 },
    { 3, 3, 3, 5, 5, 3, 3, 3 },
    { 2, 2, 5, 5, 5, 5, 2, 2 },
    { 1, 1, 5, 5, 5, 5, 1, 1 },
    { 1, 1, 3, 5, 5, 3, 1, 1 },
    { 0, 0, 2, 3, 3, 2, 0, 0 },
    { -1, 0, 0, 0, 0, 0, 0, -1 },
    };
    //Taula utilitzada per l'heuristica en les fitxes blanques
    private static final int[][] VALUES_TABLEB = {
    { 10, 10, 15, 15, 15, 15, 10, 10 },
    { 10, 10, 14, 14, 14, 14, 10, 9 },
    { 0, 13, 0, 13, 0, 13, 0, 8 },
    { 6, 0, 12, 0, 12, 0, 7, 0 },
    { 0, 5, 0, 10, 0, 10, 0, 1 },
    { 1, 0, 40, 0, 30, 0, 40, 0 },
    { 0, 5, 0, 7, 0, 7, 0, 1 },
    { 1, 0, 80, 0, 55, 0, 80, 0 }
    };
    //Taula utilitzada per l'heuristica en les fitxes queen blanques
    private static final int[][] VALUES_TABLEBF = {
    { -1, 0, 0, 0, 0, 0, 0, -1 },
    { 0, 0, 2, 3, 3, 2, 0, 0 },
    { 1, 1, 3, 5, 5, 3, 1, 1 },
    { 1, 1, 5, 5, 5, 5, 1, 1 },
    { 2, 2, 5, 5, 5, 5, 2, 2 },
    { 3, 3, 3, 5, 5, 3, 3, 3 },
    { 5, 5, 5, 5, 5, 5, 5, 5 },
    { 10, 10, 10, 10, 10, 10, 10, 10},
    };
    
    /**
     * Constructor PlayerID
     * @param name nom del jugador
     */
    public MyPlayerIDS(String name) {
        this.name = name;
        timeIsOut = false;
    }

    /**
     * Getter del nom del jugador
     * @return el nom del jugador
     */
    @Override
    public String getName() {
        return "MyPlayer (" + name + ")";
    }
    
    /**
     * Ens avisa que hem de parar la cerca en curs perquè s'ha exhaurit el temps
     * de joc.
     */
    @Override
    public void timeout() {
        timeIsOut=true;
    }

    /**
     * Decideix el moviment del jugador donat un tauler i un color de peça que
     * ha de posar.
     *
     * @param s Tauler i estat actual de joc.
     * @return el moviment que fa el jugador.
     */
    @Override
    public PlayerMove move(GameStatus s) {
        long tempsinicial = System.currentTimeMillis();
        ElMeuStatus q=new ElMeuStatus(s);
        TAULERS_EXAMINATS=0;
        nNodesExplorats=0;
        timeIsOut=false;
        tTransp = new HashMap<>();
        int best_valor=MIN; 
        Nosaltres = q.getCurrentPlayer(); 
        Oponent= PlayerType.opposite(q.getCurrentPlayer());
        //System.out.print(Nosaltres +" "+ Oponent);
        List<Point> best_cami = null;  
        List<List<Point>> allPaths = new ArrayList<>(); //llista de camins [(1-3),(1-2-4)]
        List<MoveNode> moves =  q.getMoves(); //#posicions=#peces que es poden moure
        for (MoveNode move : moves) {
            getPaths(move,allPaths); // Agregar la función path que construye la lista de puntos
        }
        //Inici: ordenar primer node a explorar a la cerca
        GameInfo newG = null;
        if(optimitzat) newG = ordenarPrimerNode(newG, q, allPaths);
        //Fi: ordenar primer node a explorar a la cerca
        List<Point> best_cami_ids = null;
        //loop de profunditats IDS
        for(int prof=1; prof<=100; prof++){
            mDepth=prof;
            nNodesExplorats++;
            int c =0;
            int millorI=0;
            best_cami = null;
            best_valor=MIN;
            for(List<Point> cami_candidat : allPaths){
                ElMeuStatus aux=new ElMeuStatus(q);
                aux.movePiece(cami_candidat);
                nNodesExplorats++;
                int actual=minValorIDS(aux,prof-1,MIN,MAX);
                if(best_cami==null) best_cami=cami_candidat;
                if(timeIsOut) {
                     nivellspartida+=mDepth-1;
                     long tempsfinal = System.currentTimeMillis();
                    double temps =  (tempsfinal- tempsinicial)/1000.0;    //calcular el temps 
                    tempstotal+=temps;
                    System.out.println("Temps: " + temps + " s" + "  TEMPS TOTAL:"+tempstotal );
                    System.out.println("TAULERS EXPLORATS EN LA PARTIDA: " + _taulerspartida + " NIVELLS EXPLORATS EN LA PARTIDA: " + nivellspartida);
                    return new PlayerMove( best_cami_ids, nNodesExplorats, mDepth-1, SearchType.MINIMAX_IDS);
                }
                if (best_valor < actual){
                    best_valor=actual;
                    best_cami=cami_candidat;
                    millorI=c;
                }
            c++;
            }
            best_cami_ids=best_cami;
            if(optimitzat) guardarGameInfo(newG, q, millorI, prof);
            _taulerspartida+=nNodesExplorats;  
        }
        nivellspartida+=mDepth-1;
        long tempsfinal = System.currentTimeMillis();
        double temps =  (tempsfinal- tempsinicial)/1000.0;    //calcular el temps 
        tempstotal+=temps;
        System.out.println("Temps: " + temps + " s" + "  TEMPS TOTAL:"+tempstotal );
        System.out.println("TAULERS EXPLORATS EN LA PARTIDA: " + _taulerspartida + " NIVELLS EXPLORATS EN LA PARTIDA: " + nivellspartida);
        return new PlayerMove( best_cami, nNodesExplorats, mDepth, SearchType.MINIMAX_IDS);
    }
    
    /**
     * Si el tauler actual es troba en el HashMap,
     * ordena la llista allPaths perque el millor moviment sigui 
     * el primer a explorar
     * @param newG Informacio del joc inicialitzada a null
     * @param q Estat del tauler actual
     * @param allPaths Llista dels possibles moviments del tauler actual
     * @return el nou GameInfo amb la informacio extreta del HashMap
     */
    public GameInfo ordenarPrimerNode(GameInfo newG,ElMeuStatus q, List<List<Point>> allPaths){
        if(tTransp!=null && tTransp.containsKey(q)){
            newG =tTransp.get(q);
            int best_index=newG.getIndexMillorMoviment();
            if(best_index!=0 && best_index<allPaths.size()){ //Proteccio pels casos d'error
                //Swap de posicions amb el primer i el millor
                List<Point> pos0 =allPaths.get(0);
                allPaths.set(0, allPaths.get(best_index));//ERROR D'EXECUCIÓ AQUI!!
                allPaths.set(best_index, pos0);
            }
        }
        return newG;
    }
    
    /**
     * Guardar informació del tauler en el HashMap
     * @param newG Informacio del tauler a guardar
     * @param q Estat del tauler actual
     * @param millorI Index del millor moviment trobat en el minimax actual
     * @param prof El numero de nivells per sota del millor moviment trobat en el minimax actual
     */
    public void guardarGameInfo(GameInfo newG, ElMeuStatus q, int millorI, int prof){
        //System.out.println("GuardarGameInfoINDEX: "+millorI);
        GameInfo gameInfo = new GameInfo(millorI,prof);
        if(newG!=null){ 
            if(newG.getNivellsPerSota()< gameInfo.getNivellsPerSota()){
                 tTransp.put(q, gameInfo);
            }
        }else{
            tTransp.put(q, gameInfo);
        }
    }
    
    /**
     * Part MAX del algorisme minimaxIDS
     * @param q
     * @param depth
     * @param alpha
     * @param beta
     * @return La tirada maxima possible
     */
    public int maxValorIDS(ElMeuStatus q, int depth, int alpha, int beta) {
        nNodesExplorats++;
        if(timeIsOut) return 0;
        //put
        int best_valor=MIN; 
        List<List<Point>> allPaths = new ArrayList<>(); //llista de camins [(1-3),(1-2-4)]
        if(q.isGameOver()){ //checkgameover
            if(q.GetWinner()==null) {
                return 0;
            }//Cas empat
            int prof=depth-mDepth;
            prof=-prof;
            return MIN+prof;
        }
        else if(!q.currentPlayerCanMove()|| depth==0) return heuristica(q);
        else{
            List<MoveNode> moves =  q.getMoves(); //#posicions=#peces que es poden moure
            for (MoveNode move : moves) {
                getPaths(move, allPaths); // Agregar la función path que construye la lista de puntos
            }
            //Ordenar cerca
            GameInfo newG = null;
            if(optimitzat) newG = ordenarPrimerNode(newG, q, allPaths);
            //Fi ordenar cerca
            int c =0;
            int millorI=0;
            for(List<Point> cami_candidat : allPaths){
                ElMeuStatus aux=new ElMeuStatus(q);  
                aux.movePiece(cami_candidat);
                best_valor = max(best_valor,minValorIDS(aux, depth-1, alpha, beta));
                if(_ambPoda){
                    alpha = max(best_valor, alpha);
                    if(beta <= alpha){
                        millorI=c;
                        break;
                    }                     
                }
            c++;
            }
            if(optimitzat)guardarGameInfo(newG, q, millorI, depth);
        }
        return best_valor;
    }
    
    /**
     * Part MIN del algorisme minimax
     * @param q
     * @param depth
     * @param alpha
     * @param beta
     * @return La tirada minima possible
     */
    public int minValorIDS(ElMeuStatus q, int depth, int alpha, int beta) {
        nNodesExplorats++;
        if(timeIsOut) return 0;
        int best_valor=MAX; 
        List<List<Point>> allPaths = new ArrayList<>(); //llista de camins [(1-3),(1-2-4)]
        if(q.isGameOver()){
            if(q.GetWinner()==null) return 0;//Cas empat
            return MAX;
        } //checkgameover
        else if(!q.currentPlayerCanMove()|| depth==0) return heuristica(q);
        else{
            List<MoveNode> moves =  q.getMoves(); //#posicions=#peces que es poden moure
            for (MoveNode move : moves) {
                getPaths(move, allPaths); // Agregar la función path que construye la lista de puntos
            }
            //Ordenar cerca
            GameInfo newG = null; 
            if(optimitzat) newG = ordenarPrimerNode(newG, q, allPaths);
            //Fi ordenar cerca 
            int c =0;
            int millorI=0;
            for(List<Point> cami_candidat : allPaths){
                ElMeuStatus aux=new ElMeuStatus(q);  
                aux.movePiece(cami_candidat);
                best_valor = min(best_valor,maxValorIDS(aux, depth-1, alpha, beta));
                if(_ambPoda){
                    beta = min(best_valor, beta);
                    if(beta <= alpha){
                        millorI=c;
                        break;
                    }                     
                }
            c++;
            }
            if(optimitzat) guardarGameInfo(newG, q, millorI, depth);
        }
        return best_valor;
    }

     /**
     * Mètode recursiu per obtenir una llista de llistes de nodes que representen camins
     * @param node node arrel d'un posible moviment
     * @param paths llista de llista de punts (camins de moviment possibles)
     */    
    public static void getPaths(MoveNode node, List<List<Point>> paths) {
        if (node.getChildren().isEmpty()) {
            List<Point> currentPath = new LinkedList<>(); //lista 1-3
            MoveNode parent=node;
            while(parent != null){
                currentPath.add(0,parent.getPoint());
                parent=parent.getParent();
            }
            paths.add(currentPath);
        } else {
            List<MoveNode> children = node.getChildren();
            for (MoveNode child : children) {
                getPaths(child, paths);
            }
        }
    }
        
    /**
     * Heuristica del alogrisme minimax que avlalua el valor d'un tauler qualsevol
     * @param s tauler a avaluar
     * @return El valor del tauler
     */
    public int heuristica(ElMeuStatus s) {
        TAULERS_EXAMINATS++;
        int h = 0;//valor heuristica
        int nAliadesEnemigues=0; //= num_fitxes_nostres - num_fitxes_rivals
        for (    int i = 0; i < s.getSize(); i++) {
            for (int j = 0; j < s.getSize(); j++) {
                CellType p=s.getPos(i,j); 
                PlayerType piece = p.getPlayer();
                if (piece == Nosaltres) { //nosotros
                    nAliadesEnemigues++;
                    if(Nosaltres==PlayerType.PLAYER1 && !p.isQueen())h+=VALUES_TABLEN[i][j];
                    else if(Nosaltres==PlayerType.PLAYER1 && p.isQueen())h+=VALUES_TABLENF[i][j];
                    else if(Nosaltres==PlayerType.PLAYER2 && !p.isQueen())h+=VALUES_TABLEBF[i][j];
                    else if(Nosaltres==PlayerType.PLAYER2 && p.isQueen())h+=VALUES_TABLEB[i][j];
                    if(p.isQueen()){
                       h+=30;
                    }
                }
                else if (piece == Oponent){
                    nAliadesEnemigues--;
                    if(p.isQueen()){
                       h-=30;
                    }
                    
                }
            }
        }
        //Nivell d'agressio es controla comprovant si anem guanyant en quant a num de fixes
        if(nAliadesEnemigues>0) h+=(nAliadesEnemigues*100);
        else if(nAliadesEnemigues>1) h+=(nAliadesEnemigues*200);
        else h+=(nAliadesEnemigues*50);
        return h;
    }
}