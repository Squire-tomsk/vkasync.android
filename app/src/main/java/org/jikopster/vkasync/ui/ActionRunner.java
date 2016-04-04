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
 */

package org.jikopster.vkasync.ui;


import android.content.Context;
import android.support.annotation.NonNull;

import org.jikopster.vkasync.R;
import org.jikopster.vkasync.action.Action;
import org.jikopster.vkasync.core.Exception;

public class ActionRunner
{
    public ActionRunner(ActionState state, Class<? extends Action> action) {
        this.state = state;
        this.action = action;
    }

    private final Class<? extends Action> action;

    private final ActionState state;

    public void run(Context context) {
        Action instance;
        try {
             instance = action.getConstructor(Context.class).newInstance(context);
        } catch (java.lang.Exception e) {
            throw new RuntimeException(e);
        }

        state.PROGRESS.apply();

        instance.run(new Exception.Listener() {
            int count;

            @Override
            public void done() {
                state.ENABLED.apply();
                if (count == 0)
                    SingleToast.show(context, SingleToast.State.DONE, null);
                else
                    SingleToast.show(context, SingleToast.State.WARN,
                            context.getString(R.string.error_count, count));
            }

            @Override
            public void fail(@NonNull Exception e) {
                state.ENABLED.apply();
                SingleToast.show(context, SingleToast.State.FAIL,
                        context.getString(R.string.error_count, ++count));
            }

            @Override
            public void warn(@NonNull Exception e) {
                count++;
            }
        });
    }
}
