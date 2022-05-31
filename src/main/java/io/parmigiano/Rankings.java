package io.parmigiano;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.function.BiPredicate;
import java.util.stream.Stream;

import static io.parmigiano.ArrayUtil.checkLength;
import static io.parmigiano.ArrayUtil.negativeFailure;
import static io.parmigiano.Preconditions.checkState;
import static java.lang.System.arraycopy;
import static java.util.Arrays.binarySearch;

/**
 * A collection of methods that return rankings, or operate on rankings.
 */
final class Rankings {

    private Rankings() {
    }

    private static final Comparator<int[]> COMPARE_FIRST = Comparator.comparingInt(a -> a[0]);

    /**
     * Ensure that the input is a ranking.
     * @param a an array
     * @throws java.lang.IllegalArgumentException if {@code a} is not a valid ranking
     */
    static void checkRanking(int[] a) {
        boolean[] used = new boolean[a.length];
        for (int i : a) {
            if (i < 0)
                throw new IllegalArgumentException("found negative number in ranking: " + i);
            if (i >= a.length)
                throw new IllegalArgumentException("out-of-bounds value in ranking: " + i);
            if (used[i])
                throw new IllegalArgumentException("duplicate number in ranking: " + i);
            used[i] = true;
        }
    }

    /**
     * Calculate the inverse ranking.
     * This method does not check if the input is indeed a ranking and may have unexpected results otherwise.
     * @param ranking a ranking
     * @return the inverse ranking
     */
    static int[] invert(int[] ranking) {
        int[][] rankingWithIndex = ArrayUtil.withIndex(ranking);
        Arrays.sort(rankingWithIndex, COMPARE_FIRST);
        int[] inverted = new int[ranking.length];
        for (int i = 0; i < ranking.length; i += 1)
            inverted[i] = rankingWithIndex[i][1];
        return inverted;
    }

    /**
     * Generate a random ranking of given length.
     * @param length a non-negative integer
     * @return a random ranking
     * @throws IllegalArgumentException if {@code length} is negative
     */
    static int[] random(int length) {
        int[] a = ArrayUtil.range(length);
        ArrayUtil.shuffle(a);
        return a;
    }

    /* ================= nextOffset ================= */

    /**
     * Find the next position, after {@code idx + offset}, of {@code sorted[idx]} in a sorted array.
     * For a given element {@code el}, iterating this method over the {@code offset} element, starting with
     * {@code offset = 0}, will enumerate all positions of {@code sorted[idx]} in the sorted array.
     *
     * @param idx the start index
     * @param offset the current offset from the start index
     * @param sorted a sorted array
     * @return the next offset or {@code 0} if there is no next offset
     */
    static int nextOffset(int[] sorted, int idx, int offset) {
        if (offset < 0) {
            int next = idx + offset - 1;
            if (next >= 0 && sorted[next] == sorted[idx]) {
                return offset - 1; // decrement offset
            }
            return 0; // done
        }
        int next = idx + offset + 1;
        if (next < sorted.length && sorted[next] == sorted[idx]) {
            return offset + 1; // increment offset
        }
        if (idx > 0 && sorted[idx - 1] == sorted[idx]) {
            return -1; // continue with negative offset
        }
        return 0; // done
    }

    static <E> int nextOffset(List<E> sorted, int idx, int offset, BiPredicate<E, E> equality) {
        if (offset < 0) {
            int next = idx + offset - 1;
            if (next >= 0 && equality.test(sorted.get(next), sorted.get(idx))) {
                return offset - 1; // decrement offset
            }
            return 0; // done
        }
        int next = idx + offset + 1;
        if (next < sorted.size() && equality.test(sorted.get(next), sorted.get(idx))) {
            return offset + 1; // increment offset
        }
        if (idx > 0 && equality.test(sorted.get(idx - 1), sorted.get(idx))) {
            return -1; // continue with negative offset
        }
        return 0; // done
    }

    /* ================= shift ================= */

    /**
     * Encode an int as a non-zero int
     * @param i an int
     * @return {@code i + 1} if {@code i} is non-negative, otherwise {@code i}
     */
    static int shift(int i) {
        return i >= 0 ? i + 1 : i;
    }

    /**
     * Undo the shift
     * @param shifted a non-zero number
     * @return {@code shifted - 1} if {@code shifted} is positive, otherwise {@code shifted}
     * @throws IllegalArgumentException if the input is zero
     */
    static int unshift(int shifted) {
        if (shifted == 0) {
            throw new IllegalArgumentException("zero is not allowed");
        }
        return shifted > 0 ? shifted - 1 : shifted;
    }

    /* ================= sorting ================= */

    /**
     * Produce a particular ranking that sorts the input when applied to it.
     * For each index {@code i < a.length}, the return value
     * satisfies the following property.
     * Let
     * <pre><code>
     *   int[] sorting = sorting(a);
     *   int[] sorted = apply(sorting, a);
     *   int[] unsort = invert(sorting);
     *   int idx = Arrays.binarySearch(sorted, el);
     * </code></pre>
     * then for each index {@code i < a.length}, the following is true:
     * <pre><code>
     *   ArrayUtil.indexOf(a, el, 0) == unsort[idx]
     * </code></pre>
     * @param a an array
     * @return a ranking that sorts the input
     * @see #apply(int[], int[])
     */
    static int[] sorting(int[] a) {
        int[] sorted = ArrayUtil.sortedCopy(a);
        int[] ranking = new int[a.length];
        int[] offsets = new int[a.length];
        for (int i = 0; i < a.length; i++) {
            int idx = binarySearch(sorted, a[i]);
            if (offsets[idx] == 0) {
                ranking[i] = idx;
                offsets[idx] = 1; // shift(0)
            } else {
                // a contains duplicates
                int offset = unshift(offsets[idx]);
                int newOffset = nextOffset(sorted, idx, offset);
                ranking[i] = idx + newOffset;
                offsets[idx] = shift(newOffset);
            }
        }
        checkRanking(ranking);
        return ranking;
    }

    static <E extends Comparable<E>> int[] sorting(List<E> a) {
        List<E> sorted = a.stream().sorted().toList();
        int[] ranking = new int[a.size()];
        int[] offsets = new int[a.size()];
        for (int i = 0; i < a.size(); i++) {
            int idx = Collections.binarySearch(sorted, a.get(i));
            if (offsets[idx] == 0) {
                ranking[i] = idx;
                offsets[idx] = 1; // shift(0)
            } else {
                // a contains duplicates
                int offset = unshift(offsets[idx]);
                int newOffset = nextOffset(
                        sorted,
                        idx,
                        offset,
                        (e1, e2) -> e1.compareTo(e2) == 0);
                ranking[i] = idx + newOffset;
                offsets[idx] = shift(newOffset);
            }
        }
        return ranking;
    }

    static <E> int[] sorting(List<E> a, Comparator<E> comp) {
        List<E> sorted = a.stream().sorted(comp).toList();
        int[] ranking = new int[a.size()];
        int[] offsets = new int[a.size()];
        for (int i = 0; i < a.size(); i++) {
            int idx = Collections.binarySearch(sorted, a.get(i), comp);
            if (offsets[idx] == 0) {
                ranking[i] = idx;
                offsets[idx] = 1; // shift(0)
            } else {
                // a contains duplicates
                int offset = unshift(offsets[idx]);
                int newOffset = nextOffset(
                        sorted,
                        idx,
                        offset,
                        (e1, e2) -> comp.compare(e1, e2) == 0);
                ranking[i] = idx + newOffset;
                offsets[idx] = shift(newOffset);
            }
        }
        return ranking;
    }

    /* ================= from ================= */

    /**
     * Produce a particular ranking that produces {@code b} when applied to {@code a}.
     * @param a an array
     * @param b an array
     * @return a ranking that produces {@code b} when applied to {@code a}
     * @throws java.lang.IllegalArgumentException if {@code b} can not be obtained by rearranging {@code a}
     * @throws java.lang.NullPointerException if any argument is {@code null}
     * @see #apply(int[], int[])
     */
    static int[] from(int[] a, int[] b) {
        ArrayUtil.checkEqualLength(a, b);
        int[] ranking = new int[a.length];
        for (int i = 0; i < a.length; i += 1) {
            int indexInB = ArrayUtil.indexOf(b, a[i]);
            checkState(indexInB >= 0, "not found in b: %s", a[i]);
            ranking[i] = indexInB;
        }
        checkRanking(ranking);
        return ranking;
    }

    static <E> int[] from(List<E> a, List<E> b) {
        ArrayUtil.checkEqualLength(a, b);
        int[] ranking = new int[a.size()];
        for (int i = 0; i < a.size(); i += 1) {
            int indexInB = b.indexOf(a.get(i));
            checkState(indexInB >= 0, "not found in b: %s", a.get(i));
            ranking[i] = indexInB;
        }
        checkRanking(ranking);
        return ranking;
    }

    /**
     * Check where the {@code ranking} moves the index {@code i}.
     * The following is true for all {@code j < a.length}:
     * <code><pre>
     *   apply(ranking, a)[apply(ranking, j)] == a[j];
     * </pre></code>
     * This method does not check whether the input ranking is valid.
     * @param i a non negative number
     * @return the moved index
     * @throws java.lang.IllegalArgumentException if {@code i} is negative
     */
    static int apply(int[] ranking, int i) {
        if (i < 0)
            negativeFailure();
        if (i >= ranking.length)
            return i;
        return ranking[i];
    }

    /* ================= apply ================= */

    /**
     * Apply the ranking to the input array. An element at {@code i} is moved to {@code ranking[i]}.
     * Indexes that are greater or equal to the length of the ranking are not moved.
     * This method does not validate that the first argument is indeed a ranking.
     * @param ranking a ranking
     * @param input an input array
     * @return the result of applying the ranking to the input
     * @throws java.lang.IllegalArgumentException if the length of {@code input} is less than the length of {@code ranking}
     * @throws java.lang.ArrayIndexOutOfBoundsException can be thrown if the {@code ranking} argument is not a ranking
     */
    static int[] apply(int[] ranking, int[] input) {
        checkLength(ranking.length, input.length);
        int[] result = new int[input.length];
        for (int i = 0; i < ranking.length; i += 1) {
            result[ranking[i]] = input[i];
        }
        if (input.length > ranking.length) {
            arraycopy(input, ranking.length, result, ranking.length, input.length - ranking.length);
        }
        return result;
    }

    /**
     * Returns all possible permutations of given length
     * @param n length of permutations to generate
     * @return all possible permutations of length {@code n}; this will contain {@code n!}
     * different permutations
     */
    static List<int[]> symmetricGroup(int n) {
        List<int[]> generation = List.of(new int[]{0});
        for (int i = 1; i < n; i++) {
            generation = nextGeneration(generation, i);
        }
        return generation;
    }

    private static List<int[]> nextGeneration(List<int[]> generation, int i) {
        List<int[]> result = new ArrayList<>(generation.size() * i);
        for (int pos = 0; pos <= i; pos++) {
            for (int[] a : generation) {
                result.add(insert(a, i, pos));
            }
        }
        return result;
    }

    static int[] insert(int[] a, int i, int pos) {
        int[] dest = new int[a.length + 1];
        arraycopy(a, 0, dest, 0, pos);
        arraycopy(a, pos, dest, pos + 1, a.length - pos);
        dest[pos] = i;
        return dest;
    }
}
