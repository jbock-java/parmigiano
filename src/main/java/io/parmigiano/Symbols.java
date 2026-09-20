package io.parmigiano;

import io.parmigiano.LispParser.Symbol;

public final class Symbols {

    public static final Symbol MUL = Symbol.of("*");
    public static final Symbol DEF = Symbol.of("def");
    public static final Symbol INV = Symbol.of("inv");
    public static final Symbol DO = Symbol.of("do");

    private Symbols() {
    }
}
