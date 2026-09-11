package io.parmigiano;

public sealed interface EvalResult permits Permutation, LispParser.Symbol, LispParser.Number {
    default boolean isSymbol() {
        return false;
    }
    default boolean isNumber() {
        return false;
    }
    boolean isPermutation();
}
