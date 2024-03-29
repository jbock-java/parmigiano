package io.parmigiano;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static io.parmigiano.CycleUtil.chaseCycle;
import static io.parmigiano.Preconditions.checkState;
import static java.util.stream.Collectors.joining;

/**
 * <p>An operation that shuffles a list.
 */
public final class Permutation {

    private static final Permutation IDENTITY = new Permutation(new int[0][]);

    private final int maxMovedIndex;
    private final int[][] cycles;

    private Permutation(int[][] cycles) {
        this(cycles, maxIndex(cycles));
    }

    private Permutation fromCycles(int[][] cycles) {
        if (cycles.length == 0) {
            return IDENTITY;
        }
        return new Permutation(cycles);
    }

    private Permutation(int[][] cycles, int maxMovedIndex) {
        this.maxMovedIndex = maxMovedIndex;
        this.cycles = cycles;
    }

    public static Permutation cycle(int i1, int i2, int... more) {
        int[] cycle = new int[more.length + 2];
        cycle[0] = i1;
        cycle[1] = i2;
        System.arraycopy(more, 0, cycle, 2, more.length);
        return new Permutation(new int[][]{cycle});
    }

    public static Permutation cycle(int i1, int i2) {
        checkState(i1 >= 0, "negative index: %d", i1);
        checkState(i2 >= 0, "negative index: %d", i2);
        checkState(i1 != i2, "duplicate index: %d", i1);
        int[] cycle = {i1, i2};
        return new Permutation(new int[][]{cycle}, Math.max(i1, i2));
    }

    /**
     * Get the identity permutation.
     *
     * @return the identity permutation
     */
    public static Permutation identity() {
        return IDENTITY;
    }

    private static Permutation fromRanking(int... ranking) {
        if (ranking.length == 0) {
            return IDENTITY;
        }
        return new Permutation(CycleUtil.toOrbits(ranking));
    }

    public Permutation invert() {
        int[][] newCycles = new int[cycles.length][];
        for (int i = 0; i < cycles.length; i++) {
            newCycles[i] = reverse(cycles[i]);
        }
        return new Permutation(newCycles, maxMovedIndex);
    }

    private static int[] reverse(int[] cycle) {
        int[] inverse = new int[cycle.length];
        for (int i = 0; i < cycle.length; i++) {
            inverse[i] = cycle[cycle.length - 1 - i];
        }
        return inverse;
    }

    public static Permutation random(int length) {
        return fromRanking(Rankings.random(length));
    }

    /**
     * Apply this operation to produce a new array. This method does not modify the input.
     *
     * @param a an array of length not less than {@code this.length()}
     * @return the result of applying this permutation to {@code a}
     * @throws java.lang.IllegalArgumentException if {@code a.length < this.length()}
     */
    public int[] apply(int[] a) {
        int[] copy = Arrays.copyOf(a, a.length);
        ArrayUtil.checkLength(maxMovedIndex, copy.length);
        for (int[] cycle : cycles) {
            for (int j = cycle.length - 2; j >= 0; j--) {
                int temp = copy[cycle[j + 1]];
                copy[cycle[j + 1]] = copy[cycle[j]];
                copy[cycle[j]] = temp;
            }
        }
        return copy;
    }

    /**
     * Apply this operation to produce a new list. This method does not modify the input.
     *
     * @param a a list of size not less than {@code this.length()}
     * @return the result of applying this permutation to {@code a}
     * @throws java.lang.IllegalArgumentException if {@code a.size() < this.length()}
     */
    public <E> List<E> apply(List<E> a) {
        List<E> copy = new ArrayList<>(a);
        ArrayUtil.checkLength(maxMovedIndex, copy.size());
        for (int[] cycle : cycles) {
            for (int j = cycle.length - 2; j >= 0; j--) {
                E temp = copy.get(cycle[j + 1]);
                copy.set(cycle[j + 1], copy.get(cycle[j]));
                copy.set(cycle[j], temp);
            }
        }
        return copy;
    }

    public String apply(String s) {
        char[] dst = new char[s.length()];
        s.getChars(0, s.length(), dst, 0);
        ArrayUtil.checkLength(maxMovedIndex, dst.length);
        for (int[] cycle : cycles) {
            for (int j = cycle.length - 2; j >= 0; j--) {
                char temp = dst[cycle[j + 1]];
                dst[cycle[j + 1]] = dst[cycle[j]];
                dst[cycle[j]] = temp;
            }
        }
        return new String(dst);
    }

    /**
     * Move an index.
     *
     * @param n a number
     * @return the moved index
     */
    public int apply(int n) {
        Preconditions.checkState(n >= 0, "negative index: %d", n);
        if (n > maxMovedIndex) {
            return n;
        }
        for (int[] cycle : cycles)
            for (int j = cycle.length - 2; j >= 0; j--)
                n = n == cycle[j] ? cycle[j + 1] : n == cycle[j + 1] ? cycle[j] : n;
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
        if (maxMovedIndex == 0)
            return other;
        if (other.maxMovedIndex == 0)
            return this;
        int max = Math.max(maxMovedIndex, other.maxMovedIndex);
        List<int[]> allCycles = new ArrayList<>();
        Set<Integer> done = new HashSet<>();
        for (int i = 0; i < max; i++) {
            if (done.contains(i)) {
                continue;
            }
            List<Integer> newCycle = chaseCycle(i, n -> apply(other.apply(n)));
            if (newCycle.isEmpty()) {
                continue;
            }
            done.addAll(newCycle);
            allCycles.add(newCycle.stream().mapToInt(p -> p).toArray());
        }
        int[][] ints = allCycles.toArray(new int[0][]);
        return fromCycles(ints);
    }

    private static int maxIndex(int[][] ints) {
        int result = 0;
        Set<Integer> seen = new HashSet<>();
        for (int[] a : ints) {
            if (a.length == 0) {
                continue;
            }
            for (int i : a) {
                checkState(i >= 0, "negative index: %d", i);
                checkState(seen.add(i), "duplicate index: %d", i);
                result = Math.max(result, i);
            }
        }
        return result;
    }

    /**
     * Take the product of the input operations, in order.
     *
     * @param permutations an array of permutations
     * @return the composition or product
     */
    public static Permutation product(Permutation... permutations) {
        Permutation result = identity();
        for (Permutation permutation : permutations)
            result = result.compose(permutation);
        return result;
    }

    public boolean isIdentity() {
        return cycles.length == 0;
    }

    public Permutation pow(int n) {
        if (n == 0 || isIdentity()) {
            return identity();
        }
        Permutation seed = n < 0 ? invert() : this;
        Permutation result = seed;
        for (int i = 1; i < Math.abs(n); i += 1) {
            result = result.compose(seed);
        }
        return result;
    }

    /**
     * Max moved index.
     *
     * @return the length of this operation
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
        return cycles.length;
    }

    /**
     * Calculate the <a href="http://en.wikipedia.org/wiki/Parity_of_a_permutation">signature</a> of this permutation.
     *
     * @return {@code 1} if this permutation can be written as an even number of transpositions, {@code -1} otherwise
     */
    public int signature() {
        int evenLengthCycles = 0;
        for (int[] cycle : cycles)
            if (cycle.length % 2 == 0)
                evenLengthCycles++;
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
        int i = 1;
        Permutation p = this;
        while (!p.isIdentity()) {
            i += 1;
            p = p.compose(this);
        }
        return i;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Permutation other = (Permutation) o;
        if (other.maxMovedIndex != maxMovedIndex) {
            return false;
        }
        for (int i = 0; i < maxMovedIndex; i++) {
            if (apply(i) != other.apply(i)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public int hashCode() {
        int result = 1;
        for (int i = 0; i <= maxMovedIndex; i++) {
            int apply = apply(i);
            result = 31 * result + apply;
        }
        return result;
    }


    @Override
    public String toString() {
        if (isIdentity()) {
            return "id";
        }
        return Arrays.stream(cycles)
                .map(Arrays::stream)
                .map(s -> s.mapToObj(Integer::toString))
                .map(s -> s.collect(joining(" ", "(", ")")))
                .collect(joining(" "));
    }

    public String print() {
        if (isIdentity()) {
            return "identity()";
        }
        String receiver = printCycle(cycles[0]);
        if (cycles.length == 1) {
            return receiver;
        }
        return receiver + Arrays.stream(cycles)
                .skip(1)
                .map(Permutation::printCycle)
                .collect(joining(").compose(", ".compose(", ")"));
    }

    private static String printCycle(int[] cycle) {
        return Arrays.stream(cycle)
                .mapToObj(Integer::toString)
                .collect(joining(", ", "create(", ")"));
    }

    public Permutation normalize() {
        int[][] newCycles = new int[cycles.length][];
        for (int i = 0; i < cycles.length; i++) {
            int[] cycle = cycles[i];
            newCycles[i] = CycleUtil.rotateToIndex(cycle, CycleUtil.maxIndex(cycle));
        }
        Arrays.sort(newCycles, Comparator.<int[]>comparingInt(o -> o[0]).reversed());
        return new Permutation(newCycles, maxMovedIndex);
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
