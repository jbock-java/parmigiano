package io.parmigiano;

import io.parmigiano.LispParser.LispExpr;
import io.parmigiano.LispParser.ListExpr;
import io.parmigiano.LispParser.Symbol;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static io.parmigiano.LispParser.NIL;

final class Eval {

    private final Map<Symbol, LispExpr> definitions = new HashMap<>();

    private LispExpr resolve(LispExpr expr) {
        return switch (expr) {
            case ListExpr listExpr -> resolveList(listExpr);
            case Symbol symbol -> {
                LispExpr lispExpr = resolve(symbol);
                yield switch (lispExpr) {
                    case ListExpr listExpr -> listExpr;
                    case LispParser.Number number -> number;
                    case Symbol smb -> {
                        if (symbol.equals(smb)) {
                            yield symbol;
                        }
                        yield resolve(smb);
                    }
                    case null -> symbol;
                    case Permutation p -> p;
                };
            }
            case LispParser.Number number -> number;
            case Permutation p -> p;
        };
    }

    private ListExpr resolveList(ListExpr listExpr) {
        if (listExpr.startsWith(Symbols.DEF)) {
            return ListExpr.of(List.of(
                    Symbols.DEF,
                    listExpr.get(1),
                    resolve(listExpr.get(2))));
        }
        return ListExpr.of(listExpr.exprs().stream()
                .map(this::resolve)
                .toList());
    }

    public LispExpr resolveSymbol(Symbol symbol) {
        LispExpr lispExpr = resolve(symbol);
        return switch (lispExpr) {
            case ListExpr listExpr -> listExpr;
            case LispParser.Number number -> number;
            case Symbol smb -> {
                if (symbol.equals(smb)) {
                    yield symbol;
                }
                yield resolve(smb);
            }
            case null -> symbol;
            case Permutation permutation -> permutation;
        };
    }

    LispExpr resolve(Symbol symbol) {
        return definitions.get(symbol);
    }

    public Permutation toPermutation(ListExpr list) {
        if (list.isEmpty() || list.startsWithNumber()) {
            int[] numbers = new int[list.length()];
            int numbers_pos = 0;
            for (LispExpr pr : list.exprs()) {
                numbers[numbers_pos++] = ((LispParser.Number) pr).number();
            }
            return Permutation.cycle(numbers);
        } else if (list.startsWithList()) {
            return Permutation.product(list.exprs().stream()
                    .map(this::eval)
                    .map(expr -> (Permutation) expr)
                    .toList());
        } else {
            throw new IllegalArgumentException("bad list: " + this);
        }
    }

    LispExpr evalListExpression(ListExpr list) {
        if (list.startsWith(Symbols.DO)) {
            if (list.length() == 1) {
                return NIL;
            }
            for (int j = 1; j < list.length() - 1; j++) {
                eval(list.get(j));
            }
            return eval(list.get(list.length() - 1));
        }
        if (list.startsWith(Symbols.INV)) {
            if (list.length() != 2) {
                throw new IllegalArgumentException("inv takes 1 parameter");
            }
            if (list.get(1) instanceof Permutation p) {
                return p.invert();
            }
            if (list.get(1) instanceof ListExpr l) {
                return toPermutation(l).invert();
            }
            throw new IllegalArgumentException("param of inv must be a permutation");
        }
        if (list.startsWith(Symbols.DEF)) {
            if (list.length() != 3) {
                throw new IllegalArgumentException("def takes 2 parameters");
            }
            Symbol lhs = (Symbol) list.get(1);
            LispExpr rhs = list.get(2);
            if (rhs instanceof Symbol) {
                throw new IllegalArgumentException("undefined: " + rhs);
            }
            definitions.put(lhs, rhs);
            return eval(rhs);
        }
        if (list.isSingleton()) {
            return eval(list.get(0));
        }
        if (list.startsWithList() || list.isEmpty() || list.startsWithNumber()) {
            return toPermutation(list);
        }
        return list;
    }

    LispExpr eval(LispExpr expr) {
        return switch (expr) {
            case ListExpr list -> evalListExpression(resolveList(list));
            case Symbol symbol -> {
                LispExpr result = resolveSymbol(symbol);
                if (result instanceof Symbol) {
                    yield result;
                }
                yield eval(result);
            }
            case LispParser.Number number -> number;
            case Permutation p -> p;
        };
    }
}
