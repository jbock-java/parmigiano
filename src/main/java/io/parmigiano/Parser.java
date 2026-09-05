package io.parmigiano;

final class Parser {

    private static Expr parse(byte[] input) {
        for (int j = 0; j < input.length; j++) {
            byte c = input[j];
            if (c == 40 || c == 41) { // ( || )
                return CycleParser.parseCycle(input);
            } else if (c >= 97 && c < 123) { // lowecase characters
                return eval(Expr.parseSymbol(input, j));                
            } else if (c != ' ') {
                throw new IllegalArgumentException("bad input: " + c);
            }
        }
        throw new IllegalArgumentException("could not parse input");
    }

    private static Expr eval(Expr.Symbolic symbolic) {
        return switch (symbolic) {
            case Expr.Assignment assignment -> throw new UnsupportedOperationException();
            case Expr.Symbol symbol -> symbol;
        };
    }

    static Expr parse(String s) {
        byte[] bytes = s.getBytes();
        return parse(bytes);
    }
}
