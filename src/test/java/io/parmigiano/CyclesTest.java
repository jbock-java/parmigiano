package io.parmigiano;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

class CyclesTest {

    @Test
    void testUnApply() {
        Cycles p = Permutation.random(100).toCycles();
        for (int i = -1; i <= p.length(); i++)
            assertEquals(i, p.unApply(p.apply(i)));
    }

    @Test
    void testUnclobber() {
        int[] a = ArrayUtil.range(100);
        Cycles p = Permutation.random(100).toCycles();
        p.clobber(a);
        p.unclobber(a);
        assertArrayEquals(ArrayUtil.range(100), a);
    }
}
