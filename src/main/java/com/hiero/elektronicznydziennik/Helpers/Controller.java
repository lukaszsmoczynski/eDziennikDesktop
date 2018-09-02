package com.hiero.elektronicznydziennik.Helpers;

import java.util.HashSet;

public abstract class Controller<T> implements ControllerInterface<T> {
    private HashSet<T> mListeners = new HashSet();

    @Override
    public void Attach(T pListener) {
        mListeners.add(pListener);
    }

    @Override
    public void Detach(T pListener) {
        mListeners.remove(pListener);
    }

    @Override
    public void Notify(T pListener, Object... pObjects) {
    }

    @Override
    public void NotifyAll(Object... pObjects) {
        for (T listener : mListeners) {
            Notify(listener, pObjects);
        }
    }
}
