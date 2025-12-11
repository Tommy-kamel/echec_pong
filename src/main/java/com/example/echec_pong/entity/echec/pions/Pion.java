package com.example.echec_pong.entity.echec.pions;

public class Pion {
    private String symbol;
    private String nom;
    private int position;
    private int point_de_vie;
    private int point_de_vie_max; // Santé maximale pour la jauge
    private String couleur;

    public Pion(String symbol, String nom, int position, int point_de_vie) {
        this.symbol = symbol;
        this.nom = nom;
        this.position = position;
        this.point_de_vie = point_de_vie;
        this.point_de_vie_max = point_de_vie; // Initialiser max à la valeur initiale
        // Infer color from symbol (white pieces: ♔♕♖♗♘♙, black pieces: ♚♛♜♝♞♟)
        this.couleur = (symbol.equals("♔") || symbol.equals("♕") || symbol.equals("♖") || 
                        symbol.equals("♗") || symbol.equals("♘") || symbol.equals("♙")) ? "blanc" : "noir";
    }

    public Pion(String couleur, int position, int vie) {
        this(couleur.equals("blanc") ? "♙" : "♟", "Pion", position, vie);
        this.couleur = couleur;
    }

    public String getSymbol() {
        return symbol;
    }

    public String getNom() {
        return nom;
    }

    public int getPosition() {
        return position;
    }

    public int getPoint_de_vie() {
        return point_de_vie;
    }
    
    // Alias methods for compatibility
    public int getSante() {
        return point_de_vie;
    }
    
    public int getSanteMax() {
        return point_de_vie_max;
    }
    
    public int getHealth() {
        return point_de_vie;
    }
    
    public String getCouleur() {
        return couleur;
    }
    
    public String getColor() {
        return couleur;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public void setPoint_de_vie(int point_de_vie) {
        this.point_de_vie = point_de_vie;
    }
    
    public void setSante(int sante) {
        this.point_de_vie = sante;
    }
    
    public void setHealth(int health) {
        this.point_de_vie = health;
    }
    
    public void setCouleur(String couleur) {
        this.couleur = couleur;
    }

    public void setPosition(int position) {
        this.position = position;
    }

}
