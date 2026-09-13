package io.parmigiano;

import io.parmigiano.LispParser.LispExpr;
import io.parmigiano.LispParser.ListExpr;

public final class Parser {

    public static Permutation parse(String s) {
        LispExpr ex = LispParser.parse(s.toCharArray());
        if (ex instanceof ListExpr expr) {
            if (expr.length() == 1 && expr.get(0) instanceof ListExpr listExpr) {
                expr = listExpr;
            }
            LispExpr er = new Eval().evalListExpression(expr);//
            return er.toPermutation();
        } else {
            throw new IllegalArgumentException("not a list: " + ex);
        }
    }
}
