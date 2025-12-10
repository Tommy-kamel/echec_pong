package com.example.echec_pong.game_logic;

import com.example.echec_pong.entity.echec.pions.Pion;

public class GameLogic {
    private final GameState gameState;
    private final CollisionDetector collisionDetector;
    private final PaddleController paddleController;
    private final int boardWidth;
    private boolean ballMoving;
    
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
        
        // Déplacer la balle
        gameState.getBalle().deplacer();
        
        // Vérifier collision balle-pion
        Pion hitPiece = collisionDetector.checkBallPieceCollision();
        if(hitPiece != null) {
            handlePieceHit(hitPiece);
        }
        
        // Vérifier collision balle-raquettes
        if(collisionDetector.checkBallPaddleCollision(true)) {
            gameState.getBalle().inverserVitesseY();
        }
        if(collisionDetector.checkBallPaddleCollision(false)) {
            gameState.getBalle().inverserVitesseY();
        }
        
        // Vérifier collision balle-murs
        if(collisionDetector.checkBallWallCollision()) {
            gameState.getBalle().inverserVitesseX();
        }
        
        // Vérifier sortie de terrain
        String scorer = collisionDetector.checkBallOutOfBounds();
        if(scorer != null) {
            resetBall();
        }
        
        // Vérifier victoire
        checkWinCondition();
    }
    
    /**
     * Gère quand un pion est touché
     */
    private void handlePieceHit(Pion piece) {
        piece.setSante(piece.getSante() - 1);
        
        if(piece.getSante() <= 0) {
            gameState.removePiece(piece);
        }
        
        // Notifier le callback si défini
        if (pieceHitCallback != null) {
            GameState.GridPosition pos = gameState.getPiecePosition(piece);
            if (pos != null) {
                pieceHitCallback.onPieceHit(piece, pos.row, pos.col, piece.getSante());
            }
        }
        
        // Inverser direction balle
        gameState.getBalle().inverserVitesseY();
    }
    
    /**
     * Vérifie condition de victoire
     */
    private void checkWinCondition() {
        if(collisionDetector.checkKingDeath("noir")) {
            gameState.setGameOver(true);
            gameState.setWinner("blanc");
        } else if(collisionDetector.checkKingDeath("blanc")) {
            gameState.setGameOver(true);
            gameState.setWinner("noir");
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
        ballMoving = false;
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
