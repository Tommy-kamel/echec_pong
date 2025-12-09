package com.example.echec_pong.entity.echec.pions;

public class Cavalier extends Pion {

    public Cavalier(String couleur, int position) {
        super(couleur.equals("blanc") ? "♘" : "♞", "Cavalier", position, 3);
    }

    // Méthodes spécifiques au Cavalier si nécessaire
}
