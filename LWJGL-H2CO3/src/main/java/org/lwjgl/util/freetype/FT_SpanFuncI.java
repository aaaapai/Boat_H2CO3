/*
 * Copyright LWJGL. All rights reserved.
 * License terms: https://www.lwjgl.org/license
 * MACHINE GENERATED FILE, DO NOT EDIT
 */
package org.lwjgl.util.freetype;

import static org.lwjgl.system.APIUtil.apiCreateCIF;
import static org.lwjgl.system.MemoryUtil.memGetAddress;
import static org.lwjgl.system.MemoryUtil.memGetInt;
import static org.lwjgl.system.libffi.LibFFI.FFI_DEFAULT_ABI;
import static org.lwjgl.system.libffi.LibFFI.ffi_type_pointer;
import static org.lwjgl.system.libffi.LibFFI.ffi_type_sint32;
import static org.lwjgl.system.libffi.LibFFI.ffi_type_void;

import org.lwjgl.system.CallbackI;
import org.lwjgl.system.NativeType;
import org.lwjgl.system.libffi.FFICIF;

/**
 * <h3>Type</h3>
 *
 * <pre><code>
 * void (*{@link #invoke}) (
 *     int y,
 *     int count,
 *     FT_Span const *spans,
 *     void *user
 * )</code></pre>
 */
@FunctionalInterface
@NativeType("FT_SpanFunc")
public interface FT_SpanFuncI extends CallbackI {

    FFICIF CIF = apiCreateCIF(
            FFI_DEFAULT_ABI,
            ffi_type_void,
            ffi_type_sint32, ffi_type_sint32, ffi_type_pointer, ffi_type_pointer
    );

    @Override
    default FFICIF getCallInterface() {
        return CIF;
    }

    @Override
    default void callback(long ret, long args) {
        invoke(
                memGetInt(memGetAddress(args)),
                memGetInt(memGetAddress(args + POINTER_SIZE)),
                memGetAddress(memGetAddress(args + 2L * POINTER_SIZE)),
                memGetAddress(memGetAddress(args + 3L * POINTER_SIZE))
        );
    }

    /**
     * A function used as a call-back by the anti-aliased renderer in order to let client applications draw themselves the pixel spans on each scan line.
     */
    void invoke(int y, int count, @NativeType("FT_Span const *") long spans, @NativeType("void *") long user);

}