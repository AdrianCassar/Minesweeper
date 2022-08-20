package minesweeperfx.textualMinesweeper;

import java.io.ByteArrayInputStream;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javafx.application.Platform;
import minesweeperfx.MinesweeperGUI;

public class Minesweeper {

    private final Parser parser = new Parser();
    private MinefieldConsole minesweeper;

    public Minesweeper(final int rows, final int columns, final int mines, final int seed) {
        minesweeper = new MinefieldConsole(rows, columns);
        minesweeper.setSeed(seed);
        minesweeper.populate(mines);
    }

    private Minesweeper(final int rows, final int columns, final int mines) {
        minesweeper = new MinefieldConsole(rows, columns);
        minesweeper.populate(mines);
    }

    private Minesweeper(final int rows, final int columns) {
        minesweeper = new MinefieldConsole(rows, columns);
        minesweeper.populate(calculateDifficulty(rows, columns));
    }

    //Pass a pre-calculated MinefieldConsole to the Minesweeper class.
    private Minesweeper(MinefieldConsole customMinefield) {
        this.minesweeper = customMinefield;
    }

    //Pass a pre-calculated MinefieldConsole to the Minesweeper class.
    public static void main(MinefieldConsole minesweeper) {
        Minesweeper customMinesweeepr = new Minesweeper(minesweeper);

        customMinesweeepr.commandLine();
    }

    public static void main(String args[]) {
        Minesweeper ct;

        if (validateInput(args)) {
            switch (args.length) {
                case 4:
                    ct = new Minesweeper(Integer.valueOf(args[0]), Integer.valueOf(args[1]), Integer.valueOf(args[2]), Integer.valueOf(args[3]));
                    break;
                case 3:
                    ct = new Minesweeper(Integer.valueOf(args[0]), Integer.valueOf(args[1]), Integer.valueOf(args[2]));
                    break;
                case 2:
                    ct = new Minesweeper(Integer.valueOf(args[0]), Integer.valueOf(args[1]));
                    break;
                default:
                    ct = new Minesweeper(10, 10, 20);
                    break;
            }

            ct.commandLine();
        }
    }

    private static boolean validateInput(String args[]) {
        String invalidInput = "{Rows} and {Columns} must be more than or equal to 2." + System.lineSeparator() + System.lineSeparator()
                + "Example: java -jar Minesweeper.jar {Rows} {Columns} {Mines} {Seed}" + System.lineSeparator()
                + "Example: java -jar Minesweeper.jar {Rows} {Columns} {Mines}" + System.lineSeparator()
                + "Example: java -jar Minesweeper.jar {Rows} {Columns}" + System.lineSeparator()
                + "Example: java -jar Minesweeper.jar";

        //This regular expressions evaludates the input
        String regularExpression = "^(\\[(([2-9])|([0-9][0-9]))\\d*, (([2-9])|([0-9][0-9]))\\d*, [0-9]\\d*, [0-9]\\d*\\])"
                + "|(\\[(([2-9])|([0-9][0-9]))\\d*, (([2-9])|([0-9][0-9]))\\d*, [0-9]\\d*\\])"
                + "|(\\[(([2-9])|([0-9][0-9]))\\d*, (([2-9])|([0-9][0-9]))\\d*\\])"
                + "|(\\[\\])$";

        Pattern regEx = Pattern.compile(regularExpression);
        Matcher match = regEx.matcher(Arrays.toString(args));

        if (!match.matches()) {
            if (args.length > 4) {
                invalidInput = "Too many paramaters: " + args.length + System.lineSeparator() + System.lineSeparator() + invalidInput;
            }

            System.out.println(invalidInput);

            return false;
        }

        return true;
    }

    private void execute(Command c) {
        CommandWord command = c.getCommand();

        int row = c.getRow();
        int column = c.getColumn();

        String message = "";
        String gameOverMSG = System.lineSeparator() + "To start a new game, use the NEW command." + System.lineSeparator() + System.lineSeparator();

        if (command != CommandWord.NEW && !minesweeper.isInRange(row, column)) {
            message = "Invalid Coordinate" + System.lineSeparator();
        }

        switch (command) {
            case STEP:
                if (!minesweeper.isGameOver()) {
                    minesweeper.step(row, column);
                    
                    if (minesweeper.getMineTile(row, column).isMarked()) {
                        message = "Cannot step on a marked mine, suicide prevented." + System.lineSeparator();
                    }
                }
                
                break;
            case MARK:
                if (!minesweeper.isGameOver()) {
                    minesweeper.mark(row, column);
                }
                break;
            case NEW:
                //Cannot create a new game unless the size is <= 2.
                if (row >= 2 && column >= 2) {
                    minesweeper = new MinefieldConsole(row, column);
//                    minesweeper.populate(calculateDifficulty(row, column));
                } else {
                    String invalidInput = "{Rows} and {Columns} must be more than or equal to 2." + System.lineSeparator() + System.lineSeparator();

                    if (minesweeper.isGameOver()) {
                        gameOverMSG += invalidInput;
                    } else {
                        message += invalidInput;
                    }
                }

                break;
//                case GUI:
//                    MinesweeperGUI.main(new String[]{});
//                    break;
        }

        if (minesweeper.isGameOver()) {
            message = minesweeper.isWon() ? "Congratulations You Win" : "Game Over, you stepped on a mine!";
            message += gameOverMSG;
        }

        System.out.println(minesweeper.toString());
        System.out.println(message + c);
        printPrompt(c.getMsg());
    }

    private void commandLine() {
        System.out.println(minesweeper.toString());
        printPrompt("New Game");

        Command c = parser.getCommand();

        while (c.getCommand() != CommandWord.QUIT) {
            printCommand();

//            final Command readOnly = c;
//            
//            Platform.runLater(() -> {
//                execute(readOnly);
//            });
            
            execute(c);

            c = parser.getCommand();
        }

        printCommand();

        System.out.println(c.getMsg());
        
        Platform.exit();
    }

    private void printPrompt(String msg) {
        System.out.println(msg);
        System.out.print(">");
    }

    private int calculateDifficulty(int rows, int columns) {       
//        ~20% of all squares will be mines
//        ((2 * 2) / 100) * 20 = 0.8 = 1
//        (10 * 10) / 100) * 20 = 20

        return (int) Math.ceil((double) (((rows * columns) / 100.0) * 20.0));
    }

    
    /**
     * Print command if System.in is a ByteArrayInputStream thus 
    */
    public void printCommand() {
        if (ByteArrayInputStream.class.equals(System.in.getClass())) {
            System.out.println(parser.getInputLine());
        }
    }
}
