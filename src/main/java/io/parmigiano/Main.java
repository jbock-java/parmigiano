package io.parmigiano;

import io.parmigiano.LispParser.LispExpr;
import io.parmigiano.LispParser.ListExpr;
import io.parmigiano.LispParser.Number;
import io.parmigiano.LispParser.Symbol;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.function.Consumer;

public class Main implements Consumer<LispExpr> {

    private boolean running = true;

    private final Eval eval = new Eval();

    private final Consumer<String> out;

    public Main(Consumer<String> out) {
        this.out = out;
    }

    void getNextString(BufferedReader reader) throws IOException {
        String line;
        line = reader.readLine();
        if (line == null) {
            running = false;
            return;
        }
        LispParser.parse(line.toCharArray(), this);
    }

    @Override
    public void accept(LispExpr expr) {
        LispExpr ex = eval.eval(expr);
        switch (ex) {
            case Number number -> {
                out.accept(number.toString());
            }
            case Symbol symbol -> {
                if (symbol.name().equals("q")) {
                    running = false;
                } else {
                    out.accept("?");
                }
            }
            case ListExpr listExpr -> {
                if (listExpr.length() == 1) {
                    out.accept(listExpr.get(0).toString());
                } else {
                    out.accept(listExpr.toPermutation().toString());
                }
            }
        }
    }

    void run(BufferedReader reader) throws IOException {
        while (running) {
            try {
                getNextString(reader);
            } catch (RuntimeException e) {
                e.printStackTrace(System.err);
            }
        }
    }

    static void main() {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(System.in))) {
            new Main(System.out::println).run(reader);
        } catch (IOException e) {
            e.printStackTrace(System.err);
            System.exit(1);
        }
    }

    boolean isRunning() {
        return running;
    }
}
