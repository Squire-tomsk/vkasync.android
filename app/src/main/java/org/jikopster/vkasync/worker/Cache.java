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

import org.jikopster.vkasync.core.Exception;
import org.jikopster.vkasync.core.Master.TrackList;
import org.jikopster.vkasync.core.Track;
import org.jikopster.vkasync.core.Worker;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class Cache extends Worker
{
	public static final int FLAG = nextFlag();

	public static class Checker implements Worker.Checker
    {
        public Checker(File cacheDir) {
            mDir = cacheDir;
        }

        private File mDir;

        @Override
        public void check(TrackList tracks) {
            File[] files = mDir.listFiles();
            if (files != null)
                for (File file : files)
                    tracks.get(file.getName()).set(FLAG);
        }
    }

    public static class Processor implements Worker.Processor
    {
        public class CantCreateLocalDirException extends Exception { }

        public class FileNotCopiedException extends Exception {
            public FileNotCopiedException(Throwable throwable) {
                super(throwable);
            }
        }

        public class FileNotDeletedException extends Exception { }


        public Processor(Context context, File cacheDir, File localDir, boolean clean) {
            mHelper = new ContentHelper(context, localDir.getPath());
            mCacheDir = cacheDir;
            mLocalDir = localDir;
            mClean = clean;
        }

        private File mCacheDir;
        private File mLocalDir;

        private final ContentHelper mHelper;

        private boolean mClean;

        @Override
        public void prepare() throws CantCreateLocalDirException {
            if (!mLocalDir.mkdirs())
            if (!mLocalDir.isDirectory())
                throw new CantCreateLocalDirException();
        }

        @Override
        public void process(Track track) throws Exception {
            if (!track.isset(FLAG)) return;
            if ( track.isset(Local.FLAG)) return;
            if (!track.isset(Cloud.FLAG)) return;

            File fIn  = new File(mCacheDir, track.getID());
            File fOut = new File(mLocalDir, track.filename(mLocalDir.getPath().length()));

            Exception.Multi me = new Exception.Multi();

            try (FileInputStream  is = new FileInputStream (fIn);
                 FileOutputStream os = new FileOutputStream(fOut)) {
                is
                .getChannel()
                .transferTo(0, is.getChannel().size(), os.getChannel());
                track.file = fOut;
                mHelper.upsert(track);
            } catch (IOException e) {
                throw new FileNotCopiedException(e);
            } catch (ContentHelper.UpsertException e) {
                me.add(e);
            }

            if (mClean && !fIn.delete())
                me.add(new FileNotDeletedException());

            me.throwIfNotEmpty();
        }
    }
}
