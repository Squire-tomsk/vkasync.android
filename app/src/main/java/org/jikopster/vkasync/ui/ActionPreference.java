package org.jikopster.vkasync.ui;

import org.jikopster.vkasync.R;

import android.content.Context;
import android.preference.Preference;
import android.util.AttributeSet;
import android.view.View;

public class ActionPreference extends Preference {
	
	private boolean mProgress;
	public void setProgress(boolean value)
	{
		mProgress = value;
		setEnabled(!value);
	}

	public ActionPreference(Context context, AttributeSet attrs) {
		super(context, attrs);
		setWidgetLayoutResource(R.layout.progressbar);
	}

	@Override
	protected void onBindView(View view) {
		super.onBindView(view);
		view.findViewById(R.id.progressbar).setVisibility(mProgress ? View.VISIBLE : View.GONE);
	}

}
