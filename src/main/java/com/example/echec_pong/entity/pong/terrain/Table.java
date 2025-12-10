package com.example.echec_pong.entity.pong.terrain;
import com.example.echec_pong.entity.pong.accessoires.Raquette;
import com.example.echec_pong.entity.pong.accessoires.Balle;
import java.util.List;

public class Table {
    private int longeur;
    private int largeur;
    private List<Raquette> raquettes;
    private Balle balle;

    public Table(int longeur, int largeur, List<Raquette> raquettes, Balle balle) {
        this.longeur = longeur;
        this.largeur = largeur;
        this.raquettes = raquettes;
        this.balle = balle;
    }

    public int getLongeur() {
        return longeur;
    }

    public int getLargeur() {
        return largeur;
    }

    public List<Raquette> getRaquettes() {
        return raquettes;
    }

    public Balle getBalle() {
        return balle;
    }

    public void setLongeur(int longeur) {
        this.longeur = longeur;
    }

    public void setLargeur(int largeur) {
        this.largeur = largeur;
    }

    public void setRaquettes(List<Raquette> raquettes) {
        this.raquettes = raquettes;
    }   

    public void setBalle(Balle balle) {
        this.balle = balle;
    }
}
