package com.comp2042.util;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

/**
 * Manages high score persistence to/from a file.
 * High scores are stored in a simple text file in the user's home directory.
 * Implements robust error handling and atomic writes to prevent data corruption.
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
     * If the file is corrupted, attempts to repair it by resetting to default.
     * * @return the high score from the file, or DEFAULT_HIGH_SCORE if file doesn't exist or is invalid
     */
    private int loadHighScore() {
        if (!Files.exists(highScoreFilePath)) {
            return DEFAULT_HIGH_SCORE;
        }

        try (BufferedReader reader = Files.newBufferedReader(highScoreFilePath)) {
            String line = reader.readLine();
            if (line != null && !line.trim().isEmpty()) {
                int score = Integer.parseInt(line.trim());

                // Validate score is reasonable (not negative, not impossibly high)
                if (score < 0) {
                    System.err.println("Warning: High score file contains negative value. Resetting to 0.");
                    repairHighScoreFile();
                    return DEFAULT_HIGH_SCORE;
                }

                if (score > 999999) {
                    System.err.println("Warning: High score file contains suspiciously high value. Resetting.");
                    repairHighScoreFile();
                    return DEFAULT_HIGH_SCORE;
                }

                return score;
            }
        } catch (IOException e) {
            System.err.println("Error reading high score file: " + e.getMessage());
            // RECOVERY: Try to repair the file
            if (repairHighScoreFile()) {
                System.out.println("High score file repaired successfully.");
            }
        } catch (NumberFormatException e) {
            System.err.println("High score file is corrupted (invalid number format).");
            // RECOVERY: Repair the corrupted file
            if (repairHighScoreFile()) {
                System.out.println("Corrupted high score file has been reset.");
            }
        }

        return DEFAULT_HIGH_SCORE;
    }

    /**
     * Repairs a corrupted high score file by resetting it to the default value.
     * * @return true if repair was successful, false otherwise
     */
    private boolean repairHighScoreFile() {
        try {
            // Create backup of corrupted file (optional but good practice)
            if (Files.exists(highScoreFilePath)) {
                Path backupPath = Paths.get(highScoreFilePath.toString() + ".backup");
                Files.copy(highScoreFilePath, backupPath,
                        StandardCopyOption.REPLACE_EXISTING);
                System.out.println("Backup created: " + backupPath);
            }

            // Write default value directly (bypassing the atomic check to force reset)
            try (BufferedWriter writer = Files.newBufferedWriter(highScoreFilePath)) {
                writer.write(String.valueOf(DEFAULT_HIGH_SCORE));
            }
            return true;
        } catch (IOException e) {
            System.err.println("Failed to repair high score file: " + e.getMessage());
            return false;
        }
    }

    /**
     * Saves the high score to the file with error recovery.
     * * @param score the score to save
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

            // Write to temporary file first (atomic operation pattern)
            Path tempFile = Files.createTempFile("tetris_highscore", ".tmp");
            try (BufferedWriter writer = Files.newBufferedWriter(tempFile)) {
                writer.write(String.valueOf(score));
            }

            // RECOVERY: Use atomic move to prevent corruption
            // If power fails during write, original file remains intact until the very last moment
            Files.move(tempFile, highScoreFilePath,
                    StandardCopyOption.REPLACE_EXISTING,
                    StandardCopyOption.ATOMIC_MOVE);

            highScore = score;
            return true;
        } catch (IOException e) {
            System.err.println("Error saving high score file: " + e.getMessage());

            // RECOVERY: Try saving to alternative location
            return saveHighScoreToFallbackLocation(score);
        }
    }

    /**
     * Attempts to save high score to a fallback location if primary location fails.
     * Uses system temp directory as fallback.
     * * @param score the score to save
     * @return true if fallback save was successful, false otherwise
     */
    private boolean saveHighScoreToFallbackLocation(int score) {
        try {
            Path fallbackPath = Paths.get(System.getProperty("java.io.tmpdir"),
                    ".tetris_highscore_fallback.txt");
            System.out.println("Attempting to save to fallback location: " + fallbackPath);

            try (BufferedWriter writer = Files.newBufferedWriter(fallbackPath)) {
                writer.write(String.valueOf(score));
            }

            System.out.println("High score saved to fallback location successfully.");
            highScore = score;
            return true;
        } catch (IOException e) {
            System.err.println("Failed to save to fallback location: " + e.getMessage());
            // At this point, we've exhausted all options
            return false;
        }
    }

    /**
     * Gets the current high score.
     * * @return the current high score
     */
    public int getHighScore() {
        return highScore;
    }

    /**
     * Updates the high score if the new score is higher.
     * * @param newScore the new score to check
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
     * * @return true if the reset was successful, false otherwise
     */
    public boolean resetHighScore() {
        return saveHighScore(DEFAULT_HIGH_SCORE);
    }
}