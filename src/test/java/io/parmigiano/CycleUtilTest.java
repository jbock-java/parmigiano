package io.parmigiano;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class CycleUtilTest {

    @Test
    void rotateToIndex() {
        assertArrayEquals(new int[]{1, 3, 2}, CycleUtil.rotateToIndex(new int[]{1, 3, 2}, 0));
        assertArrayEquals(new int[]{3, 2, 1}, CycleUtil.rotateToIndex(new int[]{1, 3, 2}, 1));
        assertArrayEquals(new int[]{2, 1, 3}, CycleUtil.rotateToIndex(new int[]{1, 3, 2}, 2));
    }

    @Test
    void maxIndex() {
        assertEquals(1, CycleUtil.maxIndex(new int[]{1, 3, 2}));
        assertEquals(0, CycleUtil.maxIndex(new int[]{3, 2, 1}));
        assertEquals(2, CycleUtil.maxIndex(new int[]{2, 1, 3}));
    }

    /* gaps in ranking */
    @Test
    void testInvalidGap() {
        assertThrows(IllegalArgumentException.class, () -> CycleUtil.toOrbits(new int[]{1, 2, 0, 5}));
    }

    /* missing zero in ranking */
    @Test
    void testInvalidMissingZero() {
        assertThrows(IllegalArgumentException.class, () -> CycleUtil.toOrbits(new int[]{1, 2, 3}));
    }

    /* duplicates in ranking */
    @Test
    void testInvalidDuplicate() {
        int[] ranking = {1, 2, 0, 2, 3};
        assertThrows(IllegalArgumentException.class, () -> Rankings.checkRanking(ranking));
        assertThrows(IllegalArgumentException.class, () -> CycleUtil.toOrbits(ranking));
    }

    /* negative number in ranking */
    @Test
    void testInvalidNegative() {
        assertThrows(IllegalArgumentException.class, () -> CycleUtil.toOrbits(new int[]{-1, 0, 1}));
    }
}