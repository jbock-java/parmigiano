package io.parmigiano;

import org.junit.jupiter.api.Test;

import static io.parmigiano.LispParser.parse;
import static org.junit.jupiter.api.Assertions.*;

class LispParserTest {

    @Test
    void testParse() {
        assertEquals("(1 (2 3))", parse("(1 * (2 * 3))").toString());
    }
}
