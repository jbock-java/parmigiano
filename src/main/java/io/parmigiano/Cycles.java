package io.parmigiano;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static io.parmigiano.ArrayUtil.checkLength;

/**
 * <p>An operation that shuffles a list.
 */
public final class Cycles {

    private static final Cycles IDENTITY = new Cycles(Permutation.Orbits.EMPTY, 0);

    private final int maxMovedIndex;
    private final int[][] cycles;

    private Cycles(Permutation.Orbits orbits, int maxMovedIndex) {
        this(orbits.orbits);
    }

    private Cycles(int[][] cycles) {
        this.maxMovedIndex = maxIndex(cycles);
        this.cycles = cycles;
    }

    public static Cycles create(int... cycle) {
        return new Cycles(new int[][]{cycle});
    }

    public static Permutation cycle(int... cycle) {
        return Permutation.define(CycleUtil.cyclic(cycle));
    }

    /**
     * Get the identity permutation.
     *
     * @return the identity permutation
     */
    public static Cycles identity() {
        return IDENTITY;
    }

    /**
     * Define a new operation as a list of cycles.
     *
     * @param orbits a list of cycles
     * @return the operation defined by the input
     */
    static Cycles create(Permutation.Orbits orbits) {
        if (orbits.orbits.length == 0)
            return IDENTITY;
        int maxIndex = 0;
        for (int[] orbit : orbits.orbits)
            for (int i : orbit)
                maxIndex = Math.max(maxIndex, i);
        return new Cycles(orbits, maxIndex + 1);
    }

    /**
     * Apply this operation by modifying the input array.
     *
     * @param array an array
     * @throws IllegalArgumentException if {@code array.length < this.length()}
     */
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
    /**
     * Apply this operation by modifying the input array.
     *
     * @param array an array
     * @throws IllegalArgumentException if {@code array.length < this.length()}
     */
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

    /**
     * Undo the action of this operation by modifying the input array.
     *
     * @param array an array
     * @throws IllegalArgumentException if {@code array.length < this.length()}
     */
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

    /**
     * Apply this operation by modifying the input list.
     * The input list must support {@link List#set(int, Object)}.
     *
     * @param list a list
     * @throws UnsupportedOperationException if the input list is not mutable
     * @throws IllegalArgumentException      if {@code list.size() < this.length()}
     */
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
            List<Integer> newCycle = chaseCycle(i, other);
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
        for (int[] a : ints) {
            for (int i : a) {
                result = Math.max(result, i);
            }
        }
        return result;
    }

    private List<Integer> chaseCycle(int i, Cycles other) {
        int j = apply(other.apply(i));
        if (i == j) {
            return List.of();
        }
        List<Integer> acc = new ArrayList<>();
        acc.add(i);
        acc.add(j);
        return chaseCycle(j, other, acc);
    }

    private List<Integer> chaseCycle(int i, Cycles other, List<Integer> acc) {
        int j = apply(other.apply(i));
        if (acc.contains(j)) {
            return acc;
        }
        acc.add(j);
        return chaseCycle(j, other, acc);
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
