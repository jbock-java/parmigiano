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

    EvalResult evalExpression(LispExpr expr) {
        return switch (expr) {
            case ListExpr list -> eval.evalListExpression(resolveList(list));
            case Symbol symbol -> eval.eval(resolveSymbol(symbol));
            case Number number -> number;
            case Nothing nothing -> nothing;
        };
    }

    void run(BufferedReader reader) throws IOException {
        String line;
        while ((line = reader.readLine()) != null) {
            try {
                EvalResult expr = evalExpression(Parser.parseExpr(line));
                switch (expr) {
                    case Number number -> System.out.println(number);
                    case Symbol symbol -> {
                        if (symbol.name().equals("q")) {
                            return;
                        } else {
                            System.out.println("?");
                        }
                    }
                    case Permutation permutation -> System.out.println(permutation);
                    case Nothing _ -> {
                    }
                }
            } catch (RuntimeException e) {
                System.out.println(e.getMessage());
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
