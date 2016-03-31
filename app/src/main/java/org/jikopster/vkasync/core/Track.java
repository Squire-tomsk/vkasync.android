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

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.annotation.Nullable;

import org.jikopster.vkasync.misc.FieldAdapter;
import org.jikopster.vkasync.worker.Media;

import java.io.File;
import java.util.regex.Pattern;

import static org.jikopster.vkasync.misc.Lambda.*;

public final class Track
{
    private static class Field extends FieldAdapter<Track,String> {
        Field(Function1<Track,String> getter, Function2<Track,String,String> setter) {
            super(getter, setter);
        }
    }

    private static final Field fTitle  =
            new Field(track -> track.mTitle , (track, value) -> track.mTitle  = value);
    private static final Field fArtist =
            new Field(track -> track.mArtist, (track, value) -> track.mArtist = value);

    private Track() { }
    public  Track(String id) { this.mID = id; }

    private String mID;
    public String getID() { return mID; }

    private String mArtist;
    public String getArtist() { return mArtist; }

    private String mTitle;
    public String getTitle() { return mTitle; }

    public String url;

    public File file;

    private int mFlags;
    private final Object mFlagsLock = new Object();

	public Track set(int flag) {
        synchronized (mFlagsLock) {
            mFlags |= flag;
        }
		return this;
	}

    public boolean isset(int flag) {
        return (mFlags & flag) != 0;
    }

    private boolean set(FieldAdapter<Track,String> field, String value, boolean over) {
        synchronized (field) {
            if (null == field.get(this))
                return field.set(this, value) != null;
            if (field.get(this).equals(value))
                return false;
            if (over)
                field.set(this, value);
        }
        return true;
    }

    public boolean setTitle(String title, boolean over) {
        return set(fTitle, title, over);
    }

    public boolean setArtist(String artist, boolean over) {
        return set(fArtist, artist, over);
    }

    static final Pattern PATTERN_ID = Pattern.compile("^-?[0-9]+_[0-9]+$");

    public static String idFromFilename(String s) {
        try {
            s = s.substring(0, s.lastIndexOf(".mp3"));
            s = s.substring(s.lastIndexOf('.') + 1);
            return PATTERN_ID.matcher(s).matches() ? s : null;
        } catch(IndexOutOfBoundsException e) {
            return null;
        }
    }

    public static String filenameById(String id) {
        return id + ".mp3";
    }

    static final Pattern PATTERN_FILENAME = Pattern.compile("[?:\"*/|\\<>]");

    static final int MAX_FILENAME = 127;

    public String filename(int pathLength) {
        String Artist = PATTERN_FILENAME.matcher(mArtist).replaceAll("");
        String Title  = PATTERN_FILENAME.matcher(mTitle ).replaceAll("");
        String dash = mArtist.isEmpty() || mTitle.isEmpty() ? "" : " - ";
        String dot  = mArtist.isEmpty() && mTitle.isEmpty() ? "" :  "." ;

        String name = Artist + dash + Title;
        String suffix = filenameById(mID);
        int restLength = MAX_FILENAME - pathLength - suffix.length() - dot.length();
        if (restLength < 1)
            return suffix;
        if (restLength < name.length())
            name = name.substring(0, restLength);
        return name + dot + suffix;
    }

    public String filename(String path) {
        return new File(path, filename(path.length())).getPath();
    }

    public ContentValues values() {
        ContentValues v = new ContentValues(file == null ? 4 : 5);
        if (file != null)
            v.put(MediaStore.Audio.Media.DATA, file.getPath());
        v.put(MediaStore.Audio.Media.ALBUM, "");
        v.put(MediaStore.Audio.Media.ARTIST, mArtist);
        v.put(MediaStore.Audio.Media.TITLE, mTitle);
        v.put(MediaStore.Audio.Media.IS_MUSIC, true);
        return v;
    }

    private static final Field[] fields = {
            new Field(track -> track.mID, (track, id) -> track.mID = id),
            fArtist,
            fTitle,
    };

    public void serialize(Context context, long id)
    {
        KeyBuilder kb = new KeyBuilder(id);
        SharedPreferences.Editor editor =
                PreferenceManager.getDefaultSharedPreferences(context).edit();
        for (int i = fields.length; i > 0; )
            editor.putString(kb.key(i), fields[--i].get(this));
        editor.apply();
    }

    @Nullable
    public static Track unserialize(Context context, long id)
    {
        KeyBuilder kb = new KeyBuilder(id);
        SharedPreferences p = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor e = p.edit();
        Track track = new Track();
        for (int i = fields.length; i > 0; ) {
            String key = kb.key(i);
            fields[--i].set(track, p.getString(key, null));
            e.remove(key);
        }
        e.apply();
        return track.mID == null ? null : track;
    }

    private static class KeyBuilder
    {
        KeyBuilder(long id) {
            prefix = new StringBuilder(String.valueOf(id)).append(":");
        }

        final StringBuilder prefix;

        String key(int key) {
            return prefix.append(String.valueOf(key)).toString();
        }
    }
	
}
