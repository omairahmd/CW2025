package com.comp2042.view;

import com.comp2042.controller.GameInputHandler;
import com.comp2042.events.InputEventListener;
import com.comp2042.events.MoveEvent;
import com.comp2042.events.EventSource;
import com.comp2042.events.EventType;
import com.comp2042.model.DownData;
import com.comp2042.model.ViewData;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Group;
import javafx.scene.effect.Reflection;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.util.Duration;

import java.net.URL;
import java.util.ResourceBundle;

public class GuiController implements Initializable {

    // Block and sizing constants
    /** Size of each brick/block in pixels */
    private static final int BRICK_SIZE = 20;
    
    /** Arc radius for rounded rectangle corners */
    private static final double RECTANGLE_ARC_RADIUS = 9.0;
    
    // Font constants
    /** Font size for loading the digital font */
    private static final double DIGITAL_FONT_SIZE = 38.0;
    
    // Layout offsets
    /** Starting row index for visible board (skips top 2 hidden rows) */
    private static final int BOARD_VISIBLE_START_ROW = 2;
    
    /** Vertical offset for positioning the brick panel relative to game panel */
    private static final double BRICK_PANEL_Y_OFFSET = -42.0;
    
    // Animation timing
    /** Duration in milliseconds for automatic brick drop animation */
    private static final int AUTO_DROP_INTERVAL_MS = 400;
    
    // Visual effects (Reflection)
    /** Fraction of reflection effect (0.0 to 1.0) */
    private static final double REFLECTION_FRACTION = 0.8;
    
    /** Top opacity of reflection effect (0.0 to 1.0) */
    private static final double REFLECTION_TOP_OPACITY = 0.9;
    
    /** Top offset of reflection effect in pixels */
    private static final double REFLECTION_TOP_OFFSET = -12.0;
    
    // Color mapping for brick types
    /** Array mapping color index to Paint color. Index 0 is TRANSPARENT, indices 1-7 are brick colors. */
    private static final Paint[] COLOR_MAP = {
            Color.TRANSPARENT,  // 0
            Color.AQUA,          // 1
            Color.BLUEVIOLET,    // 2
            Color.DARKGREEN,     // 3
            Color.YELLOW,        // 4
            Color.RED,           // 5
            Color.BEIGE,         // 6
            Color.BURLYWOOD      // 7
    };
    
    /** Default color used when color index is out of bounds */
    private static final Paint DEFAULT_COLOR = Color.WHITE;

    @FXML
    private GridPane gamePanel;

    @FXML
    private Group groupNotification;

    @FXML
    private GridPane brickPanel;

    @FXML
    private GameOverPanel gameOverPanel;

    private Rectangle[][] displayMatrix;

    private InputEventListener eventListener;

    private Rectangle[][] rectangles;

    private Timeline timeLine;

    private final BooleanProperty isPause = new SimpleBooleanProperty();

    private final BooleanProperty isGameOver = new SimpleBooleanProperty();

    private GameInputHandler inputHandler;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Font.loadFont(getClass().getClassLoader().getResource("digital.ttf").toExternalForm(), DIGITAL_FONT_SIZE);
        gamePanel.setFocusTraversable(true);
        gamePanel.requestFocus();
        initializeInputHandlers();
        gameOverPanel.setVisible(false);

        final Reflection reflection = new Reflection();
        reflection.setFraction(REFLECTION_FRACTION);
        reflection.setTopOpacity(REFLECTION_TOP_OPACITY);
        reflection.setTopOffset(REFLECTION_TOP_OFFSET);
    }

    /**
     * Initializes keyboard input handlers for game controls.
     * Creates and configures a GameInputHandler to manage all keyboard input.
     */
    private void initializeInputHandlers() {
        inputHandler = new GameInputHandler(
                gamePanel,
                eventListener,
                isPause,
                isGameOver,
                this::refreshBrick,
                this::moveDown,
                () -> newGame(null)
        );
        inputHandler.initialize();
    }

    /**
     * Initializes the game view by setting up the background grid, falling brick, and game loop.
     *
     * @param boardMatrix the game board matrix
     * @param brick the initial brick data
     */
    public void initGameView(int[][] boardMatrix, ViewData brick) {
        initializeBackgroundGrid(boardMatrix);
        initializeFallingBrick(brick);
        setupGameLoop();
    }

    /**
     * Initializes the background grid (displayMatrix) that represents the game board.
     * Creates transparent rectangles for each cell in the visible portion of the board.
     *
     * @param boardMatrix the game board matrix
     */
    private void initializeBackgroundGrid(int[][] boardMatrix) {
        displayMatrix = new Rectangle[boardMatrix.length][boardMatrix[0].length];
        for (int i = BOARD_VISIBLE_START_ROW; i < boardMatrix.length; i++) {
            for (int j = 0; j < boardMatrix[i].length; j++) {
                Rectangle rectangle = new Rectangle(BRICK_SIZE, BRICK_SIZE);
                rectangle.setFill(Color.TRANSPARENT);
                displayMatrix[i][j] = rectangle;
                gamePanel.add(rectangle, j, i - BOARD_VISIBLE_START_ROW);
            }
        }
    }

    /**
     * Initializes the falling brick by creating rectangles for the brick shape
     * and positioning the brick panel at the initial spawn location.
     *
     * @param brick the ViewData containing brick shape and position information
     */
    private void initializeFallingBrick(ViewData brick) {
        rectangles = new Rectangle[brick.getBrickData().length][brick.getBrickData()[0].length];
        for (int i = 0; i < brick.getBrickData().length; i++) {
            for (int j = 0; j < brick.getBrickData()[i].length; j++) {
                Rectangle rectangle = new Rectangle(BRICK_SIZE, BRICK_SIZE);
                rectangle.setFill(getFillColor(brick.getBrickData()[i][j]));
                rectangles[i][j] = rectangle;
                brickPanel.add(rectangle, j, i);
            }
        }
        brickPanel.setLayoutX(gamePanel.getLayoutX() + brick.getxPosition() * brickPanel.getVgap() + brick.getxPosition() * BRICK_SIZE);
        brickPanel.setLayoutY(BRICK_PANEL_Y_OFFSET + gamePanel.getLayoutY() + brick.getyPosition() * brickPanel.getHgap() + brick.getyPosition() * BRICK_SIZE);
    }

    /**
     * Sets up the automatic game loop that moves bricks down at regular intervals.
     * Creates and starts a Timeline that triggers down movement events.
     */
    private void setupGameLoop() {
        timeLine = new Timeline(new KeyFrame(
                Duration.millis(AUTO_DROP_INTERVAL_MS),
                ae -> moveDown(new MoveEvent(EventType.DOWN, EventSource.THREAD))
        ));
        timeLine.setCycleCount(Timeline.INDEFINITE);
        timeLine.play();
    }

    /**
     * Returns the Paint color corresponding to the given color index.
     * 
     * @param colorIndex the color index (0 = TRANSPARENT, 1-7 = brick colors)
     * @return the Paint color for the given index, or DEFAULT_COLOR if index is out of bounds
     */
    private Paint getFillColor(int colorIndex) {
        if (colorIndex >= 0 && colorIndex < COLOR_MAP.length) {
            return COLOR_MAP[colorIndex];
        }
        return DEFAULT_COLOR;
    }


    /**
     * Refreshes the brick display based on the provided ViewData.
     * This method is called by GameInputHandler and must be accessible.
     *
     * @param brick the ViewData containing brick position and shape information
     */
    public void refreshBrick(ViewData brick) {
        if (isPause.getValue() == Boolean.FALSE) {
            brickPanel.setLayoutX(gamePanel.getLayoutX() + brick.getxPosition() * brickPanel.getVgap() + brick.getxPosition() * BRICK_SIZE);
            brickPanel.setLayoutY(BRICK_PANEL_Y_OFFSET + gamePanel.getLayoutY() + brick.getyPosition() * brickPanel.getHgap() + brick.getyPosition() * BRICK_SIZE);
            for (int i = 0; i < brick.getBrickData().length; i++) {
                for (int j = 0; j < brick.getBrickData()[i].length; j++) {
                    setRectangleData(brick.getBrickData()[i][j], rectangles[i][j]);
                }
            }
        }
    }

    public void refreshGameBackground(int[][] board) {
        for (int i = BOARD_VISIBLE_START_ROW; i < board.length; i++) {
            for (int j = 0; j < board[i].length; j++) {
                setRectangleData(board[i][j], displayMatrix[i][j]);
            }
        }
    }

    private void setRectangleData(int color, Rectangle rectangle) {
        rectangle.setFill(getFillColor(color));
        rectangle.setArcHeight(RECTANGLE_ARC_RADIUS);
        rectangle.setArcWidth(RECTANGLE_ARC_RADIUS);
    }

    /**
     * Handles the down movement event.
     * This method is called by GameInputHandler and must be accessible.
     *
     * @param event the MoveEvent representing the down movement
     */
    public void moveDown(MoveEvent event) {
        if (isPause.getValue() == Boolean.FALSE) {
            DownData downData = eventListener.onDownEvent(event);
            if (downData.getClearRow() != null && downData.getClearRow().getLinesRemoved() > 0) {
                NotificationPanel notificationPanel = new NotificationPanel("+" + downData.getClearRow().getScoreBonus());
                groupNotification.getChildren().add(notificationPanel);
                notificationPanel.showScore(groupNotification.getChildren());
            }
            refreshBrick(downData.getViewData());
        }
        gamePanel.requestFocus();
    }

    public void setEventListener(InputEventListener eventListener) {
        this.eventListener = eventListener;
        // Update the event listener in the input handler if it's already initialized
        if (inputHandler != null) {
            inputHandler.setEventListener(eventListener);
        }
    }

    public void bindScore(IntegerProperty integerProperty) {
    }

    public void gameOver() {
        timeLine.stop();
        gameOverPanel.setVisible(true);
        isGameOver.setValue(Boolean.TRUE);
    }

    public void newGame(ActionEvent actionEvent) {
        timeLine.stop();
        gameOverPanel.setVisible(false);
        eventListener.createNewGame();
        gamePanel.requestFocus();
        timeLine.play();
        isPause.setValue(Boolean.FALSE);
        isGameOver.setValue(Boolean.FALSE);
    }

    public void pauseGame(ActionEvent actionEvent) {
        gamePanel.requestFocus();
    }
}

