package com.example.echec_pong.ui;

import javafx.scene.control.Label;
import javafx.scene.control.Button;
import javafx.scene.layout.Pane;

public class GameViewData {
    public final Label statusLabel;
    public final Pane gameArea;
    public final Button replayButton;

    public GameViewData(Label statusLabel, Pane gameArea, Button replayButton) {
        this.statusLabel = statusLabel;
        this.gameArea = gameArea;
        this.replayButton = replayButton;
    }
}