package com.example.echec_pong.game_logic;

import com.example.echec_pong.entity.echec.pions.Pion;
import com.example.echec_pong.entity.pong.accessoires.Balle;
import com.example.echec_pong.entity.pong.accessoires.Raquette;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GameState {
    private final int boardWidth;
    private final int boardRows = 8;
    private final double cellSize = 60.0;
    
    private List<Pion> blackMainPieces;
    private List<Pion> blackPawns;
    private List<Pion> whiteMainPieces;
    private List<Pion> whitePawns;
    
    private Raquette raquetteNoir;
    private Raquette raquetteBlanc;
    private Balle balle;
    
    private Map<Pion, GridPosition> piecePositions;
    private boolean isGameOver;
    private String winner;
    
    // Service state
    private String currentServer; // "white" or "black"
    private boolean waitingForServe;
    private double serveAngle; // Angle en degrés: -45 (gauche), 0 (centre), 45 (droite)
    
    public GameState(int boardWidth) {
        this.boardWidth = boardWidth;
        this.blackMainPieces = new ArrayList<>();
        this.blackPawns = new ArrayList<>();
        this.whiteMainPieces = new ArrayList<>();
        this.whitePawns = new ArrayList<>();
        this.piecePositions = new HashMap<>();
        this.isGameOver = false;
        this.waitingForServe = true;
        this.serveAngle = 0.0; // default au centre
    }
    
    public void addPiece(Pion piece, int row, int col) {
        piecePositions.put(piece, new GridPosition(row, col));
        if(row == 0) {
            blackMainPieces.add(piece);
        } else if(row == 1) {
            blackPawns.add(piece);
        } else if(row == boardRows - 2) {
            whitePawns.add(piece);
        } else if(row == boardRows - 1) {
            whiteMainPieces.add(piece);
        }
    }
    
    public Pion getPieceAt(int row, int col) {
        for(Map.Entry<Pion, GridPosition> entry : piecePositions.entrySet()) {
            GridPosition pos = entry.getValue();
            if(pos.row == row && pos.col == col && entry.getKey().getSante() > 0) {
                return entry.getKey();
            }
        }
        return null;
    }
    
    public Pion getPieceAtWithCouleurNom(int row, int col, String couleur, String nom) {
        for(Map.Entry<Pion, GridPosition> entry : piecePositions.entrySet()) {
            GridPosition pos = entry.getValue();
            Pion piece = entry.getKey();
            if(pos.row == row && pos.col == col && 
               piece.getCouleur().equals(couleur) && 
               piece.getNom().equals(nom)) {
                return piece;
            }
        }
        return null;
    }
    
    public GridPosition getPosition(Pion piece) {
        return piecePositions.get(piece);
    }
    
    public GridPosition getPiecePosition(Pion piece) {
        return piecePositions.get(piece);
    }
    
    public void removePiece(Pion piece) {
        piecePositions.remove(piece);
        blackMainPieces.remove(piece);
        blackPawns.remove(piece);
        whiteMainPieces.remove(piece);
        whitePawns.remove(piece);
    }
    
    public List<Pion> getAllPieces() {
        List<Pion> all = new ArrayList<>();
        all.addAll(blackMainPieces);
        all.addAll(blackPawns);
        all.addAll(whiteMainPieces);
        all.addAll(whitePawns);
        return all;
    }
    
    public List<Pion> getBlackPieces() {
        List<Pion> pieces = new ArrayList<>();
        pieces.addAll(blackMainPieces);
        pieces.addAll(blackPawns);
        return pieces;
    }
    
    public List<Pion> getWhitePieces() {
        List<Pion> pieces = new ArrayList<>();
        pieces.addAll(whiteMainPieces);
        pieces.addAll(whitePawns);
        return pieces;
    }
    
    // Getters et setters
    public int getBoardWidth() { return boardWidth; }
    public int getBoardRows() { return boardRows; }
    public double getCellSize() { return cellSize; }
    
    public Raquette getRaquetteNoir() { return raquetteNoir; }
    public void setRaquetteNoir(Raquette raquette) { this.raquetteNoir = raquette; }
    
    public Raquette getRaquetteBlanc() { return raquetteBlanc; }
    public void setRaquetteBlanc(Raquette raquette) { this.raquetteBlanc = raquette; }
    
    public Balle getBalle() { return balle; }
    public void setBalle(Balle balle) { this.balle = balle; }
    
    public boolean isGameOver() { return isGameOver; }
    public void setGameOver(boolean gameOver) { this.isGameOver = gameOver; }
    
    public String getWinner() { return winner; }
    public void setWinner(String winner) { this.winner = winner; }
    
    public String getCurrentServer() { return currentServer; }
    public void setCurrentServer(String server) { this.currentServer = server; }
    
    public boolean isWaitingForServe() { return waitingForServe; }
    public void setWaitingForServe(boolean waiting) { this.waitingForServe = waiting; }
    
    public double getServeAngle() { return serveAngle; }
    public void setServeAngle(double angle) { this.serveAngle = angle; }
    
    public static class GridPosition {
        public final int row;
        public final int col;
        
        public GridPosition(int row, int col) {
            this.row = row;
            this.col = col;
        }
    }
}
