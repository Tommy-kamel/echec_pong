package com.example.echec_pong.entity.pong.accessoires;

public class Balle {
    private int positionX;
    private int positionY;

    public Balle(int positionX, int positionY, int vitesseX, int vitesseY) {
        this.positionX = positionX;
        this.positionY = positionY;
    }

    public int getPositionX() {
        return positionX;
    }

    public int getPositionY() {
        return positionY;
    }

    public void setPositionX(int positionX) {
        this.positionX = positionX;
    }

    public void setPositionY(int positionY) {
        this.positionY = positionY;
    }
}
