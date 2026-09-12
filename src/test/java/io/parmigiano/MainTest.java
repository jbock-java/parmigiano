package io.parmigiano;

import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

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
        assertEquals("()", eval("def a (0 1)", "a a"));
        assertEquals("(0 2 1)", eval("(def a (0 1 2))", "a a"));
    }

    @Test
    void testNothing() {
        assertEquals("nothing", eval(""));
    }

    @Test
    void testNumber() {
        assertEquals("1", eval("1"));
    }

    @Test
    void testNumbers() {
        assertEquals("(0 1)", eval("(def a 0)", "(def b 1)", "a b"));
    }

    @Test
    void testDef() {
        assertEquals("1", eval("(def a 1)", "a"));
    }

    @Test
    void testDoubleDef() {
        assertEquals("1", eval("(def a 0)", "(def a 1)", "a"));
    }

    @Test
    void testUndefined() {
        assertThrows(RuntimeException.class, () -> eval("a"));
        assertThrows(RuntimeException.class, () -> eval("(def a a)"));
    }

    @Test
    void testBadInput() {
        assertThrows(RuntimeException.class, () -> eval("(0 1) 2"));
    }
}
