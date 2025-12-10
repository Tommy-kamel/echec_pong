package com.example.echec_pong.entity.echec.pions;

public class Tour extends Pion {
    
    public Tour(String couleur, int position, int vie) {
        super(couleur.equals("blanc") ? "♖" : "♜", "Tour", position, vie);
    }
    
    // Méthodes spécifiques à la Tour si nécessaire
}
