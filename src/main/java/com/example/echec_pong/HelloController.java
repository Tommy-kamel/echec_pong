package com.example.echec_pong;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.Pane;

import java.io.*;
import java.net.*;

import com.example.echec_pong.ui.ViewLoader;
import com.example.echec_pong.ui.BoardRenderer;
import com.example.echec_pong.ui.HostSettingsData;
import com.example.echec_pong.ui.GameViewData;

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
                Platform.runLater(() -> loadGame());
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

        try {
            out.writeInt(width);
            out.writeInt(pionHealth);
            out.writeInt(cavalierHealth);
            out.writeInt(fouHealth);
            out.writeInt(tourHealth);
            out.writeInt(dameHealth);
            out.writeInt(roiHealth);
            out.flush();
            loadGame();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void createBoard() {
        BoardRenderer.renderBoard(gameArea, width, height, pionHealth, cavalierHealth, 
                                  fouHealth, tourHealth, dameHealth, roiHealth);
    }
}
