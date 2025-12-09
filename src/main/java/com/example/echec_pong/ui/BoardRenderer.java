package com.example.echec_pong.ui;

import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import com.example.echec_pong.entity.echec.pions.*;

import java.util.ArrayList;
import java.util.List;

public class BoardRenderer {
    
    public static void renderBoard(Pane gameArea, int width, int height, 
                                   int pionHealth, int cavalierHealth, int fouHealth, 
                                   int tourHealth, int dameHealth, int roiHealth) {
        GridPane grid = new GridPane();
        grid.setAlignment(javafx.geometry.Pos.CENTER);
        
        // Create pieces for black player (top)
        List<Pion> blackMainPieces = new ArrayList<>();
        List<Pion> blackPawns = new ArrayList<>();
        
        // Create pieces for white player (bottom)
        List<Pion> whitePawns = new ArrayList<>();
        List<Pion> whiteMainPieces = new ArrayList<>();
        
        // Adapt layout based on board width
        if(width == 2){
            // 2x4: Dame, Roi
            blackMainPieces.add(new Dame("noir", 0, dameHealth));
            blackMainPieces.add(new Roi("noir", 1, roiHealth));
            whiteMainPieces.add(new Dame("blanc", 0, dameHealth));
            whiteMainPieces.add(new Roi("blanc", 1, roiHealth));
        }else if(width == 4){
            // 4x6: Fou, Dame, Roi, Fou
            blackMainPieces.add(new Fou("noir", 0, fouHealth));
            blackMainPieces.add(new Dame("noir", 1, dameHealth));
            blackMainPieces.add(new Roi("noir", 2, roiHealth));
            blackMainPieces.add(new Fou("noir", 3, fouHealth));
            whiteMainPieces.add(new Fou("blanc", 0, fouHealth));
            whiteMainPieces.add(new Dame("blanc", 1, dameHealth));
            whiteMainPieces.add(new Roi("blanc", 2, roiHealth));
            whiteMainPieces.add(new Fou("blanc", 3, fouHealth));
        }else if(width == 6){
            // 6x8: Cavalier, Fou, Dame, Roi, Fou, Cavalier
            blackMainPieces.add(new Cavalier("noir", 0, cavalierHealth));
            blackMainPieces.add(new Fou("noir", 1, fouHealth));
            blackMainPieces.add(new Dame("noir", 2, dameHealth));
            blackMainPieces.add(new Roi("noir", 3, roiHealth));
            blackMainPieces.add(new Fou("noir", 4, fouHealth));
            blackMainPieces.add(new Cavalier("noir", 5, cavalierHealth));
            whiteMainPieces.add(new Cavalier("blanc", 0, cavalierHealth));
            whiteMainPieces.add(new Fou("blanc", 1, fouHealth));
            whiteMainPieces.add(new Dame("blanc", 2, dameHealth));
            whiteMainPieces.add(new Roi("blanc", 3, roiHealth));
            whiteMainPieces.add(new Fou("blanc", 4, fouHealth));
            whiteMainPieces.add(new Cavalier("blanc", 5, cavalierHealth));
        }else{
            // 8x10: Standard chess layout
            for(int i=0; i<width; i++){
                if(i==0 || i==7){
                    blackMainPieces.add(new Tour("noir", i, tourHealth));
                    whiteMainPieces.add(new Tour("blanc", i, tourHealth));
                }else if(i==1 || i==6){
                    blackMainPieces.add(new Cavalier("noir", i, cavalierHealth));
                    whiteMainPieces.add(new Cavalier("blanc", i, cavalierHealth));
                }else if(i==2 || i==5){
                    blackMainPieces.add(new Fou("noir", i, fouHealth));
                    whiteMainPieces.add(new Fou("blanc", i, fouHealth));
                }else if(i==3){
                    blackMainPieces.add(new Dame("noir", i, dameHealth));
                    whiteMainPieces.add(new Dame("blanc", i, dameHealth));
                }else if(i==4){
                    blackMainPieces.add(new Roi("noir", i, roiHealth));
                    whiteMainPieces.add(new Roi("blanc", i, roiHealth));
                }
            }
        }
        
        // Add pawns for all columns
        for(int i=0; i<width; i++){
            blackPawns.add(new Pion("noir", i, pionHealth));
            whitePawns.add(new Pion("blanc", i, pionHealth));
        }
        
        // Create grid
        for(int row=0; row<height; row++){
            for(int col=0; col<width; col++){
                Rectangle rect = new Rectangle(60,60);
                rect.setFill((row+col)%2==0 ? Color.BEIGE : Color.SADDLEBROWN);
                StackPane cell = new StackPane();
                cell.getChildren().add(rect);
                
                // Add piece based on row
                Pion piece = null;
                if(row==0 && col < blackMainPieces.size()){
                    piece = blackMainPieces.get(col);
                }else if(row==1 && col < blackPawns.size()){
                    piece = blackPawns.get(col);
                }else if(row==height-2 && col < whitePawns.size()){
                    piece = whitePawns.get(col);
                }else if(row==height-1 && col < whiteMainPieces.size()){
                    piece = whiteMainPieces.get(col);
                }
                
                if(piece != null){
                    Label label = new Label(piece.getSymbol());
                    label.setStyle("-fx-font-size: 40px; -fx-font-weight: bold;");
                    // All pieces of the same player have the same color
                    if(row <= 1){
                        // Black player (top) - always black pieces
                        label.setTextFill(Color.BLACK);
                    }else{
                        // White player (bottom) - always white/light gray pieces  
                        label.setTextFill(Color.WHITESMOKE);
                    }
                    cell.getChildren().add(label);
                }
                
                grid.add(cell, col, row);
            }
        }
        gameArea.getChildren().add(grid);
    }
}
