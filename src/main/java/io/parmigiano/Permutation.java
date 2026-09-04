package io.parmigiano;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import static java.lang.Math.floorMod;

/**
 * <p>An operation that shuffles a list.
 */
public final class Permutation {

    static final Permutation IDENTITY = new Permutation(new int[0], new int[0], 0);

    private final int maxMovedIndex;
    private final int[] lengths;
    private final int[] cycles;

    Permutation(
            int[] lengths,
            int[] cycles,
            int maxMovedIndex) {
        this.maxMovedIndex = maxMovedIndex;
        this.lengths = lengths;
        this.cycles = cycles;
    }

    public static Permutation cycle(int... cycle) {
        int max = 0;
        for (int n : cycle) {
            max = Math.max(max, n);
        }
        return new Permutation(new int[]{cycle.length}, cycle, max);
    }

    public static Permutation cycle(int i1, int i2) {
        int[] cycle = {i1, i2};
        return new Permutation(new int[]{2}, cycle, Math.max(i1, i2));
    }

    /**
     * Get the identity permutation.
     *
     * @return the identity permutation
     */
    public static Permutation identity() {
        return IDENTITY;
    }

    public static Permutation fromRanking(int... ranking) {
        return CycleUtil.toCycles(ranking);
    }

    public Permutation invert() {
        int[] inverse = Arrays.copyOf(cycles, cycles.length);
        int off = 0;
        for (int len : lengths) {
            if (len == 0) {
                break;
            }
            for (int j = 0; j < len; j++) {
                inverse[j + off] = cycles[off + len - 1 - j];
            }
            off += len;
        }
        return new Permutation(lengths, inverse, maxMovedIndex);
    }

    public int[] toRanking() {
        int[] ints = Rankings.identityRanking(maxMovedIndex + 1);
        int[] result = new int[ints.length];
        apply(ints, result, 1);
        return result;
    }

    /**
     * Apply this operation to the input array. A new array is returned. the
     * input array is not modified.
     *
     * @param a an array of length at least {@link #maxMovedIndex() + 1}
     * @return the result of applying this permutation to {@code a}
     * @throws IllegalArgumentException if {@code a.length <= maxMovedIndex}
     */
    public int[] apply(int[] a) {
        ArrayUtil.checkLength(maxMovedIndex, a.length);
        int[] result = new int[a.length];
        apply(a, result, -1);
        return result;
    }

    /**
     * Apply the inverse of this operation to the input array.
     * A new array is returned. the input array is not modified.
     *
     * @param a an array of length at least {@link #maxMovedIndex() + 1}
     * @return the result of applying the inverse permutation to {@code a}
     * @throws IllegalArgumentException if {@code a.length <= maxMovedIndex}
     */
    public int[] inverseApply(int[] a) {
        return powApply(a, 1);
    }

    public void apply(int[] a, int[] out, int sign) {
        System.arraycopy(a, 0, out, 0, a.length);
        int off = 0;
        for (int len : lengths) {
            if (len == 0) {
                break;
            }
            for (int j = 0; j < len; j++) {
                out[cycles[off + j]] = a[cycles[off + floorMod(j + sign, len)]];
            }
            off += len;
        }
    }

    /**
     * Apply this operation to produce a new list. This method does not modify the input.
     *
     * @param list a list of size not less than {@code this.length()}
     * @return the result of applying this permutation to {@code a}
     * @throws IllegalArgumentException if {@code a.size() < this.length()}
     */
    public <E> List<E> apply(List<E> list) {
        ArrayUtil.checkLength(maxMovedIndex, list.size());
        Object[] a = list.toArray(new Object[0]);
        Object[] result = Arrays.copyOf(a, a.length);
        int off = 0;
        for (int len : lengths) {
            if (len == 0) {
                break;
            }
            for (int j = 0; j < len; j++) {
                result[cycles[off + j]] = a[cycles[off + floorMod(j - 1, len)]];
            }
            off += len;
        }
        @SuppressWarnings("unchecked")
        E[] foo = (E[]) result;
        return List.of(foo);
    }

    public String apply(String a) {
        ArrayUtil.checkLength(maxMovedIndex, a.length());
        char[] result = new char[a.length()];
        a.getChars(0, a.length(), result, 0);
        int off = 0;
        for (int len : lengths) {
            if (len == 0) {
                break;
            }
            for (int j = 0; j < len; j++) {
                result[cycles[off + j]] = a.charAt(cycles[off + floorMod(j - 1, len)]);
            }
            off += len;
        }
        return new String(result);
    }

    /**
     * Move an index.
     *
     * @param n a number
     * @return the moved index
     */
    public int apply(int n) {
        if (n > maxMovedIndex) {
            return n;
        }
        int off = 0;
        for (int len : lengths) {
            if (len == 0) {
                break;
            }
            for (int j = 0; j < len; j++) {
                if (n == cycles[off + j]) {
                    return cycles[off + (j + 1) % len];
                }
            }
            off += len;
        }
        return n;
    }

    /**
     * Composing with another permutation creates a new operation.
     *
     * @param other another permutation
     * @return the composition or product
     */
    public Permutation compose(int... other) {
        return compose(cycle(other));
    }

    /**
     * Composing with another permutation creates a new operation.
     *
     * @param other another permutation
     * @return the composition or product
     */
    public Permutation compose(Permutation other) {
        int[] ints = Rankings.identityRanking(Math.max(maxMovedIndex, other.maxMovedIndex) + 1);
        int[] output = new int[ints.length];
        apply(ints, output, 1);
        other.apply(output, ints, 1);
        return CycleUtil.toCycles(ints);
    }

    /**
     * Take the product of the input operations, in order.
     *
     * @param permutations an array of permutations
     * @return the composition or product
     */
    public static Permutation product(Permutation... permutations) {
        int maxMov = 0;
        for (Permutation permutation : permutations) {
            maxMov = Math.max(permutation.maxMovedIndex, maxMov);
        }
        int[] ints = Rankings.identityRanking(maxMov + 1);
        int[] output = new int[ints.length];
        for (Permutation p : permutations) {
            p.apply(ints, output, 1);
            int[] tmp = ints;
            ints = output;
            output = tmp;
        }
        return CycleUtil.toCycles(ints);
    }

    public boolean isIdentity() {
        return maxMovedIndex == 0;
    }

    public Permutation pow(int n) {
        int[] ints = Rankings.identityRanking(maxMovedIndex + 1);
        int[] ranking = powApply(ints, n);
        return CycleUtil.toCycles(ranking);
    }

    public int[] powApply(int[] a, int n) {
        int[] output = new int[a.length];
        apply(a, output, n);
        return output;
    }

    /**
     * Max moved index.
     *
     * @return the largest {@code i} such that {@code apply(i) != i}
     */
    public int maxMovedIndex() {
        return maxMovedIndex;
    }

    /**
     * Get the number of cycles of this operation.
     *
     * @return the number of cycles
     */
    public int numCycles() {
        int num = 0;
        for (int len : lengths) {
            if (len == 0) {
                return num;
            }
            num++;
        }
        return num;
    }

    /**
     * Calculate the <a href="http://en.wikipedia.org/wiki/Parity_of_a_permutation">signature</a> of this permutation.
     *
     * @return {@code 1} if this permutation can be written as an even number of transpositions, {@code -1} otherwise
     */
    public int signature() {
        int evenLengthCycles = 0;
        for (int len : lengths) {
            if (len == 0) {
                break;
            }
            if (len % 2 == 0) {
                evenLengthCycles++;
            }
        }
        return evenLengthCycles % 2 == 0 ? 1 : -1;
    }

    public static Permutation sorting(int[] input) {
        return fromRanking(Rankings.sorting(input));
    }

    public static <E extends Comparable<E>> Permutation sorting(List<E> input) {
        return fromRanking(Rankings.sorting(input));
    }

    public static <E> Permutation sorting(List<E> input, Comparator<E> comparator) {
        return fromRanking(Rankings.sorting(input, comparator));
    }

    public static List<Permutation> symmetricGroup(int n) {
        List<int[]> rankings = Rankings.symmetricGroup(n);
        List<Permutation> result = new ArrayList<>(rankings.size());
        for (int[] ranking : rankings) {
            result.add(Permutation.fromRanking(ranking));
        }
        return result;
    }

    private static int gcd(int a, int b) {
        while (b > 0) {
            int temp = b;
            b = a % b;
            a = temp;
        }
        return a;
    }

    private static int lcm(int a, int b) {
        int gcd = gcd(a, b);
        return a * (b / gcd);
    }

    public int order() {
        if (lengths.length == 0) {
            return 1;
        }
        int result = lengths[0];
        for (int i = 1; i < lengths.length; i++) {
            int len = lengths[i];
            if (len == 0) {
                return result;
            }
            result = lcm(result, len);
        }
        return result;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Permutation other)) {
            return false;
        }
        if (other.maxMovedIndex != maxMovedIndex) {
            return false;
        }
        int[] a = Rankings.identityRanking(maxMovedIndex + 1);
        int[] b = apply(a);
        int[] c = other.apply(a);
        return Arrays.equals(b, c);
    }

    @Override
    public int hashCode() {
        int[] a = Rankings.identityRanking(maxMovedIndex + 1);
        int[] b = apply(a);
        return Arrays.hashCode(b);
    }


    @Override
    public String toString() {
        String[] result = new String[lengths.length];
        int off = 0;
        int j = 0;
        for (; j < lengths.length; j++) {
            int len = lengths[j];
            if (len == 0) {
                break;
            }
            String[] c = new String[len];
            for (int k = 0; k < len; k++) {
                c[k] = Integer.toString(cycles[off + k]);
            }
            result[j] = String.join(" ", c);
            off += len;
        }
        return '(' + String.join(") (", Arrays.copyOf(result, j)) + ')';
    }

    public Permutation normalize() {
        int[] result = new int[cycles.length];
        int off = 0;
        for (int len : lengths) {
            if (len == 0) {
                break;
            }
            int n = CycleUtil.maxOff(cycles, off, len);
            for (int j = 0; j < len; j++) {
                result[off + j] = cycles[off + (j + n) % len];
            }
            off += len;
        }
        return new Permutation(lengths, result, maxMovedIndex);
    }

    public static TakingBuilderInt taking(int[] a) {
        return new TakingBuilderInt(a);
    }

    public static <E extends Comparable<E>> TakingBuilderList<E> taking(List<E> a) {
        return new TakingBuilderList<>(a);
    }

    public record TakingBuilderList<E>(List<E> from) {
        public Permutation to(List<E> to) {
            return fromRanking(Rankings.from(from, to));
        }
    }

    public record TakingBuilderInt(int[] from) {
        public Permutation to(int[] to) {
            return fromRanking(Rankings.from(from, to));
        }
    }
}
