package io.parmigiano;

import io.parmigiano.Expr.Symbol;
import org.junit.jupiter.api.Test;

import static io.parmigiano.Parser.parse;
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
        assertEquals(Symbol.of("a"), parse("a"));
        assertEquals(Symbol.of("a"), parse(" a"));
        assertEquals(Symbol.of("a"), parse("a "));
        assertEquals(Symbol.of("a"), parse(" a "));
    }
}
