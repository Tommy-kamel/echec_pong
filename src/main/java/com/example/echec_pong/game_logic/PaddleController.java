package com.example.echec_pong.game_logic;

public class PaddleController {
    private final GameState gameState;
    private final int boardWidth;
    private final double paddleSpeed = 15.0; // Vitesse augmentée pour meilleure réactivité
    
    public PaddleController(GameState gameState, int boardWidth) {
        this.gameState = gameState;
        this.boardWidth = boardWidth;
    }
    
    public void moveBlackPaddleLeft() {
        double newX = gameState.getRaquetteNoir().getPositionX() - paddleSpeed;
        if(newX >= 0) {
            gameState.getRaquetteNoir().setPositionX(newX);
        } 
    }
    
    public void moveBlackPaddleRight() {
        double newX = gameState.getRaquetteNoir().getPositionX() + paddleSpeed;
        double maxX = (gameState.getBoardWidth() * gameState.getCellSize()) - gameState.getRaquetteNoir().getLargeur();
        if(newX <= maxX) {
            gameState.getRaquetteNoir().setPositionX(newX);
        }
    }
    
    public void moveWhitePaddleLeft() {
        double newX = gameState.getRaquetteBlanc().getPositionX() - paddleSpeed;
        if(newX >= 0) {
            gameState.getRaquetteBlanc().setPositionX(newX);
        }
    }
    
    public void moveWhitePaddleRight() {
        double newX = gameState.getRaquetteBlanc().getPositionX() + paddleSpeed;
        double maxX = (gameState.getBoardWidth() * gameState.getCellSize()) - gameState.getRaquetteBlanc().getLargeur();
        if(newX <= maxX) {
            gameState.getRaquetteBlanc().setPositionX(newX);
        }
    }
}
