package com.example.echec_pong;

import javafx.animation.AnimationTimer;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.Pane;
import javafx.scene.input.KeyEvent;

import java.io.*;
import java.net.*;
import java.util.Map;

import com.example.echec_pong.ui.ViewLoader;
import com.example.echec_pong.ui.BoardRenderer;
import com.example.echec_pong.ui.HostSettingsData;
import com.example.echec_pong.ui.GameViewData;
import com.example.echec_pong.ui.GameRenderData;
import com.example.echec_pong.entity.echec.pions.Pion;
import com.example.echec_pong.game_logic.GameLogic;
import com.example.echec_pong.network.GameStateUpdate;

public class HelloController {
    @FXML
    private StackPane mainContainer;

    private boolean isHost;
    private ServerSocket serverSocket;
    private Socket clientSocket;
    private ObjectOutputStream out;
    private ObjectInputStream in;
    private boolean clientConnected = false;
    private int width, height, pionHealth, cavalierHealth, fouHealth, tourHealth, dameHealth, roiHealth;
    private boolean blackStarts = true;
    
    private GameLogic gameLogic;
    private AnimationTimer gameLoop;
    private Map<Pion, Label> healthLabels;
    private GameRenderData renderData;
    private volatile boolean networkRunning = true;

    @FXML
    private Label gameStatusLabel;

    @FXML
    private Pane gameArea;

    @FXML
    public void initialize() {
        ViewLoader.loadRoleSelection(mainContainer, this::choisirHote, this::choisirClient);
    }

    private void choisirHote() {
        isHost = true;
        loadHostSettings();
    }

    private void choisirClient() {
        isHost = false;
        loadClientWaiting();
        connectAsClient();
    }

    private void loadHostSettings() {
        ViewLoader.loadHostSettings(mainContainer, this::demarrerJeuHost, this::startServer);
    }

    private void loadClientWaiting() {
        ViewLoader.loadClientWaiting(mainContainer);
    }

    private void loadGame() {
        GameViewData gameData = ViewLoader.loadGame(mainContainer);
        if(gameData != null) {
            gameStatusLabel = gameData.statusLabel;
            gameArea = gameData.gameArea;
            createBoard();
        }
    }

    private void startServer(Label statusLabel) {
        new Thread(() -> {
            try {
                serverSocket = new ServerSocket(12345);
                Platform.runLater(() -> statusLabel.setText("Serveur démarré, en attente de client..."));
                clientSocket = serverSocket.accept();
                out = new ObjectOutputStream(clientSocket.getOutputStream());
                in = new ObjectInputStream(clientSocket.getInputStream());
                clientConnected = true;
                Platform.runLater(() -> statusLabel.setText("Client connecté !"));
                startNetworkListener();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }

    private void connectAsClient() {
        new Thread(() -> {
            try {
                clientSocket = new Socket("localhost", 12345);
                out = new ObjectOutputStream(clientSocket.getOutputStream());
                in = new ObjectInputStream(clientSocket.getInputStream());
                width = in.readInt();
                height = width + 2;
                pionHealth = in.readInt();
                cavalierHealth = in.readInt();
                fouHealth = in.readInt();
                tourHealth = in.readInt();
                dameHealth = in.readInt();
                roiHealth = in.readInt();
                String firstServe = in.readUTF();
                blackStarts = firstServe.equalsIgnoreCase("host");
                Platform.runLater(() -> loadGame());
                startNetworkListener();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }

    private void demarrerJeuHost(HostSettingsData data) {
        if (!clientConnected) {
            data.statusLabel.setText("Attendez que le client se connecte !");
            return;
        }

        width = Integer.parseInt(data.widthField.getText());
        height = width + 2;
        pionHealth = Integer.parseInt(data.pionHealth.getText());
        cavalierHealth = Integer.parseInt(data.cavalierHealth.getText());
        fouHealth = Integer.parseInt(data.fouHealth.getText());
        tourHealth = Integer.parseInt(data.tourHealth.getText());
        dameHealth = Integer.parseInt(data.dameHealth.getText());
        roiHealth = Integer.parseInt(data.roiHealth.getText());
        String firstServe = data.firstServeField.getText();
        blackStarts = firstServe.equalsIgnoreCase("host");

        try {
            out.writeInt(width);
            out.writeInt(pionHealth);
            out.writeInt(cavalierHealth);
            out.writeInt(fouHealth);
            out.writeInt(tourHealth);
            out.writeInt(dameHealth);
            out.writeInt(roiHealth);
            out.writeUTF(firstServe);
            out.flush();
            loadGame();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void createBoard() {
        renderData = BoardRenderer.renderBoard(gameArea, width, height, pionHealth, cavalierHealth, 
                                  fouHealth, tourHealth, dameHealth, roiHealth, isHost);
        
        healthLabels = renderData.healthLabels;
        gameLogic = new GameLogic(renderData.gameState, width);
        
        // Setup callback for piece hits (only host sends updates)
        if (isHost) {
            gameLogic.setPieceHitCallback((piece, row, col, newHealth) -> {
                sendPieceHit(row, col, newHealth);
            });
        }
        
        // Only host controls the ball
        if (isHost) {
            gameLogic.startGame(blackStarts);
        }
        
        startGameLoop(renderData);
        setupKeyboardControls();
        
        String playerSide = isHost ? "noir (haut)" : "blanc (bas)";
        gameStatusLabel.setText("Jeu prêt ! Vous jouez " + playerSide + ". Utilisez les flèches.");
    }
    
    private void startGameLoop(GameRenderData renderData) {
        gameLoop = new AnimationTimer() {
            private long lastNetworkUpdate = 0;
            private static final long NETWORK_UPDATE_INTERVAL = 33_333_333; // ~30 FPS pour réduire le trafic réseau
            
            @Override
            public void handle(long now) {
                // Only host runs the physics simulation
                if (isHost) {
                    gameLogic.update();
                    
                    // Send ball state to client periodically (moins fréquent)
                    if (now - lastNetworkUpdate > NETWORK_UPDATE_INTERVAL) {
                        new Thread(() -> sendBallState()).start();
                        lastNetworkUpdate = now;
                    }
                }
                
                updateUI(renderData);
                checkWinCondition();
            }
        };
        gameLoop.start();
    }
    
    private void updateUI(GameRenderData renderData) {
        // Update paddle positions
        renderData.blackPaddleRect.setLayoutX(gameLogic.getGameState().getRaquetteNoir().getPositionX());
        renderData.blackPaddleRect.setLayoutY(gameLogic.getGameState().getRaquetteNoir().getPositionY());
        renderData.whitePaddleRect.setLayoutX(gameLogic.getGameState().getRaquetteBlanc().getPositionX());
        renderData.whitePaddleRect.setLayoutY(gameLogic.getGameState().getRaquetteBlanc().getPositionY());
        
        // Update ball position
        renderData.ballCircle.setCenterX(gameLogic.getGameState().getBalle().getPositionX());
        renderData.ballCircle.setCenterY(gameLogic.getGameState().getBalle().getPositionY());
        
        // Update health labels
        for (Map.Entry<Pion, Label> entry : healthLabels.entrySet()) {
            Pion piece = entry.getKey();
            Label healthLabel = entry.getValue();
            healthLabel.setText("HP:" + piece.getHealth());
            
            // Hide pieces with 0 health
            if (piece.getHealth() <= 0) {
                healthLabel.setVisible(false);
                healthLabel.getParent().setVisible(false);
            }
        }
    }
    
    private void checkWinCondition() {
        if (gameLogic.getGameState().isGameOver()) {
            gameLoop.stop();
            String winner = gameLogic.getGameState().getWinner();
            gameStatusLabel.setText("Jeu terminé ! " + winner + " a gagné !");
        }
    }
    
    private void setupKeyboardControls() {
        gameArea.setFocusTraversable(true);
        gameArea.requestFocus();
        
        gameArea.setOnKeyPressed(this::handleKeyPressed);
        gameArea.setOnKeyReleased(this::handleKeyReleased);
    }
    
    private void handleKeyPressed(KeyEvent event) {
        switch (event.getCode()) {
            case LEFT:
                if (isHost) {
                    gameLogic.moveBlackPaddleLeft();
                    sendPaddlePosition();
                } else {
                    gameLogic.moveWhitePaddleLeft();
                    sendPaddlePosition();
                }
                break;
            case RIGHT:
                if (isHost) {
                    gameLogic.moveBlackPaddleRight();
                    sendPaddlePosition();
                } else {
                    gameLogic.moveWhitePaddleRight();
                    sendPaddlePosition();
                }
                break;
            default:
                break;
        }
    }
    
    private void handleKeyReleased(KeyEvent event) {
        // Optional: stop paddle movement when key released
        // Currently paddles move discretely per key press
    }
    
    private synchronized void sendPaddlePosition() {
        if (out == null) return;
        
        try {
            double x, y;
            if (isHost) {
                x = gameLogic.getGameState().getRaquetteNoir().getPositionX();
                y = gameLogic.getGameState().getRaquetteNoir().getPositionY();
            } else {
                x = gameLogic.getGameState().getRaquetteBlanc().getPositionX();
                y = gameLogic.getGameState().getRaquetteBlanc().getPositionY();
            }
            
            GameStateUpdate update = GameStateUpdate.paddleMove(x, y);
            out.writeObject(update);
            out.flush();
            out.reset(); // Important pour éviter la mise en cache
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    private synchronized void sendBallState() {
        if (out == null || !isHost) return;
        
        try {
            double x = gameLogic.getGameState().getBalle().getPositionX();
            double y = gameLogic.getGameState().getBalle().getPositionY();
            double vx = gameLogic.getGameState().getBalle().getVitesseX();
            double vy = gameLogic.getGameState().getBalle().getVitesseY();
            
            GameStateUpdate update = GameStateUpdate.ballState(x, y, vx, vy);
            out.writeObject(update);
            out.flush();
            out.reset(); // Important pour éviter la mise en cache
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    private synchronized void sendPieceHit(int row, int col, int newHealth) {
        if (out == null || !isHost) return;
        
        try {
            GameStateUpdate update = GameStateUpdate.pieceHit(row, col, newHealth);
            out.writeObject(update);
            out.flush();
            out.reset();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    private void startNetworkListener() {
        new Thread(() -> {
            while (networkRunning) {
                try {
                    GameStateUpdate update = (GameStateUpdate) in.readObject();
                    
                    Platform.runLater(() -> {
                        switch (update.getType()) {
                            case PADDLE_MOVE:
                                handleRemotePaddleMove(update);
                                break;
                            case BALL_STATE:
                                handleRemoteBallState(update);
                                break;
                            case PIECE_HIT:
                                handleRemotePieceHit(update);
                                break;
                            case GAME_OVER:
                                handleRemoteGameOver(update);
                                break;
                        }
                    });
                } catch (IOException | ClassNotFoundException e) {
                    if (networkRunning) {
                        e.printStackTrace();
                    }
                    break;
                }
            }
        }).start();
    }
    
    private void handleRemotePaddleMove(GameStateUpdate update) {
        if (isHost) {
            // Host receives white paddle moves from client
            gameLogic.getGameState().getRaquetteBlanc().setPositionX(update.getPaddleX());
            gameLogic.getGameState().getRaquetteBlanc().setPositionY(update.getPaddleY());
        } else {
            // Client receives black paddle moves from host
            gameLogic.getGameState().getRaquetteNoir().setPositionX(update.getPaddleX());
            gameLogic.getGameState().getRaquetteNoir().setPositionY(update.getPaddleY());
        }
    }
    
    private void handleRemoteBallState(GameStateUpdate update) {
        if (!isHost) {
            // Only client receives ball state from host
            gameLogic.getGameState().getBalle().setPositionX(update.getBallX());
            gameLogic.getGameState().getBalle().setPositionY(update.getBallY());
            gameLogic.getGameState().getBalle().setVitesseX(update.getBallVelX());
            gameLogic.getGameState().getBalle().setVitesseY(update.getBallVelY());
        }
    }
    
    private void handleRemotePieceHit(GameStateUpdate update) {
        if (!isHost) {
            // Client receives piece hit updates from host
            int row = update.getPieceRow();
            int col = update.getPieceCol();
            int newHealth = update.getPieceHealth();
            
            // Find the piece at this position and update its health
            Pion piece = gameLogic.getGameState().getPieceAt(row, col);
            if (piece != null) {
                piece.setSante(newHealth);
                
                // Remove piece if health is 0
                if (newHealth <= 0) {
                    gameLogic.getGameState().removePiece(piece);
                }
            }
        }
    }
    
    private void handleRemoteGameOver(GameStateUpdate update) {
        gameLoop.stop();
        gameStatusLabel.setText("Jeu terminé ! " + update.getWinner() + " a gagné !");
    }
}
