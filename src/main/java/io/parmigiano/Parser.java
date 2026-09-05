package io.parmigiano;

final class Parser {

    static Expr parse(byte[] input) {
        for (int j = 0; j < input.length; j++) {
            byte c = input[j];
            if (c == 40 || c == 41) { // ( || )
                return CycleParser.parseCycle(input);
            } else if (c >= 97 && c < 123) { // lower case characters
                return Expr.parseSymbol(input, j);
            } else if (c != ' ') {
                throw new IllegalArgumentException("bad input: " + c);
            }
        }
        throw new IllegalArgumentException("could not parse input");
    }

    static Expr parse(String s) {
        byte[] bytes = s.getBytes();
        return parse(bytes);
    }
}
