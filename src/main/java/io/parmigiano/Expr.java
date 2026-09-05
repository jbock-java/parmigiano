package io.parmigiano;

public sealed interface Expr permits Permutation, Expr.Symbol, Expr.Assignment {

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
                return parseAssignment(Symbol.of(input, off, len), input, j + 1);
            }
            if (c == ' ') {
                end = true;
                continue;
            }
            if ((c < 'a' || c > 'z') && (c < 'A' || c > 'Z')) {
                throw new IllegalArgumentException("bad input: " + c);
            }
            if (end) { // after symbol reading ended, only ' ' and '=' allowed
                throw new IllegalArgumentException("bad input: " + c);
            }
            len++;
        }
        return Symbol.of(input, off, len);
    }

    static Assignment parseAssignment(Symbol symbol, char[] input, int off) {
        return new Assignment(symbol, Parser.parse(input, off));
    }
}
