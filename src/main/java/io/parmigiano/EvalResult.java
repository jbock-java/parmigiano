package io.parmigiano;

public sealed interface EvalResult permits Permutation, LispParser.Nothing, LispParser.Symbol, LispParser.Number {
    default boolean isNumber() {
        return false;
    }

    default boolean isPermutation() {
        return false;
    }
}
