package io.parmigiano;

import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

class MainTest {

    private String eval(String... exprs) {
        String s = Stream.of(exprs).map(expr -> expr + "\n")
                .collect(Collectors.joining());
        EvalResult result = null;
        Main main = new Main();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(s.getBytes())))) {
            String line;
            while ((line = reader.readLine()) != null) {
                result = main.evalExpression(Parser.parseExpr(line));
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        if (result == null) {
            return null;
        }
        return result.toString();
    }

    @Test
    void testEval() {
        assertEquals("()", eval("a = (0 1)", "a a"));
        assertEquals("()", eval(""));
        assertEquals("(0 2 1)", eval("a = (0 1 2)", "a a"));
    }

    @Test
    void testError() {
        assertEquals("a", eval("a"));
        assertEquals("a", eval("a=a"));
        assertEquals("a", eval("a = a"));
        assertEquals("a", eval("a = a", "a"));
    }
}
