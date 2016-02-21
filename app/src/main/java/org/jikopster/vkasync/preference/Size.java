/*
 * Copyright (c) 2016 Jikopster Orglobster.
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

public class Size
{
    public static int get(Context context) {
        String size = PreferenceManager
                .getDefaultSharedPreferences(context)
                .getString(context.getString(R.string.folder_local_size), "0");
        return Integer.parseInt(size);
    }
}
