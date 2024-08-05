package org.koishi.launcher.h2co3.core.fakefx.collections;

import org.koishi.launcher.h2co3.core.fakefx.beans.Observable;

import java.util.Set;

public interface ObservableSet<E> extends Set<E>, Observable {
    /**
     * Add a listener to this observable set.
     *
     * @param listener the listener for listening to the set changes
     */
    void addListener(SetChangeListener<? super E> listener);

    /**
     * Tries to removed a listener from this observable set. If the listener is not
     * attached to this list, nothing happens.
     *
     * @param listener a listener to remove
     */
    void removeListener(SetChangeListener<? super E> listener);
}
