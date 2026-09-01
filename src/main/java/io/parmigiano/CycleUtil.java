package io.parmigiano;

import java.util.Arrays;
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
    static Permutation toCycles(int[] ranking) {
        int[] cycles = new int[ranking.length];
        int[] lengths = new int[ranking.length / 2];
        int max = 0;
        boolean[] done = new boolean[ranking.length];
        int[] acc = new int[ranking.length];
        IntUnaryOperator op = n -> ranking[n];
        int cyclesPos = 0;
        int lengthsPos = 0;
        for (int i = 0; i < ranking.length; i += 1) {
            if (done[i]) {
                continue;
            }
            acc[0] = i;
            int len = chaseCycle(acc, op);
            if (len == 1) {
                continue;
            }
            for (int j = 0; j < len; j++) {
                max = Math.max(max, acc[j]);
                done[acc[j]] = true;
            }
            System.arraycopy(acc, 0, cycles, cyclesPos, len);
            cyclesPos += len;
            lengths[lengthsPos++] = len;
        }
        if (lengths.length == 0 || lengths[0] == 0) {
            return Permutation.IDENTITY;
        }
        return new Permutation(lengths, cycles, max);
    }

    static int maxOff(int[] a, int off, int len) {
        int result = 0;
        for (int i = 0; i < len; i++) {
            if (a[off + i] > a[off + result]) {
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
