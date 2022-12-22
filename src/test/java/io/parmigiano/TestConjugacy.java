package io.parmigiano;

import org.junit.jupiter.api.Test;

import static io.parmigiano.Permutation.cycle;
import static org.junit.jupiter.api.Assertions.assertEquals;

class TestConjugacy {

    @Test
    void testInvert() {
        Permutation p = cycle(0, 1);
        Permutation j = cycle(0, 1, 2);
        assertEquals(cycle(0, 2), conj(p, j));
        assertEquals(cycle(1, 2), conj(p, j.invert()));
    }

    private Permutation conj(Permutation p, Permutation j) {
        return Permutation.product(j.invert(), p, j);
    }
}
