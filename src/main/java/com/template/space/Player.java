package com.template.space;

// Player object is responsible for storing the player's position
// And moving them around the level (+ checking if a player can move in a certain direction)
public class Player {
    private int [][] level;
    private int xPos;
    private int yPos;

    private int score = 0;

    // A Player object constructor which randomly generates a new level of random size (1x1 to 10x10)
    // and then sets the Player's position in the middle of the newly generated level
    public Player() {
        level = new int[(int)(Math.random()*10)+1][(int)(Math.random()*10)+1];
        generateLevel();
        xPos = level[0].length/2;
        yPos = level.length/2;
    }

    // "unorthodox" shaped level -> a level with an impassable hole in the center
    public Player(String unorthodox) {
        // Note for Future: It would make more sense to store the empty tiles as -1 instead of 0
        level = new int[][] {
                {0,0,0,2,2,0,0,0},
                {0,0,2,1,1,2,0,0},
                {2,1,1,0,0,1,1,2},
                {2,1,1,0,0,1,1,2},
                {0,0,2,1,1,2,0,0},
                {0,0,0,2,2,0,0,0}
        };

        xPos = level[0].length/2;
        yPos = level.length/2;

        // if the level has a hole in the middle, spawn the player somewhere that has solid ground underneath
        while(level[yPos][xPos] == 0) {
            xPos = (int) (Math.random() * level[0].length - 1) + 1;
            yPos = (int) (Math.random() * level.length - 1) + 1;
        }
    }

    // Generates the level randomly by randomly filling the level array with tiles 1 through 3
    private void generateLevel() {
        for (int i = 0; i < level.length; i++) {
            for (int j = 0; j < level[0].length; j++) {
                // sets the tile's value randomly from 1-3
                level[i][j] = (int)(Math.random()*3)+1;
            }
        }
    }

    // Re-generates the Level
    public void regenerateLevel() {
        level = new int[(int)(Math.random()*10)+1][(int)(Math.random()*10)+1];
        generateLevel();
        xPos = level[0].length/2;
        yPos = level.length/2;
    }

    // Set/Get methods for updating the Player's position
    public int getX() {
        return xPos;
    }
    public void setX(int xPos) {
        this.xPos = xPos;
    }
    public int getY() {
        return yPos;
    }
    public void setY(int yPos) {
        this.yPos = yPos;
    }

    // Checks if the player can move in a certain direction, and updates their position
    public boolean move(String direction) {
        if(direction.equalsIgnoreCase("up")) {
            if(yPos>0)
            {
                if(level[yPos-1][xPos] != 0) {
                    yPos--;
                    checkForCoins();
                    return true;
                }
            }
        }
        if(direction.equalsIgnoreCase("down")) {
            if(yPos<level.length-1)
            {
                if(level[yPos+1][xPos] != 0) {
                    yPos++;
                    checkForCoins();
                    return true;
                }
            }
        }
        if(direction.equalsIgnoreCase("right")) {
            if(xPos<level[0].length-1)
            {
                if(level[yPos][xPos+1] != 0) {
                    xPos++;
                    checkForCoins();
                    return true;
                }
            }
        }
        if(direction.equalsIgnoreCase("left")) {
            if(xPos>0)
            {
                if(level[yPos][xPos-1] != 0) {
                    xPos--;
                    checkForCoins();
                    return true;
                }
            }
        }

        return false;
    }

    // if a Player's position matches with the position of a coin, update the score and remove the coin
    public void checkForCoins() {
        if(level[yPos][xPos] == 3) {
            score++;
            level[yPos][xPos] = 4;
        }
    }

    public int[][] getLevel() {
        return level;
    }
    public int getScore() {
        return score;
    }
}
