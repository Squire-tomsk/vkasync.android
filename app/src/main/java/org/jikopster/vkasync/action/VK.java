/*
 * Copyright (c) 2016 Jikopster Orglobster.
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
