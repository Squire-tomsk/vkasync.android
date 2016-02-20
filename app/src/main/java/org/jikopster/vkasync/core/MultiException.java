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
