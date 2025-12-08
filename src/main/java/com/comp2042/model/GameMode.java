package com.comp2042.model;

/**
 * Enum representing different game modes.
 * CLASSIC: Traditional Tetris with game over condition.
 * OVERGROWTH: Survival mode where the board shifts up and adds vine lines every 10 seconds.
 * TREASURE_HUNT: Objective-based mode where player must clear all Gold blocks from pre-filled Dirt/Gold field.
 */
public enum GameMode {
    CLASSIC,
    OVERGROWTH,
    TREASURE_HUNT
}

