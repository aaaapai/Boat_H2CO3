package org.koishi.launcher.h2co3.core.utils;

import org.koishi.launcher.h2co3.core.fakefx.beans.InvalidationListener;
import org.koishi.launcher.h2co3.core.fakefx.beans.Observable;

import java.util.Objects;

public final class Holder<T> implements InvalidationListener {
    public T value;

    public Holder() {
    }

    public Holder(T value) {
        this.value = value;
    }

    @Override
    public void invalidated(Observable observable) {
        // no-op
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(value);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;

        if (!(obj instanceof Holder))
            return false;

        return Objects.equals(this.value, ((Holder<?>) obj).value);
    }

    @Override
    public String toString() {
        return "Holder[" + value + "]";
    }
}
