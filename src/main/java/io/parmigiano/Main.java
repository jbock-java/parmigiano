package io.parmigiano;

import io.parmigiano.Expr.Assignment;
import io.parmigiano.LispParser.LispExpr;
import io.parmigiano.LispParser.ListExpr;
import io.parmigiano.LispParser.Number;
import io.parmigiano.LispParser.Symbol;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

public class Main {

    private final Map<Symbol, LispExpr> definitions = new HashMap<>();

    private LispExpr resolve(LispExpr expr) {
        return switch (expr) {
            case ListExpr listExpr -> {
                if (listExpr.exprs().size() == 1) {
                    yield resolve(listExpr.exprs().getFirst());
                }
                yield ListExpr.of(listExpr.exprs().stream()
                        .map(this::resolve)
                        .toList());
            }
            case Symbol symbol -> {
                LispExpr lispExpr = definitions.get(symbol);
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
                };
            }
            case Number number -> number;
        };
    }

    EvalResult evalExpression(Expr expr) {
        switch (expr) {
            case Assignment assignment -> {
                LispExpr lispExpr = resolve(assignment.rhs());
                definitions.put(assignment.lhs(), lispExpr);
                return lispExpr.eval();
            }
            case ListExpr listExpr -> {
                return resolve(listExpr).eval();
            }
            case Symbol symbol -> {
                return resolve(symbol).eval();
            }
            case Number number -> {
                return number;
            }
        }
    }

    void run(BufferedReader reader) throws IOException {
        String line;
        while ((line = reader.readLine()) != null) {
            try {
                EvalResult expr = evalExpression(Parser.parseExpr(line));
                if (expr.isSymbol()) {
                    System.out.println("?");
                } else {
                    System.out.println(expr);
                }
            } catch (RuntimeException e) {
                System.out.println(e.getMessage());
            }
        }
    }

    public static void main(String[] args) {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(System.in))) {
            new Main().run(reader);
        } catch (IOException e) {
            e.printStackTrace(System.err);
            System.exit(1);
        }
    }
}
