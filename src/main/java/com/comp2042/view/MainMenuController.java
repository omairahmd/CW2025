package com.comp2042.view;

import com.comp2042.model.GameMode;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

public class MainMenuController implements Initializable {

    @FXML
    private MediaView videoBackground;

    @FXML
    private javafx.scene.layout.Region fallbackBackground;

    @FXML
    private javafx.scene.layout.VBox buttonsOverlay;

    @FXML
    private Button startButton;

    @FXML
    private Button settingsButton;

    @FXML
    private Button quitButton;
    
    @FXML
    private javafx.scene.layout.StackPane settingsPanelContainer;
    
    @FXML
    private javafx.scene.layout.StackPane modeSelectionPanelContainer;
    
    @FXML
    private javafx.scene.layout.StackPane instructionsPanelContainer;

    private MediaPlayer mediaPlayer;
    private Stage primaryStage;
    private SettingsPanel settingsPanel;
    private ModeSelectionPanel modeSelectionPanel;
    private InstructionsPanel instructionsPanel;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Load the Modern Tetris font
        try {
            javafx.scene.text.Font.loadFont(
                getClass().getClassLoader().getResource("modern-tetris.otf").toExternalForm(),
                22
            );
        } catch (Exception e) {
            System.err.println("Error loading Modern Tetris font: " + e.getMessage());
            e.printStackTrace();
        }
        
        // Load and play the video background
        try {
            // Try different possible file names (new file first, then fallbacks)
            String[] possibleNames = {
                "MainMenuBackground.mp4",  // Main menu background video
                "Video_Generation_With_Specific_Requirements.mp4",  // Previous video
                "MainMenuVideo.mp4",  // Older file
                "main_menu_video.mp4",
                "main menu video.mp4",
                "main%20menu%20video.mp4"
            };
            
            URL videoUrl = null;
            for (String name : possibleNames) {
                videoUrl = getClass().getClassLoader().getResource(name);
                if (videoUrl != null) {
                    break;
                }
            }
            
            if (videoUrl == null) {
                // Try direct file path
                videoUrl = getClass().getResource("/MainMenuBackground.mp4");
                if (videoUrl == null) {
                    videoUrl = getClass().getResource("/Video_Generation_With_Specific_Requirements.mp4");
                }
                if (videoUrl == null) {
                    videoUrl = getClass().getResource("/MainMenuVideo.mp4");
                }
                if (videoUrl == null) {
                    videoUrl = getClass().getResource("/main menu video.mp4");
                }
            }
            
            if (videoUrl != null) {
                String videoPath = videoUrl.toExternalForm();
                
                if (videoPath.startsWith("file:/") && !videoPath.startsWith("file:///")) {
                    videoPath = videoPath.replace("file:/", "file:///");
                }
                
                // URL encode spaces for proper Media loading
                if (videoPath.contains(" ")) {
                    videoPath = videoPath.replace(" ", "%20");
                }
                
                try {
                    // Try to load the media with better error handling
                    Media media = new Media(videoPath);
                    
                    // Check if media is valid before creating player
                    media.getMetadata().addListener((javafx.collections.MapChangeListener<String, Object>) change -> {
                        // metadata listener retained intentionally for potential future logging
                    });
                
                    mediaPlayer = new MediaPlayer(media);
                    mediaPlayer.setCycleCount(MediaPlayer.INDEFINITE); // Loop the video
                    mediaPlayer.setMute(true); // Mute the video
                    mediaPlayer.setAutoPlay(true);
                    
                    videoBackground.setMediaPlayer(mediaPlayer);
                    
                    // Set video to visible immediately (it will show when playing)
                    videoBackground.setVisible(true);
                    
                    // Show video when it's ready
                    mediaPlayer.setOnReady(() -> {
                        javafx.application.Platform.runLater(() -> {
                            videoBackground.setVisible(true);
                            // Hide fallback background when video is playing
                            if (fallbackBackground != null) {
                                fallbackBackground.setVisible(false);
                            }
                            // Ensure buttons stay on top
                            if (buttonsOverlay != null) {
                                buttonsOverlay.toFront();
                            }
                            if (mediaPlayer.getStatus() == MediaPlayer.Status.READY) {
                                mediaPlayer.play();
                            }
                        });
                    });
                    
                    // Handle errors gracefully - fail silently and use fallback background
                    mediaPlayer.setOnError(() -> {
                        // Silently fail and use fallback background (video codec not supported)
                        javafx.application.Platform.runLater(() -> {
                            videoBackground.setVisible(false);
                            // Show fallback background on error
                            if (fallbackBackground != null) {
                                fallbackBackground.setVisible(true);
                            }
                            // Stop and dispose the media player on error to prevent memory leaks
                            if (mediaPlayer != null) {
                                try {
                                    mediaPlayer.stop();
                                    mediaPlayer.dispose();
                                } catch (Exception e) {
                                    // Ignore disposal errors
                                }
                                mediaPlayer = null;
                            }
                        });
                    });
                    
                    // Add error listener to Media object as well (catches errors before player is created)
                    media.errorProperty().addListener((obs, oldError, newError) -> {
                        if (newError != null) {
                            // Silently fail and use fallback background
                            javafx.application.Platform.runLater(() -> {
                                videoBackground.setVisible(false);
                                if (fallbackBackground != null) {
                                    fallbackBackground.setVisible(true);
                                }
                                // Don't try to play if media has errors
                                if (mediaPlayer != null) {
                                    try {
                                        mediaPlayer.stop();
                                        mediaPlayer.dispose();
                                    } catch (Exception e) {
                                        // Ignore disposal errors
                                    }
                                    mediaPlayer = null;
                                }
                            });
                        }
                    });
                    
                    // Log status changes (only log successful playback, not errors)
                    mediaPlayer.statusProperty().addListener((obs, oldStatus, newStatus) -> {
                        // Only handle successful playback states, ignore DISPOSED and error states
                        if (newStatus == MediaPlayer.Status.PLAYING) {
                            javafx.application.Platform.runLater(() -> {
                                videoBackground.setVisible(true);
                                // Hide fallback background when video is playing
                                if (fallbackBackground != null) {
                                    fallbackBackground.setVisible(false);
                                }
                                // Ensure buttons stay on top
                                if (buttonsOverlay != null) {
                                    buttonsOverlay.toFront();
                                }
                            });
                        } else if (newStatus == MediaPlayer.Status.READY) {
                            javafx.application.Platform.runLater(() -> {
                                videoBackground.setVisible(true);
                                if (fallbackBackground != null) {
                                    fallbackBackground.setVisible(false);
                                }
                                // Ensure buttons stay on top
                                if (buttonsOverlay != null) {
                                    buttonsOverlay.toFront();
                                }
                            });
                        }
                        // Silently ignore DISPOSED and error states (handled by error listeners)
                    });
                } catch (Exception mediaException) {
                    // Silently fail and use fallback background (video codec not supported)
                    videoBackground.setVisible(false);
                    // Show fallback background on error
                    if (fallbackBackground != null) {
                        fallbackBackground.setVisible(true);
                    }
                }
                
            } else {
                // No video file found - use fallback background
                videoBackground.setVisible(false);
                // Show fallback background if video not found
                if (fallbackBackground != null) {
                    fallbackBackground.setVisible(true);
                }
            }
        } catch (Exception e) {
            // Silently fail and use fallback background
            videoBackground.setVisible(false);
            // Show fallback background on error
            if (fallbackBackground != null) {
                fallbackBackground.setVisible(true);
            }
            // Continue without video if it fails to load
        }
        
        // Initialize settings panel
        initializeSettingsPanel();
        
        // Initialize mode selection panel
        initializeModeSelectionPanel();
    }
    
    /**
     * Initializes the settings panel and sets up its actions.
     */
    private void initializeSettingsPanel() {
        if (settingsPanelContainer != null) {
            settingsPanel = new SettingsPanel();
            
            // Set up back button to hide settings and show main menu buttons
            settingsPanel.setOnBack(() -> {
                showMainMenu();
            });
            
            // Add settings panel to container
            settingsPanelContainer.getChildren().add(settingsPanel);
        }
    }
    
    /**
     * Shows the settings panel and hides the main menu buttons.
     */
    private void showSettings() {
        if (settingsPanelContainer != null && buttonsOverlay != null) {
            settingsPanelContainer.setVisible(true);
            settingsPanelContainer.setManaged(true);
            settingsPanelContainer.toFront();
            buttonsOverlay.setVisible(false);
        }
    }
    
    /**
     * Shows the main menu buttons and hides the settings panel.
     */
    private void showMainMenu() {
        if (settingsPanelContainer != null && buttonsOverlay != null) {
            settingsPanelContainer.setVisible(false);
            settingsPanelContainer.setManaged(false);
            buttonsOverlay.setVisible(true);
            buttonsOverlay.toFront();
        }
    }

    /**
     * Sets the primary stage for navigation purposes.
     *
     * @param stage the primary stage
     */
    public void setPrimaryStage(Stage stage) {
        this.primaryStage = stage;
    }

    /**
     * Handles the Start button action.
     * Shows the mode selection panel.
     */
    @FXML
    private void handleStart() {
        showModeSelection();
    }
    
    /**
     * Shows the instructions panel for the selected game mode.
     * 
     * @param mode the game mode
     */
    private void showInstructions(GameMode mode) {
        // Hide mode selection panel
        hideModeSelection();
        
        // Create or update instructions panel
        if (instructionsPanelContainer != null) {
            // Remove existing panel if any
            instructionsPanelContainer.getChildren().removeIf(node -> node instanceof InstructionsPanel);
            
            // Create new instructions panel for this mode
            instructionsPanel = new InstructionsPanel(mode);
            
            // Set up button actions
            instructionsPanel.setOnStart(() -> startGame(mode));
            instructionsPanel.setOnBack(() -> {
                hideInstructions();
                showModeSelection();
            });
            
            // Add to container
            instructionsPanelContainer.getChildren().add(instructionsPanel);
            
            // Show the panel
            instructionsPanelContainer.setVisible(true);
            instructionsPanelContainer.setManaged(true);
            instructionsPanelContainer.toFront();
        }
    }
    
    /**
     * Hides the instructions panel.
     */
    private void hideInstructions() {
        if (instructionsPanelContainer != null) {
            instructionsPanelContainer.setVisible(false);
            instructionsPanelContainer.setManaged(false);
        }
    }
    
    /**
     * Starts the game in the specified mode.
     * 
     * @param mode the game mode to start
     */
    private void startGame(GameMode mode) {
        try {
            // Hide instructions panel
            hideInstructions();
            
            // Hide mode selection panel
            hideModeSelection();
            
            // Stop the video
            if (mediaPlayer != null) {
                mediaPlayer.stop();
            }

            // Load the game layout
            URL location = getClass().getClassLoader().getResource("gameLayout.fxml");
            ResourceBundle resources = null;
            FXMLLoader fxmlLoader = new FXMLLoader(location, resources);
            Parent root = fxmlLoader.load();
            GuiController guiController = fxmlLoader.getController();

            // Update stage to show game
            if (primaryStage != null) {
                primaryStage.setScene(new Scene(root, 500, 510));
                primaryStage.setTitle("TetrisJFX - Game");
                
                // Set primary stage reference in GuiController for navigation
                guiController.setPrimaryStage(primaryStage);
                
                // Initialize the game with selected mode
                new com.comp2042.controller.GameController(guiController, mode);
            }
        } catch (Exception e) {
            System.err.println("Error loading game: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Handles the Settings button action.
     * Shows the settings panel.
     */
    @FXML
    private void handleSettings() {
        showSettings();
    }
    
    /**
     * Initializes the mode selection panel.
     */
    private void initializeModeSelectionPanel() {
        modeSelectionPanel = new ModeSelectionPanel();
        modeSelectionPanel.setOnClassic(() -> showInstructions(GameMode.CLASSIC));
        modeSelectionPanel.setOnOvergrowth(() -> showInstructions(GameMode.OVERGROWTH));
        modeSelectionPanel.setOnTreasureHunt(() -> showInstructions(GameMode.TREASURE_HUNT));
        if (modeSelectionPanelContainer != null) {
            modeSelectionPanelContainer.getChildren().add(modeSelectionPanel);
            modeSelectionPanelContainer.setVisible(false);
            modeSelectionPanelContainer.setManaged(false);
        }
    }
    
    /**
     * Shows the mode selection panel and hides main menu buttons.
     */
    private void showModeSelection() {
        buttonsOverlay.setVisible(false);
        buttonsOverlay.setManaged(false);
        if (modeSelectionPanelContainer != null) {
            modeSelectionPanelContainer.setVisible(true);
            modeSelectionPanelContainer.setManaged(true);
            modeSelectionPanelContainer.toFront();
        }
    }
    
    /**
     * Hides the mode selection panel and shows main menu buttons.
     */
    private void hideModeSelection() {
        if (modeSelectionPanelContainer != null) {
            modeSelectionPanelContainer.setVisible(false);
            modeSelectionPanelContainer.setManaged(false);
        }
        buttonsOverlay.setVisible(true);
        buttonsOverlay.setManaged(true);
        buttonsOverlay.toFront();
    }

    /**
     * Handles the Quit button action.
     * Closes the application.
     */
    @FXML
    private void handleQuit() {
        // Stop the video
        if (mediaPlayer != null) {
            mediaPlayer.stop();
        }
        
        // Close the application
        if (primaryStage != null) {
            primaryStage.close();
        } else {
            System.exit(0);
        }
    }

    /**
     * Cleanup method to stop media player when controller is no longer needed.
     */
    public void cleanup() {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.dispose();
        }
    }
}

