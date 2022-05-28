package io.parmigiano;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Stack;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import static io.parmigiano.ArrayUtil.INT_0;
import static io.parmigiano.ArrayUtil.checkEqualLength;
import static io.parmigiano.ArrayUtil.checkLength;
import static io.parmigiano.ArrayUtil.lengthFailure;
import static io.parmigiano.ArrayUtil.negativeFailure;
import static io.parmigiano.ArrayUtil.range;
import static io.parmigiano.ArrayUtil.sortedCopy;
import static io.parmigiano.ArrayUtil.withIndex;
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

    private static final int[] IDENTITY_0 = new int[0];

    /**
     * Check that the input ranking is valid. In order to be valid, each non-negative integer less than
     * {@code a.length} must appear exactly once.
     * @param a an array
     * @return true if a is a ranking
     */
    public static boolean isValid(int[] a) {
        boolean[] used = new boolean[a.length];
        for (int i = 0; i < a.length; i++) {
            if (a[i] < 0 || a[i] >= a.length)
                return false;
            if (used[a[i]])
                return false;
            used[a[i]] = true;
        }
        return true;
    }

    /**
     * Return the identity ranking
     * @param length the length of the ranking
     * @return the identity ranking of the given length
     */
    public static int[] identity(int length) {
        if (length == 0)
            return IDENTITY_0;
        return ArrayUtil.range(length);
    }

    /**
     * Find the length that this ranking can be safely trimmed to.
     * @param ranking a ranking
     * @return the length that this ranking can be safely trimmed to
     */
    public static int trimmedLength(int[] ranking) {
        for (int i = ranking.length - 1; i >= 0; i--)
            if (ranking[i] != i)
                return i + 1;
        return 0;
    }

    /**
     * If possible, trim ranking to something shorter but equivalent.
     * @param ranking a ranking
     * @return the trimmed ranking
     */
    public static int[] trim(int[] ranking) {
        int length = trimmedLength(ranking);
        if (length == 0)
            return INT_0;
        if (length < ranking.length)
            return Arrays.copyOf(ranking, length);
        return ranking;
    }

    /**
     * Return a ranking that acts similar to the input but operates on higher indexes,
     * leaving the lower {@code n} indexes unmoved.
     * @param n a non-negative number
     * @param ranking a ranking
     * @return the shifted ranking
     * @throws java.lang.IllegalArgumentException if {@code n} is negative
     */
    public static int[] shift(int n, int[] ranking) {
        if (n < 0)
            negativeFailure();
        if (n == 0)
            return ranking;
        int[] shifted = new int[n + ranking.length];
        for (int i = 1; i < n; i++)
            shifted[i] = i;
        for (int i = 0; i < ranking.length; i++)
            shifted[i + n] = ranking[i] + n;
        return shifted;
    }

    /**
     * Ensure that the input is a ranking.
     * @param a an array
     * @return the input ranking
     * @throws java.lang.IllegalArgumentException if {@code a} is not a valid ranking
     * @see #isValid
     */
    public static int[] checkRanking(int[] a) {
        if (!isValid(a)) {
            String msg = "argument is not a ranking";
            if (a.length < 20) {
                msg += ": " + Arrays.toString(a);
            }
            throw new IllegalArgumentException(msg);
        }
        return a;
    }

    /**
     * Calculate the inverse ranking.
     * This method does not check if the input is indeed a ranking and may have unexpected results otherwise.
     * @param ranking a ranking
     * @return the inverse ranking
     */
    public static int[] invert(int[] ranking) {
        int[][] rankingWithIndex = withIndex(ranking);
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
        int[] a = range(length);
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
    public static int nextOffset(final int idx, int offset, final int[] sorted) {
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

    /**
     * Find the next position, after {@code idx + offset}, of {@code sorted[idx]} in a sorted array.
     * For a given element {@code el}, iterating this method over the {@code offset} element, starting with
     * {@code offset = 0}, will enumerate all positions of {@code sorted[idx]} in the sorted array.
     * @param idx the start index
     * @param offset the current offset from the start index
     * @param sorted a sorted array
     * @return the next offset or {@code 0} if there is no next offset
     */
    public static int nextOffset(final int idx, int offset, final byte[] sorted) {
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

    /**
     * Find the next position, after {@code idx + offset}, of {@code sorted[idx]} in a sorted array.
     * For a given element {@code el}, iterating this method over the {@code offset} element, starting with
     * {@code offset = 0}, will enumerate all positions of {@code sorted[idx]} in the sorted array.
     * @param idx the start index
     * @param offset the current offset from the start index
     * @param sorted a sorted array
     * @return the next offset or {@code 0} if there is no next offset
     */
    public static int nextOffset(final int idx, int offset, final short[] sorted) {
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

    /**
     * Find the next position, after {@code idx + offset}, of {@code sorted[idx]} in a sorted array.
     * For a given element {@code el}, iterating this method over the {@code offset} element, starting with
     * {@code offset = 0}, will enumerate all positions of {@code sorted[idx]} in the sorted array.
     * @param idx the start index
     * @param offset the current offset from the start index
     * @param sorted a sorted array
     * @return the next offset or {@code 0} if there is no next offset
     */
    public static int nextOffset(final int idx, int offset, final long[] sorted) {
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

    /**
     * Find the next position, after {@code idx + offset}, of {@code sorted[idx]} in a sorted array.
     * For a given element {@code el}, iterating this method over the {@code offset} element, starting with
     * {@code offset = 0}, will enumerate all positions of {@code sorted[idx]} in the sorted array.
     * @param idx the start index
     * @param offset the current offset from the start index
     * @param sorted a sorted array
     * @return the next offset or {@code 0} if there is no next offset
     */
    public static int nextOffset(final int idx, int offset, final float[] sorted) {
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

    /**
     * Find the next position, after {@code idx + offset}, of {@code sorted[idx]} in a sorted array.
     * For a given element {@code el}, iterating this method over the {@code offset} element, starting with
     * {@code offset = 0}, will enumerate all positions of {@code sorted[idx]} in the sorted array.
     * @param idx the start index
     * @param offset the current offset from the start index
     * @param sorted a sorted array
     * @return the next offset or {@code 0} if there is no next offset
     */
    public static int nextOffset(final int idx, int offset, final double[] sorted) {
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

    /**
     * Find the next position, after {@code idx + offset}, of {@code sorted[idx]} in a sorted array.
     * For a given element {@code el}, iterating this method over the {@code offset} element, starting with
     * {@code offset = 0}, will enumerate all positions of {@code sorted[idx]} in the sorted array.
     * @param idx the start index
     * @param offset the current offset from the start index
     * @param sorted a sorted array
     * @return the next offset or {@code 0} if there is no next offset
     */
    public static int nextOffset(final int idx, int offset, final char[] sorted) {
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

    /**
     * Find the next position, after {@code idx + offset}, of {@code sorted[idx]} in a sorted array.
     * For a given element {@code el}, iterating this method over the {@code offset} element, starting with
     * {@code offset = 0}, will enumerate all positions of {@code sorted[idx]} in the sorted array.
     * @param idx the start index
     * @param offset the current offset from the start index
     * @param sorted a sorted array
     * @return the next offset or {@code 0} if there is no next offset
     */
    public static int nextOffset(final int idx, int offset, final Object[] sorted) {
        if (offset >= 0) {
            int next = idx + ++offset;
            if (next >= sorted.length || !sorted[next].equals(sorted[idx]))
                if (idx == 0 || !sorted[idx - 1].equals(sorted[idx]))
                    return 0;
                else
                    return -1;
        } else {
            int next = idx + --offset;
            if (next < 0 || !sorted[next].equals(sorted[idx]))
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
     * @see #shift
     */
    public static int unshift(int i) {
        return i > 0 ? i - 1 : i;
    }

    static int nextOffsetShifting(final int idx, int shiftedOffset, final int[] sorted) {
        if (shiftedOffset == 0)
            return 1;
        int offset = nextOffset(idx, unshift(shiftedOffset), sorted);
        return offset == 0 ? 0 : shift(offset);
    }

    static int nextOffsetShifting(final int idx, int shiftedOffset, final byte[] sorted) {
        if (shiftedOffset == 0)
            return 1;
        int offset = nextOffset(idx, unshift(shiftedOffset), sorted);
        return offset == 0 ? 0 : shift(offset);
    }

    static int nextOffsetShifting(final int idx, int shiftedOffset, final short[] sorted) {
        if (shiftedOffset == 0)
            return 1;
        int offset = nextOffset(idx, unshift(shiftedOffset), sorted);
        return offset == 0 ? 0 : shift(offset);
    }

    static int nextOffsetShifting(final int idx, int shiftedOffset, final long[] sorted) {
        if (shiftedOffset == 0)
            return 1;
        int offset = nextOffset(idx, unshift(shiftedOffset), sorted);
        return offset == 0 ? 0 : shift(offset);
    }

    static int nextOffsetShifting(final int idx, int shiftedOffset, final char[] sorted) {
        if (shiftedOffset == 0)
            return 1;
        int offset = nextOffset(idx, unshift(shiftedOffset), sorted);
        return offset == 0 ? 0 : shift(offset);
    }

    static int nextOffsetShifting(final int idx, int shiftedOffset, final float[] sorted) {
        if (shiftedOffset == 0)
            return 1;
        int offset = nextOffset(idx, unshift(shiftedOffset), sorted);
        return offset == 0 ? 0 : shift(offset);
    }

    static int nextOffsetShifting(final int idx, int shiftedOffset, final double[] sorted) {
        if (shiftedOffset == 0)
            return 1;
        int offset = nextOffset(idx, unshift(shiftedOffset), sorted);
        return offset == 0 ? 0 : shift(offset);
    }

    static int nextOffsetShifting(final int idx, int shiftedOffset, final Object[] sorted) {
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
        int[] sorted = sortedCopy(a);
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
     * @see #apply(int[], byte[])
     * @see ArrayUtil#indexOf
     */
    public static int[] sorting(byte[] a) {
        byte[] sorted = sortedCopy(a);
        int[] ranking = new int[a.length];
        int[] offsets = new int[a.length];
        for (int i = 0; i < a.length; i++) {
            int idx = binarySearch(sorted, a[i]);
            int offset = nextOffsetShifting(idx, offsets[idx], sorted);
            ranking[i] = idx + unshift(offset);
            offsets[idx] = offset;
        }
        return ranking;
    }


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
     * @see #apply(int[], short[])
     * @see ArrayUtil#indexOf
     */
    public static int[] sorting(short[] a) {
        short[] sorted = sortedCopy(a);
        int[] ranking = new int[a.length];
        int[] offsets = new int[a.length];
        for (int i = 0; i < a.length; i++) {
            int idx = binarySearch(sorted, a[i]);
            int offset = nextOffsetShifting(idx, offsets[idx], sorted);
            ranking[i] = idx + unshift(offset);
            offsets[idx] = offset;
        }
        return ranking;
    }


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
     * @see #apply(int[], long[])
     * @see ArrayUtil#indexOf
     */
    public static int[] sorting(long[] a) {
        long[] sorted = sortedCopy(a);
        int[] ranking = new int[a.length];
        int[] offsets = new int[a.length];
        for (int i = 0; i < a.length; i++) {
            int idx = binarySearch(sorted, a[i]);
            int offset = nextOffsetShifting(idx, offsets[idx], sorted);
            ranking[i] = idx + unshift(offset);
            offsets[idx] = offset;
        }
        return ranking;
    }


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
     * @see #apply(int[], float[])
     * @see ArrayUtil#indexOf
     */
    public static int[] sorting(float[] a) {
        float[] sorted = sortedCopy(a);
        int[] ranking = new int[a.length];
        int[] offsets = new int[a.length];
        for (int i = 0; i < a.length; i++) {
            int idx = binarySearch(sorted, a[i]);
            int offset = nextOffsetShifting(idx, offsets[idx], sorted);
            ranking[i] = idx + unshift(offset);
            offsets[idx] = offset;
        }
        return ranking;
    }

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
     * @see #apply(int[], double[])
     * @see ArrayUtil#indexOf
     */
    public static int[] sorting(double[] a) {
        double[] sorted = sortedCopy(a);
        int[] ranking = new int[a.length];
        int[] offsets = new int[a.length];
        for (int i = 0; i < a.length; i++) {
            int idx = binarySearch(sorted, a[i]);
            int offset = nextOffsetShifting(idx, offsets[idx], sorted);
            ranking[i] = idx + unshift(offset);
            offsets[idx] = offset;
        }
        return ranking;
    }


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
     * @see #apply(int[], char[])
     * @see ArrayUtil#indexOf
     */
    public static int[] sorting(char[] a) {
        char[] sorted = sortedCopy(a);
        int[] ranking = new int[a.length];
        int[] offsets = new int[a.length];
        for (int i = 0; i < a.length; i++) {
            int idx = binarySearch(sorted, a[i]);
            int offset = nextOffsetShifting(idx, offsets[idx], sorted);
            ranking[i] = idx + unshift(offset);
            offsets[idx] = offset;
        }
        return ranking;
    }

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
     * @see ArrayUtil#indexOf
     * @throws java.lang.NullPointerException if {@code a} is {@code null} or contains a {@code null} element
     */
    public static <E extends Comparable> int[] sorting(E[] a) {
        Comparable[] sorted = sortedCopy(a);
        int[] ranking = new int[a.length];
        int[] offsets = new int[a.length];
        for (int i = 0; i < a.length; i++) {
            int idx = binarySearch(sorted, a[i]);
            int offset = nextOffsetShifting(idx, offsets[idx], sorted);
            ranking[i] = idx + unshift(offset);
            offsets[idx] = offset;
        }
        return ranking;
    }


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
     * @param comp a comparator
     * @return a ranking that sorts the input
     * @see #apply(int[], Object[])
     * @see ArrayUtil#indexOf
     * @throws java.lang.NullPointerException if {@code a} is {@code null} or contains a {@code null} element
     */
    public static <E> int[] sorting(Object[] a, Comparator<E> comp) {
        Object[] sorted = sortedCopy(a, comp);
        int[] ranking = new int[a.length];
        int[] offsets = new int[a.length];
        for (int i = 0; i < a.length; i++) {
            @SuppressWarnings("unchecked")
            int idx = binarySearch(sorted, a[i], (Comparator) comp);
            int offset = nextOffsetShifting(idx, offsets[idx], sorted);
            ranking[i] = idx + unshift(offset);
            offsets[idx] = offset;
        }
        return ranking;
    }

    public static <E> int[] sorting(List<E> a, Comparator<E> comp) {
        return sorting(a.toArray(), comp);
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
    public static <E extends Comparable> int[] from(E[] a, E[] b) {
        checkEqualLength(a, b);
        int[] sort = sorting(b);
        int[] unsort = invert(sort);
        Comparable[] sorted = apply(sort, b);
        int[] ranking = new int[a.length];
        int[] offsets = new int[a.length];
        for (int i = 0; i < a.length; i += 1) {
            int idx = binarySearch(sorted, a[i]);
            if (idx < 0)
                return null;
            int offset = nextOffsetShifting(idx, offsets[idx], sorted);
            if (offset == 0)
                return null;
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
     * @throws java.lang.NullPointerException if any argument is {@code null}
     * @throws java.lang.IllegalArgumentException if {@code b} can not be obtained by rearranging {@code a}
     * @see #apply(int[], byte[])
     */
    public static int[] from(byte[] a, byte[] b) {
        checkEqualLength(a, b);
        int[] sort = sorting(b);
        int[] unsort = invert(sort);
        byte[] sorted = apply(sort, b);
        int[] ranking = new int[a.length];
        int[] offsets = new int[a.length];
        for (int i = 0; i < a.length; i += 1) {
            int idx = binarySearch(sorted, a[i]);
            if (idx < 0)
                return null;
            int offset = nextOffsetShifting(idx, offsets[idx], sorted);
            if (offset == 0)
                return null;
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
     * @throws java.lang.NullPointerException if any argument is {@code null}
     * @throws java.lang.IllegalArgumentException if {@code b} can not be obtained by rearranging {@code a}
     * @see #apply(int[], long[])
     */
    public static int[] from(long[] a, long[] b) {
        checkEqualLength(a, b);
        int[] sort = sorting(b);
        int[] unsort = invert(sort);
        long[] sorted = apply(sort, b);
        int[] ranking = new int[a.length];
        int[] offsets = new int[a.length];
        for (int i = 0; i < a.length; i += 1) {
            int idx = binarySearch(sorted, a[i]);
            if (idx < 0)
                return null;
            int offset = nextOffsetShifting(idx, offsets[idx], sorted);
            if (offset == 0)
                return null;
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
     * @throws java.lang.NullPointerException if any argument is {@code null}
     * @throws java.lang.IllegalArgumentException if {@code b} can not be obtained by rearranging {@code a}
     * @see #apply(int[], float[])
     */
    public static int[] from(float[] a, float[] b) {
        checkEqualLength(a, b);
        int[] sort = sorting(b);
        int[] unsort = invert(sort);
        float[] sorted = apply(sort, b);
        int[] ranking = new int[a.length];
        int[] offsets = new int[a.length];
        for (int i = 0; i < a.length; i += 1) {
            int idx = binarySearch(sorted, a[i]);
            if (idx < 0)
                return null;
            int offset = nextOffsetShifting(idx, offsets[idx], sorted);
            if (offset == 0)
                return null;
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
     * @throws java.lang.NullPointerException if any argument is {@code null}
     * @throws java.lang.IllegalArgumentException if {@code b} can not be obtained by rearranging {@code a}
     * @see #apply(int[], double[])
     */
    public static int[] from(double[] a, double[] b) {
        checkEqualLength(a, b);
        int[] sort = sorting(b);
        int[] unsort = invert(sort);
        double[] sorted = apply(sort, b);
        int[] ranking = new int[a.length];
        int[] offsets = new int[a.length];
        for (int i = 0; i < a.length; i += 1) {
            int idx = binarySearch(sorted, a[i]);
            if (idx < 0)
                return null;
            int offset = nextOffsetShifting(idx, offsets[idx], sorted);
            if (offset == 0)
                return null;
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
     * @throws java.lang.NullPointerException if any argument is null, or if {@code a} or {@code b}
     * contain a {@code null} element
     * @throws java.lang.IllegalArgumentException if {@code b} can not be obtained by rearranging {@code a}
     * @see #apply(int[], java.lang.Object[])
     */
    public static <E> int[] from(E[] a, E[] b, Comparator<E> comp) {
        checkEqualLength(a, b);
        int[] sort = sorting(b, comp);
        int[] unsort = invert(sort);
        Object[] sorted = apply(sort, b);
        int[] ranking = new int[a.length];
        int[] offsets = new int[a.length];
        for (int i = 0; i < a.length; i += 1) {
            @SuppressWarnings("unchecked")
            int idx = binarySearch(sorted, a[i], (Comparator) comp);
            if (idx < 0)
                return null;
            int offset = nextOffsetShifting(idx, offsets[idx], sorted);
            if (offset == 0)
                return null;
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
     * This method does not check that the first argument is indeed a ranking.
     * @param ranking a ranking
     * @param input an input array
     * @return the result of applying the ranking to the input
     * @throws java.lang.IllegalArgumentException if the length of {@code input} is less than the length of {@code ranking}
     */
    public static <T> T[] apply(int[] ranking, T[] input) {
        checkLength(ranking.length, input.length);
        Class<?> type = input.getClass().getComponentType();
        @SuppressWarnings("unchecked")
        T[] result = (T[]) Array.newInstance(type, input.length);
        for (int i = 0; i < ranking.length; i += 1)
            result[ranking[i]] = input[i];
        if (input.length > ranking.length)
            arraycopy(input, ranking.length, result, ranking.length, input.length - ranking.length);
        return result;
    }

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
    public static byte[] apply(int[] ranking, byte[] input) {
        checkLength(ranking.length, input.length);
        byte[] result = new byte[input.length];
        for (int i = 0; i < ranking.length; i += 1)
            result[ranking[i]] = input[i];
        if (input.length > ranking.length)
            arraycopy(input, ranking.length, result, ranking.length, input.length - ranking.length);
        return result;
    }


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
    public static short[] apply(int[] ranking, short[] input) {
        checkLength(ranking.length, input.length);
        short[] result = new short[input.length];
        for (int i = 0; i < ranking.length; i += 1)
            result[ranking[i]] = input[i];
        if (input.length > ranking.length)
            arraycopy(input, ranking.length, result, ranking.length, input.length - ranking.length);
        return result;
    }

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
     * Apply the ranking to the input array. An element at {@code i} is moved to {@code ranking[i]}.
     * Indexes that are greater or equal to the length of the ranking are not moved.
     * This method does not validate that the first argument is indeed a ranking.
     * @param ranking a ranking
     * @param input an input array
     * @return the result of applying the ranking to the input
     * @throws java.lang.IllegalArgumentException if the length of {@code input} is less than the length of {@code ranking}
     * @throws java.lang.ArrayIndexOutOfBoundsException can be thrown if the {@code ranking} argument is not a ranking
     */
    public static long[] apply(int[] ranking, long[] input) {
        checkLength(ranking.length, input.length);
        long[] result = new long[input.length];
        for (int i = 0; i < ranking.length; i += 1)
            result[ranking[i]] = input[i];
        if (input.length > ranking.length)
            arraycopy(input, ranking.length, result, ranking.length, input.length - ranking.length);
        return result;
    }


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
    public static float[] apply(int[] ranking, float[] input) {
        checkLength(ranking.length, input.length);
        float[] result = new float[input.length];
        for (int i = 0; i < ranking.length; i += 1)
            result[ranking[i]] = input[i];
        if (input.length > ranking.length)
            arraycopy(input, ranking.length, result, ranking.length, input.length - ranking.length);
        return result;
    }


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
    public static double[] apply(int[] ranking, double[] input) {
        checkLength(ranking.length, input.length);
        double[] result = new double[input.length];
        for (int i = 0; i < ranking.length; i += 1)
            result[ranking[i]] = input[i];
        if (input.length > ranking.length)
            arraycopy(input, ranking.length, result, ranking.length, input.length - ranking.length);
        return result;
    }


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
    public static boolean[] apply(int[] ranking, boolean[] input) {
        checkLength(ranking.length, input.length);
        boolean[] result = new boolean[input.length];
        for (int i = 0; i < ranking.length; i += 1)
            result[ranking[i]] = input[i];
        if (input.length > ranking.length)
            arraycopy(input, ranking.length, result, ranking.length, input.length - ranking.length);
        return result;
    }


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
    public static char[] apply(int[] ranking, char[] input) {
        checkLength(ranking.length, input.length);
        char[] result = new char[input.length];
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
        ArrayList<E> result = new ArrayList<E>(length);
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

    /**
     * Check if the input ranking will sort the input array when applied to it.
     * This method does not check if the first argument is indeed a valid ranking, and will have unexpected results otherwise.
     * @param a an array
     * @param ranking a ranking
     * @return true if the return value of {@code apply(ranking, a)} is a sorted array
     * @see #isValid
     */
    public static boolean sorts(int[] ranking, byte[] a) {
        if (a.length < ranking.length)
            lengthFailure();
        if (a.length < 2)
            return true;
        int idx = apply(ranking, 0);
        byte test = a[0];
        for (int i = 1; i < a.length; i++) {
            int idx2 = apply(ranking, i);
            byte test2 = a[i];
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

    /**
     * Check if the input ranking will sort the input array when applied to it.
     * This method does not check if the first argument is indeed a valid ranking, and will have unexpected results otherwise.
     * @param a an array
     * @param ranking a ranking
     * @return true if the return value of {@code apply(ranking, a)} is a sorted array
     * @see #isValid
     */
    public static boolean sorts(int[] ranking, short[] a) {
        if (a.length < ranking.length)
            lengthFailure();
        if (a.length < 2)
            return true;
        int idx = apply(ranking, 0);
        short test = a[0];
        for (int i = 1; i < a.length; i++) {
            int idx2 = apply(ranking, i);
            short test2 = a[i];
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

    /**
     * Check if the input ranking will sort the input array when applied to it.
     * This method does not check if the first argument is indeed a valid ranking, and will have unexpected results otherwise.
     * @param a an array
     * @param ranking a ranking
     * @return true if the return value of {@code apply(ranking, a)} is a sorted array
     * @see #isValid
     */
    public static boolean sorts(int[] ranking, long[] a) {
        if (a.length < ranking.length)
            lengthFailure();
        if (a.length < 2)
            return true;
        int idx = apply(ranking, 0);
        long test = a[0];
        for (int i = 1; i < a.length; i++) {
            int idx2 = apply(ranking, i);
            long test2 = a[i];
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

    /**
     * Check if the input ranking will sort the input array when applied to it.
     * This method does not check if the first argument is indeed a valid ranking, and will have unexpected results otherwise.
     * @param a an array
     * @param ranking a ranking
     * @return true if the return value of {@code apply(ranking, a)} is a sorted array
     * @see #isValid
     */
    public static boolean sorts(int[] ranking, char[] a) {
        if (a.length < ranking.length)
            lengthFailure();
        if (a.length < 2)
            return true;
        int idx = apply(ranking, 0);
        char test = a[0];
        for (int i = 1; i < a.length; i++) {
            int idx2 = apply(ranking, i);
            char test2 = a[i];
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

    /**
     * Check if the input ranking will sort the input array when applied to it.
     * This method does not check if the first argument is indeed a valid ranking, and will have unexpected results otherwise.
     * @param a an array
     * @param ranking a ranking
     * @return true if the return value of {@code apply(ranking, a)} is a sorted array
     * @see #isValid
     */
    public static boolean sorts(int[] ranking, float[] a) {
        if (a.length < ranking.length)
            lengthFailure();
        if (a.length < 2)
            return true;
        int idx = apply(ranking, 0);
        float test = a[0];
        for (int i = 1; i < a.length; i++) {
            int idx2 = apply(ranking, i);
            float test2 = a[i];
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

    /**
     * Check if the input ranking will sort the input array when applied to it.
     * This method does not check if the first argument is indeed a valid ranking, and will have unexpected results otherwise.
     * @param a an array
     * @param ranking a ranking
     * @return true if the return value of {@code apply(ranking, a)} is a sorted array
     * @see #isValid
     */
    public static boolean sorts(int[] ranking, double[] a) {
        if (a.length < ranking.length)
            lengthFailure();
        if (a.length < 2)
            return true;
        int idx = apply(ranking, 0);
        double test = a[0];
        for (int i = 1; i < a.length; i++) {
            int idx2 = apply(ranking, i);
            double test2 = a[i];
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

    /**
     * Check if the input ranking will sort the input array when applied to it.
     * This method does not check if the first argument is indeed a valid ranking, and will have unexpected results otherwise.
     * @param a an array
     * @param ranking a ranking
     * @return true if the return value of {@code apply(ranking, a)} is a sorted array
     * @see #isValid
     * @throws java.lang.NullPointerException if {@code a} contains a {@code null} element
     */
    public static <E extends Comparable<E>> boolean sorts(int[] ranking, E[] a) {
        if (a.length < ranking.length)
            lengthFailure();
        if (a.length < 2)
            return true;
        int idx = apply(ranking, 0);
        E test = a[0];
        for (int i = 1; i < a.length; i++) {
            int idx2 = apply(ranking, i);
            E test2 = a[i];
            int comparison = test.compareTo(test2);
            if (idx2 > idx) {
                if (comparison > 0)
                    return false;
            } else if (comparison < 0)
                return false;
            idx = idx2;
            test = test2;
        }
        return true;
    }

    /**
     * Check if the input ranking will sort the input array when applied to it.
     * This method does not check if the first argument is indeed a valid ranking, and will have unexpected results otherwise.
     * @param a an array
     * @param ranking a ranking
     * @param comparator a Comparator
     * @return true if the return value of {@code apply(ranking, a)} is a sorted array
     * @see #isValid
     * @throws java.lang.NullPointerException if {@code a} contains a {@code null} element
     */
    public static <E> boolean sorts(int[] ranking, E[] a, Comparator<E> comparator) {
        if (a.length < ranking.length)
            lengthFailure();
        if (a.length < 2)
            return true;
        int idx = apply(ranking, 0);
        E test = a[0];
        for (int i = 1; i < a.length; i++) {
            int idx2 = apply(ranking, i);
            E test2 = a[i];
            int comparison = comparator.compare(test, test2);
            if (idx2 > idx) {
                if (comparison > 0)
                    return false;
            } else if (comparison < 0)
                return false;
            idx = idx2;
            test = test2;
        }
        return true;
    }

    /**
     * Check if the input ranking will sort the input list when applied to it.
     * This method does not check if the first argument is indeed a valid ranking, and will have unexpected results otherwise.
     * @param a a list
     * @param ranking a ranking
     * @return true if the return value of {@code apply(ranking, a)} is a sorted list
     * @see #isValid
     * @throws java.lang.NullPointerException if {@code a} contains a {@code null} element
     */
    public static <E extends Comparable<E>> boolean sorts(int[] ranking, List<E> a) {
        if (a.size() < ranking.length)
            lengthFailure();
        if (a.size() < 2)
            return true;
        int idx = apply(ranking, 0);
        E test = a.get(0);
        for (int i = 1; i < a.size(); i++) {
            int idx2 = apply(ranking, i);
            E test2 = a.get(i);
            int comparison = test.compareTo(test2);
            if (idx2 > idx) {
                if (comparison > 0)
                    return false;
            } else if (comparison < 0)
                return false;
            idx = idx2;
            test = test2;
        }
        return true;
    }

    /**
     * Check if the input ranking will sort the input list when applied to it.
     * This method does not check if the first argument is indeed a valid ranking, and will have unexpected results otherwise.
     * @param a a list
     * @param ranking a ranking
     * @param comparator a Comparator
     * @return true if the return value of {@code apply(ranking, a)} is a sorted list
     * @see #isValid
     * @throws java.lang.NullPointerException if {@code a} contains a {@code null} element
     */
    public static <E> boolean sorts(int[] ranking, List<E> a, Comparator<E> comparator) {
        if (a.size() < ranking.length)
            lengthFailure();
        if (a.size() < 2)
            return true;
        int idx = apply(ranking, 0);
        E test = a.get(0);
        for (int i = 1; i < a.size(); i++) {
            int idx2 = apply(ranking, i);
            E test2 = a.get(i);
            int comparison = comparator.compare(test, test2);
            if (idx2 > idx) {
                if (comparison > 0)
                    return false;
            } else if (comparison < 0)
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
        Iterable<int[]> it = () -> new Iterator<int[]>() {

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
