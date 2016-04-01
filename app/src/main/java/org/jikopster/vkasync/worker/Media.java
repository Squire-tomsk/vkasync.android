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

package org.jikopster.vkasync.worker;

import android.content.Context;
import android.database.Cursor;

import org.jikopster.vkasync.core.*;
import org.jikopster.vkasync.core.Master.TrackList;


public class Media extends Worker
{
    public static final int
            FLAG   = nextFlag(),
            UPDATE = nextFlag();


    public static class Checker extends ContentHelper implements Worker.Checker
    {
        public Checker(Context context, String localPath) { super(context, localPath); }

        @Override
        public void check(TrackList tracks) throws NullCursorException {
            try (Cursor cursor = query()) {
                while (cursor.moveToNext()) {
                    String id = Track.idFromFilename(cursor.getString(COLUMN_DATA));
                    if (null == id) continue;
                    Track track = tracks.get(id).set(FLAG);

                    if (track.setArtist(cursor.getString(COLUMN_ARTIST), false))
                        track.set(UPDATE);
                    if (track.setTitle(cursor.getString(COLUMN_TITLE), false))
                        track.set(UPDATE);
                }
            }
        }
    }

	public static class Processor extends ContentHelper implements Worker.Processor
    {
        public Processor(Context context, String localPath) {
            super(context, localPath);
        }

        @Override
        public void process(Track track) throws UpdateException, InsertException {
            if (!track.isset(Local.FLAG)) return;
            if ( track.isset(Cloud.FLAG) && track.isset(UPDATE)) {
                update(track);
                return;
            }
            if (!track.isset(FLAG))
                insert(track);
        }
    }
}
