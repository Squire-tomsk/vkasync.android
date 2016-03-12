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
import android.os.Build;

import java.util.ArrayList;
import java.util.List;

import static org.jikopster.vkasync.core.Worker.*;

import org.jikopster.vkasync.misc.Lambda.Action1;

public class Master {

    public interface TrackList {
        Track get(String id);
    }

    public interface Listener extends Action1<Exception> { }

    private interface ASyncTask
    {
        void doInBackground() throws Exception;

        default void execute(List<AsyncTask> mates, Listener listener) {
            AsyncTask<Void,Void,Exception> task = new AsyncTask<Void,Void,Exception>() {
                @Override
                protected Exception doInBackground(Void... params) {
                    try {
                        ASyncTask.this.doInBackground();
                        return null;
                    } catch (Exception e) {
                        return e;
                    }
                }

                @Override
                protected void onPostExecute(Exception e) {
                    if (!mates.remove(this)) return;
                    if (e != null) {
                        if (e instanceof FatalException) {
                            for (int i = mates.size(); 0 < i--; )
                                mates.get(i).cancel(true);
                            mates.clear();
                            listener.invoke(e);
                            return;
                        }
                        listener.invoke(e);
                    }
                    if (mates.isEmpty())
                        listener.invoke(null);
                }
            };
            mates.add(task);
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB)
                task.execute();
            else
                task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        }
    }

    public static void check(TrackList tracks, Iterable<Worker.Checker> checkers, Listener listener)
    {
        ArrayList<AsyncTask> tasks = new ArrayList<>();
        for (Worker.Checker checker : checkers) {
            ASyncTask task = () -> checker.check(tracks);
            task.execute(tasks, listener);
        }
    }

	public static void process(Iterable<? extends Track> tracks, Iterable<Worker.Processor> processors, Listener listener)
    {
        for (Processor processor : processors) {
            try {
                processor.prepare();
            } catch (Exception e) {
                listener.invoke(e);
                if (e instanceof FatalException)
                    return;
            }
        }

        ArrayList<AsyncTask> tasks = new ArrayList<>();
        for (Processor processor : processors)
            ((ASyncTask) (() -> {
                for (Track track : tracks) {
                    MultiException me = new MultiException();
                    try {
                        processor.process(track);
                    } catch (Exception e) {
                        if (e instanceof FatalException)
                            throw e;
                        else
                            me.add(e);
                    }
                    me.throwIfNotEmpty();
                }
            })).execute(tasks, listener);
	}

}
