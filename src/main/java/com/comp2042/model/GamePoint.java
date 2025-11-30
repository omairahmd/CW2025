package com.comp2042.model;

/**
 * Represents a 2D point with integer coordinates.
 * This class is immutable and replaces java.awt.Point to decouple from AWT.
 */
public final class GamePoint {

    /** X coordinate */
    private final int x;
    
    /** Y coordinate */
    private final int y;

    /**
     * Creates a new GamePoint with the specified coordinates.
     *
     * @param x the x coordinate
     * @param y the y coordinate
     */
    public GamePoint(int x, int y) {
        this.x = x;
        this.y = y;
    }

    /**
     * Creates a new GamePoint by copying another GamePoint.
     *
     * @param other the GamePoint to copy
     */
    public GamePoint(GamePoint other) {
        this.x = other.x;
        this.y = other.y;
    }

    /**
     * Returns the x coordinate.
     *
     * @return the x coordinate
     */
    public int getX() {
        return x;
    }

    /**
     * Returns the y coordinate.
     *
     * @return the y coordinate
     */
    public int getY() {
        return y;
    }

    /**
     * Returns a new GamePoint that is translated by the specified offsets.
     * Since this class is immutable, this method returns a new instance
     * rather than modifying the current one.
     *
     * @param dx the x offset
     * @param dy the y offset
     * @return a new GamePoint with translated coordinates
     */
    public GamePoint translate(int dx, int dy) {
        return new GamePoint(this.x + dx, this.y + dy);
    }

    /**
     * Returns a string representation of this point.
     *
     * @return a string in the format "GamePoint(x, y)"
     */
    @Override
    public String toString() {
        return "GamePoint(" + x + ", " + y + ")";
    }
}

