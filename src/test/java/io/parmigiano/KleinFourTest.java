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
        Set<Set<Permutation>> cosets = new LinkedHashSet<>();
        Permutation.symmetricGroup(4)
                .forEach(g -> {
                    Set<Permutation> gH = new HashSet<>();
                    for (Permutation h : klein()) {
                        gH.add(g.compose(h));
                    }
                    cosets.add(gH);
                });
        assertEquals(6, cosets.size());
        assertTrue(cosets.contains(Set.of(
                Permutation.create(0, 1).compose(Permutation.create(2, 3)),
                Permutation.create(0, 2).compose(Permutation.create(1, 3)),
                Permutation.create(0, 3).compose(Permutation.create(1, 2)),
                Permutation.identity())));
        assertTrue(cosets.contains(Set.of(
                Permutation.create(0, 1),
                Permutation.create(2, 3),
                Permutation.create(0, 2, 1, 3),
                Permutation.create(0, 3, 1, 2))));
        assertTrue(cosets.contains(Set.of(
                Permutation.create(0, 2),
                Permutation.create(1, 3),
                Permutation.create(0, 1, 2, 3),
                Permutation.create(0, 3, 2, 1))));
        assertTrue(cosets.contains(Set.of(
                Permutation.create(0, 3),
                Permutation.create(1, 2),
                Permutation.create(0, 1, 3, 2),
                Permutation.create(0, 2, 3, 1))));
        assertTrue(cosets.contains(Set.of(
                Permutation.create(0, 1, 3),
                Permutation.create(0, 2, 1),
                Permutation.create(0, 3, 2),
                Permutation.create(1, 2, 3))));
        assertTrue(cosets.contains(Set.of(
                Permutation.create(0, 1, 2),
                Permutation.create(0, 2, 3),
                Permutation.create(0, 3, 1),
                Permutation.create(1, 3, 2))));
    }

    private List<Permutation> klein() {
        List<Permutation> result = new ArrayList<>();
        result.add(Permutation.create(0, 1).compose(Permutation.create(2, 3)));
        result.add(Permutation.create(0, 3).compose(Permutation.create(1, 2)));
        result.add(Permutation.create(0, 2).compose(Permutation.create(1, 3)));
        result.add(Permutation.identity());
        return result;
    }
}
