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
    private Label titleLabel;
    
    public GameOverPanel() {
        // Set jungle-themed background
        this.getStyleClass().add("game-over-panel");
        this.setAlignment(Pos.CENTER);
        this.setSpacing(30);
        this.setPadding(new javafx.geometry.Insets(40, 30, 40, 30));
        
        // "GAME OVER" title
        titleLabel = new Label("GAME OVER");
        titleLabel.getStyleClass().add("game-over-title");
        
        // Buttons
        newGameButton = new Button("NEW GAME");
        newGameButton.getStyleClass().add("jungle-button");
        newGameButton.getStyleClass().add("main-menu-button"); // Same font size as MAIN MENU
        
        mainMenuButton = new Button("MAIN MENU");
        mainMenuButton.getStyleClass().add("jungle-button");
        mainMenuButton.getStyleClass().add("main-menu-button");
        
        // Add all elements to VBox
        this.getChildren().addAll(
            titleLabel,
            newGameButton,
            mainMenuButton
        );
    }
    
    /**
     * Changes the title to "YOU WIN!" with gold color for victory screen.
     */
    public void setVictoryTitle() {
        if (titleLabel != null) {
            titleLabel.setText("YOU WIN!");
            titleLabel.setStyle("-fx-text-fill: #FFD700; -fx-font-size: 32px; -fx-font-family: 'Modern Tetris'; -fx-font-weight: bold;");
        }
    }
    
    /**
     * Resets the title back to "GAME OVER" for normal game over screen.
     */
    public void resetTitle() {
        if (titleLabel != null) {
            titleLabel.setText("GAME OVER");
            titleLabel.getStyleClass().clear();
            titleLabel.getStyleClass().add("game-over-title");
        }
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

