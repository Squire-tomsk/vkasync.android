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
import android.support.annotation.NonNull;

import org.jikopster.vkasync.core.*;
import org.jikopster.vkasync.core.Exception;
import org.jikopster.vkasync.core.Worker.*;
import org.jikopster.vkasync.misc.Fucktory;
import org.jikopster.vkasync.misc.Lambda;
import org.jikopster.vkasync.preference.Bool;
import org.jikopster.vkasync.preference.Path;
import org.jikopster.vkasync.preference.Size;
import org.jikopster.vkasync.worker.Cache;
import org.jikopster.vkasync.worker.Cloud;
import org.jikopster.vkasync.worker.Local;
import org.jikopster.vkasync.worker.Media;

import java.io.File;
import java.util.HashMap;

public class Sync
{
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
        this.context = context;
        localPath = Path.getCurrent(context, Path.LOCAL);
        cachePath = Path.getCurrent(context, Path.CACHE);
    }

    private final Context context;
    private final String localPath;
    private final String cachePath;

    private final HashMap<String,Track> trackMap = new HashMap<>(100);
    private final Master.TrackList trackList = id -> {
        Track track;
        synchronized (trackMap) {
            if (null == (track = trackMap.get(id)))
                trackMap.put(id, (track = new Track(id)));
        }
        return track;
    };

    public void sync(Exception.Listener listener) {
        class Listener extends Exception.Listener
        {
            Listener(@NonNull Lambda.Action then) { this.then = then; }

            private final Lambda.Action then;

            @Override
            public void done() { then.invoke(); }
            @Override
            public void fail(Exception e) { listener.fail(e); }
            @Override
            public void warn(Exception e) { listener.warn(e); }
        }

        check(new Listener(() -> process(listener)));
    }

    public void check(Exception.Listener listener) {
        Iterable<Checker> checkers =
                Fucktory.<Checker>fuckEmAll(
                        new Fucktory<>(
                                () -> new Cloud.Checker(Size.get(context))),
                        new Fucktory<>(
                                () -> new Local.Checker(new File(localPath))),
                        new Fucktory<>(
                                Bool.get(context, Bool.CACHE),
                                () -> new Cache.Checker(new File(cachePath))),
                        new Fucktory<>(
                                () -> new Media.Checker(context, localPath))
                );

        Master.check(trackList, checkers, listener);
    }

    public void process(Exception.Listener listener) {
        Iterable<Processor> processors =
                Fucktory.<Processor>fuckEmAll(
                        new Fucktory<>(
                                Bool.get(context, Bool.CLOUD),
                                () -> new Cloud.Processor(context, localPath)),
                        new Fucktory<>(
                                Bool.get(context, Bool.LOCAL),
                                () -> new Local.Processor(context, localPath)),
                        new Fucktory<>(
                                Bool.get(context, Bool.CACHE),
                                () -> new Cache.Processor(
                                        context,
                                        new File(cachePath),
                                        new File(localPath),
                                        Bool.get(context, Bool.CLEAN))),
                        new Fucktory<>(
                                () -> new Media.Processor(context, localPath))
                );

        Master.process(trackMap.values(), processors, listener);
    }
}
