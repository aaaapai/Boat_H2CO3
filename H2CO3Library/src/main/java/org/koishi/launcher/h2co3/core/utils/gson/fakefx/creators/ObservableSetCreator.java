package org.koishi.launcher.h2co3.core.utils.gson.fakefx.creators;

import com.google.gson.InstanceCreator;

import org.koishi.launcher.h2co3.core.fakefx.collections.FXCollections;
import org.koishi.launcher.h2co3.core.fakefx.collections.ObservableSet;

import java.lang.reflect.Type;

/**
 * An {@link InstanceCreator} for observable sets using {@link FXCollections}.
 */
public class ObservableSetCreator implements InstanceCreator<ObservableSet<?>> {

    public ObservableSet<?> createInstance(Type type) {
        // No need to use a parametrized set since the actual instance will have the raw type anyway.
        return FXCollections.observableSet();
    }
}