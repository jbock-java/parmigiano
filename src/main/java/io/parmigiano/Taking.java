package io.parmigiano;

import io.parmigiano.Permutation.TakingBuilderInt;
import io.parmigiano.Permutation.TakingBuilderList;

import java.util.List;

/**
 * @deprecated use {@link Permutation#taking}
 */
@Deprecated(forRemoval = true)
public final class Taking {

    public static TakingBuilderInt from(int[] a) {
        return new TakingBuilderInt(a);
    }

    public static <E extends Comparable<E>> TakingBuilderList<E> from(List<E> a) {
        return new TakingBuilderList<>(a);
    }

    private Taking() {
    }
}
