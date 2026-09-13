package io.parmigiano;

public final class Parser {

    public static Permutation parse(String s) {
        Permutation[] result = new Permutation[1];
        LispParser.parse(s.toCharArray(), ex -> result[0] = ex.toPermutation());
        return result[0];
    }
}
