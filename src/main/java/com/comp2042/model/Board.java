package com.comp2042.model;

import com.comp2042.logic.bricks.Brick;
import com.comp2042.model.ClearRow;
import com.comp2042.model.Score;
import com.comp2042.model.ViewData;

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
}

