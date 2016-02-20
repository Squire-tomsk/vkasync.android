package org.jikopster.vkasync.action;

import android.app.Activity;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.vk.sdk.VKAccessToken;
import com.vk.sdk.VKCallback;
import com.vk.sdk.VKSdk;
import com.vk.sdk.api.VKError;

import org.jikopster.vkasync.misc.Lambda.Action;

public class VK
{
    public VK(Activity activity) {
        mActivity = activity;
    }

    private Activity mActivity;

    private Action mLoginListener;

    private Action mRefreshListener;
    public void setOnRefreshListener(@Nullable Action listener) {
        mRefreshListener = listener;
    }

    public void refresh() {
        if (mRefreshListener != null)
            mRefreshListener.invoke();

        if (VKSdk.isLoggedIn() && mLoginListener != null)
        {
            mLoginListener.invoke();
            mLoginListener = null;
        }
    }

    public void login(@NonNull Action listener) {
        if (VKSdk.isLoggedIn()) {
            listener.invoke();
            return;
        }
        mLoginListener = listener;
        login();
    }

    public void login() { VKSdk.login(mActivity, "wall", "audio"); }

    public void logout() { VKSdk.logout(); refresh(); }

    public void toggle() {
        if (VKSdk.isLoggedIn())
            logout();
        else
            login();
    }

    public boolean onActivityResult(int requestCode, int resultCode, Intent data) {
        return VKSdk.onActivityResult(requestCode, resultCode, data, new VKCallback<VKAccessToken>() {
            @Override
            public void onResult(VKAccessToken result) { refresh(); }
            @Override
            public void onError(VKError error)         { refresh(); }
        });
    }
}
