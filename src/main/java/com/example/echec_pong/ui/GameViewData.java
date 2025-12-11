package com.example.echec_pong.ui;

import javafx.scene.control.Label;
import javafx.scene.control.Button;
import javafx.scene.layout.Pane;

public class GameViewData {
    public final Label statusLabel;
    public final Pane gameArea;
    public final Button replayButton;
    public final Button restartButton;
    public final Button backToSettingsButton;

    public GameViewData(Label statusLabel, Pane gameArea, Button replayButton, Button restartButton, Button backToSettingsButton) {
        this.statusLabel = statusLabel;
        this.gameArea = gameArea;
        this.replayButton = replayButton;
        this.restartButton = restartButton;
        this.backToSettingsButton = backToSettingsButton;
    }
}