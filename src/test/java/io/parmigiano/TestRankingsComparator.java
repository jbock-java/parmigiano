package io.parmigiano;

import org.junit.jupiter.api.Test;

import java.util.Comparator;
import java.util.List;

import static io.parmigiano.MyInt.box;
import static io.parmigiano.MyInt.box2;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/* like PermutationFactoryTest, but use the Comparator versions of sorting and from */
class TestRankingsComparator {

    static final int REPEAT = 1000;

    static MyInt[] randomMyInts(int maxNumber, int length) {
        return box(ArrayUtil.randomNumbers(maxNumber, length));
    }

    @Test
    void testSortRandom() {
        List<MyInt> a;
        for (int __ = 0; __ < REPEAT; __ += 1) {
            a = box2(ArrayUtil.randomNumbers(100, 200));
            assertEquals(ArrayUtil.sortedCopy(a, MyInt.COMP), Permutation.sorting(a).using(MyInt.COMP).apply(a));
        }
        for (int i = 0; i < REPEAT; i += 1) {
            a = box2(ArrayUtil.randomNumbers(100, 200));
            assertEquals(ArrayUtil.sortedCopy(a, MyInt.COMP), Permutation.sorting(a).using(MyInt.COMP).apply(a));
        }
    }

    @Test
    void testSortStrict() {
        for (int __ = 0; __ < REPEAT; __ += 1) {
            String[] a = TestUtil.symbols(100);
            String[] shuffled = Permutation.random(a.length).apply(a);
            assertArrayEquals(ArrayUtil.sortedCopy(a), Permutation.sorting(shuffled).apply(shuffled));
        }
    }

    @Test
    void testFromRandom() {
        for (int __ = 0; __ < REPEAT; __ += 1) {
            int[] a = ArrayUtil.randomNumbers(100, 200);
            MyInt[] b = Permutation.random(a.length).apply(box(a));
            assertArrayEquals(b, Permutation.taking(box(a)).to(b).using(MyInt.COMP).apply(box(a)));
        }
        for (int i = 0; i < REPEAT; i += 1) {
            MyInt[] a = randomMyInts(100, 20);
            MyInt[] b = Permutation.random(a.length).apply(a);
            assertArrayEquals(b, Permutation.taking(a).to(b).using(MyInt.COMP).apply(a));
        }
    }

    @Test
    void testFromStrict() {
        for (int __ = 0; __ < REPEAT; __ += 1) {
            String[] a = TestUtil.symbols(100);
            String[] shuffled = Permutation.random(a.length).apply(a);
            assertArrayEquals(a, Permutation.taking(shuffled).to(a).apply(shuffled));
        }
    }

    @Test
    @SuppressWarnings("unchecked")
    void testMismatch() {

        for (int __ = 0; __ < REPEAT; __ += 1) {
            MyInt[] a = randomMyInts(100, 110);
            Object[] b = Permutation.random(a.length).apply(a);

            int[] bdupes = TestUtil.duplicateIndexes(b, MyInt.COMP);
            int[] adupes = TestUtil.duplicateIndexes(a, MyInt.COMP);

            MyInt changed = null;
            // subtly mess things up by changing b,
            // so that all elements in a can still be found in b,
            // but b is not a reordering of a anymore
            if (Math.random() < 0.5) {
                for (int j = 0; j < b.length; j += 1) {
                    if (!b[bdupes[0]].equals(b[j])) {
                        b[bdupes[0]] = b[j];
                        changed = (MyInt) b[j];
                        break;
                    }
                }
            } else {
                for (int j = 0; j < a.length; j += 1) {
                    if (!a[adupes[0]].equals(a[j])) {
                        a[adupes[0]] = a[j];
                        changed = a[j];
                        break;
                    }
                }
            }
            int bc = TestUtil.count(b, changed);
            int ac = TestUtil.count(a, changed);
            assertNotEquals(bc, ac);
            assertTrue(ac > 0);
            assertTrue(bc > 0);

            // null because b is not a rearrangement of a
            assertNull(Rankings.from(a, b, (Comparator) MyInt.COMP));
        }
    }
}
