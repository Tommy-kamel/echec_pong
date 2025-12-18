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
    
    // Callback pour notifier les changements de barre de progression
    private ProgressBarCallback progressBarCallback;
    
    public interface PieceHitCallback {
        void onPieceHit(Pion piece, int row, int col, int newHealth);
    }
    
    public interface ProgressBarCallback {
        void onProgressBarChanged(int currentProgress, int maxProgress);
        void onSpecialActivated(int specialDamage);
        void onSpecialDeactivated();
    }
    
    public void setPieceHitCallback(PieceHitCallback callback) {
        this.pieceHitCallback = callback;
    }
    
    public void setProgressBarCallback(ProgressBarCallback callback) {
        this.progressBarCallback = callback;
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
        if(gameState.isGameOver() || gameState.isWaitingForServe()) {
            return;
        }
        
        if(!ballMoving) {
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
            // Si la balle spéciale touche le bord, elle redevient normale
            deactivateSpecialOnBorderHit();
            gameState.getBalle().inverserVitesseY();
        }
        
        // Vérifier victoire
        checkWinCondition();
    }
    
    /**
     * Désactive la capacité spéciale si la balle touche un bord
     */
    private void deactivateSpecialOnBorderHit() {
        if (gameState.isSpecialActive()) {
            gameState.setSpecialActive(false);
            gameState.setSpecialDamageRemaining(0);
            if (progressBarCallback != null) {
                progressBarCallback.onSpecialDeactivated();
            }
        }
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
        // Vérifier si la capacité spéciale est active (barre partagée)
        boolean specialActive = gameState.isSpecialActive();
        
        int damage;
        if (specialActive) {
            // Utiliser les dégâts spéciaux restants
            int remainingDamage = gameState.getSpecialDamageRemaining();
            damage = Math.min(remainingDamage, piece.getSante()); // Ne pas infliger plus que les PV restants
            
            // Décrémenter les dégâts restants
            gameState.setSpecialDamageRemaining(remainingDamage - damage);
        } else {
            damage = 1; // Dégâts normaux
        }
        
        // Appliquer les dégâts
        piece.setSante(piece.getSante() - damage);
        
        // Get position BEFORE removing the piece
        GameState.GridPosition pos = gameState.getPiecePosition(piece);
        
        // Notifier le callback AVANT de retirer la pièce (pour que le client sache)
        if (pieceHitCallback != null && pos != null) {
            pieceHitCallback.onPieceHit(piece, pos.row, pos.col, piece.getSante());
        }
        
        // Retirer la pièce si HP <= 0
        boolean pieceDied = piece.getSante() <= 0;
        if (pieceDied) {
            gameState.removePiece(piece);
        }
        
        // Incrémenter la barre de progression (seulement si dégâts normaux)
        if (!specialActive) {
            boolean specialActivated = gameState.incrementProgressBar();
            if (progressBarCallback != null) {
                progressBarCallback.onProgressBarChanged(gameState.getProgressBar(), gameState.getProgressBarCapacity());
                
                if (specialActivated) {
                    progressBarCallback.onSpecialActivated(gameState.getSpecialDamage());
                }
            }
        }
        
        // Gérer le comportement de la balle après collision
        if (specialActive) {
            int remainingDamage = gameState.getSpecialDamageRemaining();
            
            if (remainingDamage <= 0) {
                // Capacité épuisée, désactiver et faire rebondir normalement
                gameState.checkAndDeactivateSpecial();
                if (progressBarCallback != null) {
                    progressBarCallback.onSpecialDeactivated();
                }
                gameState.getBalle().inverserVitesseY();
            } else if (pieceDied) {
                // Le pion est mort mais il reste des dégâts: la balle continue sans rebondir
                // Ne pas inverser la direction
            } else {
                // Le pion n'est pas mort: la balle rebondit normalement
                gameState.getBalle().inverserVitesseY();
            }
        } else {
            // Comportement normal: inverser direction balle
            gameState.getBalle().inverserVitesseY();
        }
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
    public void startGame(String server) {
        gameState.setCurrentServer(server);
        gameState.setWaitingForServe(true);
        
        // Position initiale de la balle au centre
        resetBall();
        
        // Angle initial au centre
        gameState.setServeAngle(0.0);
    }
    
    /**
     * Lance la balle après le service avec l'angle choisi
     */
    public void serveBall() {
        if(!gameState.isWaitingForServe()) {
            return;
        }
        
        ballMoving = true;
        gameState.setWaitingForServe(false);
        
        // Déterminer la direction selon le serveur
        boolean servingUp = "white".equals(gameState.getCurrentServer());
        
        // Calculer les composantes de vitesse selon l'angle
        // Vitesse de base
        double baseSpeed = 3.0;
        double angle = Math.toRadians(gameState.getServeAngle());
        
        // Composante horizontale (X) selon l'angle
        double velocityX = baseSpeed * Math.sin(angle);
        
        // Composante verticale (Y) - toujours vers l'adversaire
        double velocityY = baseSpeed * Math.cos(angle);
        if(servingUp) { 
            velocityY = -Math.abs(velocityY); // vers le haut (négatif)
            // velocityX reste tel quel pour le serveur blanc
        } else {
            velocityY = Math.abs(velocityY); // vers le bas (positif)
            velocityX = -velocityX; // Inverser X pour le client car son plateau est inversé visuellement
        }
        
        // Appliquer les vitesses
        gameState.getBalle().setVitesseX(velocityX);
        gameState.getBalle().setVitesseY(velocityY);
    }
    
    /**
     * Change l'angle du service (avec flèches gauche/droite)
     */
    public void adjustServeAngle(int delta) {
        if(gameState.isWaitingForServe()) {
            double newAngle = gameState.getServeAngle() + delta;
            // Limiter l'angle entre -45 et +45 degrés
            if(newAngle < -45) newAngle = -45;
            if(newAngle > 45) newAngle = 45;
            gameState.setServeAngle(newAngle);
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
