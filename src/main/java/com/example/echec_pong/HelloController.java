package com.example.echec_pong;

import javafx.animation.AnimationTimer;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.Button;
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
import com.example.echec_pong.ui.ClientWaitingData;
import com.example.echec_pong.entity.echec.pions.Pion;
import com.example.echec_pong.game_logic.GameLogic;
import com.example.echec_pong.game_logic.GameState;
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
    private int ballStateReceived = 0; // Compteur pour debug

    @FXML
    private Label gameStatusLabel;

    @FXML
    private Pane gameArea;
    
    private Button replayButton;

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
    }

    private void loadHostSettings() {
        ViewLoader.loadHostSettings(mainContainer, this::demarrerJeuHost, this::startServer);
    }

    private void loadClientWaiting() {
        ClientWaitingData clientData = ViewLoader.loadClientWaiting(mainContainer);
        if(clientData != null) {
            clientData.connectButton.setOnAction(e -> {
                String serverIp = clientData.serverIpField.getText().trim();
                if(serverIp.isEmpty()) {
                    serverIp = "localhost";
                }
                clientData.statusLabel.setText("Connexion à " + serverIp + ":12345...");
                clientData.connectButton.setDisable(true);
                connectAsClient(serverIp, clientData.statusLabel);
            });
        }
    }

    private void loadGame() {
        GameViewData gameData = ViewLoader.loadGame(mainContainer);
        if(gameData != null) {
            gameStatusLabel = gameData.statusLabel;
            gameArea = gameData.gameArea;
            replayButton = gameData.replayButton;
            
            // Configurer le bouton Rejouer
            if(replayButton != null) {
                replayButton.setOnAction(e -> handleReplay());
            }
            
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

    private void connectAsClient(String serverIp, Label statusLabel) {
        new Thread(() -> {
            try {
                Platform.runLater(() -> statusLabel.setText("Tentative de connexion..."));
                clientSocket = new Socket(serverIp, 12345);
                Platform.runLater(() -> statusLabel.setText("Connecté ! Réception des paramètres..."));
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
                Platform.runLater(() -> {
                    statusLabel.setText("Démarrage du jeu...");
                    loadGame();
                });
                startNetworkListener();
            } catch (IOException e) {
                Platform.runLater(() -> {
                    statusLabel.setText("❌ Erreur de connexion: " + e.getMessage());
                    statusLabel.setStyle("-fx-text-fill: red;");
                });
                e.printStackTrace();
            }
        }).start();
    }

    private void demarrerJeuHost(HostSettingsData data) {
        if (!clientConnected) {
            data.statusLabel.setText("Attendez que le client se connecte !");
            return;
        }

        // Valider que tous les champs sont remplis
        String firstServe;
        try {
            String widthText = data.widthField.getText().trim();
            String pionHealthText = data.pionHealth.getText().trim();
            String cavalierHealthText = data.cavalierHealth.getText().trim();
            String fouHealthText = data.fouHealth.getText().trim();
            String tourHealthText = data.tourHealth.getText().trim();
            String dameHealthText = data.dameHealth.getText().trim();
            String roiHealthText = data.roiHealth.getText().trim();
            String firstServeText = data.firstServeField.getText().trim();
            
            if (widthText.isEmpty() || pionHealthText.isEmpty() || cavalierHealthText.isEmpty() ||
                fouHealthText.isEmpty() || tourHealthText.isEmpty() || dameHealthText.isEmpty() ||
                roiHealthText.isEmpty() || firstServeText.isEmpty()) {
                data.statusLabel.setText("Veuillez remplir tous les champs !");
                return;
            }
            
            width = Integer.parseInt(widthText);
            height = width + 2;
            pionHealth = Integer.parseInt(pionHealthText);
            cavalierHealth = Integer.parseInt(cavalierHealthText);
            fouHealth = Integer.parseInt(fouHealthText);
            tourHealth = Integer.parseInt(tourHealthText);
            dameHealth = Integer.parseInt(dameHealthText);
            roiHealth = Integer.parseInt(roiHealthText);
            firstServe = firstServeText;
            blackStarts = firstServe.equalsIgnoreCase("host");
        } catch (NumberFormatException e) {
            data.statusLabel.setText("Erreur : Entrez des nombres valides !");
            return;
        }

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
                System.out.println("[HOST] Callback PIECE_HIT: pièce=" + piece.getCouleur() + " " + piece.getNom() + " row=" + row + " col=" + col + " HP=" + newHealth);
                sendPieceHit(row, col, piece.getCouleur(), piece.getNom(), newHealth);
            });
        }
        
        // Only host controls the ball
        if (isHost) {
            gameLogic.startGame(blackStarts);
        }
        
        startGameLoop(renderData);
        setupKeyboardControls();
        
        String playerSide = isHost ? "blanc (bas)" : "noir (haut)";
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
                    if (networkRunning && now - lastNetworkUpdate > NETWORK_UPDATE_INTERVAL) {
                        sendBallState();
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
        
        // Update ball position - CSS rotation handles client transformation automatically
        renderData.ballCircle.setCenterX(gameLogic.getGameState().getBalle().getPositionX());
        renderData.ballCircle.setCenterY(gameLogic.getGameState().getBalle().getPositionY());
        
        // Update health labels - MUST run on both HOST and CLIENT to reflect health changes
        for (Map.Entry<Pion, Label> entry : healthLabels.entrySet()) {
            Pion piece = entry.getKey();
            Label healthLabel = entry.getValue();
            
            // Check if piece still exists in game state
            boolean pieceExists = gameLogic.getGameState().getAllPieces().contains(piece);
            
            if (!pieceExists || piece.getSante() <= 0) {
                // Hide pieces that are removed or have 0 health
                healthLabel.setVisible(false);
                healthLabel.getParent().setVisible(false);
            } else {
                // Update health display for existing pieces - this SHOULD update the label text
                int currentHealth = piece.getSante();
                String newText = "HP:" + currentHealth;
                if (!healthLabel.getText().equals(newText)) {
                    healthLabel.setText(newText);
                }
                healthLabel.setVisible(true);
                healthLabel.getParent().setVisible(true);
            }
        }
    }
    
    private void checkWinCondition() {
        if (gameLogic.getGameState().isGameOver()) {
            String winner = gameLogic.getGameState().getWinner();
            
            // Afficher un message de victoire
            gameStatusLabel.setText("🏆 VICTOIRE ! Le joueur " + winner + " a gagné ! 🏆");
            gameStatusLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: gold;");
            
            // Afficher le bouton Rejouer
            if(replayButton != null) {
                replayButton.setVisible(true);
            }
            
            // Envoyer la notification de fin de jeu à l'adversaire
            if(isHost) {
                sendGameOver(winner);
            }
            
            // Arrêter seulement la boucle de jeu, garder le réseau actif
            if (gameLoop != null) {
                gameLoop.stop();
            }
        }
    }
    
    private void setupKeyboardControls() {
        // Attacher les événements clavier à la scène pour capturer tous les événements
        Platform.runLater(() -> {
            if (gameArea.getScene() != null) {
                gameArea.getScene().setOnKeyPressed(this::handleKeyPressed);
                gameArea.getScene().setOnKeyReleased(this::handleKeyReleased);
                System.out.println((isHost ? "[HOST] " : "[CLIENT] ") + "Listeners clavier attachés à la scène");
            } else {
                System.err.println((isHost ? "[HOST] " : "[CLIENT] ") + "ERREUR: Scène non disponible !");
            }
        });
    }
    
    private void handleKeyPressed(KeyEvent event) {
        System.out.println((isHost ? "[HOST] " : "[CLIENT] ") + "Touche pressée: " + event.getCode());
        switch (event.getCode()) {
            case LEFT:
                if (isHost) {
                    // Host contrôle la raquette blanche (bas de son écran)
                    gameLogic.moveWhitePaddleLeft();
                    sendPaddlePosition();
                } else {
                    // Client : écran tourné 180°, donc LEFT devient RIGHT dans le modèle
                    gameLogic.moveBlackPaddleRight();
                    sendPaddlePosition();
                }
                break;
            case RIGHT:
                if (isHost) {
                    // Host contrôle la raquette blanche (bas de son écran)
                    gameLogic.moveWhitePaddleRight();
                    sendPaddlePosition();
                } else {
                    // Client : écran tourné 180°, donc RIGHT devient LEFT dans le modèle
                    gameLogic.moveBlackPaddleLeft();
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
        if (out == null || !networkRunning) {
            System.out.println((isHost ? "[HOST] " : "[CLIENT] ") + "Envoi annulé: out=" + (out != null) + ", networkRunning=" + networkRunning);
            return;
        }
        
        try {
            double x, y;
            if (isHost) {
                // Host envoie sa raquette blanche
                x = gameLogic.getGameState().getRaquetteBlanc().getPositionX();
                y = gameLogic.getGameState().getRaquetteBlanc().getPositionY();
            } else {
                // Client envoie sa raquette noire
                x = gameLogic.getGameState().getRaquetteNoir().getPositionX();
                y = gameLogic.getGameState().getRaquetteNoir().getPositionY();
            }
            
            GameStateUpdate update = GameStateUpdate.paddleMove(x, y);
            out.writeObject(update);
            out.flush();
            out.reset(); // Important pour éviter la mise en cache
            System.out.println((isHost ? "[HOST] " : "[CLIENT] ") + "Envoi PADDLE_MOVE: (" + x + ", " + y + ")");
        } catch (IOException e) {
            System.err.println((isHost ? "[HOST] " : "[CLIENT] ") + "Erreur envoi position raquette: " + e.getMessage());
            e.printStackTrace();
            networkRunning = false;
        }
    }
    
    private synchronized void sendBallState() {
        if (out == null || !isHost || !networkRunning) return;
        
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
            System.err.println("Erreur envoi état balle: " + e.getMessage());
            networkRunning = false;
        }
    }
    
    private synchronized void sendPieceHit(int row, int col, String couleur, String nom, int newHealth) {
        if (out == null || !isHost || !networkRunning) return;
        
        try {
            System.out.println("[HOST] Envoi PIECE_HIT: row=" + row + " col=" + col + " couleur=" + couleur + " nom=" + nom + " HP=" + newHealth);
            GameStateUpdate update = GameStateUpdate.pieceHit(row, col, couleur, nom, newHealth);
            out.writeObject(update);
            out.flush();
            out.reset();
        } catch (IOException e) {
            System.err.println((isHost ? "[HOST] " : "[CLIENT] ") + "Erreur envoi pièce touchée: " + e.getMessage());
            e.printStackTrace();
            networkRunning = false;
        }
    }
    
    private synchronized void sendGameOver(String winner) {
        if (out == null || !isHost || !networkRunning) return;
        
        try {
            GameStateUpdate update = GameStateUpdate.gameOver(winner);
            out.writeObject(update);
            out.flush();
            out.reset();
        } catch (IOException e) {
            System.err.println("Erreur envoi fin de jeu: " + e.getMessage());
        }
    }
    
    private void startNetworkListener() {
        new Thread(() -> {
            System.out.println((isHost ? "[HOST] " : "[CLIENT] ") + "Network listener démarré");
            while (networkRunning) {
                try {
                    GameStateUpdate update = (GameStateUpdate) in.readObject();
                    // Ne logger que les messages importants (pas BALL_STATE)
                    if (update.getType() != GameStateUpdate.UpdateType.BALL_STATE) {
                        System.out.println((isHost ? "[HOST] " : "[CLIENT] ") + "Message reçu: " + update.getType());
                    }
                    
                    Platform.runLater(() -> {
                        try {
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
                        } catch (Exception e) {
                            System.err.println((isHost ? "[HOST] " : "[CLIENT] ") + "Exception dans handler: " + e.getMessage());
                            e.printStackTrace();
                        }
                    });
                } catch (EOFException e) {
                    // Connexion fermée proprement, arrêter silencieusement
                    System.out.println((isHost ? "[HOST] " : "[CLIENT] ") + "Connexion fermée");
                    break;
                } catch (IOException | ClassNotFoundException e) {
                    if (networkRunning) {
                        System.err.println((isHost ? "[HOST] " : "[CLIENT] ") + "Erreur réseau: " + e.getMessage());
                        e.printStackTrace();
                    }
                    break;
                }
            }
            System.out.println((isHost ? "[HOST] " : "[CLIENT] ") + "Network listener arrêté");
        }).start();
    }
    
    private void handleRemotePaddleMove(GameStateUpdate update) {
        System.out.println((isHost ? "[HOST] " : "[CLIENT] ") + "Réception PADDLE_MOVE: (" + update.getPaddleX() + ", " + update.getPaddleY() + ")");
        if (isHost) {
            // Host reçoit les mouvements de la raquette noire du client
            gameLogic.getGameState().getRaquetteNoir().setPositionX(update.getPaddleX());
            gameLogic.getGameState().getRaquetteNoir().setPositionY(update.getPaddleY());
        } else {
            // Client reçoit les mouvements de la raquette blanche de l'host
            gameLogic.getGameState().getRaquetteBlanc().setPositionX(update.getPaddleX());
            gameLogic.getGameState().getRaquetteBlanc().setPositionY(update.getPaddleY());
        }
    }
    
    private void handleRemoteBallState(GameStateUpdate update) {
        if (!isHost) {
            // Only client receives ball state from host
            try {
                ballStateReceived++;
                if (ballStateReceived % 30 == 0) {
                    System.out.println("[CLIENT] BALL_STATE reçus: " + ballStateReceived + " - Position: (" + update.getBallX() + ", " + update.getBallY() + ")");
                }
                
                // Don't transform - boardContainer rotation handles coordinate transformation automatically
                gameLogic.getGameState().getBalle().setPositionX(update.getBallX());
                gameLogic.getGameState().getBalle().setPositionY(update.getBallY());
                gameLogic.getGameState().getBalle().setVitesseX(update.getBallVelX());
                gameLogic.getGameState().getBalle().setVitesseY(update.getBallVelY());
            } catch (Exception e) {
                System.err.println("[CLIENT] Erreur lors de la mise à jour de la balle: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }
    
    private void handleRemotePieceHit(GameStateUpdate update) {
        if (!isHost) {
            try {
                // Client receives piece hit updates from host
                int row = update.getPieceRow();
                int col = update.getPieceCol();
                String couleur = update.getPieceCouleur();
                String nom = update.getPieceNom();
                int newHealth = update.getPieceHealth();
                
                System.out.println("[CLIENT] PIECE_HIT reçu: row=" + row + " col=" + col + " couleur=" + couleur + " nom=" + nom + " HP=" + newHealth);
                System.out.println("[CLIENT] healthLabels map size: " + healthLabels.size());
                
                // Find the EXACT piece at this position with the matching couleur and nom
                Pion piece = gameLogic.getGameState().getPieceAtWithCouleurNom(row, col, couleur, nom);
                if (piece != null) {
                    System.out.println("[CLIENT] Pièce trouvée: " + piece + " (hashCode=" + piece.hashCode() + ")");
                    
                    piece.setSante(newHealth);
                    System.out.println("[CLIENT] Mise à jour pièce " + couleur + " " + nom + " à (" + row + "," + col + ") HP: " + newHealth);
                    
                    // Check if piece is in healthLabels map
                    Label healthLabel = healthLabels.get(piece);
                    System.out.println("[CLIENT] healthLabel trouvé dans map: " + (healthLabel != null));
                    
                    if (healthLabel != null) {
                        // Force update the health label immediately on the JavaFX thread
                        Platform.runLater(() -> {
                            healthLabel.setText("HP:" + newHealth);
                            System.out.println("[CLIENT] Label mis à jour: HP:" + newHealth);
                        });
                    } else {
                        // Debug: print all keys in the map
                        System.out.println("[CLIENT] DEBUG: Pièce NON trouvée dans healthLabels. Clés dans la map:");
                        for (Pion p : healthLabels.keySet()) {
                            System.out.println("  - " + p.getCouleur() + " " + p.getNom() + " (hashCode=" + p.hashCode() + ")");
                        }
                    }
                    
                    // Remove piece if health is 0
                    if (newHealth <= 0) {
                        gameLogic.getGameState().removePiece(piece);
                        System.out.println("[CLIENT] Pièce " + couleur + " " + nom + " retirée");
                        
                        // Hide the piece visually
                        if (healthLabel != null) {
                            Platform.runLater(() -> {
                                if (healthLabel.getParent() != null) {
                                    healthLabel.setVisible(false);
                                    healthLabel.getParent().setVisible(false);
                                }
                            });
                        }
                    }
                } else {
                    System.err.println("[CLIENT] ERREUR: Pièce " + couleur + " " + nom + " non trouvée à (" + row + "," + col + ")");
                }
            } catch (Exception e) {
                System.err.println("[CLIENT] Exception dans handleRemotePieceHit: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }
    
    private void handleRemoteGameOver(GameStateUpdate update) {
        gameLogic.getGameState().setGameOver(true);
        
        String winner = update.getWinner();
        gameStatusLabel.setText("🏆 VICTOIRE ! Le joueur " + winner + " a gagné ! 🏆");
        gameStatusLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: gold;");
        
        // Afficher le bouton Rejouer
        if(replayButton != null) {
            replayButton.setVisible(true);
        }
        
        // Arrêter seulement la boucle de jeu
        if(gameLoop != null) {
            gameLoop.stop();
        }
    }
    
    private void handleReplay() {
        // Cacher le bouton Rejouer
        if(replayButton != null) {
            replayButton.setVisible(false);
        }
        
        // Réinitialiser le label de statut
        String playerSide = isHost ? "blanc (bas)" : "noir (haut)";
        gameStatusLabel.setText("Jeu prêt ! Vous jouez " + playerSide + ". Utilisez les flèches.");
        gameStatusLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: black;");
        
        // Nettoyer le plateau actuel
        gameArea.getChildren().clear();
        
        // Recréer le plateau et redémarrer le jeu
        createBoard();
    }
    
    public void cleanup() {
        networkRunning = false;
        if (gameLoop != null) {
            gameLoop.stop();
        }
        try {
            if (out != null) {
                try {
                    out.close();
                } catch (IOException e) {
                    // Ignorer les erreurs de fermeture
                }
            }
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    // Ignorer les erreurs de fermeture
                }
            }
            if (clientSocket != null && !clientSocket.isClosed()) {
                clientSocket.close();
            }
            if (serverSocket != null && !serverSocket.isClosed()) {
                serverSocket.close();
            }
        } catch (IOException e) {
            // Ignorer les erreurs de fermeture
        }
    }
}
