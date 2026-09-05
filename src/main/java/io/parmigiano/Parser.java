package io.parmigiano;

final class Parser {

    static Expr parse(char[] input, int off) {
        for (int j = off; j < input.length; j++) {
            char c = input[j];
            if (c == '(' || c == ')') {
                return CycleParser.parseCycle(input, j);
            } else if (c >= 'a' && c <= 'z' || c >= 'A' && c <= 'Z') {
                return Expr.parseSymbol(input, j);
            } else if (c != ' ') {
                throw new IllegalArgumentException("bad input: " + c);
            }
        }
        throw new IllegalArgumentException("could not parse input");
    }

    static Expr parse(char[] input) {
        return parse(input, 0);
    }

    static Expr parse(String s) {
        char[] bytes = s.toCharArray();
        return parse(bytes);
    }
}
