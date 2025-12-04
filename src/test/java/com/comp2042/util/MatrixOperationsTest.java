package com.comp2042.util;

import com.comp2042.model.ClearRow;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Test suite for MatrixOperations utility class.
 * Tests verify bug fixes for ArrayIndexOutOfBoundsException and indexing errors.
 */
class MatrixOperationsTest {

    // Test constants
    private static final int BOARD_WIDTH = 10;
    private static final int BOARD_HEIGHT = 20;
    private static final int BASE_SCORE_PER_LINE = 50;

    // ==================== isOutOfBounds() Tests (via intersect) ====================

    /**
     * Verifies the bug fix: isOutOfBounds should return true for negative Y coordinates.
     * This test proves the crash fix for checking collisions at the top of the board.
     */
    @Test
    void testIsOutOfBounds_NegativeY_ReturnsTrue() {
        // Arrange: Try to place brick at y = -1 (above board - would crash without fix)
        int[][] board = new int[BOARD_HEIGHT][BOARD_WIDTH];
        int[][] brick = new int[][]{{1}}; // Single cell brick
        
        // Act: This should NOT throw ArrayIndexOutOfBoundsException
        // If isOutOfBounds is fixed, it should detect negative Y and return true (intersect = true)
        boolean intersects = MatrixOperations.intersect(board, brick, 5, -1);
        
        // Assert: Should detect out of bounds (negative Y) without crashing
        assertTrue(intersects, "Negative Y coordinate should be detected as out of bounds (bug fix verified)");
    }

    /**
     * Verifies isOutOfBounds returns false for valid coordinates.
     */
    @Test
    void testIsOutOfBounds_ValidCoordinates_ReturnsFalse() {
        // Arrange: Valid coordinates within board bounds
        int[][] board = new int[BOARD_HEIGHT][BOARD_WIDTH];
        int[][] brick = new int[][]{{1}};
        int x = 5;
        int y = 10; // Valid Y coordinate
        
        // Act: Should not intersect (coordinates are in bounds)
        boolean intersects = MatrixOperations.intersect(board, brick, x, y);
        
        // Assert: Should not intersect because coordinates are valid
        assertFalse(intersects, "Valid coordinates should not cause intersection");
    }

    @Test
    void testIsOutOfBounds_NegativeX_ReturnsTrue() {
        // Arrange: Negative X coordinate
        int[][] board = new int[BOARD_HEIGHT][BOARD_WIDTH];
        int[][] brick = new int[][]{{1}};
        
        // Act
        boolean intersects = MatrixOperations.intersect(board, brick, -1, 5);
        
        // Assert
        assertTrue(intersects, "Negative X coordinate should be detected as out of bounds");
    }

    @Test
    void testIsOutOfBounds_XTooLarge_ReturnsTrue() {
        // Arrange: X coordinate exceeds board width
        int[][] board = new int[BOARD_HEIGHT][BOARD_WIDTH];
        int[][] brick = new int[][]{{1}};
        
        // Act
        boolean intersects = MatrixOperations.intersect(board, brick, BOARD_WIDTH, 5);
        
        // Assert
        assertTrue(intersects, "X coordinate >= board width should be out of bounds");
    }

    @Test
    void testIsOutOfBounds_YTooLarge_ReturnsTrue() {
        // Arrange: Y coordinate exceeds board height
        int[][] board = new int[BOARD_HEIGHT][BOARD_WIDTH];
        int[][] brick = new int[][]{{1}};
        
        // Act
        boolean intersects = MatrixOperations.intersect(board, brick, 5, BOARD_HEIGHT);
        
        // Assert
        assertTrue(intersects, "Y coordinate >= board height should be out of bounds");
    }

    // ==================== intersect() Tests ====================

    /**
     * Verifies collision detection works correctly.
     */
    @Test
    void testIntersect_NoCollision_EmptyBoard() {
        // Arrange: Empty board, valid position
        int[][] board = new int[BOARD_HEIGHT][BOARD_WIDTH];
        int[][] brick = new int[][]{
                {1, 1},
                {1, 1}
        };
        int x = 4;
        int y = 5;
        
        // Act
        boolean result = MatrixOperations.intersect(board, brick, x, y);
        
        // Assert
        assertFalse(result, "Brick should not intersect with empty board");
    }

    @Test
    void testIntersect_Collision_WithExistingBrick() {
        // Arrange: Board with existing brick
        int[][] board = new int[BOARD_HEIGHT][BOARD_WIDTH];
        board[5][4] = 2;
        board[5][5] = 2;
        
        int[][] brick = new int[][]{
                {1, 1},
                {1, 1}
        };
        int x = 4;
        int y = 5;
        
        // Act
        boolean result = MatrixOperations.intersect(board, brick, x, y);
        
        // Assert
        assertTrue(result, "Brick should intersect with existing brick");
    }

    /**
     * Verifies the indexing bug fix: brick[i][j] instead of brick[j][i].
     * This test uses a brick with more columns than rows to catch the bug.
     */
    @Test
    void testIntersect_IndexingFix_WideBrick() {
        // Arrange: Wide brick (1 row, 2 columns) - this would crash with old brick[j][i] indexing
        int[][] board = new int[BOARD_HEIGHT][BOARD_WIDTH];
        int[][] brick = new int[][]{
                {1, 1} // 1 row, 2 columns
        };
        int x = 4;
        int y = 5;
        
        // Act: Should NOT throw ArrayIndexOutOfBoundsException
        boolean result = MatrixOperations.intersect(board, brick, x, y);
        
        // Assert: Should work correctly with fixed indexing
        assertFalse(result, "Wide brick should not cause ArrayIndexOutOfBoundsException (indexing fix verified)");
    }

    @Test
    void testIntersect_IndexingFix_TallBrick() {
        // Arrange: Tall brick (2 rows, 1 column)
        int[][] board = new int[BOARD_HEIGHT][BOARD_WIDTH];
        int[][] brick = new int[][]{
                {1},
                {1}
        };
        int x = 4;
        int y = 5;
        
        // Act: Should NOT throw ArrayIndexOutOfBoundsException
        boolean result = MatrixOperations.intersect(board, brick, x, y);
        
        // Assert
        assertFalse(result, "Tall brick should work correctly with fixed indexing");
    }

    @Test
    void testIntersect_EdgeCase_AtTopOfBoard() {
        // Arrange: Brick at y = 0 (top row) - tests the negative Y fix
        int[][] board = new int[BOARD_HEIGHT][BOARD_WIDTH];
        int[][] brick = new int[][]{{1}};
        int x = 5;
        int y = 0; // Top row
        
        // Act: Should NOT crash
        boolean result = MatrixOperations.intersect(board, brick, x, y);
        
        // Assert
        assertFalse(result, "Brick at top row (y=0) should be valid");
    }

    // ==================== checkRemoving() Tests ====================

    /**
     * Verifies row clearing logic works correctly.
     */
    @Test
    void testCheckRemoving_NoFullRows() {
        // Arrange: Board with no complete rows
        int[][] board = new int[][]{
                {0, 0, 0, 0, 0},
                {1, 0, 1, 0, 1},
                {0, 0, 0, 0, 0}
        };
        
        // Act
        ClearRow result = MatrixOperations.checkRemoving(board);
        
        // Assert
        assertEquals(0, result.getLinesRemoved(), "No rows should be removed");
        assertEquals(0, result.getScoreBonus(), "Score bonus should be 0");
        assertNotNull(result.getNewMatrix(), "New matrix should not be null");
    }

    @Test
    void testCheckRemoving_OneFullRow() {
        // Arrange: Board with one complete row
        int[][] board = new int[][]{
                {0, 0, 0, 0, 0},
                {1, 1, 1, 1, 1}, // Full row
                {0, 0, 0, 0, 0}
        };
        
        // Act
        ClearRow result = MatrixOperations.checkRemoving(board);
        
        // Assert
        assertEquals(1, result.getLinesRemoved(), "One row should be removed");
        assertEquals(BASE_SCORE_PER_LINE * 1 * 1, result.getScoreBonus(), 
                "Score bonus should be 50 for one row (50 * 1^2)");
        
        // Verify the full row is removed
        int[][] newMatrix = result.getNewMatrix();
        assertEquals(board.length, newMatrix.length, "Matrix height should remain the same");
        
        // Top row should be empty after shift
        boolean topRowEmpty = true;
        for (int j = 0; j < newMatrix[0].length; j++) {
            if (newMatrix[0][j] != 0) {
                topRowEmpty = false;
                break;
            }
        }
        assertTrue(topRowEmpty, "Top row should be empty after removing full row");
    }

    @Test
    void testCheckRemoving_TwoFullRows() {
        // Arrange: Board with two complete rows
        int[][] board = new int[][]{
                {0, 0, 0, 0, 0},
                {1, 1, 1, 1, 1}, // Full row 1
                {2, 2, 2, 2, 2}, // Full row 2
                {1, 0, 1, 0, 1}
        };
        
        // Act
        ClearRow result = MatrixOperations.checkRemoving(board);
        
        // Assert
        assertEquals(2, result.getLinesRemoved(), "Two rows should be removed");
        assertEquals(BASE_SCORE_PER_LINE * 2 * 2, result.getScoreBonus(), 
                "Score bonus should be 200 for two rows (50 * 2^2)");
    }

    @Test
    void testCheckRemoving_FourFullRows() {
        // Arrange: Board with four complete rows (Tetris!)
        int[][] board = new int[][]{
                {1, 1, 1, 1, 1},
                {2, 2, 2, 2, 2},
                {3, 3, 3, 3, 3},
                {4, 4, 4, 4, 4}
        };
        
        // Act
        ClearRow result = MatrixOperations.checkRemoving(board);
        
        // Assert
        assertEquals(4, result.getLinesRemoved(), "Four rows should be removed");
        assertEquals(BASE_SCORE_PER_LINE * 4 * 4, result.getScoreBonus(), 
                "Score bonus should be 800 for four rows (50 * 4^2)");
    }

    @Test
    void testCheckRemoving_RowsShiftDown() {
        // Arrange: Board with full row in middle
        int[][] board = new int[][]{
                {0, 0, 0, 0, 0},
                {1, 0, 1, 0, 1}, // Row to keep
                {2, 2, 2, 2, 2}, // Full row to remove
                {3, 0, 3, 0, 3}  // Row to keep
        };
        
        // Act
        ClearRow result = MatrixOperations.checkRemoving(board);
        
        // Assert
        assertEquals(1, result.getLinesRemoved(), "One row should be removed");
        
        int[][] newMatrix = result.getNewMatrix();
        // Verify rows shifted down correctly
        assertEquals(0, newMatrix[0][0], "Top row should be empty after shift");
        assertEquals(0, newMatrix[1][0], "Second row should contain original top row");
        assertEquals(1, newMatrix[2][0], "Third row should contain original row 1");
        assertEquals(3, newMatrix[3][0], "Bottom row should contain original row 3");
    }

    // ==================== merge() Tests ====================

    /**
     * Verifies the indexing bug fix in merge method.
     */
    @Test
    void testMerge_IndexingFix_WideBrick() {
        // Arrange: Wide brick (1 row, 2 columns) - tests indexing fix
        // Note: merge uses targetX = x + i, targetY = y + j
        // So for brick {{1, 2}} at (x=4, y=5):
        // - i=0, j=0: result[5][4] = 1
        // - i=0, j=1: result[6][4] = 2
        int[][] board = new int[BOARD_HEIGHT][BOARD_WIDTH];
        int[][] brick = new int[][]{
                {1, 2} // 1 row, 2 columns
        };
        int x = 4;
        int y = 5;
        
        // Act: Should NOT throw ArrayIndexOutOfBoundsException
        int[][] result = MatrixOperations.merge(board, brick, x, y);
        
        // Assert: Should merge correctly with fixed indexing (no crash)
        assertEquals(1, result[y][x], "First brick cell should be merged correctly at (x, y)");
        assertEquals(2, result[y + 1][x], "Second brick cell should be merged correctly at (x, y+1) - indexing fix verified");
    }

    @Test
    void testMerge_IntoEmptyBoard() {
        // Arrange
        int[][] board = new int[BOARD_HEIGHT][BOARD_WIDTH];
        int[][] brick = new int[][]{
                {1, 1},
                {1, 1}
        };
        int x = 4;
        int y = 5;
        
        // Act
        int[][] result = MatrixOperations.merge(board, brick, x, y);
        
        // Assert
        assertEquals(1, result[y][x], "Brick should be merged at position (x, y)");
        assertEquals(1, result[y][x + 1], "Brick should be merged at position (x+1, y)");
        assertEquals(1, result[y + 1][x], "Brick should be merged at position (x, y+1)");
        assertEquals(1, result[y + 1][x + 1], "Brick should be merged at position (x+1, y+1)");
        
        // Original board should not be modified
        assertEquals(0, board[y][x], "Original board should not be modified");
    }
}

