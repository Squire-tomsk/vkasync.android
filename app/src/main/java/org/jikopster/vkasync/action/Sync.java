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

package org.jikopster.vkasync.action;

import android.content.Context;
import android.support.annotation.NonNull;

import org.jikopster.vkasync.core.Exception;
import org.jikopster.vkasync.core.Worker.*;
import org.jikopster.vkasync.misc.Fucktory;
import org.jikopster.vkasync.preference.Bool;

public class Sync extends Action
{
    public static String getMessage(Context context, @NonNull Exception e) {
        int resId = getMessageResId(e);
        return resId == 0
                ? null
                : context.getString(resId);
    }

    public static int getMessageResId(@NonNull Exception ex) {
        try {
            throw ex;
        } catch (Exception e) {
            return 0;
        }
    }

    public Sync(Context context) { super(context); }

    @Override
    protected Iterable<Checker> getCheckers() {
        return
                Fucktory.<Checker>fuckEmAll(
                        new Fucktory<>(
                            this::getCloudChecker),
                        new Fucktory<>(
                            this::getLocalChecker),
                        new Fucktory<>(
                            Bool.get(context, Bool.CACHE),
                            this::getCacheChecker),
                        new Fucktory<>(
                            this::getMediaChecker)
                );
    }

    @Override
    protected Iterable<Processor> getProcessors() {
        return
                Fucktory.<Processor>fuckEmAll(
                        new Fucktory<>(
                                Bool.get(context, Bool.CLOUD),
                                this::getCloudProcessor),
                        new Fucktory<>(
                                Bool.get(context, Bool.LOCAL),
                                this::getLocalProcessor),
                        new Fucktory<>(
                                Bool.get(context, Bool.CACHE),
                                this::getCacheProcessor),
                        new Fucktory<>(
                                this::getMediaProcessor)
                );
    }
}
