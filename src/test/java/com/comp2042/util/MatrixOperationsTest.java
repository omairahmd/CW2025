package com.comp2042.util;

import com.comp2042.model.ClearRow;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Test suite for MatrixOperations utility class.
 * Tests collision detection, boundary checking, row clearing, and matrix operations.
 */
class MatrixOperationsTest {

    // Test constants
    private static final int BOARD_WIDTH = 10;
    private static final int BOARD_HEIGHT = 20;
    private static final int BASE_SCORE_PER_LINE = 50;

    // ==================== intersect() Tests ====================

    @Test
    void testIntersect_NoCollision_EmptyBoard() {
        // Arrange: Create an empty board and a small brick
        int[][] board = new int[BOARD_HEIGHT][BOARD_WIDTH];
        int[][] brick = new int[][]{
                {1, 1},
                {1, 1}
        };
        int x = 4; // Valid position
        int y = 5; // Valid position

        // Act
        boolean result = MatrixOperations.intersect(board, brick, x, y);

        // Assert
        assertFalse(result, "Brick should not intersect with empty board");
    }

    @Test
    void testIntersect_Collision_WithExistingBrick() {
        // Arrange: Create a board with an existing brick at position (4, 5)
        int[][] board = new int[BOARD_HEIGHT][BOARD_WIDTH];
        board[5][4] = 2;
        board[5][5] = 2;
        board[6][4] = 2;
        board[6][5] = 2;

        int[][] brick = new int[][]{
                {1, 1},
                {1, 1}
        };
        int x = 4; // Same position as existing brick
        int y = 5;

        // Act
        boolean result = MatrixOperations.intersect(board, brick, x, y);

        // Assert
        assertTrue(result, "Brick should intersect with existing brick at same position");
    }

    @Test
    void testIntersect_Collision_WithWall_Left() {
        // Arrange: Try to place brick at x = -1 (left wall)
        int[][] board = new int[BOARD_HEIGHT][BOARD_WIDTH];
        int[][] brick = new int[][]{
                {1, 1},
                {1, 1}
        };
        int x = -1; // Out of bounds (left wall)
        int y = 5;

        // Act
        boolean result = MatrixOperations.intersect(board, brick, x, y);

        // Assert
        assertTrue(result, "Brick should intersect with left wall (x < 0)");
    }

    @Test
    void testIntersect_Collision_WithWall_Right() {
        // Arrange: Try to place brick at x = 9 (right edge, brick extends beyond)
        int[][] board = new int[BOARD_HEIGHT][BOARD_WIDTH];
        int[][] brick = new int[][]{
                {1, 1},
                {1, 1}
        };
        int x = 9; // Right edge, brick width 2 will exceed board width
        int y = 5;

        // Act
        boolean result = MatrixOperations.intersect(board, brick, x, y);

        // Assert
        assertTrue(result, "Brick should intersect with right wall (x + brick width > board width)");
    }

    @Test
    void testIntersect_Collision_WithFloor() {
        // Arrange: Try to place brick at bottom of board
        int[][] board = new int[BOARD_HEIGHT][BOARD_WIDTH];
        int[][] brick = new int[][]{
                {1, 1},
                {1, 1}
        };
        int x = 4;
        int y = BOARD_HEIGHT - 1; // Bottom row, brick height 2 will exceed board height

        // Act
        boolean result = MatrixOperations.intersect(board, brick, x, y);

        // Assert
        assertTrue(result, "Brick should intersect with floor (y + brick height > board height)");
    }

    @Test
    void testIntersect_Collision_WithCeiling() {
        // Arrange: Try to place brick at y = -1 (above board)
        int[][] board = new int[BOARD_HEIGHT][BOARD_WIDTH];
        int[][] brick = new int[][]{
                {1, 1},
                {1, 1}
        };
        int x = 4;
        int y = -1; // Above board

        // Act
        boolean result = MatrixOperations.intersect(board, brick, x, y);

        // Assert
        assertTrue(result, "Brick should intersect with ceiling (y < 0)");
    }

    @Test
    void testIntersect_EdgeCase_XEqualsZero() {
        // Arrange: Place brick at x = 0 (leftmost valid position)
        int[][] board = new int[BOARD_HEIGHT][BOARD_WIDTH];
        int[][] brick = new int[][]{
                {1, 1}
        };
        int x = 0; // Left edge
        int y = 5;

        // Act
        boolean result = MatrixOperations.intersect(board, brick, x, y);

        // Assert
        assertFalse(result, "Brick at x=0 should be valid if it fits within board");
    }

    @Test
    void testIntersect_EdgeCase_YEqualsZero() {
        // Arrange: Place brick at y = 0 (top row)
        int[][] board = new int[BOARD_HEIGHT][BOARD_WIDTH];
        int[][] brick = new int[][]{
                {1, 1}
        };
        int x = 4;
        int y = 0; // Top row

        // Act
        boolean result = MatrixOperations.intersect(board, brick, x, y);

        // Assert
        assertFalse(result, "Brick at y=0 should be valid");
    }

    @Test
    void testIntersect_EdgeCase_YAtBottom() {
        // Arrange: Place brick at bottom row (y = BOARD_HEIGHT - 1)
        int[][] board = new int[BOARD_HEIGHT][BOARD_WIDTH];
        int[][] brick = new int[][]{
                {1} // Single cell brick
        };
        int x = 4;
        int y = BOARD_HEIGHT - 1; // Bottom row

        // Act
        boolean result = MatrixOperations.intersect(board, brick, x, y);

        // Assert
        assertFalse(result, "Single cell brick at bottom row should be valid");
    }

    @Test
    void testIntersect_EdgeCase_XAtRightEdge() {
        // Arrange: Place brick at right edge (x = BOARD_WIDTH - 1)
        int[][] board = new int[BOARD_HEIGHT][BOARD_WIDTH];
        int[][] brick = new int[][]{
                {1} // Single cell brick
        };
        int x = BOARD_WIDTH - 1; // Right edge
        int y = 5;

        // Act
        boolean result = MatrixOperations.intersect(board, brick, x, y);

        // Assert
        assertFalse(result, "Single cell brick at right edge should be valid");
    }

    @Test
    void testIntersect_TransparentBrick_NoCollision() {
        // Arrange: Brick with all zeros (transparent) should not cause collision
        int[][] board = new int[BOARD_HEIGHT][BOARD_WIDTH];
        board[5][4] = 2; // Existing brick
        int[][] brick = new int[][]{
                {0, 0},
                {0, 0}
        };
        int x = 4;
        int y = 5;

        // Act
        boolean result = MatrixOperations.intersect(board, brick, x, y);

        // Assert
        assertFalse(result, "Transparent brick (all zeros) should not cause collision");
    }

    @Test
    void testIntersect_PartialOverlap_Collision() {
        // Arrange: Brick partially overlaps with existing brick
        int[][] board = new int[BOARD_HEIGHT][BOARD_WIDTH];
        board[5][5] = 2; // Existing brick at (5, 5)
        int[][] brick = new int[][]{
                {1, 1},
                {1, 1}
        };
        int x = 4; // Overlaps at (5, 5)
        int y = 5;

        // Act
        boolean result = MatrixOperations.intersect(board, brick, x, y);

        // Assert
        assertTrue(result, "Brick should intersect when partially overlapping existing brick");
    }

    // ==================== isOutOfBounds() Tests (via intersect) ====================

    @Test
    void testIsOutOfBounds_InsideBounds() {
        // Arrange: Coordinates within valid range
        int[][] board = new int[BOARD_HEIGHT][BOARD_WIDTH];
        int[][] brick = new int[][]{{1}};
        int x = 5;
        int y = 10;

        // Act: Should not intersect (no collision means in bounds)
        boolean intersects = MatrixOperations.intersect(board, brick, x, y);

        // Assert
        assertFalse(intersects, "Coordinates inside bounds should not cause intersection");
    }

    @Test
    void testIsOutOfBounds_NegativeX() {
        // Arrange: x coordinate is negative
        int[][] board = new int[BOARD_HEIGHT][BOARD_WIDTH];
        int[][] brick = new int[][]{{1}};
        int x = -1;
        int y = 5;

        // Act
        boolean intersects = MatrixOperations.intersect(board, brick, x, y);

        // Assert
        assertTrue(intersects, "Negative x coordinate should be out of bounds");
    }

    @Test
    void testIsOutOfBounds_NegativeY() {
        // Arrange: y coordinate is negative
        int[][] board = new int[BOARD_HEIGHT][BOARD_WIDTH];
        int[][] brick = new int[][]{{1}};
        int x = 5;
        int y = -1;

        // Act
        boolean intersects = MatrixOperations.intersect(board, brick, x, y);

        // Assert
        assertTrue(intersects, "Negative y coordinate should be out of bounds");
    }

    @Test
    void testIsOutOfBounds_XTooLarge() {
        // Arrange: x coordinate exceeds board width
        int[][] board = new int[BOARD_HEIGHT][BOARD_WIDTH];
        int[][] brick = new int[][]{{1}};
        int x = BOARD_WIDTH; // Equal to width (out of bounds, indices are 0 to width-1)
        int y = 5;

        // Act
        boolean intersects = MatrixOperations.intersect(board, brick, x, y);

        // Assert
        assertTrue(intersects, "x coordinate >= board width should be out of bounds");
    }

    @Test
    void testIsOutOfBounds_YTooLarge() {
        // Arrange: y coordinate exceeds board height
        int[][] board = new int[BOARD_HEIGHT][BOARD_WIDTH];
        int[][] brick = new int[][]{{1}};
        int x = 5;
        int y = BOARD_HEIGHT; // Equal to height (out of bounds)

        // Act
        boolean intersects = MatrixOperations.intersect(board, brick, x, y);

        // Assert
        assertTrue(intersects, "y coordinate >= board height should be out of bounds");
    }

    @Test
    void testIsOutOfBounds_EdgeCase_XAtZero() {
        // Arrange: x = 0 is valid (leftmost column)
        int[][] board = new int[BOARD_HEIGHT][BOARD_WIDTH];
        int[][] brick = new int[][]{{1}};
        int x = 0;
        int y = 5;

        // Act
        boolean intersects = MatrixOperations.intersect(board, brick, x, y);

        // Assert
        assertFalse(intersects, "x = 0 should be in bounds");
    }

    @Test
    void testIsOutOfBounds_EdgeCase_YAtZero() {
        // Arrange: y = 0 is valid (top row)
        int[][] board = new int[BOARD_HEIGHT][BOARD_WIDTH];
        int[][] brick = new int[][]{{1}};
        int x = 5;
        int y = 0;

        // Act
        boolean intersects = MatrixOperations.intersect(board, brick, x, y);

        // Assert
        assertFalse(intersects, "y = 0 should be in bounds");
    }

    // ==================== checkRemoving() Tests ====================

    @Test
    void testCheckRemoving_NoFullRows() {
        // Arrange: Board with no complete rows
        int[][] board = new int[][]{
                {0, 0, 0, 0, 0},
                {1, 0, 1, 0, 1},
                {0, 0, 0, 0, 0},
                {1, 1, 0, 1, 0}
        };

        // Act
        ClearRow result = MatrixOperations.checkRemoving(board);

        // Assert
        assertEquals(0, result.getLinesRemoved(), "No rows should be removed");
        assertEquals(0, result.getScoreBonus(), "Score bonus should be 0 for no rows cleared");
        assertNotNull(result.getNewMatrix(), "New matrix should not be null");
        assertEquals(board.length, result.getNewMatrix().length, "Matrix height should remain the same");
    }

    @Test
    void testCheckRemoving_OneFullRow() {
        // Arrange: Board with one complete row
        int[][] board = new int[][]{
                {0, 0, 0, 0, 0},
                {1, 1, 1, 1, 1}, // Full row
                {0, 0, 0, 0, 0},
                {1, 0, 1, 0, 1}
        };

        // Act
        ClearRow result = MatrixOperations.checkRemoving(board);

        // Assert
        assertEquals(1, result.getLinesRemoved(), "One row should be removed");
        assertEquals(BASE_SCORE_PER_LINE * 1 * 1, result.getScoreBonus(), 
                "Score bonus should be 50 for one row (50 * 1^2)");
        
        // Verify the full row is removed and remaining rows fall down
        int[][] newMatrix = result.getNewMatrix();
        assertEquals(board.length, newMatrix.length, "Matrix height should remain the same");
        
        // Top row should now be empty (rows shifted down)
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
        
        // Verify both full rows are removed
        int[][] newMatrix = result.getNewMatrix();
        // Top two rows should be empty
        boolean topTwoRowsEmpty = true;
        for (int i = 0; i < 2; i++) {
            for (int j = 0; j < newMatrix[i].length; j++) {
                if (newMatrix[i][j] != 0) {
                    topTwoRowsEmpty = false;
                    break;
                }
            }
        }
        assertTrue(topTwoRowsEmpty, "Top two rows should be empty after removing two full rows");
    }

    @Test
    void testCheckRemoving_ThreeFullRows() {
        // Arrange: Board with three complete rows
        int[][] board = new int[][]{
                {1, 1, 1, 1, 1}, // Full row 1
                {2, 2, 2, 2, 2}, // Full row 2
                {3, 3, 3, 3, 3}, // Full row 3
                {1, 0, 1, 0, 1}
        };

        // Act
        ClearRow result = MatrixOperations.checkRemoving(board);

        // Assert
        assertEquals(3, result.getLinesRemoved(), "Three rows should be removed");
        assertEquals(BASE_SCORE_PER_LINE * 3 * 3, result.getScoreBonus(), 
                "Score bonus should be 450 for three rows (50 * 3^2)");
    }

    @Test
    void testCheckRemoving_FourFullRows() {
        // Arrange: Board with four complete rows (Tetris!)
        int[][] board = new int[][]{
                {1, 1, 1, 1, 1}, // Full row 1
                {2, 2, 2, 2, 2}, // Full row 2
                {3, 3, 3, 3, 3}, // Full row 3
                {4, 4, 4, 4, 4}  // Full row 4
        };

        // Act
        ClearRow result = MatrixOperations.checkRemoving(board);

        // Assert
        assertEquals(4, result.getLinesRemoved(), "Four rows should be removed");
        assertEquals(BASE_SCORE_PER_LINE * 4 * 4, result.getScoreBonus(), 
                "Score bonus should be 800 for four rows (50 * 4^2)");
    }

    @Test
    void testCheckRemoving_AllRowsFull() {
        // Arrange: All rows are full
        int[][] board = new int[][]{
                {1, 1, 1, 1, 1},
                {2, 2, 2, 2, 2},
                {3, 3, 3, 3, 3}
        };

        // Act
        ClearRow result = MatrixOperations.checkRemoving(board);

        // Assert
        assertEquals(3, result.getLinesRemoved(), "All three rows should be removed");
        assertEquals(BASE_SCORE_PER_LINE * 3 * 3, result.getScoreBonus(), 
                "Score bonus should be 450 for three rows");
        
        // All rows should be empty after clearing
        int[][] newMatrix = result.getNewMatrix();
        boolean allRowsEmpty = true;
        for (int i = 0; i < newMatrix.length; i++) {
            for (int j = 0; j < newMatrix[i].length; j++) {
                if (newMatrix[i][j] != 0) {
                    allRowsEmpty = false;
                    break;
                }
            }
        }
        assertTrue(allRowsEmpty, "All rows should be empty after clearing all full rows");
    }

    @Test
    void testCheckRemoving_NonConsecutiveFullRows() {
        // Arrange: Full rows separated by non-full rows
        int[][] board = new int[][]{
                {1, 1, 1, 1, 1}, // Full row
                {0, 0, 0, 0, 0}, // Empty row
                {2, 2, 2, 2, 2}, // Full row
                {1, 0, 1, 0, 1}  // Partial row
        };

        // Act
        ClearRow result = MatrixOperations.checkRemoving(board);

        // Assert
        assertEquals(2, result.getLinesRemoved(), "Two non-consecutive full rows should be removed");
        assertEquals(BASE_SCORE_PER_LINE * 2 * 2, result.getScoreBonus(), 
                "Score bonus should be 200 for two rows");
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
        // Row 0 should be empty
        assertEquals(0, newMatrix[0][0], "Top row should be empty after shift");
        // Row 1 should contain what was in row 0
        assertEquals(0, newMatrix[1][0], "Second row should contain original top row");
        // Row 2 should contain what was in row 1 (original row 1 shifts down)
        assertEquals(1, newMatrix[2][0], "Third row should contain original row 1 (which shifted down)");
        // Row 3 should contain what was in row 3 (original bottom row)
        assertEquals(3, newMatrix[3][0], "Bottom row should contain original row 3");
    }

    // ==================== merge() Tests ====================

    @Test
    void testMerge_IntoEmptyBoard() {
        // Arrange: Empty board and a brick
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
        assertNotNull(result, "Merged matrix should not be null");
        assertEquals(1, result[y][x], "Brick should be merged at position (x, y)");
        assertEquals(1, result[y][x + 1], "Brick should be merged at position (x+1, y)");
        assertEquals(1, result[y + 1][x], "Brick should be merged at position (x, y+1)");
        assertEquals(1, result[y + 1][x + 1], "Brick should be merged at position (x+1, y+1)");
        
        // Original board should not be modified (deep copy)
        assertEquals(0, board[y][x], "Original board should not be modified");
    }

    @Test
    void testMerge_IntoBoardWithExistingBricks() {
        // Arrange: Board with existing bricks
        int[][] board = new int[BOARD_HEIGHT][BOARD_WIDTH];
        board[0][0] = 2;
        board[0][1] = 2;
        
        int[][] brick = new int[][]{
                {1, 1},
                {1, 1}
        };
        int x = 4;
        int y = 5;

        // Act
        int[][] result = MatrixOperations.merge(board, brick, x, y);

        // Assert
        // Existing bricks should remain
        assertEquals(2, result[0][0], "Existing brick at (0,0) should remain");
        assertEquals(2, result[0][1], "Existing brick at (0,1) should remain");
        // New brick should be merged
        assertEquals(1, result[y][x], "New brick should be merged");
    }

    @Test
    void testMerge_TransparentBrick_NoChange() {
        // Arrange: Brick with all zeros (transparent)
        int[][] board = new int[BOARD_HEIGHT][BOARD_WIDTH];
        board[5][4] = 2; // Existing brick
        
        int[][] brick = new int[][]{
                {0, 0},
                {0, 0}
        };
        int x = 4;
        int y = 5;

        // Act
        int[][] result = MatrixOperations.merge(board, brick, x, y);

        // Assert
        // Existing brick should remain unchanged
        assertEquals(2, result[y][x], "Existing brick should remain when merging transparent brick");
    }

    @Test
    void testMerge_EdgeCase_AtXEqualsZero() {
        // Arrange: Merge at left edge
        int[][] board = new int[BOARD_HEIGHT][BOARD_WIDTH];
        int[][] brick = new int[][]{{1}};
        int x = 0;
        int y = 5;

        // Act
        int[][] result = MatrixOperations.merge(board, brick, x, y);

        // Assert
        assertEquals(1, result[y][x], "Brick should merge correctly at x=0");
    }

    @Test
    void testMerge_EdgeCase_AtYEqualsZero() {
        // Arrange: Merge at top row
        int[][] board = new int[BOARD_HEIGHT][BOARD_WIDTH];
        int[][] brick = new int[][]{{1}};
        int x = 4;
        int y = 0;

        // Act
        int[][] result = MatrixOperations.merge(board, brick, x, y);

        // Assert
        assertEquals(1, result[y][x], "Brick should merge correctly at y=0");
    }

    @Test
    void testMerge_EdgeCase_AtBottom() {
        // Arrange: Merge at bottom row
        int[][] board = new int[BOARD_HEIGHT][BOARD_WIDTH];
        int[][] brick = new int[][]{{1}};
        int x = 4;
        int y = BOARD_HEIGHT - 1;

        // Act
        int[][] result = MatrixOperations.merge(board, brick, x, y);

        // Assert
        assertEquals(1, result[y][x], "Brick should merge correctly at bottom row");
    }

    @Test
    void testMerge_DeepCopy_OriginalNotModified() {
        // Arrange
        int[][] board = new int[BOARD_HEIGHT][BOARD_WIDTH];
        int[][] brick = new int[][]{{1}};
        int x = 4;
        int y = 5;

        // Act
        int[][] result = MatrixOperations.merge(board, brick, x, y);
        result[y][x] = 99; // Modify result

        // Assert
        assertEquals(0, board[y][x], "Original board should not be modified");
        assertEquals(99, result[y][x], "Result matrix should be independent copy");
    }

    // ==================== copy() Tests ====================

    @Test
    void testCopy_EmptyMatrix() {
        // Arrange
        int[][] original = new int[BOARD_HEIGHT][BOARD_WIDTH];

        // Act
        int[][] copy = MatrixOperations.copy(original);

        // Assert
        assertNotNull(copy, "Copy should not be null");
        assertEquals(original.length, copy.length, "Copy should have same height");
        assertEquals(original[0].length, copy[0].length, "Copy should have same width");
    }

    @Test
    void testCopy_MatrixWithData() {
        // Arrange
        int[][] original = new int[][]{
                {1, 2, 3},
                {4, 5, 6},
                {7, 8, 9}
        };

        // Act
        int[][] copy = MatrixOperations.copy(original);

        // Assert
        assertArrayEquals(original, copy, "Copy should have same values as original");
    }

    @Test
    void testCopy_DeepCopy_ModifyCopyDoesNotAffectOriginal() {
        // Arrange
        int[][] original = new int[][]{
                {1, 2, 3},
                {4, 5, 6}
        };

        // Act
        int[][] copy = MatrixOperations.copy(original);
        copy[0][0] = 99; // Modify copy

        // Assert
        assertEquals(99, copy[0][0], "Copy should be modified");
        assertEquals(1, original[0][0], "Original should not be affected by copy modification");
    }

    @Test
    void testCopy_IrregularMatrix() {
        // Arrange: Matrix with different row lengths
        int[][] original = new int[3][];
        original[0] = new int[]{1, 2};
        original[1] = new int[]{3, 4, 5};
        original[2] = new int[]{6};

        // Act
        int[][] copy = MatrixOperations.copy(original);

        // Assert
        assertEquals(original.length, copy.length, "Copy should have same number of rows");
        assertEquals(original[0].length, copy[0].length, "First row should have same length");
        assertEquals(original[1].length, copy[1].length, "Second row should have same length");
        assertEquals(original[2].length, copy[2].length, "Third row should have same length");
        assertEquals(1, copy[0][0], "Copy should preserve values");
        assertEquals(5, copy[1][2], "Copy should preserve values");
        assertEquals(6, copy[2][0], "Copy should preserve values");
    }
}

