package com.example.echec_pong.entity.echec.pions;

public class Cavalier extends Pion {

    public Cavalier(String couleur, int position, int vie) {
        super(couleur.equals("blanc") ? "♘" : "♞", "Cavalier", position, vie);
    }

    // Méthodes spécifiques au Cavalier si nécessaire
}
