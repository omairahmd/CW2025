package com.comp2042.logic.bricks;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.Iterator;
import java.util.List;
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
    
    @Override
    public List<Brick> getNextBricks(int count) {
        // Ensure we have enough bricks in the queue
        while (nextBricks.size() < count) {
            nextBricks.add(BrickFactory.getBrick(ThreadLocalRandom.current().nextInt(BrickFactory.TOTAL_BRICK_TYPES)));
        }
        
        // Return the next N bricks without removing them
        List<Brick> result = new ArrayList<>();
        Iterator<Brick> iterator = nextBricks.iterator();
        for (int i = 0; i < count && iterator.hasNext(); i++) {
            result.add(iterator.next());
        }
        return result;
    }
}
