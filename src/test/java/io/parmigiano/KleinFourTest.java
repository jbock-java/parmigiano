package io.parmigiano;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static io.parmigiano.Permutation.symmetricGroup;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class KleinFourTest {

    private final List<Permutation> klein = List.of(
            Permutation.identity(),
            Permutation.create(0, 1).compose(2, 3),
            Permutation.create(0, 2).compose(1, 3),
            Permutation.create(0, 3).compose(1, 2));

    private final Set<Permutation> coset_a1 = Set.of(
            Permutation.create(0, 1, 2),
            Permutation.create(0, 2, 3),
            Permutation.create(0, 3, 1),
            Permutation.create(1, 3, 2));

    private final Set<Permutation> coset_a2 = Set.of(
            Permutation.create(0, 1, 3),
            Permutation.create(0, 2, 1),
            Permutation.create(0, 3, 2),
            Permutation.create(1, 2, 3));

    private final Set<Permutation> coset_na0 = Set.of(
            Permutation.create(0, 1),
            Permutation.create(2, 3),
            Permutation.create(0, 2, 1, 3),
            Permutation.create(0, 3, 1, 2));

    private final Set<Permutation> coset_na1 = Set.of(
            Permutation.create(0, 2),
            Permutation.create(1, 3),
            Permutation.create(0, 1, 2, 3),
            Permutation.create(0, 3, 2, 1));

    private final Set<Permutation> coset_na2 = Set.of(
            Permutation.create(0, 3),
            Permutation.create(1, 2),
            Permutation.create(0, 1, 3, 2),
            Permutation.create(0, 2, 3, 1));

    @Test
    void leftCosetEqualRightCoset() {
        for (Permutation permutation : symmetricGroup(4)) {
            assertEquals(
                    Set.copyOf(leftCoset(permutation)),
                    Set.copyOf(rightCoset(permutation)));
        }
    }

    @Test
    void funProduct() {
        assertEquals(
                Permutation.create(0, 1, 2, 3),
                Permutation.create(0, 1).compose(2, 3).compose(1, 3));
    }

    @Test
    void testCosetsInA4() {
        Set<Set<Permutation>> cosets = symmetricGroup(4)
                .stream()
                .filter(p -> p.signature() == 1) // A(4)
                .map(this::leftCoset)
                .map(Set::copyOf)
                .collect(Collectors.toSet());
        assertEquals(3, cosets.size());
        assertTrue(cosets.contains(Set.copyOf(klein)));
        assertTrue(cosets.contains(coset_a2));
        assertTrue(cosets.contains(coset_a1));
        assertEquals(coset_a1, Set.copyOf(leftCoset(Permutation.create(0, 1, 2))));
    }

    @Test
    void testCosetsNotInA4() {
        Set<Set<Permutation>> cosets = symmetricGroup(4)
                .stream()
                .filter(p -> p.signature() == -1)
                .map(this::leftCoset)
                .map(Set::copyOf)
                .collect(Collectors.toSet());
        assertEquals(3, cosets.size());
        assertTrue(cosets.contains(coset_na0));
        assertTrue(cosets.contains(coset_na2));
        assertTrue(cosets.contains(coset_na1));
        assertEquals(coset_na0, Set.copyOf(leftCoset(Permutation.create(0, 1))));
    }

    private List<Permutation> leftCoset(Permutation g) {
        List<Permutation> result = new ArrayList<>(klein.size());
        for (Permutation h : klein) {
            result.add(g.compose(h));
        }
        return result;
    }

    private List<Permutation> rightCoset(Permutation g) {
        List<Permutation> result = new ArrayList<>(klein.size());
        for (Permutation h : klein) {
            result.add(h.compose(g));
        }
        return result;
    }
}
