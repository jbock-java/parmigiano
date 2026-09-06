package io.parmigiano;

import org.junit.jupiter.api.Test;

import static io.parmigiano.Parser.parse;
import static org.junit.jupiter.api.Assertions.assertEquals;

class S3Test {

    @Test
    void testS3() {
        assertEquals(parse("(1 2)"), parse("(0 2) (1 2) (0 1)"));
        assertEquals(parse("(0 1)"), parse("(0 2) (0 1) (1 2)"));
        assertEquals(parse("(0 2)"), parse("(1 2) (0 2) (0 1)"));
        assertEquals(parse("(0 1)"), parse("(1 2) (0 1) (0 2)"));
        assertEquals(parse("(0 2)"), parse("(0 1) (0 2) (1 2)"));
        assertEquals(parse("(1 2)"), parse("(0 1) (1 2) (0 2)"));
    }
}
