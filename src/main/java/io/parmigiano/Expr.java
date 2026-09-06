package io.parmigiano;

import java.util.List;

public sealed interface Expr permits Permutation, Expr.Symbol, Expr.Assignment, Expr.ListExpr {

    record Symbol(String name) implements Expr {
        public static Symbol of(String name) {
            return new Symbol(name);
        }

        public static Symbol of(char[] input, int off, int len) {
            char[] smb = new char[len];
            System.arraycopy(input, off, smb, 0, len);
            return new Symbol(new String(smb));
        }
    }

    record ListExpr(List<? extends Expr> exprs) implements Expr {
        public static ListExpr of(List<? extends Expr> exprs) {
            return new ListExpr(exprs);
        }
    }

    record Assignment(Symbol lhs, Expr rhs) implements Expr {
        public static Assignment of(String lhs, Expr rhs) {
            return new Assignment(Symbol.of(lhs), rhs);
        }
    }

    static Expr parseSymbol(char[] input, int off) {
        int len = 0;
        boolean end = false;
        for (int j = off; j < input.length; j++) {
            char c = input[j];
            if (c == '=') {
                ListExpr rhs = CycleParser.parseCycles(input, j + 1);
                return new Assignment(Symbol.of(input, off, len), rhs);
            }
            if (c == ' ') {
                end = true;
                continue;
            }
            if (c == '*' || c == '(') {
                return CycleParser.parseCycles(input, 0);
            }
            if ((c < 'a' || c > 'z') && (c < 'A' || c > 'Z')) {
                throw new IllegalArgumentException("bad input: " + c);
            }
            if (end) {
                return CycleParser.parseCycles(input, 0);
            }
            len++;
        }
        return CycleParser.parseCycles(input, 0);
    }
}
