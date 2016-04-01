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

import android.content.ContentResolver;
import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore;

import org.jikopster.vkasync.misc.Lambda;
import org.jikopster.vkasync.preference.Path;
import org.jikopster.vkasync.worker.ContentHelper;

import java.io.File;

public class Clear
{
    public static void clear(Context context, Lambda.Action1<Boolean> listener)
    {
        new AsyncTask<Void, Void, Boolean>() {
            private final String mPath = Path.getCurrent(context, Path.LOCAL);
            private final File mDir = new File(mPath);

            private Uri mUri = MediaStore.Audio.Media.getContentUriForPath(mPath);

            private ContentResolver mCoRe = context.getContentResolver();

            @Override
            protected void onPostExecute(Boolean result) {
                listener.invoke(result);
            }

            @Override
            protected Boolean doInBackground(Void... params) {
                boolean result = delete(mDir);
                mCoRe.delete(mUri, ContentHelper.whereByPath(mPath), null);
                return result;
            }

            private boolean delete(File file) {
                File[] files = file.listFiles();
                if (files != null)
                    for (File f : files) delete(f);
                return file.delete();
            }
        }.execute();
    }
}
