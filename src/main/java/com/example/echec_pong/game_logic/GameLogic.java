package com.example.echec_pong.game_logic;

import com.example.echec_pong.entity.echec.pions.Pion;

public class GameLogic {
    private final GameState gameState;
    private final CollisionDetector collisionDetector;
    private final PaddleController paddleController;
    private final int boardWidth;
    private boolean ballMoving;
    
    // Cooldowns to prevent multiple rapid collisions
    private int paddleCollisionCooldown = 0;
    private int pieceCollisionCooldown = 0;
    private static final int COLLISION_COOLDOWN_FRAMES = 10;
    
    // Callback pour notifier les changements de santé
    private PieceHitCallback pieceHitCallback;
    
    public interface PieceHitCallback {
        void onPieceHit(Pion piece, int row, int col, int newHealth);
    }
    
    public void setPieceHitCallback(PieceHitCallback callback) {
        this.pieceHitCallback = callback;
    }
    
    public GameLogic(GameState gameState, int boardWidth) {
        this.gameState = gameState;
        this.boardWidth = boardWidth;
        this.collisionDetector = new CollisionDetector(gameState);
        this.paddleController = new PaddleController(gameState, boardWidth);
        this.ballMoving = false;
    }
    
    /**
     * Met à jour le jeu (appelé à chaque frame)
     */
    public void update() {
        if(gameState.isGameOver() || !ballMoving) {
            return;
        }
        
        // Décrémenter les cooldowns
        if(paddleCollisionCooldown > 0) paddleCollisionCooldown--;
        if(pieceCollisionCooldown > 0) pieceCollisionCooldown--;
        
        // Déplacer la balle
        gameState.getBalle().deplacer();
        
        // Vérifier collision balle-pion (avec cooldown)
        if(pieceCollisionCooldown == 0) {
            Pion hitPiece = collisionDetector.checkBallPieceCollision();
            if(hitPiece != null) {
                handlePieceHit(hitPiece);
                pieceCollisionCooldown = COLLISION_COOLDOWN_FRAMES;
            }
        }
        
        // Vérifier collision balle-raquettes (avec cooldown et séparation)
        if(paddleCollisionCooldown == 0) {
            if(collisionDetector.checkBallPaddleCollision(true)) {
                handlePaddleBounce(true);
                paddleCollisionCooldown = COLLISION_COOLDOWN_FRAMES;
            } else if(collisionDetector.checkBallPaddleCollision(false)) {
                handlePaddleBounce(false);
                paddleCollisionCooldown = COLLISION_COOLDOWN_FRAMES;
            }
        }
        
        // Vérifier collision balle-murs
        if(collisionDetector.checkBallWallCollision()) {
            gameState.getBalle().inverserVitesseX();
        }
        
        // Vérifier collision haut/bas - rebondir au lieu de téléporter
        if(collisionDetector.checkBallTopBottomCollision()) {
            gameState.getBalle().inverserVitesseY();
        }
        
        // Vérifier victoire
        checkWinCondition();
    }
    
    /**
     * Gère le rebond sur une raquette
     */
    private void handlePaddleBounce(boolean isBlackPaddle) {
        // Inverser vitesse Y
        gameState.getBalle().inverserVitesseY();
        
        // Séparer la balle de la raquette pour éviter qu'elle ne colle
        double separationDistance = 3.0;
        if(isBlackPaddle) {
            // Raquette noire en haut - pousser la balle vers le bas
            double paddleBottom = gameState.getRaquetteNoir().getPositionY() + 
                                 gameState.getRaquetteNoir().getHauteur();
            gameState.getBalle().setPositionY(paddleBottom + gameState.getBalle().getRayon() + separationDistance);
        } else {
            // Raquette blanche en bas - pousser la balle vers le haut
            double paddleTop = gameState.getRaquetteBlanc().getPositionY();
            gameState.getBalle().setPositionY(paddleTop - gameState.getBalle().getRayon() - separationDistance);
        }
    }
    
    /**
     * Gère quand un pion est touché
     */
    private void handlePieceHit(Pion piece) {
        piece.setSante(piece.getSante() - 1);
        
        // Get position BEFORE removing the piece
        GameState.GridPosition pos = gameState.getPiecePosition(piece);
        
        // Notifier le callback AVANT de retirer la pièce (pour que le client sache)
        if (pieceHitCallback != null && pos != null) {
            pieceHitCallback.onPieceHit(piece, pos.row, pos.col, piece.getSante());
        }
        
        // Retirer la pièce si HP <= 0
        if(piece.getSante() <= 0) {
            gameState.removePiece(piece);
        }
        
        // Inverser direction balle
        gameState.getBalle().inverserVitesseY();
    }
    
    /**
     * Vérifie condition de victoire
     */
    private void checkWinCondition() {
        // Ne vérifier que si le jeu n'est pas déjà terminé
        if(!gameState.isGameOver()) {
            boolean blackKingDead = collisionDetector.checkKingDeath("noir");
            boolean whiteKingDead = collisionDetector.checkKingDeath("blanc");
            
            if(blackKingDead) {
                gameState.setGameOver(true);
                gameState.setWinner("Blanc");
                ballMoving = false;
            } else if(whiteKingDead) {
                gameState.setGameOver(true);
                gameState.setWinner("Noir");
                ballMoving = false;
            }
        }
    }
    
    /**
     * Réinitialise la balle au centre
     */
    private void resetBall() {
        double centerX = (gameState.getBoardWidth() * gameState.getCellSize()) / 2;
        double centerY = (gameState.getBoardRows() * gameState.getCellSize()) / 2;
        gameState.getBalle().setPositionX(centerX);
        gameState.getBalle().setPositionY(centerY);
        // Don't stop the ball - let it continue playing
    }
    
    /**
     * Démarre le jeu avec le service
     */
    public void startGame(boolean blackStarts) {
        ballMoving = true;
        if(!blackStarts) {
            gameState.getBalle().inverserVitesseY();
        }
    }
    
    // Contrôle raquettes
    public void moveBlackPaddleLeft() {
        paddleController.moveBlackPaddleLeft();
    }
    
    public void moveBlackPaddleRight() {
        paddleController.moveBlackPaddleRight();
    }
    
    public void moveWhitePaddleLeft() {
        paddleController.moveWhitePaddleLeft();
    }
    
    public void moveWhitePaddleRight() {
        paddleController.moveWhitePaddleRight();
    }
    
    // Getters
    public GameState getGameState() {
        return gameState;
    }
    
    public boolean isBallMoving() {
        return ballMoving;
    }
}
