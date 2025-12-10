package com.example.echec_pong.ui;

import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.geometry.Pos;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Polygon;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import com.example.echec_pong.entity.echec.pions.*;
import com.example.echec_pong.entity.pong.accessoires.Raquette;
import com.example.echec_pong.entity.pong.accessoires.Balle;
import com.example.echec_pong.game_logic.GameState;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BoardRenderer {
    private static final double CELL_SIZE = 60.0;
    private static final int BOARD_ROWS = 8; // Fixe: mêmes espaces que l'échiquier standard
    
    public static GameRenderData renderBoard(Pane gameArea, int width, int height, 
                                   int pionHealth, int cavalierHealth, int fouHealth, 
                                   int tourHealth, int dameHealth, int roiHealth, boolean isHost) {
        gameArea.getChildren().clear();

        double boardWidth = width * CELL_SIZE;
        double boardHeight = BOARD_ROWS * CELL_SIZE;

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
        
        // Create grid (toujours 8 rangées pour conserver l'espace standard)
        for(int row=0; row<BOARD_ROWS; row++){
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
                }else if(row==BOARD_ROWS-2 && col < whitePawns.size()){
                    piece = whitePawns.get(col);
                }else if(row==BOARD_ROWS-1 && col < whiteMainPieces.size()){
                    piece = whiteMainPieces.get(col);
                }
                
                if(piece != null){
                    VBox pieceContainer = new VBox();
                    pieceContainer.setAlignment(Pos.CENTER);
                    
                    Label symbolLabel = new Label(piece.getSymbol());
                    symbolLabel.setFont(Font.font("System", FontWeight.BOLD, 36));
                    
                    Label healthLabel = new Label("HP:" + piece.getSante());
                    healthLabel.setFont(Font.font("System", FontWeight.NORMAL, 10));
                    healthLabel.setTextFill(Color.RED);
                    // All pieces of the same player have the same color
                    if(row <= 1){
                        // Black player (top) - always black pieces
                        symbolLabel.setTextFill(Color.BLACK);
                    }else{
                        // White player (bottom) - always white/light gray pieces  
                        symbolLabel.setTextFill(Color.GRAY);
                    }
                    
                    pieceContainer.getChildren().addAll(symbolLabel, healthLabel);
                    cell.getChildren().add(pieceContainer);
                }
                
                grid.add(cell, col, row);
            }
        }
        // Overlay container for paddles and ball, same size as the board
        Pane overlay = new Pane();
        overlay.setPrefSize(boardWidth, boardHeight);

        // Create paddles positions: between pawns (rows 1 and height-2)
        double paddleWidth = width * CELL_SIZE * 0.15;
        double paddleHeight = 10;

        double blackPaddleX = ((width * CELL_SIZE) - paddleWidth) / 2;
        // Rapproché du rang des pions noirs (haut du rang vide)
        double blackPaddleY = (2 * CELL_SIZE) + (CELL_SIZE * 0.25) - (paddleHeight / 2);
        Raquette raquetteNoir = new Raquette(blackPaddleX, blackPaddleY, paddleWidth, paddleHeight, "noir");
        Rectangle blackPaddleRect = new Rectangle(paddleWidth, paddleHeight);
        blackPaddleRect.setFill(Color.DARKBLUE);
        blackPaddleRect.setStroke(Color.YELLOW);
        blackPaddleRect.setStrokeWidth(2);
        blackPaddleRect.setLayoutX(blackPaddleX);
        blackPaddleRect.setLayoutY(blackPaddleY);

        double whitePaddleX = ((width * CELL_SIZE) - paddleWidth) / 2;
        // Rapproché du rang des pions blancs (bas du rang vide)
        double whitePaddleY = ((BOARD_ROWS - 2) * CELL_SIZE) - (CELL_SIZE * 0.25) - (paddleHeight / 2);
        Raquette raquetteBlanc = new Raquette(whitePaddleX, whitePaddleY, paddleWidth, paddleHeight, "blanc");
        Rectangle whitePaddleRect = new Rectangle(paddleWidth, paddleHeight);
        whitePaddleRect.setFill(Color.LIGHTBLUE);
        whitePaddleRect.setStroke(Color.YELLOW);
        whitePaddleRect.setStrokeWidth(2);
        whitePaddleRect.setLayoutX(whitePaddleX);
        whitePaddleRect.setLayoutY(whitePaddleY);

        // Ball at center
        double ballRadius = 8;
        double ballX = (width * CELL_SIZE) / 2;
        double ballY = (BOARD_ROWS * CELL_SIZE) / 2;
        Balle balle = new Balle(ballX, ballY, 1, 1, ballRadius);
        Circle ballCircle = new Circle(ballRadius);
        ballCircle.setFill(Color.RED);
        ballCircle.setCenterX(ballX);
        ballCircle.setCenterY(ballY);
        ballCircle.setLayoutX(0);
        ballCircle.setLayoutY(0);

        overlay.getChildren().addAll(blackPaddleRect, whitePaddleRect, ballCircle);

        StackPane boardContainer = new StackPane();
        boardContainer.setPrefSize(boardWidth, boardHeight);
        boardContainer.setMaxSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE);
        StackPane.setAlignment(grid, Pos.CENTER);
        StackPane.setAlignment(overlay, Pos.CENTER);
        boardContainer.getChildren().addAll(grid, overlay);
        
        // Client sees the board rotated 180 degrees (their pieces at bottom)
        if (!isHost) {
            boardContainer.setRotate(180);
            // Rotate individual pieces back so they're readable
            for (javafx.scene.Node node : grid.getChildren()) {
                if (node instanceof StackPane) {
                    for (javafx.scene.Node child : ((StackPane) node).getChildren()) {
                        if (child instanceof VBox) {
                            child.setRotate(180);
                        }
                    }
                }
            }
            // Rotate paddles back (ball doesn't need rotation as it's a circle)
            blackPaddleRect.setRotate(180);
            whitePaddleRect.setRotate(180);
        }

        gameArea.getChildren().add(boardContainer);
        
        // Créer GameState et ajouter les pièces
        GameState gameState = new GameState(width);
        gameState.setRaquetteNoir(raquetteNoir);
        gameState.setRaquetteBlanc(raquetteBlanc);
        gameState.setBalle(balle);
        
        // Store isHost for later use
        boolean isClientView = !isHost;
        
        // Ajouter toutes les pièces au GameState avec leurs positions
        for(int i = 0; i < blackMainPieces.size(); i++) {
            gameState.addPiece(blackMainPieces.get(i), 0, i);
        }
        for(int i = 0; i < blackPawns.size(); i++) {
            gameState.addPiece(blackPawns.get(i), 1, i);
        }
        for(int i = 0; i < whitePawns.size(); i++) {
            gameState.addPiece(whitePawns.get(i), BOARD_ROWS - 2, i);
        }
        for(int i = 0; i < whiteMainPieces.size(); i++) {
            gameState.addPiece(whiteMainPieces.get(i), BOARD_ROWS - 1, i);
        }
        
        // Créer map des pièces vers leurs labels pour mise à jour
        Map<Pion, Label> pieceHealthLabels = new HashMap<>();
        for(int row = 0; row < BOARD_ROWS; row++) {
            for(int col = 0; col < width; col++) {
                Pion piece = gameState.getPieceAt(row, col);
                if(piece != null) {
                    StackPane cell = (StackPane) grid.getChildren().get(row * width + col);
                    VBox container = (VBox) cell.getChildren().get(1);
                    Label healthLabel = (Label) container.getChildren().get(1);
                    pieceHealthLabels.put(piece, healthLabel);
                }
            }
        }
        
        // Créer la flèche de service (initialement invisible)
        Polygon serveArrow = new Polygon();
        serveArrow.getPoints().addAll(
            0.0, -30.0,  // pointe haute
            -15.0, 0.0,  // gauche
            -5.0, 0.0,   // gauche intérieur
            -5.0, 20.0,  // bas gauche
            5.0, 20.0,   // bas droite
            5.0, 0.0,    // droite intérieur
            15.0, 0.0    // droite
        );
        serveArrow.setFill(Color.YELLOW);
        serveArrow.setStroke(Color.ORANGE);
        serveArrow.setStrokeWidth(2);
        serveArrow.setVisible(false);
        
        // Label pour indiquer le service
        Label serveLabel = new Label("Appuyez sur ESPACE pour servir");
        serveLabel.setFont(Font.font("System", FontWeight.BOLD, 16));
        serveLabel.setTextFill(Color.YELLOW);
        serveLabel.setStyle("-fx-background-color: rgba(0,0,0,0.7); -fx-padding: 10;");
        serveLabel.setVisible(false);
        
        // Positionner la flèche et le label près de la balle
        double centerX = (width * CELL_SIZE) / 2;
        double centerY = (BOARD_ROWS * CELL_SIZE) / 2;
        serveArrow.setLayoutX(centerX);
        serveArrow.setLayoutY(centerY - 60); // Au-dessus de la balle
        serveLabel.setLayoutX(centerX - 150);
        serveLabel.setLayoutY(centerY + 50);
        
        // Rotate label back for client so text is readable
        if (isClientView) {
            serveLabel.setRotate(180);
            // La flèche sera tournée dynamiquement dans updateUI() en tenant compte de la rotation du plateau
        }
        
        overlay.getChildren().addAll(serveArrow, serveLabel);
        
        return new GameRenderData(gameState, grid, overlay, blackPaddleRect, whitePaddleRect, 
                                  ballCircle, pieceHealthLabels, serveArrow, serveLabel);
    }
    
}
