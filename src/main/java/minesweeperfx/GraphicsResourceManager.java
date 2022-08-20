package minesweeperfx;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public enum GraphicsResourceManager {
    SaveFile(new Image("images/Save_File.png")), ExitIcon(new Image("images/Exit.png")), LoadIcon(new Image("images/File.png")),
    ExitFullscreen(new Image("images/Exit_Fullscreen.png")), Fullscreen(new Image("images/Fullscreen.png")), Trophy(new Image("images/Trophy.png")), 
    SpeakerMuted(new Image("images/Speaker_Muted.png")), Speaker(new Image("images/Speaker.png")), 
    Restart(new Image("images/Restart.png")), Save(new Image("images/Save.png")), LogoBanner(new Image("images/Minesweeper_Logo.png")), 
    AboutIcon(new Image("images/About.png")), ApplicationIcon(new Image("images/Icon.png")), Flag(new Image("images/Mine_Flag.png")), 
    Mine(new Image("images/Mine.png")), IncorrectFlag(new Image("images/Red_X.png"));

    private final Image graphic;

    GraphicsResourceManager(Image graphic) {
        this.graphic = graphic;
    }
    
    public ImageView getGraphicViewer() {
        ImageView imageViewer = getGraphicViewer(43, 46);
        
        return imageViewer;
    }
    
    public ImageView getGraphicViewer(int width, int height) {
        ImageView imageViewer = defaultConfig();
        imageViewer.setFitWidth(width);
        imageViewer.setFitHeight(height);

        return imageViewer;
    }
    
    private ImageView defaultConfig() {
        ImageView imageViewer = new ImageView(graphic);
        imageViewer.setSmooth(true);
        imageViewer.setCache(true);
        
        return imageViewer;
    }
    
    public Image getGraphic() {
        return graphic;
    }
}
