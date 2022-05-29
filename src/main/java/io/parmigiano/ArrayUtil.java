package io.parmigiano;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

/**
 * A collection of array related utilities
 */
public final class ArrayUtil {
    /** An empty array of ints */
    public static final int[] INT_0 = new int[]{};

    private ArrayUtil() {
    }

    /**
     * Creates an array of [element, index] pairs from given array.
     * @param a an array of length {@code n}
     * @return a two dimensional array of length {@code n} which contains the  [element, index] pairs in {@code a}
     */
    public static int[][] withIndex(int[] a) {
        int[][] result = new int[a.length][];
        for (int i = 0; i < a.length; i += 1)
            result[i] = new int[]{a[i], i};
        return result;
    }

    /**
     * Creates an array of the numbers {@code 0} (included) to {@code end} (excluded) in sequence.
     * If {@code end == 0} an empty array is returned. If {@code end} is negative, the range
     * will be descending.
     * @param end a number
     * @return an array of length {@code | end | }
     */
    public static int[] range(int end) {
        return range(0, end);
    }

    /**
     * Creates an array of the numbers {@code 0} (included) to {@code end} (excluded) in sequence.
     * If {@code start == end}, an empty array is returned. If {@code end} is negative, the range
     * will be descending.
     * @param end a non-negative number
     * @return an array of length {@code | start - end | }
     * @throws java.lang.IllegalArgumentException if {@code end} is negative
     */
    public static int[] range(int start, int end) {
        if (start == end)
            return INT_0;
        int[] result = new int[Math.abs(start - end)];
        if (start < end)
            for (int i = 0; i < result.length; i++)
                result[i] = start++;
        else
            for (int i = 0; i < result.length; i++)
                result[i] = start--;
        return result;
    }

    /**
     * Find element in array by comparing each element in sequence, starting at index {@code 0}.
     * @param a an array
     * @param el a number
     * @param skip number of matches to skip; if {@code skip = 0} the index of the first match, if any, will be returned
     * @return the least non-negative number {@code i} so that {@code a[i] = el}, or {@code -1} if {@code el} is not
     * found in {@code a}, or if all occurences are skipped
     * @throws java.lang.IllegalArgumentException if {@code skip < 0}
     */
    public static int indexOf(int[] a, int el, final int skip) {
        if (skip < 0)
            negativeFailure();
        int cnt = 0;
        for (int i = 0; i < a.length; i += 1)
            if (a[i] == el)
                if (cnt++ >= skip)
                    return i;
        return -1;
    }

    static int indexOf(int[] a, int el) {
        return indexOf(a, el, 0);
    }

    /**
     * Add a fixed number to each element of given array.
     * @param a an array of numbers
     * @param k a number
     * @return the array {@code b} defined as {@code b[i] = a[i] + k}
     */
    public static int[] add(int[] a, int k) {
        int[] result = new int[a.length];
        for (int i = 0; i < a.length; i += 1)
            result[i] = a[i] + k;
        return result;
    }

    /**
     * Shuffle the input array in place, using a random permutation.
     * This method will modify the input array.
     * @param a an array
     */
    public static void shuffle(int[] a) {
        Random r = new Random();
        for (int i = a.length - 1; i > 0; i--) {
            int j = r.nextInt(i + 1);
            if (j != i) {
                int tmp = a[j];
                a[j] = a[i];
                a[i] = tmp;
            }
        }
    }
    
    /**
     * Returns a sorted copy of the input.
     * @param input an array
     * @return a sorted copy of the input
     */
    public static int[] sortedCopy(int[] input) {
        int[] sorted = Arrays.copyOf(input, input.length);
        Arrays.sort(sorted);
        return sorted;
    }

    static void lengthFailure() {
        throw new IllegalArgumentException("length mismatch");
    }

    static void checkLength(int rankingLength, int inputLength) {
        if (inputLength < rankingLength)
            throw new IllegalArgumentException("not enough input: minimum input length is " + rankingLength
                    + ", but input length is " + inputLength);
    }

    static void negativeFailure() {
        throw new IllegalArgumentException("negative number not allowed");
    }
    
    /**
     * Test if input is sorted
     * @param input an array
     * @return true if the {@code input} is sorted
     */
    public static boolean isSorted(int[] input) {
        if (input.length < 2) {
            return true;
        }
        int test = input[0];
        for (int i : input) {
            if (i < test) {
                return false;
            }
            test = i;
        }
        return true;
    }
    
    static void checkEqualLength(List<?> a, List<?> b) {
        if (a.size() != b.size())
            lengthFailure();
    }

    static void checkEqualLength(int[] a, int[] b) {
        if (a.length != b.length)
            lengthFailure();
    }

    static void checkEqualLength(byte[] a, byte[] b) {
        if (a.length != b.length)
            lengthFailure();
    }

    static void checkEqualLength(short[] a, short[] b) {
        if (a.length != b.length)
            lengthFailure();
    }

    static void checkEqualLength(float[] a, float[] b) {
        if (a.length != b.length)
            lengthFailure();
    }

    static void checkEqualLength(double[] a, double[] b) {
        if (a.length != b.length)
            lengthFailure();
    }

    static void checkEqualLength(long[] a, long[] b) {
        if (a.length != b.length)
            lengthFailure();
    }

    static void checkEqualLength(char[] a, char[] b) {
        if (a.length != b.length)
            lengthFailure();
    }

    static void checkEqualLength(Object[] a, Object[] b) {
        if (a.length != b.length)
            lengthFailure();
    }

    /* ================= isUnique ================= */

    /**
     * Test if the input contains duplicates. This method always returns the correct result,
     * whether or not the input is sorted.
     * @param a an array
     * @return true if the input contains no duplicate element
     */
    public static boolean isUnique(int[] a) {
        return isUnique(a, false);
    }

    /**
     * Test if the input contains duplicates.
     * @param a an array
     * @param omitCheck omit sorted check. Set this to true if it is known that {@code a} is sorted,
     *                  to improve performance.
     *                  If set to true, but the input is not sorted, this method will not return
     *                  the correct result.
     * @return true if the input contains no duplicate element
     */
    public static boolean isUnique(int[] a, boolean omitCheck) {
        if (a.length < 2)
            return true;
        if (!omitCheck && !isSorted(a))
            a = sortedCopy(a);
        for (int i = 1; i < a.length; i++)
            if (a[i] == a[i - 1])
                return false;
        return true;
    }

    /**
     * Remove an element at index {@code i}.
     * @param a an array
     * @param i cut point, must be non negative and less than {@code a.length}
     * @return an array of length {@code a.length - 1}
     */
    public static int[] cut(int[] a, int i) {
        if (i < 0 || i >= a.length)
            throw new IllegalArgumentException("i must be non negative and less than " + a.length);
        int[] result = new int[a.length - 1];
        System.arraycopy(a, 0, result, 0, i);
        System.arraycopy(a, i + 1, result, i, a.length - i - 1);
        return result;
    }

    /**
     * Insert an element at index {@code i}.
     * @param a an array
     * @param i insertion point, must be non negative and not greater than {@code a.length}
     * @param el new element to be inserted
     * @return an array of length {@code a.length + 1}, this will have {@code el} at position {@code i}
     */
    public static int[] paste(int[] a, int i, int el) {
        if (i < 0 || i > a.length)
            throw new IllegalArgumentException("i must be non negative and not greater than " + a.length);
        int[] result = new int[a.length + 1];
        System.arraycopy(a, 0, result, 0, i);
        result[i] = el;
        System.arraycopy(a, i, result, i + 1, a.length - i);
        return result;
    }
}
