package io.parmigiano;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PushbackReader;

public final class Parser {

    public static Permutation parse(String s) {
        byte[] input = s.getBytes();
        byte[] mod = new byte[input.length + 2];
        mod[0] = '(';
        mod[mod.length - 1] = ')';
        System.arraycopy(input, 0, mod, 1, input.length);
        try (PushbackReader reader = new PushbackReader(new InputStreamReader(new ByteArrayInputStream(mod)))) {
            LispParser.consumeWhitespace(reader);
            return LispParser.parse(reader).toPermutation();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
