package io.parmigiano;

import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

/**
 * A collection of array related utilities
 */
final class ArrayUtil {
    /** An empty array of ints */
    private static final int[] INT_0 = new int[]{};

    private ArrayUtil() {
    }

    /**
     * Creates an array of [element, index] pairs from given array.
     * @param a an array of length {@code n}
     * @return a two dimensional array of length {@code n} which contains the  [element, index] pairs in {@code a}
     */
    static int[][] withIndex(int[] a) {
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
    static int[] range(int end) {
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
    static int[] range(int start, int end) {
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
     * @return the least non-negative number {@code i} so that {@code a[i] = el}, or {@code -1} if {@code el} is not
     * found in {@code a}, or if all occurences are skipped
     */
    static int indexOf(int[] a, int el) {
        for (int i = 0; i < a.length; i += 1)
            if (a[i] == el)
                return i;
        return -1;
    }

    /**
     * Add a fixed number to each element of given array.
     * @param a an array of numbers
     * @return the array {@code b} defined as {@code b[i] = a[i] + k}
     */
    static int[] decrement(int[] a) {
        int[] result = new int[a.length];
        for (int i = 0; i < a.length; i += 1) {
            result[i] = a[i] - 1;
        }
        return result;
    }

    /**
     * Shuffle the input array in place, using a random permutation.
     * This method will modify the input array.
     * @param a an array
     */
    static void shuffle(int[] a) {
        Random r = ThreadLocalRandom.current();
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
    static int[] sortedCopy(int[] input) {
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

    static void checkEqualLength(List<?> a, List<?> b) {
        if (a.size() != b.size())
            lengthFailure();
    }

    static void checkEqualLength(int[] a, int[] b) {
        if (a.length != b.length)
            lengthFailure();
    }
}
