package com.example.echec_pong.entity.echec.pions;

public class Roi extends Pion {

    public Roi(String couleur, int position, int vie) {
        super(couleur.equals("blanc") ? "♔" : "♚", "Roi", position, vie);
    }

    // Méthodes spécifiques au Roi si nécessaire
}
