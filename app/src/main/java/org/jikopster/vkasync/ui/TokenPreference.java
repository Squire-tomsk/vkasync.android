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

import org.jikopster.vkasync.R;

import android.content.Context;
import android.os.Build;
import android.preference.Preference;
import android.util.AttributeSet;

import com.vk.sdk.VKSdk;

public class TokenPreference extends Preference {

	public TokenPreference(Context context, AttributeSet attrs)
	{
		super(context, attrs);
		refresh();
	}

	public void refresh()
	{
		boolean in = VKSdk.isLoggedIn();
		
		setTitle(in
					? R.string.token_title_out
					: R.string.token_title_in );
		
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
			setIcon(in
					? R.drawable.ic_logout
					: R.drawable.ic_login );
	}

}
