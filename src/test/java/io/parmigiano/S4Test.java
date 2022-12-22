package io.parmigiano;

import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;

import static io.parmigiano.Permutation.cycle;
import static org.junit.jupiter.api.Assertions.assertEquals;

class S4Test {

    @Test
    void testFourCycles() {
        Set<Permutation> set = new HashSet<>();
        set.add(cycle(0, 3, 1, 2));
        set.add(cycle(0, 3, 2, 1));
        set.add(cycle(0, 2, 1, 3));
        set.add(cycle(0, 1, 3, 2));
        set.add(cycle(0, 2, 3, 1));
        set.add(cycle(0, 1, 2, 3));
        assertEquals(6, set.size());
    }
}
