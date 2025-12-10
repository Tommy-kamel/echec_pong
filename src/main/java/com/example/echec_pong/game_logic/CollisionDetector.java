package com.example.echec_pong.game_logic;

import com.example.echec_pong.entity.echec.pions.Pion;
import com.example.echec_pong.entity.echec.pions.Roi;

public class CollisionDetector {
    private final GameState gameState;
    
    public CollisionDetector(GameState gameState) {
        this.gameState = gameState;
    }
    
    /**
     * Vérifie et traite collision balle-pion
     * @return le pion touché ou null
     */
    public Pion checkBallPieceCollision() {
        double ballX = gameState.getBalle().getPositionX();
        double ballY = gameState.getBalle().getPositionY();
        double cellSize = gameState.getCellSize();
        
        // Convertir position balle en coordonnées grille
        int ballRow = (int)(ballY / cellSize);
        int ballCol = (int)(ballX / cellSize);
        
        // Vérifier si dans les limites
        if(ballRow < 0 || ballRow >= gameState.getBoardRows() || 
           ballCol < 0 || ballCol >= gameState.getBoardWidth()) {
            return null;
        }
        
        Pion hitPiece = gameState.getPieceAt(ballRow, ballCol);
        if(hitPiece != null && hitPiece.getSante() > 0) {
            return hitPiece;
        }
        
        return null;
    }
    
    /**
     * Vérifie collision balle-raquette
     */
    public boolean checkBallPaddleCollision(boolean isBlackPaddle) {
        double ballX = gameState.getBalle().getPositionX();
        double ballY = gameState.getBalle().getPositionY();
        double ballRadius = gameState.getBalle().getRayon();
        
        double paddleX, paddleY, paddleWidth, paddleHeight;
        
        if(isBlackPaddle) {
            paddleX = gameState.getRaquetteNoir().getPositionX();
            paddleY = gameState.getRaquetteNoir().getPositionY();
            paddleWidth = gameState.getRaquetteNoir().getLargeur();
            paddleHeight = gameState.getRaquetteNoir().getHauteur();
        } else {
            paddleX = gameState.getRaquetteBlanc().getPositionX();
            paddleY = gameState.getRaquetteBlanc().getPositionY();
            paddleWidth = gameState.getRaquetteBlanc().getLargeur();
            paddleHeight = gameState.getRaquetteBlanc().getHauteur();
        }
        
        // Collision rectangle-cercle
        return ballX + ballRadius > paddleX && 
               ballX - ballRadius < paddleX + paddleWidth &&
               ballY + ballRadius > paddleY && 
               ballY - ballRadius < paddleY + paddleHeight;
    }
    
    /**
     * Vérifie collision balle-murs
     */
    public boolean checkBallWallCollision() {
        double ballX = gameState.getBalle().getPositionX();
        double ballY = gameState.getBalle().getPositionY();
        double ballRadius = gameState.getBalle().getRayon();
        double boardWidth = gameState.getBoardWidth() * gameState.getCellSize();
        double boardHeight = gameState.getBoardRows() * gameState.getCellSize();
        
        // Murs gauche/droite
        return ballX - ballRadius <= 0 || ballX + ballRadius >= boardWidth;
    }
    
    /**
     * Vérifie collision balle avec haut/bas pour rebond
     */
    public boolean checkBallTopBottomCollision() {
        double ballY = gameState.getBalle().getPositionY();
        double ballRadius = gameState.getBalle().getRayon();
        double boardHeight = gameState.getBoardRows() * gameState.getCellSize();
        
        return ballY - ballRadius <= 0 || ballY + ballRadius >= boardHeight;
    }
    
    /**
     * Vérifie si la balle sort du terrain (haut/bas)
     */
    public String checkBallOutOfBounds() {
        double ballY = gameState.getBalle().getPositionY();
        double ballRadius = gameState.getBalle().getRayon();
        double boardHeight = gameState.getBoardRows() * gameState.getCellSize();
        
        if(ballY - ballRadius <= 0) {
            return "blanc"; // Blanc marque (balle sort en haut)
        } else if(ballY + ballRadius >= boardHeight) {
            return "noir"; // Noir marque (balle sort en bas)
        }
        
        return null;
    }
    
    /**
     * Vérifie si le roi d'une équipe est mort (complètement retiré du jeu)
     */
    public boolean checkKingDeath(String couleur) {
        for(Pion piece : gameState.getAllPieces()) {
            if(piece instanceof Roi && piece.getCouleur().equals(couleur)) {
                // Le roi existe avec santé > 0, donc pas mort
                if(piece.getSante() > 0) {
                    return false;
                }
                // Le roi existe avec santé <= 0, donc mort
                return true;
            }
        }
        // Aucun roi trouvé = roi retiré du jeu = mort
        return true;
    }
}
