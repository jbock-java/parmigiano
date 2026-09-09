package io.parmigiano;

import io.parmigiano.Expr.Assignment;
import io.parmigiano.LispParser.LispExpr;
import io.parmigiano.LispParser.Symbol;

public final class Parser {

    static Expr parseSymbols(char[] input, int off) {
        int len = 0;
        boolean end = false;
        for (int j = off; j < input.length; j++) {
            char c = input[j];
            if (c == '=') {
                Symbol lhs = Symbol.of(input, off, len);
                LispExpr rhs = LispParser.parse(input, j + 1);
                return new Assignment(lhs, rhs);
            }
            if (c == '*' || c == '(') {
                return LispParser.parse(input, off);
            }
            if (c == ' ') {
                end = true;
            } else if (end) {
                return LispParser.parse(input, off);
            } else {
                len++;
            }
        }
        return LispParser.parse(input, off);
    }

    public static Expr parseExpr(char[] input, int off) {
        for (int j = off; j < input.length; j++) {
            char c = input[j];
            if (c == '(') {
                return LispParser.parse(input, j);
            } else if (c >= 'a' && c <= 'z' || c >= 'A' && c <= 'Z') {
                return parseSymbols(input, j);
            } else if (c != ' ') {
                throw new IllegalArgumentException("bad input: " + c);
            }
        }
        return LispParser.parse(input, off);
    }

    public static Expr parseExpr(String s) {
        char[] input = s.toCharArray();
        return parseExpr(input, 0);
    }

    public static Permutation parse(String s) {
        Expr expr = parseExpr(s);
        if (expr instanceof LispExpr p) {
            EvalResult er = p.eval();
            return switch (er) {
                case Symbol symbol -> throw new IllegalArgumentException("not a cycle expression");
                case Permutation permutation -> permutation;
            };
        }
        throw new IllegalArgumentException("not a cycle expression");
    }
}
