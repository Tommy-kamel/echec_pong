package com.example.echec_pong.entity.echec.pions;

public class Roi extends Pion {
    
    public Roi(String couleur, int position) {
        super(couleur.equals("blanc") ? "♔" : "♚", "Roi", position, 10);
    }
    
    // Méthodes spécifiques au Roi si nécessaire
}