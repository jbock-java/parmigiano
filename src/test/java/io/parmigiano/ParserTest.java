package io.parmigiano;

import io.parmigiano.Expr.Assignment;
import io.parmigiano.Expr.ListExpr;
import io.parmigiano.Expr.Symbol;
import org.junit.jupiter.api.Test;

import java.util.List;

import static io.parmigiano.Parser.parse;
import static io.parmigiano.Parser.parseExpr;
import static org.junit.jupiter.api.Assertions.assertEquals;

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
        Expr expr = parseExpr("a = (1 2)");
        assertEquals(assignmentOf("a", "(1 2)"), expr);
    }

    private static ListExpr listOf(String symbol) {
        return ListExpr.of(List.of(Symbol.of(symbol)));
    }

    private static ListExpr listOf(String smb1, String smb2) {
        return ListExpr.of(List.of(Symbol.of(smb1), Symbol.of(smb2)));
    }

    private static ListExpr listOf(Permutation permutation) {
        return ListExpr.of(List.of(permutation));
    }

    private static Assignment assignmentOf(String symbol, String rhs) {
        return Assignment.of(symbol, listOf(parse(rhs)));
    }
}
