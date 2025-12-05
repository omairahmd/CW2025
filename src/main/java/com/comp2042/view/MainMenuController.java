package com.comp2042.view;

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

    private MediaPlayer mediaPlayer;
    private Stage primaryStage;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Load and play the video background
        try {
            // Try different possible file names (new file first, then fallbacks)
            String[] possibleNames = {
                "MainMenuVideo.mp4",  // New file with proper naming
                "main_menu_video.mp4",
                "main menu video.mp4",
                "main%20menu%20video.mp4"
            };
            
            URL videoUrl = null;
            for (String name : possibleNames) {
                videoUrl = getClass().getClassLoader().getResource(name);
                if (videoUrl != null) {
                    System.out.println("Found video file: " + name);
                    break;
                }
            }
            
            if (videoUrl == null) {
                // Try direct file path
                videoUrl = getClass().getResource("/MainMenuVideo.mp4");
                if (videoUrl == null) {
                    videoUrl = getClass().getResource("/main menu video.mp4");
                }
            }
            
            if (videoUrl != null) {
                String videoPath = videoUrl.toExternalForm();
                System.out.println("Video URL (original): " + videoPath);
                
                // Fix file:/// protocol issue on Windows
                if (videoPath.startsWith("file:/") && !videoPath.startsWith("file:///")) {
                    videoPath = videoPath.replace("file:/", "file:///");
                }
                
                // URL encode spaces for proper Media loading
                if (videoPath.contains(" ")) {
                    videoPath = videoPath.replace(" ", "%20");
                }
                
                System.out.println("Loading media from: " + videoPath);
                
                try {
                    Media media = new Media(videoPath);
                
                    mediaPlayer = new MediaPlayer(media);
                    mediaPlayer.setCycleCount(MediaPlayer.INDEFINITE); // Loop the video
                    mediaPlayer.setMute(true); // Mute the video
                    mediaPlayer.setAutoPlay(true);
                    
                    videoBackground.setMediaPlayer(mediaPlayer);
                    
                    // Set video to visible immediately (it will show when playing)
                    videoBackground.setVisible(true);
                    
                    // Show video when it's ready
                    mediaPlayer.setOnReady(() -> {
                        System.out.println("Video is ready, showing video background");
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
                    
                    // Handle errors gracefully
                    mediaPlayer.setOnError(() -> {
                        System.err.println("Video playback error: " + mediaPlayer.getError());
                        if (mediaPlayer.getError() != null) {
                            System.err.println("Error message: " + mediaPlayer.getError().getMessage());
                            System.err.println("Error type: " + mediaPlayer.getError().getType());
                        }
                        javafx.application.Platform.runLater(() -> {
                            videoBackground.setVisible(false);
                            // Show fallback background on error
                            if (fallbackBackground != null) {
                                fallbackBackground.setVisible(true);
                            }
                        });
                    });
                    
                    // Log status changes
                    mediaPlayer.statusProperty().addListener((obs, oldStatus, newStatus) -> {
                        System.out.println("MediaPlayer status changed: " + oldStatus + " -> " + newStatus);
                        javafx.application.Platform.runLater(() -> {
                            if (newStatus == MediaPlayer.Status.PLAYING) {
                                System.out.println("Video is now playing - making it visible");
                                videoBackground.setVisible(true);
                                // Hide fallback background when video is playing
                                if (fallbackBackground != null) {
                                    fallbackBackground.setVisible(false);
                                }
                                // Ensure buttons stay on top
                                if (buttonsOverlay != null) {
                                    buttonsOverlay.toFront();
                                }
                            } else if (newStatus == MediaPlayer.Status.READY) {
                                videoBackground.setVisible(true);
                                if (fallbackBackground != null) {
                                    fallbackBackground.setVisible(false);
                                }
                                // Ensure buttons stay on top
                                if (buttonsOverlay != null) {
                                    buttonsOverlay.toFront();
                                }
                            }
                        });
                    });
                } catch (Exception mediaException) {
                    System.err.println("Failed to create Media object: " + mediaException.getMessage());
                    mediaException.printStackTrace();
                    videoBackground.setVisible(false);
                }
                
            } else {
                System.err.println("Video file not found in resources");
                System.err.println("Tried: " + java.util.Arrays.toString(possibleNames));
                videoBackground.setVisible(false);
                // Show fallback background if video not found
                if (fallbackBackground != null) {
                    fallbackBackground.setVisible(true);
                }
            }
        } catch (Exception e) {
            System.err.println("Error loading video: " + e.getMessage());
            e.printStackTrace();
            videoBackground.setVisible(false);
            // Show fallback background on error
            if (fallbackBackground != null) {
                fallbackBackground.setVisible(true);
            }
            // Continue without video if it fails to load
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
     * Loads and displays the game screen.
     */
    @FXML
    private void handleStart() {
        try {
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
                
                // Initialize the game
                new com.comp2042.controller.GameController(guiController);
            }
        } catch (Exception e) {
            System.err.println("Error loading game: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Handles the Settings button action.
     * Placeholder for future settings functionality.
     */
    @FXML
    private void handleSettings() {
        // Placeholder - can be implemented later
        System.out.println("Settings button clicked - placeholder");
        // TODO: Implement settings dialog
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

