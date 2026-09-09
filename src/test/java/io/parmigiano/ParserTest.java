package io.parmigiano;

import io.parmigiano.Expr.Assignment;
import io.parmigiano.LispParser.ListExpr;
import io.parmigiano.LispParser.Symbol;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.stream.Stream;

import static io.parmigiano.Parser.parse;
import static io.parmigiano.Parser.parseExpr;
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
    void testParseSymbol() {
        assertEquals(listOf("a"), parseExpr("a"));
        assertEquals(listOf("a"), parseExpr(" a"));
        assertEquals(listOf("a"), parseExpr("a "));
        assertEquals(listOf("a"), parseExpr(" a "));
    }

    @Test
    void testParseSymbols() {
        assertEquals(listOf("a", "b"), parseExpr("a b"));
        assertEquals(listOf("a", "b"), parseExpr(" a b"));
        assertEquals(listOf("a", "b"), parseExpr("a b "));
        assertEquals(listOf("a", "b"), parseExpr(" a b "));
        assertEquals(listOf("a", "b"), parseExpr("a * b"));
        assertEquals(listOf("a", "b"), parseExpr("a*b"));
        assertEquals(listOf("a", "b"), parseExpr(" a * b "));
        assertEquals(listOf("a", "b"), parseExpr(" a*b "));
    }

    @Test
    void testParseAssignment() {
        Expr expr = Parser.parseExpr("a = (1 2)");
        Assertions.assertInstanceOf(Assignment.class, expr);
        Assignment a = (Assignment) expr;
        assertEquals(Symbol.of("a"), a.lhs());
        assertEquals(Parser.parse("(1 2)"), a.rhs().eval());
    }

    @Test
    void testParseError() {
        assertThrows(RuntimeException.class, () -> parseExpr("("));
        assertThrows(RuntimeException.class, () -> parseExpr(")"));
        assertThrows(RuntimeException.class, () -> parseExpr(")("));
    }

    private static ListExpr listOf(String... symbols) {
        return ListExpr.of(Stream.of(symbols).map(Symbol::of).toList());
    }
}
