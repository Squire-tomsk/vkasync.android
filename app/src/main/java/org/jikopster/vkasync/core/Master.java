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

package org.jikopster.vkasync.core;

import android.os.AsyncTask;
import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

import static org.jikopster.vkasync.core.Worker.*;


public class Master {

    public interface TrackList {
        @NonNull Track get(String id);
    }

    static class Runner {
        Runner(Exception.Listener listener) { injected = listener; }

        List<AsyncTask> mates = new ArrayList<>(4);
        int count;

        final Exception.Listener injected;
        final Exception.Listener listener = new Exception.Listener() {
            @Override
            public void done() {
                if (--count > 0) return;
                mates.clear();
                injected.done();
            }
            @Override
            public void fail(Exception e) {
                count = 0;
                for (int i = mates.size(); 0 < i--; )
                    mates.get(i).cancel(true);
                mates.clear();
                injected.fail(e);
            }
            @Override
            public void warn(Exception e) {
                count--;
                injected.warn(e);
            }
        };

        void run(Async.Runnable runnable) {
            mates.add(Async.run(listener, runnable));
            count++;
        }
    }

    public static void check(TrackList tracks, Iterable<Worker.Checker> checkers, Exception.Listener listener)
    {
        Runner runner = new Runner(listener);
        for (Worker.Checker checker : checkers)
            runner.run(() -> checker.check(tracks));
    }

	public static void process(Iterable<Track> tracks, Iterable<Worker.Processor> processors, Exception.Listener listener)
    {
        Runner runner = new Runner(listener);
        for (Processor processor : processors)
            runner.run (() -> {
                Exception.Multi me = new Exception.Multi();
                try {
                    processor.prepare();
                } catch (Exception e) {
                    if (e.isFatal())
                        throw e;
                    else
                        me.add(e);
                }
                for (Track track : tracks)
                    try {
                        processor.process(track);
                    } catch (Exception e) {
                        me.add(e);
                        if (e.isFatal())
                            throw me;
                    }
                me.throwIfNotEmpty();
            });
	}
}
