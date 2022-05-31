package io.parmigiano;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Example class that does not implement Comparable
 * and does not override equals or hashCode.
 */
public class MyInt {

    public static final Comparator<MyInt> COMP = Comparator.comparingInt(a -> a.n);

    static List<MyInt> box(int[] a) {
        List<MyInt> result = new ArrayList<>(a.length);
        for (int j : a) {
            result.add(new MyInt(j));
        }
        return result;
    }

    public final int n;

    public MyInt(int n) {
        this.n = n;
    }

    public String toString() {
        return Integer.toString(n);
    }
}
