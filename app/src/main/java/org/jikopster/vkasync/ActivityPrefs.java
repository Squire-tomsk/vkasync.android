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

package org.jikopster.vkasync;

import java.io.File;

import org.jikopster.vkasync.action.Clear;
import org.jikopster.vkasync.action.Sync;
import org.jikopster.vkasync.action.VK;
import org.jikopster.vkasync.preference.Path;
import org.jikopster.vkasync.ui.ActionRunner;

import org.jikopster.vkasync.ui.ActionPreference;
import org.jikopster.vkasync.ui.ActionState;
import org.jikopster.vkasync.ui.TokenPreference;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.preference.PreferenceScreen;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.StyleSpan;


public class ActivityPrefs extends PreferenceActivity
{
    @SuppressWarnings("deprecation")
    private Preference preference(int key) {
        return findPreference(getString(key));
    }

    private VK mVK = new VK(this);
	
	private static final ActionState
			sSyncState  = new ActionState(),
			sCleanState = new ActionState();
	
	@TargetApi(Build.VERSION_CODES.HONEYCOMB) @SuppressWarnings("deprecation")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.preferences);
        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);

        TokenPreference token = (TokenPreference) preference(R.string.key_token);
        mVK.setOnRefreshListener(token::refresh);
        token.setOnPreferenceClickListener(preference -> {
            mVK.toggle();
            return true;
        });

        sSyncState
                .setPreference((ActionPreference) preference(R.string.key_action_sync))
                .setOnPreferenceClickListener(preference -> {
                    mVK.login(this::sync);
                    return true;
                });

        sCleanState
                .setPreference((ActionPreference) preference(R.string.key_action_clean))
                .setOnPreferenceClickListener(preference -> {
                    File dir = new File(Path.getCurrent(this, Path.LOCAL));
                    showDialog(dir.isDirectory() ? DIALOG_CLEAN : DIALOG_CLEAR);
                    return true;
                });

        Preference eula = preference(R.string.eula);
        boolean isEulaAccepted = PreferenceManager
                .getDefaultSharedPreferences(this).getBoolean(eula.getKey(), false);
        if (isEulaAccepted) return;

        PreferenceScreen root = (PreferenceScreen) preference(R.string.key_root);
        setPreferenceScreen((PreferenceScreen) preference(R.string.key_eula));
        eula.setOnPreferenceChangeListener(
                (preference, newValue) -> {
                    if (newValue.equals(true)) {
                        eula.setOnPreferenceChangeListener(null);
                        setPreferenceScreen(root);
                    }
                    return true;
                });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if (mVK.onActivityResult(requestCode, resultCode, data)) return;
        super.onActivityResult(requestCode, resultCode, data);
    }


    private static final int
            DIALOG_CLEAN = 1,
            DIALOG_CLEAR = 2;
	
	@Override
	protected Dialog onCreateDialog(int id) {
		switch (id) {
            case DIALOG_CLEAN:
                return new
                        AlertDialog.Builder(this)
                        .setPositiveButton(android.R.string.ok, (dialog, which) -> clear())
                        .setNegativeButton(android.R.string.cancel, null)
                        .setMessage("")
                        .create();
            case DIALOG_CLEAR:
                return new
                        AlertDialog.Builder(this)
                        .setPositiveButton(android.R.string.ok, null)
                        .setMessage("")
                        .create();
        }
		return null;
	}

	@Override
	protected void onPrepareDialog(int id, Dialog dialog) {
		String message;
		
		switch (id) {
		case DIALOG_CLEAN:
			message = getString(R.string.clear_dialog_message);
			break;
		case DIALOG_CLEAR:
			message = getString(R.string.clean_dialog_message);
			break;
		default:
			return;
		}
		int i = message.indexOf("_");
		
		SpannableStringBuilder s = new SpannableStringBuilder(new File(Path.getCurrent(this, Path.LOCAL)).getName());
		s.setSpan(new StyleSpan(Typeface.BOLD), 0, s.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
		s.insert(0, message, 0, i);
		s.append(message.substring(i + 1));
		
		((AlertDialog)dialog).setMessage(s);
	}

    private static ActionRunner sSyncAction = new ActionRunner(sSyncState, Sync::new);
    private void sync() { sSyncAction.run(this); }

    private static ActionRunner sClearAction = new ActionRunner(sCleanState, Clear::new);
    private void clear() { sClearAction.run(this); }
}


