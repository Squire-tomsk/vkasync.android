package org.jikopster.vkasync.preference;

import android.content.Context;
import android.preference.PreferenceManager;

import org.jikopster.vkasync.R;

public class Size
{
    public static int get(Context context) {
        String size = PreferenceManager
                .getDefaultSharedPreferences(context)
                .getString(context.getString(R.string.folder_local_size), "0");
        return Integer.parseInt(size);
    }
}
