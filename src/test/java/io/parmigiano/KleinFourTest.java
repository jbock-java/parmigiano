package io.parmigiano;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import static io.parmigiano.Permutation.symmetricGroup;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class KleinFourTest {

    private final List<Permutation> klein = klein();

    @Test
    void leftCosetEqualRightCoset() {
        for (Permutation permutation : symmetricGroup(4)) {
            assertEquals(
                    Set.copyOf(leftKlein(permutation)),
                    Set.copyOf(rightKlein(permutation)));
        }
    }

    @Test
    void funProduct() {
        assertEquals(
                Permutation.create(0, 1, 2, 3),
                Permutation.create(0, 1).compose(2, 3).compose(1, 3));
    }

    @Test
    void testCosetsInS4() {
        Set<Set<Permutation>> cosets = new LinkedHashSet<>();
        symmetricGroup(4)
                .stream()
                .map(this::leftKlein)
                .map(Set::copyOf)
                .forEach(cosets::add);
        assertEquals(6, cosets.size());
        assertTrue(cosets.contains(Set.copyOf(klein)));
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

    private List<Permutation> leftKlein(Permutation g) {
        List<Permutation> result = new ArrayList<>(klein.size());
        for (Permutation h : klein) {
            result.add(g.compose(h));
        }
        return result;
    }

    private List<Permutation> rightKlein(Permutation g) {
        List<Permutation> result = new ArrayList<>(klein.size());
        for (Permutation h : klein) {
            result.add(h.compose(g));
        }
        return result;
    }

    private static List<Permutation> klein() {
        List<Permutation> result = new ArrayList<>(4);
        result.add(Permutation.create(0, 1).compose(2, 3));
        result.add(Permutation.create(0, 3).compose(1, 2));
        result.add(Permutation.create(0, 2).compose(1, 3));
        result.add(Permutation.identity());
        return result;
    }
}
