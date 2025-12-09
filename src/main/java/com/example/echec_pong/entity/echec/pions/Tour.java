package com.example.echec_pong.entity.echec.pions;

public class Tour extends Pion {
    
    public Tour(String couleur, int position) {
        super(couleur.equals("blanc") ? "♖" : "♜", "Tour", position, 5);
    }
    
    // Méthodes spécifiques à la Tour si nécessaire
}
