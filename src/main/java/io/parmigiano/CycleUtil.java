package io.parmigiano;

import java.util.ArrayList;
import java.util.Arrays;
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
        int[] acc = new int[ranking.length];
        IntUnaryOperator op = n -> ranking[n];
        for (int i = 0; i < ranking.length; i += 1) {
            if (done[i]) {
                continue;
            }
            acc[0] = i;
            int len = chaseCycle(acc, op);
            if (len == 1) {
                continue;
            }
            int[] newc = Arrays.copyOf(acc, len);
            for (int j = 0; j < len; j++) {
                done[newc[j]] = true;
            }
            result.add(newc);
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

    static int chaseCycle(int[] acc, IntUnaryOperator op) {
        int pos = 0;
        while (true) {
            int j = op.applyAsInt(acc[pos++]);
            if (j == acc[0]) {
                return pos;
            }
            acc[pos] = j;
        }
    }
}
