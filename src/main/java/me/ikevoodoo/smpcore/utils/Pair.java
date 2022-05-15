package me.ikevoodoo.smpcore.utils;

public class Pair<A, B> implements Comparable<Pair<A, B>> {
    private final A a;
    private final B b;

    public Pair(A a, B b) {
        this.a = a;
        this.b = b;
    }

    public A getFirst() {
        return a;
    }

    public B getSecond() {
        return b;
    }

    @Override
    public int compareTo(Pair<A, B> o) {
        if(a instanceof Integer num) {
            return num.compareTo((Integer) o.getFirst());
        }

        return 0;
    }

    @Override
    public boolean equals(Object obj) {
        if(obj instanceof Pair<?, ?> pair) {
            return a.equals(pair.getFirst()) && b.equals(pair.getSecond());
        }

        return false;
    }

    @Override
    public int hashCode() {
        return a.hashCode() ^ b.hashCode();
    }
}
