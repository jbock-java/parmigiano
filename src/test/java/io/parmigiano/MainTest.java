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
        String result = null;
        String line;
        Main main = new Main();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(s.getBytes())))) {
            do {
                line = main.getNextString(reader);
                if (line != null) {
                    result = line;
                }
            } while (line != null);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return result;
    }

    @Test
    void testEval() {
        assertEquals("()", eval("def a (0 1)", "a a"));
        assertEquals("(0 2 1)", eval("(def a (0 1 2))", "a a"));
    }

    @Test
    void testNothing() {
        assertEquals("()", eval(""));
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
        assertEquals("?", eval("a"));
        assertThrows(RuntimeException.class, () -> eval("(def a a)"));
    }

    @Test
    void testBadInput() {
        assertThrows(RuntimeException.class, () -> eval("(0 1) 2"));
    }

    @Test
    void testBadCycle() {
        assertThrows(RuntimeException.class, () -> eval("(1 1)"));
    }
}
