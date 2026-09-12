package io.parmigiano;

// todo remove this
public sealed interface EvalResult permits Permutation, LispParser.Nothing, LispParser.Symbol, LispParser.Number, LispParser.ListExpr {
    default boolean isNumber() {
        return false;
    }

    default boolean isPermutation() {
        return false;
    }
}
