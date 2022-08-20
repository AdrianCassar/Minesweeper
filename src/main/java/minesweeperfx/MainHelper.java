package minesweeperfx;

import javafx.application.Application;
import minesweeperfx.textualMinesweeper.Minesweeper;

public class MainHelper {
    public static void main(String[] args) {
        if (args.length > 0) {
            if (args[0].equalsIgnoreCase("gui")) {
                Application.launch(MinesweeperGUI.class);
            }
        } else if (System.console() == null) {
            Application.launch(MinesweeperGUI.class);
        } else {
            Minesweeper.main(args);
        }
    }
}
