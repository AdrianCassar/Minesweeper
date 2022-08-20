package minesweeperfx;

import java.io.Serializable;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.control.Button;

public class MineTileButton extends Button implements Serializable {
    private static final long serialVersionUID = 5835324539612743186L;

    private boolean mined;
    private boolean revealed;
    private boolean marked;
    private int minedNeighbours;
//    transient MineTileButton resumeState;

    public MineTileButton() {
        final int size = 45;

        super.setPrefSize(size, size);
        super.setMinSize(size, size);
//        this.setMaxSize(size, size);
        this.setCursor(Cursor.HAND);
        this.setAlignment(Pos.CENTER);

        this.getStyleClass().add("mine-tile-button");
    }

    public boolean isMined() {
        return mined;
    }

    public boolean isRevealed() {
        return revealed;
    }

    public boolean isMarked() {
        return marked;
    }

    public int getMinedNeighbours() {
        return minedNeighbours;
    }

    public void updateNeighbours() {
        this.minedNeighbours++;
    }

    public void setMined(final boolean mined) {
        this.mined = mined;
    }

    public void setRevealed(final boolean revealed) {
        setTextColour(getMinedNeighbours());

        if (isMined()) {
            addMineImage();
        } else if (getMinedNeighbours() == 0) {
            if (isMarked()) {
                setGraphic(null);
            }

            setOnMouseEntered(null);
            setOnMouseExited(null);

            setDisable(true);
        } else {
            setText(String.valueOf(this.getMinedNeighbours()));
        }

        this.setCursor(Cursor.DEFAULT);
        this.setOnAction(null);
        this.setOnMousePressed(null);
        this.setOnMouseReleased(null);
        this.setOnMouseClicked(null);

        this.revealed = revealed;
    }

    public void setGameOverReveal(final boolean revealed, final boolean won) {
        setTextColour(this.getMinedNeighbours());

        if (isMined() && !isMarked()) {
            if (won) {
                addMarkImage();
            } else {
                addMineImage();
            }
        } else if (!isMined() && isMarked()) {
            addIncorrectFlagImage();
        } else if (getMinedNeighbours() == 0) {
            if (isMarked()) {
                this.setGraphic(null);
            }

            this.setDisable(true);
        } else if (!isMined() && !isMarked()) {
            this.setText(String.valueOf(getMinedNeighbours()));
        }

        if (getMinedNeighbours() != 0) {
            this.setOnMouseEntered(null);
            this.setOnMouseExited(null);
        }

        if (!isRevealed()) {
            this.setCursor(Cursor.DEFAULT);
            this.setOnAction(null);
            this.setOnMousePressed(null);
            this.setOnMouseReleased(null);
            this.setOnMouseClicked(null);

            this.revealed = revealed;
        }
    }

    public void setTextColour(int minedNeighbours) {
        switch (minedNeighbours) {
            case 0:
                this.setId("button-disabled");
                break;
            case 1:
                this.setStyle("-fx-text-fill: DarkGreen");
                break;
            case 2:
                this.setStyle("-fx-text-fill: DarkOrange");
                break;
            case 3:
                this.setId("three");
                break;
            case 4:
                this.setId("four");
                break;
            case 5:
                this.setId("five");
                break;
            case 6:
                this.setStyle("-fx-text-fill: DarkCyan");
                break;
            case 7:
                this.setStyle("-fx-text-fill: Pink");
                break;
            case 8:
                this.setStyle("-fx-text-fill: Red");
                break;
            default:
                this.setStyle("-fx-text-fill: Black");
                break;
        }
    }

    @Override
    public String toString() {
        if (isMarked()) {
            return "?";
        } else if (isMined() && isRevealed()) {
            return "*";
        } else if (isRevealed()) {
            return String.valueOf(getMinedNeighbours());
        } else {
            return ".";
        }

        //■ ▣
        //✖
    }

    public String revealedString(final boolean gameOver) {
        if (gameOver) {
            if (isMarked() && isMined()) {
                return "?";
            } else if (isMined() && !isMarked()) {
                return "*";
            } else if (!isMined() && !isMarked()) {
                return String.valueOf(getMinedNeighbours());
            } else { //(!isMined() && isMarked())
                return "X";
            }
            
//            return isMarked() && isMined() ? "?" : isMined() && !isMarked() ? "*" : String.valueOf(getMinedNeighbours());
//            return isMarked() && isMined() ? "?" : isMined() && !isMarked() ? "*" : !isMined() && !isMarked() ? String.valueOf(getMinedNeighbours()) : "X";
        } else {
            return isMined() ? "*" : String.valueOf(getMinedNeighbours());
        }
    }
    
    public void toggleMark() {
        if (this.marked) {
            this.setCursor(Cursor.HAND);
            this.setGraphic(null);
        } else {
            this.setCursor(Cursor.DEFAULT);
            addMarkImage();
        }

        this.marked = !this.marked;
    }

    public void setMarked(boolean marked) {
        this.marked = marked;
    }

    public void addMarkImage() {
        this.setGraphic(GraphicsResourceManager.Flag.getGraphicViewer());
    }

    private void addMineImage() {
        this.setGraphic(GraphicsResourceManager.Mine.getGraphicViewer());
    }

    private void addIncorrectFlagImage() {
        this.setGraphic(GraphicsResourceManager.IncorrectFlag.getGraphicViewer());
    }
}
