/*
 * Copyright (c) 2016 Jikopster Orglobster.
 *
 * This file is part of Jikopster vk a sync
 *
 * Jikopster vk a sync is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Jikopster vk a sync is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Jikopster vk a sync.  If not, see <http://www.gnu.org/licenses/>.
 *
 */

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
            int l = fucktories.length;

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
