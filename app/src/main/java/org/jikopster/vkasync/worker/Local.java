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

import java.io.File;

import org.jikopster.vkasync.core.*;
import org.jikopster.vkasync.core.Master.TrackList;

public class Local extends Worker
{
	public static final int FLAG = nextFlag();

    public static class Checker implements Worker.Checker
    {
        public Checker(File dir) {
            mDir = dir;
        }

        private File mDir;

        @Override
        public void check(TrackList tracks) {
            File[] files = mDir.listFiles();
            if (files != null)
                for (File file : files) {
                    if (!file.isFile()) continue;
                    String id = Track.idFromFilename(file.getName());
                    if (id == null) continue;
                    tracks.get(id).set(FLAG).file = file;
                }
        }
    }

    public static class Processor implements Worker.Processor
    {
        public class LocalFileNotFoundException extends Exception { }
        public class CantDeleteLocalFileException extends Exception implements FatalException { }

        public Processor(Context context, String localPath) {
            mMediaHelper = new Media.ContentHelper(context, localPath);
        }

        private final Media.ContentHelper mMediaHelper;

        public static final int SKIP = Cloud.FLAG;

        @Override
        public void process(Track track)
                                        throws
                                        LocalFileNotFoundException,
                                        CantDeleteLocalFileException,
                Media.ContentHelper.DeleteException
        {
            if (!track.isset(FLAG)) return;
            if ( track.isset(SKIP)) return;

            if (!track.file.exists())
                throw new LocalFileNotFoundException();
            if (!track.file.delete())
                throw new CantDeleteLocalFileException();

            mMediaHelper.delete(track);
        }
    }


}
