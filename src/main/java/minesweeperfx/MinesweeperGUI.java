package minesweeperfx;

import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.input.*;
import javafx.scene.layout.*;
import javafx.scene.image.*;
import javafx.scene.control.Alert.*;

import java.io.*;
import java.util.*;
import javafx.geometry.*;
import javafx.stage.*;

import java.awt.Desktop;
import java.awt.image.RenderedImage;
import javafx.embed.swing.SwingFXUtils;

import java.time.LocalDateTime;
import java.util.logging.Level;
import java.util.logging.Logger;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javax.imageio.ImageIO;

//import minesweepermvc.textualMinesweeper.Parser;
//import minesweepermvc.textualMinesweeper.Command;
//import minesweepermvc.textualMinesweeper.CommandWord;
//import javafx.event.Event;

//import javafx.collections.ObservableList;

public class MinesweeperGUI extends Application {
    
    private ApplicationSettings appSettings;
    private Minefield minesweeper;
//    private Parser parser = new Parser();
//    private Thread consoleThread;
    
    private BorderPane baseControl = new BorderPane();
    private ComboBox<String> comboBoxSize;
    private ComboBox<Difficulty> comboBoxDifficulties;

    private Label currentPosition = new Label("Position: X: 1, Y: 1");
    private Label displayMines = new Label();
    private Label displayFlagged = new Label();
    
    private Timer timer;
    private Label timeLabel = new Label("00:00:00");

    private Stage aboutWindow = new Stage();
    private Stage primaryStage;

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;

        try {
            appSettings = loadAppSettings();
//            System.out.println(ObjectStreamClass.lookup(appSettings.getClass()).getSerialVersionUID());
        } catch (IOException | ClassNotFoundException ex) {
            Logger.getLogger(MinesweeperGUI.class.getName()).log(Level.SEVERE, null, ex);

            appSettings = new ApplicationSettings();
        }

        initialiseAboutStage();

        BorderPane root = new BorderPane();

//        root.setStyle("-fx-border-color: blue;-fx-border-width: 5px;");
        root.setTop(createMenuBar());

        Screen screen = Screen.getPrimary();
        Rectangle2D bounds = screen.getVisualBounds();

        Scene scene = new Scene(root, bounds.getWidth(), bounds.getHeight());

        root.setCenter(initialiseGUI(scene));

        scene.getStylesheets().add(appSettings.getThemeType().getThemeURL());

        primaryStage.setOnCloseRequest(e -> {
            e.consume();
            closeProgram();
        });

        primaryStage.getIcons().add(GraphicsResourceManager.ApplicationIcon.getGraphic());
        primaryStage.setTitle("Minesweeepr");
        primaryStage.setMaximized(true);
//        primaryStage.sizeToScene();
        primaryStage.setScene(scene);

        primaryStage.setOnShown(e -> {
            toggleFullscreenImage();

//            primaryStage.setMaxWidth(bounds.getWidth());
//            primaryStage.setMaxHeight(bounds.getHeight());

            ((StackPane) baseControl.getBottom()).getChildren().forEach(event -> {
                if (event instanceof ComboBox) {
                    ((ComboBox) event).getSelectionModel().select(appSettings.getThemeType());
                }
            });

            comboBoxDifficulties.getSelectionModel().select(appSettings.getDifficulty());
            comboBoxSize.getSelectionModel().select(appSettings.getBoardSize().toString());
            
//            ((StackPane) baseControl.getTop()).getChildren().forEach(event -> {
//                if (event instanceof VBox) {
//                    try {
//                        ((ComboBox<Difficulty>) ((VBox) event).getChildren().get(0)).getSelectionModel().select(appSettings.getDifficulty());
//                    } catch (Exception ex) {
//
//                    }
//                }
//            });
        });

        primaryStage.show();
        

    }
    
    private void initialiseAboutStage() {
        GridPane aboutLayout = new GridPane();

        //        aboutLayout.setGridLinesVisible(true);
        ImageView imageViewer = GraphicsResourceManager.LogoBanner.getGraphicViewer();
        imageViewer.setFitWidth(320);
        imageViewer.setFitHeight(0);
        imageViewer.setPreserveRatio(true);

        Label developer = new Label("Developed by Adrian Cassar");
        Label school = new Label("University of Sussex");
        Label version = new Label("Version: 1.0");

        Button closeButton = new Button();
        closeButton.setCursor(Cursor.HAND);
        closeButton.setText("Close");
        closeButton.setMinWidth(150);
        closeButton.setMaxWidth(150);
        closeButton.setOnAction((ActionEvent e) -> aboutWindow.close());

        aboutLayout.add(imageViewer, 0, 0);
        GridPane.setValignment(imageViewer, VPos.TOP);

        for (int i = 0; i <= 3; i++) {
            RowConstraints con = new RowConstraints();
            con.setVgrow(Priority.ALWAYS);
            aboutLayout.getRowConstraints().add(con);
        }

        aboutLayout.add(developer, 0, 1);
        aboutLayout.add(school, 0, 2);
        aboutLayout.add(version, 0, 3);
        aboutLayout.add(closeButton, 0, 4);

        GridPane.setHalignment(developer, HPos.CENTER);
        GridPane.setHalignment(school, HPos.CENTER);
        GridPane.setHalignment(version, HPos.CENTER);

        GridPane.setHalignment(closeButton, HPos.CENTER);
        GridPane.setMargin(closeButton, new Insets(0, 1, 1, 0));

        aboutLayout.setAlignment(Pos.TOP_LEFT);

        Scene aboutScene = new Scene(aboutLayout, 310, 280);
        aboutScene.getStylesheets().add(appSettings.getThemeType().getThemeURL());

        aboutWindow.getIcons().add(GraphicsResourceManager.AboutIcon.getGraphic());
        aboutWindow.setTitle("About");
        aboutWindow.initOwner(primaryStage);
        aboutWindow.initModality(Modality.APPLICATION_MODAL);
        aboutWindow.setResizable(false);

        aboutWindow.setScene(aboutScene);
    }

    public Stage getAboutWindow() {
        return aboutWindow;
    }

    private void closeProgram() {
        if (minesweeper.isStarted() && !minesweeper.isGameOver()) {
            saveDialog("Save Game?", "Save Manager", ".mspr");
        }

        stopTimer();

        try {
            saveAppSettings();
        } catch (IOException ex) {
            Logger.getLogger(MinesweeperGUI.class.getName()).log(Level.SEVERE, null, ex);
        }

        Platform.exit();
    }

    private Node initialiseGUI(Scene scene) {
//        baseControl.setStyle("-fx-border-color: purple;-fx-border-width: 5px;");
//        Insets margin = new Insets(5, 0, 0, 0);
//        ComboBox<Difficulty> comboBoxDifficulties = new ComboBox();
        comboBoxDifficulties = new ComboBox();
        comboBoxDifficulties.setCursor(Cursor.HAND);
        comboBoxDifficulties.setPrefWidth(150);

        comboBoxSize = new ComboBox();
        comboBoxSize.setTooltip(new Tooltip("Minesweeper Board Sizes"));
        comboBoxSize.setCursor(Cursor.HAND);
        comboBoxSize.setPrefWidth(150);

        comboBoxDifficulties.getItems().addAll(Difficulty.values());
        comboBoxDifficulties.setTooltip(new Tooltip("Difficulties"));
        comboBoxDifficulties.getSelectionModel().select(appSettings.getDifficulty());

        //Call method directly, events will not trigger unless stage is shown
        correspondingSize(appSettings.getDifficulty());

        comboBoxDifficulties.setOnAction((ActionEvent e) -> {
            appSettings.setDifficulty(comboBoxDifficulties.getSelectionModel().getSelectedItem());
            correspondingSize(comboBoxDifficulties.getSelectionModel().getSelectedItem());
        });

        ImageView fullscreen = new ImageView(toggleFullscreenImage());
        fullscreen.setFitWidth(35);
        fullscreen.setFitHeight(35);
        fullscreen.setPickOnBounds(true);
        fullscreen.setCursor(Cursor.HAND);
        Tooltip.install(fullscreen, new Tooltip(appSettings.isFullscreenMode() ? "Exit Fullscreen" : "Fullscreen"));

        fullscreen.setOnMouseClicked(e -> {
            appSettings.toggleFullscreenMode();

            if (appSettings.isVolumeEnabled()) {
                AudioClipResourceManager.Click.playAudioClip();
            }

            Tooltip.install(fullscreen, new Tooltip(appSettings.isFullscreenMode() ? "Exit Fullscreen" : "Fullscreen"));

            fullscreen.setImage(toggleFullscreenImage());
        });

        ImageView speaker = appSettings.isVolumeEnabled() ? 
                GraphicsResourceManager.Speaker.getGraphicViewer(40, 40) 
                : 
                GraphicsResourceManager.SpeakerMuted.getGraphicViewer(40, 40);
        
        speaker.setPickOnBounds(true);
        speaker.setCursor(Cursor.HAND);

        AudioClipResourceManager.setVolume(appSettings.isVolumeEnabled() ? 1 : -80);

        speaker.setOnMouseClicked(e -> {
            AudioClipResourceManager.Click.playAudioClip();

            speaker.setImage(toggleVolume());
        });

        Button restartGame = new Button("Restart Game");
        restartGame.setOnAction(e -> restartGame());

        ComboBox<Themes> toggleTheme = new ComboBox();
        toggleTheme.getItems().addAll(Themes.values());
        toggleTheme.getSelectionModel().select(appSettings.getThemeType());

        toggleTheme.setOnAction(e -> {
            scene.getStylesheets().clear();

            scene.getStylesheets().add(toggleTheme.getSelectionModel().getSelectedItem().getThemeURL());

            appSettings.setThemeType(toggleTheme.getSelectionModel().getSelectedItem());
        });

        Button saveAsImage = new Button("Save As PNG");

        saveAsImage.setOnAction(e -> {
            saveDialog("Save Image?", "Save Manager", ".png");
        });

        FXCollections.observableArrayList(toggleTheme, restartGame, saveAsImage).forEach(e -> {
            e.setCursor(Cursor.HAND);
            e.setMaxSize(200, 40);
            e.setMinSize(200, 40);
        });

        VBox comboBoxes = new VBox(10);
        comboBoxes.getChildren().addAll(comboBoxDifficulties, comboBoxSize);

        VBox images = new VBox(10);
        images.getChildren().addAll(fullscreen, speaker);
        images.setAlignment(Pos.TOP_RIGHT);

        HBox settingsLayout = new HBox(10);
        settingsLayout.setAlignment(Pos.TOP_RIGHT);
        settingsLayout.getChildren().addAll(comboBoxes, images);

        VBox labels = new VBox(10);

        displayMines.setText("Total Mines: " + appSettings.getDifficulty().getValue());
        
        resetFlagged();
        
        labels.getChildren().addAll(currentPosition, displayFlagged, displayMines);
        labels.setId("position");

        BorderPane minefieldGUI = (BorderPane) generateMinefield(getBoardSize()[0], getBoardSize()[1]);

        StackPane centre = new StackPane();

        centre.getChildren().add(minefieldGUI);
        baseControl.setCenter(minefieldGUI);

        StackPane top = new StackPane();

        top.getChildren().addAll(timeLabel, labels, settingsLayout);
        timeLabel.setId("timer");
        StackPane.setAlignment(timeLabel, Pos.CENTER);
        StackPane.setAlignment(settingsLayout, Pos.TOP_RIGHT);
        top.setStyle("-fx-border-color: black;-fx-border-width: 2 0 2 0;");

        StackPane bottom = new StackPane();

        bottom.setMaxHeight(80);
        bottom.setMinHeight(80);
        bottom.setStyle("-fx-border-color: black;-fx-border-width: 2 0 2 0;");

        HBox layoutButtons = new HBox();
        layoutButtons.getChildren().addAll(toggleTheme, restartGame, saveAsImage);

        Insets buttonMargin = new Insets(0, 10, 0, 10);
        HBox.setMargin(toggleTheme, buttonMargin);
        HBox.setMargin(restartGame, buttonMargin);
        HBox.setMargin(saveAsImage, buttonMargin);

        layoutButtons.setAlignment(Pos.CENTER);
        bottom.getChildren().addAll(layoutButtons);

        baseControl.setTop(top);
        baseControl.setBottom(bottom);

        comboBoxSize.setOnAction((ActionEvent e) -> {
            if ((!minesweeper.isStarted() || minesweeper.isGameOver()) && !comboBoxSize.getItems().isEmpty()) {
                appSettings.setBoardSize(BoardSize.getEnum(comboBoxSize.getSelectionModel().getSelectedItem()));

                baseControl.setCenter(generateMinefield(getBoardSize()[0], getBoardSize()[1])
                );

//                minesweeper.setGameBoardSize(getBoardSize());
//                baseControl.setCenter(generateMinefield(minesweeper.getGameBoardSize()));
//                ScrollPane minefieldGUITest = (ScrollPane) generateMinefield(getBoardSize()[0], getBoardSize()[1]);
//
//                for (Node node : root.getChildren()) {
//                    int columIndex = (int) Math.ceil(root.getColumnConstraints().size() / 2);
//                    int rowIndex = (int) Math.ceil(root.getRowConstraints().size() / 2);
//
//                    if (node instanceof ScrollPane) {
//                        root.getChildren().remove(node);
//                        root.add(minefieldGUITest, (columIndex), rowIndex);
//                        return;
//                    }
//                }
//                root.getChildren().remove(minefieldGUI);
//                root.add(minefieldGUITest, (int)(root.getColumnConstraints().size() / 2), (int)(root.getRowConstraints().size() / 2));
            }
        });

//        int size[] = BoardSize.getEnum(comboBoxSize.getSelectionModel().getSelectedItem()).getValue();
//        baseControl.setCenter(generateMinefield(size[0], size[1]));
        return baseControl;
    }

    private void resetFlagged() {
        if (minesweeper != null) {
            minesweeper.setFlagged(0);
            displayFlagged.setText("Total Flagged: " + minesweeper.getFlagged());
        } else {
            displayFlagged.setText("Total Flagged: 0");
        }
    }
    
    private void saveImage(File fileToSave) throws SecurityException, IOException {
        Image minefildSnapshot = getMinefieldSnapshot();
        RenderedImage renderedImage = SwingFXUtils.fromFXImage(minefildSnapshot, null);

        ImageIO.write(renderedImage, "png", fileToSave);
    }

    private Image toggleVolume() {
        appSettings.toggleVolume();

        AudioClipResourceManager.setVolume(appSettings.isVolumeEnabled() ? 1 : -80);

        if (appSettings.isVolumeEnabled()) {
            return GraphicsResourceManager.Speaker.getGraphicViewer(50, 50).getImage();
        } else {
            return GraphicsResourceManager.SpeakerMuted.getGraphicViewer(50, 50).getImage();
        }
    }

    private Image toggleFullscreenImage() {
        if (appSettings.isFullscreenMode()) {
            baseControl.setMaxWidth(primaryStage.getWidth());
            baseControl.setMaxHeight(primaryStage.getHeight());

            baseControl.setStyle("");

            return GraphicsResourceManager.ExitFullscreen.getGraphic();
        } else {
//            ScrollPane centre = (ScrollPane) ((BorderPane) baseControl.getCenter()).getCenter();

            baseControl.setMaxWidth(362);
            baseControl.setMaxHeight(362);
//            baseControl.setMaxHeight(362);
            baseControl.setStyle("-fx-border-color: black;-fx-border-width: 0 2 0 2;");

            return GraphicsResourceManager.Fullscreen.getGraphic();
        }
    }

    private Image getMinefieldSnapshot() {
//        TilePane centre = (TilePane)((ScrollPane)((BorderPane)baseControl.getCenter()).getCenter()).getContent();
//        params.setViewport(new Rectangle2D(centre.localToScene(centre.getBoundsInLocal()).getMinX() - 1, centre.localToScene(centre.getBoundsInLocal()).getMinY(), centre.localToScene(centre.getBoundsInLocal()).getWidth() + 2, centre.localToScene(centre.getBoundsInLocal()).getHeight()));

        SnapshotParameters params = new SnapshotParameters();
        ScrollPane centre = (ScrollPane) ((BorderPane) baseControl.getCenter()).getCenter();
//        TilePane centre = (TilePane) ((ScrollPane) ((BorderPane) baseControl.getCenter()).getCenter()).getContent();

        Rectangle2D viewpoint = new Rectangle2D(centre.localToScene(centre.getBoundsInLocal()).getMinX(),
                centre.localToScene(centre.getBoundsInLocal()).getMinY(),
                centre.localToScene(centre.getBoundsInLocal()).getWidth(),
                centre.localToScene(centre.getBoundsInLocal()).getHeight());

        params.setViewport(viewpoint);

        return baseControl.snapshot(params, null);
    }

    private ApplicationSettings loadAppSettings() throws IOException, ClassNotFoundException {
        File settingsFolder = new File(System.getenv("LOCALAPPDATA") + "\\Minesweeper");
        File settingsFile = new File(settingsFolder.getAbsolutePath() + "\\settings.dat");

        if (!settingsFolder.exists() || !settingsFile.exists()) {
            appSettings = new ApplicationSettings();

            saveAppSettings();
        }

        if (settingsFile.exists() && settingsFile.canRead()) {
            Object appSettingsObject = EncryptionManager.decryptObject(settingsFile, ApplicationSettings.class);

            if (appSettingsObject instanceof ApplicationSettings) {
                return (ApplicationSettings) appSettingsObject;
            }
        }

        return null;
    }

    private void saveAppSettings() throws IOException {
        File settingsFolder = new File(System.getenv("LOCALAPPDATA") + "\\Minesweeper");
        File settingsFile = new File(settingsFolder.getAbsolutePath() + "\\settings.dat");

        if (!settingsFolder.exists()) {
            settingsFolder.mkdir();
            settingsFile.createNewFile();
        } else if (!settingsFile.exists()) {
            settingsFile.createNewFile();
        }

        if (settingsFile.canRead() && settingsFile.canWrite()) {
            EncryptionManager.encryptionFile(settingsFile, this.appSettings);
        }
    }

//    private TilePane customMinefield() {
//        Label numRows = new Label("Rows:");
//        TextField rows = new TextField();
//        rows.setStyle("-fx-font-size: 14px;");
//        rows.setMinWidth(100);
//        rows.setMaxWidth(100);
//
//        Label numColumns = new Label("Columns:");
//        TextField columns = new TextField();
//        columns.setStyle("-fx-font-size: 14px;");
//        columns.setMinWidth(100);
//        columns.setMaxWidth(100);
//
//        Label numMines = new Label("Total Mines:");
//        TextField mines = new TextField();
//        mines.setStyle("-fx-font-size: 14px;");
//        mines.setMinWidth(100);
//        mines.setMaxWidth(100);
//
//        TilePane customMinefield = new TilePane(Orientation.HORIZONTAL);
//        customMinefield.setPrefColumns(2);
//        customMinefield.setAlignment(Pos.CENTER);
//        customMinefield.getChildren().addAll(numRows, rows, numColumns, columns, numMines, mines);
//
//        return customMinefield;
//    }
    
//    private BoardSize getBoardSize() {
//        return BoardSize.getEnum(comboBoxSize.getSelectionModel().getSelectedItem());
//    }
    private int[] getBoardSize() {
        return BoardSize.getEnum(comboBoxSize.getSelectionModel().getSelectedItem()).getValue();
    }

    public void correspondingSize(Difficulty difficultyType) {
        comboBoxSize.getItems().removeAll(comboBoxSize.getItems());

        comboBoxSize.getItems().addAll(BoardSize.getCollection(difficultyType));
        comboBoxSize.getSelectionModel().selectFirst();
    }

    public Node generateMinefield(BoardSize gameBoardSize) {
        minesweeper = new Minefield(gameBoardSize);

        return generateMinefield(minesweeper);
    }

    public Node generateMinefield(int rows, int columns) {
        minesweeper = new Minefield(rows, columns);

        return generateMinefield(minesweeper);
    }

    public Node generateMinefield(Minefield minesweeper) {
        this.minesweeper = minesweeper;

        TilePane minefieldGUI = new TilePane(Orientation.VERTICAL);
        ScrollPane scrollPane = new ScrollPane(minefieldGUI);
        BorderPane minefieldContainer = new BorderPane(scrollPane);

//        minefieldGUI.setStyle("-fx-border-color: green;-fx-border-width: 5px;");
//        scrollPane.setStyle("-fx-border-color: black;-fx-border-width: 5px;");
//        minefieldContainer.setStyle("-fx-border-color: red;-fx-border-width: 5px;");
        minefieldGUI.setPrefTileWidth(45);
        minefieldGUI.setPrefTileHeight(45);

        minefieldGUI.setPrefRows(minesweeper.getRows());
        minefieldGUI.setPrefColumns(minesweeper.getColumns());

        for (int y = 0; y < minesweeper.getColumns(); y++) {
            for (int x = 0; x < minesweeper.getRows(); x++) {
                final int xCord = x;
                final int yCord = y;

                MineTileButton buttonTile = minesweeper.getMineTile(xCord, yCord);

                if (minesweeper.isObjectStateSaved()) {
                    int size = 45;
                    buttonTile.setPrefSize(size, size);
                    buttonTile.setMinSize(size, size);
                    buttonTile.setAlignment(Pos.CENTER);
                    buttonTile.getStyleClass().add("mine-tile-button");

                    if (buttonTile.isRevealed()) {
                        buttonTile.setRevealed(true);
                    } else {
                        if (buttonTile.isMarked()) {
                            buttonTile.addMarkImage();
                        } else {
                            buttonTile.setCursor(Cursor.HAND);
                        }

                        attachEvents(buttonTile, xCord, yCord);
                    }
                    
                    attachHintEvent(buttonTile, xCord, yCord);

                } else {
                    attachEvents(buttonTile, xCord, yCord);
                }

                minefieldGUI.getChildren().add(buttonTile);
            }
        }


        int verticalBarSize = 2;

        int width = (int) (minefieldGUI.getPrefColumns() * minefieldGUI.getPrefTileWidth() + 2 + verticalBarSize);// + 21; //(int)(verticalBar.getWidth())
        int height = (int) (minefieldGUI.getPrefRows() * minefieldGUI.getPrefTileHeight() + 2 + verticalBarSize);

        scrollPane.setMaxSize(width, height);
        scrollPane.setMinSize(width, height);

//        Set<Node> nodes = scrollPane.lookupAll(".scroll-bar");
//
//        ScrollBar horizontalBar;
//        ScrollBar verticalBar = null;
//        
//        for (Node node : nodes) {
//            if (node instanceof ScrollBar) {
//                ScrollBar scrollBar = (ScrollBar) node;
//                
//                if (scrollBar.getOrientation() == Orientation.HORIZONTAL) {
//                    horizontalBar = scrollBar;
//                } else if (scrollBar.getOrientation() == Orientation.VERTICAL) {
//                    verticalBar = scrollBar;
//                }
//            }
//        }
        
//        if (appSettings.isFullscreenMode() && width > primaryStage.getWidth() || height > primaryStage.getHeight()) {
////            baseControl.setMaxWidth(width);
////            baseControl.setMaxHeight(height);
//            
////            baseControl.setMaxWidth(Screen.getPrimary().getBounds().getWidth());
////            baseControl.setMaxHeight(Screen.getPrimary().getBounds().getHeight());
//            
//            baseControl.setStyle("-fx-border-color: black;-fx-border-width: 0 2 0 2;");
//        } else {
////            scrollPane.setMaxSize(width, height);
////            scrollPane.setMinSize(width, height);
//        }
//        
////        if (!appSettings.isFullscreenMode()) {
//
////        }
        if (minesweeper.isObjectStateSaved()) {
//            BoardSize boardSize = BoardSize.getEnum(minesweeper.getRows() + " x " + minesweeper.getColumns());
//            Difficulty difficultyType = Difficulty.getDifficulty(boardSize.getValue()[0], boardSize.getValue()[1]);
//
//            correspondingSize(difficultyType);
//            comboBoxSize.getSelectionModel().select(boardSize.toString());
//            comboBoxDifficulties.getSelectionModel().select(difficultyType);

            correspondingSize(minesweeper.getGameDifficulty());
            comboBoxSize.getSelectionModel().select(minesweeper.getGameBoardSize().toString());
            comboBoxDifficulties.getSelectionModel().select(minesweeper.getGameDifficulty());
            
            displayFlagged.setText("Total Flagged: " + minesweeper.getFlagged());
            
            timeLabel.setText(formatTime((int) minesweeper.getTimePlayed().toSeconds()));
            
            minesweeper.areAllMinesRevealed();

            if (!minesweeper.isGameOver()) {
                startTimer();
            }
            
            minesweeper.setObjectStateSaved(false);
        }

        displayMines.setText("Total Mines: " + comboBoxDifficulties.getSelectionModel().getSelectedItem().getValue());
        
        return minefieldContainer;
    }

    private void attachEvents(MineTileButton buttonTile, int xCord, int yCord) {
        buttonTile.setOnAction(e -> onAction(xCord, yCord));
        buttonTile.setOnMousePressed(e -> onPressed(buttonTile));
        buttonTile.setOnMouseReleased(e -> onReleased(buttonTile));
        buttonTile.setOnMouseClicked(e -> onRightClick(e, xCord, yCord));
        
        attachHintEvent(buttonTile, xCord, yCord);
    }

    private void attachHintEvent(MineTileButton buttonTile, int xCord, int yCord) {
        buttonTile.setOnMouseEntered(e -> onEnter(xCord, yCord));
        buttonTile.setOnMouseExited(e -> highlightNeighbours(false, xCord, yCord));
    }
    
    private void onPressed(MineTileButton buttonTile) {
        if (!buttonTile.isRevealed() && !buttonTile.isMarked()) {
            buttonTile.setId("mine-tile-button-pressed");
        }
    }

    private void onReleased(MineTileButton buttonTile) {
        if (!buttonTile.isRevealed() && !buttonTile.isMarked()) {
            buttonTile.setId(null);
        }
    }

    private void onEnter(int xCord, int yCord) {
        MineTileButton mineTileButton = minesweeper.getMineTile(xCord, yCord);

        if (!minesweeper.getMineTile(xCord, yCord).isRevealed()) {
            String position = "Position: " + "X:" + (xCord + 1) + " " + "Y:" + (yCord + 1);

            currentPosition.setText(position);
        }

        if (!mineTileButton.isMarked() && !mineTileButton.isRevealed()) {
            AudioClipResourceManager.Hover.playAudioClip();
        }

        if (!mineTileButton.isMarked()) {
            highlightNeighbours(true, xCord, yCord);
        }
    }

    private void onAction(int xCord, int yCord) {
        MineTileButton mineTileButton = minesweeper.getMineTile(xCord, yCord);

        if (mineTileButton.isMarked()) {
            return;
        }

        if (!minesweeper.isStarted()) {
            startTimer();
            
            if (minesweeper.getFlagged() > 0) {
                for (int i = 0; i < minesweeper.getRows(); i++) {
                    for (int j = 0; j < minesweeper.getColumns(); j++) {
                        MineTileButton buttonTile = minesweeper.getMineTile(i, j);

                        if (buttonTile.isMarked()) {
                            buttonTile.toggleMark();
                        }
                    }
                }

                resetFlagged();
            }
        }
        
//        minesweeper = new Minefield(10, 10);
        if (!mineTileButton.isMined()) { // && !mineTileButton.isRevealed()
            AudioClipResourceManager.Step.playAudioClip();
        }

        minesweeper.step(xCord, yCord);

//        printMinefield();

        if (mineTileButton.getId().equals("mine-tile-button-pressed")) {
            mineTileButton.setId(null);
        }

        highlightNeighbours(false, xCord, yCord);

        if (!mineTileButton.isMarked()) {
            highlightNeighbours(true, xCord, yCord);
        }

        if (minesweeper.isGameOver()) {
            highlightNeighbours(false, xCord, yCord);

            gameOver();
        }
    }

    private void onRightClick(MouseEvent event, int xCord, int yCord) {
        MineTileButton buttonTile = minesweeper.getMineTile(xCord, yCord);

        if ((event.getButton() == MouseButton.SECONDARY || event.getButton() == MouseButton.MIDDLE) && !buttonTile.isRevealed()) {
            AudioClipResourceManager.Flag.playAudioClip();
            
            minesweeper.mark(xCord, yCord);

//            printMinefield();

            if (buttonTile.isMarked()) {
                highlightNeighbours(false, xCord, yCord);
            } else {
                highlightNeighbours(true, xCord, yCord);
            }
        }

        displayFlagged.setText("Total Flagged: " + minesweeper.getFlagged());
        
        if (minesweeper.isGameOver()) {
            gameOver();
        }
    }

    private void startTimer() {
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                if (minesweeper.isGameOver()) {
                    timer.cancel();
                } else {
                    minesweeper.incrementTimePlayed();

                    Platform.runLater(() -> {
                        timeLabel.setText(formatTime((int) minesweeper.getTimePlayed().toSeconds()));
                    });
                }
            }
        };

        timer = new Timer();
        timer.scheduleAtFixedRate(timerTask, 1000, 1000);
    }

    private void stopTimer() {
        if (timer != null) {
            if (minesweeper.isGameOver() || minesweeper.isStarted()) {
                timer.cancel();

                timer.purge();
            }

            timer = null;
        }
    }
    
    private String formatTime(int totalSeconds) {
        int seconds = (totalSeconds % 60);
        int minutes = (totalSeconds % 3600) / 60;
        int hours = totalSeconds / 3600;

        return String.format("%02d:%02d:%02d", hours, minutes, seconds);
    }

    private void highlightNeighbours(boolean highlight, int xCord, int yCord) {
        for (int x = -1; x <= 1; x++) {
            for (int y = -1; y <= 1; y++) {
                int xOffset = xCord + x;
                int yOffset = yCord + y;

                if (!(xOffset == xCord && yOffset == yCord)) {
                    if (minesweeper.isInRange(xOffset, yOffset)) {
                        MineTileButton buttonTile = minesweeper.getMineTile(xOffset, yOffset);

                        if (highlight && !buttonTile.isRevealed() && !buttonTile.isMarked()) {
                            buttonTile.getStyleClass().add("highlightTile");
                        } else {
                            buttonTile.getStyleClass().remove("highlightTile");
                        }
                    }
                }
            }
        }
    }

    private MenuBar createMenuBar() {
        Menu fileMenu = new Menu("File");
        Menu helpMenu = new Menu("Help");

        MenuItem about = new MenuItem("About");

        about.setGraphic(GraphicsResourceManager.AboutIcon.getGraphicViewer(20, 20));

        about.setOnAction(e -> aboutWindow.show());

        helpMenu.getItems().addAll(about);

        MenuItem fileItems[] = new MenuItem[]{
            new MenuItem("Save"),
            new MenuItem("Load"),
            new MenuItem("Save Settings"),
            new MenuItem("Restart Game"),
            new MenuItem("Exit")
        };

        int size = 25;

        fileItems[0].setGraphic(GraphicsResourceManager.SaveFile.getGraphicViewer(size, size));
        fileItems[1].setGraphic(GraphicsResourceManager.LoadIcon.getGraphicViewer(size, size));
        fileItems[2].setGraphic(GraphicsResourceManager.SaveFile.getGraphicViewer(size, size));
        fileItems[3].setGraphic(GraphicsResourceManager.Restart.getGraphicViewer(size, size));
        fileItems[4].setGraphic(GraphicsResourceManager.ExitIcon.getGraphicViewer(size, size));

        fileItems[0].setOnAction(e -> {
            saveDialog("Save Game?", "Save Manager", ".mspr");
        });

        fileItems[1].setOnAction(e -> loadGame());

        fileItems[2].setOnAction(e -> saveSettings());

        fileItems[3].setOnAction(e -> restartGame());
        fileItems[4].setOnAction(e -> closeProgram());

        fileMenu.getItems().addAll(fileItems);
        MenuBar menuBar = new MenuBar(fileMenu, helpMenu);

        return menuBar;
    }

    private void loadGame() {
        File gameStateData = chooseFile(primaryStage);

        if (gameStateData != null) {
            try {
                Minefield minefieldObject = Minefield.load(gameStateData);

                System.out.println("Sucessfully Loaded");

                stopTimer();

                baseControl.setCenter(generateMinefield(minefieldObject));
            } catch (IOException | ClassNotFoundException ex) {
                System.out.println("Corrupt File");

                Dialog dialog = dialogConfig(AlertType.INFORMATION, null);
                dialog.setHeaderText("Corrupted Save Game");

                Label message = new Label("The file " + gameStateData.getName() + " is corrupted.");
                message.setPrefWidth(380);
                message.setAlignment(Pos.CENTER);
                message.setPadding(new Insets(5, 5, 5, 5));
                message.setWrapText(true);
                dialog.getDialogPane().setContent(message);

                dialog.showAndWait();
            }
        }
    }
    
    private void saveSettings() {
        try {
            saveAppSettings();

            Dialog dialog = dialogConfig(AlertType.INFORMATION, null);
            dialog.setHeaderText("Settings Saved");

            File settingsFolder = new File(System.getenv("LOCALAPPDATA") + "\\Minesweeper");
            File settingsFile = new File(settingsFolder.getAbsolutePath() + "\\settings.dat");

            VBox dialogRoot = new VBox(10);

            Label lblSavedAt = new Label("Saved at");
            Hyperlink savedLocation = new Hyperlink(settingsFile.getAbsolutePath());

            Label lblmsg = new Label("Settings are automatically saved when the program closes." + System.lineSeparator()
                    + System.lineSeparator() + "Delete this file to reset settings to their default.");

            savedLocation.setOnAction(event -> {
                try {
                    Desktop.getDesktop().open(settingsFolder);
                } catch (IOException ex) {
                    Logger.getLogger(MinesweeperGUI.class.getName()).log(Level.SEVERE, null, ex);
                }
            });

            savedLocation.setAlignment(Pos.CENTER_LEFT);
            savedLocation.setPrefWidth(350);

            dialogRoot.getChildren().addAll(lblSavedAt, savedLocation, lblmsg);

            dialog.getDialogPane().setContent(dialogRoot);

            dialog.showAndWait();
        } catch (IOException ex) {
        }
    }
    
    private File chooseFile(Stage stage) {
        FileChooser fileChooser = new FileChooser();

        fileChooser.setInitialDirectory(new File(System.getProperty("user.dir")));

        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Minesweeepr Files", "*.mspr")
        );

        return fileChooser.showOpenDialog(stage);
    }

    private void saveDialog(String titleText, String headerText, String extentionType) {
        Dialog alert = dialogConfig(Alert.AlertType.CONFIRMATION, GraphicsResourceManager.Save.getGraphicViewer());
        alert.setTitle(titleText);
        alert.setHeaderText(headerText);

        alert.getDialogPane().getButtonTypes().clear();

        ButtonType buttonSave = new ButtonType("Save");
        ButtonType buttonCancel = new ButtonType("Cancel");

        alert.getDialogPane().getButtonTypes().addAll(buttonSave, buttonCancel);

        alert.getDialogPane().setContent(saveVBoxPane(alert, extentionType));

        Optional<ButtonType> result = alert.showAndWait();

        VBox dialogRoot = (VBox) alert.getDialogPane().getContent();

        File fileToSave = new File("");

        for (Node node : dialogRoot.getChildren()) {
            if (node instanceof TextField) {
                String textContent = ((TextField) node).getText();
                String saveDirectory = appSettings.getSaveDirectory().getAbsolutePath();

                if (!saveDirectory.equals(textContent)) {
                    fileToSave = new File(saveDirectory + "\\" + textContent);
                }
            }
        }

        if (result.isPresent() && result.get() == buttonSave) {
            if (appSettings.getSaveDirectory().canWrite()) {
                try {
                    switch (extentionType) {
                        case ".mspr":
                            minesweeper.save(fileToSave);
                            break;
                        case ".png":
                            saveImage(fileToSave);
                            break;
                        default:
                            savingErrorDialog(null);
                            break;
                    }
                } catch (IOException ex) {
                    savingErrorDialog(null);
                }
            } else {
                savingErrorDialog("Access is denied.");
            }
        }
    }

    private void savingErrorDialog(String reason) {
        Dialog dialog = dialogConfig(AlertType.INFORMATION, null);
        dialog.setHeaderText("Cannot save, an error occured");

        Label message;

        if (reason != null) {
            message = new Label(reason);
        } else {
            message = new Label("Try saving the file is a different directory or restart.");
        }

        message.setAlignment(Pos.CENTER);
        message.setPadding(new Insets(5, 5, 5, 5));
        message.setWrapText(true);
        dialog.getDialogPane().setContent(message);

        dialog.getDialogPane().setPrefWidth(400);

        dialog.showAndWait();
    }

    private VBox saveVBoxPane(Dialog alert, String extentionType) {
        VBox dialogRoot = new VBox(10);

        Label descriptionContent = new Label("");

        Label warningLabel = new Label("");
        warningLabel.setId("warning");

        Label fileLabel = new Label("File Name");
        TextField fileName = new TextField();
        fileName.setPrefWidth(200);

        CheckBox autoAddExtention = new CheckBox();
        autoAddExtention.setText("Auto Extention");
        autoAddExtention.setTooltip(new Tooltip("Automatically adds file extention if not present."));

        Label fileDirectoryLabel = new Label("Save Directory");
        TextField saveDirectory = new TextField();
        saveDirectory.setPrefWidth(200);
        saveDirectory.setEditable(false);
        saveDirectory.setFocusTraversable(false);
//        saveDirectory.setMouseTransparent(true);

        Button browseDirectory = new Button("Browse Directory");
        browseDirectory.setCursor(Cursor.HAND);

        browseDirectory.setOnAction(event -> {
            DirectoryChooser directoryChooser = new DirectoryChooser();
            directoryChooser.setInitialDirectory(new File(saveDirectory.getText()));

            File directory = directoryChooser.showDialog(primaryStage);

            if (directory != null) {
                saveDirectory.setText(directory.getAbsolutePath());
            }
        });

        dialogRoot.getChildren().addAll(fileDirectoryLabel, saveDirectory, browseDirectory, fileLabel, fileName, autoAddExtention);

        saveDirectory.setText(appSettings.getSaveDirectory().getAbsolutePath());

        File defaultFile = new File("Minesweeper - " + LocalDateTime.now().toLocalDate().toString());
        fileName.setText(defaultFile.getName());

        autoAddExtention.selectedProperty().addListener(e -> addExtention(autoAddExtention.isSelected(), fileName, extentionType));
        autoAddExtention.setSelected(appSettings.isAutoExtention());

        saveDirectory.textProperty().addListener(e -> {
            appSettings.setSaveDirectory(new File(saveDirectory.getText()));

            configureDialog(new File(saveDirectory.getText() + "\\" + fileName.getText()), warningLabel, descriptionContent, extentionType, alert, dialogRoot);
        });

        fileName.textProperty().addListener(e -> {
            addExtention(autoAddExtention.isSelected(), fileName, extentionType);

            configureDialog(new File(saveDirectory.getText() + "\\" + fileName.getText()), warningLabel, descriptionContent, extentionType, alert, dialogRoot);
        });

        configureDialog(new File(saveDirectory.getText() + "\\" + fileName.getText()), warningLabel, descriptionContent, extentionType, alert, dialogRoot);

        return dialogRoot;
    }

    private void addExtention(boolean selected, TextField fileName, String extentionType) {
        appSettings.setAutoExtention(selected);

        if (selected) {
            if (!fileName.getText().endsWith(extentionType)) {
                fileName.setText(fileName.getText() + extentionType);
            }
        } else {
            if (fileName.getText().endsWith(extentionType)) {
                fileName.setText(fileName.getText().substring(0, fileName.getText().length() - extentionType.length()));
            }
        }
    }

    private void configureDialog(File file, Label warningLabel, Label descriptionContent, String extentionType, Dialog alert, VBox dialogRoot) {
//        VBox dialogRoot = (VBox) alert.getDialogPane().getContent();
        ButtonType buttonSave = alert.getDialogPane().getButtonTypes().get(0);

        if (extentionType.equals(".mspr")) {
            if (minesweeper.isStarted() && !minesweeper.isGameOver() && !dialogRoot.getChildren().contains(descriptionContent)) {
                descriptionContent.setText("Game is not finished, do you want to save before existing?");

                dialogRoot.getChildren().add(0, descriptionContent);
            }
        } else if (extentionType.equals(".png")) {
            if (dialogRoot.getChildren().contains(descriptionContent)) {
                dialogRoot.getChildren().remove(descriptionContent);
            }
        }

        if (!file.getAbsoluteFile().toString().endsWith(extentionType)) {
            file = new File(file.getAbsoluteFile().toString() + extentionType);
        }

        if (!file.isDirectory() && file.exists()) {
            warningLabel.setText("Warning: " + (file.getName().length() >= 28 ? file.getName().substring(0, 28) + "..." : file.getName()) + " already exists.");

            if (!dialogRoot.getChildren().contains(warningLabel)) {
                dialogRoot.getChildren().add(dialogRoot.getChildren().contains(descriptionContent) ? 1 : 0, warningLabel);
            }

            ((Button) alert.getDialogPane().lookupButton(buttonSave)).setText("Overwrite");
        } else if (((Button) alert.getDialogPane().lookupButton(buttonSave)).getText().equals("Overwrite")) {
            dialogRoot.getChildren().remove(warningLabel);

            ((Button) alert.getDialogPane().lookupButton(buttonSave)).setText("Save");
        }

//        alert.getDialogPane().getScene().getWindow().sizeToScene();
        if (dialogRoot.getChildren().containsAll(FXCollections.observableArrayList(descriptionContent, warningLabel))) {
            if (descriptionContent.getPrefWidth() > warningLabel.getPrefWidth()) {
                alert.getDialogPane().setPrefWidth(descriptionContent.getPrefWidth());
            } else {
                alert.getDialogPane().setPrefWidth(warningLabel.getPrefWidth());
            }
        } else {
            if (dialogRoot.getChildren().contains(descriptionContent)) {
                alert.getDialogPane().setPrefWidth(descriptionContent.getPrefWidth());
            } else if (dialogRoot.getChildren().contains(warningLabel)) {
                alert.getDialogPane().setPrefWidth(warningLabel.getPrefWidth());
            } else {
                alert.getDialogPane().setPrefWidth(350);
            }
        }

//        dialogRoot.setPrefWidth(VBox.USE_COMPUTED_SIZE);
    }

    private void gameOver() {
        for (int x = 0; x < minesweeper.getRows(); x++) {
            for (int y = 0; y < minesweeper.getColumns(); y++) {
                minesweeper.getMineTile(x, y).setGameOverReveal(true, minesweeper.isWon());
            }
        }

        Dialog window = dialogConfig(null, null);

        window.getDialogPane().getButtonTypes().clear();

        ButtonType buttonRestart = new ButtonType("Restart Game");
        ButtonType buttonViewMineField = new ButtonType("View Minefield");

        window.getDialogPane().getButtonTypes().addAll(buttonRestart, buttonViewMineField);

        VBox report = new VBox(10);
        report.setAlignment(Pos.CENTER);

        Label lblTitle = new Label("Game Summary");
        lblTitle.setId("gameSummaryTitle");

//        ((StackPane) baseControl.getTop()).getChildren().forEach(event -> {
//            if (event instanceof VBox) {
//                try {
//                    currentDifficulty = ((ComboBox<Difficulty>) ((VBox) event).getChildren().get(0)).getSelectionModel().getSelectedItem();
//                } catch (Exception ex) {
//
//                }
//            }
//        });

        Label lblTimePlayed = new Label("Time Played: " + timeLabel.getText());
        Label lblDifficulty = new Label("Difficulty: " + comboBoxDifficulties.getSelectionModel().getSelectedItem().name());
        Label lblBoardSize = new Label("Board Size: " + comboBoxSize.getSelectionModel().getSelectedItem());
        Label lblFlagged = new Label("Flags Placed: " + minesweeper.getFlagged());
        Label lblNumBombs = new Label("Bombs: " + comboBoxDifficulties.getSelectionModel().getSelectedItem().getValue());

        report.getChildren().addAll(lblTitle, lblTimePlayed, lblDifficulty, lblBoardSize, lblFlagged, lblNumBombs);

        window.getDialogPane().setContent(report);

        if (minesweeper.isWon()) {
            window.setTitle("You Won");
            window.setHeaderText("Congratulations you won");
            window.setGraphic(GraphicsResourceManager.Trophy.getGraphicViewer());

            AudioClipResourceManager.Win.playAudioClip();
        } else {
            window.setTitle("You Lost");
            window.setHeaderText("Game over, you stepped on a mine.");

            AudioClipResourceManager.Boom.playAudioClip();
        }

//        window.show();
//        
        Optional<ButtonType> result = window.showAndWait();

        if (result.isPresent()) {
            if (result.get() == buttonRestart) {
                perfromRestart();
            }
        }
    }

    private void restartGame() {
        if (minesweeper.isGameOver()) {
            perfromRestart();
        } else {
            Dialog alert = dialogConfig(Alert.AlertType.CONFIRMATION, GraphicsResourceManager.Restart.getGraphicViewer(50, 50));

            if (!minesweeper.isStarted()) {
                alert.setHeaderText("Please start a game.");
                alert.setContentText("Click on a square to start the game.");
            } else {
                alert.setHeaderText("Restart Game?");
                alert.setContentText("Are you sure you want to restart?");
            }

            Optional<ButtonType> result = alert.showAndWait();

            if (result.isPresent() && result.get() == ButtonType.OK) {
                perfromRestart();
            }
        }
    }

    private Dialog dialogConfig(Alert.AlertType alertType, ImageView graphicViewer) {
        Dialog alert = new Alert(alertType);
        alert.initStyle(StageStyle.UNDECORATED);
        alert.initOwner(primaryStage);
        alert.getDialogPane().getStylesheets().add(appSettings.getThemeType().getThemeURL());
        alert.setGraphic(graphicViewer);

        return alert;
    }

    private void perfromRestart() {
        resetFlagged();
        
        timeLabel.setText("00:00:00");
        stopTimer();

        baseControl.setCenter(generateMinefield(getBoardSize()[0], getBoardSize()[1]));
    }

//    private void printMinefield() {
//        if (appSettings.isRedirectToConsole() && minesweeper.isStarted()) {
//            System.out.println(minesweeper);
//
//            if (minesweeper.isGameOver()) {
//                if (minesweeper.isWon()) {
//                    System.out.println("Congratulations you won");
//                } else {
//                    System.out.println("Game over, you stepped on a mine.");
//                }
//            }
//        }
//    }

    
    
    
    
    
    
//    private void playCMDAndGUI() {
//        Parameters param = this.getParameters();
//
//        if (System.console() == null) {
//            primaryStage.show();
//
////            startConsoleThread();
//        } else {
//            if (param.getRaw().contains("gui")) {
//                primaryStage.show();
//            }
//
//            startConsoleThread();
//        }
//    }
//    
//    private void startConsoleThread() {
//        Runnable task = () -> {
//
//            System.out.println(minesweeper.toString());
//            printPrompt("New Game");
//
//            Command c = parser.getCommand();
//
//            while (c.getCommand() != CommandWord.QUIT) {
//                final Command cc = c;
//
//                Platform.runLater(() -> {
//                    execute(cc);
//                });
//
//                c = parser.getCommand();
//            }
//
//            System.out.println(c.getMsg());
//
//            Platform.runLater(() -> {
//                closeProgram();
//            });
//        };
//
//        consoleThread = new Thread(task);
//
//        consoleThread.start();
//    }
//
//    private void closeConsoleThread() {
//        if (consoleThread.isAlive()) {
//            if (minesweeper.isGameOver() || minesweeper.isStarted()) {
//
//            }
//
//            consoleThread = null;
//        }
//    }
//    
//    private void execute(Command c) {
//        CommandWord command = c.getCommand();
//
//        int row = c.getRow();
//        int column = c.getColumn();
//
//        String message = "";
//        String gameOverMSG = System.lineSeparator() + "To start a new game, use the NEW command." + System.lineSeparator() + System.lineSeparator();
//
//        if (command != CommandWord.NEW && !minesweeper.isInRange(row, column)) {
//            message = "Invalid Coordinate" + System.lineSeparator();
//        }
//
//        MineTileButton buttonTile = minesweeper.getMineTile(row, column);
//
//        switch (command) {
//            case STEP:
//                if (!minesweeper.isGameOver()) {
//                    //disable hover style
//                    //disable hits
//
//                    onEnter(row, column);
//                    onPressed(buttonTile);
//                    onAction(row, column);
//                    onReleased(buttonTile);
//                    highlightNeighbours(false, row, column);
//
////                    minesweeper.step(row, column);
//                    if (minesweeper.getMineTile(row, column).isMarked()) {
//                        message = "Cannot step on a marked mine, suicide prevented." + System.lineSeparator();
//                    }
//                }
//
//                break;
//            case MARK:
//                if (!minesweeper.isGameOver()) {
//                    MouseEvent event = new MouseEvent(MouseEvent.MOUSE_CLICKED, 0, 0, 0, 0, MouseButton.SECONDARY, 1, false, false,
//                            false, false, false, false, false, false, false, false, null);
//
//                    onEnter(row, column);
//                    onPressed(buttonTile);
//
//                    Event.fireEvent(buttonTile, event);
//
//                    onReleased(buttonTile);
//                    highlightNeighbours(false, row, column);
//
////                    onRightClick(event, row, column);
////                    minesweeper.mark(row, column);
//                }
//                break;
//            case NEW:
//                //Cannot create a new game unless the size is <= 2.
//                if (row >= 2 && column >= 2) {
//                    minesweeper = new Minefield(row, column);
////                    minesweeper.populate(calculateDifficulty(row, column));
//                } else {
//                    String invalidInput = "{Rows} and {Columns} must be more than or equal to 2." + System.lineSeparator() + System.lineSeparator();
//
//                    if (minesweeper.isGameOver()) {
//                        gameOverMSG += invalidInput;
//                    } else {
//                        message += invalidInput;
//                    }
//                }
//
//                break;
//            case GUI:
//                primaryStage.show();
//                break;
//            case HIDEGUI:
//                primaryStage.hide();
//                break;
//        }
//
//        if (minesweeper.isGameOver()) {
//            message = minesweeper.isWon() ? "Congratulations You Win" : "Game Over, you stepped on a mine!";
//            message += gameOverMSG;
//        }
//
//        System.out.println(minesweeper.toString());
//        System.out.println(message + c);
//        printPrompt(c.getMsg());
//    }
//
//    private void printPrompt(String msg) {
//        System.out.println(msg);
//        System.out.print(">");
//    }
}
