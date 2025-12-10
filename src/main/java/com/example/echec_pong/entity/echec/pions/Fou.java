package com.example.echec_pong.entity.echec.pions;

public class Fou extends Pion {
    
    public Fou(String couleur, int position, int vie) {
        super(couleur.equals("blanc") ? "♗" : "♝", "Fou", position, vie);
    }
    
    // Méthodes spécifiques au Fou si nécessaire
}