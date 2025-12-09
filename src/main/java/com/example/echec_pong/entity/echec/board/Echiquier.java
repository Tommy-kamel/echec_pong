package com.example.echec_pong.entity.echec.board;
import com.example.echec_pong.entity.echec.pions.Pion;
import java.util.List;

public class Echiquier {
    private int taille;
    private List<Pion> pions;
    
    public Echiquier(int taille, List<Pion> pions) {
        this.taille = taille;
        this.pions = pions;
    }
}
