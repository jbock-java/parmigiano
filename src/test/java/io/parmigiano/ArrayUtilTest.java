package io.parmigiano;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ArrayUtilTest {

    @Test
    void testCombinations() {
        List<Permutation> permutations = Permutation.symmetricGroup(3).toList();
        assertEquals(6, permutations.size());
        Set<Permutation> perms = new HashSet<>();
        for (Permutation perm : permutations) {
            assertTrue(perms.add(perm));
        }
        for (Permutation perm : Permutation.symmetricGroup(3).collect(Collectors.toList())) {
            assertFalse(perms.add(perm));
        }
    }

    @Test
    void testCartesian() {
        int total = 0;
        int offDiagonal = 0;
        List<Permutation> a = Permutation.symmetricGroup(3).toList();
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
        List<Permutation> a = Permutation.symmetricGroup(5).toList();
        List<Permutation> center = TestUtil.center(a);
        assertEquals(1, center.size());
        assertTrue(center.get(0).isIdentity());
    }

    @Test
    void testClosed() {
        Permutation id = Permutation.fromRanking(0, 1, 2, 3);
        Permutation p = Permutation.fromRanking(1, 0, 2, 3);
        Permutation k = Permutation.fromRanking(0, 1, 3, 2);
        Permutation p2 = Permutation.fromRanking(1, 2, 0, 3);
        Assertions.assertTrue(TestUtil.isClosed(List.of(id)));
        Assertions.assertTrue(TestUtil.isClosed(List.of(id, p)));
        Assertions.assertTrue(TestUtil.isClosed(List.of(id, p2, p2.pow(2))));
        Assertions.assertTrue(TestUtil.isClosed(List.of(id, p, k, Permutation.product(p, k))));
        Assertions.assertFalse(TestUtil.isClosed(List.of(id, p2)));
        Assertions.assertFalse(TestUtil.isClosed(List.of(p)));
        Assertions.assertFalse(TestUtil.isClosed(List.of(id, p, p2)));
        assertTrue(Permutation.product(p, k).pow(2).isIdentity());
    }

    @Test
    void testCommutator5() {
        assertEquals(120L, Permutation.symmetricGroup(5).count());
        Assertions.assertTrue(TestUtil.isClosed(Permutation.symmetricGroup(5).toList()));
        assertEquals(60, TestUtil.commutator(Permutation.symmetricGroup(5).toList()).size());
        Assertions.assertTrue(TestUtil.isClosed(TestUtil.commutator(Permutation.symmetricGroup(5).toList())));
        assertEquals(60, TestUtil.commutator(TestUtil.commutator(Permutation.symmetricGroup(5).toList())).size());
        Assertions.assertTrue(TestUtil.isClosed(TestUtil.commutator(TestUtil.commutator(Permutation.symmetricGroup(5).toList()))));
    }

    @Test
    void testCommutator4() {
        assertEquals(24, Permutation.symmetricGroup(4).toList().size());
        Assertions.assertTrue(TestUtil.isClosed(Permutation.symmetricGroup(4).toList()));
        assertEquals(12, TestUtil.commutator(Permutation.symmetricGroup(4).toList()).size());
        Assertions.assertTrue(TestUtil.isClosed(TestUtil.commutator(Permutation.symmetricGroup(4).toList())));
        assertEquals(4, TestUtil.commutator(TestUtil.commutator(Permutation.symmetricGroup(4).toList())).size());
        Assertions.assertTrue(TestUtil.isClosed(TestUtil.commutator(TestUtil.commutator(Permutation.symmetricGroup(4).toList()))));
        assertEquals(1, TestUtil.commutator(TestUtil.commutator(TestUtil.commutator(Permutation.symmetricGroup(4).toList()))).size());
        Assertions.assertTrue(TestUtil.isClosed(TestUtil.commutator(TestUtil.commutator(TestUtil.commutator(Permutation.symmetricGroup(4).toList())))));
    }

    @Test
    void testCommutatorEven() {
        for (int i = 3; i < 7; i += 1) {
            List<Permutation> sym = Permutation.symmetricGroup(i).toList();
            assertEquals(TestUtil.factorial(i), sym.size());
            assertEquals(0, TestUtil.signatureSum(sym));
            assertEquals(sym.size() / 2, TestUtil.signatureSum(TestUtil.commutator(sym)));
        }
    }

    @Test
    void testDistinctInts() {
        for (int i = 0; i < 1000; i += 1) {
            int[] ints = Rankings.random((int) (Math.random() * 1024));
            Assertions.assertTrue(TestUtil.isDistinct(ints));
        }
    }

    @Test
    void testRandomExtreme() {
        int radius = (int) (50 * Math.random()) + 50;
        HashSet<Integer> seen = new HashSet<Integer>(radius);
        for (int i = 0; i < 1000; i += 1) {
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
        for (int i = 0; i < 1000; i += 1) {
            int[] ints = TestUtil.randomNumbers(Integer.MAX_VALUE - radius, Integer.MAX_VALUE, 100);
            for (int a : ints) {
                assertTrue(a >= Integer.MAX_VALUE - radius,
                        () -> (Integer.MAX_VALUE - a) + " " + radius);
                seen.add(a);
            }
        }
        for (int i = 0; i <= radius; i++) {
            assertTrue(seen.contains(Integer.MAX_VALUE - radius));
        }
    }

    @Test
    void testRandom() {
        for (int radius = 3; radius < 10; radius++) {
            for (int low = -10; low < 4; low++) {
                HashSet<Integer> seen = new HashSet<Integer>(radius);
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
        for (int i = 0; i < 1000; i += 1) {
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
        for (int i = 0; i < 1000; i += 1) {
            int maxNumber = 100;
            List<MyInt> ints = MyInt.box(TestUtil.randomNumbers(maxNumber, maxNumber + 2 + (int) (Math.random() * 20)));
            int[] pair = TestUtil.duplicateIndexes(ints, MyInt.COMP);
            assertTrue(TestUtil.count(ints, ints.get(pair[0])) > 1);
            assertEquals(ints.get(pair[0]), ints.get(pair[1]));
        }
    }

    @Test
    void testFactorial() {
        assertEquals(1, TestUtil.factorial(0));
        assertEquals(1, TestUtil.factorial(1));
        assertEquals(2, TestUtil.factorial(2));
        assertEquals(6, TestUtil.factorial(3));
        assertEquals(24, TestUtil.factorial(4));
        assertEquals(120, TestUtil.factorial(5));
        assertEquals(19, TestUtil.factorial(19) / TestUtil.factorial(18));
        assertEquals(20, TestUtil.factorial(20) / TestUtil.factorial(19));
    }

    @Test
    void testFindCommutator() {
        Permutation p = Permutation.create(1, 2);
        Permutation q = Permutation.create(0, 1);
        assertEquals(Permutation.fromRanking(1, 2, 0), Permutation.product(p.invert(), q.invert(), p, q));
    }

    @Test
    void testEvenCommutator() {
        Permutation p = Permutation.create(0, 4, 1);
        Permutation q = Permutation.create(0, 3, 2, 1, 4);
        assertEquals(Permutation.fromRanking(1, 2, 0), Permutation.product(p.invert(), q.invert(), p, q));
    }

    @Test
    void testEvenCommutator2() {
        Permutation p = Permutation.create(0, 3, 1);
        Permutation q = Permutation.create(0, 4, 2, 1, 3);
        assertEquals(Permutation.fromRanking(1, 2, 0), Permutation.product(p.invert(), q.invert(), p, q));
    }

    @Test
    void testRange() {
        assertArrayEquals(new int[]{10, 9, 8}, ArrayUtil.range(10, 7));
        assertArrayEquals(new int[]{7, 8, 9}, ArrayUtil.range(7, 10));
        assertArrayEquals(new int[]{}, ArrayUtil.range(7, 7));
    }

    @Test
    void testUnique() {
        int[] a = {8, 5, 7, 2, 9, 4, 1, 6, 0, 3};
        assertTrue(ArrayUtil.isUnique(a));
    }

    @Test
    void testCut() {
        int[] a = {8, 5, 7, 2, 9, 4, 1, 6, 0, 3};
        assertArrayEquals(new int[]{5, 7, 2, 9, 4, 1, 6, 0, 3}, ArrayUtil.cut(a, 0));
        assertArrayEquals(new int[]{8, 7, 2, 9, 4, 1, 6, 0, 3}, ArrayUtil.cut(a, 1));
        assertArrayEquals(new int[]{8, 5, 7, 2, 9, 4, 1, 6, 0}, ArrayUtil.cut(a, 9));
    }

    @Test
    void testPaste() {
        int[] a = {8, 5, 7, 2, 9, 4, 1, 6, 0, 3};
        assertArrayEquals(new int[]{0, 8, 5, 7, 2, 9, 4, 1, 6, 0, 3}, ArrayUtil.paste(a, 0, 0));
        assertArrayEquals(new int[]{8, 0, 5, 7, 2, 9, 4, 1, 6, 0, 3}, ArrayUtil.paste(a, 1, 0));
        assertArrayEquals(new int[]{8, 5, 7, 2, 9, 4, 1, 6, 0, 0, 3}, ArrayUtil.paste(a, 9, 0));
        assertArrayEquals(new int[]{8, 5, 7, 2, 9, 4, 1, 6, 0, 3, 0}, ArrayUtil.paste(a, 10, 0));
    }
}
