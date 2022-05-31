package io.parmigiano;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class FifthRootsOfUnityTest {
    
    enum Root {
        ONE{
            @Override
            Root times(Root other) {
                return other;
            }
        },
        R1 {
            @Override
            Root times(Root other) {
                return other.next();
            }
        },
        R2{
            @Override
            Root times(Root other) {
                return other.next().next();
            }
        },
        R3{
            @Override
            Root times(Root other) {
                return other.next().next().next();
            }
        },
        R4{
            @Override
            Root times(Root other) {
                return other.next().next().next().next();
            }
        };

        abstract Root times(Root other);

        Root next() {
            Root[] values = values();
            return values[(ordinal() + 1) % values.length];
        }
    }

    @Test
    void testAll() {
        List<Root> roots = Arrays.asList(Root.values());
        List<List<Root>> permutations = Permutation.symmetricGroup(5).stream()
                .map(p -> p.apply(roots))
                .filter(r -> r.get(0).times(r.get(0)).equals(r.get(0)))
                .filter(r -> r.get(0).times(r.get(1)).equals(r.get(1)))
                .filter(r -> r.get(1).times(r.get(1)).equals(r.get(2)))
                .filter(r -> r.get(1).times(r.get(2)).equals(r.get(3)))
                .filter(r -> r.get(2).times(r.get(2)).equals(r.get(4)))
                .filter(r -> r.get(2).times(r.get(3)).equals(r.get(0)))
                .filter(r -> r.get(3).times(r.get(3)).equals(r.get(1)))
                .filter(r -> r.get(3).times(r.get(4)).equals(r.get(2)))
                .filter(r -> r.get(4).times(r.get(4)).equals(r.get(3)))
                .toList();
        assertTrue(permutations.contains(roots));
        assertEquals(4, permutations.size());
    }
}
