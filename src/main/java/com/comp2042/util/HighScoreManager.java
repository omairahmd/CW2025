package com.comp2042.util;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Manages high score persistence to/from a file.
 * High scores are stored in a simple text file in the user's home directory.
 */
public class HighScoreManager {
    
    /** Name of the high score file */
    private static final String HIGH_SCORE_FILE_NAME = ".tetris_highscore.txt";
    
    /** Default high score if no file exists */
    private static final int DEFAULT_HIGH_SCORE = 0;
    
    /** Path to the high score file in the user's home directory */
    private final Path highScoreFilePath;
    
    /** Current high score value */
    private int highScore;
    
    /**
     * Creates a new HighScoreManager and loads the high score from file.
     */
    public HighScoreManager() {
        // Store high score file in user's home directory
        String userHome = System.getProperty("user.home");
        highScoreFilePath = Paths.get(userHome, HIGH_SCORE_FILE_NAME);
        highScore = loadHighScore();
    }
    
    /**
     * Loads the high score from the file.
     * If the file doesn't exist or contains invalid data, returns the default high score (0).
     * 
     * @return the high score from the file, or 0 if file doesn't exist or is invalid
     */
    private int loadHighScore() {
        if (!Files.exists(highScoreFilePath)) {
            return DEFAULT_HIGH_SCORE;
        }
        
        try (BufferedReader reader = Files.newBufferedReader(highScoreFilePath)) {
            String line = reader.readLine();
            if (line != null && !line.trim().isEmpty()) {
                int score = Integer.parseInt(line.trim());
                return Math.max(score, DEFAULT_HIGH_SCORE); // Ensure non-negative
            }
        } catch (IOException | NumberFormatException e) {
            // If file read fails or contains invalid data, return default
            System.err.println("Error reading high score file: " + e.getMessage());
        }
        
        return DEFAULT_HIGH_SCORE;
    }
    
    /**
     * Saves the high score to the file.
     * Creates the file if it doesn't exist.
     * 
     * @param score the score to save
     * @return true if the save was successful, false otherwise
     */
    public boolean saveHighScore(int score) {
        if (score < 0) {
            return false; // Don't save negative scores
        }
        
        try {
            // Create parent directories if they don't exist
            if (highScoreFilePath.getParent() != null) {
                Files.createDirectories(highScoreFilePath.getParent());
            }
            
            // Write the score to the file
            try (BufferedWriter writer = Files.newBufferedWriter(highScoreFilePath)) {
                writer.write(String.valueOf(score));
            }
            
            highScore = score;
            return true;
        } catch (IOException e) {
            System.err.println("Error saving high score file: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Gets the current high score.
     * 
     * @return the current high score
     */
    public int getHighScore() {
        return highScore;
    }
    
    /**
     * Updates the high score if the new score is higher.
     * 
     * @param newScore the new score to check
     * @return true if the high score was updated, false otherwise
     */
    public boolean updateHighScore(int newScore) {
        if (newScore > highScore) {
            return saveHighScore(newScore);
        }
        return false;
    }
    
    /**
     * Resets the high score to 0.
     * 
     * @return true if the reset was successful, false otherwise
     */
    public boolean resetHighScore() {
        return saveHighScore(DEFAULT_HIGH_SCORE);
    }
}

