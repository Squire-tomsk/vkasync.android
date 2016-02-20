package org.jikopster.vkasync;

import com.vk.sdk.VKSdk;

public class Application extends android.app.Application
{

	@Override
	public void onCreate()
	{
		VKSdk.initialize(this);
		super.onCreate();
	}
	
}
