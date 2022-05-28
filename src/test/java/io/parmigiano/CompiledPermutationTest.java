package io.parmigiano;

import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

class CompiledPermutationTest {

    /* test defining property of apply */
    @Test
    void testApply() {
        for (int __ = 0; __ < 100; __++) {
            int[] a = ArrayUtil.range(100);
            int[] b = Arrays.copyOf(a, a.length);
            Cycles p = Cycles.random(a.length - 10);
            int[] c = p.apply(a);
            for (int i = 0; i < a.length; i += 1) {
                assertEquals(c[p.apply(i)], a[i]);
                if (i > p.maxMovedIndex()) {
                    assertEquals(a[i], c[i]);
                }
            }
            assertArrayEquals(b, a);
        }
    }
}
