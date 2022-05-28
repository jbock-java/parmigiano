package io.parmigiano;

import java.util.List;

public final class Taking {

    public record TakingBuilderList<E extends Comparable<E>>(List<E> from) {
        public Permutation to(List<E> to) {
            return Permutation.fromRanking(Rankings.from(from, to));
        }
    }

    public static final class TakingBuilderInt {
        private final int[] from;

        private TakingBuilderInt(int[] from) {
            this.from = from;
        }

        public Permutation to(int[] to) {
            return Permutation.fromRanking(Rankings.from(from, to));
        }
    }


    public static TakingBuilderInt from(int[] a) {
        return new TakingBuilderInt(a);
    }


    public static <E extends Comparable<E>> TakingBuilderList<E> from(List<E> a) {
        return new TakingBuilderList<>(a);
    }

    private Taking() {
    }
}
