package io.parmigiano;

import io.parmigiano.LispParser.LispExpr;
import io.parmigiano.LispParser.ListExpr;
import io.parmigiano.LispParser.Nothing;
import io.parmigiano.LispParser.Number;
import io.parmigiano.LispParser.Symbol;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

final class Eval {

    private final Map<Symbol, LispExpr> definitions = new HashMap<>();

    LispExpr resolve(Symbol symbol) {
        return definitions.get(symbol);
    }

    EvalResult eval(LispExpr expr) {
        return switch (expr) {
            case ListExpr listExpr -> evalListExpression(listExpr);
            case Nothing nothing -> nothing;
            case Number number -> number;
            case Symbol symbol -> symbol;
        };
    }

    EvalResult evalListExpression(ListExpr list) {
        if (list.startsWith(Symbols.DEF)) {
            definitions.put((Symbol) list.get(1), list.get(2));
            return eval(list.get(2));
        }
        List<Permutation> result = new ArrayList<>(list.exprs().size());
        int[] numbers = new int[list.exprs().size()];
        int numbers_pos = 0;
        EvalResult previous = null;
        for (LispExpr expr : list.exprs()) {
            EvalResult er = eval(expr);
            switch (er) {
                case Symbol smb -> throw new IllegalArgumentException("undefined: " + smb);
                case Permutation permutation -> {
                    if (previous != null && !previous.isPermutation()) {
                        throw new IllegalArgumentException("bad product");
                    }
                    result.add(permutation);
                }
                case Number number -> {
                    if (previous != null && !previous.isNumber()) {
                        throw new IllegalArgumentException("bad product");
                    }
                    numbers[numbers_pos++] = number.number();
                }
                case Nothing _ -> throw new IllegalArgumentException("nothing not expected here");
                case ListExpr _ -> throw new IllegalArgumentException("todo, create permutation");
            }
            previous = er;
        }
        // can we return list expression
        if (numbers_pos == 0) {
            return Permutation.product(result);
        } else {
            if (numbers_pos == 1) {
                return new Number(numbers[0]);
            }
            int[] cycle = new int[numbers_pos];
            System.arraycopy(numbers, 0, cycle, 0, numbers_pos);
            return Permutation.cycle(cycle);
        }
    }
}
