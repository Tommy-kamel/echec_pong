package com.example.echec_pong.ui;

import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.Region;
import javafx.geometry.Pos;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Circle;
import com.example.echec_pong.entity.echec.pions.*;
import com.example.echec_pong.entity.pong.accessoires.Raquette;
import com.example.echec_pong.entity.pong.accessoires.Balle;

import java.util.ArrayList;
import java.util.List;

public class BoardRenderer {
    private static final double CELL_SIZE = 60.0;
    
    public static void renderBoard(Pane gameArea, int width, int height, 
                                   int pionHealth, int cavalierHealth, int fouHealth, 
                                   int tourHealth, int dameHealth, int roiHealth) {
        gameArea.getChildren().clear();

        double boardWidth = width * CELL_SIZE;
        double boardHeight = height * CELL_SIZE;

        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        
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
                Rectangle rect = new Rectangle(CELL_SIZE,CELL_SIZE);
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
        // Overlay container for paddles and ball, same size as the board
        Pane overlay = new Pane();
        overlay.setPrefSize(boardWidth, boardHeight);

        // Create paddles positions: between pawns (rows 1 and height-2)
        double paddleWidth = boardWidth / 3;
        double paddleHeight = 10;

        double blackPaddleX = (boardWidth - paddleWidth) / 2;
        double blackPaddleY = (2 * CELL_SIZE) - (paddleHeight / 2);
        Raquette raquetteNoir = new Raquette(blackPaddleX, blackPaddleY, paddleWidth, paddleHeight, "noir");
        Rectangle blackPaddleRect = new Rectangle(paddleWidth, paddleHeight);
        blackPaddleRect.setFill(Color.DARKBLUE);
        blackPaddleRect.setStroke(Color.YELLOW);
        blackPaddleRect.setStrokeWidth(2);
        blackPaddleRect.setLayoutX(blackPaddleX);
        blackPaddleRect.setLayoutY(blackPaddleY);

        double whitePaddleX = (boardWidth - paddleWidth) / 2;
        double whitePaddleY = ((height - 2) * CELL_SIZE) - (paddleHeight / 2);
        Raquette raquetteBlanc = new Raquette(whitePaddleX, whitePaddleY, paddleWidth, paddleHeight, "blanc");
        Rectangle whitePaddleRect = new Rectangle(paddleWidth, paddleHeight);
        whitePaddleRect.setFill(Color.LIGHTBLUE);
        whitePaddleRect.setStroke(Color.YELLOW);
        whitePaddleRect.setStrokeWidth(2);
        whitePaddleRect.setLayoutX(whitePaddleX);
        whitePaddleRect.setLayoutY(whitePaddleY);

        // Ball at center
        double ballRadius = 8;
        double ballX = boardWidth / 2;
        double ballY = boardHeight / 2;
        Balle balle = new Balle(ballX, ballY, 3, 3, ballRadius);
        Circle ballCircle = new Circle(ballRadius);
        ballCircle.setFill(Color.RED);
        ballCircle.setLayoutX(ballX);
        ballCircle.setLayoutY(ballY);

        overlay.getChildren().addAll(blackPaddleRect, whitePaddleRect, ballCircle);

        StackPane boardContainer = new StackPane();
        boardContainer.setPrefSize(boardWidth, boardHeight);
        boardContainer.setMaxSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE);
        StackPane.setAlignment(grid, Pos.CENTER);
        StackPane.setAlignment(overlay, Pos.CENTER);
        boardContainer.getChildren().addAll(grid, overlay);

        gameArea.getChildren().add(boardContainer);
    }
}
