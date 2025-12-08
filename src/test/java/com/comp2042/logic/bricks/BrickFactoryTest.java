package com.comp2042.logic.bricks;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class BrickFactoryTest {

    @Test
    void testValidBricks() {
        for (int i = 0; i < BrickFactory.TOTAL_BRICK_TYPES; i++) {
            Brick brick = BrickFactory.getBrick(i);
            assertNotNull(brick, "Factory should return a Brick for type " + i);
        }
    }

    @Test
    void testInvalidBrickThrows() {
        assertThrows(IllegalArgumentException.class, () -> BrickFactory.getBrick(99));
    }

    @Test
    void testSpecificType() {
        Brick brick = BrickFactory.getBrick(BrickFactory.BRICK_TYPE_I);
        assertNotNull(brick);
        assertEquals("IBrick", brick.getClass().getSimpleName(), "Type 0 should produce an IBrick");
    }
}

