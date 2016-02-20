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
