/*
 * Copyright (c) 2014-2016 Jikopster Orglobster.
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
 */

package org.jikopster.vkasync.core;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.crashlytics.android.Crashlytics;

import org.jikopster.vkasync.misc.Lambda;

import java.util.ArrayList;
import java.util.List;


public class Exception extends Throwable
{
    public static abstract class Listener  {
        public abstract void done();
        public abstract void fail(@NonNull Exception e);
        public abstract void warn(@NonNull Exception e);
    }

    public static class Fatal extends Exception
    {
        public Fatal() { super(); }

        public Fatal(Throwable cause) { super(cause); }

        public Fatal(String message) { super(message); }

        @Override
        public boolean isFatal() { return true; }
    }

    public static class Multi extends Exception
    {
        private boolean fatal;

        @Override
        public boolean isFatal() { return fatal; }

        private List<Exception> list = new ArrayList<>();

        public Multi add(Exception e) {
            list.add(e);
            fatal |= e.isFatal();
            return this;
        }

        @Override
        protected void notify(Listener listener) {
            for (Exception e : list)
                e.notify(listener);
        }

        public void throwIfNotEmpty() throws Exception {
            switch (list.size()) {
                case 0:
                    return;
                case 1:
                    throw list.get(0);
                default:
                    throw this;
            }
        }
    }

    public static void log(Throwable throwable) {
        Crashlytics.logException(throwable);
        throwable.printStackTrace();
    }

    static void notify(@Nullable Exception e, @NonNull Listener listener) {
        if (e == null) {
            listener.done();
            return;
        }
        e.notify(listener);
        if (!e.isFatal())
            listener.done();
    }


    public Exception() { super(); }

    public Exception(Throwable cause) { super(cause); }

    public Exception(String message) { super(message); }


    public boolean isFatal() { return false; }

    public void log() { log(this); }

    protected void notify(Listener listener) {
        if (isFatal())
            listener.fail(this);
        else
            listener.warn(this);

        log();
    }
}
