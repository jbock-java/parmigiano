package io.parmigiano;

import io.parmigiano.LispParser.LispExpr;
import io.parmigiano.LispParser.Nothing;
import io.parmigiano.LispParser.Number;
import io.parmigiano.LispParser.Symbol;

public final class Parser {

    public static LispExpr parseExpr(char[] input, int off) {
        for (int j = off; j < input.length; j++) {
            char c = input[j];
            if (c == '(') {
                return LispParser.parse(input, j);
            } else if (c >= 'a' && c <= 'z' || c >= 'A' && c <= 'Z') {
                return LispParser.parse(input, j);
            } else if (c >= '0' && c <= '9') {
                return LispParser.parse(input, j);
            } else if (c != ' ') {
                throw new IllegalArgumentException("bad input: " + c);
            }
        }
        return new Nothing();
    }

    public static LispExpr parseExpr(String s) {
        char[] input = s.toCharArray();
        return parseExpr(input, 0);
    }

    public static Permutation parse(String s) {
        LispExpr expr = parseExpr(s);
        EvalResult er = new Eval().eval(expr); // todo inefficient
        return switch (er) {
            case Symbol symbol -> throw new IllegalArgumentException("not a cycle expression");
            case Number number -> throw new IllegalArgumentException("not a cycle expression");
            case Permutation permutation -> permutation;
            case Nothing nothing -> throw new IllegalArgumentException("nothing");
        };
    }
}
