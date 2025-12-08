package com.comp2042.manager;

import javafx.scene.media.AudioClip;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import java.util.HashMap;
import java.util.Map;

/**
 * Singleton class for managing game audio (music and sound effects).
 * Provides centralized control over background music and SFX playback.
 * Implements the "Null Object Pattern" to handle missing audio files gracefully.
 */
public class SoundManager {
    
    private static SoundManager instance;
    
    private MediaPlayer musicPlayer;
    private final Map<String, AudioClip> soundEffects;
    private final DoubleProperty musicVolume;
    private final DoubleProperty sfxVolume;
    
    /**
     * Private constructor to enforce singleton pattern.
     * Loads all audio resources.
     */
    private SoundManager() {
        musicVolume = new SimpleDoubleProperty(0.5);
        sfxVolume = new SimpleDoubleProperty(0.5);
        soundEffects = new HashMap<>();
        
        loadMusic();
        loadSoundEffects();
    }
    
    /**
     * Returns the singleton instance of SoundManager.
     * * @return the SoundManager instance
     */
    public static SoundManager getInstance() {
        if (instance == null) {
            instance = new SoundManager();
        }
        return instance;
    }
    
    /**
     * Loads the background music.
     * Handles errors gracefully by logging them and continuing without music.
     */
    private void loadMusic() {
        try {
            String musicPath = getClass().getResource("/Sounds/ForestBackgroundMusic.wav").toExternalForm();
            Media media = new Media(musicPath);
            musicPlayer = new MediaPlayer(media);
            musicPlayer.setCycleCount(MediaPlayer.INDEFINITE);
            musicPlayer.volumeProperty().bind(musicVolume);
        } catch (Exception e) {
            System.err.println("Warning: Could not load background music: " + e.getMessage());
            // Recovery: Game continues without background music
        }
    }
    
    /**
     * Loads sound effects from resource files.
     * Creates silent dummy clips for any sounds that fail to load,
     * ensuring the game can continue without crashes.
     */
    private void loadSoundEffects() {
        String[] soundNames = {"move", "land", "clear", "gameover"};
        String[] soundFiles = {
            "/Sounds/MovingBlockSFx.wav",
            "/Sounds/BlockLandingSFx.wav",
            "/Sounds/LineCompletedSFx.wav",
            "/Sounds/GameOverSFx.wav"
        };
        
        int failedLoads = 0;
        
        for (int i = 0; i < soundNames.length; i++) {
            try {
                // Try to load the real sound file
                String soundPath = getClass().getResource(soundFiles[i]).toExternalForm();
                AudioClip clip = new AudioClip(soundPath);
                soundEffects.put(soundNames[i], clip);
            } catch (Exception e) {
                // RECOVERY: Log warning and create a dummy silent clip so playSound() won't fail
                System.err.println("Warning: Could not load sound '" + soundNames[i] + "'. Using silent fallback.");
                soundEffects.put(soundNames[i], createDummyClip());
                failedLoads++;
            }
        }
        
        if (failedLoads > 0) {
            System.err.println("Note: Game running with " + failedLoads + " missing sound effects.");
        }
    }

    /**
     * Creates a no-op (silent) audio clip that safely does nothing when played.
     * Used as a fallback when a sound file cannot be loaded.
     * * @return a safe, silent AudioClip, or null if creation fails
     */
    private AudioClip createDummyClip() {
        try {
            // Minimal valid WAV header as a Base64 string to create a valid but silent clip
            // This allows the AudioClip to be instantiated without an external file
            String dataUrl = "data:audio/wav;base64,UklGRiQAAABXQVZFZm10IBAAAAABAAEAQB8AAAB9AAACABAAZGF0YQAAAAA=";
            return new AudioClip(dataUrl);
        } catch (Exception e) {
            // If even the dummy fails (e.g. data URIs not supported), return null
            // playSound handles nulls safely
            return null;
        }
    }
    
    /**
     * Starts playing the background music.
     */
    public void startMusic() {
        if (musicPlayer != null) {
            musicPlayer.play();
        }
    }
    
    /**
     * Stops the background music.
     */
    public void stopMusic() {
        if (musicPlayer != null) {
            musicPlayer.stop();
        }
    }
    
    /**
     * Plays a sound effect by name at the current SFX volume.
     * Safely handles missing sounds (silent clips) without crashing.
     * * @param name the name of the sound effect to play
     */
    public void playSound(String name) {
        AudioClip clip = soundEffects.get(name);
        
        // Check for null just in case even the dummy creation failed
        if (clip != null) {
            try {
                if (sfxVolume != null) {
                    clip.setVolume(sfxVolume.get());
                }
                clip.play();
            } catch (Exception e) {
                // Final safety net: Log error but don't crash the game loop
                System.err.println("Warning: Audio playback failed for '" + name + "': " + e.getMessage());
            }
        }
    }
    
    /**
     * Returns the music volume property for binding.
     * * @return the music volume property
     */
    public DoubleProperty musicVolumeProperty() {
        return musicVolume;
    }
    
    /**
     * Returns the SFX volume property for binding.
     * * @return the SFX volume property
     */
    public DoubleProperty sfxVolumeProperty() {
        return sfxVolume;
    }
}