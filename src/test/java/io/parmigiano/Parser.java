package io.parmigiano;

import io.parmigiano.LispParser.LispExpr;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PushbackReader;

public final class Parser {

    public static Permutation run(String s) {
        byte[] input = s.getBytes();
        byte[] mod = new byte[input.length + 5];
        mod[0] = '(';
        mod[1] = 'd';
        mod[2] = 'o';
        mod[3] = ' ';
        mod[mod.length - 1] = ')';
        System.arraycopy(input, 0, mod, 4, input.length);
        try (PushbackReader reader = new PushbackReader(new InputStreamReader(new ByteArrayInputStream(mod)))) {
            LispParser.consumeWhitespace(reader);
            LispExpr result = new Eval().eval(LispParser.parse(reader));
            return (Permutation) result;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static Permutation parse(String s) {
        return (Permutation) parseExpr(s);
    }

    static LispExpr parseExpr(String s) {
        try (PushbackReader reader = new PushbackReader(new InputStreamReader(new ByteArrayInputStream(s.getBytes())))) {
            LispParser.consumeWhitespace(reader);
            return new Eval().eval(LispParser.parse(reader));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
