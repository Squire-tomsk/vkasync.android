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

package org.jikopster.vkasync.core;

import java.util.LinkedList;

public class MultiException extends Exception {
    public interface Action {
        void invoke() throws Exception;
    }

    private LinkedList<Exception> mExceptions = new LinkedList<>();

    public MultiException add(Exception e) {
        mExceptions.add(e);
        return this;
    }

    public void tryOrAdd(Action action) {
        try {
            action.invoke();
        } catch (Exception e) {
            add(e);
        }
    }

    public void throwIfNotEmpty() throws Exception {
        switch (mExceptions.size()) {
            case 0:
                return;
            case 1:
                throw mExceptions.get(0);
            default:
                throw this;
        }
    }
}
