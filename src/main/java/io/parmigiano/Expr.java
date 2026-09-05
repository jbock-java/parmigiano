package io.parmigiano;

sealed interface Expr permits Permutation, Expr.Symbol {

    sealed interface Symbolic permits Symbol, Assignment {
    }

    record Symbol(String name) implements Expr, Symbolic {
        public static Symbol of(String name) {
            return new Symbol(name);
        }
    }

    record Assignment() implements Symbolic {
    }

    static Symbolic parseSymbol(byte[] input, int off) {
        int len = 0;
        boolean end = false;
        for (int j = off; j < input.length; j++) {
            byte c = input[j];
            if (c == 61) { // =
                byte[] smb = new byte[len];
                System.arraycopy(input, off, smb, 0, len);
                return parseAssignment(new Symbol(new String(smb)), input, j);
            }
            if (c == 32) { // space
                end = true;
                continue;
            }
            if (c < 97 || c >= 123) { // lowercase characters
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
        return new Assignment();
    }
}
