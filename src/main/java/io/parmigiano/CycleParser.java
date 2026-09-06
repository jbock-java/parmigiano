package io.parmigiano;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

final class CycleParser {

    static Expr.ListExpr parseCycles(char[] input, int off) {
        List<Permutation> result = new ArrayList<>(input.length / 5 + 1);
        int[] acc = new int[(input.length - off) / 2];
        int base = 0;
        int pos = 0;
        for (int j = off; j < input.length; j++) {
            char c = input[j];
            if (c == '(') {
                Arrays.fill(acc, 0);
            } else if (c == ')') {
                int[] cycle = new int[pos - base];
                System.arraycopy(acc, base, cycle, 0, pos - base);
                result.add(Permutation.cycle(cycle));
                base = pos;
            } else if (c >= '0' && c <= '9') {
                int n = c - '0';
                while (j < input.length && input[j + 1] >= '0' && input[j + 1] <= '9') {
                    int digit = input[j + 1] - '0';
                    j++;
                    n *= 10;
                    n += digit;
                }
                acc[pos++] = n;
            } else if (c != ' ' && !(c == '*' && base == pos)) {
                throw new IllegalArgumentException("bad input: " + c);
            }
        }
        return new Expr.ListExpr(result);
    }

    static Permutation parseCycle(char[] input, int off) {
        List<? extends Expr> exprs = parseCycles(input, off).exprs();
        for (Expr expr : exprs) {
            if (!(expr instanceof Permutation)) {
                throw new IllegalArgumentException("not a cycle expression");
            }

        }
        @SuppressWarnings("unchecked")
        List<Permutation> permutations = (List<Permutation>) exprs;
        return Permutation.product(permutations);
    }
}
