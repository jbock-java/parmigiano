package io.parmigiano;

public final class Parser {

    public static Expr parseExpr(char[] input, int off) {
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

    public static Expr parseExpr(String s) {
        char[] input = s.toCharArray();
        return parseExpr(input, 0);
    }

    public static Permutation parse(String s) {
        Expr expr = parseExpr(s);
        if (expr instanceof Permutation p) {
            return p;
        }
        throw new IllegalArgumentException("not a cycle expression");
    }
}
