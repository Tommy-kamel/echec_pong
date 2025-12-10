package com.example.echec_pong.network;

import java.io.Serializable;

public class GameStateUpdate implements Serializable {
    private static final long serialVersionUID = 1L;
    
    public enum UpdateType {
        PADDLE_MOVE,
        BALL_STATE,
        PIECE_HIT,
        GAME_OVER,
        SERVE_STATE,     // Initialiser le serveur
        SERVE_ACTION,    // Lancer la balle
        SERVE_ANGLE      // Changer l'angle
    }
    
    private UpdateType type;
    
    // Paddle data
    private double paddleX;
    private double paddleY;
    
    // Ball data
    private double ballX;
    private double ballY;
    private double ballVelX;
    private double ballVelY;
    
    // Piece data
    private int pieceRow;
    private int pieceCol;
    private int pieceHealth;
    private String pieceCouleur; // "blanc" ou "noir"
    private String pieceNom;     // "Pion", "Tour", "Cavalier", etc.
    
    // Game over data
    private String winner;
    
    // Serve data
    private String server;  // "white" ou "black"
    private double serveAngle;  // Angle en degrés
    
    public GameStateUpdate(UpdateType type) {
        this.type = type;
    }
    
    // Static factory methods
    public static GameStateUpdate paddleMove(double x, double y) {
        GameStateUpdate update = new GameStateUpdate(UpdateType.PADDLE_MOVE);
        update.paddleX = x;
        update.paddleY = y;
        return update;
    }
    
    public static GameStateUpdate ballState(double x, double y, double velX, double velY) {
        GameStateUpdate update = new GameStateUpdate(UpdateType.BALL_STATE);
        update.ballX = x;
        update.ballY = y;
        update.ballVelX = velX;
        update.ballVelY = velY;
        return update;
    }
    
    public static GameStateUpdate pieceHit(int row, int col, String couleur, String nom, int health) {
        GameStateUpdate update = new GameStateUpdate(UpdateType.PIECE_HIT);
        update.pieceRow = row;
        update.pieceCol = col;
        update.pieceCouleur = couleur;
        update.pieceNom = nom;
        update.pieceHealth = health;
        return update;
    }
    
    public static GameStateUpdate gameOver(String winner) {
        GameStateUpdate update = new GameStateUpdate(UpdateType.GAME_OVER);
        update.winner = winner;
        return update;
    }
    
    public static GameStateUpdate serveState(String server) {
        GameStateUpdate update = new GameStateUpdate(UpdateType.SERVE_STATE);
        update.server = server;
        return update;
    }
    
    public static GameStateUpdate serveAction() {
        return new GameStateUpdate(UpdateType.SERVE_ACTION);
    }
    
    public static GameStateUpdate serveAngle(double angle) {
        GameStateUpdate update = new GameStateUpdate(UpdateType.SERVE_ANGLE);
        update.serveAngle = angle;
        return update;
    }
    
    // Getters
    public UpdateType getType() { return type; }
    public double getPaddleX() { return paddleX; }
    public double getPaddleY() { return paddleY; }
    public double getBallX() { return ballX; }
    public double getBallY() { return ballY; }
    public double getBallVelX() { return ballVelX; }
    public double getBallVelY() { return ballVelY; }
    public int getPieceRow() { return pieceRow; }
    public int getPieceCol() { return pieceCol; }
    public int getPieceHealth() { return pieceHealth; }
    public String getPieceCouleur() { return pieceCouleur; }
    public String getPieceNom() { return pieceNom; }
    public String getWinner() { return winner; }
    public String getServer() { return server; }
    public double getServeAngle() { return serveAngle; }
}
