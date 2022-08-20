package minesweeperfx.textualMinesweeper;

import java.util.Random;

public class MinefieldConsole {

    private final MineTile[][] minefield;
    private final int rows, columns;

    private Integer seed;
    private boolean gameOver = false, won = false;
    
    /**
     * Create an instance of the minefield class based from the parameters rows and columns.
     * 
     * @param rows
     * @param columns
    */
    public MinefieldConsole(final int rows, final int columns) {
        this.rows = rows;
        this.columns = columns;

        minefield = new MineTile[rows][columns];

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                minefield[i][j] = new MineTile();
            }
        }
    }

    private boolean mineTile(int row, int column) {
        MineTile currentTile = minefield[row][column];

        if (currentTile.isMined()) {
            return false;
        }

        currentTile.setMined(true);

        for (int i = -1; i <= 1; i++) {
            for (int j = -1; j <= 1; j++) {
                int xOffset = row + j;
                int yOffset = column + i;

                if (isInRange(xOffset, yOffset)) {
                    minefield[xOffset][yOffset].updateNeighbours();
                }
            }
        }

        currentTile.updateNeighbours();

        return true;
    }

    /**
     * Set a seed of the Random class, used to generate a pre-calculated minefield
     * 
     * @param seed
    */
    public void setSeed(final int seed) {
        this.seed = seed;
    }

    /**
     * Populate the minefield with a set number of mines. 
     * 
     * @param numMines
    */
    public void populate(int numMines) {
        Random rand = seed != null ? new Random(seed) : new Random();

        int row, column, i = 0;

        numMines = numMines >= (this.rows * this.columns) ? (this.rows * this.columns) - 1 : numMines;

        while (i < numMines) {
            row = rand.nextInt(this.rows);
            column = rand.nextInt(this.columns);

            if (!minefield[row][column].isMined() && !(row == 0 && column == 0)) {
                mineTile(row, column);
                i++;
            }
        }
    }
    
    /**
     * 
     * 
     * @param row
     * @param column
     * @return boolean
    */
    public boolean step(int row, int column) {
        MineTile currentTile;

        if (isInRange(row, column)) {
            currentTile = minefield[row][column];

            //Prevent Suicide
            if (currentTile.isMarked()) {
                return true;
            }

            //Game over, if stepped on mine
            if (currentTile.isMined()) {
                this.won = false;
                this.gameOver = true;

                return false;
            }

            currentTile.setRevealed(true);

            for (int i = -1; i <= 1; i++) {
                for (int j = -1; j <= 1; j++) {
                    int xOffset = row + j;
                    int yOffset = column + i;

                    if (!(xOffset == row && yOffset == column)) {
                        if (isInRange(xOffset, yOffset)) {
                            currentTile = minefield[xOffset][yOffset];

                            if (!currentTile.isRevealed() && !currentTile.isMarked()) {
                                if (currentTile.getMinedNeighbours() == 0) {
                                    currentTile.setRevealed(true);

                                    step(xOffset, yOffset);
                                } else if (!currentTile.isMined()) {
                                    currentTile.setRevealed(true);
                                }
                            }
                        }
                    }
                }
            }
        }

        return true;
    }

    
    /**
     * If all mines have been correctly marked and none are incorrectly marked then the return true, otherwise return false.
     * 
     * @return boolean
    */
    public boolean areAllMinesRevealed() {
        for (int i = 0; i < this.rows; i++) {
            for (int j = 0; j < this.columns; j++) {
                if ((minefield[i][j].isMined() && !minefield[i][j].isMarked()) || (!minefield[i][j].isMined() && minefield[i][j].isMarked())) {
                    return false;
                }
            }
        }
        
//        if ((!minefield[i][j].isMarked() && minefield[i][j].isMined()) || (!minefield[i][j].isMined() && !minefield[i][j].isRevealed())) {
//            return false;
//        }

        this.gameOver = true;
        this.won = true;

        return true;
    }

    @Override
    public String toString() {
        return convertToString(this.gameOver);
    }

    public String toStringClearMinefied() {
        return convertToString(true);
    }
    
    private String convertToString(boolean clearMinefied) {
        String minefieldString = "";

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                if (clearMinefied) {
                    minefieldString += minefield[i][j].revealedString(this.gameOver);
                } else {
                    minefieldString += minefield[i][j].toString();
                }
            }

            minefieldString += System.lineSeparator();
        }

        return minefieldString;
    }

    /**
     * Marks a tile if it has not been revealed.
     * 
     * @param row
     * @param column
    */
    public void mark(int row, int column) {
        MineTile currentTile;

        if (isInRange(row, column)) {
            currentTile = minefield[row][column];

            if (!currentTile.isRevealed()) {
                currentTile.toggleMark();
            }
        }
    }

    public MineTile getMineTile(int row, int column) {
        return minefield[row][column];
    }

    /**
     * A boolean value indicating if the coordinates are in range.
     * 
     * @param row
     * @param column
     * @return boolean
    */
    public boolean isInRange(int row, int column) {
        return (row > -1 && row < this.rows && column > -1 && column < this.columns);

//        return (row >= 0 && row < rows && column >= 0 && column < columns);
    }

    public boolean isGameOver() {
        return gameOver;
    }

    public boolean isWon() {
        return won;
    }

    public int getRows() {
        return rows;
    }

    public int getColumns() {
        return columns;
    }
}
