package edu.upc.epsevg.prop.checkers.players;

import edu.upc.epsevg.prop.checkers.*;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;
import static java.lang.Math.max;
import static java.lang.Math.min;
import java.util.LinkedList;

/**
 * Jugador minimax
 * @author Ferran, Nawal
 */
public class MyPlayer implements IPlayer, IAuto {
    private final int MAX = Integer.MAX_VALUE; // ∞
    private final int MIN = Integer.MIN_VALUE; // -∞
    private int mDepth=8;
    private int nNodesExplorats=0;
    private String name;
    private final boolean _ambPoda=true;
    private long TAULERS_EXAMINATS=0; 
    private PlayerType Nosaltres;
    private PlayerType Oponent;
    private int nivellspartida=0;
    private int _taulerspartida=0;
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
     * Inicialitza el nou jugador amb el seu nom
     * @param name
     */
    public MyPlayer(String name) {
        this.name = name;
    }
    
    /**
     * Getter del nom del jugador
     * @return el nom del jugador
     */
    @Override
    public String getName() {
        return "MyPlayer(" + name + ")";
    }
    
    /**
     * Ens avisa que hem de parar la cerca en curs perquè s'ha exhaurit el temps
     * de joc.
     */
    @Override
    public void timeout() {
    }

    /**
     * Decideix el moviment del jugador donat un tauler i un color de peça que
     * ha de posar.
     *
     * @param s Tauler i estat actual de joc.
     * @return el millor moviment que pot fer el jugador.
     */
    @Override
    public PlayerMove move(GameStatus s) {
       long tempsinicial = System.currentTimeMillis();
       TAULERS_EXAMINATS=0;
       nNodesExplorats=0;
       int best_valor=MIN; 
       Nosaltres = s.getCurrentPlayer();
       Oponent= PlayerType.opposite(s.getCurrentPlayer());  
       List<Point> best_cami = null;
        List<List<Point>> allPaths = new ArrayList<>(); //llista de camins [(1-3),(1-2-4)]
            List<MoveNode> moves =  s.getMoves(); //#posicions=#peces que es poden moure
            for (MoveNode move : moves) {
                getPaths(move,allPaths); // Agregar la función path que construye la lista de puntos
            }
            for(List<Point> cami_candidat : allPaths){
                GameStatus aux=new GameStatus(s);
                aux.movePiece(cami_candidat);
                nNodesExplorats++;
                int actual=minValor(aux,mDepth-1,MIN,MAX);
                if(best_cami==null) best_cami=cami_candidat;
                if (best_valor < actual){
                    best_valor=actual;
                    best_cami=cami_candidat;
                }
            }           
        nivellspartida+=mDepth-1;
        _taulerspartida+=nNodesExplorats; 
        long tempsfinal = System.currentTimeMillis();
        double temps =  (tempsfinal- tempsinicial)/1000.0;    //calcular el temps 
        tempstotal+=temps;
        System.out.println("Temps: " + temps + " s" + "  TEMPS TOTAL:"+tempstotal );
        System.out.println("TAULERS EXPLORATS EN LA PARTIDA: " + _taulerspartida + " NIVELLS EXPLORATS EN LA PARTIDA: " + nivellspartida);
        return new PlayerMove( best_cami, nNodesExplorats, mDepth, SearchType.MINIMAX);
    }
    
    /**
     * Part MAX del algorisme minimax
     * @param s
     * @param depth
     * @param alpha
     * @param beta
     * @return La tirada maxima possible
     */
    public int maxValor(GameStatus s, int depth, int alpha, int beta) {
        nNodesExplorats++;
        int best_valor=MIN; 
        List<List<Point>> allPaths = new ArrayList<>(); //llista de camins [(1-3),(1-2-4)]
        if(s.isGameOver()){ //checkgameover
            if(s.GetWinner()==null) {
                System.out.println("EMPAT");
                return 0;
            }//Cas empat
            int prof=depth-mDepth;
            prof=-prof;
            return MIN+prof;
        }
        else if(!s.currentPlayerCanMove()|| depth==0) return heuristica(s);
        else{
            List<MoveNode> moves =  s.getMoves(); //#posicions=#peces que es poden moure
            for (MoveNode move : moves) {
                getPaths(move,allPaths); // Agregar la función path que construye la lista de puntos
            }  
            for(List<Point> cami_candidat : allPaths){
                GameStatus aux=new GameStatus(s);  
                aux.movePiece(cami_candidat);
                best_valor = max(best_valor,minValor(aux, depth-1, alpha, beta));
                if(_ambPoda){
                    alpha = max(best_valor, alpha);
                    if(beta <= alpha)break;                     
                }
            }           
        }
        return best_valor;
    }
    
    /**
     * Part MIN del algorisme minimax
     * @param s
     * @param depth
     * @param alpha
     * @param beta
     * @return La tirada minima possible
     */
    public int minValor(GameStatus s, int depth, int alpha, int beta) {
        nNodesExplorats++;
        int best_valor=MAX; 
        List<List<Point>> allPaths = new ArrayList<>(); //llista de camins [(1-3),(1-2-4)]
        if(s.isGameOver()){
            if(s.GetWinner()==null) {
                System.out.println("EMPAT");
                return 0;
            }//Cas empat
            return MAX;
        } //checkgameover
        else if(!s.currentPlayerCanMove()|| depth==0) return heuristica(s);
        else{
            List<MoveNode> moves =  s.getMoves(); //#posicions=#peces que es poden moure
            for (MoveNode move : moves) {
                getPaths(move,allPaths); // Agregar la función path que construye la lista de puntos
            } 
            for(List<Point> cami_candidat : allPaths){
                GameStatus aux=new GameStatus(s);  
                aux.movePiece(cami_candidat);
                best_valor = min(best_valor,maxValor(aux, depth-1, alpha, beta));
                if(_ambPoda){
                    beta = min(best_valor, beta);
                    if(beta <= alpha)break;                     
                }
            }          
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
    public int heuristica(GameStatus s) {
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
        if(nAliadesEnemigues>0) h+=(nAliadesEnemigues*100);
        else if(nAliadesEnemigues>1) h+=(nAliadesEnemigues*200);
        else h+=(nAliadesEnemigues*50);
        return h;
    }
}