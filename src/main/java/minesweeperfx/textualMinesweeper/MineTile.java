package minesweeperfx.textualMinesweeper;

public class MineTile {

    private boolean mined;
    private boolean revealed;
    private boolean marked;
    private int minedNeighbours;

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
        this.revealed = revealed;
    }
    
    public void toggleMark() {
        this.marked = !this.marked;
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
}
