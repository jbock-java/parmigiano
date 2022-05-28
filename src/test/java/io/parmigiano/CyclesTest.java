package io.parmigiano;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CyclesTest {

    @Test
    void testUnApply() {
        Cycles p = Cycles.random(100);
        for (int i = 0; i <= p.maxMovedIndex(); i++)
            assertEquals(i, p.unApply(p.apply(i)));
    }

    @Test
    void testApply() {
        Cycles cycle = Cycles.create(0, 1);
        Assertions.assertEquals(1, cycle.maxMovedIndex());
        Assertions.assertEquals(0, cycle.apply(1));
        Assertions.assertEquals(1, cycle.apply(0));
        Assertions.assertEquals(2, cycle.apply(2));
        Assertions.assertEquals(10, cycle.apply(10));
    }    
}
