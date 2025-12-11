package com.example.echec_pong.ui;

import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;

import java.io.IOException;
import java.util.function.Consumer;

public class ViewLoader {
    
    public static void loadRoleSelection(StackPane mainContainer, 
                                        Runnable onHostSelected, 
                                        Runnable onClientSelected) {
        try {
            FXMLLoader loader = new FXMLLoader(ViewLoader.class.getResource("/com/example/echec_pong/RoleSelection.fxml"));
            Node root = loader.load();
            mainContainer.getChildren().setAll(root);

            Button hostButton = (Button) root.lookup("#hostButton");
            Button clientButton = (Button) root.lookup("#clientButton");

            hostButton.setOnAction(e -> onHostSelected.run());
            clientButton.setOnAction(e -> onClientSelected.run());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public static HostSettingsData loadHostSettings(StackPane mainContainer,
                                                     Consumer<HostSettingsData> onStartGame,
                                                     Consumer<Label> onServerStart) {
        try {
            FXMLLoader loader = new FXMLLoader(ViewLoader.class.getResource("/com/example/echec_pong/HostSettings.fxml"));
            Node root = loader.load();
            mainContainer.getChildren().setAll(root);

            TextField widthField = (TextField) root.lookup("#widthField");
            TextField pionHealth = (TextField) root.lookup("#pionHealth");
            TextField cavalierHealth = (TextField) root.lookup("#cavalierHealth");
            TextField fouHealth = (TextField) root.lookup("#fouHealth");
            TextField tourHealth = (TextField) root.lookup("#tourHealth");
            TextField dameHealth = (TextField) root.lookup("#dameHealth");
            TextField roiHealth = (TextField) root.lookup("#roiHealth");
            ComboBox<String> firstServeCombo = (ComboBox<String>) root.lookup("#firstServeCombo");
            Label statusLabel = (Label) root.lookup("#statusLabel");
            Button startButton = (Button) root.lookup("#startButton");
            
            // Nouveaux éléments pour EJB/base de données
            CheckBox useDbSettings = (CheckBox) root.lookup("#useDbSettings");
            ComboBox<String> dbSettingsCombo = (ComboBox<String>) root.lookup("#dbSettingsCombo");
            Button refreshDbButton = (Button) root.lookup("#refreshDbButton");
            Label dbStatusLabel = (Label) root.lookup("#dbStatusLabel");
            HBox dbSettingsBox = (HBox) root.lookup("#dbSettingsBox");
            
            // Initialiser le ComboBox
            if(firstServeCombo != null) {
                firstServeCombo.getItems().addAll("Blanc (Hôte)", "Noir (Client)");
                firstServeCombo.setValue("Blanc (Hôte)");
            }
            
            // Masquer les éléments DB par défaut
            if(dbSettingsBox != null) {
                dbSettingsBox.setVisible(false);
                dbSettingsBox.setManaged(false);
            }
            if(dbStatusLabel != null) {
                dbStatusLabel.setVisible(false);
            }

            HostSettingsData data = new HostSettingsData(widthField, pionHealth, cavalierHealth, 
                                                         fouHealth, tourHealth, dameHealth, 
                                                         roiHealth, firstServeCombo, statusLabel,
                                                         useDbSettings, dbSettingsCombo, refreshDbButton,
                                                         dbStatusLabel, dbSettingsBox);

            startButton.setOnAction(e -> onStartGame.accept(data));
            onServerStart.accept(statusLabel);
            
            return data;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
    
    public static ClientWaitingData loadClientWaiting(StackPane mainContainer) {
        try {
            FXMLLoader loader = new FXMLLoader(ViewLoader.class.getResource("/com/example/echec_pong/ClientWaiting.fxml"));
            Node root = loader.load();
            mainContainer.getChildren().setAll(root);
            
            TextField serverIpField = (TextField) root.lookup("#serverIpField");
            Button connectButton = (Button) root.lookup("#connectButton");
            Label statusLabel = (Label) root.lookup("#statusLabel");
            
            return new ClientWaitingData(serverIpField, connectButton, statusLabel);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
    
    public static GameViewData loadGame(StackPane mainContainer) {
        try {
            FXMLLoader loader = new FXMLLoader(ViewLoader.class.getResource("/com/example/echec_pong/Game.fxml"));
            Node root = loader.load();
            mainContainer.getChildren().setAll(root);

            Label gameStatusLabel = (Label) root.lookup("#gameStatusLabel");
            Pane gameArea = (Pane) root.lookup("#gameArea");
            Button replayButton = (Button) root.lookup("#replayButton");
            Button restartButton = (Button) root.lookup("#restartButton");
            Button backToSettingsButton = (Button) root.lookup("#backToSettingsButton");

            return new GameViewData(gameStatusLabel, gameArea, replayButton, restartButton, backToSettingsButton);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
