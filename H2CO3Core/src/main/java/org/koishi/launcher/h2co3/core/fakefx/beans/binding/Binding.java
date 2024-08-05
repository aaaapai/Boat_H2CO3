package org.koishi.launcher.h2co3.core.fakefx.beans.binding;

import org.koishi.launcher.h2co3.core.fakefx.beans.value.ObservableValue;
import org.koishi.launcher.h2co3.core.fakefx.collections.ObservableList;

public interface Binding<T> extends ObservableValue<T> {

    boolean isValid();

    void invalidate();

    ObservableList<?> getDependencies();

    void dispose();

}
