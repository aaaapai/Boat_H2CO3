/*
 * Copyright LWJGL. All rights reserved.
 * License terms: https://www.lwjgl.org/license
 * MACHINE GENERATED FILE, DO NOT EDIT
 */
package org.lwjgl.util.freetype;

import static org.lwjgl.system.APIUtil.apiClosureRet;
import static org.lwjgl.system.APIUtil.apiCreateCIF;
import static org.lwjgl.system.MemoryUtil.memGetAddress;
import static org.lwjgl.system.MemoryUtil.memGetCLong;
import static org.lwjgl.system.libffi.LibFFI.FFI_DEFAULT_ABI;
import static org.lwjgl.system.libffi.LibFFI.ffi_type_pointer;
import static org.lwjgl.system.libffi.LibFFI.ffi_type_ulong;

import org.lwjgl.system.CallbackI;
import org.lwjgl.system.NativeType;
import org.lwjgl.system.libffi.FFICIF;

/**
 * <h3>Type</h3>
 *
 * <pre><code>
 * unsigned long (*{@link #invoke}) (
 *     FT_Stream stream,
 *     unsigned long offset,
 *     unsigned char *buffer,
 *     unsigned long count
 * )</code></pre>
 */
@FunctionalInterface
@NativeType("FT_Stream_IoFunc")
public interface FT_Stream_IoFuncI extends CallbackI {

    FFICIF CIF = apiCreateCIF(
            FFI_DEFAULT_ABI,
            ffi_type_ulong,
            ffi_type_pointer, ffi_type_ulong, ffi_type_pointer, ffi_type_ulong
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
                memGetAddress(memGetAddress(args + 2L * POINTER_SIZE)),
                memGetCLong(memGetAddress(args + 3L * POINTER_SIZE))
        );
        apiClosureRet(ret, __result);
    }

    /**
     * A function used to seek and read data from a given input stream.
     */
    @NativeType("unsigned long")
    long invoke(@NativeType("FT_Stream") long stream, @NativeType("unsigned long") long offset, @NativeType("unsigned char *") long buffer, @NativeType("unsigned long") long count);

}