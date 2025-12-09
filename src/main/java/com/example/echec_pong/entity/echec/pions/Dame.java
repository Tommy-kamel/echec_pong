package com.example.echec_pong.entity.echec.pions;

public class Dame extends Pion {
    
    public Dame(String couleur, int position) {
        super(couleur.equals("blanc") ? "♕" : "♛", "Dame", position, 8);
    }
    
    // Méthodes spécifiques à la Dame si nécessaire
}