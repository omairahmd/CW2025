package com.comp2042.controller;

import com.comp2042.events.InputEventListener;
import com.comp2042.events.MoveEvent;
import com.comp2042.events.EventSource;
import com.comp2042.manager.SoundManager;
import com.comp2042.model.Board;
import com.comp2042.model.ClearRow;
import com.comp2042.model.DownData;
import com.comp2042.model.GameMode;
import com.comp2042.model.SimpleBoard;
import com.comp2042.model.ViewData;
import com.comp2042.view.GuiController;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.util.Duration;

public class GameController implements InputEventListener {

    // Board dimensions
    /** Number of rows (height) in the game board */
    private static final int BOARD_HEIGHT = 25;
    
    /** Number of columns (width) in the game board */
    private static final int BOARD_WIDTH = 10;

    // Scoring rules
    /** Points awarded for manually moving a brick down */
    private static final int MANUAL_MOVE_SCORE = 1;
    
    /** Points awarded per row when hard dropping a brick */
    private static final int HARD_DROP_SCORE_PER_ROW = 2;

    private Board board = new SimpleBoard(BOARD_WIDTH, BOARD_HEIGHT);

    private final GuiController viewGuiController;
    
    private Timeline overgrowthTimer;

    public GameController(GuiController c) {
        this(c, GameMode.CLASSIC);
    }
    
    public GameController(GuiController c, GameMode mode) {
        viewGuiController = c;
        board.setGameMode(mode);
        board.newGame(); // Initialize the game (including treasure field for TREASURE_HUNT mode)
        viewGuiController.setEventListener(this);
        viewGuiController.setBoard(board); // Set board reference for next bricks display
        viewGuiController.initGameView(board.getBoardMatrix(), board.getViewData());
        viewGuiController.bindScore(board.getScore().scoreProperty()); // Binds score
        viewGuiController.bindLevel(board.levelProperty(), board.getGameMode()); // Binds level and sets up speed adjustment
        viewGuiController.setGameController(this); // Set GameController reference for pause/resume
        
        // Initialize overgrowth timer if in OVERGROWTH mode
        if (board.getGameMode() == GameMode.OVERGROWTH) {
            initializeOvergrowthTimer();
        }
    }
    
    /**
     * Initializes the overgrowth timer that adds vine lines every 10 seconds.
     */
    private void initializeOvergrowthTimer() {
        overgrowthTimer = new Timeline(new KeyFrame(
            Duration.seconds(10),
            ae -> {
                if (board.addVineLine()) {
                    // Operation successful - refresh the view
                    refreshGameView();
                } else {
                    // Game over - top row has blocks
                    viewGuiController.updateHighScore(board.getScore().scoreProperty().get());
                    viewGuiController.gameOver();
                    if (overgrowthTimer != null) {
                        overgrowthTimer.stop();
                    }
                }
            }
        ));
        overgrowthTimer.setCycleCount(Timeline.INDEFINITE);
        overgrowthTimer.play();
    }
    
    /**
     * Pauses or resumes the overgrowth timer.
     * Used when the game is paused/unpaused.
     * 
     * @param pause true to pause, false to resume
     */
    public void pauseOvergrowthTimer(boolean pause) {
        if (overgrowthTimer != null) {
            if (pause) {
                overgrowthTimer.pause();
            } else {
                overgrowthTimer.play();
            }
        }
    }

    @Override
    public DownData onDownEvent(MoveEvent event) {
        boolean canMove = board.moveBrickDown();
        ClearRow clearRow = null;
        if (!canMove) {
            clearRow = handleBrickLocked();
        } else {
            awardManualMoveScore(event);
        }
        return new DownData(clearRow, board.getViewData());
    }

    /**
     * Handles the logic when a brick can no longer move down (is locked).
     * Merges the brick to the background, clears completed rows, updates score,
     * checks for game over, and refreshes the view.
     *
     * @return ClearRow information about cleared rows, or null if no rows were cleared
     */
    private ClearRow handleBrickLocked() {
        board.mergeBrickToBackground();
        ClearRow clearRow = board.clearRows();
        updateScoreForClearedRows(clearRow);
        
        // Check for victory condition in Treasure Hunt mode
        if (board.getGameMode() == GameMode.TREASURE_HUNT && !board.hasRemainingTreasure()) {
            viewGuiController.showVictory();
            viewGuiController.gameOver(); // Stop the game loop
        } else {
            checkAndHandleGameOver();
        }
        
        refreshGameView();
        return clearRow;
    }

    /**
     * Updates the score if any rows were cleared.
     *
     * @param clearRow the result of row clearing operation
     */
    private void updateScoreForClearedRows(ClearRow clearRow) {
        if (clearRow.getLinesRemoved() > 0) {
            board.getScore().add(clearRow.getScoreBonus());
            SoundManager.getInstance().playSound("clear");
        }
    }

    /**
     * Checks if the game is over (new brick cannot be created) and triggers game over if needed.
     */
    private void checkAndHandleGameOver() {
        if (board.createNewBrick()) {
            // Update high score before showing game over
            int currentScore = board.getScore().scoreProperty().getValue();
            viewGuiController.updateHighScore(currentScore);
            viewGuiController.gameOver();
            // Stop overgrowth timer if it's running
            if (overgrowthTimer != null) {
                overgrowthTimer.stop();
            }
        }
    }

    /**
     * Refreshes the game view to display the current board state.
     */
    private void refreshGameView() {
        viewGuiController.refreshGameBackground(board.getBoardMatrix());
    }

    /**
     * Awards score points for manually moving a brick down (user input).
     *
     * @param event the move event that triggered this action
     */
    private void awardManualMoveScore(MoveEvent event) {
        if (event.getEventSource() == EventSource.USER) {
            board.getScore().add(MANUAL_MOVE_SCORE);
        }
    }

    @Override
    public ViewData onLeftEvent(MoveEvent event) {
        board.moveBrickLeft();
        return board.getViewData();
    }

    @Override
    public ViewData onRightEvent(MoveEvent event) {
        board.moveBrickRight();
        return board.getViewData();
    }

    @Override
    public ViewData onRotateEvent(MoveEvent event) {
        board.rotateLeftBrick();
        return board.getViewData();
    }
    
    @Override
    public DownData onHardDropEvent(MoveEvent event) {
        // Hard drop the brick to the bottom
        int rowsDropped = board.hardDrop();
        
        // Award score for hard drop (2 points per row)
        if (rowsDropped > 0 && event.getEventSource() == EventSource.USER) {
            board.getScore().add(rowsDropped * HARD_DROP_SCORE_PER_ROW);
        }
        
        // The brick is now at the bottom, so it can't move down anymore
        // Handle brick locking (merge, clear rows, etc.)
        ClearRow clearRow = handleBrickLocked();
        
        return new DownData(clearRow, board.getViewData());
    }

    @Override
    public void createNewGame() {
        board.newGame();
        refreshGameView();
    }
}

