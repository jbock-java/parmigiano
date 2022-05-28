package io.parmigiano;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static io.parmigiano.ArrayUtil.duplicateFailure;
import static io.parmigiano.ArrayUtil.indexOf;
import static io.parmigiano.ArrayUtil.negativeFailure;
import static io.parmigiano.Cycles.chaseCycle;

/**
 * A collection of methods that return cycles or operate on cycles.
 */
public final class CycleUtil {

    private CycleUtil() {
    }

    /**
     * Get the indexes that are moved by the input cycle.
     *
     * @param cycle a cycle in cycle notation
     * @return the indexes that are moved by the cycle
     * @throws java.lang.IllegalArgumentException if the input does not define a cycle
     */
    public static boolean[] movedIndexes(int[] cycle) {
        if (cycle.length == 0)
            return new boolean[0];
        boolean[] moved = new boolean[ArrayUtil.max(cycle) + 1];
        for (int el : cycle) {
            if (el < 0)
                negativeFailure();
            if (moved[el])
                duplicateFailure();
            moved[el] = true;
        }
        return moved;
    }

    /**
     * Create a ranking from a cycle in cycle notation.
     *
     * @param cycle a cycle in cycle notation
     * @return a ranking that represents the cycle
     * @throws java.lang.IllegalArgumentException if the input does not define a cycle
     */
    public static int[] cyclic(int... cycle) {
        boolean[] moved = movedIndexes(cycle);
        int[] ranking = new int[moved.length];
        for (int i = 0; i < moved.length; i += 1)
            ranking[i] = moved[i] ? cycle[(indexOf(cycle, i, 0) + 1) % cycle.length] : i;
        return ranking;
    }

    /**
     * Find all nontrivial cycles in the input ranking.
     * This method does not check if the input is indeed a valid ranking and will have unexpected results otherwise.
     *
     * @param ranking a ranking
     * @return an array of all nontrivial orbits in the input ranking
     */
    public static int[][] toOrbits(int[] ranking) {
        List<int[]> orbits = new ArrayList<>();
        Set<Integer> done = new HashSet<>();
        for (int i = 0; i < ranking.length; i += 1) {
            if (done.contains(i)) {
                continue;
            }
            List<Integer> newCycle = chaseCycle(i, n -> ranking[n]);
            if (newCycle.isEmpty()) {
                continue;
            }
            done.addAll(newCycle);
            orbits.add(newCycle.stream().mapToInt(p -> p).toArray());
        }
        return orbits.toArray(new int[0][]);
    }
}
