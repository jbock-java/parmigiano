package io.parmigiano;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.IntUnaryOperator;

import static io.parmigiano.Rankings.checkRanking;

final class CycleUtil {

    private CycleUtil() {
    }

    /**
     * Find all nontrivial cycles in the input ranking.
     *
     * @param ranking a ranking
     * @return an array of all nontrivial orbits in the input ranking
     */
    static int[][] toOrbits(int[] ranking) {
        checkRanking(ranking);
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

    static int[] rotateToIndex(int[] a, int n) {
        if (n == 0) {
            return a;
        }
        int[] result = new int[a.length];
        for (int i = 0; i < a.length; i++) {
            result[(a.length + (i - n)) % a.length] = a[i];
        }
        return result;
    }

    static int maxIndex(int[] a) {
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
}
