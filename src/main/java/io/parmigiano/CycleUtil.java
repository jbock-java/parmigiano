package io.parmigiano;

import java.util.Arrays;
import java.util.function.IntUnaryOperator;

final class CycleUtil {

    private CycleUtil() {
    }

    static final class CycleResult {
        final int max;
        final int[] lengths;
        final int[] cycles;

        CycleResult(
                int max,
                int[] lengths,
                int[] cycles) {
            this.max = max;
            this.lengths = lengths;
            this.cycles = cycles;
        }

        int[][] toCycles() {
                int[][] result = new int[lengths.length][];
                int cyclesPos = 0;
                int i;
                for (i = 0; i < lengths.length; i++) {
                    int len = lengths[i];
                    if (len == 0) {
                        break;
                    }
                    int[] cycle = new int[len];
                    System.arraycopy(cycles, cyclesPos, cycle, 0, len);
                    result[i] = cycle;
                    cyclesPos += len;
                }
                return Arrays.copyOf(result, i);
            }
        }

    /**
     * Find all nontrivial cycles in the input ranking.
     *
     * @param ranking a ranking
     *
     * @return cycles of length 2 or greater
     */
    static CycleResult toCycles(int[] ranking) {
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
            int[] newc = Arrays.copyOf(acc, len);
            for (int j = 0; j < len; j++) {
                max = Math.max(max, newc[j]);
                done[newc[j]] = true;
            }
            System.arraycopy(acc, 0, cycles, cyclesPos, len);
            cyclesPos += len;
            lengths[lengthsPos++] = len;
        }
        return new CycleResult(max, lengths, cycles);
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
