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

import android.os.AsyncTask;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;


public class Async
{
    public interface Runnable { void run() throws Exception; }

    public static AsyncTask run(@NonNull Exception.Listener listener, @NonNull Runnable runnable) {
        AsyncTask<Void,?,?> task = new AsyncTask<Void, Void, Exception>()
        {
            @Override
            protected Exception doInBackground(Void... params) {
                try {
                    runnable.run();
                    return null;
                } catch (Exception e) {
                    return e;
                }
            }

            @Override
            protected void onPostExecute(@Nullable Exception e) {
                Exception.notify(e, listener);
            }
        };

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB)
            task.execute();
        else
            task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

        return task;
    }

}
