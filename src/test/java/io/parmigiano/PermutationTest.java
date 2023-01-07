package io.parmigiano;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import static io.parmigiano.MyInt.box;
import static io.parmigiano.Permutation.cycle;
import static io.parmigiano.Permutation.symmetricGroup;
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
        Permutation p = Permutation.cycle(0, 1, 2);
        assertEquals("cab", p.apply("abc"));
    }

    @Test
    void testComp() {
        Permutation p = Permutation.cycle(0, 1, 2);
        assertEquals(Permutation.cycle(0, 1, 2), p);
        assertEquals(List.of("c", "a", "b"), p.apply(TestUtil.symbols(3)));
        assertEquals(List.of("b", "c", "a"), p.compose(p).apply(TestUtil.symbols(3)));
    }

    /* check defining property of composition */
    @Test
    void testComp2() {
        Permutation p = Permutation.random(7);
        Permutation p2 = Permutation.random(7);
        for (int i = 0; i < 10; i += 1) {
            assertEquals(p2.apply(p.apply(i)), p2.compose(p).apply(i));
        }
    }

    /* check defining property of apply */
    @Test
    void testApply() {
        int[] a = TestUtil.randomNumbers(100, 200);
        Permutation p = Permutation.random(ThreadLocalRandom.current().nextInt(a.length));
        int[] pa = p.apply(a);
        for (int i = 0; i < a.length; i += 1) {
            int pi = p.apply(i);
            assertEquals(pa[pi], a[i]);
        }
    }

    @Test
    void testIterable() {
        for (int __ = 0; __ < 100; __++) {
            List<MyInt> a = box(TestUtil.randomNumbers(100, 50 + ThreadLocalRandom.current().nextInt(100)));
            Permutation p = Permutation.random((int) (Math.random() * a.size()));
            List<MyInt> applied = p.apply(a);
            List<MyInt> arrayList = new ArrayList<>(a.size());
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

    @Test
    void testInvert() {
        Permutation p = Permutation.cycle(0, 1, 2);
        assertTrue(Permutation.product(p.invert(), p).isIdentity());
        assertTrue(Permutation.product(p, p.invert()).isIdentity());
        assertTrue(Permutation.product(p, p.pow(2)).isIdentity());
        assertTrue(Permutation.product(p.pow(2), p).isIdentity());
        assertTrue(Permutation.product().isIdentity());
        assertTrue(p.pow(0).isIdentity());
        assertFalse(p.pow(1).isIdentity());
        assertFalse(p.pow(2).isIdentity());
        assertEquals(p.pow(0), p.pow(3));
        assertEquals(p.pow(2), Permutation.product(p, p));
        assertEquals(p.pow(1), p);
        assertEquals(p.pow(-1), Permutation.product(p, p));
        assertEquals(p.pow(-1), p.invert());
        assertEquals(p.pow(2), p.compose(p));
        assertEquals("abc", Permutation.product(p, p.invert()).apply("abc"));
    }

    @Test
    void testIdentity() {
        assertTrue(Permutation.identity().isIdentity());
        Assertions.assertTrue(Permutation.cycle(1, 2).compose(1, 2).isIdentity());
        Assertions.assertFalse(Permutation.cycle(1, 2).isIdentity());
    }

    /* test defining property of identity */
    @Test
    void testIdentity2() {
        Permutation identity = Permutation.identity();
        for (int i = 0; i < 10; i += 1) {
            assertEquals(i, identity.apply(i));
        }
    }

    /* Check defining property of inverse */
    @Test
    void testInverse2() {
        Permutation p = Permutation.sorting(new int[]{4, 6, 10, -5, 195, 33, 2});
        for (int i = 0; i <= p.maxMovedIndex(); i += 1) {
            assertEquals(i, p.invert().apply(p.apply(i)));
        }
    }

    @Test
    void cycleEquality() {
        assertEquals(Permutation.cycle(1, 5, 3, 2), Permutation.cycle(5, 3, 2, 1));
        assertEquals(Permutation.cycle(1, 5, 3, 2), Permutation.cycle(2, 1, 5, 3));
        assertNotEquals(Permutation.cycle(1, 5, 3, 2), Permutation.cycle(1, 5, 2, 3));
    }

    @Test
    void cycleApply() {
        assertEquals(List.of("b", "c", "e", "d", "a"),
                Permutation.cycle(0, 4, 2, 1).apply(TestUtil.symbols(5)));
        assertEquals(List.of("c", "b", "e", "d", "a"),
                Permutation.cycle(0, 4, 2).apply(TestUtil.symbols(5)));
        assertEquals(List.of("c", "a", "b"),
                Permutation.cycle(0, 1, 2).apply(TestUtil.symbols(3)));
    }

    @Test
    void testCycleApply() {
        assertEquals(List.of("c", "a", "b"),
                Permutation.product(Permutation.cycle(0, 1), Permutation.cycle(1, 2)).apply(TestUtil.symbols(3)));
        assertEquals(List.of("c", "a", "b"), Permutation.cycle(0, 1, 2).apply(TestUtil.symbols(3)));
        assertEquals(List.of("a", "c", "b"), Permutation.product(Permutation.cycle(0, 1),
                Permutation.product(Permutation.cycle(0, 1), Permutation.cycle(1, 2))).apply(TestUtil.symbols(3)));
    }

    @Test
    void testCycleEquals() {
        assertTrue(Permutation.product(Permutation.cycle(1, 2), Permutation.cycle(2, 1)).isIdentity());
        assertEquals(Permutation.cycle(2, 3), Permutation.product(Permutation.cycle(1, 2),
                Permutation.product(Permutation.cycle(1, 2), Permutation.cycle(2, 3))));
    }

    @Test
    void testCycleLaw() {
        Permutation longest = Permutation.cycle(2, 4, 1, 11, 3);
        assertEquals(Permutation.product(Permutation.cycle(2, 4),
                Permutation.cycle(4, 1, 11, 3)), longest);
    }

    @Test
    void testSort() {
        int[] x = new int[]{4, 6, 10, -5, 195, 33, 2};
        int[] y = Arrays.copyOf(x, x.length);
        Arrays.sort(y);
        Permutation p = Permutation.sorting(x);
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
        Permutation unsort = Permutation.sorting(x).invert();
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
        Permutation p = Permutation.sorting(distinct);
        for (int i = 0; i < sorted.length; i += 1) {
            distinct[i] = sorted[p.apply(i)];
        }
    }

    @Test
    void testSortInvertComparator() {
        List<MyInt> x = box(new int[]{4, 6, 10, -5, 195, 33, 2});
        Permutation unsort = Permutation.sorting(x, MyInt.COMP).invert();
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
        assertThrows(IllegalArgumentException.class, () -> Permutation.identity().apply(-1));
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
        assertArrayEquals(classicSort(a), Permutation.sorting(a).apply(a));
    }

    @Test
    void testCycleLength() {
        Permutation swap01 = Permutation.cycle(0, 1);
        assertEquals(1, swap01.maxMovedIndex());
    }

    @Test
    void testFromQuickly() {
        Permutation p = Permutation.taking(new int[]{1, 2, 3}).to(new int[]{2, 3, 1});
        assertEquals(List.of("b", "c", "a"), p.apply(TestUtil.symbols(3)));
    }

    /* check defining property of from */
    private void testFromQuickly2() {
        int size = 2048;
        int[] a = Rankings.random(size);
        Permutation random;
        do {
            random = Permutation.random((int) (Math.random() * size));
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
        assertEquals("213", Permutation.cycle(0, 1).apply("123"));
        assertEquals("23145", Permutation.cycle(2, 1, 0).apply("12345"));
        assertEquals("14235", Permutation.cycle(1, 2, 3).apply("12345"));
    }

    /* various assertions about Sym(5) */
    @Test
    void testCyclesAndTranspositions() {
        int sign = 0;
        for (Permutation p : symmetricGroup(5)) {
            int order = p.order();
            sign += p.signature();
            if (order > 5) {
                assertEquals(6, order);
                assertEquals(2, p.numCycles());
            } else if (order == 5) {
                assertEquals(1, p.numCycles());
            } else if (order == 4) {
                assertEquals(1, p.numCycles());
            } else if (order == 3) {
                assertEquals(1, p.numCycles());
            } else if (order == 2) {
                assertTrue(p.numCycles() <= 2);
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
        assertEquals(0, p.maxMovedIndex());
        assertArrayEquals(new int[0], p.apply(new int[0]));
        assertEquals(0, p.numCycles());
    }

    /* example from README */
    @Test
    void testPprod() {
        Permutation c0 = Permutation.cycle(7, 9);
        Permutation c1 = Permutation.cycle(1, 4, 8, 10, 3, 6, 11);
        Permutation c2 = Permutation.cycle(0, 2, 5);
        assertEquals("Hello world!", c0.compose(c1).compose(c2).invert().apply(" !Hdellloorw"));
    }

    /* making sure sorting does what we think it does */
    @Test
    void testDegenerate() {
        int[] a = new int[]{3, 3, 3, 3, 3, 3, 3};
        assertFalse(Permutation.sorting(a).isIdentity());
    }

    @Test
    void testSorts() {
        for (int __ = 0; __ < 100; __++) {
            int[] a = TestUtil.randomNumbers(100, 50 + (int) (Math.random() * 100));
            Permutation p = Permutation.sorting(a);
            assertTrue(isSorted(p.apply(a)));
        }
    }

    static boolean isSorted(int[] a) {
        if (a.length <= 1) {
            return true;
        }
        int cur = a[0];
        for (int i = 1; i < a.length; i++) {
            if (a[i] < cur) {
                return false;
            }
            cur = a[i];
        }
        return true;
    }

    @Test
    void testSymmetricGroupDistinct() {
        for (int n = 1; n < 6; n++) {
            List<Permutation> sym = symmetricGroup(n);
            long count = sym.size();
            assertEquals(count, sym.stream().distinct().count());
            assertEquals(count, TestUtil.factorial(n));
        }
        assertEquals(symmetricGroup(7).size(), TestUtil.factorial(7));
    }

    @Test
    void testToString() {
        assertEquals("(1 4) (2 3)", Permutation.cycle(1, 4).compose(Permutation.cycle(2, 3)).toString());
        assertEquals("(1 3 4 2)", Permutation.cycle(1, 3, 4, 2).toString());
        assertEquals("id", Permutation.cycle(0, 1).compose(Permutation.cycle(0, 1)).toString());
    }

    @Test
    void testPrint() {
        assertEquals("create(1, 4).compose(create(2, 3))", Permutation.cycle(1, 4).compose(Permutation.cycle(2, 3)).print());
        assertEquals("create(1, 3, 4, 2)", Permutation.cycle(1, 3, 4, 2).print());
        assertEquals("identity()", Permutation.cycle(0, 1).compose(Permutation.cycle(0, 1)).print());
    }

    @Test
    void testNormalize() {
        assertEquals("(4 1 3)", cycle(1, 3, 4).normalize().toString());
        assertEquals("(4 1 3)", cycle(3, 4, 1).normalize().toString());
        assertEquals("(4 1) (3 2)", cycle(2, 3).compose(cycle(1, 4)).normalize().toString());
        assertEquals("(4 1) (3 2)", cycle(1, 4).compose(cycle(2, 3)).normalize().toString());
    }
}

