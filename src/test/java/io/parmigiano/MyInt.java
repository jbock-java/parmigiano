package io.parmigiano;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Example class that does not implement Comparable
 * and does not override equals or hashCode.
 */
public class MyInt {

    public static class MyComparator implements Comparator<MyInt> {
        @Override
        public int compare(MyInt a, MyInt b) {
            return a.n - b.n;
        }
    }

    public static final Comparator<MyInt> COMP = new MyComparator();
    
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
