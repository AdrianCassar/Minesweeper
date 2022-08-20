package minesweeperfx;

import java.io.File;
import java.io.Serializable;
public class ApplicationSettings implements Serializable {
    private static final long serialVersionUID = 6751872044706874034L;
    
    private Themes themeType = Themes.DarkTheme;
    private Difficulty difficulty = Difficulty.Expert;
    private BoardSize boardSize = BoardSize.expertEasy;
    private File saveDirectory = new File(System.getProperty("user.dir"));
    private boolean volumeEnabled = true;
    private boolean autoExtention = true;
    private boolean fullscreenMode = true;
    private transient boolean redirectToConsole = false;
    
    public Themes getThemeType() {
        return themeType;
    }

    public void setThemeType(Themes themeType) {
        this.themeType = themeType;
    }

    public void toggleVolume () {
        volumeEnabled = !volumeEnabled;
    }
    
    public boolean isVolumeEnabled() {
        return volumeEnabled;
    }

    public boolean isAutoExtention() {
        return autoExtention;
    }

    public void setAutoExtention(boolean autoExtention) {
        this.autoExtention = autoExtention;
    }

    public Difficulty getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(Difficulty difficulty) {
        this.difficulty = difficulty;
    }

    public BoardSize getBoardSize() {
        return boardSize;
    }

    public void setBoardSize(BoardSize boardSize) {
        this.boardSize = boardSize;
    }

    public File getSaveDirectory() {
        return saveDirectory;
    }

    public void setSaveDirectory(File saveDirectory) {
        this.saveDirectory = saveDirectory;
    }

    public void toggleFullscreenMode() {
        fullscreenMode = !fullscreenMode;
    }
    
    public boolean isFullscreenMode() {
        return fullscreenMode;
    }

    public void setFullscreenMode(boolean FullscreenMode) {
        this.fullscreenMode = FullscreenMode;
    }

    public boolean isRedirectToConsole() {
        return redirectToConsole;
    }

    public void setRedirectToConsole(boolean redirectToConsole) {
        this.redirectToConsole = redirectToConsole;
    }
}
