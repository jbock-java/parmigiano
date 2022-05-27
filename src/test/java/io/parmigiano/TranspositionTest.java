package io.parmigiano;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TranspositionTest {

    @Test
    void testProd() {
        Permutation p = Transposition.product(Transposition.swap(0, 1), Transposition.swap(1, 2));
        assertEquals("cab", p.apply("abc"));
    }

    @Test
    void testCommute() {
        Transposition.DefaultTranspositionFactory factory = new Transposition.DefaultTranspositionFactory(10);
        for (int __ = 0; __ < 10; __++) {
            Transposition p = Transposition.random(factory, 10);
            Transposition q = Transposition.random(factory, 10);
            if (Transposition.product(p, q).equals(Transposition.product(q, p))) {
                assertTrue(p.commutesWith(q));
                assertTrue(q.commutesWith(p));
            } else {
                assertFalse(p.commutesWith(q), () -> "p=" + p + ",q=" + q);
                assertFalse(q.commutesWith(p));
            }
        }
    }
}