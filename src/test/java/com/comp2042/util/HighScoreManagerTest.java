package com.comp2042.util;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

class HighScoreManagerTest {

    @TempDir
    Path tempDir;

    private String originalUserHome;
    private HighScoreManager manager;

    @BeforeEach
    void setUp() {
        originalUserHome = System.getProperty("user.home");
        System.setProperty("user.home", tempDir.toString());
        manager = new HighScoreManager();
    }

    @AfterEach
    void tearDown() throws Exception {
        System.setProperty("user.home", originalUserHome);
        Path file = tempDir.resolve(".tetris_highscore.txt");
        if (Files.exists(file)) {
            Files.delete(file);
        }
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
    void testNoUpdateIfLower() {
        assertTrue(manager.updateHighScore(100));
        assertFalse(manager.updateHighScore(50), "Lower score should not overwrite existing high score");
        assertEquals(100, manager.getHighScore(), "High score should remain 100");
    }
}

