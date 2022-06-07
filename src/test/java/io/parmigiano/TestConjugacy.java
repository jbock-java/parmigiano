package io.parmigiano;

import org.junit.jupiter.api.Test;

import static io.parmigiano.Permutation.create;
import static org.junit.jupiter.api.Assertions.assertEquals;

class TestConjugacy {

    @Test
    void testInvert() {
        Permutation p = create(0, 1);
        Permutation j = create(0, 1, 2);
        assertEquals(create(0, 2), conj(p, j));
        assertEquals(create(1, 2), conj(p, j.invert()));
    }

    private Permutation conj(Permutation p, Permutation j) {
        return Permutation.product(j.invert(), p, j);
    }
}
