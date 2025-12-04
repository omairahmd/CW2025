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
     * Initializes the generator with two random bricks in the queue using BrickFactory.
     */
    public RandomBrickGenerator() {
        nextBricks.add(BrickFactory.getBrick(ThreadLocalRandom.current().nextInt(BrickFactory.TOTAL_BRICK_TYPES)));
        nextBricks.add(BrickFactory.getBrick(ThreadLocalRandom.current().nextInt(BrickFactory.TOTAL_BRICK_TYPES)));
    }

    @Override
    public Brick getBrick() {
        if (nextBricks.size() <= 1) {
            nextBricks.add(BrickFactory.getBrick(ThreadLocalRandom.current().nextInt(BrickFactory.TOTAL_BRICK_TYPES)));
        }
        return nextBricks.poll();
    }

    @Override
    public Brick getNextBrick() {
        return nextBricks.peek();
    }
}
