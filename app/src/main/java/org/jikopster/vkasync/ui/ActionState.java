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
