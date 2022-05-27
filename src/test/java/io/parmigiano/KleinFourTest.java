package io.parmigiano;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class KleinFourTest {

    @Test
    void testCosetsInS4() {
        Set<Set<Cycles>> cosets = new LinkedHashSet<>();
        Cycles.symmetricGroup(4)
                .forEach(g -> {
                    Set<Cycles> gH = new HashSet<>();
                    for (Cycles h : klein()) {
                        gH.add(g.compose(h));
                    }
                    cosets.add(gH);
                });
        assertEquals(6, cosets.size());
        assertTrue(cosets.contains(Set.of(
                Cycles.create(0, 1).compose(Cycles.create(2, 3)),
                Cycles.create(0, 2).compose(Cycles.create(1, 3)),
                Cycles.create(0, 3).compose(Cycles.create(1, 2)),
                Cycles.identity())));
        assertTrue(cosets.contains(Set.of(
                Cycles.create(0, 1),
                Cycles.create(2, 3),
                Cycles.create(0, 2, 1, 3),
                Cycles.create(0, 3, 1, 2))));
        assertTrue(cosets.contains(Set.of(
                Cycles.create(0, 2),
                Cycles.create(1, 3),
                Cycles.create(0, 1, 2, 3),
                Cycles.create(0, 3, 2, 1))));
        assertTrue(cosets.contains(Set.of(
                Cycles.create(0, 3),
                Cycles.create(1, 2),
                Cycles.create(0, 1, 3, 2),
                Cycles.create(0, 2, 3, 1))));
        assertTrue(cosets.contains(Set.of(
                Cycles.create(0, 1, 3),
                Cycles.create(0, 2, 1),
                Cycles.create(0, 3, 2),
                Cycles.create(1, 2, 3))));
        assertTrue(cosets.contains(Set.of(
                Cycles.create(0, 1, 2),
                Cycles.create(0, 2, 3),
                Cycles.create(0, 3, 1),
                Cycles.create(1, 3, 2))));
    }

    private List<Cycles> klein() {
        List<Cycles> result = new ArrayList<>();
        result.add(Cycles.create(0, 1).compose(Cycles.create(2, 3)));
        result.add(Cycles.create(0, 3).compose(Cycles.create(1, 2)));
        result.add(Cycles.create(0, 2).compose(Cycles.create(1, 3)));
        result.add(Cycles.identity());
        return result;
    }
}
