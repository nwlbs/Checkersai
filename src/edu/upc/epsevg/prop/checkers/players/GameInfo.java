package edu.upc.epsevg.prop.checkers.players;


/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

/**
 *
 * @author ferran
 */
public class GameInfo {
    private int indexMillorMoviment;
    private int nivellsPerSota;
    //0Blanca, 1BlancaReina, 2Negra, 3 NegraReina
    //private int peca;
    
    public GameInfo(int index, int nivells){
        this.indexMillorMoviment=index;
        this.nivellsPerSota=nivells;
    }

    public int getIndexMillorMoviment() {
        return indexMillorMoviment;
    }

    public int getNivellsPerSota() {
        return nivellsPerSota;
    }
    
    
}

