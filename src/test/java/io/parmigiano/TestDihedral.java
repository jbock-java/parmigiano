package io.parmigiano;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static io.parmigiano.Parser.parse;
import static io.parmigiano.Permutation.identity;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TestDihedral {

    @Test
    void testD4() {
        Permutation p = parse("(0 1 2 3)");
        Permutation q = parse("(0 2)");
        assertEquals(8, span(p, q).size());
    }

    @Test
    void testDihedral() {
        Set<Permutation> d4 = span(parse("(1 2)"), parse("(1 3 2 4)"));
        assertEquals(8, d4.size());
        assertTrue(d4.contains(parse("(* (1 3) (2 4))")));
        assertTrue(d4.contains(parse("(* (1 2) (3 4))")));
        assertTrue(d4.contains(parse("(* (1 4) (2 3))")));
        assertTrue(d4.contains(parse("(1 2)")));
        assertTrue(d4.contains(parse("(3 4)")));
        assertTrue(d4.contains(parse("(1 3 2 4)")));
        assertTrue(d4.contains(parse("(1 4 2 3)")));
        assertTrue(d4.contains(identity()));
    }

    @Test
    void testD5() {
        Permutation p = parse("(0 1 2 3 4)");
        Permutation q = parse("(* (0 4) (1 3))");
        assertEquals(10, span(p, q).size());
    }

    @Test
    void testD6() {
        Permutation p = parse("(0 1 2 3 4 5)");
        Permutation q = parse("(* (0 5) (1 4) (2 3))");
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
