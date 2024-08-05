/*
 * Copyright LWJGL. All rights reserved.
 * License terms: https://www.lwjgl.org/license
 * MACHINE GENERATED FILE, DO NOT EDIT
 */
package org.lwjgl.util.freetype;

import static org.lwjgl.system.APIUtil.apiClosureRet;
import static org.lwjgl.system.APIUtil.apiCreateCIF;
import static org.lwjgl.system.MemoryUtil.memGetAddress;
import static org.lwjgl.system.libffi.LibFFI.FFI_DEFAULT_ABI;
import static org.lwjgl.system.libffi.LibFFI.ffi_type_pointer;
import static org.lwjgl.system.libffi.LibFFI.ffi_type_sint32;

import org.lwjgl.system.CallbackI;
import org.lwjgl.system.NativeType;
import org.lwjgl.system.libffi.FFICIF;

/**
 * <h3>Type</h3>
 *
 * <pre><code>
 * int (*{@link #invoke}) (
 *     FT_Vector const *control,
 *     FT_Vector const *to,
 *     void *user
 * )</code></pre>
 */
@FunctionalInterface
@NativeType("FT_Outline_ConicToFunc")
public interface FT_Outline_ConicToFuncI extends CallbackI {

    FFICIF CIF = apiCreateCIF(
            FFI_DEFAULT_ABI,
            ffi_type_sint32,
            ffi_type_pointer, ffi_type_pointer, ffi_type_pointer
    );

    @Override
    default FFICIF getCallInterface() {
        return CIF;
    }

    @Override
    default void callback(long ret, long args) {
        int __result = invoke(
                memGetAddress(memGetAddress(args)),
                memGetAddress(memGetAddress(args + POINTER_SIZE)),
                memGetAddress(memGetAddress(args + 2L * POINTER_SIZE))
        );
        apiClosureRet(ret, __result);
    }

    /**
     * A function pointer type used to describe the signature of a 'conic to' function during outline walking or decomposition.
     */
    int invoke(@NativeType("FT_Vector const *") long control, @NativeType("FT_Vector const *") long to, @NativeType("void *") long user);

}