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
 */
public class SoundManager {
    
    private static SoundManager instance;
    
    private MediaPlayer musicPlayer;
    private Map<String, AudioClip> soundEffects;
    private DoubleProperty musicVolume;
    private DoubleProperty sfxVolume;
    
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
     * 
     * @return the SoundManager instance
     */
    public static SoundManager getInstance() {
        if (instance == null) {
            instance = new SoundManager();
        }
        return instance;
    }
    
    /**
     * Loads the background music.
     */
    private void loadMusic() {
        try {
            String musicPath = getClass().getResource("/Sounds/ForestBackgroundMusic.wav").toExternalForm();
            Media media = new Media(musicPath);
            musicPlayer = new MediaPlayer(media);
            musicPlayer.setCycleCount(MediaPlayer.INDEFINITE);
            musicPlayer.volumeProperty().bind(musicVolume);
        } catch (Exception e) {
            System.err.println("Error loading background music: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Loads all sound effects into the map.
     */
    private void loadSoundEffects() {
        String[] soundNames = {"move", "land", "clear", "gameover"};
        String[] soundFiles = {
            "/Sounds/MovingBlockSFx.wav",
            "/Sounds/BlockLandingSFx.wav",
            "/Sounds/LineCompletedSFx.wav",
            "/Sounds/GameOverSFx.wav"
        };
        
        for (int i = 0; i < soundNames.length; i++) {
            try {
                String soundPath = getClass().getResource(soundFiles[i]).toExternalForm();
                AudioClip clip = new AudioClip(soundPath);
                soundEffects.put(soundNames[i], clip);
            } catch (Exception e) {
                System.err.println("Error loading sound effect '" + soundNames[i] + "': " + e.getMessage());
                e.printStackTrace();
            }
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
     * 
     * @param name the name of the sound effect to play
     */
    public void playSound(String name) {
        AudioClip clip = soundEffects.get(name);
        if (clip != null) {
            clip.setVolume(sfxVolume.get());
            clip.play();
        } else {
            System.err.println("Sound effect '" + name + "' not found");
        }
    }
    
    /**
     * Returns the music volume property for binding.
     * 
     * @return the music volume property
     */
    public DoubleProperty musicVolumeProperty() {
        return musicVolume;
    }
    
    /**
     * Returns the SFX volume property for binding.
     * 
     * @return the SFX volume property
     */
    public DoubleProperty sfxVolumeProperty() {
        return sfxVolume;
    }
}

