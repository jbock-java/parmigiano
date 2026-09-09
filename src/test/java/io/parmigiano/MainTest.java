package io.parmigiano;

import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class MainTest {

    private final Main main = new Main();

    private String eval(String... exprs) {
        String s = Stream.of(exprs).map(expr -> expr + "\n")
                .collect(Collectors.joining());
        Permutation result = null;
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(s.getBytes())))) {
            String line;
            while ((line = reader.readLine()) != null) {
                result = main.evalExpression(Parser.parseExpr(line));
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        assertNotNull(result);
        return result.toString();
    }

    @Test
    void testEval() {
        assertEquals("()", eval("a = (0 1)", "a a"));
        assertEquals("(0 2 1)", eval("a = (0 1 2)", "a a"));
    }
}
