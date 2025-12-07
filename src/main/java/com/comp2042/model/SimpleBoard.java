package com.comp2042.model;

import com.comp2042.logic.bricks.Brick;
import com.comp2042.logic.bricks.BrickGenerator;
import com.comp2042.logic.bricks.RandomBrickGenerator;
import com.comp2042.manager.SoundManager;
import com.comp2042.model.tetromino.BrickRotator;
import com.comp2042.model.tetromino.NextShapeInfo;
import com.comp2042.util.MatrixOperations;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;

import java.util.List;

public class SimpleBoard implements Board {

    // Movement offsets for brick translation
    /** Horizontal offset for moving brick left (negative direction) */
    private static final int MOVE_LEFT_OFFSET = -1;
    
    /** Horizontal offset for moving brick right (positive direction) */
    private static final int MOVE_RIGHT_OFFSET = 1;
    
    /** Vertical offset for moving brick down */
    private static final int MOVE_DOWN_OFFSET = 1;
    
    /** No movement offset (used when only one axis changes) */
    private static final int NO_MOVEMENT = 0;

    // Brick spawn position
    /** X coordinate (column) where new bricks spawn */
    private static final int BRICK_SPAWN_X = 4;
    
    /** Y coordinate (row) where new bricks spawn */
    private static final int BRICK_SPAWN_Y = 1;

    private final int width;
    private final int height;
    private final BrickGenerator brickGenerator;
    private final BrickRotator brickRotator;
    private int[][] currentGameMatrix;
    private GamePoint currentOffset;
    private final Score score;
    private final IntegerProperty level = new SimpleIntegerProperty(1);
    private int linesClearedTotal = 0;

    public SimpleBoard(int width, int height) {
        this.width = width;
        this.height = height;
        currentGameMatrix = new int[height][width];
        brickGenerator = new RandomBrickGenerator();
        brickRotator = new BrickRotator();
        score = new Score();
    }

    /**
     * Attempts to move the current brick by the specified offsets.
     * Checks for conflicts and updates the current offset only if the move is valid.
     *
     * @param deltaX the horizontal offset (positive = right, negative = left)
     * @param deltaY the vertical offset (positive = down, negative = up)
     * @return true if the move was successful (no conflict), false otherwise
     */
    private boolean tryMove(int deltaX, int deltaY) {
        int[][] currentMatrix = MatrixOperations.copy(currentGameMatrix);
        GamePoint newPosition = currentOffset.translate(deltaX, deltaY);
        boolean conflict = MatrixOperations.intersect(currentMatrix, brickRotator.getCurrentShape(), newPosition.getX(), newPosition.getY());
        if (conflict) {
            return false;
        } else {
            currentOffset = newPosition;
            return true;
        }
    }

    @Override
    public boolean moveBrickDown() {
        return tryMove(NO_MOVEMENT, MOVE_DOWN_OFFSET);
    }

    @Override
    public boolean moveBrickLeft() {
        boolean success = tryMove(MOVE_LEFT_OFFSET, NO_MOVEMENT);
        if (success) {
            SoundManager.getInstance().playSound("move");
        }
        return success;
    }

    @Override
    public boolean moveBrickRight() {
        boolean success = tryMove(MOVE_RIGHT_OFFSET, NO_MOVEMENT);
        if (success) {
            SoundManager.getInstance().playSound("move");
        }
        return success;
    }

    @Override
    public boolean rotateLeftBrick() {
        int[][] currentMatrix = MatrixOperations.copy(currentGameMatrix);
        NextShapeInfo nextShape = brickRotator.getNextShape();
        boolean conflict = MatrixOperations.intersect(currentMatrix, nextShape.getShape(), currentOffset.getX(), currentOffset.getY());
        if (conflict) {
            return false;
        } else {
            brickRotator.setCurrentShape(nextShape.getPosition());
            SoundManager.getInstance().playSound("move");
            return true;
        }
    }

    @Override
    public boolean createNewBrick() {
        Brick currentBrick = brickGenerator.getBrick();
        brickRotator.setBrick(currentBrick);
        currentOffset = new GamePoint(BRICK_SPAWN_X, BRICK_SPAWN_Y);
        return MatrixOperations.intersect(currentGameMatrix, brickRotator.getCurrentShape(), currentOffset.getX(), currentOffset.getY());
    }

    @Override
    public int[][] getBoardMatrix() {
        return currentGameMatrix;
    }

    @Override
    public ViewData getViewData() {
        return new ViewData(brickRotator.getCurrentShape(), currentOffset.getX(), currentOffset.getY(), brickGenerator.getNextBrick().getShapeMatrix().get(0));
    }

    @Override
    public void mergeBrickToBackground() {
        currentGameMatrix = MatrixOperations.merge(currentGameMatrix, brickRotator.getCurrentShape(), currentOffset.getX(), currentOffset.getY());
        SoundManager.getInstance().playSound("land");
    }

    @Override
    public ClearRow clearRows() {
        ClearRow clearRow = MatrixOperations.checkRemoving(currentGameMatrix);
        currentGameMatrix = clearRow.getNewMatrix();
        
        // Update level based on lines cleared
        linesClearedTotal += clearRow.getLinesRemoved();
        int newLevel = (linesClearedTotal / 10) + 1;
        if (newLevel > level.get()) {
            level.set(newLevel);
        }
        
        return clearRow;

    }

    @Override
    public Score getScore() {
        return score;
    }


    @Override
    public void newGame() {
        currentGameMatrix = new int[height][width];
        score.reset();
        level.set(1);
        linesClearedTotal = 0;
        createNewBrick();
    }
    
    /**
     * Returns the level property for binding to UI.
     * Level starts at 1 and increments every 10 cleared lines.
     * 
     * @return the IntegerProperty representing the current level
     */
    public IntegerProperty levelProperty() {
        return level;
    }
    
    @Override
    public List<Brick> getNextBricks(int count) {
        return brickGenerator.getNextBricks(count);
    }
    
    @Override
    public int getGhostPieceY() {
        if (currentOffset == null) {
            return BRICK_SPAWN_Y;
        }
        
        // Save the current position values (not the reference, since GamePoint is immutable)
        int savedX = currentOffset.getX();
        int savedY = currentOffset.getY();
        
        // Simulate dropping the brick by repeatedly calling moveBrickDown() until it returns false
        // This is the EXACT same logic that determines where the brick will land
        // Keep moving down until we can't move anymore
        while (moveBrickDown()) {
            // Keep moving down - ghostY will be updated to currentOffset.getY() after each successful move
        }
        
        // After the loop, currentOffset.getY() is the landing position (where brick can't move down)
        int ghostY = currentOffset.getY();
        
        // Restore the original position (we don't want to actually move the brick)
        currentOffset = new GamePoint(savedX, savedY);
        
        return ghostY;
    }
    
    @Override
    public int hardDrop() {
        if (currentOffset == null) {
            return 0;
        }
        
        // Save the starting Y position to calculate how many rows we dropped
        int startY = currentOffset.getY();
        
        // Drop the brick down until it can't move anymore
        // This is the same logic as getGhostPieceY(), but we actually move the brick
        while (moveBrickDown()) {
            // Keep moving down until we can't move anymore
        }
        
        // Calculate how many rows the brick was dropped
        int rowsDropped = currentOffset.getY() - startY;
        
        return rowsDropped;
    }
}

