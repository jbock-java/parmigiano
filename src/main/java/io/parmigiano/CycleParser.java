package io.parmigiano;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

final class CycleParser {

    static List<Permutation> parseCycles(char[] input, int off) {
        List<Permutation> result = new ArrayList<>(input.length / 5 + 1);
        int[] acc = new int[(input.length - off) / 2];
        int base = 0;
        int pos = 0;
        for (int j = off; j < input.length; j++) {
            char c = input[j];
            if (c == '(') {
                Arrays.fill(acc, 0);
                base = pos;
            } else if (c == ')') {
                int[] cycle = new int[pos - base];
                System.arraycopy(acc, base, cycle, 0, pos - base);
                result.add(Permutation.cycle(cycle));
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

    static Permutation parseCycle(char[] input, int off) {
        List<Permutation> permutations = parseCycles(input, off);
        return Permutation.product(permutations);
    }

    static Permutation parseCycle(char[] input) {
        return parseCycle(input, 0);
    }

    static Permutation parseCycle(String s) {
        char[] bytes = s.toCharArray();
        return parseCycle(bytes);
    }
}
