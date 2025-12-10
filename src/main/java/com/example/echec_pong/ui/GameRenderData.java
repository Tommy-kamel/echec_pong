package com.example.echec_pong.ui;

import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
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
    public final Map<Pion, Label> healthLabels;
    
    public GameRenderData(GameState gameState, GridPane grid, Pane overlay,
                         Rectangle blackPaddleRect, Rectangle whitePaddleRect,
                         Circle ballCircle, Map<Pion, Label> pieceHealthLabels) {
        this.gameState = gameState;
        this.grid = grid;
        this.overlay = overlay;
        this.blackPaddleRect = blackPaddleRect;
        this.whitePaddleRect = whitePaddleRect;
        this.ballCircle = ballCircle;
        this.healthLabels = pieceHealthLabels;
    }
}
