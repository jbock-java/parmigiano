package io.parmigiano;

import io.parmigiano.Expr.Assignment;
import io.parmigiano.LispParser.LispExpr;
import io.parmigiano.LispParser.ListExpr;
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
            case ListExpr listExpr -> ListExpr.of(listExpr.exprs().stream()
                    .map(this::resolve)
                    .toList());
            case Symbol symbol -> {
                LispExpr lispExpr = definitions.get(symbol);
                yield switch (lispExpr) {
                    case ListExpr listExpr -> listExpr;
                    case Symbol smb -> resolve(smb);
                    case null -> symbol;
                };
            }
        };
    }

    private LispExpr evalLispExpr(LispExpr rhs) {
        switch (rhs) {
            case Symbol symbol -> {
                return resolve(symbol);
            }
            case ListExpr list -> {
                return resolve(list);
            }
        }
    }

    Permutation evalExpression(Expr expr) {
        switch (expr) {
            case Assignment assignment -> {
                Symbol lhs = assignment.lhs();
                LispExpr lispExpr = evalLispExpr(assignment.rhs());
                definitions.put(lhs, lispExpr);
                return lispExpr.toPermutation();
            }
            case ListExpr listExpr -> {
                return evalLispExpr(listExpr).toPermutation();
            }
            case Symbol symbol -> {
                LispExpr resolved = resolve(symbol);
                if (resolved.isSymbol()) {
                    throw new IllegalArgumentException("unknown symbol: " + resolved);
                }
                return resolved.toPermutation();
            }
        }
    }

    void run(BufferedReader reader) throws IOException {
        String line;
        while ((line = reader.readLine()) != null) {
            if (line.isBlank()) {
                continue;
            }
            try {
                Permutation expr = evalExpression(Parser.parseExpr(line));
                if (expr == null) {
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
