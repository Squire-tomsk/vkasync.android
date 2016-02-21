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

package org.jikopster.vkasync.ui;

import android.content.Context;
import android.os.Build;
import android.support.annotation.Nullable;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.widget.Toast;

import org.jikopster.vkasync.R;

public class SingleToast
{
    public enum State {
        DONE(R.color.done, R.string.done),
        WARN(R.color.warn, R.string.fail),
        FAIL(R.color.fail, R.string.fail);

        State(int color, int title) {
            this.color = color;
            this.title = title;
        }

        public final int color;
        public final int title;
    }

    @SuppressWarnings("deprecation")
    public static void show(Context context, State state, @Nullable String message) {
        String title = context.getString(state.title);
        int color;
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M)
            color = context.getResources().getColor(state.color);
        else
            color = context.getResources().getColor(state.color, context.getTheme());

        SpannableStringBuilder text = new SpannableStringBuilder(title);
        text.setSpan(new ForegroundColorSpan(color), 0, text.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        text.insert(0, String.format("%s: ", context.getString(R.string.app_name)));

        if (!TextUtils.isEmpty(message))
            text.append(String.format("%n%s", message));

        int duration = message == null ? Toast.LENGTH_SHORT : Toast.LENGTH_LONG;

        if (sToast != null)
            sToast.cancel();

        sToast = Toast.makeText(context, text, duration);
        sToast.show();
    }

    private static Toast sToast;
}
