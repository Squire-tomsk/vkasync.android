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
