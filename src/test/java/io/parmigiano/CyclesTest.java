package io.parmigiano;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CyclesTest {

    @Test
    void testUnApply() {
        Permutation p = Permutation.random(100);
        Permutation q = p.invert();
        for (int i = 0; i <= p.maxMovedIndex(); i++)
            assertEquals(i, q.apply(p.apply(i)));
    }

    @Test
    void testApply() {
        Permutation cycle = Permutation.cycle(0, 1);
        Assertions.assertEquals(1, cycle.maxMovedIndex());
        Assertions.assertEquals(0, cycle.apply(1));
        Assertions.assertEquals(1, cycle.apply(0));
        Assertions.assertEquals(2, cycle.apply(2));
        Assertions.assertEquals(10, cycle.apply(10));
    }    
}
