package org.jikopster.vkasync.preference;

import android.content.Context;
import android.preference.PreferenceManager;

import org.jikopster.vkasync.R;

public class Bool
{
    public static final int
            CLOUD = R.string.cloud_switch,
            LOCAL = R.string.local_switch,
            CACHE = R.string.cache_switch,
            CLEAN = R.string.clean_switch;

    public static boolean get(Context context, int key) {
        return get(context, key, false);
    }

    public static boolean get(Context context, int key, boolean def) {
        return PreferenceManager
                .getDefaultSharedPreferences(context)
                .getBoolean(context.getString(key), def);
    }
}
