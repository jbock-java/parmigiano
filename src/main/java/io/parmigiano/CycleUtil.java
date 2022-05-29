package io.parmigiano;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static io.parmigiano.Permutation.chaseCycle;

/**
 * A collection of methods that return cycles or operate on cycles.
 */
public final class CycleUtil {

    private CycleUtil() {
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

    public static int[] rotateToIndex(int[] a, int n) {
        if (n == 0) {
            return a;
        }
        int[] result = new int[a.length];
        for (int i = 0; i < a.length; i++) {
            result[(a.length + (i - n)) % a.length] = a[i];
        }
        return result;
    }

    public static int maxIndex(int[] a) {
        if (a.length == 0) {
            return -1;
        }
        int result = 0;
        for (int i = 0; i < a.length; i++) {
            if (a[i] > a[result]) {
                result = i; 
            }
        }
        return result;
    }
}
