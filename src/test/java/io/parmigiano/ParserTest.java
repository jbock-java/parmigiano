package io.parmigiano;

import io.parmigiano.LispParser.LispExpr;
import io.parmigiano.LispParser.ListExpr;
import io.parmigiano.LispParser.Number;
import io.parmigiano.LispParser.Symbol;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PushbackReader;
import java.util.List;
import java.util.stream.Stream;

import static io.parmigiano.Parser.parse;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ParserTest {

    @Test
    void testParseCycle() {
        assertEquals("()", parse("()").toString());
        assertEquals("(1 2)", parse("(1 2)").toString());
        assertEquals("(1 2 3)", parse("(1 2 3)").toString());
        assertEquals("(1 2) (3 4)", parse("(1 2) (3 4)").toString());
    }

    @Test
    void testWhitespace() {
        assertEquals(listOf("a", "b"), parseExpr("(a b)"));
        assertEquals(listOf("a", "b"), parseExpr("( a b)"));
        assertEquals(listOf("a", "b"), parseExpr("(a b )"));
        assertEquals(listOf("a", "b"), parseExpr("( a b )"));
        assertEquals(listOf("a", "b"), parseExpr("(a  b)"));
        assertEquals(listOf("a", "b"), parseExpr(" ( a  b ) "));
        assertEquals(listOf("a"), parseExpr("(a)"));
        assertEquals(listOf("a"), parseExpr("( a)"));
        assertEquals(listOf("a"), parseExpr("(a )"));
        assertEquals(listOf("a"), parseExpr("( a )"));
    }

    @Test
    void testParseAssignment() {
        LispExpr expr = parseExpr("(def a (1 2))");
        ListExpr list = (ListExpr) expr;
        assertEquals(Symbol.of("def"), list.get(0));
        assertEquals(Symbol.of("a"), list.get(1));
        assertEquals(ListExpr.of(List.of(Number.of(1), Number.of(2))), list.get(2));
    }

    @Test
    void testParseError() {
        assertThrows(RuntimeException.class, () -> parseExpr("("));
        assertThrows(RuntimeException.class, () -> parseExpr(")"));
        assertThrows(RuntimeException.class, () -> parseExpr(")("));
    }

    @Test
    void testParse() {
        assertEquals("(1 (2 3))", parseExpr("(1 (2 3))").toString());
    }

    private static LispExpr parseExpr(String s) {
        byte[] input = s.getBytes();
        try (PushbackReader reader = new PushbackReader(new InputStreamReader(new ByteArrayInputStream(input)))) {
            LispParser.consumeWhitespace(reader);
            return LispParser.parse(reader);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static ListExpr listOf(String... symbols) {
        return ListExpr.of(Stream.of(symbols).map(Symbol::of).toList());
    }
}
