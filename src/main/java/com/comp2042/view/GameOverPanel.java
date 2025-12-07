package com.comp2042.view;

import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

/**
 * Game over panel that appears when the game ends.
 * Matches the jungle theme with dark background and gold text.
 */
public class GameOverPanel extends VBox {
    
    private Button newGameButton;
    private Button mainMenuButton;
    
    public GameOverPanel() {
        // Set jungle-themed background
        this.getStyleClass().add("game-over-panel");
        this.setAlignment(Pos.CENTER);
        this.setSpacing(30);
        this.setPadding(new javafx.geometry.Insets(40, 30, 40, 30));
        
        // "GAME OVER" title
        Label gameOverLabel = new Label("GAME OVER");
        gameOverLabel.getStyleClass().add("game-over-title");
        
        // Buttons
        newGameButton = new Button("NEW GAME");
        newGameButton.getStyleClass().add("jungle-button");
        newGameButton.getStyleClass().add("main-menu-button"); // Same font size as MAIN MENU
        
        mainMenuButton = new Button("MAIN MENU");
        mainMenuButton.getStyleClass().add("jungle-button");
        mainMenuButton.getStyleClass().add("main-menu-button");
        
        // Add all elements to VBox
        this.getChildren().addAll(
            gameOverLabel,
            newGameButton,
            mainMenuButton
        );
    }
    
    /**
     * Sets the action for the New Game button.
     * 
     * @param action the Runnable to execute when New Game is clicked
     */
    public void setOnNewGame(Runnable action) {
        if (newGameButton != null) {
            newGameButton.setOnAction(e -> action.run());
        }
    }
    
    /**
     * Sets the action for the Main Menu button.
     * 
     * @param action the Runnable to execute when Main Menu is clicked
     */
    public void setOnMainMenu(Runnable action) {
        if (mainMenuButton != null) {
            mainMenuButton.setOnAction(e -> action.run());
        }
    }
}

