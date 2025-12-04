package com.comp2042.logic.bricks;

/**
 * Factory class for creating Brick instances.
 * Implements the Factory Design Pattern to centralize brick creation logic.
 * 
 * <p>This factory maps integer types (0-6) to specific brick implementations:
 * <ul>
 *   <li>0 - I-Brick (straight line)</li>
 *   <li>1 - J-Brick (L-shape, left)</li>
 *   <li>2 - L-Brick (L-shape, right)</li>
 *   <li>3 - O-Brick (square)</li>
 *   <li>4 - S-Brick (S-shape)</li>
 *   <li>5 - T-Brick (T-shape)</li>
 *   <li>6 - Z-Brick (Z-shape)</li>
 * </ul>
 */
public class BrickFactory {

    // Brick type constants
    /** Type identifier for I-Brick (straight line) */
    public static final int BRICK_TYPE_I = 0;
    
    /** Type identifier for J-Brick (L-shape, left) */
    public static final int BRICK_TYPE_J = 1;
    
    /** Type identifier for L-Brick (L-shape, right) */
    public static final int BRICK_TYPE_L = 2;
    
    /** Type identifier for O-Brick (square) */
    public static final int BRICK_TYPE_O = 3;
    
    /** Type identifier for S-Brick (S-shape) */
    public static final int BRICK_TYPE_S = 4;
    
    /** Type identifier for T-Brick (T-shape) */
    public static final int BRICK_TYPE_T = 5;
    
    /** Type identifier for Z-Brick (Z-shape) */
    public static final int BRICK_TYPE_Z = 6;
    
    /** Total number of brick types available */
    public static final int TOTAL_BRICK_TYPES = 7;

    // We don't want to instantiate this factory class
    private BrickFactory() {
    }

    /**
     * Creates and returns a Brick instance based on the specified type.
     * 
     * @param type the brick type (0-6). Values outside this range will result in IllegalArgumentException
     * @return a new Brick instance of the specified type
     * @throws IllegalArgumentException if the type is not in the valid range [0, 6]
     */
    public static Brick getBrick(int type) {
        return switch (type) {
            case BRICK_TYPE_I -> new IBrick();
            case BRICK_TYPE_J -> new JBrick();
            case BRICK_TYPE_L -> new LBrick();
            case BRICK_TYPE_O -> new OBrick();
            case BRICK_TYPE_S -> new SBrick();
            case BRICK_TYPE_T -> new TBrick();
            case BRICK_TYPE_Z -> new ZBrick();
            default -> throw new IllegalArgumentException(
                    "Invalid brick type: " + type + ". Valid range is 0-6.");
        };
    }
}

