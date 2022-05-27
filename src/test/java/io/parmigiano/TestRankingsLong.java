package io.parmigiano;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
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
            String[] a = TestUtil.symbols(100);
            String[] shuffled = Permutation.random(a.length).apply(a);
            assertArrayEquals(ArrayUtil.sortedCopy(a), Permutation.sorting(shuffled).apply(shuffled));
        }
    }

    @Test
    void testFromRandom() {
        for (int i = 0; i < 100; i += 1) {
            long[] a = randomNumbers(100, 200);
            long[] b = Permutation.random(a.length).apply(a);
            assertArrayEquals(b, Permutation.taking(a).to(b).apply(a));
        }
        for (int i = 0; i < 100; i += 1) {
            long[] a = randomNumbers(100, 20);
            long[] b = Permutation.random(a.length).apply(a);
            assertArrayEquals(b, Permutation.taking(a).to(b).apply(a));
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

    @Test
    void testMismatch() {
        long[] a = randomNumbers(100, 110);
        long[] b = Permutation.random(a.length).apply(a);

        int[] dupes = TestUtil.duplicateIndexes(b);

        for (int j = 0; j < b.length; j += 1) {
            if (b[dupes[0]] != b[j]) {
                b[dupes[0]] = b[j];
                break;
            }
        }

        // null because b is not a rearrangement of a
        assertNull(Rankings.from(a, b));
    }
}
