package org.koishi.launcher.h2co3.core.fakefx.collections;

import org.koishi.launcher.h2co3.core.fakefx.collections.ListChangeListener.Change;

import java.util.ArrayList;
import java.util.List;

/**
 *
 */
public abstract class TrackableObservableList<T> extends ObservableListWrapper<T> {

    public TrackableObservableList(List<T> list) {
        super(list);
    }

    public TrackableObservableList() {
        super(new ArrayList<T>());
        addListener((Change<? extends T> c) -> {
            TrackableObservableList.this.onChanged((Change<T>) c);
        });
    }

    protected abstract void onChanged(Change<T> c);

}
