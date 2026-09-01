package io.parmigiano;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.function.IntUnaryOperator;

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

    public static Permutation cycle(int i1, int i2, int... more) {
        int[] cycle = new int[more.length + 2];
        cycle[0] = i1;
        cycle[1] = i2;
        int max = Math.max(i1, i2);
        for (int j = 0; j < more.length; j++) {
            int n = more[j];
            cycle[j + 2] = n;
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
        return inverseApply(ints);
    }

    public static Permutation random(int length) {
        return fromRanking(Rankings.random(length));
    }

    /**
     * Apply this operation to produce a new array. The input array is not modified.
     *
     * @param a an array of length not less than {@code this.length()}
     * @return the result of applying this permutation to {@code a}
     * @throws IllegalArgumentException if {@code a.length < this.length()}
     */
    public int[] apply(int[] a) {
        ArrayUtil.checkLength(maxMovedIndex, a.length);
        int[] result = Arrays.copyOf(a, a.length);
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
        return result;
    }

    /**
     * Apply the inverse of this permutation to produce a new array.
     *
     * @param a an array of length not less than {@code this.length()}
     * @return the result of applying the inverse permutation to {@code a}
     * @throws IllegalArgumentException if {@code a.length < this.length()}
     */
    public int[] inverseApply(int[] a) {
        ArrayUtil.checkLength(maxMovedIndex, a.length);
        int[] result = Arrays.copyOf(a, a.length);
        int off = 0;
        for (int len : lengths) {
            if (len == 0) {
                break;
            }
            for (int j = 0; j < len; j++) {
                result[cycles[off + j]] = a[cycles[off + (j + 1) % len]];
            }
            off += len;
        }
        return result;
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
    public Permutation compose(int i1, int i2, int... other) {
        if (other.length == 0) {
            return compose(cycle(i1, i2));
        }
        return compose(cycle(i1, i2, other));
    }

    /**
     * Composing with another permutation creates a new operation.
     *
     * @param other another permutation
     * @return the composition or product
     */
    public Permutation compose(Permutation other) {
        if (isIdentity()) {
            return other;
        }
        if (other.isIdentity()) {
            return this;
        }
        int new_len = Math.max(maxMovedIndex, other.maxMovedIndex) + 1;
        int[] cycles = new int[new_len];
        int[] lengths = new int[new_len / 2];
        int max = 0;
        boolean[] done = new boolean[new_len];
        int[] acc = new int[new_len];
        // todo inefficient
        IntUnaryOperator op = n -> apply(other.apply(n));
        int cyclesPos = 0;
        int lengthsPos = 0;
        for (int j = 0; j < new_len; j++) {
            if (done[j]) {
                continue;
            }
            acc[0] = j;
            int len = CycleUtil.chaseCycle(acc, op);
            if (len == 1) {
                continue;
            }
            for (int k = 0; k < len; k++) {
                max = Math.max(max, acc[k]);
                done[acc[k]] = true;
            }
            System.arraycopy(acc, 0, cycles, cyclesPos, len);
            cyclesPos += len;
            lengths[lengthsPos++] = len;
        }
        if (lengths.length == 0 || lengths[0] == 0) {
            return IDENTITY;
        }
        return new Permutation(lengths, cycles, max);
    }

    /**
     * Take the product of the input operations, in order.
     *
     * @param permutations an array of permutations
     * @return the composition or product
     */
    public static Permutation product(Permutation... permutations) {
        Permutation result = identity();
        for (Permutation permutation : permutations) {
            // todo inefficient
            result = result.compose(permutation);
        }
        return result;
    }

    public boolean isIdentity() {
        return maxMovedIndex == 0;
    }

    public Permutation pow(int n) {
        if (n == 0) {
            return identity();
        }
        Permutation result = this;
        int abs_n = Math.abs(n);
        for (int j = 1; j < abs_n; j++) {
            // todo inefficient
            result = result.compose(this);
        }
        if (n < 0) {
          return result.invert();
        } else {
          return result;
        }
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

    public int order() {
        // LCM?
        int j = 1;
        Permutation p = this;
        while (!p.isIdentity()) {
            j += 1;
            // todo inefficient
            p = p.compose(this);
        }
        return j;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || !(o instanceof Permutation)) {
            return false;
        }
        Permutation other = (Permutation) o;
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
        int result = 1;
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
