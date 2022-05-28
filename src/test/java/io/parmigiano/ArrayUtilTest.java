package io.parmigiano;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static io.parmigiano.TestUtil.count;
import static io.parmigiano.TestUtil.duplicateIndexes;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ArrayUtilTest {

    @Test
    void testCombinations() {
        List<Cycles> permutations = Cycles.symmetricGroup(3).toList();
        assertEquals(6, permutations.size());
        Set<Cycles> perms = new HashSet<>();
        for (Cycles perm : permutations) {
            assertTrue(perms.add(perm));
        }
        for (Cycles perm : Cycles.symmetricGroup(3).collect(Collectors.toList())) {
            assertFalse(perms.add(perm));
        }
    }

    @Test
    void testCartesian() {
        int total = 0;
        int offDiagonal = 0;
        List<Cycles> a = Cycles.symmetricGroup(3).toList();
        for (Cycles[] permutation : TestUtil.cartesian(a, a)) {
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
        List<Cycles> a = Cycles.symmetricGroup(5).toList();
        List<Cycles> center = TestUtil.center(a);
        assertEquals(1, center.size());
        assertTrue(center.get(0).isIdentity());
    }

    @Test
    void testClosed() {
        Cycles id = Cycles.fromRanking(0, 1, 2, 3);
        Cycles p = Cycles.fromRanking(1, 0, 2, 3);
        Cycles k = Cycles.fromRanking(0, 1, 3, 2);
        Cycles p2 = Cycles.fromRanking(1, 2, 0, 3);
        Assertions.assertTrue(TestUtil.isClosed(List.of(id)));
        Assertions.assertTrue(TestUtil.isClosed(List.of(id, p)));
        Assertions.assertTrue(TestUtil.isClosed(List.of(id, p2, p2.pow(2))));
        Assertions.assertTrue(TestUtil.isClosed(List.of(id, p, k, Cycles.product(p, k))));
        Assertions.assertFalse(TestUtil.isClosed(List.of(id, p2)));
        Assertions.assertFalse(TestUtil.isClosed(List.of(p)));
        Assertions.assertFalse(TestUtil.isClosed(List.of(id, p, p2)));
        assertTrue(Cycles.product(p, k).pow(2).isIdentity());
    }

    @Test
    void testCommutator5() {
        Assertions.assertEquals(120L, Cycles.symmetricGroup(5).count());
        Assertions.assertTrue(TestUtil.isClosed(Cycles.symmetricGroup(5).toList()));
        Assertions.assertEquals(60, TestUtil.commutator(Cycles.symmetricGroup(5).toList()).size());
        Assertions.assertTrue(TestUtil.isClosed(TestUtil.commutator(Cycles.symmetricGroup(5).toList())));
        Assertions.assertEquals(60, TestUtil.commutator(TestUtil.commutator(Cycles.symmetricGroup(5).toList())).size());
        Assertions.assertTrue(TestUtil.isClosed(TestUtil.commutator(TestUtil.commutator(Cycles.symmetricGroup(5).toList()))));
    }

    @Test
    void testCommutator4() {
        Assertions.assertEquals(24, Cycles.symmetricGroup(4).toList().size());
        Assertions.assertTrue(TestUtil.isClosed(Cycles.symmetricGroup(4).toList()));
        Assertions.assertEquals(12, TestUtil.commutator(Cycles.symmetricGroup(4).toList()).size());
        Assertions.assertTrue(TestUtil.isClosed(TestUtil.commutator(Cycles.symmetricGroup(4).toList())));
        Assertions.assertEquals(4, TestUtil.commutator(TestUtil.commutator(Cycles.symmetricGroup(4).toList())).size());
        Assertions.assertTrue(TestUtil.isClosed(TestUtil.commutator(TestUtil.commutator(Cycles.symmetricGroup(4).toList()))));
        Assertions.assertEquals(1, TestUtil.commutator(TestUtil.commutator(TestUtil.commutator(Cycles.symmetricGroup(4).toList()))).size());
        Assertions.assertTrue(TestUtil.isClosed(TestUtil.commutator(TestUtil.commutator(TestUtil.commutator(Cycles.symmetricGroup(4).toList())))));
    }

    @Test
    void testCommutatorEven() {
        for (int i = 3; i < 7; i += 1) {
            List<Cycles> sym = Cycles.symmetricGroup(i).toList();
            Assertions.assertEquals(TestUtil.factorial(i), sym.size());
            Assertions.assertEquals(0, TestUtil.signatureSum(sym));
            Assertions.assertEquals(sym.size() / 2, TestUtil.signatureSum(TestUtil.commutator(sym)));
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
            int[] ints = ArrayUtil.randomNumbers(Integer.MIN_VALUE, Integer.MIN_VALUE + radius, 100);
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
            int[] ints = ArrayUtil.randomNumbers(Integer.MAX_VALUE - radius, Integer.MAX_VALUE, 100);
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
                    int[] ints = ArrayUtil.randomNumbers(low, low + radius, 10);
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
            int[] ints = ArrayUtil.randomNumbers(maxNumber, maxNumber + 2 + (int) (Math.random() * 20));
            int[] pair = TestUtil.duplicateIndexes(ints, 0);
            assertTrue(TestUtil.count(ints, ints[pair[0]]) > 1);
            assertEquals(ints[pair[0]], ints[pair[1]]);
            assertEquals(ArrayUtil.indexOf(ints, ints[pair[0]], 0), pair[0]);
        }
    }

    @Test
    void testDuplicateIndexes3() {
        int[] ints = {0, 1, 4, 1, 2, 6, 5, 2, 0, 0, 6, 0};
        Assertions.assertEquals(1, TestUtil.duplicateIndexes(ints, 0)[0]);
    }

    @Test
    void testDuplicateIndexes4() {
        for (int i = 0; i < 1000; i += 1) {
            int maxNumber = 100;
            MyInt[] ints = MyInt.box(ArrayUtil.randomNumbers(maxNumber, maxNumber + 2 + (int) (Math.random() * 20)));
            int[] pair = duplicateIndexes(ints, MyInt.COMP);
            assertTrue(count(ints, ints[pair[0]]) > 1);
            assertEquals(ints[pair[0]], ints[pair[1]]);
        }
    }

    @Test
    void testFactorial() {
        Assertions.assertEquals(1, TestUtil.factorial(0));
        Assertions.assertEquals(1, TestUtil.factorial(1));
        Assertions.assertEquals(2, TestUtil.factorial(2));
        Assertions.assertEquals(6, TestUtil.factorial(3));
        Assertions.assertEquals(24, TestUtil.factorial(4));
        Assertions.assertEquals(120, TestUtil.factorial(5));
        Assertions.assertEquals(19, TestUtil.factorial(19) / TestUtil.factorial(18));
        Assertions.assertEquals(20, TestUtil.factorial(20) / TestUtil.factorial(19));
    }

    @Test
    void testFindCommutator() {
        Cycles p = Cycles.create(1, 2);
        Cycles q = Cycles.create(0, 1);
        Assertions.assertEquals(Cycles.fromRanking(1, 2, 0), Cycles.product(p.invert(), q.invert(), p, q));
    }

    @Test
    void testEvenCommutator() {
        Cycles p = Cycles.create(0, 4, 1);
        Cycles q = Cycles.create(0, 3, 2, 1, 4);
        Assertions.assertEquals(Cycles.fromRanking(1, 2, 0), Cycles.product(p.invert(), q.invert(), p, q));
    }

    @Test
    void testEvenCommutator2() {
        Cycles p = Cycles.create(0, 3, 1);
        Cycles q = Cycles.create(0, 4, 2, 1, 3);
        Assertions.assertEquals(Cycles.fromRanking(1, 2, 0), Cycles.product(p.invert(), q.invert(), p, q));
    }

    @Test
    void testRange() {
        assertArrayEquals(new int[]{10, 9, 8, 7}, ArrayUtil.range(10, 7, true));
        assertArrayEquals(new int[]{10, 9, 8}, ArrayUtil.range(10, 7, false));
        assertArrayEquals(new int[]{7, 8, 9, 10}, ArrayUtil.range(7, 10, true));
        assertArrayEquals(new int[]{7, 8, 9}, ArrayUtil.range(7, 10, false));
        assertArrayEquals(new int[]{7}, ArrayUtil.range(7, 7, true));
        assertArrayEquals(new int[]{}, ArrayUtil.range(7, 7, false));
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
