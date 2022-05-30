package io.parmigiano;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.IntFunction;
import java.util.stream.IntStream;

import static io.parmigiano.Rankings.nextOffset;
import static io.parmigiano.Rankings.shift;
import static io.parmigiano.Rankings.unshift;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TestRankings {

    @Test
    void testSortRandom() {
        for (int __ = 0; __ < 100; __ += 1) {
            int[] a = TestUtil.randomNumbers(100, 200);
            assertArrayEquals(ArrayUtil.sortedCopy(a), Permutation.sorting(a).apply(a));
        }
        for (int __ = 0; __ < 100; __ += 1) {
            int[] a = TestUtil.randomNumbers(100, 20);
            assertArrayEquals(ArrayUtil.sortedCopy(a), Permutation.sorting(a).apply(a));
        }
    }

    @Test
    void testSortStrict() {
        for (int __ = 0; __ < 100; __ += 1) {
            List<String> a = TestUtil.symbols(100);
            List<String> shuffled = Permutation.random(a.size()).apply(a);
            assertEquals(a.stream().sorted().toList(), Permutation.sorting(shuffled).apply(shuffled));
        }
    }

    @Test
    void testFromRandom() {
        for (int __ = 0; __ < 10; __ += 1) {
            List<Integer> a = new ArrayList<>(IntStream.range(0, 100).boxed().toList());
            Collections.shuffle(a);
            List<Integer> b = Permutation.random(a.size()).apply(a);
            assertEquals(b, Taking.from(a).to(b).apply(a));
        }
    }

    @Test
    void testMismatch() {
        // throws because b is not a rearrangement of a
        assertThrows(IllegalArgumentException.class, () -> Rankings.from(new int[]{1, 1, 2}, new int[]{1, 2, 2}));
    }

    @Test
    void testSort() {
        for (int __ = 0; __ < 100; __++) {
            int[] a = TestUtil.randomNumbers(100, ThreadLocalRandom.current().nextInt(1000));
            int[] sort = Rankings.sorting(a);
            int[] sorted = Rankings.apply(sort, a);
            int[] unsort = Rankings.invert(sort);
            int[] hopefullyIdentity = TestUtil.comp(sort, unsort);
            assertTrue(TestUtil.isSorted(hopefullyIdentity));
            assertTrue(TestUtil.isSorted(sorted));
            for (int el : a) {
                assertEquals(ArrayUtil.indexOf(a, el), unsort[Arrays.binarySearch(sorted, el)]);
            }
        }
    }

    @Test
    void testSortUnique() {
        for (int __ = 0; __ < 100; __++) {
            List<Integer> rr = new ArrayList<>(IntStream.range(0, 100).boxed().toList());
            Collections.shuffle(rr);
            int[] a = rr.stream().mapToInt(i -> i).toArray();
            int[] sort = Rankings.sorting(a);
            int[] sorted = Rankings.apply(sort, a);
            int[] unsort = Rankings.invert(sort);
            int[] hopefullyIdentity = TestUtil.comp(sort, unsort);
            assertTrue(TestUtil.isSorted(hopefullyIdentity));
            assertTrue(TestUtil.isSorted(sorted));
            for (int el : a) {
                assertEquals(ArrayUtil.indexOf(a, el), unsort[Arrays.binarySearch(sorted, el)]);
            }
        }
    }

    @Test
    void testSort2() {
        int[] a = new int[]{2, 3, 5, 2};
        int[] sort = Rankings.sorting(a);
        int[] sorted = Rankings.apply(sort, a);
        int[] unsort = Rankings.invert(sort);
        int[] hopefullyIdentity = TestUtil.comp(sort, unsort);
        assertTrue(TestUtil.isSorted(hopefullyIdentity));
        assertTrue(TestUtil.isSorted(sorted));
    }

    @Test
    void testNextOffset() {
        int[] sorted = {0, 0, 1, 3, 3, 3, 4, 4};
        assertEquals(1, nextOffset(sorted, 0, 0));
        assertEquals(-1, nextOffset(sorted, 1, 0));
        assertEquals(0, nextOffset(sorted, 2, 0));
        assertEquals(1, nextOffset(sorted, 3, 0));
        assertEquals(2, nextOffset(sorted, 3, 1));
        assertEquals(0, nextOffset(sorted, 3, 2));
        assertEquals(1, nextOffset(sorted, 4, 0));
        assertEquals(-1, nextOffset(sorted, 4, 1));
        assertEquals(-1, nextOffset(sorted, 5, 0));
        assertEquals(-2, nextOffset(sorted, 5, -1));
        assertEquals(1, nextOffset(sorted, 6, 0));
        assertEquals(-1, nextOffset(sorted, 7, 0));
    }

    private IntFunction<List<Integer>> offsets(int[] sorted) {
        return idx -> {
            List<Integer> result = new ArrayList<>();
            int offset = 0;
            while ((offset = nextOffset(sorted, idx, offset)) != 0) {
                result.add(offset);
            }
            return result;
        };
    }

    @Test
    void testNextOffsetIterate() {
        int[] a = new int[]{3, 3, 3, 3, 3, 3, 3};
        int[] sorted = {0, 1, 3, 3, 3, 3, 4, 4, 4, 5};
        IntFunction<List<Integer>> offsets = offsets(sorted);
        assertEquals(List.of(), offsets.apply(0));
        assertEquals(List.of(), offsets.apply(1));
        assertEquals(List.of(1, 2, 3), offsets.apply(2));
        assertEquals(List.of(1, 2, -1), offsets.apply(3));
        assertEquals(List.of(1, -1, -2), offsets.apply(4));
        assertEquals(List.of(-1, -2, -3), offsets.apply(5));
        assertEquals(List.of(1, 2), offsets.apply(6));
        assertEquals(List.of(1, -1), offsets.apply(7));
        assertEquals(List.of(-1, -2), offsets.apply(8));
        assertEquals(List.of(), offsets.apply(9));
    }

    @Test
    void testNextOffsetIterateDegenerate() {
        int[] sorted = new int[]{3, 3, 3, 3, 3, 3, 3};
        IntFunction<List<Integer>> offsets = offsets(sorted);
        assertEquals(List.of(1, 2, 3, 4, 5, 6), offsets.apply(0));
        assertEquals(List.of(1, 2, 3, 4, 5, -1), offsets.apply(1));
        assertEquals(List.of(1, 2, 3, 4, -1, -2), offsets.apply(2));
        assertEquals(List.of(1, 2, 3, -1, -2, -3), offsets.apply(3));
        assertEquals(List.of(1, 2, -1, -2, -3, -4), offsets.apply(4));
        assertEquals(List.of(1, -1, -2, -3, -4, -5), offsets.apply(5));
        assertEquals(List.of(-1, -2, -3, -4, -5, -6), offsets.apply(6));
    }

    @Test
    void testShift() {
        assertArrayEquals(new int[]{-2, -1, 0, 1, 2}, IntStream.of(-2, -1, 0, 1, 2)
                .map(i1 -> unshift(shift(i1))).toArray());
        assertArrayEquals(new int[]{-2, -1, 1, 2}, IntStream.of(-2, -1, 1, 2)
                .map(i -> shift(unshift(i))).toArray());
    }

    @Test
    void testNextOffsetList() {
        List<Integer> sorted = List.of(0, 0, 1, 3, 3, 3, 4, 4);
        assertEquals(1, nextOffset(sorted, 0, 0, Integer::equals));
        assertEquals(-1, nextOffset(sorted, 1, 0, Integer::equals));
        assertEquals(0, nextOffset(sorted, 2, 0, Integer::equals));
        assertEquals(1, nextOffset(sorted, 3, 0, Integer::equals));
        assertEquals(2, nextOffset(sorted, 3, 1, Integer::equals));
        assertEquals(0, nextOffset(sorted, 3, 2, Integer::equals));
        assertEquals(1, nextOffset(sorted, 4, 0, Integer::equals));
        assertEquals(-1, nextOffset(sorted, 4, 1, Integer::equals));
        assertEquals(-1, nextOffset(sorted, 5, 0, Integer::equals));
        assertEquals(-2, nextOffset(sorted, 5, -1, Integer::equals));
        assertEquals(1, nextOffset(sorted, 6, 0, Integer::equals));
        assertEquals(-1, nextOffset(sorted, 7, 0, Integer::equals));
    }

    @Test
    void testSorts() {
        int[] ranking = {0, 3, 1, 4, 2};
        int[] a = {0, 4, 2, 4, 3};
        assertTrue(TestUtil.isSorted(Rankings.apply(ranking, a)));
        assertTrue(TestUtil.sorts(ranking, a));
    }

    @Test
    void testSorts2() {
        for (int __ = 0; __ < 100; __++) {
            int[] a = TestUtil.randomNumbers(100, ThreadLocalRandom.current().nextInt(100) + 20);
            int[] ranking = Rankings.sorting(a);
            assertTrue(TestUtil.isSorted(Rankings.apply(ranking, a)));
            assertTrue(TestUtil.sorts(ranking, a));
        }
    }
}
