package io.parmigiano;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.IntUnaryOperator;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static io.parmigiano.ArrayUtil.checkLength;

/**
 * <p>An operation that shuffles a list.
 */
public final class Cycles {

    private static final Cycles IDENTITY = new Cycles(new int[0][]);

    private final int maxMovedIndex;
    private final int[][] cycles;

    private Cycles(int[][] cycles) {
        this.maxMovedIndex = maxIndex(cycles);
        this.cycles = cycles;
    }

    public static Cycles create(int... cycle) {
        return new Cycles(new int[][]{cycle});
    }

    /**
     * Get the identity permutation.
     *
     * @return the identity permutation
     */
    public static Cycles identity() {
        return IDENTITY;
    }

    static Cycles fromRanking(int... ranking) {
        if (ranking.length == 0) {
            return IDENTITY;
        }
        return new Cycles(CycleUtil.toOrbits(ranking));
    }

    public Cycles invert() {
        int[][] newCycles = new int[cycles.length][];
        for (int i = 0; i < cycles.length; i++) {
            int[] cycle = cycles[i];
            newCycles[i] = reverse(cycles[i]);
        }
        return new Cycles(newCycles);
    }

    private static int[] reverse(int[] validData) {
        int[] result = new int[validData.length];
        for (int i = 0; i < validData.length; i++) {
            result[validData.length - 1 - i] = validData[i];
        }
        return result;
    }

    public static Cycles random(int length) {
        return fromRanking(Rankings.random(length));
    }

    private void clobber(int[] array) {
        checkLength(maxMovedIndex, array.length);
        for (int[] cycle : cycles) {
            for (int j = cycle.length - 2; j >= 0; j--) {
                int temp = array[cycle[j + 1]];
                array[cycle[j + 1]] = array[cycle[j]];
                array[cycle[j]] = temp;
            }
        }
    }

    private void clobber(char[] array) {
        checkLength(maxMovedIndex, array.length);
        for (int[] cycle : cycles) {
            for (int j = cycle.length - 2; j >= 0; j--) {
                char temp = array[cycle[j + 1]];
                array[cycle[j + 1]] = array[cycle[j]];
                array[cycle[j]] = temp;
            }
        }
    }

    private void unclobber(int[] array) {
        checkLength(maxMovedIndex, array.length);
        for (int[] cycle : cycles) {
            for (int j = 0; j < cycle.length - 1; j++) {
                int temp = array[cycle[j + 1]];
                array[cycle[j + 1]] = array[cycle[j]];
                array[cycle[j]] = temp;
            }
        }
    }

    private <E> void clobber(List<E> list) {
        checkLength(maxMovedIndex, list.size());
        for (int[] cycle : cycles) {
            for (int j = cycle.length - 2; j >= 0; j--) {
                E temp = list.get(cycle[j + 1]);
                list.set(cycle[j + 1], list.get(cycle[j]));
                list.set(cycle[j], temp);
            }
        }
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
        clobber(copy);
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
        clobber(copy);
        return copy;
    }

    public String apply(String s) {
        char[] dst = new char[s.length()];
        s.getChars(0, s.length(), dst, 0);
        clobber(dst);
        return new String(dst);
    }

    /**
     * Move an index. This method will not fail if the input is negative, but just return it unchanged.
     *
     * @param n a number
     * @return the moved index
     */
    public int apply(int n) {
        if (n > maxMovedIndex) {
            return n;
        }
        for (int[] cycle : cycles)
            for (int j = cycle.length - 2; j >= 0; j--)
                n = n == cycle[j] ? cycle[j + 1] : n == cycle[j + 1] ? cycle[j] : n;
        return n;
    }

    /**
     * Move an index back. This method will not fail if the input is negative, but just return it unchanged.
     *
     * @param n a number
     * @return the moved index
     */
    public int unApply(int n) {
        for (int[] cycle : cycles)
            for (int j = 0; j < cycle.length - 1; j++)
                n = n == cycle[j] ? cycle[j + 1] : n == cycle[j + 1] ? cycle[j] : n;
        return n;
    }

    /**
     * Uncompile this operation.
     *
     * @return a ranking-based version of this operation
     */
    public Permutation toPermutation() {
        int[] ranking = ArrayUtil.range(maxMovedIndex + 1);
        unclobber(ranking);
        return Permutation.define(ranking);
    }

    /**
     * Composing with another permutation creates a new operation.
     *
     * @param other another permutation
     * @return the composition or product
     */
    public Cycles compose(Cycles other) {
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
        return new Cycles(ints);
    }

    private static int maxIndex(int[][] ints) {
        int result = 0;
        Set<Integer> seen = new HashSet<>();
        for (int[] a : ints) {
            if (a.length == 0) {
                continue;
            }
            for (int i : a) {
                if (i < 0) {
                    throw new IllegalArgumentException("negative index: " + i);
                }
                if (!seen.add(i)) {
                    throw new IllegalArgumentException("duplicate index: " + i);
                }
                result = Math.max(result, i);
            }
        }
        return result;
    }

    static List<Integer> chaseCycle(int i, IntUnaryOperator op) {
        int j = op.applyAsInt(i);
        if (i == j) {
            return List.of();
        }
        List<Integer> acc = new ArrayList<>();
        acc.add(i);
        acc.add(j);
        return chaseCycle(j, op, acc);
    }

    static List<Integer> chaseCycle(int i, IntUnaryOperator op, List<Integer> acc) {
        int j = op.applyAsInt(i);
        if (acc.contains(j)) {
            return acc;
        }
        acc.add(j);
        return chaseCycle(j, op, acc);
    }

    /**
     * Take the product of the input operations, in order.
     *
     * @param permutations an array of permutations
     * @return the composition or product
     */
    public static Permutation product(Cycles... permutations) {
        Permutation result = Permutation.identity();
        for (Cycles permutation : permutations)
            result = result.compose(permutation.toPermutation());
        return result;
    }

    @Override
    public String toString() {
        if (cycles.length == 0)
            return "()";
        List<String> s = Arrays.stream(cycles).map(a ->
                "(" + String.join(" ",
                        Arrays.stream(a).mapToObj(Integer::toString).collect(Collectors.toList()))
                        + ")").collect(Collectors.toList());
        return String.join(" ", s);
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

    public static Stream<Cycles> symmetricGroup(int n) {
        return Rankings.symmetricGroup(n).map(a -> Permutation.define(a, false)).map(Permutation::toCycles);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Cycles cycles = (Cycles) o;
        if (cycles.maxMovedIndex != maxMovedIndex) {
            return false;
        }
        for (int i = 0; i < maxMovedIndex; i++) {
            if (apply(i) != cycles.apply(i)) {
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
}
