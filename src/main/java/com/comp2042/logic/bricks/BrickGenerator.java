package com.comp2042.logic.bricks;

import java.util.List;

public interface BrickGenerator {

    Brick getBrick();

    Brick getNextBrick();
    
    /**
     * Gets the next N bricks without removing them from the queue.
     * Used for displaying upcoming bricks in the UI.
     * 
     * @param count the number of next bricks to retrieve
     * @return a list of the next N bricks (may contain fewer if queue is small)
     */
    List<Brick> getNextBricks(int count);
}
