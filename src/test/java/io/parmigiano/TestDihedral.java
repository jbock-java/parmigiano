package io.parmigiano;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static io.parmigiano.Permutation.cycle;
import static io.parmigiano.Permutation.identity;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TestDihedral {

    @Test
    void testD4() {
        Permutation p = cycle(0, 1, 2, 3);
        Permutation q = cycle(0, 2);
        assertEquals(8, span(p, q).size());
    }

    @Test
    void testDihedral() {
        Set<Permutation> d4 = span(cycle(1, 2), cycle(1, 3, 2, 4));
        assertEquals(8, d4.size());
        assertTrue(d4.contains(Permutation.cycle(1, 3).compose(2, 4)));
        assertTrue(d4.contains(Permutation.cycle(1, 2).compose(3, 4)));
        assertTrue(d4.contains(Permutation.cycle(1, 4).compose(2, 3)));
        assertTrue(d4.contains(Permutation.cycle(1, 2)));
        assertTrue(d4.contains(Permutation.cycle(3, 4)));
        assertTrue(d4.contains(Permutation.cycle(1, 3, 2, 4)));
        assertTrue(d4.contains(Permutation.cycle(1, 4, 2, 3)));
        assertTrue(d4.contains(identity()));
    }

    @Test
    void testD5() {
        Permutation p = cycle(0, 1, 2, 3, 4);
        Permutation q = cycle(0, 4).compose(1, 3);
        assertEquals(10, span(p, q).size());
    }

    @Test
    void testD6() {
        Permutation p = cycle(0, 1, 2, 3, 4, 5);
        Permutation q = cycle(0, 5).compose(1, 4).compose(2, 3);
        assertEquals(12, span(p, q).size());
    }


    private Set<Permutation> span(Permutation p, Permutation q) {
        Set<Permutation> result = new HashSet<>();
        result.add(p);
        result.add(q);
        while (true) {
            List<Permutation> objects = new ArrayList<>(result.size() * 4);
            for (Permutation permutation : result) {
                objects.add(permutation.compose(p));
                objects.add(permutation.compose(q));
                objects.add(p.compose(permutation));
                objects.add(q.compose(permutation));
            }
            if (!result.addAll(objects)) {
                break;
            }
        }
        return result;
    }
}
