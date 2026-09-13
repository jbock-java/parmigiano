package io.parmigiano;

import io.parmigiano.LispParser.LispExpr;
import io.parmigiano.LispParser.ListExpr;
import io.parmigiano.LispParser.Symbol;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
                };
            }
            case LispParser.Number number -> number;
        };
    }

    private ListExpr resolveList(ListExpr listExpr) {
        if (listExpr.startsWith(Symbols.DEF)) {
            return ListExpr.of(List.of(
                    listExpr.get(0),
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
        };
    }

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

    LispExpr eval(LispExpr expr) {
        return switch (expr) {
            case ListExpr list -> evalListExpression(resolveList(list));
            case Symbol symbol -> resolveSymbol(symbol);
            case LispParser.Number number -> number;
        };
    }
}
