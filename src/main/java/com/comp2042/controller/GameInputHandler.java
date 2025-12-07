package com.comp2042.controller;

import com.comp2042.events.InputEventListener;
import com.comp2042.events.MoveEvent;
import com.comp2042.events.EventSource;
import com.comp2042.events.EventType;
import com.comp2042.model.ViewData;
import javafx.beans.property.BooleanProperty;
import javafx.event.EventHandler;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;

import java.util.function.Consumer;

/**
 * Handles keyboard input for the Tetris game.
 * Processes arrow keys, WASD keys, and the new game key (N).
 */
public class GameInputHandler {

    private final GridPane gamePanel;
    private InputEventListener eventListener;
    private final BooleanProperty isPause;
    private final BooleanProperty isGameOver;
    private final Consumer<ViewData> refreshBrickCallback;
    private final Consumer<MoveEvent> moveDownCallback;
    private final Consumer<MoveEvent> hardDropCallback;
    private final Runnable newGameCallback;

    /**
     * Creates a new GameInputHandler.
     *
     * @param gamePanel the GridPane to attach key event handlers to
     * @param eventListener the InputEventListener to process game events
     * @param isPause the BooleanProperty indicating if the game is paused
     * @param isGameOver the BooleanProperty indicating if the game is over
     * @param refreshBrickCallback callback to refresh the brick display
     * @param moveDownCallback callback to handle down movement
     * @param hardDropCallback callback to handle hard drop
     * @param newGameCallback callback to start a new game
     */
    public GameInputHandler(
            GridPane gamePanel,
            InputEventListener eventListener,
            BooleanProperty isPause,
            BooleanProperty isGameOver,
            Consumer<ViewData> refreshBrickCallback,
            Consumer<MoveEvent> moveDownCallback,
            Consumer<MoveEvent> hardDropCallback,
            Runnable newGameCallback) {
        this.gamePanel = gamePanel;
        this.eventListener = eventListener;
        this.isPause = isPause;
        this.isGameOver = isGameOver;
        this.refreshBrickCallback = refreshBrickCallback;
        this.moveDownCallback = moveDownCallback;
        this.hardDropCallback = hardDropCallback;
        this.newGameCallback = newGameCallback;
    }

    /**
     * Updates the event listener reference.
     * This is called after the event listener is set in GuiController.
     *
     * @param eventListener the new event listener to use
     */
    public void setEventListener(InputEventListener eventListener) {
        this.eventListener = eventListener;
    }

    /**
     * Initializes keyboard input handlers for game controls.
     * Sets up event handlers for arrow keys, WASD keys, and the new game key (N).
     */
    public void initialize() {
        gamePanel.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent keyEvent) {
                if (isPause.getValue() == Boolean.FALSE && isGameOver.getValue() == Boolean.FALSE) {
                    if (keyEvent.getCode() == KeyCode.LEFT || keyEvent.getCode() == KeyCode.A) {
                        if (eventListener != null) {
                            refreshBrickCallback.accept(eventListener.onLeftEvent(new MoveEvent(EventType.LEFT, EventSource.USER)));
                        }
                        keyEvent.consume();
                    }
                    if (keyEvent.getCode() == KeyCode.RIGHT || keyEvent.getCode() == KeyCode.D) {
                        if (eventListener != null) {
                            refreshBrickCallback.accept(eventListener.onRightEvent(new MoveEvent(EventType.RIGHT, EventSource.USER)));
                        }
                        keyEvent.consume();
                    }
                    if (keyEvent.getCode() == KeyCode.UP || keyEvent.getCode() == KeyCode.W) {
                        if (eventListener != null) {
                            refreshBrickCallback.accept(eventListener.onRotateEvent(new MoveEvent(EventType.ROTATE, EventSource.USER)));
                        }
                        keyEvent.consume();
                    }
                    if (keyEvent.getCode() == KeyCode.DOWN || keyEvent.getCode() == KeyCode.S) {
                        moveDownCallback.accept(new MoveEvent(EventType.DOWN, EventSource.USER));
                        keyEvent.consume();
                    }
                    if (keyEvent.getCode() == KeyCode.SPACE) {
                        // Hard drop - instantly drop the brick to the bottom
                        hardDropCallback.accept(new MoveEvent(EventType.DOWN, EventSource.USER));
                        keyEvent.consume();
                    }
                }
                if (keyEvent.getCode() == KeyCode.N) {
                    newGameCallback.run();
                }
                // Handle ESC key for pause/unpause
                if (keyEvent.getCode() == KeyCode.ESCAPE) {
                    // Toggle pause state - this will be handled by GuiController
                    // We need to pass a callback for pause toggle
                    if (pauseToggleCallback != null) {
                        pauseToggleCallback.run();
                    }
                    keyEvent.consume();
                }
            }
        });
    }
    
    private Runnable pauseToggleCallback;
    
    /**
     * Sets the callback to be invoked when ESC key is pressed to toggle pause.
     * 
     * @param callback the Runnable to execute when pause is toggled
     */
    public void setPauseToggleCallback(Runnable callback) {
        this.pauseToggleCallback = callback;
    }
}

