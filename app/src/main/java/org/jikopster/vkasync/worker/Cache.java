package org.jikopster.vkasync.worker;

import android.content.Context;

import org.jikopster.vkasync.core.*;
import org.jikopster.vkasync.core.Master.TrackList;

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
            mMediaHelper = new Media.ContentHelper(context, localDir.getAbsolutePath());
            mCacheDir = cacheDir;
            mLocalDir = localDir;
            mClean = clean;
        }

        private File mCacheDir;
        private File mLocalDir;

        private final Media.ContentHelper mMediaHelper;

        private boolean mClean;

        @Override
        public void prepare() throws CantCreateLocalDirException {
            if (!mLocalDir.mkdirs())
            if (!mLocalDir.isDirectory())
                throw new CantCreateLocalDirException();
        }

        @Override
        public void process(Track track) throws
                Exception {
            if (!track.isset(FLAG)) return;
            if ( track.isset(Local.FLAG)) return;
            if (!track.isset(Cloud.FLAG)) return;

            File fIn  = new File(mCacheDir, track.getID());
            File fOut = new File(mLocalDir, track.filename());

            MultiException me = new MultiException();

            try (FileInputStream  is = new FileInputStream (fIn);
                 FileOutputStream os = new FileOutputStream(fOut)) {
                is
                .getChannel()
                .transferTo(0, is.getChannel().size(), os.getChannel());
                track.file = fOut;
                mMediaHelper.upsert(track);
            } catch (IOException e) {
                throw new FileNotCopiedException(e);
            } catch (Media.ContentHelper.UpsertException e) {
                me.add(e);
            }

            if (mClean && !fIn.delete())
                me.add(new FileNotDeletedException());

            me.throwIfNotEmpty();
        }
    }
}
