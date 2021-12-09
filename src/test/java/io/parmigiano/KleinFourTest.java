package io.parmigiano;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import static io.parmigiano.Cycles.cycle;
import static io.parmigiano.Transposition.swap;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class KleinFourTest {

    @Test
    void testCosetsInS4() {
        Set<Set<Permutation>> cosets = new LinkedHashSet<>();
        Permutation.symmetricGroup(4)
                .forEach(g -> {
                    Set<Permutation> gH = new TreeSet<>();
                    for (Permutation h : klein()) {
                        gH.add(g.compose(h));
                    }
                    cosets.add(gH);
                });
        assertEquals(6, cosets.size());
        assertTrue(cosets.contains(Set.of(
                cycle(0, 1).compose(cycle(2, 3)),
                cycle(0, 2).compose(cycle(1, 3)),
                cycle(0, 3).compose(cycle(1, 2)),
                Permutation.identity())));
        assertTrue(cosets.contains(Set.of(
                cycle(0, 1),
                cycle(2, 3),
                cycle(0, 2, 1, 3),
                cycle(0, 3, 1, 2))));
        assertTrue(cosets.contains(Set.of(
                cycle(0, 2),
                cycle(1, 3),
                cycle(0, 1, 2, 3),
                cycle(0, 3, 2, 1))));
        assertTrue(cosets.contains(Set.of(
                cycle(0, 3),
                cycle(1, 2),
                cycle(0, 1, 3, 2),
                cycle(0, 2, 3, 1))));
        assertTrue(cosets.contains(Set.of(
                cycle(0, 1, 3),
                cycle(0, 2, 1),
                cycle(0, 3, 2),
                cycle(1, 2, 3))));
        assertTrue(cosets.contains(Set.of(
                cycle(0, 1, 2),
                cycle(0, 2, 3),
                cycle(0, 3, 1),
                cycle(1, 3, 2))));
    }

    private List<Permutation> klein() {
        List<Permutation> result = new ArrayList<>();
        result.add(swap(0, 1).toPermutation().compose(swap(2, 3).toPermutation()));
        result.add(swap(0, 3).toPermutation().compose(swap(1, 2).toPermutation()));
        result.add(swap(0, 2).toPermutation().compose(swap(1, 3).toPermutation()));
        result.add(Permutation.identity());
        return result;
    }
}
