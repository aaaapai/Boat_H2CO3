package org.koishi.launcher.h2co3.core.utils.gson.fakefx;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.koishi.launcher.h2co3.core.fakefx.collections.ObservableList;
import org.koishi.launcher.h2co3.core.fakefx.collections.ObservableMap;
import org.koishi.launcher.h2co3.core.fakefx.collections.ObservableSet;
import org.koishi.launcher.h2co3.core.utils.gson.fakefx.creators.ObservableListCreator;
import org.koishi.launcher.h2co3.core.utils.gson.fakefx.creators.ObservableMapCreator;
import org.koishi.launcher.h2co3.core.utils.gson.fakefx.creators.ObservableSetCreator;
import org.koishi.launcher.h2co3.core.utils.gson.fakefx.factories.JavaFxPropertyTypeAdapterFactory;

public class FxGsonBuilder {

    private final GsonBuilder builder;
    private final boolean includeExtras = false;
    private boolean strictProperties = true;
    private boolean strictPrimitives = true;

    public FxGsonBuilder() {
        this(new GsonBuilder());
    }

    public FxGsonBuilder(GsonBuilder sourceBuilder) {
        this.builder = sourceBuilder;
    }

    public GsonBuilder builder() {
        // serialization of nulls is necessary to have properties with null values deserialized properly
        builder.serializeNulls()
                .registerTypeAdapter(ObservableList.class, new ObservableListCreator())
                .registerTypeAdapter(ObservableSet.class, new ObservableSetCreator())
                .registerTypeAdapter(ObservableMap.class, new ObservableMapCreator())
                .registerTypeAdapterFactory(new JavaFxPropertyTypeAdapterFactory(strictProperties, strictPrimitives));
        return builder;
    }

    public Gson create() {
        return builder().create();
    }

    public FxGsonBuilder acceptNullProperties() {
        strictProperties = false;
        return this;
    }

    public FxGsonBuilder acceptNullPrimitives() {
        strictPrimitives = false;
        return this;
    }

}