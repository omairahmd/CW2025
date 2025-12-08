package com.comp2042.view;

import com.comp2042.model.GameMode;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

/**
 * Instructions panel that appears after selecting a game mode.
 * Shows mode-specific instructions before starting the game.
 * Matches the jungle theme with dark background and gold text.
 */
public class InstructionsPanel extends StackPane {
    
    private Button startButton;
    private Button backButton;
    private Label titleLabel;
    private Label instructionsLabel;
    
    public InstructionsPanel(GameMode mode) {
        // Set semi-transparent black background
        this.setStyle("-fx-background-color: rgba(0, 0, 0, 0.7);");
        
        // Create central VBox with content - same size as settings panel
        VBox contentBox = new VBox(25);
        contentBox.setAlignment(Pos.CENTER);
        contentBox.getStyleClass().add("instructions-panel-content");
        contentBox.setPadding(new javafx.geometry.Insets(40, 30, 40, 30));
        
        // Title - Mode Name
        String modeName = getModeName(mode);
        titleLabel = new Label(modeName);
        if (mode == GameMode.CLASSIC) {
            titleLabel.getStyleClass().add("instructions-title");
        } else {
            titleLabel.getStyleClass().add("instructions-title-small");
        }
        
        // Instructions - Mode-specific text
        String instructions = getInstructions(mode);
        instructionsLabel = new Label(instructions);
        if (mode == GameMode.CLASSIC) {
            instructionsLabel.getStyleClass().add("instructions-text");
        } else {
            instructionsLabel.getStyleClass().add("instructions-text-small");
        }
        
        // Buttons - smaller size for instructions panel
        startButton = new Button("START");
        startButton.getStyleClass().add("jungle-button");
        startButton.getStyleClass().add("instructions-button");
        
        backButton = new Button("BACK");
        backButton.getStyleClass().add("jungle-button");
        backButton.getStyleClass().add("instructions-button");
        
        // Add all elements to VBox
        contentBox.getChildren().addAll(
            titleLabel,
            instructionsLabel,
            startButton,
            backButton
        );
        
        // Add VBox to StackPane (centered)
        this.getChildren().add(contentBox);
        StackPane.setAlignment(contentBox, Pos.CENTER);
    }
    
    /**
     * Gets the display name for the game mode.
     * 
     * @param mode the game mode
     * @return the display name
     */
    private String getModeName(GameMode mode) {
        switch (mode) {
            case CLASSIC:
                return "CLASSIC";
            case OVERGROWTH:
                return "JUNGLE\nOVERGROWTH";
            case TREASURE_HUNT:
                return "TREASURE\nHUNT";
            default:
                return "CLASSIC";
        }
    }
    
    /**
     * Gets the instructions text for the game mode.
     * 
     * @param mode the game mode
     * @return the instructions text
     */
    private String getInstructions(GameMode mode) {
        switch (mode) {
            case CLASSIC:
                return "Clear lines to score points.\nSpeed increases every 10 lines.";
            case OVERGROWTH:
                return "SURVIVAL MODE\nThe jungle is alive!\nVines grow from the bottom every 10 seconds.\nClear lines fast to survive!";
            case TREASURE_HUNT:
                return "OBJECTIVE MODE\nFind the lost gold!\nThe bottom is filled with dirt.\nClear lines to dig.\nCollect all Gold Blocks to WIN!";
            default:
                return "Clear lines to score points.\nSpeed increases every 10 lines.";
        }
    }
    
    /**
     * Sets the action for the Start Game button.
     * 
     * @param action the Runnable to execute when Start Game is clicked
     */
    public void setOnStart(Runnable action) {
        if (startButton != null) {
            startButton.setOnAction(e -> action.run());
        }
    }
    
    /**
     * Sets the action for the Back button.
     * 
     * @param action the Runnable to execute when Back is clicked
     */
    public void setOnBack(Runnable action) {
        if (backButton != null) {
            backButton.setOnAction(e -> action.run());
        }
    }
}

