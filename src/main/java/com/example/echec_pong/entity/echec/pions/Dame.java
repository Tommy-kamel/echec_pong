package com.example.echec_pong.entity.echec.pions;

public class Dame extends Pion {

    public Dame(String couleur, int position, int vie) {
        super(couleur.equals("blanc") ? "♕" : "♛", "Dame", position, vie);
    }

    // Méthodes spécifiques à la Dame si nécessaire
}
