package com.comp2042.view;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

/**
 * Settings panel for adjusting game settings like music and SFX volume.
 * Matches the jungle theme with dark background and gold text.
 */
public class SettingsPanel extends VBox {
    
    private Slider musicVolumeSlider;
    private Slider sfxVolumeSlider;
    private Button backButton;
    
    public SettingsPanel() {
        // Set jungle-themed background
        this.getStyleClass().add("settings-panel");
        this.setAlignment(Pos.CENTER);
        this.setSpacing(30);
        this.setPadding(new Insets(40, 20, 40, 20));
        
        // "SETTINGS" title
        Label settingsTitle = new Label("SETTINGS");
        settingsTitle.getStyleClass().add("settings-title");
        
        // Music Volume Control
        VBox musicBox = new VBox(10);
        musicBox.setAlignment(Pos.CENTER);
        Label musicLabel = new Label("Music Volume");
        musicLabel.getStyleClass().add("settings-label");
        
        musicVolumeSlider = new Slider(0, 100, 50);
        musicVolumeSlider.getStyleClass().add("jungle-slider");
        musicVolumeSlider.setShowTickLabels(true);
        musicVolumeSlider.setShowTickMarks(true);
        musicVolumeSlider.setMajorTickUnit(25);
        musicVolumeSlider.setBlockIncrement(5);
        
        musicBox.getChildren().addAll(musicLabel, musicVolumeSlider);
        
        // SFX Volume Control
        VBox sfxBox = new VBox(10);
        sfxBox.setAlignment(Pos.CENTER);
        Label sfxLabel = new Label("SFX Volume");
        sfxLabel.getStyleClass().add("settings-label");
        
        sfxVolumeSlider = new Slider(0, 100, 50);
        sfxVolumeSlider.getStyleClass().add("jungle-slider");
        sfxVolumeSlider.setShowTickLabels(true);
        sfxVolumeSlider.setShowTickMarks(true);
        sfxVolumeSlider.setMajorTickUnit(25);
        sfxVolumeSlider.setBlockIncrement(5);
        
        sfxBox.getChildren().addAll(sfxLabel, sfxVolumeSlider);
        
        // Back button
        backButton = new Button("BACK");
        backButton.getStyleClass().add("jungle-button");
        
        // Add all elements to main VBox
        this.getChildren().addAll(
            settingsTitle,
            musicBox,
            sfxBox,
            backButton
        );
    }
    
    /**
     * Gets the music volume slider value.
     * 
     * @return the music volume (0-100)
     */
    public double getMusicVolume() {
        return musicVolumeSlider != null ? musicVolumeSlider.getValue() : 50.0;
    }
    
    /**
     * Sets the music volume slider value.
     * 
     * @param volume the music volume (0-100)
     */
    public void setMusicVolume(double volume) {
        if (musicVolumeSlider != null) {
            musicVolumeSlider.setValue(volume);
        }
    }
    
    /**
     * Gets the SFX volume slider value.
     * 
     * @return the SFX volume (0-100)
     */
    public double getSfxVolume() {
        return sfxVolumeSlider != null ? sfxVolumeSlider.getValue() : 50.0;
    }
    
    /**
     * Sets the SFX volume slider value.
     * 
     * @param volume the SFX volume (0-100)
     */
    public void setSfxVolume(double volume) {
        if (sfxVolumeSlider != null) {
            sfxVolumeSlider.setValue(volume);
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
    
    /**
     * Adds a listener to the music volume slider.
     * 
     * @param listener the listener to add
     */
    public void addMusicVolumeListener(javafx.beans.value.ChangeListener<Number> listener) {
        if (musicVolumeSlider != null) {
            musicVolumeSlider.valueProperty().addListener(listener);
        }
    }
    
    /**
     * Adds a listener to the SFX volume slider.
     * 
     * @param listener the listener to add
     */
    public void addSfxVolumeListener(javafx.beans.value.ChangeListener<Number> listener) {
        if (sfxVolumeSlider != null) {
            sfxVolumeSlider.valueProperty().addListener(listener);
        }
    }
}

