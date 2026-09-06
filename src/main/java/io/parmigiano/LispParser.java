package io.parmigiano;

import java.io.CharArrayReader;
import java.io.IOException;
import java.io.PushbackReader;
import java.util.ArrayList;
import java.util.List;

import static java.util.stream.Collectors.joining;

public final class LispParser {
    public sealed interface LispExpr permits LispList, LispSymbol {
    }

    public record LispList(List<? extends LispExpr> exprs) implements LispExpr {
        public static LispList of(List<? extends LispExpr> exprs) {
            return new LispList(exprs);
        }

        @Override
        public String toString() {
            return stringify(this);
        }
    }

    public record LispSymbol(String name) implements LispExpr {
        public static LispSymbol of(String name) {
            return new LispSymbol(name);
        }

        public static LispSymbol of(char[] input, int off, int len) {
            char[] smb = new char[len];
            System.arraycopy(input, off, smb, 0, len);
            return new LispSymbol(new String(smb));
        }

        @Override
        public String toString() {
            return stringify(this);
        }
    }

    static LispList parseList(PushbackReader reader) throws IOException {
        List<LispExpr> result = new ArrayList<>();
        int c;
        while ((c = reader.read()) != -1) {
            if (c == ')') {
                return LispList.of(result);
            } else if (c != ' ') {
                reader.unread(c);
                result.add(parse(reader));
            }
        }
        throw new IllegalArgumentException("incomplete list");
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
                    } else if (c == ' ' || c == '(' || c == ')' || c == '=') {
                        reader.unread(c);
                        return LispSymbol.of(smb, 0, len);
                    } else {
                        throw new IllegalArgumentException("bad input: " + c);
                    }
                }
                return LispSymbol.of(smb, 0, len);
            } else if (c == '(') {
                return parseList(reader);
            } else if (c != ' ' && c != '*') {
                throw new IllegalArgumentException("bad input: " + c);
            }
        }
        throw new IllegalArgumentException("bad input?");
    }

    public static LispExpr parse(String s) {
        char[] chars = s.toCharArray();
        try (PushbackReader reader = new PushbackReader(new CharArrayReader(chars))) {
            return parse(reader);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static String stringify(LispExpr expr) {
        return switch (expr) {
            case LispList lispList -> lispList.exprs.stream()
                    .map(LispParser::stringify)
                    .collect(joining(" ", "(", ")"));
            case LispSymbol lispSymbol -> lispSymbol.name;
        };
    }
}
