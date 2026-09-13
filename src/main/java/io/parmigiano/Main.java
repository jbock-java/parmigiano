package io.parmigiano;

import io.parmigiano.LispParser.ListExpr;
import io.parmigiano.LispParser.Number;
import io.parmigiano.LispParser.Symbol;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class Main {

    private final Eval eval = new Eval();

    String getNextString(BufferedReader reader) throws IOException {
        String line;
        line = reader.readLine();
        if (line == null) {
            return null;
        }
        switch (eval.eval(LispParser.parse(line.toCharArray()))) {
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
        while (true) {
            try {
                line = getNextString(reader);
                if (line == null) {
                    break;
                }
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
