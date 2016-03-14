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
 *
 */

package org.jikopster.vkasync.action;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;

import org.jikopster.vkasync.core.*;
import org.jikopster.vkasync.core.Exception;
import org.jikopster.vkasync.core.Worker.*;
import org.jikopster.vkasync.misc.Fucktory;
import static org.jikopster.vkasync.misc.Lambda.*;
import org.jikopster.vkasync.preference.Bool;
import org.jikopster.vkasync.preference.Path;
import org.jikopster.vkasync.preference.Size;
import org.jikopster.vkasync.worker.Cache;
import org.jikopster.vkasync.worker.Cloud;
import org.jikopster.vkasync.worker.Local;
import org.jikopster.vkasync.worker.Media;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class Sync
{
    public static abstract class Listener implements Master.Listener
    {
        public Listener() {
            mMethods.put(Cloud.FLAG, this::onCloud);
            mMethods.put(Local.FLAG, this::onLocal);
            mMethods.put(Cache.FLAG, this::onCache);
        }

        private HashMap<Integer, Runnable> mMethods = new HashMap<>(3);

        private Handler mHandler = new Handler(Looper.getMainLooper());

        protected void onFlag(int flag) {
            for (Map.Entry<Integer, Runnable> e : mMethods.entrySet())
                if (0 < (e.getKey() & flag))
                    mHandler.post(e.getValue());
        }

        public void invoke(Exception e) {
            if (e == null) {
                onComplete();
                return;
            }
            if (e instanceof Exception.Fatal)
                onFail(e);
            else
                onWarning(e);
        }

        public abstract void onComplete();
        public abstract void onFail(Exception e);
        public abstract void onWarning(Exception e);

        public void onCloud() { }
        public void onLocal() { }
        public void onCache() { }
    }

    class Track extends org.jikopster.vkasync.core.Track
    {
        public Track(String id, @NonNull Action1<Integer> listener) {
            super(id);
            mListener = listener;
        }

        private final Action1<Integer> mListener;

        public Track set(int flag) {
            super.set(flag);
            mListener.invoke(flag);
            return this;
        }
    }

    public static String getMessage(Context context, @NonNull Exception e) {
        int resId = getMessageResId(e);
        return resId == 0
                ? null
                : context.getString(resId);
    }

    public static int getMessageResId(@NonNull Exception ex) {
        try {
            throw ex;
        } catch (Exception e) {
            return 0;
        }
    }

    public Sync(Context context) {
        mContext = context;
        mLocalPath = Path.getCurrent(context, Path.LOCAL);
        mCachePath = Path.getCurrent(context, Path.CACHE);
    }

    private final Context mContext;

    private final String mLocalPath;
    private final String mCachePath;
    private final HashMap<String,Track> mTracks = new HashMap<>(100);
    public void sync(Listener listener) {
        class Listener extends Sync.Listener
        {
            Listener(@NonNull Action then) {
                this.then = then;
            }

            private final Action then;

            @Override
            protected void onFlag(int flag) {
                listener.onFlag(flag);
            }

            @Override
            public void onComplete() {
                then.invoke();
            }
            @Override
            public void onFail(Exception e) {
                listener.onFail(e);
            }
            @Override
            public void onWarning(Exception e) {
                listener.onWarning(e);
            }
        }

        check(new Listener(() -> process(listener)));
    }

    public void check(Listener listener) {
        Iterable<Checker> checkers =
                Fucktory.<Checker>fuckEmAll(
                        new Fucktory<>(
                                () -> new Cloud.Checker(Size.get(mContext))),
                        new Fucktory<>(
                                () -> new Local.Checker(new File(mLocalPath))),
                        new Fucktory<>(
                                Bool.get(mContext, Bool.CACHE),
                                () -> new Cache.Checker(new File(mCachePath))),
                        new Fucktory<>(
                                () -> new Media.Checker(mContext, mLocalPath))
                );

        Master.check(
                id -> {
                    Track track;
                    synchronized (mTracks) {
                        if (null == (track = mTracks.get(id)))
                            mTracks.put(id, track = new Track(id, listener::onFlag));
                    }
                    return track;
                },
                checkers,
                listener
        );
    }

    public void process(Listener listener) {
        Iterable<Processor> processors =
                Fucktory.<Processor>fuckEmAll(
                        new Fucktory<>(
                                Bool.get(mContext, Bool.CLOUD),
                                () -> new Cloud.Processor(mContext, mLocalPath)),
                        new Fucktory<>(
                                Bool.get(mContext, Bool.LOCAL),
                                () -> new Local.Processor(mContext, mLocalPath)),
                        new Fucktory<>(
                                Bool.get(mContext, Bool.CACHE),
                                () -> new Cache.Processor(
                                        mContext,
                                        new File(mCachePath),
                                        new File(mLocalPath),
                                        Bool.get(mContext, Bool.CLEAN))),
                        new Fucktory<>(
                                () -> new Media.Processor(mContext, mLocalPath))
                );

        Master.process(mTracks.values(), processors, listener);
    }
}
