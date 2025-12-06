package com.comp2042.events;

import com.comp2042.model.DownData;
import com.comp2042.model.ViewData;

public interface InputEventListener {

    DownData onDownEvent(MoveEvent event);

    ViewData onLeftEvent(MoveEvent event);

    ViewData onRightEvent(MoveEvent event);

    ViewData onRotateEvent(MoveEvent event);
    
    /**
     * Handles a hard drop event (instant drop to bottom).
     * 
     * @param event the move event that triggered the hard drop
     * @return DownData containing information about the drop and any cleared rows
     */
    DownData onHardDropEvent(MoveEvent event);

    void createNewGame();
}

