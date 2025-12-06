package com.comp2042.model;

import com.comp2042.logic.bricks.Brick;
import com.comp2042.model.ClearRow;
import com.comp2042.model.Score;
import com.comp2042.model.ViewData;
import javafx.beans.property.IntegerProperty;

import java.util.List;

public interface Board {

    boolean moveBrickDown();

    boolean moveBrickLeft();

    boolean moveBrickRight();

    boolean rotateLeftBrick();

    boolean createNewBrick();

    int[][] getBoardMatrix();

    ViewData getViewData();

    void mergeBrickToBackground();

    ClearRow clearRows();

    Score getScore();

    void newGame();
    
    /**
     * Gets the next N bricks that will appear.
     * Used for displaying upcoming bricks in the UI.
     * 
     * @param count the number of next bricks to retrieve
     * @return a list of the next N bricks
     */
    List<Brick> getNextBricks(int count);
    
    /**
     * Calculates the Y position where the current brick will land (ghost piece position).
     * Drops the brick down until it hits the bottom or another block.
     * 
     * @return the Y coordinate where the brick will land, or the current Y if already at bottom
     */
    int getGhostPieceY();
    
    /**
     * Instantly drops the current brick to the bottom (hard drop).
     * Moves the brick down until it can no longer move, then returns true.
     * The brick will be at its landing position after this call.
     * 
     * @return the number of rows the brick was dropped (for scoring purposes)
     */
    int hardDrop();
    
    /**
     * Returns the level property for binding to UI.
     * Level starts at 1 and increments every 10 cleared lines.
     * 
     * @return the IntegerProperty representing the current level
     */
    IntegerProperty levelProperty();
}

