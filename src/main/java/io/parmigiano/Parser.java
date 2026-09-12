package io.parmigiano;

import io.parmigiano.LispParser.LispExpr;
import io.parmigiano.LispParser.ListExpr;
import io.parmigiano.LispParser.Nothing;
import io.parmigiano.LispParser.Number;
import io.parmigiano.LispParser.Symbol;

public final class Parser {

    public static LispExpr parseExpr(String s) {
        char[] input = s.toCharArray();
        return LispParser.parse(input);
    }

    public static Permutation parse(String s) {
        LispExpr expr = parseExpr(s);
        EvalResult er = new Eval().eval(expr); // todo inefficient
        return switch (er) {
            case Symbol symbol -> throw new IllegalArgumentException("not a cycle expression");
            case Number number -> throw new IllegalArgumentException("not a cycle expression");
            case Permutation permutation -> permutation;
            case Nothing nothing -> throw new IllegalArgumentException("nothing");
            case ListExpr listExpr -> throw new IllegalArgumentException("todo, create permutation");
        };
    }
}
