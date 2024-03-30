package edu.upc.epsevg.prop.checkers.players;




import edu.upc.epsevg.prop.checkers.GameStatus;
import edu.upc.epsevg.prop.checkers.*;
import java.util.HashMap;
import java.util.Random;

/**
 *
 * @author Usuari
 */
public class ElMeuStatus extends GameStatus {
    
    
    static private long[][][] zobrist;
    static private long black;
    static  {
        newZobrist();
    }
    
    public ElMeuStatus(int [][] tauler){
        super(tauler);
    }
    //new gamestatus=pasant el meu status)
     public ElMeuStatus(GameStatus gs){
        super(gs);
    }
    
    //Per cada posició del tauler li donem un valor a cada peça possible
    static private void newZobrist(){
        zobrist = new long[8][8][4];
        Random random = new Random();
        // Inicialitzem la matriu amb valors aleatoris
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                for (int k = 0; k < 4; k++) {
                    zobrist[i][j][k] = random.nextLong();
                }
            }
        }
        black = random.nextLong();
    }
     
     
     
    //metode gethash
     /*
     @Override
     oublic void movePiece(List<Point> list){
        super.movePiece(list);
     //actualitzar hash[int i fer xor] 'fer xor posicionini(eliminar)-posiciofinal' matar: xor(posicioini(eliminar), matar pecçaoponent, posicionfinal)'
     }
     */

    @Override
    public boolean equals(Object o) {
        return this.hashCode()==o.hashCode(); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/OverriddenMethodBody
    }
    @Override
    public int hashCode(){
        long hash  = 0;
        if(getCurrentPlayer()==PlayerType.PLAYER1) {
             hash ^= black;
        }
        //Recorrer la matiu estatica amb els valors aleatoris i el GameStatus
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                //obtenir peça del GameStatus
                if(getPos(i, j) != CellType.EMPTY){
                    for (int k = 0; k < 4; k++) {
                        //XOR al hash amb el valor de la nova peça obtinguda
                        hash ^= zobrist[i][j][k];
                    }
                }
            }
        }
        int h=(int) hash;
        return h;
    }
}
