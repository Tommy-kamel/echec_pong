package com.example.echec_pong.entity.pong.accessoires;

public class Balle {
    private double positionX;
    private double positionY;
    private double vitesseX;
    private double vitesseY;
    private double rayon;

    public Balle(double positionX, double positionY, double vitesseX, double vitesseY, double rayon) {
        this.positionX = positionX;
        this.positionY = positionY;
        this.vitesseX = vitesseX;
        this.vitesseY = vitesseY;
        this.rayon = rayon;
    }

    public double getPositionX() {
        return positionX;
    }

    public double getPositionY() {
        return positionY;
    }

    public void setPositionX(double positionX) {
        this.positionX = positionX;
    }

    public void setPositionY(double positionY) {
        this.positionY = positionY;
    }

    public double getVitesseX() {
        return vitesseX;
    }

    public void setVitesseX(double vitesseX) {
        this.vitesseX = vitesseX;
    }

    public double getVitesseY() {
        return vitesseY;
    }

    public void setVitesseY(double vitesseY) {
        this.vitesseY = vitesseY;
    }

    public double getRayon() {
        return rayon;
    }

    public void setRayon(double rayon) {
        this.rayon = rayon;
    }

    public void deplacer() {
        positionX += vitesseX;
        positionY += vitesseY;
    }

    public void inverserVitesseX() {
        vitesseX = -vitesseX;
    }

    public void inverserVitesseY() {
        vitesseY = -vitesseY;
    }
}
