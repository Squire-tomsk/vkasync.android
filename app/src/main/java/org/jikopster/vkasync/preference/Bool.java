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
