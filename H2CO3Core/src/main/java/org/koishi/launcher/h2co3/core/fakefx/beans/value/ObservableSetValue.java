package org.koishi.launcher.h2co3.core.fakefx.beans.value;

import org.koishi.launcher.h2co3.core.fakefx.collections.ObservableSet;

/**
 * An observable reference to an {@link ObservableSet}.
 *
 * @param <E> the type of the {@code Set} elements
 * @see ObservableSet
 * @see ObservableObjectValue
 * @see ObservableValue
 * @since JavaFX 2.1
 */
public interface ObservableSetValue<E> extends ObservableObjectValue<ObservableSet<E>>, ObservableSet<E> {
}
