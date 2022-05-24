package com.template.space;

import javafx.animation.AnimationTimer;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

public class SampleController {
    @FXML
    private Canvas gameCanvas;
    @FXML
    private AnchorPane gamePane;

    // This variable allows to change the resource filepath easily
    private final String filepath = "C:\\Users\\trunt\\IdeaProjects\\SpaceTemplateCREdition\\src\\main\\resources\\com\\template\\space\\";
    // This value controls the size of individual tiles
    private final int tileSize = 50;
    
    Player player;
    
    int[][] currentLevel;
    
    // optional centering of the display of the level (looks nice and uses simple math) OPTIONAL
    private double levelWidth;
    private double levelHeight;

    private double xOffset;
    private double yOffset;
    // end of centering calculations


    // Image[] tileset stores all the tiles that the level uses
    // I recommend assigning the # in the 2D level array corresponding to the order of this array
    // EX: floor_1.png is at index 0, and is represented in the level array as 1, floor_2.png is at index 1 and corresponds to 2, etc.
    // This organization should follow the rule: Index = (Level Tile Number - 1)
    private Image[] tileset = {
            new Image(filepath + "floor_1.png"), new Image(filepath + "floor_2.png"),
            new Image(filepath + "pedestal_coin.png"), new Image(filepath + "pedestal.png")
    };

    // Image[] playerFrames stores all the frames of the player animation
    private Image[] playerFrames = {
            new Image(filepath + "player_0.png"), new Image(filepath + "player_1.png")
    };

    public void setup() {
        System.out.println("Startin'");

        // Creating the ImageView for the player
        Image playerIcon = new Image(filepath + "player_1.png");
        ImageView playerView = new ImageView(playerIcon);

        playerView.setFitWidth(tileSize);
        playerView.setFitHeight(tileSize);

        gamePane.getChildren().add(playerView);

        Label scoreLabel = new Label("Score: 0");
        scoreLabel.setFont(Font.font("Copperplate Gothic Light",20));
        scoreLabel.setTextFill(Color.WHITE);
        scoreLabel.setLayoutX(750);
        scoreLabel.setLayoutY(50);
        //scoreLabel.setFont();
        gamePane.getChildren().add(scoreLabel);

        Button rerollButton = new Button();
        rerollButton.setBackground(new Background(new BackgroundFill(Color.BLACK,new CornerRadii(0),new Insets(1))));
        rerollButton.setFont(Font.font("Copperplate Gothic Light",20));
        rerollButton.setTextFill(Color.WHITE);
        rerollButton.setText("REROLL");
        rerollButton.setLayoutX(50);
        rerollButton.setLayoutY(50);
        gamePane.getChildren().add(rerollButton);

        player = new Player();

        currentLevel = player.getLevel();

        recalculateCentering();

        playerView.setLayoutX(player.getX()*tileSize + xOffset);
        playerView.setLayoutY(player.getY()*tileSize + yOffset);

        // Non Continous Movement
        EventHandler<KeyEvent> movementKeyDown = new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent keyPressed) {
                System.out.println("pressin'" + keyPressed.getCharacter());

                // has to use keyPressed.getCharacter() instead of .getCode() because it is a typed event for some reason
               // String pressedCharacter = keyPressed.getCharacter();

                if(keyPressed.getCharacter().equalsIgnoreCase("w")) {
                    if (player.move("UP"))
                        playerView.setLayoutY(playerView.getLayoutY() - tileSize);
                }
                else if(keyPressed.getCharacter().equalsIgnoreCase("a")) {
                    if (player.move("LEFT"))
                        playerView.setLayoutX(playerView.getLayoutX() - tileSize);
                }
                else if(keyPressed.getCharacter().equalsIgnoreCase("s")) {
                    if (player.move("DOWN"))
                        playerView.setLayoutY(playerView.getLayoutY() + tileSize);
                }
                else if(keyPressed.getCharacter().equalsIgnoreCase("d")) {
                    if (player.move("RIGHT"))
                        playerView.setLayoutX(playerView.getLayoutX() + tileSize);
                }

                player.checkForCoins();

                //System.out.println("Score: " + player.getScore());
                scoreLabel.setText("Score: " + player.getScore());

                // No need to redraw the entire level for this game
                //drawLevel(levelTest, xOffset, yOffset);
                // Instead redraw the tile the player stepped on
                updateTile(player.getY(), player.getX());

                //printArray(levelTest);
            }
        };

        //rerollButton.addEventHandler(KeyEvent.KEY_TYPED, movementKeyDown);
        gamePane.addEventHandler(KeyEvent.KEY_TYPED, movementKeyDown);

        // initial drawing of the level
        drawLevel();



        // regenerate the level OPTIONAL
        rerollButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                player.regenerateLevel();

                currentLevel = player.getLevel();
                recalculateCentering();

                playerView.setLayoutX(player.getX()*tileSize + xOffset);
                playerView.setLayoutY(player.getY()*tileSize + yOffset);

                drawLevel();
            }
        });

        // character animation
        final int[] frame = {1};
        AnimationTimer idleAnimation = new AnimationTimer() {
            private long lastUpdate = 0;

            @Override
            public void handle(long now) {
                if (now - lastUpdate >= (1000/8) * 1_000_000) {
                    playerView.setImage(playerFrames[frame[0]%playerFrames.length]);
                    frame[0]++;
                    lastUpdate = now ;
                }
            }
        };

        idleAnimation.start();

    }

    private void recalculateCentering() {
        levelWidth = currentLevel[0].length*tileSize;
        levelHeight = currentLevel.length*tileSize;

        xOffset = (gameCanvas.getWidth()-levelWidth)/2.0;
        yOffset = (gameCanvas.getHeight()-levelHeight)/2.0;
    }

    // Debugging Method -> Prints out the integer array
    private void printArray(int[][] grid) {
        for(int[] row : grid) {
            for(int tile : row) {
                System.out.print(tile+" ");
            }
            System.out.println();
        }
    }

    private void drawLevel() {
     
        // in order to have the white outline around the unorthodox level shape
        // I will need to first print out white squares "below" the level itself
        // and then print the level over it

        GraphicsContext gc = gameCanvas.getGraphicsContext2D();

        // Draw the Space Background
        gc.drawImage(new Image(filepath + "space_background.png"),0,0,900,600);

        // Draw the white outline
        gc.setFill(Color.rgb(244,244,244));
        for (int i = 0; i < currentLevel.length; i++) {
            for (int j = 0; j < currentLevel[0].length; j++) {
                if(currentLevel[i][j] != 0)
                    gc.fillRect(tileSize * j + xOffset-3, tileSize*i + yOffset-3, tileSize+6, tileSize+6);
            }
        }

        // Draw the Level
        for (int i = 0; i < currentLevel.length; i++) {
            for (int j = 0; j < currentLevel[0].length; j++) {
                if(currentLevel[i][j] != 0)
                    updateTile(i, j);
            }
        }
    }

    private void updateTile(int row, int col) {
        GraphicsContext gc = gameCanvas.getGraphicsContext2D();
        gc.setLineWidth(1);
        // tile
        gc.drawImage(tileset[currentLevel[row][col]-1],tileSize * col + xOffset, tileSize*row + yOffset, tileSize, tileSize);
        // grid
        gc.strokeRect(tileSize * col + xOffset, tileSize*row + yOffset, tileSize, tileSize);
    }
}