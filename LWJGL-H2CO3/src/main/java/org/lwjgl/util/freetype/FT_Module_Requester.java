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
 * FT_Module_Interface (*{@link #invoke}) (
 *     FT_Module module,
 *     char const *name
 * )</code></pre>
 */
public abstract class FT_Module_Requester extends Callback implements FT_Module_RequesterI {

    protected FT_Module_Requester() {
        super(CIF);
    }

    FT_Module_Requester(long functionPointer) {
        super(functionPointer);
    }

    /**
     * Creates a {@code FT_Module_Requester} instance from the specified function pointer.
     *
     * @return the new {@code FT_Module_Requester}
     */
    public static FT_Module_Requester create(long functionPointer) {
        FT_Module_RequesterI instance = Callback.get(functionPointer);
        return instance instanceof FT_Module_Requester
                ? (FT_Module_Requester) instance
                : new Container(functionPointer, instance);
    }

    /**
     * Like {@link #create(long) create}, but returns {@code null} if {@code functionPointer} is {@code NULL}.
     */
    @Nullable
    public static FT_Module_Requester createSafe(long functionPointer) {
        return functionPointer == NULL ? null : create(functionPointer);
    }

    /**
     * Creates a {@code FT_Module_Requester} instance that delegates to the specified {@code FT_Module_RequesterI} instance.
     */
    public static FT_Module_Requester create(FT_Module_RequesterI instance) {
        return instance instanceof FT_Module_Requester
                ? (FT_Module_Requester) instance
                : new Container(instance.address(), instance);
    }

    private static final class Container extends FT_Module_Requester {

        private final FT_Module_RequesterI delegate;

        Container(long functionPointer, FT_Module_RequesterI delegate) {
            super(functionPointer);
            this.delegate = delegate;
        }

        @Override
        public long invoke(long module, long name) {
            return delegate.invoke(module, name);
        }

    }

}