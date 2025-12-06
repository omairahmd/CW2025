package com.comp2042.controller;

import com.comp2042.events.InputEventListener;
import com.comp2042.events.MoveEvent;
import com.comp2042.events.EventSource;
import com.comp2042.model.Board;
import com.comp2042.model.ClearRow;
import com.comp2042.model.DownData;
import com.comp2042.model.SimpleBoard;
import com.comp2042.model.ViewData;
import com.comp2042.view.GuiController;

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

    public GameController(GuiController c) {
        viewGuiController = c;
        board.createNewBrick();
        viewGuiController.setEventListener(this);
        viewGuiController.setBoard(board); // Set board reference for next bricks display
        viewGuiController.initGameView(board.getBoardMatrix(), board.getViewData());
        viewGuiController.bindScore(board.getScore().scoreProperty());
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
        checkAndHandleGameOver();
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

