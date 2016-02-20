package org.jikopster.vkasync.preference;

import android.content.Context;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import org.jikopster.vkasync.R;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import static org.jikopster.vkasync.misc.Lambda.*;

public class Path
{
    public static final int LOCAL = R.string.folder_local;
    public static final int CACHE = R.string.folder_cache;

    private static final HashMap<Integer,Function1<Context,File>> defaultsByInteger = new HashMap<>();
    private static final HashMap<String, Function1<Context,File>> defaultsByString;

    static {
        defaultsByInteger.put(LOCAL, context -> new File(
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC),
                context.getString(R.string.album)));
        defaultsByInteger.put(CACHE, context -> new File(
                Environment.getExternalStorageDirectory(), ".vkontakte/cache/audio"));

        defaultsByString = new HashMap<>(defaultsByInteger.size());
    }

    @Nullable
    public static File getDefault(Context context, String key) {
        Function1<Context,File> f = defaultsByString.get(key);
        if (null == f)
            for (Map.Entry<Integer,Function1<Context,File>> e : defaultsByInteger.entrySet()) {
                String k = context.getString(e.getKey());
                Function1 v = e.getValue();
                defaultsByString.put(k, v);
                if (key.equals(k)) f = v;
            }
        return f == null ? null : f.invoke(context);
    }

    @Nullable
    public static String getCurrent(Context context, String key) {
        String current = PreferenceManager.getDefaultSharedPreferences(context).getString(key, null);
        if (current == null) {
            File def = getDefault(context, key);
            current = def == null ? null : def.getAbsolutePath();
        }
        return current;
    }

    public static String getCurrent(Context context, int key) {
        String current = PreferenceManager
                .getDefaultSharedPreferences(context).getString(context.getString(key), null);
        return current == null
                ? defaultsByInteger.get(key).invoke(context).getAbsolutePath()
                : current;
    }
}
