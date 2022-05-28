package io.parmigiano;

/**
 * An operation that swaps two elements of an array or list.
 */
public final class Transposition {

    /**
     * A factory that creates transpositions.
     * Transpositions are immutable, so various caching strategies can be considered.
     */
    public interface TranspositionFactory {
        Transposition swap(int j, int k);
    }

    public static final TranspositionFactory NON_CACHING_FACTORY = new DefaultTranspositionFactory(0);

    private final int j;
    private final int k;

    /**
     * A simple caching factory that maintains a permanent cache of transpositions below the configured length.
     */
    public static final class DefaultTranspositionFactory implements TranspositionFactory {

        private final Transposition[][] cache;

        /**
         * Create a new factory. If {@code maxCachedLength > 0}, the transpositions returned by the
         * {@code swap} method will be cached and reused if their {@code length} is {@code <= maxCachedLength}.
         * The cache will permanently store up to {@code maxCachedLength * (maxCachedLength - 1)} transpositions.
         *
         * @param maxCachedLength the maximum index that is moved by a cached transposition
         */
        public DefaultTranspositionFactory(int maxCachedLength) {
            this.cache = new Transposition[maxCachedLength][];
            for (int j = 1; j < maxCachedLength; j++)
                cache[j] = new Transposition[j];
        }

        /**
         * Get a transposition operation that swaps the element at the given indexes.
         *
         * @param j a non-negative number
         * @param k a non-negative number that must not be the same as {@code j}
         * @return a transposition operation
         * @throws java.lang.IllegalArgumentException if the arguments are equal or negative
         */
        public Transposition swap(int j, int k) {
            if (j < 0 || k < 0)
                ArrayUtil.negativeFailure();
            if (j == k)
                throw new IllegalArgumentException("arguments must not be equal");
            if (k > j) {
                // make sure that j is larger than k
                int temp = k;
                k = j;
                j = temp;
            }
            if (j < cache.length) {
                if (cache[j][k] == null)
                    cache[j][k] = new Transposition(j, k);
                return cache[j][k];
            }
            return new Transposition(j, k);
        }

    }

    /**
     * Return a random transposition that can be applied to arrays of the specified length.
     *
     * @param length an integer not less than {@code 2}
     * @param factory a transposition factory
     * @return a random transposition of length {@code length} or less
     * @throws IllegalArgumentException if {@code length} is less than {@code 2}
     */
    public static Transposition random(TranspositionFactory factory, int length) {
        if (length < 2)
            throw new IllegalArgumentException("minimum length of a transposition is 2");
        int j = (int) (length * Math.random());
        int k = (int) (length * Math.random());
        if (j == k)
            k = j == 0 ? 1 : j - 1;
        return factory.swap(j, k);
    }

    /**
     * Return a random transposition that can be applied to arrays of the specified length.
     *
     * @param length an integer not less than {@code 2}
     * @return a random transposition of length {@code length} or less
     * @throws IllegalArgumentException if {@code length} is less than {@code 2}
     */
    public static Transposition random(int length) {
        return random(NON_CACHING_FACTORY, length);
    }

    /**
     * Return an operation that swaps the elements at the given indexes.
     *
     * @param j a non-negative number
     * @param k a non-negative number
     * @return the transposition that swaps the elements at {@code j} and {@code k}
     * @throws IllegalArgumentException if {@code j < 0}, {@code k < 0} or {@code j == k}
     */
    public static Transposition swap(int j, int k) {
        return NON_CACHING_FACTORY.swap(j, k);
    }

    private Transposition(int j, int k) {
        assert j > k;
        this.j = j;
        this.k = k;
    }
    
    /**
     * Check if this transposition commutes with the other.
     *
     * @param other a transposition
     * @return true if {@code this.apply(other.apply(i)) == other.apply(this.apply(i))} for all integers {@code i}
     */
    public boolean commutesWith(Transposition other) {
        return (this.j != other.j && this.k != other.k && this.j != other.k && this.k != other.j)
                || (this.j == other.j && this.k == other.k);
    }

    /**
     * Get a permutation version of this operation.
     *
     * @return a permutation
     */
    public Permutation toPermutation() {
        return Permutation.create(j, k);
    }

    private Permutation compose(Permutation other) {
        return toPermutation().compose(other);
    }

    public Permutation compose(Transposition other) {
        return compose(other.toPermutation());
    }

    /**
     * Take the product of the given transpositions.
     * @param transpositions an array of transpositions
     * @return the product of the input
     */
    public static Permutation product(Transposition... transpositions) {
        int maxIndex = 0;
        for (Transposition t : transpositions)
            maxIndex = Math.max(maxIndex, t.j);
        int[] ranking = ArrayUtil.range(maxIndex + 1);
        for (Transposition t : transpositions) {
            int temp = ranking[t.k];
            ranking[t.k] = ranking[t.j];
            ranking[t.j] = temp;
        }
        return Permutation.fromRanking(ranking);
    }

    public String toString() {
        return String.format("(%d %d)", j, k);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Transposition that = (Transposition) o;
        return (j == that.j && k == that.k);
    }

    @Override
    public int hashCode() {
        int result = j;
        result = 31 * result + k;
        return result;
    }
}
