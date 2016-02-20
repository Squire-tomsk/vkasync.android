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
