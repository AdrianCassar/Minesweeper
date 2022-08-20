package minesweeperfx.textualMinesweeper;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

public class MinefieldTest {

    private MinefieldConsole minesweeper;
    private int numMines;

    @Before
    public void setUp() {
        minesweeper = new MinefieldConsole(3, 3);
        minesweeper.setSeed(0);
        this.numMines = 3;

        minesweeper.populate(this.numMines);
    }

    @Test
    public void testStep() {
        String preCalculatedMinefield = "1*2" + System.lineSeparator()
                + "13*" + System.lineSeparator()
                + "02*" + System.lineSeparator();

        assertEquals(preCalculatedMinefield, minesweeper.toStringClearMinefied());
    }

    @Test
    public void testMark() {
        MineTile currentTile = minesweeper.getMineTile(0, 0);

        currentTile.toggleMark();
        assertTrue(currentTile.isMarked());
    }

    @Test
    public void testPopulate() {
        char[] minefieldChars = minesweeper.toStringClearMinefied().toCharArray();
        int mineCount = 0;

        for (int i = 0; i < minefieldChars.length; i++) {
            if (minefieldChars[i] == '*') {
                mineCount++;
            }
        }

        assertTrue(this.numMines == mineCount);
    }

    @Test
    public void testAreAllMinesRevealed() {
        markAllMines();

        assertTrue(minesweeper.areAllMinesRevealed());
    }

    @Test
    public void loseGame() {
        //step on a mine
        minesweeper.step(0, 1);

        assertTrue(!minesweeper.areAllMinesRevealed() && minesweeper.isGameOver() && !minesweeper.isWon());
    }

    @Test
    public void winGame() {
        markAllMines();

        assertTrue(minesweeper.areAllMinesRevealed() && minesweeper.isGameOver() && minesweeper.isWon());
    }

    private void markAllMines() {
        for (int i = 0; i < minesweeper.getRows(); i++) {
            for (int j = 0; j < minesweeper.getColumns(); j++) {
                if (minesweeper.getMineTile(i, j).isMined()) {
                    minesweeper.mark(i, j);
                }
            }
        }
    }
}
