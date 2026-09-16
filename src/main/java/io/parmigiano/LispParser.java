package io.parmigiano;

import java.io.IOException;
import java.io.PushbackReader;
import java.util.ArrayList;
import java.util.List;

import static java.util.stream.Collectors.joining;

public final class LispParser {

    static final ListExpr NIL = ListExpr.of(List.of());

    public sealed interface LispExpr permits ListExpr, Number, Symbol, Permutation {
    }

    public record ListExpr(List<? extends LispExpr> exprs) implements LispExpr {
        public static ListExpr of(List<? extends LispExpr> exprs) {
            return new ListExpr(exprs);
        }

        public boolean startsWith(Symbol smb) {
            return !exprs.isEmpty() && exprs.getFirst().equals(smb);
        }

        public boolean isEmpty() {
            return exprs.isEmpty();
        }

        public boolean startsWithNumber() {
            return !exprs.isEmpty() && exprs.getFirst() instanceof Number;
        }

        public boolean startsWithList() {
            if (exprs.isEmpty()) {
                return false;
            }
            LispExpr first = exprs.getFirst();
            return first instanceof ListExpr;
        }

        public boolean isSingleton() {
            return length() == 1;
        }

        public LispExpr get(int n) {
            return exprs.get(n);
        }

        public int length() {
            return exprs.size();
        }

        @Override
        public String toString() {
            return exprs.stream()
                    .map(LispExpr::toString)
                    .collect(joining(" ", "(", ")"));
        }
    }

    public record Symbol(String name) implements LispExpr {
        public static Symbol of(String name) {
            return new Symbol(name);
        }

        public static Symbol of(char[] input, int off, int len) {
            char[] smb = new char[len];
            System.arraycopy(input, off, smb, 0, len);
            return new Symbol(new String(smb));
        }

        @Override
        public String toString() {
            return name;
        }
    }

    public record Number(int number) implements LispExpr {
        public static Number of(int n) {
            return new Number(n);
        }

        public static Number of(String n) {
            return new Number(Integer.parseInt(n));
        }

        public static Number of(char[] input, int off, int len) {
            char[] smb = new char[len];
            System.arraycopy(input, off, smb, 0, len);
            return of(new String(smb));
        }

        @Override
        public String toString() {
            return Integer.toString(number);
        }
    }

    private static ListExpr parseList(PushbackReader reader) throws IOException {
        List<LispExpr> result = new ArrayList<>();
        int d;
        while ((d = reader.read()) != -1) {
            char c = (char) d;
            if (c == ')') {
                return ListExpr.of(result);
            } else if (c != ' ' && c != '\n') {
                reader.unread(c);
                result.add(parse(reader));
            }
        }
        throw new IllegalArgumentException("unmatched parentheses");
    }

    private static Number parseNumber(PushbackReader reader, char c) throws IOException {
        int d;
        char[] smb = new char[16];
        int len = 1;
        smb[0] = c;
        while ((d = reader.read()) != -1) {
            c = (char) d;
            if (c >= '0' && c <= '9') {
                smb[len++] = c;
            } else if (c == ' ' || c == '\n' || c == '(' || c == ')' || c == '=') {
                reader.unread(c);
                return Number.of(smb, 0, len);
            } else {
                throw new IllegalArgumentException("digit expected: " + c);
            }
        }
        return Number.of(smb, 0, len);
    }

    private static Symbol parseSymbol(PushbackReader reader, char c) throws IOException {
        int d;
        char[] smb = new char[16];
        int len = 1;
        smb[0] = c;
        while ((d = reader.read()) != -1) {
            c = (char) d;
            if (c >= '0' && c <= '9' || c >= 'a' && c <= 'z' || c >= 'A' && c <= 'Z' || c == '_' || c == '-') {
                smb[len++] = c;
            } else if (c == ' ' || c == '\t' || c == '\n' || c == ')') {
                reader.unread(c);
                return Symbol.of(smb, 0, len);
            } else {
                throw new IllegalArgumentException("bad symbol: " + c);
            }
        }
        return Symbol.of(smb, 0, len);
    }

    static LispExpr parse(PushbackReader reader) throws IOException {
        int d = reader.read();
        if (d == -1) {
            return NIL;
        }
        char c = (char) d;
        if (c == '-' || c >= '0' && c <= '9') {
            return parseNumber(reader, c);
        } else if (c >= 'a' && c <= 'z' || c >= 'A' && c <= 'Z' || c == '_') {
            return parseSymbol(reader, c);
        } else if (c == '(') {
            return parseList(reader);
        } else if (c == ')') {
            throw new IllegalArgumentException("unmatched parentheses");
        } else {
            throw new IllegalArgumentException("bad input: " + c + "(" + (int) c + ")");
        }
    }

    static boolean consumeWhitespace(PushbackReader reader) throws IOException {
        int d;
        while ((d = reader.read()) != -1) {
            if (d != ' ' && d != '\n' && d != '\t') {
                reader.unread(d);
                return true;
            }
        }
        return false;
    }
}
