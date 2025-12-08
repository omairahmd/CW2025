package com.comp2042.view;

import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

/**
 * Mode selection panel that appears when the user clicks "Start" in the main menu.
 * Allows the user to choose between CLASSIC and OVERGROWTH game modes.
 */
public class ModeSelectionPanel extends StackPane {

    private Button classicButton;
    private Button overgrowthButton;

    public ModeSelectionPanel() {
        // Semi-transparent black background
        setStyle("-fx-background-color: rgba(0, 0, 0, 0.7);");
        
        // Create central VBox with content - same size as settings panel
        VBox contentBox = new VBox(30);
        contentBox.setAlignment(Pos.CENTER);
        contentBox.getStyleClass().add("mode-selection-panel-content");
        contentBox.setPadding(new javafx.geometry.Insets(40, 20, 40, 20));
        
        // "SELECT MODE" title
        Label titleLabel = new Label("SELECT MODE");
        titleLabel.getStyleClass().add("mode-selection-title");
        
        // Buttons - use CSS for sizing
        classicButton = new Button("CLASSIC");
        classicButton.getStyleClass().add("jungle-button");
        
        overgrowthButton = new Button("jungle\nOvergrowth");
        overgrowthButton.getStyleClass().add("jungle-button");
        overgrowthButton.getStyleClass().add("overgrowth-button"); // Smaller font for longer text
        
        // Add all elements to VBox
        contentBox.getChildren().addAll(
            titleLabel,
            classicButton,
            overgrowthButton
        );
        
        // Add VBox to StackPane (centered)
        this.getChildren().add(contentBox);
        StackPane.setAlignment(contentBox, Pos.CENTER);
    }

    public void setOnClassic(Runnable action) {
        classicButton.setOnAction(e -> action.run());
    }
    
    public void setOnOvergrowth(Runnable action) {
        overgrowthButton.setOnAction(e -> action.run());
    }
}

