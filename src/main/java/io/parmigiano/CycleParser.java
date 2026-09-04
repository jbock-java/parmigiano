package io.parmigiano;

import java.util.Arrays;

final class CycleParser {

    private static Permutation parse(byte[] input) {
        Permutation result = Permutation.identity();
        int[] acc = new int[input.length / 2];
        int base = 0;
        int pos = 0;
        for (int j = 0; j < input.length; j++) {
            byte c = input[j];
            if (c == '(') {
                Arrays.fill(acc, 0);
                base = pos;
            } else if (c == ')') {
                int[] cycle = new int[pos - base];
                System.arraycopy(acc, base, cycle, 0, pos - base);
                Permutation p = Permutation.cycle(cycle);
                result = result.compose(p);
            } else if (c >= 48 && c < 58) {
                int n = c - 48;
                while (j < input.length && input[j + 1] >= 48 && input[j + 1] < 58) {
                    int digit = input[j + 1] - 48;
                    j++;
                    n *= 10;
                    n += digit;
                }
                acc[pos++] = n;
            } else if (c != ' ') {
                throw new IllegalArgumentException("bad input: " + c);
            }
        }
        return result;
    }

    static Permutation parse(String s) {
        byte[] bytes = s.getBytes();
        return parse(bytes);
    }
}
