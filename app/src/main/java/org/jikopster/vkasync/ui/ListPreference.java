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

package org.jikopster.vkasync.ui;

import android.content.Context;
import android.preference.Preference;
import android.util.AttributeSet;
import android.view.View;


public class ListPreference extends android.preference.ListPreference implements Preference.OnPreferenceChangeListener
{
    public ListPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
        setOnPreferenceChangeListener(this);
    }

    @Override
    protected void onBindView(View view) {
        super.onBindView(view);
        setSummary(getEntry());
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        int i = findIndexOfValue(newValue.toString());
        if (i < 0) return false;
        setSummary(getEntries()[i]);
        return true;
    }
}
