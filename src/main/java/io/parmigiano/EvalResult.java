package io.parmigiano;

public sealed interface EvalResult permits Permutation, LispParser.Symbol {
    default boolean isSymbol() {
        return false;
    }
}
