package com.comp2042.model;

import com.comp2042.logic.bricks.Brick;
import com.comp2042.logic.bricks.BrickFactory; // Added Import
import com.comp2042.logic.bricks.BrickGenerator;
import com.comp2042.logic.bricks.RandomBrickGenerator;
import com.comp2042.manager.SoundManager;
import com.comp2042.model.tetromino.BrickRotator;
import com.comp2042.model.tetromino.NextShapeInfo;
import com.comp2042.util.MatrixOperations;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class SimpleBoard implements Board {

    // Movement offsets for brick translation
    private static final int MOVE_LEFT_OFFSET = -1;
    private static final int MOVE_RIGHT_OFFSET = 1;
    private static final int MOVE_DOWN_OFFSET = 1;
    private static final int NO_MOVEMENT = 0;

    // Brick spawn position
    private static final int BRICK_SPAWN_X = 4;
    private static final int BRICK_SPAWN_Y = 1;

    // Treasure Hunt Colors
    private static final int DIRT_COLOR = 8;
    private static final int GOLD_COLOR = 9;
    private static final int VINE_COLOR = 8;

    private final int width;
    private final int height;
    private final BrickGenerator brickGenerator;
    private final BrickRotator brickRotator;
    private int[][] currentGameMatrix;
    private GamePoint currentOffset;
    private final Score score;
    private final IntegerProperty level = new SimpleIntegerProperty(1);
    private int linesClearedTotal = 0;
    private GameMode gameMode = GameMode.CLASSIC;

    public SimpleBoard(int width, int height) {
        this.width = width;
        this.height = height;
        currentGameMatrix = new int[height][width];
        brickGenerator = new RandomBrickGenerator();
        brickRotator = new BrickRotator();
        score = new Score();
    }

    /**
     * Creates a new brick at the spawn position with robust error handling.
     * * @return true if game is over (new brick collides with existing blocks), false otherwise
     * @throws IllegalStateException if brick generation fails critically
     */
    @Override
    public boolean createNewBrick() {
        try {
            Brick currentBrick = brickGenerator.getBrick();

            if (currentBrick == null) {
                // RECOVERY: This shouldn't happen, but if it does, create a default brick
                System.err.println("Warning: Brick generator returned null. Using fallback brick.");
                currentBrick = BrickFactory.getBrick(BrickFactory.BRICK_TYPE_I);
            }

            brickRotator.setBrick(currentBrick);
            currentOffset = new GamePoint(BRICK_SPAWN_X, BRICK_SPAWN_Y);

            // Check if game is over (new brick collides with existing blocks)
            boolean gameOver = MatrixOperations.intersect(
                    currentGameMatrix,
                    brickRotator.getCurrentShape(),
                    currentOffset.getX(),
                    currentOffset.getY()
            );

            if (gameOver) {
                System.out.println("Game Over: New brick cannot be placed at spawn position.");
            }

            return gameOver;

        } catch (IllegalArgumentException e) {
            // RECOVERY: Handle invalid brick type
            System.err.println("Error creating brick: " + e.getMessage());
            System.err.println("Using fallback I-brick to continue game.");

            // Use a safe fallback brick
            Brick fallbackBrick = BrickFactory.getBrick(BrickFactory.BRICK_TYPE_I);
            brickRotator.setBrick(fallbackBrick);
            currentOffset = new GamePoint(BRICK_SPAWN_X, BRICK_SPAWN_Y);

            return MatrixOperations.intersect(
                    currentGameMatrix,
                    brickRotator.getCurrentShape(),
                    currentOffset.getX(),
                    currentOffset.getY()
            );

        } catch (Exception e) {
            // RECOVERY: Last resort - log error and signal game over
            System.err.println("Critical error creating new brick: " + e.getMessage());
            e.printStackTrace();

            // Signal game over to prevent further errors
            throw new IllegalStateException("Unable to create new brick. Game cannot continue.", e);
        }
    }

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

        if (gameMode == GameMode.TREASURE_HUNT) {
            initTreasureField();
        }

        createNewBrick();
    }

    private void initTreasureField() {
        int startRow = height - 8;
        for (int row = startRow; row < height; row++) {
            for (int col = 0; col < width; col++) {
                currentGameMatrix[row][col] = DIRT_COLOR;
            }
        }

        int goldCount = ThreadLocalRandom.current().nextInt(5, 9);
        int placedGold = 0;

        while (placedGold < goldCount) {
            int row = ThreadLocalRandom.current().nextInt(startRow, height);
            int col = ThreadLocalRandom.current().nextInt(width);

            if (currentGameMatrix[row][col] == DIRT_COLOR) {
                currentGameMatrix[row][col] = GOLD_COLOR;
                placedGold++;
            }
        }

        for (int row = startRow; row < height; row++) {
            boolean hasEmpty = false;
            for (int col = 0; col < width; col++) {
                if (currentGameMatrix[row][col] == 0) {
                    hasEmpty = true;
                    break;
                }
            }

            if (!hasEmpty) {
                int col = ThreadLocalRandom.current().nextInt(width);
                if (currentGameMatrix[row][col] == GOLD_COLOR) {
                    for (int c = 0; c < width; c++) {
                        if (currentGameMatrix[row][c] == DIRT_COLOR) {
                            col = c;
                            break;
                        }
                    }
                }
                currentGameMatrix[row][col] = 0;
            }
        }
    }

    public boolean hasRemainingTreasure() {
        for (int row = 0; row < height; row++) {
            for (int col = 0; col < width; col++) {
                if (currentGameMatrix[row][col] == GOLD_COLOR) {
                    return true;
                }
            }
        }
        return false;
    }

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

        int savedX = currentOffset.getX();
        int savedY = currentOffset.getY();

        while (moveBrickDown()) {
            // Loop until collision
        }

        int ghostY = currentOffset.getY();
        currentOffset = new GamePoint(savedX, savedY);
        return ghostY;
    }

    @Override
    public int hardDrop() {
        if (currentOffset == null) {
            return 0;
        }

        int startY = currentOffset.getY();
        while (moveBrickDown()) {
            // Loop until collision
        }
        return currentOffset.getY() - startY;
    }

    @Override
    public void setGameMode(GameMode mode) {
        this.gameMode = mode;
    }

    @Override
    public GameMode getGameMode() {
        return gameMode;
    }

    @Override
    public boolean addVineLine() {
        for (int col = 0; col < width; col++) {
            if (currentGameMatrix[0][col] != 0) {
                return false;
            }
        }

        for (int row = 0; row < height - 1; row++) {
            for (int col = 0; col < width; col++) {
                currentGameMatrix[row][col] = currentGameMatrix[row + 1][col];
            }
        }

        int[] newBottomRow = new int[width];
        for (int col = 0; col < width; col++) {
            newBottomRow[col] = VINE_COLOR;
        }

        int randomHole = ThreadLocalRandom.current().nextInt(width);
        newBottomRow[randomHole] = 0;

        for (int col = 0; col < width; col++) {
            currentGameMatrix[height - 1][col] = newBottomRow[col];
        }

        return true;
    }
}