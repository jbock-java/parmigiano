package io.parmigiano;

import io.parmigiano.LispParser.LispExpr;
import io.parmigiano.LispParser.ListExpr;
import io.parmigiano.LispParser.Symbol;

import java.util.HashMap;
import java.util.Map;

final class Eval {

    private final Map<Symbol, LispExpr> definitions = new HashMap<>();

    LispExpr resolve(Symbol symbol) {
        return definitions.get(symbol);
    }

    LispExpr evalListExpression(ListExpr list) {
        if (list.startsWith(Symbols.DEF)) {
            LispExpr tail = list.get(2);
            if (tail instanceof Symbol) {
                throw new IllegalArgumentException("undefined: " + tail);
            }
            definitions.put((Symbol) list.get(1), tail);
            return tail;
        }
        return list;
    }
}
