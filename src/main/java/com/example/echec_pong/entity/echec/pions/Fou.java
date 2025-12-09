package com.example.echec_pong.entity.echec.pions;

public class Fou extends Pion {
    
    public Fou(String couleur, int position) {
        super(couleur.equals("blanc") ? "♗" : "♝", "Fou", position, 5);
    }
    
    // Méthodes spécifiques au Fou si nécessaire
}