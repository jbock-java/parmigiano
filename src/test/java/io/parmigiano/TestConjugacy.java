package io.parmigiano;

import org.junit.jupiter.api.Test;

import static io.parmigiano.Parser.parse;
import static org.junit.jupiter.api.Assertions.assertEquals;

class TestConjugacy {

    @Test
    void testInvert() {
        Permutation p = parse("(0 1)");
        Permutation j = parse("(0 1 2)");
        assertEquals(parse("(0 2)"), conj(p, j));
        assertEquals(parse("(1 2)"), conj(p, j.invert()));
    }

    private Permutation conj(Permutation p, Permutation j) {
        return Permutation.product(j.invert(), p, j);
    }
}
