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

package org.jikopster.vkasync.worker;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.BaseColumns;
import android.support.annotation.NonNull;

import org.jikopster.vkasync.core.Exception;
import org.jikopster.vkasync.core.Track;

import static android.provider.MediaStore.Audio.Media.*;


public class ContentHelper
{
    public class NullCursorException extends Exception.Fatal {}

    public class InsertException extends Exception { }

    public class DeleteException extends Exception { }

    public class UpdateException extends Exception { }

    public class UpsertException extends Exception {
        public UpsertException(Throwable throwable) {
            super(throwable);
        }
    }

    public static final int
            COLUMN_ARTIST = 0,
            COLUMN_TITLE  = 1,
            COLUMN_DATA   = 2;

    public static String whereByPath(String path) {
        return whereById(path, "");
    }

    public static String whereById(String path, String id) {
        return DATA + " LIKE '" + path.replace("'", "\\'") + "%" + Track.filenameById(id) + "'";
    }

    public ContentHelper(Context context, String localPath) {
        mCoRe = context.getContentResolver();
        mLocalUri = getContentUriForPath(localPath);
        mLocalPath = localPath;
    }

    private final ContentResolver mCoRe;
    private final Uri mLocalUri;
    private final String mLocalPath;

    private static final String[] projectionQuery  = { ARTIST, TITLE, DATA };
    private static final String[] projectionUpsert = { BaseColumns._ID };

    private String where() {
        return where("");
    }

    private String where(String id) {
        return whereById(mLocalPath, id);
    }

    public void delete(@NonNull Track track) throws DeleteException {
        if (0 == mCoRe.delete(mLocalUri, where(track.getID()), null))
            throw new DeleteException();
    }

    @NonNull
    public Cursor query(String[] projection, String id) throws NullCursorException {
        Cursor cursor = mCoRe.query(mLocalUri, projection, where(id), null, null);
        if (cursor == null)
            throw new NullCursorException();
        return cursor;
    }

    @NonNull
    public Cursor query() throws NullCursorException {
        return query(projectionQuery, "");
    }

    public void insert(@NonNull Track track) throws InsertException {
        if (null == mCoRe.insert(mLocalUri, track.values()))
            throw new InsertException();
    }

    public void update(@NonNull Track track) throws UpdateException {
        if (0 == mCoRe.update(mLocalUri, track.values(), where(track.getID()), null))
            throw new UpdateException();
    }

    public void upsert(@NonNull Track track) throws UpsertException {
        try (Cursor cursor = query(projectionUpsert, track.getID())) {
            if (cursor.moveToFirst())
                update(track);
            else
                insert(track);
        } catch (NullCursorException | UpdateException | InsertException e) {
            throw new UpsertException(e);
        }
    }
}
