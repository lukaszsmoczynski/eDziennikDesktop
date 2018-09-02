package com.hiero.elektronicznydziennik.Helpers;

import com.hiero.elektronicznydziennik.Helpers.Classes.Class;

public interface ControllerInterface<T> {
    void Attach(T pListener);

    void Detach(T pListener);

    void Notify(T pListener, Object... pObjects);

    void NotifyAll(Object... pObjects);
}
