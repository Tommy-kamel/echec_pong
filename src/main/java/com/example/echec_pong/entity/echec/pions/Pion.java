package com.example.echec_pong.entity.echec.pions;

public abstract class Pion {
    private String symbol;
    private String nom;
    private int position;
    private int point_de_vie;

    public Pion(String symbol, String nom,int position, int point_de_vie) {
        this.symbol = symbol;
        this.nom = nom;
        this.position = position;
        this.point_de_vie = point_de_vie;
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

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public void setPoint_de_vie(int point_de_vie) {
        this.point_de_vie = point_de_vie;
    }

    public void setPosition(int position) {
        this.position = position;
    }

}
