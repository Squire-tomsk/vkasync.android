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

package org.jikopster.vkasync.action;


import android.support.annotation.NonNull;

import org.jikopster.vkasync.core.Exception;
import org.jikopster.vkasync.misc.Lambda;

public class Listener extends Exception.Listener
    {
        Listener(@NonNull Exception.Listener listener, @NonNull Lambda.Action then) {
            this.listener = listener;
            this.then = then;
        }

        private final Exception.Listener listener;
        private final Lambda.Action then;

        @Override
        public void done() { then.invoke(); }
        @Override
        public void fail(@NonNull Exception e) { listener.fail(e); }
        @Override
        public void warn(@NonNull Exception e) { listener.warn(e); }
}
