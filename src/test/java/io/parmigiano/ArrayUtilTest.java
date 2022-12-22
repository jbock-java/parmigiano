package io.parmigiano;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

import static io.parmigiano.Permutation.symmetricGroup;
import static io.parmigiano.TestUtil.commutator;
import static io.parmigiano.TestUtil.factorial;
import static io.parmigiano.TestUtil.isClosed;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ArrayUtilTest {

    @Test
    void testCombinations() {
        List<Permutation> permutations = symmetricGroup(3);
        assertEquals(6, permutations.size());
        Set<Permutation> perms = new HashSet<>();
        for (Permutation perm : permutations) {
            assertTrue(perms.add(perm));
        }
        for (Permutation perm : symmetricGroup(3)) {
            assertFalse(perms.add(perm));
        }
    }

    @Test
    void testCartesian() {
        int total = 0;
        int offDiagonal = 0;
        List<Permutation> a = symmetricGroup(3);
        for (Permutation[] permutation : TestUtil.cartesian(a, a)) {
            total += 1;
            if (permutation[0] != permutation[1]) {
                offDiagonal += 1;
            }
        }
        assertEquals(36, total);
        assertEquals(30, offDiagonal);
    }

    @Test
    void testCenter() {
        List<Permutation> a = symmetricGroup(5);
        List<Permutation> center = TestUtil.center(a);
        assertEquals(1, center.size());
        assertTrue(center.get(0).isIdentity());
    }

    @Test
    void testClosed() {
        Permutation id = Permutation.identity();
        Permutation p = Permutation.cycle(0, 1);
        Permutation k = Permutation.cycle(2, 3);
        Permutation p2 = Permutation.cycle(0, 1, 2);
        assertTrue(isClosed(List.of(id)));
        assertTrue(isClosed(List.of(id, p)));
        assertTrue(isClosed(List.of(id, p2, p2.pow(2))));
        assertTrue(isClosed(List.of(id, p, k, Permutation.product(p, k))));
        Assertions.assertFalse(isClosed(List.of(id, p2)));
        Assertions.assertFalse(isClosed(List.of(p)));
        Assertions.assertFalse(isClosed(List.of(id, p, p2)));
        assertTrue(Permutation.product(p, k).pow(2).isIdentity());
    }

    @Test
    void testCommutator5() {
        assertEquals(120, symmetricGroup(5).size());
        assertTrue(isClosed(symmetricGroup(5)));
        assertEquals(60, commutator(symmetricGroup(5)).size());
        assertTrue(isClosed(commutator(symmetricGroup(5))));
        assertEquals(60, commutator(commutator(symmetricGroup(5))).size());
        assertTrue(isClosed(commutator(commutator(symmetricGroup(5)))));
    }

    @Test
    void testCommutator4() {
        assertEquals(24, symmetricGroup(4).size());
        assertTrue(isClosed(symmetricGroup(4)));
        assertEquals(12, commutator(symmetricGroup(4)).size());
        assertTrue(isClosed(commutator(symmetricGroup(4))));
        assertEquals(4, commutator(commutator(symmetricGroup(4))).size());
        assertTrue(isClosed(commutator(commutator(symmetricGroup(4)))));
        assertEquals(1, commutator(commutator(commutator(symmetricGroup(4)))).size());
        assertTrue(isClosed(commutator(commutator(commutator(symmetricGroup(4))))));
    }

    @Test
    void testCommutatorEven() {
        for (int i = 2; i < 6; i++) {
            List<Permutation> sym = symmetricGroup(i);
            assertEquals(factorial(i), sym.size());
            assertEquals(0, TestUtil.signatureSum(sym));
            assertEquals(sym.size() / 2, TestUtil.signatureSum(commutator(sym)));
        }
    }

    @Test
    void testDistinctInts() {
        for (int i = 0; i < 100; i++) {
            int[] ints = Rankings.random(ThreadLocalRandom.current().nextInt(1024));
            assertTrue(TestUtil.isDistinct(ints));
        }
    }

    @Test
    void testRandomExtreme() {
        int radius = ThreadLocalRandom.current().nextInt(50) + 50;
        Set<Integer> seen = new HashSet<>(radius);
        for (int i = 0; i < 100; i += 1) {
            int[] ints = TestUtil.randomNumbers(Integer.MIN_VALUE, Integer.MIN_VALUE + radius, 100);
            for (int a : ints) {
                assertTrue(a <= Integer.MIN_VALUE + radius,
                        () -> String.format("%d %d %d", radius, a, Integer.MIN_VALUE + radius));
                seen.add(a);
            }
        }
        for (int i = 0; i <= radius; i++) {
            assertTrue(seen.contains(Integer.MIN_VALUE + i));
        }
        seen = new HashSet<>(radius);
        int maxValue = Integer.MAX_VALUE / 2;
        for (int i = 0; i < 100; i += 1) {
            int[] ints = TestUtil.randomNumbers(maxValue - radius, maxValue, 100);
            for (int a : ints) {
                assertTrue(a >= maxValue - radius,
                        () -> (maxValue - a) + " " + radius);
                seen.add(a);
            }
        }
        for (int i = 0; i <= radius; i++) {
            assertTrue(seen.contains(maxValue - radius));
        }
    }

    @Test
    void testRandom() {
        for (int radius = 3; radius < 10; radius++) {
            for (int low = -10; low < 4; low++) {
                Set<Integer> seen = new HashSet<>(radius);
                for (int i = 0; i < 100; i += 1) {
                    int[] ints = TestUtil.randomNumbers(low, low + radius, 10);
                    for (int a : ints) {
                        assertTrue(a <= low + radius, String.format("%d %d", low, a));
                        seen.add(a);
                    }
                }
                for (int i = 0; i <= radius; i++) {
                    assertTrue(seen.contains(low + i));
                }
            }
        }
    }

    @Test
    void testDuplicateIndexes() {
        int[] ints = TestUtil.duplicateIndexes(new int[]{1, 2, 1});
        assertTrue(Arrays.equals(new int[]{0, 2}, ints) || Arrays.equals(new int[]{2, 0}, ints));
    }

    @Test
    void testDuplicateIndexes2() {
        for (int i = 0; i < 100; i += 1) {
            int maxNumber = 100;
            int[] ints = TestUtil.randomNumbers(maxNumber, maxNumber + 2 + (int) (Math.random() * 20));
            int[] pair = TestUtil.duplicateIndexes(ints);
            assertTrue(TestUtil.count(ints, ints[pair[0]]) > 1);
            assertEquals(ints[pair[0]], ints[pair[1]]);
        }
    }

    @Test
    void testDuplicateIndexes3() {
        int[] ints = {0, 1, 4, 1, 2, 6, 5, 2, 0, 0, 6, 0};
        int[] dupes = TestUtil.duplicateIndexes(ints);
        assertEquals(ints[dupes[1]], ints[dupes[0]]);
    }

    @Test
    void testDuplicateIndexes4() {
        for (int i = 0; i < 100; i += 1) {
            int maxNumber = 100;
            List<MyInt> ints = MyInt.box(TestUtil.randomNumbers(maxNumber, maxNumber + 2 + ThreadLocalRandom.current().nextInt(20)));
            int[] pair = TestUtil.duplicateIndexes(ints, MyInt.COMP);
            assertTrue(TestUtil.count(ints, ints.get(pair[0]), (n1, n2) -> MyInt.COMP.compare(n1, n2) == 0) > 1);
            assertEquals(0, MyInt.COMP.compare(ints.get(pair[0]), ints.get(pair[1])));
        }
    }

    @Test
    void testFactorial() {
        assertEquals(1, factorial(0));
        assertEquals(1, factorial(1));
        assertEquals(2, factorial(2));
        assertEquals(6, factorial(3));
        assertEquals(24, factorial(4));
        assertEquals(120, factorial(5));
        assertEquals(8, factorial(8) / factorial(7));
    }

    @Test
    void testFindCommutator() {
        Permutation p = Permutation.cycle(1, 2);
        Permutation q = Permutation.cycle(0, 1);
        assertEquals(Permutation.cycle(0, 1, 2), Permutation.product(p.invert(), q.invert(), p, q));
    }

    @Test
    void testEvenCommutator() {
        Permutation p = Permutation.cycle(0, 4, 1);
        Permutation q = Permutation.cycle(0, 3, 2, 1, 4);
        assertEquals(Permutation.cycle(0, 1, 2), Permutation.product(p.invert(), q.invert(), p, q));
    }

    @Test
    void testEvenCommutator2() {
        Permutation p = Permutation.cycle(0, 3, 1);
        Permutation q = Permutation.cycle(0, 4, 2, 1, 3);
        assertEquals(Permutation.cycle(0, 1, 2), Permutation.product(p.invert(), q.invert(), p, q));
    }

    @Test
    void testRange() {
        assertArrayEquals(new int[]{10, 9, 8}, ArrayUtil.range(10, 7));
        assertArrayEquals(new int[]{7, 8, 9}, ArrayUtil.range(7, 10));
        assertArrayEquals(new int[]{}, ArrayUtil.range(7, 7));
    }
}
