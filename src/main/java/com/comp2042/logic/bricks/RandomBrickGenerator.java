package com.comp2042.logic.bricks;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Generates random bricks using the Factory Design Pattern.
 * Uses BrickFactory to create brick instances based on random type selection.
 */
public class RandomBrickGenerator implements BrickGenerator {

    private final Deque<Brick> nextBricks = new ArrayDeque<>();

    /**
     * Constructs a new RandomBrickGenerator.
     * Initializes the generator with two random bricks in the queue.
     */
    public RandomBrickGenerator() {
        nextBricks.add(BrickFactory.getBrick(generateRandomBrickType()));
        nextBricks.add(BrickFactory.getBrick(generateRandomBrickType()));
    }

    /**
     * Generates a random brick type integer in the range [0, 6].
     * 
     * @return a random integer representing a brick type
     */
    private int generateRandomBrickType() {
        return ThreadLocalRandom.current().nextInt(BrickFactory.TOTAL_BRICK_TYPES);
    }

    @Override
    public Brick getBrick() {
        if (nextBricks.size() <= 1) {
            nextBricks.add(BrickFactory.getBrick(generateRandomBrickType()));
        }
        return nextBricks.poll();
    }

    @Override
    public Brick getNextBrick() {
        return nextBricks.peek();
    }
}
