package com.comp2042.view;

import com.comp2042.controller.GameInputHandler;
import com.comp2042.events.InputEventListener;
import com.comp2042.events.MoveEvent;
import com.comp2042.events.EventSource;
import com.comp2042.events.EventType;
import com.comp2042.logic.bricks.Brick;
import com.comp2042.manager.SoundManager;
import com.comp2042.model.Board;
import com.comp2042.model.DownData;
import com.comp2042.model.GameMode;
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
import javafx.scene.control.Label;
import javafx.beans.value.ChangeListener;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.geometry.Pos;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.util.Duration;
import javafx.application.Platform;

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
    
    /** Pixel offset adjustment for ghost piece Y position (fine-tune in pixels) */
    private static final double GHOST_Y_PIXEL_ADJUSTMENT = 10.5; // Half a row offset (half of 21px = 20px brick + 1px gap)
    
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
    /** Array mapping color index to Paint color. Index 0 is TRANSPARENT, indices 1-7 are brick colors, index 8 is vine color. */
    private static final Paint[] COLOR_MAP = {
            Color.TRANSPARENT,  // 0
            Color.AQUA,          // 1
            Color.BLUEVIOLET,    // 2
            Color.DARKGREEN,     // 3
            Color.YELLOW,        // 4
            Color.RED,           // 5
            Color.BEIGE,         // 6
            Color.BURLYWOOD,     // 7
            Color.web("#228B22") // 8 - Forest Green for vines
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
    private GridPane ghostPanel; // Ghost piece panel

    @FXML
    private BorderPane gameBoard;
    
    @FXML
    private Pane rootPane; // The root Pane from FXML
    
    @FXML
    private Pane gridOverlay; // Overlay pane for grid lines

    @FXML
    private GameOverPanel gameOverPanel;
    
    @FXML
    private Label scoreLabel;
    
    @FXML
    private Label highScoreLabel;
    
    @FXML
    private VBox scorePanel;
    
    @FXML
    private VBox scoreContainer;
    
    @FXML
    private Label levelLabel;
    
    @FXML
    private StackPane pausePanelContainer;
    
    @FXML
    private VBox nextBricksPanel;
    
    @FXML
    private VBox nextBricksContainer;

    private Rectangle[][] displayMatrix;
    
    private Board board; // Reference to board for getting next bricks

    private InputEventListener eventListener;
    
    private com.comp2042.util.HighScoreManager highScoreManager;
    
    private com.comp2042.controller.GameController gameController; // Reference to GameController for overgrowth timer

    private Rectangle[][] rectangles;
    
    private Rectangle[][] ghostRectangles; // Ghost piece rectangles

    private Timeline timeLine;

    private final BooleanProperty isPause = new SimpleBooleanProperty();

    private final BooleanProperty isGameOver = new SimpleBooleanProperty();

    private GameInputHandler inputHandler;
    
    private PausePanel pausePanel;
    private SettingsPanel settingsPanel;
    private javafx.stage.Stage primaryStage;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Font.loadFont(getClass().getClassLoader().getResource("digital.ttf").toExternalForm(), DIGITAL_FONT_SIZE);
        gamePanel.setFocusTraversable(true);
        gamePanel.requestFocus();
        initializeInputHandlers();
        gameOverPanel.setVisible(false);
        
        // Set up game over panel button actions
        gameOverPanel.setOnNewGame(() -> newGame(null));
        gameOverPanel.setOnMainMenu(() -> returnToMainMenu());
        
        // Initialize high score manager and load high score
        highScoreManager = new com.comp2042.util.HighScoreManager();
        updateHighScoreDisplay();

        final Reflection reflection = new Reflection();
        reflection.setFraction(REFLECTION_FRACTION);
        reflection.setTopOpacity(REFLECTION_TOP_OPACITY);
        reflection.setTopOffset(REFLECTION_TOP_OFFSET);
        
        // Center the game board horizontally
        centerGameBoard();
        
        // Center the game over panel horizontally
        centerGameOverPanel();
        
        // Add listener to recenter when window is resized
        setupResizeListener();
        
        // Initialize pause panel
        initializePausePanel();
        
        // FIX: Dynamic Layout Binding - Bind side panels to game board position
        Platform.runLater(() -> {
            if (gameBoard != null) {
                double boardWidth = 10 * BRICK_SIZE + 9 * 1 + 12 * 2; // ~233px
                double spacing = 10.0; // Gap between board and panels
                
                // 1. Bind Next Bricks Panel to the RIGHT of the board
                if (nextBricksPanel != null) {
                    nextBricksPanel.layoutXProperty().bind(
                        gameBoard.layoutXProperty().add(boardWidth).add(spacing)
                    );
                }
                
                // 2. Bind Score Panel to the LEFT of the board
                if (scorePanel != null) {
                    // Calculate: BoardX - PanelWidth - Spacing
                    // Use smaller spacing (5px) to fit in tight window
                    scorePanel.layoutXProperty().bind(
                        gameBoard.layoutXProperty().subtract(scorePanel.widthProperty()).subtract(5.0)
                    );
                }
            }
        });
        
        // Create grid overlay for game board (after scene is ready)
        Platform.runLater(() -> createGridOverlay());
    }
    
    /**
     * Sets up a listener to recenter elements when the window is resized.
     */
    private void setupResizeListener() {
        if (rootPane != null) {
            // Get the scene and listen for width changes
            rootPane.sceneProperty().addListener((obs, oldScene, newScene) -> {
                if (newScene != null) {
                    // Listen to scene width changes
                    newScene.widthProperty().addListener((widthObs, oldWidth, newWidth) -> {
                        centerGameBoard();
                        centerGameOverPanel();
                        // Panels are now bound to game board, no manual positioning needed
                    });
                    // Initial centering
                    Platform.runLater(() -> {
                        centerGameBoard();
                        centerGameOverPanel();
                        // Panels are now bound to game board, no manual positioning needed
                    });
                }
            });
            
            // If scene already exists, set up listener immediately
            Scene scene = rootPane.getScene();
            if (scene != null) {
                scene.widthProperty().addListener((obs, oldWidth, newWidth) -> {
                    centerGameBoard();
                    centerGameOverPanel();
                    centerPausePanel();
                    // Panels are now bound to game board, no manual positioning needed
                });
                scene.heightProperty().addListener((obs, oldHeight, newHeight) -> {
                    centerPausePanel();
                    centerGameOverPanel();
                });
            }
        }
    }
    
    /**
     * Centers the game board horizontally in the window.
     * Calculates the center position based on actual window width and board dimensions.
     */
    private void centerGameBoard() {
        if (gameBoard == null) {
            return; // Safety check
        }
        
        // Get actual window width from scene
        double windowWidth = getWindowWidth();
        if (windowWidth <= 0) {
            return; // Scene not ready yet
        }
        
        // Calculate board width: 10 columns * BRICK_SIZE + gaps + border
        // 10 columns * 20px = 200px, 9 gaps * 1px = 9px, border 12px each side = 24px
        final double BOARD_GRID_WIDTH = 10 * BRICK_SIZE + 9 * 1; // 209px
        final double BORDER_WIDTH = 12.0 * 2; // 24px (12px on each side)
        final double TOTAL_BOARD_WIDTH = BOARD_GRID_WIDTH + BORDER_WIDTH; // ~233px
        
        // Account for score panel on the left (160px width + 10px margin)
        final double SCORE_PANEL_WIDTH = 160.0;
        final double SCORE_PANEL_MARGIN = 10.0;
        final double LEFT_SPACE = SCORE_PANEL_WIDTH + SCORE_PANEL_MARGIN;
        
        // Center horizontally, but shift right to account for score panel
        // Available space = windowWidth - left space - right space (for next bricks panel)
        final double RIGHT_SPACE = 200.0; // Approximate space for next bricks panel
        final double AVAILABLE_WIDTH = windowWidth - LEFT_SPACE - RIGHT_SPACE;
        final double CENTER_X = LEFT_SPACE + (AVAILABLE_WIDTH - TOTAL_BOARD_WIDTH) / 2.0;
        gameBoard.setLayoutX(CENTER_X);
    }
    
    /**
     * Gets the current window width from the scene.
     * 
     * @return the window width, or 500.0 as fallback if scene is not available
     */
    private double getWindowWidth() {
        if (rootPane != null) {
            Scene scene = rootPane.getScene();
            if (scene != null) {
                return scene.getWidth();
            }
        }
        return 500.0; // Default fallback
    }
    
    /**
     * Centers the game over panel in the middle of the window (same as pause panel).
     */
    private void centerGameOverPanel() {
        if (groupNotification == null || rootPane == null) {
            return;
        }
        
        Platform.runLater(() -> {
            if (rootPane.getScene() != null) {
                double windowWidth = rootPane.getScene().getWidth();
                double windowHeight = rootPane.getScene().getHeight();
                
                // Get the actual size of the game over panel
                double panelWidth = gameOverPanel != null && gameOverPanel.getBoundsInParent().getWidth() > 0 
                    ? gameOverPanel.getBoundsInParent().getWidth() 
                    : 400; // Default width from CSS
                double panelHeight = gameOverPanel != null && gameOverPanel.getBoundsInParent().getHeight() > 0 
                    ? gameOverPanel.getBoundsInParent().getHeight() 
                    : 300; // Default height from CSS
                
                // Center the panel in the window
                double centerX = (windowWidth - panelWidth) / 2.0;
                double centerY = (windowHeight - panelHeight) / 2.0;
                
                groupNotification.setLayoutX(centerX);
                groupNotification.setLayoutY(centerY);
                
                // Ensure game over panel is in front of other elements
                if (gameOverPanel != null) {
                    gameOverPanel.toFront();
                }
                groupNotification.toFront();
            }
        });
    }
    
    /**
     * Creates a semi-transparent grid overlay on a separate pane.
     * The grid lines are visible but allow the background to show through.
     */
    private void createGridOverlay() {
        if (gridOverlay == null || gamePanel == null) {
            return;
        }
        
        // Board dimensions: 10 columns, 23 visible rows (25 total - 2 hidden)
        final int COLUMNS = 10;
        final int VISIBLE_ROWS = 23; // 25 total rows - 2 hidden at top
        
        // Calculate grid cell size (BRICK_SIZE + gap)
        final double CELL_SIZE = BRICK_SIZE + 1; // 20px brick + 1px gap
        
        // Set overlay size to match gamePanel
        gridOverlay.setPrefSize(COLUMNS * CELL_SIZE, VISIBLE_ROWS * CELL_SIZE);
        gridOverlay.setMaxSize(COLUMNS * CELL_SIZE, VISIBLE_ROWS * CELL_SIZE);
        
        // Create vertical grid lines
        for (int col = 0; col <= COLUMNS; col++) {
            Line verticalLine = new Line();
            verticalLine.setStartX(col * CELL_SIZE);
            verticalLine.setStartY(0);
            verticalLine.setEndX(col * CELL_SIZE);
            verticalLine.setEndY(VISIBLE_ROWS * CELL_SIZE);
            verticalLine.setStroke(Color.rgb(255, 255, 255, 0.15)); // Semi-transparent white
            verticalLine.setStrokeWidth(0.5);
            gridOverlay.getChildren().add(verticalLine);
        }
        
        // Create horizontal grid lines
        for (int row = 0; row <= VISIBLE_ROWS; row++) {
            Line horizontalLine = new Line();
            horizontalLine.setStartX(0);
            horizontalLine.setStartY(row * CELL_SIZE);
            horizontalLine.setEndX(COLUMNS * CELL_SIZE);
            horizontalLine.setEndY(row * CELL_SIZE);
            horizontalLine.setStroke(Color.rgb(255, 255, 255, 0.15)); // Semi-transparent white
            horizontalLine.setStrokeWidth(0.5);
            gridOverlay.getChildren().add(horizontalLine);
        }
    }
    
    /**
     * Positions the next bricks panel to the right of the game board.
     */
    private void positionNextBricksPanel() {
        if (nextBricksPanel == null || gameBoard == null) {
            return;
        }
        
        // Position panel to the right of the game board
        double gameBoardRight = gameBoard.getLayoutX() + (10 * BRICK_SIZE + 9 * 1 + 12 * 2);
        double panelSpacing = 20.0; // Space between game board and panel
        nextBricksPanel.setLayoutX(gameBoardRight + panelSpacing);
    }
    
    /**
     * Updates the display of the next 3 bricks in the side panel.
     */
    private void updateNextBricksDisplay() {
        if (nextBricksContainer == null || board == null) {
            return;
        }
        
        // Clear existing brick previews
        nextBricksContainer.getChildren().clear();
        
        // Get next 3 bricks
        java.util.List<Brick> nextBricks = board.getNextBricks(3);
        
        // Create preview for each brick
        for (Brick brick : nextBricks) {
            VBox brickPreview = createBrickPreview(brick);
            nextBricksContainer.getChildren().add(brickPreview);
        }
    }
    
    /**
     * Creates a preview panel for a single brick with jungle theme styling.
     * 
     * @param brick the brick to preview
     * @return a VBox containing the brick preview
     */
    private VBox createBrickPreview(Brick brick) {
        VBox preview = new VBox(5);
        preview.getStyleClass().add("jungle-brick-preview");
        preview.setAlignment(Pos.CENTER);
        
        // Get the first shape of the brick
        int[][] shape = brick.getShapeMatrix().get(0);
        
        // Create a GridPane to display the brick shape
        GridPane brickGrid = new GridPane();
        brickGrid.setHgap(2);
        brickGrid.setVgap(2);
        brickGrid.setAlignment(Pos.CENTER);
        
        // Scale factor to make preview smaller (about 12px per cell)
        final double PREVIEW_CELL_SIZE = 12.0;
        
        // Draw the brick shape
        for (int i = 0; i < shape.length; i++) {
            for (int j = 0; j < shape[i].length; j++) {
                if (shape[i][j] != 0) {
                    Rectangle cell = new Rectangle(PREVIEW_CELL_SIZE, PREVIEW_CELL_SIZE);
                    cell.setFill(getFillColor(shape[i][j]));
                    cell.setArcWidth(2);
                    cell.setArcHeight(2);
                    brickGrid.add(cell, j, i);
                }
            }
        }
        
        preview.getChildren().add(brickGrid);
        return preview;
    }
    
    /**
     * Positions the score panel on the left side of the window.
     * NOTE: This method is now deprecated - panels are bound to game board position.
     * Kept for reference but no longer called.
     */
    @Deprecated
    private void positionScorePanel() {
        // Panels are now bound to game board position in initialize() method
        // This method is kept for reference but should not be called
    }
    
    /**
     * Centers the score label relative to the game board's right edge.
     * Positions the label to the right of the game board with a margin.
     * This ensures the Score aligns vertically with the "Next Bricks" panel and never touches the board.
     */
    private void centerScoreLabel() {
        if (scoreLabel == null || gameBoard == null) {
            return; // Safety check
        }
        
        // Calculate game board's right edge
        // Board width = 10 columns * BRICK_SIZE + 9 gaps + 24px border (12px each side)
        final double BOARD_GRID_WIDTH = 10 * BRICK_SIZE + 9; // 209px
        final double BORDER_WIDTH = 24.0; // 12px each side
        final double gameBoardRightEdge = gameBoard.getLayoutX() + BOARD_GRID_WIDTH + BORDER_WIDTH;
        
        // Position score label to the right of the game board with 20px margin
        scoreLabel.setLayoutX(gameBoardRightEdge + 20);
    }
    
    /**
     * Updates the high score display label.
     */
    private void updateHighScoreDisplay() {
        if (highScoreLabel != null && highScoreManager != null) {
            highScoreLabel.setText(String.valueOf(highScoreManager.getHighScore()));
        }
    }
    
    /**
     * Updates the high score if the current score is higher.
     * Called when the game ends.
     * 
     * @param currentScore the current game score
     * @return true if a new high score was set, false otherwise
     */
    public boolean updateHighScore(int currentScore) {
        if (highScoreManager != null) {
            boolean updated = highScoreManager.updateHighScore(currentScore);
            if (updated) {
                updateHighScoreDisplay();
            }
            return updated;
        }
        return false;
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
                this::hardDrop,
                () -> newGame(null)
        );
        
        // Set up ESC key handler for pause toggle
        inputHandler.setPauseToggleCallback(this::togglePause);
        
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
     * Sets the board reference for accessing next bricks.
     * 
     * @param board the game board
     */
    public void setBoard(Board board) {
        this.board = board;
        updateNextBricksDisplay();
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
        // Clear existing rectangles if any
        if (brickPanel != null) {
            brickPanel.getChildren().clear();
        }
        if (ghostPanel != null) {
            ghostPanel.getChildren().clear();
        }
        
        rectangles = new Rectangle[brick.getBrickData().length][brick.getBrickData()[0].length];
        ghostRectangles = new Rectangle[brick.getBrickData().length][brick.getBrickData()[0].length];
        
        for (int i = 0; i < brick.getBrickData().length; i++) {
            for (int j = 0; j < brick.getBrickData()[i].length; j++) {
                // Create regular brick rectangle
                Rectangle rectangle = new Rectangle(BRICK_SIZE, BRICK_SIZE);
                rectangle.setFill(getFillColor(brick.getBrickData()[i][j]));
                rectangles[i][j] = rectangle;
                brickPanel.add(rectangle, j, i);
                
                // Create ghost piece rectangle (semi-transparent)
                Rectangle ghostRectangle = new Rectangle(BRICK_SIZE, BRICK_SIZE);
                if (brick.getBrickData()[i][j] != 0) {
                    // Make ghost piece semi-transparent (30% opacity)
                    Paint ghostColor = getFillColor(brick.getBrickData()[i][j]);
                    if (ghostColor instanceof Color) {
                        Color originalColor = (Color) ghostColor;
                        ghostRectangle.setFill(new Color(
                            originalColor.getRed(),
                            originalColor.getGreen(),
                            originalColor.getBlue(),
                            0.3 // 30% opacity for ghost effect
                        ));
                    } else {
                        ghostRectangle.setFill(Color.rgb(255, 255, 255, 0.3));
                    }
                    ghostRectangle.setStroke(Color.WHITE);
                    ghostRectangle.setStrokeWidth(1);
                } else {
                    ghostRectangle.setFill(Color.TRANSPARENT);
                }
                ghostRectangles[i][j] = ghostRectangle;
                ghostPanel.add(ghostRectangle, j, i);
            }
        }
        // Calculate gamePanel's absolute X position
        // gameBoard is centered, gamePanel is in BorderPane center (layoutX = 0), add border width
        double gamePanelAbsoluteX = (gameBoard != null ? gameBoard.getLayoutX() : 0) + 12; // BorderPane border width
        brickPanel.setLayoutX(gamePanelAbsoluteX + brick.getxPosition() * brickPanel.getVgap() + brick.getxPosition() * BRICK_SIZE);
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
            // Calculate gamePanel's absolute X position
            // gameBoard is centered, gamePanel is in BorderPane center (layoutX = 0), add border width
            double gamePanelAbsoluteX = (gameBoard != null ? gameBoard.getLayoutX() : 0) + 12; // BorderPane border width
            brickPanel.setLayoutX(gamePanelAbsoluteX + brick.getxPosition() * brickPanel.getVgap() + brick.getxPosition() * BRICK_SIZE);
            brickPanel.setLayoutY(BRICK_PANEL_Y_OFFSET + gamePanel.getLayoutY() + brick.getyPosition() * brickPanel.getHgap() + brick.getyPosition() * BRICK_SIZE);
            
            // Update regular brick display
            for (int i = 0; i < brick.getBrickData().length; i++) {
                for (int j = 0; j < brick.getBrickData()[i].length; j++) {
                    setRectangleData(brick.getBrickData()[i][j], rectangles[i][j]);
                }
            }
            
            // Update ghost piece position and display
            updateGhostPiece(brick);
        }
    }
    
    /**
     * Updates the ghost piece display to show where the current brick will land.
     * 
     * @param brick the current brick ViewData
     */
    private void updateGhostPiece(ViewData brick) {
        if (ghostPanel == null || ghostRectangles == null || board == null) {
            return;
        }
        
        // Get ghost Y position (where the brick will land)
        int ghostY = board.getGhostPieceY();
        int currentX = brick.getxPosition();
        int currentY = brick.getyPosition();
        
        // Only show ghost piece if it's different from current position (brick is falling)
        if (ghostY == currentY) {
            // Hide ghost piece if already at landing position
            ghostPanel.setVisible(false);
            return;
        }
        
        // Show ghost piece
        ghostPanel.setVisible(true);
        
        // Calculate gamePanel's absolute X position (same as regular brick)
        double gamePanelAbsoluteX = (gameBoard != null ? gameBoard.getLayoutX() : 0) + 12;
        
        // Position ghost panel at ghost Y position
        // Use the EXACT same calculation as brickPanel (matching the existing pattern)
        // Note: brickPanel uses getVgap() for X and getHgap() for Y (matching line 531-532)
        double baseY = BRICK_PANEL_Y_OFFSET + gamePanel.getLayoutY() + ghostY * brickPanel.getHgap() + ghostY * BRICK_SIZE;
        ghostPanel.setLayoutX(gamePanelAbsoluteX + currentX * brickPanel.getVgap() + currentX * BRICK_SIZE);
        ghostPanel.setLayoutY(baseY + GHOST_Y_PIXEL_ADJUSTMENT);
        
        // Update ghost rectangles to match brick shape
        for (int i = 0; i < brick.getBrickData().length; i++) {
            for (int j = 0; j < brick.getBrickData()[i].length; j++) {
                if (ghostRectangles[i][j] != null) {
                    if (brick.getBrickData()[i][j] != 0) {
                        // Make ghost piece semi-transparent (30% opacity)
                        Paint ghostColor = getFillColor(brick.getBrickData()[i][j]);
                        if (ghostColor instanceof Color) {
                            Color originalColor = (Color) ghostColor;
                            ghostRectangles[i][j].setFill(new Color(
                                originalColor.getRed(),
                                originalColor.getGreen(),
                                originalColor.getBlue(),
                                0.3 // 30% opacity for ghost effect
                            ));
                        } else {
                            ghostRectangles[i][j].setFill(Color.rgb(255, 255, 255, 0.3));
                        }
                        ghostRectangles[i][j].setStroke(Color.WHITE);
                        ghostRectangles[i][j].setStrokeWidth(1);
                        ghostRectangles[i][j].setVisible(true);
                    } else {
                        ghostRectangles[i][j].setVisible(false);
                    }
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
        updateNextBricksDisplay(); // Update next bricks when background is refreshed (brick locked)
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
            updateNextBricksDisplay(); // Update next bricks when a brick is placed
        }
        gamePanel.requestFocus();
    }
    
    /**
     * Handles the hard drop event from the input handler.
     * This method is called when the user presses the spacebar to instantly drop the brick.
     *
     * @param event the MoveEvent representing the hard drop
     */
    public void hardDrop(MoveEvent event) {
        if (isPause.getValue() == Boolean.FALSE) {
            DownData downData = eventListener.onHardDropEvent(event);
            if (downData.getClearRow() != null && downData.getClearRow().getLinesRemoved() > 0) {
                NotificationPanel notificationPanel = new NotificationPanel("+" + downData.getClearRow().getScoreBonus());
                groupNotification.getChildren().add(notificationPanel);
                notificationPanel.showScore(groupNotification.getChildren());
            }
            refreshBrick(downData.getViewData());
            refreshGameBackground(board.getBoardMatrix()); // Refresh background after hard drop
            updateNextBricksDisplay(); // Update next bricks when a brick is placed
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

    /**
     * Binds the score property to the score label display.
     * Updates the label text whenever the score changes.
     *
     * @param integerProperty the score property to bind to
     */
    public void bindScore(IntegerProperty integerProperty) {
        if (scoreLabel != null && integerProperty != null) {
            // Bind the label text to the score property
            scoreLabel.textProperty().bind(
                javafx.beans.binding.Bindings.concat("Score: ", integerProperty.asString())
            );
        }
    }
    
    /**
     * Binds the level property to the level label and sets up speed adjustment based on level.
     * The game speed increases (drop interval decreases) as the level increases.
     * 
     * @param levelProperty the IntegerProperty representing the current level
     * @param gameMode the current game mode (CLASSIC or OVERGROWTH)
     */
    public void bindLevel(IntegerProperty levelProperty, GameMode gameMode) {
        if (levelLabel != null && levelProperty != null) {
            // Bind the level text to the property
            levelLabel.textProperty().bind(levelProperty.asString());
            
            // Add listener to adjust game speed when level changes
            levelProperty.addListener((obs, oldVal, newVal) -> {
                // Calculate new speed: starts at 400ms, decreases by 50ms per level
                // Minimum speed is 100ms (level 7+)
                double newSpeed = Math.max(100, 400 - ((newVal.intValue() - 1) * 50));
                
                // Stop current timeline
                if (timeLine != null) {
                    timeLine.stop();
                }
                
                // Create new timeline with updated speed
                timeLine = new Timeline(new KeyFrame(
                    Duration.millis(newSpeed),
                    ae -> moveDown(new MoveEvent(EventType.DOWN, EventSource.THREAD))
                ));
                timeLine.setCycleCount(Timeline.INDEFINITE);
                timeLine.play();
            });
        }
    }

    public void gameOver() {
        timeLine.stop();
        gameOverPanel.setVisible(true);
        isGameOver.setValue(Boolean.TRUE);
        // Re-center the panel when it becomes visible to ensure accurate positioning
        centerGameOverPanel();
        // Ensure game over panel is in front of all other elements (including next bricks panel)
        Platform.runLater(() -> {
            if (gameOverPanel != null) {
                gameOverPanel.toFront();
            }
            if (groupNotification != null) {
                groupNotification.toFront();
            }
        });
        // Play game over sound and stop background music
        SoundManager.getInstance().playSound("gameover");
        SoundManager.getInstance().stopMusic();
    }

    public void newGame(ActionEvent actionEvent) {
        timeLine.stop();
        gameOverPanel.setVisible(false);
        eventListener.createNewGame();
        gamePanel.requestFocus();
        timeLine.play();
        isPause.setValue(Boolean.FALSE);
        isGameOver.setValue(Boolean.FALSE);
        updateNextBricksDisplay(); // Update next bricks for new game
        // Restart background music when starting a new game
        SoundManager.getInstance().startMusic();
    }

    /**
     * Initializes the pause panel and sets up its button actions.
     */
    private void initializePausePanel() {
        if (pausePanelContainer != null) {
            pausePanel = new PausePanel();
            
            // Set up Resume button
            pausePanel.setOnResume(() -> {
                togglePause();
            });
            
            // Set up Settings button
            pausePanel.setOnSettings(() -> {
                showSettings();
            });
            
            // Set up Main Menu button
            pausePanel.setOnMainMenu(() -> {
                returnToMainMenu();
            });
            
            // Add pause panel to container
            pausePanelContainer.getChildren().add(pausePanel);
        }
    }
    
    /**
     * Toggles the pause state of the game.
     * Shows/hides the pause panel and stops/starts the game timeline.
     */
    public void togglePause() {
        if (isGameOver.getValue()) {
            return; // Don't allow pausing when game is over
        }
        
        boolean newPauseState = !isPause.getValue();
        isPause.setValue(newPauseState);
        
        if (pausePanelContainer != null) {
            pausePanelContainer.setVisible(newPauseState);
            pausePanelContainer.setManaged(newPauseState);
            if (newPauseState) {
                pausePanelContainer.toFront();
                // Center the pause panel in the window
                centerPausePanel();
                if (timeLine != null) {
                    timeLine.pause();
                }
                // Pause overgrowth timer if it exists
                if (gameController != null) {
                    gameController.pauseOvergrowthTimer(true);
                }
            } else {
                if (timeLine != null) {
                    timeLine.play();
                }
                // Resume overgrowth timer if it exists
                if (gameController != null) {
                    gameController.pauseOvergrowthTimer(false);
                }
                gamePanel.requestFocus();
            }
        }
    }
    
    /**
     * Sets the GameController reference for pause/resume functionality.
     * 
     * @param controller the GameController instance
     */
    public void setGameController(com.comp2042.controller.GameController controller) {
        this.gameController = controller;
    }
    
    /**
     * Centers the pause panel in the window.
     */
    private void centerPausePanel() {
        if (pausePanelContainer == null || rootPane == null) {
            return;
        }
        
        Platform.runLater(() -> {
            if (rootPane.getScene() != null) {
                double windowWidth = rootPane.getScene().getWidth();
                double windowHeight = rootPane.getScene().getHeight();
                
                // Get the actual size of the pause panel container
                double panelWidth = pausePanelContainer.getWidth();
                double panelHeight = pausePanelContainer.getHeight();
                
                // If size is 0, use preferred size from CSS (400px width, 500px height)
                if (panelWidth == 0) {
                    panelWidth = 400;
                }
                if (panelHeight == 0) {
                    panelHeight = 500;
                }
                
                // Center the pause panel container
                double centerX = (windowWidth - panelWidth) / 2.0;
                double centerY = (windowHeight - panelHeight) / 2.0;
                
                pausePanelContainer.setLayoutX(centerX);
                pausePanelContainer.setLayoutY(centerY);
            }
        });
    }
    
    /**
     * Shows the settings panel over the pause panel.
     */
    private void showSettings() {
        if (pausePanelContainer != null && settingsPanel == null) {
            settingsPanel = new SettingsPanel();
            
            // Set up Back button to return to pause menu
            settingsPanel.setOnBack(() -> {
                hideSettings();
            });
            
            // Add settings panel to pause container (on top of pause panel)
            pausePanelContainer.getChildren().add(settingsPanel);
            settingsPanel.toFront();
        } else if (settingsPanel != null) {
            settingsPanel.setVisible(true);
            settingsPanel.setManaged(true);
            settingsPanel.toFront();
        }
    }
    
    /**
     * Hides the settings panel and returns to pause menu.
     */
    private void hideSettings() {
        if (settingsPanel != null) {
            settingsPanel.setVisible(false);
            settingsPanel.setManaged(false);
        }
    }
    
    /**
     * Returns to the main menu from the game.
     * Stops the game and loads the main menu scene.
     */
    private void returnToMainMenu() {
        // Stop the game timeline
        if (timeLine != null) {
            timeLine.stop();
        }
        
        // Get the primary stage
        if (primaryStage == null && rootPane != null && rootPane.getScene() != null) {
            primaryStage = (javafx.stage.Stage) rootPane.getScene().getWindow();
        }
        
        if (primaryStage != null) {
            try {
                // Load the main menu
                URL location = getClass().getClassLoader().getResource("mainMenuLayout.fxml");
                ResourceBundle resources = null;
                javafx.fxml.FXMLLoader fxmlLoader = new javafx.fxml.FXMLLoader(location, resources);
                javafx.scene.Parent root = fxmlLoader.load();
                com.comp2042.view.MainMenuController menuController = fxmlLoader.getController();
                menuController.setPrimaryStage(primaryStage);
                
                // Update stage to show main menu
                primaryStage.setScene(new javafx.scene.Scene(root, 500, 510));
                primaryStage.setTitle("TetrisJFX - Main Menu");
            } catch (Exception e) {
                System.err.println("Error loading main menu: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }
    
    /**
     * Sets the primary stage reference for navigation purposes.
     * 
     * @param stage the primary stage
     */
    public void setPrimaryStage(javafx.stage.Stage stage) {
        this.primaryStage = stage;
    }

    public void pauseGame(ActionEvent actionEvent) {
        togglePause();
    }
}

