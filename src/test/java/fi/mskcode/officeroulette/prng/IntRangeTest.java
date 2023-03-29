package fi.mskcode.officeroulette.prng;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

public class IntRangeTest {

    @Test
    public void ofShouldValidateInput() {
        var e = assertThrows(IllegalArgumentException.class, () -> IntRange.of(0, -1));
        assertEquals("max value -1 smaller than min value 0", e.getMessage());
    }

    @Test
    public void distanceShouldWork() {
        assertEquals(0, IntRange.of(0, 0).distance);
        assertEquals(1, IntRange.of(-1, 0).distance);
        assertEquals(1, IntRange.of(0, 1).distance);
        assertEquals(100, IntRange.of(0, 100).distance);
        assertEquals(100, IntRange.of(-100, 0).distance);
        assertEquals(200, IntRange.of(-100, 100).distance);
        assertEquals(4294967295L, IntRange.of(Integer.MIN_VALUE, Integer.MAX_VALUE).distance);
    }

    @Test
    public void lengthShouldWork() {
        assertEquals(1, IntRange.of(0, 0).length);
        assertEquals(2, IntRange.of(-1, 0).length);
        assertEquals(2, IntRange.of(0, 1).length);
        assertEquals(101, IntRange.of(0, 100).length);
        assertEquals(101, IntRange.of(-100, 0).length);
        assertEquals(201, IntRange.of(-100, 100).length);
        assertEquals(4294967296L, IntRange.of(Integer.MIN_VALUE, Integer.MAX_VALUE).length);
    }

    @Test
    public void hasNegativeRangeShouldWork() {
        assertTrue(IntRange.of(-1, 0).hasNegativeRange());
        assertTrue(IntRange.of(-1, 1).hasNegativeRange());
        assertFalse(IntRange.of(0, 0).hasNegativeRange());
        assertFalse(IntRange.of(0, 1).hasNegativeRange());
    }

    @Test
    public void materializeShouldWork() {
        assertArrayEquals(new int[] {0}, IntRange.of(0, 0).materialize());
        assertArrayEquals(new int[] {0, 1}, IntRange.of(0, 1).materialize());
        assertArrayEquals(new int[] {-1, 0}, IntRange.of(-1, 0).materialize());
        assertArrayEquals(new int[] {-1, 0, 1}, IntRange.of(-1, 1).materialize());

        var e = assertThrows(
                UnsupportedOperationException.class,
                () -> IntRange.of(Integer.MIN_VALUE, Integer.MAX_VALUE).materialize());
        assertEquals(
                "IntRange [-2147483648 - 2147483647] cannot be materialized due to its size being bigger than 2147483647",
                e.getMessage());
    }
}
