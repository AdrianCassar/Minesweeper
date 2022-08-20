package minesweeperfx.textualMinesweeper;

import java.io.IOException;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;

import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Before;

public class MinesweeperTest {

    private MinefieldConsole minesweeper;

    @Before
    public void setUp() {
        minesweeper = new MinefieldConsole(2, 2);
        minesweeper.setSeed(0);
        minesweeper.populate(1);
    }
    
    //To many arguments passed to the main method.
    @Test
    public void tooManyArguments() throws IOException {
        String simulationOutput = redirectStandardOutput("", new String[]{"2", "2", "1", "0", "0"});

        assertTrue(simulationOutput.contains("Too many paramaters: 5"));
    }

    //Inavlid arguments passed through the main method include; letters and integers <= 2
    @Test
    public void invalidArguments() throws IOException {
        String invalidInputMessage = "{Rows} and {Columns} must be more than or equal to 2." + System.lineSeparator() + System.lineSeparator()
                + "Example: java -jar Minesweeper.jar {Rows} {Columns} {Mines} {Seed}" + System.lineSeparator()
                + "Example: java -jar Minesweeper.jar {Rows} {Columns} {Mines}" + System.lineSeparator()
                + "Example: java -jar Minesweeper.jar {Rows} {Columns}" + System.lineSeparator()
                + "Example: java -jar Minesweeper.jar" + System.lineSeparator();

        String simulationOutput = redirectStandardOutput("", new String[]{"0", "0"});
        String simulationOutputStrings = redirectStandardOutput("", new String[]{"a", "2", "1", "0"});

        assertEquals(simulationOutput, invalidInputMessage);
        assertEquals(simulationOutputStrings, invalidInputMessage);
    }

    //Mark all mines.
    @Test
    public void winScenario() throws IOException {
        String simulateInput = "mark 1 1" + System.lineSeparator() + "quit";

        String simulationOutput = redirectStandardOutput(simulateInput, this.minesweeper);

        assertTrue(simulationOutput.contains("Congratulations You Win"));
    }

    //Step on a mine.
    @Test
    public void loseScenario() throws IOException {
        String simulateInput = "step 1 1" + System.lineSeparator() + "quit";

        String simulationOutput = redirectStandardOutput(simulateInput, this.minesweeper);

        assertTrue(simulationOutput.contains("Game Over, you stepped on a mine!"));
    }

    //If a mine is incorrectly marked and the player then loses, then all incorrectly marked mines will be replaced with X.
    @Test
    public void revealIncorrectlyMarkedMines() throws IOException {
        minesweeper = new MinefieldConsole(2, 2);
        minesweeper.setSeed(0);
        minesweeper.populate(2);
        
        String simulateInput = "mark 0 0" + System.lineSeparator() + "step 1 1" + System.lineSeparator() + "quit";

        String simulationOutput = redirectStandardOutput(simulateInput, this.minesweeper);

        String assertString = "X*" + System.lineSeparator() +
                              "2*" + System.lineSeparator();
        
        assertTrue(simulationOutput.contains(assertString));
    }
    
    //If a mine is marked then you cannot step on it.
    @Test
    public void preventSuicide() throws IOException {
        minesweeper = new MinefieldConsole(2, 2);
        minesweeper.setSeed(0);
        minesweeper.populate(2);

        String simulateInput = "mark 1 1" + System.lineSeparator() + "step 1 1" + System.lineSeparator() + "quit";

        String simulationOutput = redirectStandardOutput(simulateInput, this.minesweeper);

        assertTrue(simulationOutput.contains("Cannot step on a marked mine, suicide prevented."));
    }
    
    //Detecting input of invalid commands
    @Test
    public void invalidCommand() throws IOException {
        String simulateInput = "InvalidCommand" + System.lineSeparator() + "quit";

        String simulationOutput = redirectStandardOutput(simulateInput, this.minesweeper);

        assertTrue(simulationOutput.contains("Command UNKNOWN, row=0, column=0" + System.lineSeparator() + "Unknown command: InvalidCommand"));
    }

    //Method used to rediect all game output into a string for assertion.
    private String redirectStandardOutput(final String simulateInput, Object customMinefield) throws IOException {
        //Checks if customMinefield is a MinefieldConsole or String[].
        if (!(customMinefield instanceof MinefieldConsole || customMinefield instanceof String[])) {
            throw new IllegalArgumentException("Invalid datatype; Minefield and String[] expected.");
        }
        
        //Backup the default Streams
        PrintStream oldPrintStream = System.out;
        InputStream oldInputStream = System.in;

        //Setup InputStream and Output Stream
        try (InputStream inputStream = new ByteArrayInputStream(simulateInput.getBytes());
                OutputStream outputStream = new ByteArrayOutputStream()) {

            System.setIn(inputStream);

            PrintStream printStream = new PrintStream(outputStream);
            System.setOut(printStream);

            //execute the approperate method based on the datatype of customMinefield.
            if (customMinefield instanceof MinefieldConsole)  {
                Minesweeper.main((MinefieldConsole)customMinefield);
            } else {
                Minesweeper.main((String[]) customMinefield);
            }

            //Restore default Streams
            System.setIn(oldInputStream);
            System.setOut(oldPrintStream);

            return outputStream.toString();
        }
    }
}
