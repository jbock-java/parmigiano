package io.parmigiano;

import java.util.ArrayList;
import java.util.List;
import java.util.function.IntUnaryOperator;

final class CycleUtil {

    private CycleUtil() {
    }

    /**
     * Find all nontrivial cycles in the input ranking.
     *
     * @param ranking a ranking
     *
     * @return cycles of length 2 or greater
     */
    static int[][] toCycles(int[] ranking) {
        List<int[]> result = new ArrayList<>(ranking.length / 2);
        boolean[] done = new boolean[ranking.length];
        for (int i = 0; i < ranking.length; i += 1) {
            if (done[i]) {
                continue;
            }
            List<Integer> newCycle = chaseCycle(i, n -> ranking[n]);
            if (newCycle.isEmpty()) {
                continue;
            }
            for (Integer j : newCycle) {
                done[j] = true;
            }
            result.add(newCycle.stream().mapToInt(Integer::intValue).toArray());
        }
        return result.toArray(new int[0][]);
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
