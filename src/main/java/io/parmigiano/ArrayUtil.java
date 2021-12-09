package io.parmigiano;

import java.util.Arrays;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.IntStream;
import java.util.stream.LongStream;

import static java.util.Arrays.copyOf;

/**
 * A collection of array related utilities
 */
public final class ArrayUtil {

    /** An empty array of Bytes */
    static final Byte[] BOX_BYTE_0 = new Byte[0];
    /** An empty array of Shorts */
    static final Short[] BOX_SHORT_0 = new Short[0];
    /** An empty array of Characters */
    static final Character[] BOX_CHAR_0 = new Character[0];
    /** An empty array of Longs */
    static final Long[] BOX_LONG_0 = new Long[0];
    /** An empty array of Floats */
    static final Float[] BOX_FLOAT_0 = new Float[0];
    /** An empty array of Doubles */
    static final Double[] BOX_DOUBLE_0 = new Double[0];
    /** An empty array of Integers */
    static final Integer[] BOX_INT_0 = new Integer[0];
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
     * Creates an array of the numbers {@code start} to {@code end} in sequence.
     * If {@code start == end}, an empty array is returned. If {@code end} is negative, the range
     * will be descending.
     * @param start a number
     * @param end a number
     * @param inclusive whether or not {@code end} should be included in the result
     * @return the sequence from {@code start} to {@code end}
     */
    public static int[] range(int start, int end, boolean inclusive) {
        if (!inclusive)
            return range(start, end);
        return range(start, end >= start ? ++end : --end);
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
     * Calculate the maximum of the numbers in the input.
     * @param a an array
     * @return the maximum of all numbers in {@code a}
     */
    public static int max(int[] a) {
        if (a.length == 0)
            throw new IllegalArgumentException("argument must not be empty");
        int maxIndex = a[0];
        for (int index : a)
            maxIndex = Math.max(maxIndex, index);
        return maxIndex;
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
     * Produce {@code length} random numbers between {@code 0} and {@code maxNumber} (inclusive)
     * @param maxNumber upper bound of random numbers
     * @param length result length
     * @return an array of random numbers
     */
    public static int[] randomNumbers(int maxNumber, int length) {
        return randomNumbers(0, maxNumber, length);
    }

    /**
     * Generate {@code length} random numbers between {@code minNumber} and {@code maxNumber} (inclusive)
     * @param minNumber lower bound of random numbers
     * @param maxNumber upper bound of random numbers
     * @param length result length
     * @return an array of random numbers
     */
    public static int[] randomNumbers(int minNumber, int maxNumber, int length) {
        if (minNumber > maxNumber) {
            throw new IllegalArgumentException("minNumber must be less than or equal to maxNumber");
        }
        Random random = ThreadLocalRandom.current();
        if (maxNumber < Integer.MAX_VALUE) {
            IntStream ints = random.ints(length, minNumber, maxNumber + 1);
            return ints.toArray();
        } else {
            LongStream longs = random.longs(length, minNumber, (long) maxNumber + 1l);
            int[] result = new int[length];
            long[] longArray = longs.toArray();
            for (int i = 0; i < longArray.length; i++)
                result[i] = Math.toIntExact(longArray[i]);
            return result;
        }
    }

    /* ================= box ================= */

    /**
     * Box every value in the input. Return an array of boxed values.
     * @param a an array of primitives
     * @return an array of boxed primitives
     */
    public static Integer[] box(int[] a) {
        if (a.length == 0)
            return BOX_INT_0;
        Integer[] result = new Integer[a.length];
        for (int i = 0; i < a.length; i += 1)
            result[i] = a[i];
        return result;
    }

    /**
     * Box every value in the input. Return an array of boxed values.
     * @param a an array of primitives
     * @return an array of boxed primitives
     */
    public static Byte[] box(byte[] a) {
        if (a.length == 0)
            return BOX_BYTE_0;
        Byte[] result = new Byte[a.length];
        for (int i = 0; i < a.length; i += 1)
            result[i] = a[i];
        return result;
    }

    /**
     * Box every value in the input. Return an array of boxed values.
     * @param a an array of primitives
     * @return an array of boxed primitives
     */
    public static Short[] box(short[] a) {
        if (a.length == 0)
            return BOX_SHORT_0;
        Short[] result = new Short[a.length];
        for (int i = 0; i < a.length; i += 1)
            result[i] = a[i];
        return result;
    }

    /**
     * Box every value in the input. Return an array of boxed values.
     * @param a an array of primitives
     * @return an array of boxed primitives
     */
    public static Long[] box(long[] a) {
        if (a.length == 0)
            return BOX_LONG_0;
        Long[] result = new Long[a.length];
        for (int i = 0; i < a.length; i += 1)
            result[i] = a[i];
        return result;
    }

    /**
     * Box every value in the input. Return an array of boxed values.
     * @param a an array of primitives
     * @return an array of boxed primitives
     */
    public static Float[] box(float[] a) {
        if (a.length == 0)
            return BOX_FLOAT_0;
        Float[] result = new Float[a.length];
        for (int i = 0; i < a.length; i += 1)
            result[i] = a[i];
        return result;
    }

    /**
     * Box every value in the input. Return an array of boxed values.
     * @param a an array of primitives
     * @return an array of boxed primitives
     */
    public static Double[] box(double[] a) {
        if (a.length == 0)
            return BOX_DOUBLE_0;
        Double[] result = new Double[a.length];
        for (int i = 0; i < a.length; i += 1)
            result[i] = a[i];
        return result;
    }

    /**
     * Box every value in the input. Return an array of boxed values.
     * @param a an array of primitives
     * @return an array of boxed primitives
     */
    public static Character[] box(char[] a) {
        if (a.length == 0)
            return BOX_CHAR_0;
        Character[] result = new Character[a.length];
        for (int i = 0; i < a.length; i += 1)
            result[i] = a[i];
        return result;
    }

    /**
     * Returns a sorted copy of the input.
     * @param input an array
     * @return a sorted copy of the input
     */
    public static char[] sortedCopy(char[] input) {
        char[] sorted = Arrays.copyOf(input, input.length);
        Arrays.sort(sorted);
        return sorted;
    }

    /**
     * Returns a sorted copy of the input.
     * @param input an array
     * @return a sorted copy of the input
     */
    public static short[] sortedCopy(short[] input) {
        short[] sorted = Arrays.copyOf(input, input.length);
        Arrays.sort(sorted);
        return sorted;
    }

    /**
     * Returns a sorted copy of the input.
     * @param input an array
     * @return a sorted copy of the input
     */
    public static double[] sortedCopy(double[] input) {
        double[] sorted = Arrays.copyOf(input, input.length);
        Arrays.sort(sorted);
        return sorted;
    }

    /**
     * Returns a sorted copy of the input.
     * @param input an array
     * @return a sorted copy of the input
     */
    public static float[] sortedCopy(float[] input) {
        float[] sorted = Arrays.copyOf(input, input.length);
        Arrays.sort(sorted);
        return sorted;
    }

    /**
     * Returns a sorted copy of the input.
     * @param input an array
     * @return a sorted copy of the input
     */
    public static long[] sortedCopy(long[] input) {
        long[] sorted = Arrays.copyOf(input, input.length);
        Arrays.sort(sorted);
        return sorted;
    }

    /**
     * Returns a sorted copy of the input.
     * @param input an array
     * @return a sorted copy of the input
     */
    public static byte[] sortedCopy(byte[] input) {
        byte[] sorted = Arrays.copyOf(input, input.length);
        Arrays.sort(sorted);
        return sorted;
    }

    /**
     * Returns a sorted copy of the input.
     * @param input an array
     * @return a sorted copy of the input
     */
    public static Comparable[] sortedCopy(Comparable[] input) {
        Comparable[] sorted = Arrays.copyOf(input, input.length);
        Arrays.sort(sorted);
        return sorted;
    }

    /**
     * Returns a sorted copy of the input.
     * @param input an array
     * @param comp a comparator
     * @return a sorted copy of the input
     */
    @SuppressWarnings("unchecked")
    public static Object[] sortedCopy(Object[] input, Comparator comp) {
        Object[] sorted = Arrays.copyOf(input, input.length);
        Arrays.sort(sorted, comp);
        return sorted;
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

    static void slotFailure() {
        throw new IllegalArgumentException("could not find a free spot");
    }

    static void negativeFailure() {
        throw new IllegalArgumentException("negative number not allowed");
    }

    static void nullFailure() {
        throw new IllegalArgumentException("null values are not allowed");
    }

    static void duplicateFailure() {
        throw new IllegalArgumentException("repeated values are not allowed");
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

    /**
     * Test if input is sorted
     * @param input an array
     * @return true if the {@code input} is sorted
     */
    public static boolean isSorted(byte[] input) {
        if (input.length < 2) {
            return true;
        }
        byte test = input[0];
        for (byte i : input) {
            if (i < test) {
                return false;
            }
            test = i;
        }
        return true;
    }

    /**
     * Test if input is sorted
     * @param input an array
     * @return true if the {@code input} is sorted
     */
    public static boolean isSorted(short[] input) {
        if (input.length < 2) {
            return true;
        }
        short test = input[0];
        for (short i : input) {
            if (i < test) {
                return false;
            }
            test = i;
        }
        return true;
    }

    /**
     * Test if input is sorted
     * @param input an array
     * @return true if the {@code input} is sorted
     */
    public static boolean isSorted(char[] input) {
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

    /**
     * Test if input is sorted
     * @param input an array
     * @return true if the {@code input} is sorted
     */
    public static boolean isSorted(float[] input) {
        if (input.length < 2) {
            return true;
        }
        float test = input[0];
        for (float i : input) {
            if (i < test) {
                return false;
            }
            test = i;
        }
        return true;
    }

    /**
     * Test if input is sorted
     * @param input an array
     * @return true if the {@code input} is sorted
     */
    public static boolean isSorted(double[] input) {
        if (input.length < 2) {
            return true;
        }
        double test = input[0];
        for (double i : input) {
            if (i < test) {
                return false;
            }
            test = i;
        }
        return true;
    }

    /**
     * Test if input is sorted
     * @param input an array
     * @return true if the {@code input} is sorted
     */
    public static boolean isSorted(long[] input) {
        if (input.length < 2) {
            return true;
        }
        long test = input[0];
        for (long i : input) {
            if (i < test) {
                return false;
            }
            test = i;
        }
        return true;
    }

    /**
     * Test if input is sorted
     * @param input an array
     * @return true if the {@code input} is sorted
     * @throws java.lang.NullPointerException if the input contains null
     */
    @SuppressWarnings("unchecked")
    public static boolean isSorted(Comparable[] input) {
        if (input.length == 0) {
            return true;
        }
        Comparable test = input[0];
        if (test == null) {
            throw new NullPointerException("null is not allowed");
        }
        for (Comparable i : input) {
            if (i.compareTo(test) < 0) {
                return false;
            }
            test = i;
        }
        return true;
    }

    /**
     * Test if input is sorted
     * @param input an iterable
     * @return true if the {@code input} is sorted
     * @throws java.lang.NullPointerException if the input contains null
     */
    public static <E extends Comparable<E>> boolean isSorted(Iterable<E> input) {
        Iterator<E> iterator = input.iterator();
        if (!iterator.hasNext()) {
            return true;
        }
        E test = iterator.next();
        if (test == null) {
            throw new NullPointerException("null is not allowed");
        }
        while (iterator.hasNext()) {
            E next = iterator.next();
            if (next.compareTo(test) < 0) {
                return false;
            }
            test = next;
        }
        return true;
    }

    /**
     * Test if input is sorted
     * @param input an iterable
     * @return true if the {@code input} is sorted
     * @throws java.lang.NullPointerException if the input contains null
     */
    public static <E> boolean isSorted(Comparator<E> comparator, Iterable<E> input) {
        Iterator<E> iterator = input.iterator();
        if (!iterator.hasNext()) {
            return true;
        }
        E test = iterator.next();
        if (test == null)
            throw new NullPointerException("null is not allowed");
        while (iterator.hasNext()) {
            E next = iterator.next();
            if (next == null)
                throw new NullPointerException("null is not allowed");
            if (comparator.compare(next, test) < 0) {
                return false;
            }
            test = next;
        }
        return true;
    }

    /**
     * Test if input is sorted
     * @param input an array
     * @param comparator a comparator
     * @return true if the {@code input} is sorted
     * @throws java.lang.NullPointerException if the input contains null
     */
    @SuppressWarnings("unchecked")
    public static <E> boolean isSorted(Comparator<E> comparator, Object[] input) {
        if (input.length == 0) {
            return true;
        }
        Object test = input[0];
        if (test == null) {
            throw new NullPointerException("null is not allowed");
        }
        for (Object i : input) {
            if (comparator.compare((E) i, (E) test) < 0) {
                return false;
            }
            test = i;
        }
        return true;
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

    /* ================= unique ================= */

    /**
     * Returns a copy of the input array with duplicates removed.
     * This will only produce the expected result if the input is sorted.
     * @param sorted a sorted array
     * @return the unique sorted array
     */
    public static int[] unique(int[] sorted) {
        int[] result = new int[sorted.length];
        int idx = 0;
        int previous = sorted[0];
        for (int i : sorted) {
            if (i != previous) {
                result[idx++] = previous;
                previous = i;
            }
        }
        result[idx++] = previous;
        return idx == sorted.length ? result : copyOf(result, idx);
    }

    /**
     * Returns a copy of the input array with duplicates removed.
     * This will only produce the expected result if the input is sorted.
     * @param sorted a sorted array
     * @return the unique sorted array
     */
    public static byte[] unique(byte[] sorted) {
        byte[] result = new byte[sorted.length];
        int idx = 0;
        byte previous = sorted[0];
        for (byte b : sorted) {
            if (b != previous) {
                result[idx++] = previous;
                previous = b;
            }
        }
        result[idx++] = previous;
        return idx == sorted.length ? result : copyOf(result, idx);
    }

    /**
     * Returns a copy of the input array with duplicates removed.
     * This will only produce the expected result if the input is sorted.
     * @param sorted a sorted array
     * @return the unique sorted array
     */
    public static short[] unique(short[] sorted) {
        short[] result = new short[sorted.length];
        int idx = 0;
        short previous = sorted[0];
        for (short n : sorted) {
            if (n != previous) {
                result[idx++] = previous;
                previous = n;
            }
        }
        result[idx++] = previous;
        return idx == sorted.length ? result : copyOf(result, idx);
    }

    /**
     * Returns a copy of the input array with duplicates removed.
     * This will only produce the expected result if the input is sorted.
     * @param sorted a sorted array
     * @return the unique sorted array
     */
    public static long[] unique(long[] sorted) {
        long[] result = new long[sorted.length];
        int idx = 0;
        long previous = sorted[0];
        for (long n : sorted) {
            if (n != previous) {
                result[idx++] = previous;
                previous = n;
            }
        }
        result[idx++] = previous;
        return idx == sorted.length ? result : copyOf(result, idx);
    }

    /**
     * Returns a copy of the input array with duplicates removed.
     * This will only produce the expected result if the input is sorted.
     * @param sorted a sorted array
     * @return the unique sorted array
     */
    public static float[] unique(float[] sorted) {
        float[] result = new float[sorted.length];
        int idx = 0;
        float previous = sorted[0];
        for (float f : sorted) {
            if (f != previous) {
                result[idx++] = previous;
                previous = f;
            }
        }
        result[idx++] = previous;
        return idx == sorted.length ? result : copyOf(result, idx);
    }

    /**
     * Returns a copy of the input array with duplicates removed.
     * This will only produce the expected result if the input is sorted.
     * @param sorted a sorted array
     * @return the unique sorted array
     */
    public static double[] unique(double[] sorted) {
        double[] result = new double[sorted.length];
        int idx = 0;
        double previous = sorted[0];
        for (double d : sorted) {
            if (d != previous) {
                result[idx++] = previous;
                previous = d;
            }
        }
        result[idx++] = previous;
        return idx == sorted.length ? result : copyOf(result, idx);
    }

    /**
     * Returns a copy of the input array with duplicates removed.
     * This will only produce the expected result if the input is sorted.
     * @param sorted a sorted array
     * @return the unique sorted array
     */
    public static char[] unique(char[] sorted) {
        char[] result = new char[sorted.length];
        int idx = 0;
        char previous = sorted[0];
        for (char c : sorted) {
            if (c != previous) {
                result[idx++] = previous;
                previous = c;
            }
        }
        result[idx++] = previous;
        return idx == sorted.length ? result : copyOf(result, idx);
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
     * Test if the input contains duplicates. This method always returns the correct result,
     * whether or not the input is sorted.
     * @param a an array
     * @return true if the input contains no duplicate element
     */
    public static boolean isUnique(byte[] a) {
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
    public static boolean isUnique(byte[] a, boolean omitCheck) {
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
     * Test if the input contains duplicates. This method always returns the correct result,
     * whether or not the input is sorted.
     * @param a an array
     * @return true if the input contains no duplicate element
     */
    public static boolean isUnique(short[] a) {
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
    public static boolean isUnique(short[] a, boolean omitCheck) {
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
     * Test if the input contains duplicates. This method always returns the correct result,
     * whether or not the input is sorted.
     * @param a an array
     * @return true if the input contains no duplicate element
     */
    public static boolean isUnique(char[] a) {
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
    public static boolean isUnique(char[] a, boolean omitCheck) {
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
     * Test if the input contains duplicates. This method always returns the correct result,
     * whether or not the input is sorted.
     * @param a an array
     * @return true if the input contains no duplicate element
     */
    public static boolean isUnique(long[] a) {
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
    public static boolean isUnique(long[] a, boolean omitCheck) {
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
     * Test if the input contains duplicates. This method always returns the correct result,
     * whether or not the input is sorted.
     * @param a an array
     * @return true if the input contains no duplicate element
     */
    public static boolean isUnique(double[] a) {
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
    public static boolean isUnique(double[] a, boolean omitCheck) {
        if (a.length < 2)
            return true;
        if (!omitCheck && !isSorted(a))
            a = sortedCopy(a);
        double previous = a[0];
        for (int i = 1; i < a.length; i++)
            if (a[i] == a[i - 1])
                return false;
        return true;
    }

    /**
     * Test if the input contains duplicates. This method always returns the correct result,
     * whether or not the input is sorted.
     * @param a an array
     * @return true if the input contains no duplicate element
     */
    public static boolean isUnique(float[] a) {
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
    public static boolean isUnique(float[] a, boolean omitCheck) {
        if (a.length < 2)
            return true;
        if (!omitCheck && !isSorted(a))
            a = sortedCopy(a);
        float previous = a[0];
        for (int i = 1; i < a.length; i++)
            if (a[i] == a[i - 1])
                return false;
        return true;
    }

    /**
     * Test if the input contains duplicates. This method always returns the correct result,
     * whether or not the input is sorted.
     * @param a an array
     * @return true if the input contains no duplicate element
     */
    public static boolean isUnique(Comparable[] a) {
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
    public static boolean isUnique(Comparable[] a, boolean omitCheck) {
        if (a.length < 2)
            return true;
        if (!omitCheck && !isSorted(a))
            a = sortedCopy(a);
        for (int i = 1; i < a.length; i++)
            if (a[i] != a[i - 1] && !a[i].equals(a[i - 1]))
                return false;
        return true;
    }

    /**
     * Test if the input contains duplicates. This method always returns the correct result,
     * whether or not the input is sorted.
     * Null elements are not allowed.
     * @param a an array
     * @param comparator a Comparator
     * @return true if the input contains no duplicate element
     */
    @SuppressWarnings("unchecked")
    public static boolean isUnique(Object[] a, Comparator comparator) {
        return isUnique(a, comparator, false);
    }

    /**
     * Test if the input contains duplicates.
     * @param a an array
     * @param comparator a Comparator
     * @param omitCheck omit sorted check. Set this to true if it is known that {@code a} is sorted,
     *                  to improve performance.
     *                  If set to true, but the input is not sorted, this method will not return
     *                  the correct result.
     * @return true if the input contains no duplicate element
     */
    @SuppressWarnings("unchecked")
    public static boolean isUnique(Object[] a, Comparator comparator, boolean omitCheck) {
        if (a.length < 2)
            return true;
        if (!omitCheck && !isSorted(comparator, a))
            a = sortedCopy(a, comparator);
        for (int i = 1; i < a.length; i++)
            if (a[i] != a[i - 1] && !a[i].equals(a[i - 1]))
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
            throw new IllegalArgumentException("i must be non netative and less than " + a.length);
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
