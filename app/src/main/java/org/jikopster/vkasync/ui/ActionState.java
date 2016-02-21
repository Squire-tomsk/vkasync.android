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

import java.lang.ref.WeakReference;


public class ActionState {
	
	public class State
    {
        State(boolean progress) {
            mProgress = progress;
        }

		private boolean mProgress;

		void apply(ActionPreference preference) {
            preference.setProgress(mProgress);
        }
		
		public void apply() {
			setState(this);
		}
	}
	
	public final State
		ENABLED  = new State(false),
		PROGRESS = new State(true);
	
	private State mState = ENABLED;
	public void setState(State state) {
		mState = state;
		ActionPreference preference = mPreferenceReference.get();
		if (preference != null)
			state.apply(preference);
	}
	
	private WeakReference<ActionPreference> mPreferenceReference;
	public ActionPreference setPreference(ActionPreference preference) {
		mState.apply(preference);
		mPreferenceReference = new WeakReference<>(preference);
		return preference;
	}
}
