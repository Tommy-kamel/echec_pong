package com.example.echec_pong.entity.pong.accessoires;

public class Raquette {
    private double positionX;
    private double positionY;
    private double largeur;
    private double hauteur;
    private String joueur;

    public Raquette(double positionX, double positionY, double largeur, double hauteur, String joueur) {
        this.positionX = positionX;
        this.positionY = positionY;
        this.largeur = largeur;
        this.hauteur = hauteur;
        this.joueur = joueur;
    }

    public double getPositionX() {
        return positionX;
    }

    public void setPositionX(double positionX) {
        this.positionX = positionX;
    }

    public double getPositionY() {
        return positionY;
    }

    public void setPositionY(double positionY) {
        this.positionY = positionY;
    }

    public double getLargeur() {
        return largeur;
    }

    public void setLargeur(double largeur) {
        this.largeur = largeur;
    }

    public double getHauteur() {
        return hauteur;
    }

    public void setHauteur(double hauteur) {
        this.hauteur = hauteur;
    }

    public String getJoueur() {
        return joueur;
    }

    public void deplacerGauche(double vitesse) {
        positionX -= vitesse;
    }

    public void deplacerDroite(double vitesse) {
        positionX += vitesse;
    }
}