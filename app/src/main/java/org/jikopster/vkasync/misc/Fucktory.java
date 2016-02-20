package org.jikopster.vkasync.misc;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.NoSuchElementException;


public class Fucktory<T>
{
    public interface Condition extends Lambda.Function<Boolean> { }

    public interface Constructor<T> extends Lambda.Function<T> { }

    public Fucktory(Constructor<? extends T> constructor) {
        this(true, constructor);
    }

    public Fucktory(boolean condition, Constructor<? extends T> constructor) {
        this(() -> condition, constructor);
    }

    public Fucktory(Condition condition, Constructor<? extends T> constructor) {
        mCondition = condition;
        mConstructor = constructor;
    }

    private Condition mCondition;
    private Constructor<? extends T> mConstructor;

    public T fuck() {
        return mCondition.invoke()
                ? mConstructor.invoke()
                : null;
    }

    @SafeVarargs
    public static <T> Iterable<T> fuckEmAll(Fucktory<T>... fucktories) {
        ArrayList<T> cache = new ArrayList<>(fucktories.length);
        boolean[] bools = new boolean[fucktories.length];
        return () -> new Iterator() {
            int i = 0;
            int l = fucktories.length - 1;

            @Override
            public boolean hasNext() {
                return i < l;
            }

            @Override
            public T next() {
                do {
                    T t;
                    if (bools[i])
                        t = cache.get(i);
                    else {
                         bools[i] = true;
                        Fucktory<T> fucktory = fucktories[i];
                        cache.add(i, t = fucktory.fuck());
                    }
                    i++;
                    if (t != null) return t;
                } while (hasNext());
                throw new NoSuchElementException();
            }

            @Override
            public void remove() {
                throw new UnsupportedOperationException();
            }
        };
    }
}
