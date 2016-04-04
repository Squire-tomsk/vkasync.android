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

import android.app.DownloadManager;
import android.app.DownloadManager.Request;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;

import com.vk.sdk.api.VKError;
import com.vk.sdk.api.VKParameters;
import com.vk.sdk.api.VKRequest;
import com.vk.sdk.api.VKResponse;

import org.jikopster.vkasync.core.Exception;
import org.jikopster.vkasync.core.Master.TrackList;
import org.jikopster.vkasync.core.Track;
import org.jikopster.vkasync.core.Worker;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;

public class Cloud extends Worker
{
    public static final int FLAG = nextFlag();

    public static class Checker implements Worker.Checker
    {
        public class JsonException extends Exception.Fatal {
            JsonException(JSONException e) {
                super(e);
            }
        }

        public class VkErrorException extends Exception.Fatal {
            VkErrorException(VKError error) { super(error.toString()); }
        }

        private class Listener extends VKRequest.VKRequestListener
        {
            Listener(TrackList tracks) { this.tracks = tracks; }

            final TrackList tracks;

            @Override
            public void onComplete(VKResponse response) {
                try {
                    JSONObject json =
                            new JSONObject(response.responseString).getJSONObject("response");
                    JSONArray items =
                            json.getJSONArray("items");

                    for (int i = items.length(); 0 < i--; ) {
                        json = items.getJSONObject(i);
                        String id = json.getString("owner_id") + "_" + json.getString("id");

                        Track track = tracks.get(id).set(FLAG);
                        if (json.has("url"))
                            track.url = json.getString("url").replaceFirst("https://", "http://");

                        if (track.setArtist(json.getString("artist"), true))
                            track.set(Media.UPDATE);
                        if (track.setTitle(json.getString("title"), true))
                            track.set(Media.UPDATE);
                    }
                } catch (JSONException e) {
                    mException = new JsonException(e);
                }
            }

            @Override
            public void onError(VKError error) {
                mException = new VkErrorException(error);
            }
        }

        public Checker(int limit) { this.limit = limit; }

        private final int limit;
        private Exception mException;

        @Override
        public void check(TrackList tracks) throws Exception {
            new VKRequest("audio.get",
                    VKParameters.from("need_user", "0", "v", "5.25", "count", Integer.toString(limit)))
                .executeSyncWithListener(new Listener(tracks));

            if (mException != null)
                throw mException;
        }
    }

    static final String tempSubDir = ".temp";
    static final String tempSupDir = "..";


    public static class Processor implements Worker.Processor
    {
        public class CantCreateTempDirException extends Exception.Fatal { }
        public class NomediaCreationIOException extends Exception { }

        public Processor(Context context, String localPath) {
            mContext = context;
            mTempDir = new File(localPath, tempSubDir);
        }

        private Context mContext;
        private DownloadManager mDownMan;
        private File mTempDir;

        @Override
        public void prepare() throws CantCreateTempDirException, NomediaCreationIOException {
            mDownMan = (DownloadManager) mContext.getSystemService(Context.DOWNLOAD_SERVICE);

            if (!mTempDir.mkdirs())
            if (!mTempDir.isDirectory())
                throw new CantCreateTempDirException();

            try {
                new File(mTempDir, ".nomedia").createNewFile();
            } catch (IOException e) {
                throw new NomediaCreationIOException();
            }
        }

        /**
         * If a {@link Track} has either {@link Local#FLAG} or {Cache#FLAG} set,
         * it will be skipped. Because of that, don't run {@link Cache.Checker}
         * until you're gonna run {@link Cache.Processor} too. Otherwise,
         * a {@link Track} won't be neither downloaded nor copied from the cache.
         */
        public static final int SKIP = Local.FLAG | Cache.FLAG;

        @Override
        public void process(Track track) {
            if (!track.isset(FLAG)) return;
            if ( track.isset(SKIP)) return;
            if ( track.url == null) return;

            long id = mDownMan.enqueue(
                    new Request(Uri.parse(track.url))
                            .setMimeType("application/octet-stream")
                            .setVisibleInDownloadsUi(false)
                            .setTitle(String.format("%s - %s", track.getArtist(), track.getTitle()))
                            .setDestinationUri(Uri.fromFile(new File(mTempDir, track.getID())))
            );

            track.serialize(mContext, id);
        }
    }

    public static class Receiver
    {
        static DownloadManager getDownloadManager(Context context) {
            return (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
        }

        public static class DownloadComplete extends BroadcastReceiver
        {
            public class CantRenameTempFileException extends Exception { }
            public class CantDeleteTempFileException extends Exception { }

            @Override
            public void onReceive(Context context, Intent intent) {
                long id = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, 0);

                DownloadManager dm = getDownloadManager(context);
                try (Cursor cursor = dm.query(new DownloadManager.Query().setFilterById(id))) {
                    if (!cursor.moveToFirst()) return;

                    Track track = Track.deserialize(context, id);
                    if (track == null) return;

                    int status = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS));
                    if (status == DownloadManager.STATUS_FAILED) {
                        dm.remove(id);
                        return;
                    }

                    String tempUri = cursor.getString(
                            cursor.getColumnIndex(DownloadManager.COLUMN_LOCAL_URI));
                    File src = new File(Uri.parse(tempUri).getPath());
                    String path = new File(src.getParent(), tempSupDir).getCanonicalPath();
                    File dst = new File(track.filename(path));
                    if (!src.renameTo(dst)) {
                        throw src.delete()
                                ? new CantRenameTempFileException()
                                : new Exception.Multi()
                                        .add(new CantRenameTempFileException())
                                        .add(new CantDeleteTempFileException());
                    }
                    track.file = dst;
                    new ContentHelper(context, path).upsert(track);
                } catch (IOException|Exception e) {
                    Exception.log(e);
                }
            }
        }

        public static class DownloadNotificationClicked extends BroadcastReceiver
        {
            static final int filter
                    = DownloadManager.STATUS_PAUSED
                    | DownloadManager.STATUS_PENDING
                    | DownloadManager.STATUS_RUNNING;

            @Override
            public void onReceive(Context context, Intent intent) {
                DownloadManager dm = getDownloadManager(context);
                try (Cursor cursor = dm.query(new DownloadManager.Query().setFilterByStatus(filter))) {
                    if (!cursor.moveToFirst()) return;

                    int i = cursor.getColumnIndex(DownloadManager.COLUMN_ID);
                    do {
                        long id = cursor.getLong(i);
                        if (null == Track.deserialize(context, id))
                            return; // to remove our downloads only
                        dm.remove(id);
                    } while (cursor.moveToNext());
                }
            }
        }
    }
}
