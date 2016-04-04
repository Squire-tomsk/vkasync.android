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

package org.jikopster.vkasync.action;

import android.content.Context;

import org.jikopster.vkasync.core.*;
import org.jikopster.vkasync.core.Exception;
import org.jikopster.vkasync.core.Worker.*;
import org.jikopster.vkasync.preference.*;
import org.jikopster.vkasync.worker.*;

import java.io.File;
import java.util.HashMap;


public abstract class Action
{
    protected abstract Iterable<Checker>   getCheckers();
    protected abstract Iterable<Processor> getProcessors();


    public Action(Context context) {
        this.context = context;
        localPath = Path.getCurrent(context, Path.LOCAL);
        cachePath = Path.getCurrent(context, Path.CACHE);
    }

    public final Context context;

    public final String localPath;
    public final String cachePath;

    public Checker getCloudChecker() { return new Cloud.Checker(Size.get(context)); }
    public Checker getLocalChecker() { return new Local.Checker(new File(localPath)); }
    public Checker getMediaChecker() { return new Media.Checker(context, localPath); }
    public Checker getCacheChecker() { return new Cache.Checker(new File(cachePath)); }

    public Processor getCloudProcessor() { return new Cloud.Processor(context, localPath); }
    public Processor getLocalProcessor() { return new Local.Processor(context, localPath); }
    public Processor getMediaProcessor() { return new Media.Processor(context, localPath); }
    public Processor getCacheProcessor() {
        return new Cache.Processor(context, new File(cachePath), new File(localPath),
                Bool.get(context, Bool.CLEAN));
    }

    public final HashMap<String,Track> trackMap = new HashMap<>(100);
    public final Master.TrackList trackList = id -> {
        Track track;
        synchronized (trackMap) {
            if (null == (track = trackMap.get(id)))
                trackMap.put(id, (track = new Track(id)));
        }
        return track;
    };

    public void run(Exception.Listener listener) {
        check(new Listener(listener, () -> process(listener)));
    }

    public void check  (Exception.Listener listener) { check  (listener, getCheckers  ()); }
    public void process(Exception.Listener listener) { process(listener, getProcessors()); }

    protected void check  (Exception.Listener listener, Iterable<Checker>   checkers  ) {
        Master.check(trackList, checkers, listener);
    }
    protected void process(Exception.Listener listener, Iterable<Processor> processors) {
        Master.process(trackMap.values(), processors, listener);
    }
}
