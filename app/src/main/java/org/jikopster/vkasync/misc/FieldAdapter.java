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

package org.jikopster.vkasync.misc;

import org.jikopster.vkasync.misc.Lambda.*;

public class FieldAdapter<O,V>
{
    public FieldAdapter(Function1<O,V> getter, Function2<O,V,V> setter)
    {
        this.getter = getter;
        this.setter = setter;
    }

    private final Function1<O,V> getter;
    private final Function2<O,V,V> setter;

    public V get(O object) {
        return getter.invoke(object);
    }

    public V set(O object, V value) {
        return setter.invoke(object, value);
    }
}
