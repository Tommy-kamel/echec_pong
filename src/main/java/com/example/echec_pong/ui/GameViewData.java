package com.example.echec_pong.ui;

import javafx.scene.control.Label;
import javafx.scene.layout.Pane;

public class GameViewData {
    public final Label statusLabel;
    public final Pane gameArea;

    public GameViewData(Label statusLabel, Pane gameArea) {
        this.statusLabel = statusLabel;
        this.gameArea = gameArea;
    }
}