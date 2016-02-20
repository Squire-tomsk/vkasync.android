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
