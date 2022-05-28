package io.parmigiano;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

/* like PermutationFactoryTest, but use the long versions of sorting and from */
class TestRankingsLong {

    static long[] randomNumbers(int maxNumber, int length) {
        long[] result = new long[length];
        for (int i = 0; i < length; i += 1) {
            result[i] = (int) (maxNumber * Math.random());
        }
        return result;
    }

    @Test
    void testSortRandom() {
        for (int i = 0; i < 100; i += 1) {
            long[] a = randomNumbers(100, 200);
            assertArrayEquals(ArrayUtil.sortedCopy(a), Permutation.sorting(a).apply(a));
        }
        for (int i = 0; i < 100; i += 1) {
            long[] a = randomNumbers(100, 20);
            assertArrayEquals(ArrayUtil.sortedCopy(a), Permutation.sorting(a).apply(a));
        }
    }

    @Test
    void testSortStrict() {
        for (int i = 0; i < 100; i += 1) {
            List<String> a = TestUtil.symbols2(100);
            List<String> shuffled = Permutation.random(a.size()).apply(a);
            assertEquals(ArrayUtil.sortedCopy(a), Permutation.sortingComparable(shuffled).apply(shuffled));
        }
    }
    
    @Test
    void testFromStrict() {
        for (int i = 0; i < 100; i += 1) {
            String[] a = TestUtil.symbols(100);
            String[] shuffled = Permutation.random(a.length).apply(a);
            assertArrayEquals(a, Permutation.taking(shuffled).to(a).apply(shuffled));
        }
    }
}
