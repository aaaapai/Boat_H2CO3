/*
 * Copyright LWJGL. All rights reserved.
 * License terms: https://www.lwjgl.org/license
 * MACHINE GENERATED FILE, DO NOT EDIT
 */
package org.lwjgl.util.freetype;

import static org.lwjgl.system.MemoryUtil.NULL;

import org.lwjgl.system.Callback;

import javax.annotation.Nullable;

/**
 * <h3>Type</h3>
 *
 * <pre><code>
 * int (*{@link #invoke}) (
 *     void *memory,
 *     FT_Raster *raster
 * )</code></pre>
 */
public abstract class FT_Raster_NewFunc extends Callback implements FT_Raster_NewFuncI {

    protected FT_Raster_NewFunc() {
        super(CIF);
    }

    FT_Raster_NewFunc(long functionPointer) {
        super(functionPointer);
    }

    /**
     * Creates a {@code FT_Raster_NewFunc} instance from the specified function pointer.
     *
     * @return the new {@code FT_Raster_NewFunc}
     */
    public static FT_Raster_NewFunc create(long functionPointer) {
        FT_Raster_NewFuncI instance = Callback.get(functionPointer);
        return instance instanceof FT_Raster_NewFunc
                ? (FT_Raster_NewFunc) instance
                : new Container(functionPointer, instance);
    }

    /**
     * Like {@link #create(long) create}, but returns {@code null} if {@code functionPointer} is {@code NULL}.
     */
    @Nullable
    public static FT_Raster_NewFunc createSafe(long functionPointer) {
        return functionPointer == NULL ? null : create(functionPointer);
    }

    /**
     * Creates a {@code FT_Raster_NewFunc} instance that delegates to the specified {@code FT_Raster_NewFuncI} instance.
     */
    public static FT_Raster_NewFunc create(FT_Raster_NewFuncI instance) {
        return instance instanceof FT_Raster_NewFunc
                ? (FT_Raster_NewFunc) instance
                : new Container(instance.address(), instance);
    }

    private static final class Container extends FT_Raster_NewFunc {

        private final FT_Raster_NewFuncI delegate;

        Container(long functionPointer, FT_Raster_NewFuncI delegate) {
            super(functionPointer);
            this.delegate = delegate;
        }

        @Override
        public int invoke(long memory, long raster) {
            return delegate.invoke(memory, raster);
        }

    }

}