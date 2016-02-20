package org.jikopster.vkasync.core;

public abstract class Worker
{
    private static byte i;
    protected static int nextFlag() { return 1 << i++; }


    public interface Checker {
        void check(Master.TrackList tracks) throws Exception;
    }

    public interface Processor {
        default void prepare() throws Exception { }
        void process(Track track) throws Exception;
    }
}