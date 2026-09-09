package io.parmigiano;

import java.io.CharArrayReader;
import java.io.IOException;
import java.io.PushbackReader;
import java.util.ArrayList;
import java.util.List;

import static java.util.stream.Collectors.joining;

public final class LispParser {
    public sealed interface LispExpr extends Expr permits ListExpr, Symbol {
        // todo add a Number class
        boolean isNumeric();

        boolean isList();

        boolean isSymbol();

        int asNumber();

        EvalResult eval();
    }

    public record ListExpr(List<? extends LispExpr> exprs) implements LispExpr, Expr {
        public static ListExpr of(List<? extends LispExpr> exprs) {
            return new ListExpr(exprs);
        }

        @Override
        public EvalResult eval() {
            List<Permutation> result = new ArrayList<>(exprs.size());
            int[] acc = new int[exprs.size()];
            int pos = 0;
            for (LispExpr expr : exprs) {
                if (expr.isNumeric()) {
                    if (!result.isEmpty()) {
                        throw new IllegalArgumentException("mixing numbers with lists");
                    }
                    acc[pos++] = expr.asNumber();
                } else if (expr.isList()) {
                    if (pos != 0) {
                        throw new IllegalArgumentException("mixing numbers with lists");
                    }
                    EvalResult er = expr.eval();
                    switch (er) {
                        case Symbol _ -> throw new IllegalArgumentException("symbol not allowed here");
                        case Permutation permutation -> result.add(permutation);
                    }
                } else {
                    throw new IllegalArgumentException("not a number or list: " + expr);
                }
            }
            if (pos == 0) {
                return Permutation.product(result);
            } else {
                int[] cycle = new int[pos];
                System.arraycopy(acc, 0, cycle, 0, pos);
                return Permutation.cycle(cycle);
            }
        }

        @Override
        public boolean isNumeric() {
            return false;
        }

        @Override
        public boolean isList() {
            return true;
        }

        @Override
        public boolean isSymbol() {
            return false;
        }

        @Override
        public int asNumber() {
            throw new UnsupportedOperationException("not a number");
        }

        @Override
        public String toString() {
            return stringify(this);
        }
    }

    public record Symbol(String name) implements LispExpr, Expr, EvalResult {
        public static Symbol of(String name) {
            return new Symbol(name);
        }

        public static Symbol of(char[] input, int off, int len) {
            char[] smb = new char[len];
            System.arraycopy(input, off, smb, 0, len);
            return new Symbol(new String(smb));
        }

        @Override
        public boolean isNumeric() {
            if (name.isEmpty()) {
                return false;
            }
            char c = name.charAt(0);
            return c >= '0' && c <= '9';
        }

        @Override
        public boolean isList() {
            return false;
        }

        @Override
        public boolean isSymbol() {
            return true;
        }

        @Override
        public int asNumber() {
            return Integer.parseInt(name);
        }

        @Override
        public EvalResult eval() {
            return this;
        }

        @Override
        public String toString() {
            return stringify(this);
        }
    }

    static ListExpr parseList(PushbackReader reader) throws IOException {
        List<LispExpr> result = new ArrayList<>();
        int d;
        while ((d = reader.read()) != -1) {
            char c = (char) d;
            if (c == ')') {
                return ListExpr.of(result);
            } else if (c != ' ') {
                reader.unread(c);
                result.add(parse(reader));
            }
        }
        throw new IllegalArgumentException("unmatched parentheses");
    }

    static LispExpr parse(PushbackReader reader) throws IOException {
        int d;
        while ((d = reader.read()) != -1) {
            char c = (char) d;
            if (c >= '0' && c <= '9' || c >= 'a' && c <= 'z' || c >= 'A' && c <= 'Z') {
                char[] smb = new char[16];
                int len = 1;
                smb[0] = c;
                while ((d = reader.read()) != -1) {
                    c = (char) d;
                    if (c >= '0' && c <= '9' || c >= 'a' && c <= 'z' || c >= 'A' && c <= 'Z') {
                        smb[len++] = c;
                    } else if (c == ' ' || c == '*' || c == '(' || c == ')' || c == '=') {
                        reader.unread(c);
                        return Symbol.of(smb, 0, len);
                    } else {
                        throw new IllegalArgumentException("bad input: " + c);
                    }
                }
                return Symbol.of(smb, 0, len);
            } else if (c == ')') {
                throw new IllegalArgumentException("unmatched parentheses");
            } else if (c == '(') {
                return parseList(reader);
            } else if (c != ' ' && c != '*') {
                throw new IllegalArgumentException("bad input: " + c);
            }
        }
        throw new IllegalArgumentException("bad input?");
    }

    private static void consumeWhitespace(PushbackReader reader) throws IOException {
        int d;
        while ((d = reader.read()) != -1) {
            if (d != ' ' && d != '*') {
                reader.unread(d);
                return;
            }
        }
    }

    public static LispExpr parse(char[] input, int off) {
        if (off != 0) {
            char[] tmp = new char[input.length - off];
            System.arraycopy(input, off, tmp, 0, input.length - off);
            input = tmp;
        }
        List<LispExpr> acc = new ArrayList<>();
        try (PushbackReader reader = new PushbackReader(new CharArrayReader(input))) {
            int d;
            while ((d = reader.read()) != -1) {
                reader.unread(d);
                acc.add(parse(reader));
                consumeWhitespace(reader);
            }
            if (acc.isEmpty()) {
                return ListExpr.of(List.of());
            } else {
                return ListExpr.of(acc);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static LispExpr parse(String s) {
        char[] chars = s.toCharArray();
        return parse(chars, 0);
    }

    public static String stringify(LispExpr expr) {
        return switch (expr) {
            case ListExpr listExpr -> listExpr.exprs.stream()
                    .map(LispParser::stringify)
                    .collect(joining(" ", "(", ")"));
            case Symbol lispSymbol -> lispSymbol.name;
        };
    }
}
