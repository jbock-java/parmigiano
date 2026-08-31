package io.parmigiano;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class CycleUtilTest {

    @Test
    void maxHoff() {
        assertEquals(1, CycleUtil.maxOff(new int[]{1, 3, 2}, 0, 3));
        assertEquals(0, CycleUtil.maxOff(new int[]{3, 2, 1}, 0, 3));
        assertEquals(2, CycleUtil.maxOff(new int[]{2, 1, 3}, 0, 3));
        assertEquals(2, CycleUtil.maxOff(new int[]{2, 1, 3}, 0, 3));
        assertEquals(1, CycleUtil.maxOff(new int[]{1, 4, 2, 3, 0}, 2, 2));
    }

    /* gaps in ranking */
    @Test
    void testInvalidGap() {
        assertThrows(IllegalArgumentException.class, () -> Rankings.checkRanking(new int[]{1, 2, 0, 5}));
    }

    /* missing zero in ranking */
    @Test
    void testInvalidMissingZero() {
        assertThrows(IllegalArgumentException.class, () -> Rankings.checkRanking(new int[]{1, 2, 3}));
    }

    /* duplicates in ranking */
    @Test
    void testInvalidDuplicate() {
        int[] ranking = {1, 2, 0, 2, 3};
        assertThrows(IllegalArgumentException.class, () -> Rankings.checkRanking(ranking));
    }

    /* negative number in ranking */
    @Test
    void testInvalidNegative() {
        assertThrows(IllegalArgumentException.class, () -> Rankings.checkRanking(new int[]{-1, 0, 1}));
    }
}
