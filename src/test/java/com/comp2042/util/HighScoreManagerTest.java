package com.comp2042.util;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

class HighScoreManagerTest {

    @TempDir
    Path tempDir;

    private HighScoreManager manager;
    private Path scoreFile;

    @BeforeEach
    void setUp() {
        scoreFile = tempDir.resolve("test_highscore.txt");
        manager = new HighScoreManager(scoreFile);
    }

    @Test
    void testDefaultScore() {
        assertEquals(0, manager.getHighScore(), "Default high score should be 0 when file is absent");
    }

    @Test
    void testUpdateHighScore() {
        assertTrue(manager.updateHighScore(100), "Updating to a higher score should return true");
        assertEquals(100, manager.getHighScore(), "High score should update to 100");
    }

    @Test
    void testPersistence() {
        assertTrue(manager.updateHighScore(100));
        HighScoreManager reloaded = new HighScoreManager(scoreFile);
        assertEquals(100, reloaded.getHighScore(), "High score should persist to disk");
    }

    @Test
    void testNoUpdateIfLower() {
        assertTrue(manager.updateHighScore(100));
        assertFalse(manager.updateHighScore(50), "Lower score should not overwrite existing high score");
        assertEquals(100, manager.getHighScore(), "High score should remain 100");
    }
}

