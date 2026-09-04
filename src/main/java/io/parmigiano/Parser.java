package io.parmigiano;

import java.util.Arrays;

final class Parser {

    private static Permutation parse(byte[] input) {
        Permutation result = Permutation.identity();
        int[] acc = new int[input.length / 2];
        int pos_0 = 0;
        int pos = 0;
        for (int j = 0; j < input.length; j++) {
            byte c = input[j];
            if (c == '(') {
                Arrays.fill(acc, 0);
                pos_0 = pos;
            } else if (c == ')') {
                int[] cycle = new int[pos - pos_0];
                System.arraycopy(acc, pos_0, cycle, 0, pos - pos_0);
                Permutation p = Permutation.cycle(cycle);
                result = result.compose(p);
            } else if (Character.isDigit(c)) {
                int n = Integer.parseInt(Character.toString(input[j]));
                while (j < input.length && Character.isDigit(input[j + 1])) {
                    int digit = Integer.parseInt(Character.toString(input[j + 1]));
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
