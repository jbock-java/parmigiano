package io.parmigiano;

import io.parmigiano.LispParser.LispExpr;
import io.parmigiano.LispParser.Symbol;

public sealed interface Expr permits Expr.Assignment, LispParser.LispExpr, LispParser.Symbol, LispParser.ListExpr {

    record Assignment(Symbol lhs, LispExpr rhs) implements Expr {
        public static Assignment of(String lhs, LispExpr rhs) {
            return new Assignment(Symbol.of(lhs), rhs);
        }
    }

    static Expr parseSymbol(char[] input, int off) {
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
}
