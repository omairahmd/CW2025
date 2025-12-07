package com.comp2042.view;

import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

/**
 * Pause overlay panel that appears when the game is paused.
 * Matches the jungle theme with dark background and gold text.
 */
public class PausePanel extends StackPane {
    
    private Button resumeButton;
    private Button settingsButton;
    private Button mainMenuButton;
    
    public PausePanel() {
        // Set semi-transparent black background
        this.setStyle("-fx-background-color: rgba(0, 0, 0, 0.7);");
        
        // Create central VBox with content - same size as settings panel
        VBox contentBox = new VBox(30);
        contentBox.setAlignment(Pos.CENTER);
        contentBox.getStyleClass().add("pause-panel-content");
        contentBox.setPadding(new javafx.geometry.Insets(40, 20, 40, 20));
        
        // "PAUSED" title
        Label pausedLabel = new Label("PAUSED");
        pausedLabel.getStyleClass().add("pause-title");
        
        // Buttons - use CSS for sizing
        resumeButton = new Button("RESUME");
        resumeButton.getStyleClass().add("jungle-button");
        
        settingsButton = new Button("SETTINGS");
        settingsButton.getStyleClass().add("jungle-button");
        
        mainMenuButton = new Button("MAIN MENU");
        mainMenuButton.getStyleClass().add("jungle-button");
        mainMenuButton.getStyleClass().add("main-menu-button");
        
        // Add all elements to VBox
        contentBox.getChildren().addAll(
            pausedLabel,
            resumeButton,
            settingsButton,
            mainMenuButton
        );
        
        // Add VBox to StackPane (centered)
        this.getChildren().add(contentBox);
        StackPane.setAlignment(contentBox, Pos.CENTER);
    }
    
    /**
     * Sets the action for the Resume button.
     * 
     * @param action the Runnable to execute when Resume is clicked
     */
    public void setOnResume(Runnable action) {
        if (resumeButton != null) {
            resumeButton.setOnAction(e -> action.run());
        }
    }
    
    /**
     * Sets the action for the Settings button.
     * 
     * @param action the Runnable to execute when Settings is clicked
     */
    public void setOnSettings(Runnable action) {
        if (settingsButton != null) {
            settingsButton.setOnAction(e -> action.run());
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

