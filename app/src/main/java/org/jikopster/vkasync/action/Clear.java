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

import org.jikopster.vkasync.core.Async;
import org.jikopster.vkasync.core.Exception;
import org.jikopster.vkasync.core.Worker.*;
import org.jikopster.vkasync.misc.Fucktory;

import java.io.File;

public class Clear extends Action
{
    public class ClearException extends Exception.Fatal { }


    public Clear(Context context) { super(context); }

    @Override
    public void run(Exception.Listener listener) {
        super.run(
                new Listener(listener, () -> Async.run(listener, () -> {
                    if (!delete(new File(localPath)))
                        throw new ClearException();
                }))
        );
    }

    @Override
    protected Iterable<Checker> getCheckers() {
        return
                Fucktory.<Checker>fuckEmAll(
                        new Fucktory<>(this::getLocalChecker),
                        new Fucktory<>(this::getMediaChecker)
                );
    }

    @Override
    protected Iterable<Processor> getProcessors() {
        return
                Fucktory.<Processor>fuckEmAll(
                        new Fucktory<>(this::getLocalProcessor),
                        new Fucktory<>(this::getMediaProcessor)
                );
    }

    public boolean delete(File file) {
        if (file.isDirectory()) {
            File[] files = file.listFiles();
            if (files != null)
                for (File f : files) delete(f);
        }
        return file.delete();
    }
}
