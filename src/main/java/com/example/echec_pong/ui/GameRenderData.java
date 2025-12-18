package com.example.echec_pong.ui;

import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Polygon;
import com.example.echec_pong.entity.echec.pions.Pion;
import com.example.echec_pong.game_logic.GameState;

import java.util.Map;

public class GameRenderData {
    public final GameState gameState;
    public final GridPane grid;
    public final Pane overlay;
    public final Rectangle blackPaddleRect;
    public final Rectangle whitePaddleRect;
    public final Circle ballCircle;
    public final Map<Pion, ProgressBar> healthLabels;
    public final Map<Pion, VBox> pieceContainers;
    public final Polygon serveArrow;
    public final Label serveLabel;
    
    // Barre de progression unique pour la capacité spéciale (partagée entre les deux joueurs)
    public final ProgressBar progressBar;
    public final Label progressLabel;
    public final Label specialLabel;
    
    public GameRenderData(GameState gameState, GridPane grid, Pane overlay,
                         Rectangle blackPaddleRect, Rectangle whitePaddleRect,
                         Circle ballCircle, Map<Pion, ProgressBar> pieceHealthLabels,
                         Map<Pion, VBox> pieceContainers,
                         Polygon serveArrow, Label serveLabel,
                         ProgressBar progressBar, Label progressLabel, Label specialLabel) {
        this.gameState = gameState;
        this.grid = grid;
        this.overlay = overlay;
        this.blackPaddleRect = blackPaddleRect;
        this.whitePaddleRect = whitePaddleRect;
        this.ballCircle = ballCircle;
        this.healthLabels = pieceHealthLabels;
        this.pieceContainers = pieceContainers;
        this.serveArrow = serveArrow;
        this.serveLabel = serveLabel;
        this.progressBar = progressBar;
        this.progressLabel = progressLabel;
        this.specialLabel = specialLabel;
    }
}
