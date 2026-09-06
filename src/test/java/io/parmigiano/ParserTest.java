package io.parmigiano;

import io.parmigiano.Expr.Assignment;
import io.parmigiano.Expr.Symbol;
import org.junit.jupiter.api.Test;

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
        assertEquals(Symbol.of("a"), parseExpr("a"));
        assertEquals(Symbol.of("a"), parseExpr(" a"));
        assertEquals(Symbol.of("a"), parseExpr("a "));
        assertEquals(Symbol.of("a"), parseExpr(" a "));
    }

    @Test
    void testParseAssignment() {
        Expr expr = parseExpr("a = (1 2)");
        assertEquals(Assignment.of("a", parse("(1 2)")), expr);
    }
}
