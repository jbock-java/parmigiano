package io.parmigiano;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Stack;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import static io.parmigiano.ArrayUtil.checkEqualLength;
import static io.parmigiano.ArrayUtil.checkLength;
import static io.parmigiano.ArrayUtil.lengthFailure;
import static io.parmigiano.ArrayUtil.negativeFailure;
import static io.parmigiano.Preconditions.checkState;
import static java.lang.System.arraycopy;
import static java.util.Arrays.binarySearch;

/**
 * A collection of methods that return rankings, or operate on rankings.
 */
public final class Rankings {

    private Rankings() {
    }

    private static final Comparator<int[]> COMPARE_FIRST = Comparator.comparingInt(a -> a[0]);

    /**
     * Check that the input ranking is valid. In order to be valid, each non-negative integer less than
     * {@code a.length} must appear exactly once.
     * @param a an array
     * @return true if a is a ranking
     */
    public static boolean isValid(int[] a) {
        boolean[] used = new boolean[a.length];
        for (int i : a) {
            if (i < 0 || i >= a.length)
                return false;
            if (used[i])
                return false;
            used[i] = true;
        }
        return true;
    }

    /**
     * Ensure that the input is a ranking.
     * @param a an array
     * @throws java.lang.IllegalArgumentException if {@code a} is not a valid ranking
     * @see #isValid
     */
    public static void checkRanking(int[] a) {
        if (!isValid(a)) {
            String msg = "argument is not a ranking";
            if (a.length < 20) {
                msg += ": " + Arrays.toString(a);
            }
            throw new IllegalArgumentException(msg);
        }
    }

    /**
     * Calculate the inverse ranking.
     * This method does not check if the input is indeed a ranking and may have unexpected results otherwise.
     * @param ranking a ranking
     * @return the inverse ranking
     */
    public static int[] invert(int[] ranking) {
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
    public static int[] random(int length) {
        int[] a = ArrayUtil.range(length);
        ArrayUtil.shuffle(a);
        return a;
    }

    /**
     * Multiply two rankings.
     * @param lhs a ranking
     * @param rhs another ranking
     * @return the product of the input rankings
     */
    public static int[] comp(int[] lhs, int[] rhs) {
        if (lhs.length >= rhs.length) {
            if (rhs.length == 0)
                return lhs;
            int[] result = new int[lhs.length];
            for (int i = 0; i < rhs.length; i++)
                result[i] = lhs[rhs[i]];
            if (lhs.length > rhs.length)
                arraycopy(lhs, rhs.length, result, rhs.length, lhs.length - rhs.length);
            return result;
        }
        if (lhs.length == 0)
            return rhs;
        int[] result = new int[rhs.length];
        for (int i = 0; i < rhs.length; i++) {
            int n = rhs[i];
            result[i] = n >= lhs.length ? n : lhs[n];
        }
        return result;
    }

    /* ================= nextOffset ================= */

    /**
     * Find the next position, after {@code idx + offset}, of {@code sorted[idx]} in a sorted array.
     * For a given element {@code el}, iterating this method over the {@code offset} element, starting with
     * {@code offset = 0}, will enumerate all positions of {@code sorted[idx]} in the sorted array.
     * @param idx the start index
     * @param offset the current offset from the start index
     * @param sorted a sorted array
     * @return the next offset or {@code 0} if there is no next offset
     */
    public static int nextOffset(int idx, int offset, final int[] sorted) {
        if (offset >= 0) {
            int next = idx + ++offset;
            if (next >= sorted.length || sorted[next] != sorted[idx])
                if (idx == 0 || sorted[idx - 1] != sorted[idx])
                    return 0;
                else
                    return -1;
        } else {
            int next = idx + --offset;
            if (next < 0 || sorted[next] != sorted[idx])
                return 0;
        }
        return offset;
    }

    public static int nextOffset(int idx, int offset, List<?> sorted) {
        if (offset >= 0) {
            int next = idx + ++offset;
            if (next >= sorted.size() || !sorted.get(next).equals(sorted.get(idx)))
                if (idx == 0 || !sorted.get(idx - 1).equals(sorted.get(idx)))
                    return 0;
                else
                    return -1;
        } else {
            int next = idx + --offset;
            if (next < 0 || !sorted.get(next).equals(sorted.get(idx)))
                return 0;
        }
        return offset;
    }

    /* ================= shift ================= */

    /**
     * Encode an int as a non-zero int
     * @param i an int
     * @return {@code i + 1} if {@code i} is non-negative, otherwise {@code i}
     * @see #unshift
     */
    public static int shift(int i) {
        return i >= 0 ? i + 1 : i;
    }

    /**
     * Undo the shift
     * @param i a non-zero int
     * @return {@code i - 1} if {@code i} is positive, otherwise {@code i}
     */
    public static int unshift(int i) {
        return i > 0 ? i - 1 : i;
    }

    static int nextOffsetShifting(int idx, int shiftedOffset, int[] sorted) {
        if (shiftedOffset == 0)
            return 1;
        int offset = nextOffset(idx, unshift(shiftedOffset), sorted);
        return offset == 0 ? 0 : shift(offset);
    }
    
    static int nextOffsetShifting(int idx, int shiftedOffset, List<?> sorted) {
        if (shiftedOffset == 0)
            return 1;
        int offset = nextOffset(idx, unshift(shiftedOffset), sorted);
        return offset == 0 ? 0 : shift(offset);
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
     * @see ArrayUtil#indexOf
     */
    public static int[] sorting(int[] a) {
        int[] sorted = ArrayUtil.sortedCopy(a);
        int[] ranking = new int[a.length];
        int[] offsets = new int[a.length];
        for (int i = 0; i < a.length; i++) {
            int idx = binarySearch(sorted, a[i]);
            int offset = nextOffsetShifting(idx, offsets[idx], sorted);
            ranking[i] = idx + unshift(offset);
            offsets[idx] = offset;
        }
        checkRanking(ranking);
        return ranking;
    }

    public static <E extends Comparable<E>> int[] sorting(List<E> a) {
        List<E> sorted = a.stream().sorted().toList();
        int[] ranking = new int[a.size()];
        int[] offsets = new int[a.size()];
        for (int i = 0; i < a.size(); i++) {
            int idx = Collections.binarySearch(sorted, a.get(i));
            int offset = nextOffsetShifting(idx, offsets[idx], sorted);
            ranking[i] = idx + unshift(offset);
            offsets[idx] = offset;
        }
        return ranking;
    }

    public static <E> int[] sorting(List<E> a, Comparator<E> comp) {
        List<E> sorted = a.stream().sorted(comp).toList();
        int[] ranking = new int[a.size()];
        int[] offsets = new int[a.size()];
        for (int i = 0; i < a.size(); i++) {
            int idx = Collections.binarySearch(sorted, a.get(i), comp);
            int offset = nextOffsetShifting(idx, offsets[idx], sorted);
            ranking[i] = idx + unshift(offset);
            offsets[idx] = offset;
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
    public static int[] from(int[] a, int[] b) {
        checkEqualLength(a, b);
        int[] sort = sorting(b);
        int[] unsort = invert(sort);
        int[] sorted = apply(sort, b);
        int[] ranking = new int[a.length];
        int[] offsets = new int[a.length];
        for (int i = 0; i < a.length; i += 1) {
            int idx = binarySearch(sorted, a[i]);
            checkState(idx >= 0, "not found in target: %d", a[i]);
            int offset = nextOffsetShifting(idx, offsets[idx], sorted);
            checkState(offset != 0, "b is not a rearrangement of a");
            ranking[i] = unsort[idx + unshift(offset)];
            offsets[idx] = offset;
        }
        return ranking;
    }


    /**
     * Produce a particular ranking that produces {@code b} when applied to {@code a}.
     * @param a an array
     * @param b an array
     * @return a ranking that produces {@code b} when applied to {@code a}
     * @throws java.lang.IllegalArgumentException if {@code b} can not be obtained by rearranging {@code a}
     * @throws java.lang.NullPointerException if any argument is {@code null}
     */
    public static <E extends Comparable<E>> int[] from(List<E> a, List<E> b) {
        checkEqualLength(a, b);
        int[] sort = sorting(b);
        int[] unsort = invert(sort);
        List<E> sorted = apply(sort, b);
        int[] ranking = new int[a.size()];
        int[] offsets = new int[a.size()];
        for (int i = 0; i < a.size(); i += 1) {
            int idx = Collections.binarySearch(sorted, a.get(i));
            checkState(idx >= 0, "not found in target: %s", a.get(i));
            int offset = nextOffsetShifting(idx, offsets[idx], sorted);
            checkState(offset != 0, "b is not a rearrangement of a");
            ranking[i] = unsort[idx + unshift(offset)];
            offsets[idx] = offset;
        }
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
    public static int apply(int[] ranking, int i) {
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
    public static int[] apply(int[] ranking, int[] input) {
        checkLength(ranking.length, input.length);
        int[] result = new int[input.length];
        for (int i = 0; i < ranking.length; i += 1)
            result[ranking[i]] = input[i];
        if (input.length > ranking.length)
            arraycopy(input, ranking.length, result, ranking.length, input.length - ranking.length);
        return result;
    }

    /**
     * Apply the ranking to the input list. An element at {@code i} is moved to {@code ranking[i]}.
     * Indexes that are greater or equal to the length of the ranking are not moved.
     * This method does not check if the first argument is indeed a ranking, and will have unexpected results otherwise.
     * @param ranking a ranking
     * @param input an input array
     * @return the result of applying the ranking to the input
     * @throws java.lang.IllegalArgumentException if the length of {@code input} is less than the length of {@code ranking}
     * @throws java.lang.ArrayIndexOutOfBoundsException can be thrown if the {@code ranking} argument is not a ranking
     */
    public static <E> List<E> apply(int[] ranking, List<E> input) {
        if (ranking.length == 0)
            return input;
        int length = input.size();
        checkLength(ranking.length, length);
        ArrayList<E> result = new ArrayList<>(length);
        for (int i = 0; i < length; i += 1)
            result.add(null);
        for (int i = 0; i < length; i += 1)
            result.set(apply(ranking, i), input.get(i));
        return result;
    }

    /* ================= sorts ================= */

    /**
     * Check if the input ranking will sort the input array when applied to it.
     * This method does not check if the first argument is indeed a valid ranking, and will have unexpected results otherwise.
     * @param a an array
     * @param ranking a ranking
     * @return true if the return value of {@code apply(ranking, a)} is a sorted array
     * @see #isValid
     */
    public static boolean sorts(int[] ranking, int[] a) {
        if (a.length < ranking.length)
            lengthFailure();
        if (a.length < 2)
            return true;
        int idx = apply(ranking, 0);
        int test = a[0];
        for (int i = 1; i < a.length; i++) {
            int idx2 = apply(ranking, i);
            int test2 = a[i];
            if (idx2 > idx) {
                if (test > test2)
                    return false;
            } else if (test < test2)
                return false;
            idx = idx2;
            test = test2;
        }
        return true;
    }

    private static final class State {
        private final int[] prefix;
        private final int[] suffix;

        private State(int[] prefix, int[] suffix) {
            this.prefix = prefix;
            this.suffix = suffix;
        }
    }


    /**
     * Returns all possible permutations of given length
     * @param n length of permutations to generate
     * @return all possible permutations of length {@code n}; this will contain {@code n!}
     * different permutations
     */
    public static Stream<int[]> symmetricGroup(int n) {
        int[] start = new int[n];
        for (int i = 0; i < n; i += 1) {
            start[i] = i + 1;
        }
        Stack<State> stack = new Stack<>();
        stack.push(new State(new int[0], start));
        Iterable<int[]> it = () -> new Iterator<>() {

            @Override
            public boolean hasNext() {
                return !stack.isEmpty();
            }

            @Override
            public int[] next() {
                State state = stack.pop();
                while (state.suffix.length > 0) {
                    for (int i = 0; i < state.suffix.length; i += 1) {
                        int[] newPrefix = new int[state.prefix.length + 1];
                        arraycopy(state.prefix, 0, newPrefix, 0, state.prefix.length);
                        newPrefix[state.prefix.length] = state.suffix[i];
                        int[] newSuffix = new int[state.suffix.length - 1];
                        if (i != 0)
                            arraycopy(state.suffix, 0, newSuffix, 0, i);
                        if (i < state.suffix.length - 1)
                            arraycopy(state.suffix, i + 1, newSuffix, i, state.suffix.length - 1 - i);
                        stack.push(new State(newPrefix, newSuffix));
                    }
                    state = stack.pop();
                }
                return ArrayUtil.add(state.prefix, -1);
            }
        };
        return StreamSupport.stream(it.spliterator(), false);
    }
}
