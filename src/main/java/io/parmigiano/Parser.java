package io.parmigiano;

import io.parmigiano.LispParser.LispExpr;
import io.parmigiano.LispParser.ListExpr;

public final class Parser {

    public static LispExpr parseExpr(String s) {
        char[] input = s.toCharArray();
        return LispParser.parse(input);
    }

    public static Permutation parse(String s) {
        LispExpr ex = parseExpr(s);
        if (ex instanceof ListExpr expr) {
            if (expr.length() == 1 && expr.get(0) instanceof ListExpr listExpr) {
                expr = listExpr;
            }
            LispExpr er = new Eval().evalListExpression(expr);// todo inefficient
            return er.toPermutation();
        } else {
            throw new IllegalArgumentException("not a list: " + ex);
        }
    }
}
