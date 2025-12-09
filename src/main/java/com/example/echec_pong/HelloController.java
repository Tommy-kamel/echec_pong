package com.example.echec_pong;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.StackPane;

import java.io.*;
import java.net.*;

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
    public void initialize() {
        loadRoleSelection();
    }

    private void loadRoleSelection() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("RoleSelection.fxml"));
            Node root = loader.load();
            mainContainer.getChildren().setAll(root);

            Button hostButton = (Button) root.lookup("#hostButton");
            Button clientButton = (Button) root.lookup("#clientButton");

            hostButton.setOnAction(e -> choisirHote());
            clientButton.setOnAction(e -> choisirClient());
        } catch (IOException e) {
            e.printStackTrace();
        }
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
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("HostSettings.fxml"));
            Node root = loader.load();
            mainContainer.getChildren().setAll(root);

            TextField widthField = (TextField) root.lookup("#widthField");
            TextField heightField = (TextField) root.lookup("#heightField");
            TextField pionHealth = (TextField) root.lookup("#pionHealth");
            TextField cavalierHealth = (TextField) root.lookup("#cavalierHealth");
            TextField fouHealth = (TextField) root.lookup("#fouHealth");
            TextField tourHealth = (TextField) root.lookup("#tourHealth");
            TextField dameHealth = (TextField) root.lookup("#dameHealth");
            TextField roiHealth = (TextField) root.lookup("#roiHealth");
            Label statusLabel = (Label) root.lookup("#statusLabel");
            Button startButton = (Button) root.lookup("#startButton");

            startButton.setOnAction(e -> demarrerJeuHost(widthField, heightField, pionHealth, cavalierHealth, fouHealth, tourHealth, dameHealth, roiHealth, statusLabel));
            startServer(statusLabel);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadClientWaiting() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("ClientWaiting.fxml"));
            Node root = loader.load();
            mainContainer.getChildren().setAll(root);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadGame() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("Game.fxml"));
            Node root = loader.load();
            mainContainer.getChildren().setAll(root);
        } catch (IOException e) {
            e.printStackTrace();
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
                height = in.readInt();
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

    private void demarrerJeuHost(TextField tfWidth, TextField tfHeight, TextField tfPionHealth, TextField tfCavalierHealth, TextField tfFouHealth, TextField tfTourHealth, TextField tfDameHealth, TextField tfRoiHealth, Label statusLabel) {
        if (!clientConnected) {
            statusLabel.setText("Attendez que le client se connecte !");
            return;
        }

        width = Integer.parseInt(tfWidth.getText());
        height = Integer.parseInt(tfHeight.getText());
        pionHealth = Integer.parseInt(tfPionHealth.getText());
        cavalierHealth = Integer.parseInt(tfCavalierHealth.getText());
        fouHealth = Integer.parseInt(tfFouHealth.getText());
        tourHealth = Integer.parseInt(tfTourHealth.getText());
        dameHealth = Integer.parseInt(tfDameHealth.getText());
        roiHealth = Integer.parseInt(tfRoiHealth.getText());

        try {
            out.writeInt(width);
            out.writeInt(height);
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
}
