package minesweeperfx;

import java.io.*;
import java.util.Random;
import javafx.util.Duration;

//import java.nio;
public class Minefield implements Serializable {
    private static final long serialVersionUID = 985078001024069174L;
    
    private boolean ObjectStateSaved = false;
    
    private final MineTileButton[][] minefield;
    private final int rows, columns;
    
    private Difficulty gameDifficulty = Difficulty.Expert;
    private BoardSize gameBoardSize = BoardSize.expertEasy;
    private int flagged = 0;
    private int numMines;

    private transient Integer seed;
    private boolean started = false, gameOver = false, won = false;
    
    private Duration timePlayed = new Duration(0);
    private boolean paused = false;
    
    
    public Minefield(BoardSize gameBoardSize) {
        this(gameBoardSize.getValue()[0], gameBoardSize.getValue()[1]);
    }
    
    /**
     * Create an instance of the minefield class based from the parameters rows and columns.
     * 
     * @param rows
     * @param columns
    */
    public Minefield(final int rows, final int columns) {
        this.rows = rows;
        this.columns = columns;

        this.gameBoardSize = BoardSize.getEnum(rows + " x " + columns);
        this.gameDifficulty = Difficulty.getDifficulty(gameBoardSize);
        
        minefield = new MineTileButton[rows][columns];

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                minefield[i][j] = new MineTileButton() {{}};
            }
        }
        
        setNumberOfMines(Difficulty.getDifficulty(rows, columns));
    }

    private boolean mineTile(int row, int column) {
        MineTileButton currentTile = minefield[row][column];

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

    public void setNumberOfMines(final int numMines) {
        this.numMines = numMines;
    }
    
    private void setNumberOfMines(final Difficulty difficultyType) {
//        this.setGameDifficulty(difficultyType);
        
        this.numMines = difficultyType.getValue();
    }
    
    /**
     * Populate the minefield with a set number of mines. 
     * 
    */
    private void populate(int xCord, int yCord) {
        Random rand = this.seed != null ? new Random(this.seed) : new Random();

        int row, column, i = 0;

        this.numMines = this.numMines >= (this.rows * this.columns) ? (this.rows * this.columns) - 1 : this.numMines;

        while (i < this.numMines) {
            row = rand.nextInt(this.rows);
            column = rand.nextInt(this.columns);

            if (!this.minefield[row][column].isMined() && !(row == xCord && column == yCord)) {
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
    */
    public void step(int row, int column) {
        MineTileButton buttonTile;

        if (isInRange(row, column)) {
            buttonTile = minefield[row][column];

            if (!this.started) {
                populate(row, column);
                
                this.started = !this.started;
            }
            
            //Prevent Suicide
            if (buttonTile.isMarked()) {
                return;
            }

            buttonTile.setRevealed(true);
            
            //Game over, if stepped on mine
            if (buttonTile.isMined()) {
                this.gameOver = true;
                this.won = false;

                return;
            }

            for (int i = -1; i <= 1; i++) {
                for (int j = -1; j <= 1; j++) {
                    int xOffset = row + j;
                    int yOffset = column + i;

                    if (!(xOffset == row && yOffset == column)) {
                        if (isInRange(xOffset, yOffset)) {
                            buttonTile = minefield[xOffset][yOffset];

                            if (!buttonTile.isRevealed() && !buttonTile.isMarked()) {
                                if (buttonTile.getMinedNeighbours() == 0) {
                                    buttonTile.setRevealed(true);

                                    step(xOffset, yOffset);
                                } else if (!buttonTile.isMined()) {
                                    buttonTile.setRevealed(true);
                                }
                            }
                        }
                    }
                }
            }
        }
        
        areAllMinesRevealed();
    }

    public void setFlagged(int flagged) {
        this.flagged = flagged;
    }

    
    /**
     * If all mines have been correctly marked and none are incorrectly marked then the return true, otherwise return false.
     * 
    */
    public void areAllMinesRevealed() {
        for (int i = 0; i < this.rows; i++) {
            for (int j = 0; j < this.columns; j++) {
//                if ((!minefield[i][j].isMined() && minefield[i][j].isMarked() && minefield[i][j].isMined() && !minefield[i][j].isMarked()) || !minefield[i][j].isMined() && !minefield[i][j].isRevealed()) {
//                    return;
//                }
                
                if ((!minefield[i][j].isMined() && minefield[i][j].isMarked())) { //If a non-mine is marked
                    if (minefield[i][j].isMined() && !minefield[i][j].isMarked()) { //If a mine is not marked
                        return;
                    }
                } else if (!minefield[i][j].isMined() && !minefield[i][j].isRevealed()) { //If a non-mine is not revealed
                    return;
                }
            }
        }

        this.gameOver = true;
        this.won = true;
    }

    /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    
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

    /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    
    /**
     * Marks a tile if it has not been revealed.
     * 
     * @param row
     * @param column
    */
    public void mark(int row, int column) {
        MineTileButton buttonTile;

        if (isInRange(row, column)) {
            buttonTile = minefield[row][column];

            if (!buttonTile.isRevealed()) {
                buttonTile.toggleMark();
            }
            
            if (buttonTile.isMarked()) {
                flagged++;
            } else {
                flagged--;
            }
        }
        
        areAllMinesRevealed();
    }

    static Minefield load(File file) throws IOException, ClassNotFoundException {
        if (file.exists() && file.canRead()) {
            return (Minefield)EncryptionManager.decryptObject(file, Minefield.class);
        } else {
            return null;
        }
    }
    
    public void save(File fileToSave) throws IOException {
        if (!fileToSave.getAbsoluteFile().toString().endsWith(".mspr")) {
            fileToSave = new File(fileToSave.getAbsoluteFile().toString() + ".mspr");
        }
        
        if (fileToSave.isDirectory()) {
            return;
        }
        
        if (!fileToSave.exists()) {
            fileToSave.createNewFile();
        }
        
        toggleObjectStateSaved();
        
        EncryptionManager.encryptionFile(fileToSave, this);
        
        toggleObjectStateSaved();
        
//        if (fileToSave.canRead() && fileToSave.canWrite()) {
//            try(final ObjectOutputStream os = new ObjectOutputStream(new FileOutputStream(fileToSave))) {
//                toggleObjectStateSaved();
//                
//                os.writeObject(this);
//                
//                toggleObjectStateSaved();
//            }
//        }
    }
    
    public boolean isInRange(int row, int column) {
        return (row > -1 && row < this.rows && column > -1 && column < this.columns);

//        return (row >= 0 && row < rows && column >= 0 && column < columns);
    }
    
    public MineTileButton getMineTile(int row, int column) {
        return minefield[row][column];
    }
    
    public int getFlagged() {
        return flagged;
    }
    
    private void toggleObjectStateSaved() {
        this.ObjectStateSaved = !this.ObjectStateSaved;
    }

    public boolean isObjectStateSaved() {
        return ObjectStateSaved;
    }

    public void setObjectStateSaved(boolean ObjectStateSaved) {
        this.ObjectStateSaved = ObjectStateSaved;
    }

    public void incrementTimePlayed() {
        timePlayed = timePlayed.add(new Duration(1000));
    }
    
    public Duration getTimePlayed() {
        return timePlayed;
    }

    public boolean isPaused() {
        return paused;
    }
    
    public void togglePause() {
        this.paused = !this.paused;
    }

    public Difficulty getGameDifficulty() {
        return gameDifficulty;
    }

//    public void setGameDifficulty(Difficulty gameDifficulty) {
//        this.gameDifficulty = gameDifficulty;
//    }

    public BoardSize getGameBoardSize() {
        return gameBoardSize;
    }

//    public void setGameBoardSize(BoardSize gameBoardSize) {
//        this.gameBoardSize = gameBoardSize;
//    }

    public boolean isStarted() {
        return started;
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

