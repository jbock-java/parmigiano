package io.parmigiano;

import io.parmigiano.LispParser.LispExpr;
import io.parmigiano.LispParser.ListExpr;
import io.parmigiano.LispParser.Nothing;
import io.parmigiano.LispParser.Number;
import io.parmigiano.LispParser.Symbol;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

public class Main {

    private final Eval eval = new Eval();

    private LispExpr resolve(LispExpr expr) {
        return switch (expr) {
            case ListExpr listExpr -> resolveList(listExpr);
            case Symbol symbol -> {
                LispExpr lispExpr = eval.resolve(symbol);
                yield switch (lispExpr) {
                    case ListExpr listExpr -> listExpr;
                    case Number number -> number;
                    case Symbol smb -> {
                        if (symbol.equals(smb)) {
                            yield symbol;
                        }
                        yield resolve(smb);
                    }
                    case null -> symbol;
                    case Nothing nothing -> nothing;
                };
            }
            case Number number -> number;
            case Nothing nothing -> nothing;
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
        LispExpr lispExpr = eval.resolve(symbol);
        return switch (lispExpr) {
            case ListExpr listExpr -> listExpr;
            case Number number -> number;
            case Symbol smb -> {
                if (symbol.equals(smb)) {
                    yield symbol;
                }
                yield resolve(smb);
            }
            case null -> symbol;
            case Nothing nothing -> nothing;
        };
    }

    LispExpr evalExpression(LispExpr expr) {
        return switch (expr) {
            case ListExpr list -> {
                if (list.length() == 1) {
                    yield evalExpression(list.get(0));
                }
                yield  eval.evalListExpression(resolveList(list));
            }
            case Symbol symbol -> resolveSymbol(symbol);
            case Number number -> number;
            case Nothing nothing -> nothing;
        };
    }


    String getNextString(BufferedReader reader) throws IOException {
        String line;
        line = reader.readLine();
        if (line == null) {
            return null;
        }
        switch (evalExpression(Parser.parseExpr(line))) {
            case Number number -> {
                return number.toString();
            }
            case Symbol symbol -> {
                if (symbol.name().equals("q")) {
                    return null;
                } else {
                    return "?";
                }
            }
            case Nothing _ -> {
                return "()";
            }
            case ListExpr listExpr -> {
                if (listExpr.length() == 1) {
                    return listExpr.get(0).toString();
                }
                return listExpr.toPermutation().toString();
            }
        }
    }

    void run(BufferedReader reader) throws IOException {
        String line;
        while ((line = getNextString(reader)) != null) {
            try {
                System.out.println(line);
            } catch (RuntimeException e) {
                e.printStackTrace(System.err);
            }
        }
    }

    static void main() {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(System.in))) {
            new Main().run(reader);
        } catch (IOException e) {
            e.printStackTrace(System.err);
            System.exit(1);
        }
    }
}
