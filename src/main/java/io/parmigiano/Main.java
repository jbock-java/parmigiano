package io.parmigiano;

import io.parmigiano.Expr.Assignment;
import io.parmigiano.Expr.ListExpr;
import io.parmigiano.Expr.Symbol;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Main {
    
    private final Map<Symbol, Permutation> definitions = new HashMap<>();
    
    private Permutation evalListExpression(ListExpr listExpr) {
        List<Permutation> result = new ArrayList<>(listExpr.exprs().size());
        for (Expr expr : listExpr.exprs()) {
            Expr x = evalExpression(expr);
            if (x instanceof Symbol s) {
                x = resolve(s);
            }
            if (x instanceof Permutation p) {
                result.add(p);
            } else {
                return null; 
            } 
        }
        return Permutation.product(result);
    }

    private Permutation resolve(Symbol symbol) {
        return definitions.get(symbol);
    }

    private Expr evalExpression(Expr expr) {
        switch (expr) {
            case Assignment assignment -> {
                Symbol lhs = assignment.lhs();
                Expr rhs = evalExpression(assignment.rhs());
                switch (rhs) {
                    case Assignment _, ListExpr _, Symbol _ -> {
                        return null;
                    }
                    case Permutation p -> {
                        definitions.put(lhs, p);
                        return p;
                    }
                    case null -> {
                        return null;
                    }
                }
            }
            case ListExpr listExpr -> {
                return evalListExpression(listExpr);
            }
            case Symbol symbol -> {
                return resolve(symbol);
            }
            case Permutation permutation -> {
                return permutation;
            }
        }
    }
    
    void run(BufferedReader reader) throws IOException {
        String line;
        while ((line = reader.readLine()) != null) {
            Expr expr = evalExpression(Parser.parseExpr(line));
            if (expr == null) {
                System.out.println("?");
            } else {
                System.out.println(expr); 
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
