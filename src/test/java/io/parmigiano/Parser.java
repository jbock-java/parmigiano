package io.parmigiano;

public final class Parser {

    public static Permutation parse(String s) {
        Permutation[] result = new Permutation[1];
        char[] input = s.toCharArray();
        char[] mod = new char[input.length + 2];
        mod[0] = '(';
        mod[mod.length - 1] = ')';
        System.arraycopy(input, 0, mod, 1, input.length);
        LispParser.parse(mod, ex -> result[0] = ex.toPermutation());
        return result[0];
    }
}
