package io.parmigiano;

sealed interface Expr permits Permutation, Expr.Symbol, Expr.Assignment {

    record Symbol(String name) implements Expr {
        public static Symbol of(String name) {
            return new Symbol(name);
        }
    }

    record Assignment(Symbol lhs, Expr rhs) implements Expr {
        public static Assignment of(String lhs, Expr rhs) {
            return new Assignment(Symbol.of(lhs), rhs);
        }
    }

    static Expr parseSymbol(byte[] input, int off) {
        int len = 0;
        boolean end = false;
        for (int j = off; j < input.length; j++) {
            byte c = input[j];
            if (c == 61) { // =
                byte[] smb = new byte[len];
                System.arraycopy(input, off, smb, 0, len);
                return parseAssignment(new Symbol(new String(smb)), input, j + 1);
            }
            if (c == 32) { // space
                end = true;
                continue;
            }
            if (c < 97 || c >= 123) { // lower case characters
                throw new IllegalArgumentException("bad input: " + c);
            }
            if (end) { // input after spaces
                throw new IllegalArgumentException("bad input: " + c);
            }
            len++;
        }
        byte[] smb = new byte[len];
        System.arraycopy(input, off, smb, 0, len);
        return new Symbol(new String(smb));
    }

    static Assignment parseAssignment(Symbol symbol, byte[] input, int off) {
        byte[] rhs = new byte[input.length - off];
        System.arraycopy(input, off, rhs, 0, input.length - off);
        return new Assignment(symbol, Parser.parse(rhs));
    }
}
