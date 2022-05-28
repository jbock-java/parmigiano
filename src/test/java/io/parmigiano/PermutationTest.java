package io.parmigiano;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import static io.parmigiano.MyInt.box2;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Legacy tests.
 * All tests in here use the strict factory.
 */
class PermutationTest {

    /* Check example from constructor javadoc */
    @Test
    void testAbc() {
        Cycles p = Cycles.fromRanking(1, 2, 0);
        assertEquals("cab", p.apply("abc"));
    }

    @Test
    void testComp() {
        Cycles p = Cycles.fromRanking(1, 2, 0);
        assertEquals(Cycles.fromRanking(1, 2, 0), p);
        assertEquals(List.of("c", "a", "b"), p.apply(TestUtil.symbols2(3)));
        assertEquals(List.of("b", "c", "a"), p.compose(p).apply(TestUtil.symbols2(3)));
    }

    /* check defining property of composition */
    @Test
    void testComp2() {
        Cycles p = Cycles.random(7);
        Cycles p2 = Cycles.random(7);
        for (int i = 0; i < 10; i += 1) {
            assertEquals(p2.apply(p.apply(i)), p2.compose(p).apply(i));
        }
    }

    /* check defining property of apply */
    @Test
    void testApply() {
        int[] a = ArrayUtil.randomNumbers(100, 200);
        Cycles p = Cycles.random((int) (a.length * Math.random()));
        int[] pa = p.apply(a);
        for (int i = 0; i < a.length; i += 1) {
            int pi = p.apply(i);
            assertEquals(pa[pi], a[i]);
        }
    }

    @Test
    void testIterable() {
        for (int __ = 0; __ < 100; __++) {
            List<MyInt> a = box2(ArrayUtil.randomNumbers(100, 50 + (int) (Math.random() * 100)));
            Cycles p = Cycles.random((int) (Math.random() * a.size()));
            List<MyInt> applied = p.apply(a);
            List<MyInt> arrayList = new ArrayList<MyInt>(a.size());
            List<MyInt> linkedList = new LinkedList<>();
            arrayList.addAll(a);
            linkedList.addAll(a);
            List<MyInt> arrayListApplied, linkedListApplied;
            List<MyInt> arrayListApplied2, linkedListApplied2;

            // standard
            arrayListApplied = p.apply(arrayList);
            linkedListApplied = p.apply(linkedList);
            for (int i = 0; i < a.size(); i += 1) {
                assertEquals(applied.get(i), arrayListApplied.get(i));
                assertEquals(applied.get(i), linkedListApplied.get(i));
            }

            // compiled
            arrayListApplied2 = p.apply(arrayList);
            linkedListApplied2 = p.apply(linkedList);
            assertEquals(arrayListApplied, arrayListApplied2);
            assertEquals(linkedListApplied, linkedListApplied2);
        }
    }

    /* gaps in ranking */
    @Test
    void testInvalidGap() {
        assertThrows(ArrayIndexOutOfBoundsException.class, () -> Cycles.fromRanking(1, 2, 0, 5));
    }

    /* missing zero in ranking */
    @Test
    void testInvalidMissingZero() {
        assertThrows(ArrayIndexOutOfBoundsException.class, () -> Cycles.fromRanking(1, 2, 3));
    }

    /* duplicates in ranking */
    @Test
    void testInvalidDuplicate() {
        int[] ranking = {1, 2, 0, 2, 3};
        assertFalse(Rankings.isValid(ranking));
        Assertions.assertThrows(IllegalArgumentException.class, () -> Cycles.fromRanking(ranking));
    }

    /* negative number in ranking */
    @Test
    void testInvalidNegative() {
        assertThrows(ArrayIndexOutOfBoundsException.class, () -> Cycles.fromRanking(-1, 0, 1));
    }

    @Test
    void testInvert() {
        Cycles p = Cycles.fromRanking(1, 2, 0);
        assertTrue(Cycles.product(p.invert(), p).isIdentity());
        assertTrue(Cycles.product(p, p.invert()).isIdentity());
        assertTrue(Cycles.product(p, p.pow(2)).isIdentity());
        assertTrue(Cycles.product(p.pow(2), p).isIdentity());
        assertTrue(Cycles.product().isIdentity());
        assertTrue(p.pow(0).isIdentity());
        assertFalse(p.pow(1).isIdentity());
        assertFalse(p.pow(2).isIdentity());
        assertEquals(p.pow(0), p.pow(3));
        assertEquals(p.pow(2), Cycles.product(p, p));
        assertEquals(p.pow(1), p);
        assertEquals(p.pow(-1), Cycles.product(p, p));
        assertEquals(p.pow(-1), p.invert());
        assertEquals(p.pow(2), p.compose(p));
        assertEquals("abc", Cycles.product(p, p.invert()).apply("abc"));
    }

    @Test
    void testIdentity() {
        assertTrue(Cycles.identity().isIdentity());
        Assertions.assertTrue(Cycles.fromRanking(0, 1, 2, 3, 4).isIdentity());
        Assertions.assertTrue(Cycles.fromRanking(0, 1, 2, 3, 4).invert().isIdentity());
    }

    /* test defining property of identity */
    @Test
    void testIdentity2() {
        Cycles identity = Cycles.identity();
        for (int i = 0; i < 10; i += 1) {
            assertEquals(i, identity.apply(i));
        }
    }

    /* Check defining property of inverse */
    @Test
    void testInverse2() {
        Cycles p = Cycles.sorting(new int[]{4, 6, 10, -5, 195, 33, 2});
        for (int i = 0; i <= p.maxMovedIndex(); i += 1) {
            assertEquals(i, p.invert().apply(p.apply(i)));
        }
    }

    @Test
    void cycleEquality() {
        assertEquals(Cycles.create(1, 5, 3, 2), Cycles.create(5, 3, 2, 1));
        assertEquals(Cycles.create(1, 5, 3, 2), Cycles.create(2, 1, 5, 3));
        assertNotEquals(Cycles.create(1, 5, 3, 2), Cycles.create(1, 5, 2, 3));
    }

    @Test
    void cycleApply() {
        assertEquals(List.of("b", "c", "e", "d", "a"),
                Cycles.create(0, 4, 2, 1).apply(TestUtil.symbols2(5)));
        assertEquals(List.of("c", "b", "e", "d", "a"),
                Cycles.create(0, 4, 2).apply(TestUtil.symbols2(5)));
        assertEquals(List.of("c", "a", "b"),
                Cycles.create(0, 1, 2).apply(TestUtil.symbols2(3)));
    }

    @Test
    void testCycleApply() {
        assertEquals(List.of("c", "a", "b"),
                Cycles.product(Cycles.create(0, 1), Cycles.create(1, 2)).apply(TestUtil.symbols2(3)));
        assertEquals(List.of("c", "a", "b"), Cycles.create(0, 1, 2).apply(TestUtil.symbols2(3)));
        assertEquals(List.of("a", "c", "b"), Cycles.product(Cycles.create(0, 1),
                Cycles.product(Cycles.create(0,1), Cycles.create(1, 2))).apply(TestUtil.symbols2(3)));
    }

    @Test
    void testCycleEquals() {
        assertTrue(Cycles.product(Cycles.create(1, 2), Cycles.create(2, 1)).isIdentity());
        assertEquals(Cycles.create(2, 3), Cycles.product(Cycles.create(1, 2),
                Cycles.product(Cycles.create(1, 2), Cycles.create(2, 3))));
    }

    @Test
    void testCycleLaw() {
        Cycles longest = Cycles.create(2, 4, 1, 11, 3);
        assertEquals(Cycles.product(Cycles.create(2, 4),
                Cycles.create(4, 1, 11, 3)), longest);
    }

    @Test
    void testSort() {
        int[] x = new int[]{4, 6, 10, -5, 195, 33, 2};
        int[] y = Arrays.copyOf(x, x.length);
        Arrays.sort(y);
        Cycles p = Cycles.sorting(x);
        for (int i = 0; i < x.length; i += 1) {
            assertEquals(x[i], y[p.apply(i)]);
        }
        assertArrayEquals(y, p.apply(x));
    }

    int indexOf(int[] x, int el) {
        for (int i = 0; i < x.length; i += 1) {
            if (x[i] == el)
                return i;
        }
        throw new IllegalArgumentException("not in x: " + el);
    }

    int indexOf(List<MyInt> x, MyInt el) {
        for (int i = 0; i < x.size(); i += 1) {
            if (x.get(i).n == el.n)
                return i;
        }
        throw new IllegalArgumentException("not in x: " + el);
    }

    /* check example from README */
    @Test
    void testSortInvert() {
        int[] x = new int[]{4, 6, 10, -5, 195, 33, 2};
        Cycles unsort = Cycles.sorting(x).invert();
        int[] y = Arrays.copyOf(x, x.length);
        Arrays.sort(y);
        for (int k = 0; k < y.length; k += 1) {
            assertEquals(x[indexOf(x, y[k])], y[k]);
            assertEquals(indexOf(x, y[k]), unsort.apply(k));
        }
    }

    /* check defining property of sorting */
    @Test
    void testSortRandom() {
        int size = (int) (100 * Math.random());
        int[] distinct = Rankings.random(size);
        int[] sorted = Arrays.copyOf(distinct, distinct.length);
        Arrays.sort(sorted);
        Cycles p = Cycles.sorting(distinct);
        for (int i = 0; i < sorted.length; i += 1) {
            distinct[i] = sorted[p.apply(i)];
        }
    }

    @Test
    void testSortInvertComparator() {
        List<MyInt> x = box2(new int[]{4, 6, 10, -5, 195, 33, 2});
        Cycles unsort = Cycles.sorting(x, MyInt.COMP).invert();
        List<MyInt> y = new ArrayList<>(x);
        y.sort(MyInt.COMP);
        for (int k = 0; k < y.size(); k += 1) {
            assertEquals(x.get(indexOf(x, y.get(k))), y.get(k));
            assertEquals(indexOf(x, y.get(k)), unsort.apply(k));
        }
    }

    /* negative index */
    @Test
    void testApplyInvalid() {
        assertThrows(IllegalArgumentException.class, () -> Cycles.identity().apply(-1));
    }

    /**
     * @param a Any array of integers
     * @return A sorted copy of {@code a}
     */
    private int[] classicSort(int[] a) {
        int[] result = Arrays.copyOf(a, a.length);
        Arrays.sort(result);
        return result;
    }

    /* Another way of checking that duplicateRejectingFactory().sorting(a).apply(a) sorts a, for distinct array a */
    @Test
    void testSort1024() {
        int[] a = Rankings.random(1024);
        assertArrayEquals(classicSort(a), Cycles.sorting(a).apply(a));
    }

    @Test
    void testCycleLength() {
        Cycles swap01 = Cycles.create(0, 1);
        assertEquals(1, swap01.maxMovedIndex());
    }

    @Test
    void testFromQuickly() {
        Cycles p = Permutation.taking(new int[]{1, 2, 3}).to(new int[]{2, 3, 1});
        assertEquals(List.of("b", "c", "a"), p.apply(TestUtil.symbols2(3)));
    }

    /* check defining property of from */
    private void testFromQuickly2() {
        int size = 2048;
        int[] a = Rankings.random(size);
        Cycles random;
        do {
            random = Cycles.random((int) (Math.random() * size));
        } while (random.isIdentity());
        int[] b = random.apply(a);
        assertFalse(Arrays.equals(a, b));
        assertArrayEquals(Permutation.taking(a).to(b).apply(a), b);
    }

    /* check defining property of from again, on non comparable objects, possibly with null */
    @Test
    void testFromALot() {
        for (int i = 0; i < 100; i += 1) {
            testFromQuickly2();
        }
    }

    @Test
    void testMove() {
        assertEquals(Permutation.identity(), Permutation.move(5, 5));
        assertEquals("213", Permutation.move(0, 1).apply("123"));
        assertEquals("23145", Permutation.move(0, 2).apply("12345"));
        assertEquals("14235", Permutation.move(3, 1).apply("12345"));
    }

    /* various assertions about Sym(5) */
    @Test
    void testCyclesAndTranspositions() {
        int sign = 0;
        for (Permutation p : Permutation.symmetricGroup(5).collect(Collectors.toList())) {
            int order = p.order();
            sign += p.toCycles().signature();
            Cycles cycles = p.toCycles();
//      assertEquals(p, CompiledPermutation.product(cycles));
            assertEquals(p, p.toCycles().toPermutation());
            if (p.reverses(5)) {
                assertEquals(2, order);
                assertEquals(1, p.toCycles().signature());
            }
            if (order > 5) {
                assertEquals(6, order);
                assertEquals(2, cycles.numCycles());
            } else if (order == 5) {
                assertEquals(1, cycles.numCycles());
            } else if (order == 4) {
                assertEquals(1, cycles.numCycles());
            } else if (order == 3) {
                assertEquals(1, cycles.numCycles());
            } else if (order == 2) {
                assertTrue(cycles.numCycles() <= 2);
            } else {
                assertTrue(p.isIdentity());
            }
        }
        assertEquals(0, sign);
    }

    /* check edge cases */
    @Test
    void testZero() {
        Permutation p = Permutation.identity();
        assertEquals(Permutation.define(new int[0]), p);
        assertEquals(p, Permutation.defineCycle());
        assertEquals(0, p.length());
        assertArrayEquals(new int[0], p.apply(new int[0]));
        assertEquals(0, p.toCycles().numCycles());
        assertEquals(Permutation.identity(), Permutation.defineCycle(0));
        assertEquals(Permutation.identity(), Permutation.defineCycle(1));
        assertEquals(Permutation.identity(), Permutation.defineCycle(2));
    }

    /* example from README */
    @Test
    void testPprod() {
        Cycles c0 = Cycles.create(7, 9);
        Cycles c1 = Cycles.create(1, 4, 8, 10, 3, 6, 11);
        Cycles c2 = Cycles.create(0, 2, 5);
        assertEquals("Hello world!", c0.compose(c1).compose(c2).invert().apply(" !Hdellloorw"));
    }

    /* making sure sorting does what we think it does */
    @Test
    void testDegenerate() {
        int[] a = new int[]{3, 3, 3, 3, 3, 3, 3};
        assertFalse(Permutation.sorting(a).isIdentity());
    }

    @Test
    void testShift() {
        assertEquals("abccba", Permutation.reverse(3).shift(3).apply("abcabc"));
    }

    @Test
    void testShift2() {
        for (int __ = 0; __ < 100; __++) {
            Permutation p = Permutation.random(40);
            for (int n = 0; n < 100; n++) {
                for (int j = 0; j < 100; j++) {
                    if (j < n) {
                        assertEquals(j, p.shift(n).apply(j));
                    } else {
                        assertEquals(p.shift(n).apply(j), n + p.apply(j - n));
                    }
                }
            }
        }
    }
    
    @Test
    void testNonDestructive() {
        int[] a = {0, 1, 2, 3, 4};
        Permutation p = Permutation.define(1, 2, 0, 3, 4).compose(Permutation.define(0, 1, 2, 4, 3));
        Cycles d = p.toCycles();
        assertArrayEquals(p.apply(a), d.apply(a));
    }

    
    @Test
    void testSorts() {
        for (int __ = 0; __ < 100; __++) {
            int[] a = ArrayUtil.randomNumbers(100, 50 + (int) (Math.random() * 100));
            Permutation p = Permutation.sorting(a);
            assertTrue(p.sorts(a));
        }
    }

    @Test
    void testSymmetricGroupDistinct() {
        for (int n = 1; n < 8; n++) {
            List<Permutation> sym = Permutation.symmetricGroup(n).collect(Collectors.toList());
            long count = sym.stream().count();
            assertEquals(count, sym.stream().distinct().count());
            assertEquals(count, TestUtil.factorial(n));
        }
        assertEquals(Permutation.symmetricGroup(9).count(), TestUtil.factorial(9));
    }
}

