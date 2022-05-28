package io.parmigiano;

class Preconditions {

    static void checkState(boolean b, String message) {
        if (!b) {
            throw new IllegalArgumentException(message);
        }
    }

    static void checkState(boolean b, String template, int n) {
        if (!b) {
            throw new IllegalArgumentException(String.format(template, n));
        }
    }
}
