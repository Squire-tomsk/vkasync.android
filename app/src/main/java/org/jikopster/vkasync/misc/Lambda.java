package org.jikopster.vkasync.misc;

public class Lambda
{
    public interface Action {
        void invoke();
    }

    public interface Action1<T> {
        void invoke(T arg);
    }

    public interface Action2<T1, T2> {
        void invoke(T1 arg1, T2 arg2);
    }

    public interface Function<TOut> {
        TOut invoke();
    }

    public interface Function1<TIn, TOut> {
        TOut invoke(TIn arg);
    }

    public interface Function2<T1, T2, TOut> {
        TOut invoke(T1 arg1, T2 arg2);
    }
}
