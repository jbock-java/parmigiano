package io.parmigiano;

import io.parmigiano.LispParser.LispExpr;
import io.parmigiano.LispParser.Symbol;

public sealed interface Expr permits Expr.Assignment, LispParser.LispExpr, LispParser.Symbol, LispParser.ListExpr {

    record Assignment(Symbol lhs, LispExpr rhs) implements Expr {
        public static Assignment of(String lhs, LispExpr rhs) {
            return new Assignment(Symbol.of(lhs), rhs);
        }
    }
}
