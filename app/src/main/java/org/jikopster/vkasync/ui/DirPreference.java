package org.jikopster.vkasync.ui;

import android.content.Context;
import android.content.res.TypedArray;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.util.AttributeSet;
import android.view.View;

import org.jikopster.vkasync.preference.Path;

public class DirPreference extends EditTextPreference implements OnPreferenceChangeListener
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
