package com.comp2042.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Test suite for SimpleBoard class.
 * Tests game board initialization, brick movement, and score functionality.
 */
class SimpleBoardTest {

    private static final int BOARD_WIDTH = 10;
    private static final int BOARD_HEIGHT = 20;
    private SimpleBoard board;

    @BeforeEach
    void setUp() {
        board = new SimpleBoard(BOARD_WIDTH, BOARD_HEIGHT);
    }

    @Test
    void testNewGame() {
        // Initialize a board and call newGame()
        board.newGame();
        
        // Assert that getBoardMatrix() returns a non-null array
        int[][] boardMatrix = board.getBoardMatrix();
        assertNotNull(boardMatrix, "Board matrix should not be null");
        
        // Assert correct dimensions
        // Board matrix is stored as [height][width] (row-major)
        assertEquals(BOARD_HEIGHT, boardMatrix.length, "Board height should match");
        assertEquals(BOARD_WIDTH, boardMatrix[0].length, "Board width should match");
    }

    @Test
    void testMoveBrickDown() {
        // Create a board and spawn a brick
        board.newGame();
        
        // Call moveBrickDown() - should return true since the board is empty
        boolean moved = board.moveBrickDown();
        
        assertTrue(moved, "Brick should be able to move down on an empty board");
    }

    @Test
    void testScoreInitialization() {
        // Assert that getScore() is not null
        Score score = board.getScore();
        assertNotNull(score, "Score should not be null");
        
        // Assert that score starts at 0
        int initialScore = score.scoreProperty().getValue();
        assertEquals(0, initialScore, "Score should start at 0");
    }

    @Test
    void testGameModeSelection() {
        board.setGameMode(GameMode.TREASURE_HUNT);
        board.newGame();

        assertEquals(GameMode.TREASURE_HUNT, board.getGameMode(), "Board should report TREASURE_HUNT mode");
        assertTrue(board.hasRemainingTreasure(), "Treasure Hunt should spawn gold blocks at start");
    }
}

