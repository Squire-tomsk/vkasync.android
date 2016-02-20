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
