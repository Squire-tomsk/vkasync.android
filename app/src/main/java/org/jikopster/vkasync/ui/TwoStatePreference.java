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
import android.preference.CheckBoxPreference;
import android.util.AttributeSet;
import android.view.View;
import android.widget.CompoundButton;

public class TwoStatePreference extends CheckBoxPreference {
	
    private class Listener implements CompoundButton.OnCheckedChangeListener {
    	boolean mDeaf;
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        	if (mDeaf) return;
            if (!callChangeListener(isChecked)) {
                buttonView.setChecked(!isChecked);
                return;
            }
            setChecked(isChecked);
        }
    }
    
    private final Listener mListener = new Listener();

	public TwoStatePreference(Context context, AttributeSet attrs) {
		super(context, attrs);
		setWidgetLayoutResource(R.layout.two_state);
	}
	
	@Override
	protected void onBindView(View view) {
		mListener.mDeaf = true;
		super.onBindView(view);
		mListener.mDeaf = false;
		
		CompoundButton btn = (CompoundButton) view.findViewById(android.R.id.checkbox);
		btn.setOnCheckedChangeListener(mListener);
	}

}
