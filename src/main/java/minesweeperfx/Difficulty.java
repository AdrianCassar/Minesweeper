package minesweeperfx;
public enum Difficulty {
//    , Custom(new Integer(0))
    Beginner(new Integer(10)), Intermediate(new Integer(40)), Expert(new Integer(99));

    private Integer numMines = null;
    private Integer customNumMines = null;

    private Difficulty(Integer numMines) {
        this.numMines = numMines;
    }
    
    public void SetCustomValue(int customValue) {
        numMines = null;
        
        this.customNumMines = customValue;
    }
    
    public int getValue() {
        if (this.numMines != null) {
            return this.numMines;
        } else {
             return this.customNumMines;
        }
    }
    
    public static Difficulty getDifficulty(BoardSize boardSize) {
        return getDifficulty(boardSize.getValue()[0], boardSize.getValue()[1]);
    }
    
    public static Difficulty getDifficulty(final int rows, final int columns) {
        int totalSquares = rows * columns;
        
        if (totalSquares >= 64 && totalSquares <= 100) {
            return Difficulty.Beginner;
        } else if (totalSquares <= 256) {
            return Difficulty.Intermediate;
        } else { //if (totalSquares <= 480)
            return Difficulty.Expert;
        }
        
//        if (totalSquares >= 64 && totalSquares <= 100) {
//            return Difficulty.Beginner;
//        } else if (totalSquares <= 256) {
//            return Difficulty.Intermediate;
//        } else if (totalSquares <= 480) {
//            return Difficulty.Expert;
//        } else {
//            return Difficulty.Custom;
//        }
        
        
        
        //Else 20% are mines
    }
}
