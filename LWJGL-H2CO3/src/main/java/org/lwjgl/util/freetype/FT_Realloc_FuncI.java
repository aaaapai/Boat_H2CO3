/*
 * Copyright LWJGL. All rights reserved.
 * License terms: https://www.lwjgl.org/license
 * MACHINE GENERATED FILE, DO NOT EDIT
 */
package org.lwjgl.util.freetype;

import static org.lwjgl.system.APIUtil.apiClosureRetP;
import static org.lwjgl.system.APIUtil.apiCreateCIF;
import static org.lwjgl.system.MemoryUtil.memGetAddress;
import static org.lwjgl.system.MemoryUtil.memGetCLong;
import static org.lwjgl.system.libffi.LibFFI.FFI_DEFAULT_ABI;
import static org.lwjgl.system.libffi.LibFFI.ffi_type_pointer;
import static org.lwjgl.system.libffi.LibFFI.ffi_type_slong;

import org.lwjgl.system.CallbackI;
import org.lwjgl.system.NativeType;
import org.lwjgl.system.libffi.FFICIF;

/**
 * <h3>Type</h3>
 *
 * <pre><code>
 * void * (*{@link #invoke}) (
 *     FT_Memory memory,
 *     long cur_size,
 *     long new_size,
 *     void *block
 * )</code></pre>
 */
@FunctionalInterface
@NativeType("FT_Realloc_Func")
public interface FT_Realloc_FuncI extends CallbackI {

    FFICIF CIF = apiCreateCIF(
            FFI_DEFAULT_ABI,
            ffi_type_pointer,
            ffi_type_pointer, ffi_type_slong, ffi_type_slong, ffi_type_pointer
    );

    @Override
    default FFICIF getCallInterface() {
        return CIF;
    }

    @Override
    default void callback(long ret, long args) {
        long __result = invoke(
                memGetAddress(memGetAddress(args)),
                memGetCLong(memGetAddress(args + POINTER_SIZE)),
                memGetCLong(memGetAddress(args + 2L * POINTER_SIZE)),
                memGetAddress(memGetAddress(args + 3L * POINTER_SIZE))
        );
        apiClosureRetP(ret, __result);
    }

    /**
     * A function used to re-allocate a given block of memory.
     */
    @NativeType("void *")
    long invoke(@NativeType("FT_Memory") long memory, long cur_size, long new_size, @NativeType("void *") long block);

}