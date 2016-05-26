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
import android.content.res.TypedArray;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.util.AttributeSet;
import android.view.View;

import com.ls.directoryselector.DirectoryPreference;

import org.jikopster.vkasync.preference.Path;

public class DirPreference extends DirectoryPreference implements OnPreferenceChangeListener
{
	public DirPreference(Context context, AttributeSet attrs) {
		super(context, attrs);
        setOnPreferenceChangeListener(this);
		setWidgetLayoutResource(0);
	}

    @Override
    protected void onBindView(View view) {
        super.onBindView(view);
        setSummary(Path.getCurrent(getContext(), getKey()));
    }

    @Override
    protected Object onGetDefaultValue(TypedArray a, int index) {
        return Path.getCurrent(getContext(), a.getString(index));
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        preference.setSummary((String) newValue);
        return true;
    }
}
