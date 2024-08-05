/*
 * Copyright LWJGL. All rights reserved.
 * License terms: https://www.lwjgl.org/license
 * MACHINE GENERATED FILE, DO NOT EDIT
 */
package org.lwjgl.stb;

import static org.lwjgl.system.APIUtil.apiClosureRetP;
import static org.lwjgl.system.APIUtil.apiCreateCIF;
import static org.lwjgl.system.MemoryUtil.memGetAddress;
import static org.lwjgl.system.MemoryUtil.memGetInt;
import static org.lwjgl.system.libffi.LibFFI.FFI_DEFAULT_ABI;
import static org.lwjgl.system.libffi.LibFFI.ffi_type_pointer;
import static org.lwjgl.system.libffi.LibFFI.ffi_type_sint32;

import org.lwjgl.system.CallbackI;
import org.lwjgl.system.MemoryUtil;
import org.lwjgl.system.NativeType;
import org.lwjgl.system.libffi.FFICIF;

/**
 * Instances of this interface may be set to {@link STBImageWrite#stbi_zlib_compress}.
 * 
 * <h3>Type</h3>
 * 
 * <pre><code>
 * unsigned char * (*{@link #invoke}) (
 *     unsigned char *data,
 *     int data_len,
 *     int *out_len,
 *     int quality
 * )</code></pre>
 */
@FunctionalInterface
@NativeType("unsigned char * (*) (unsigned char *, int, int *, int)")
public interface STBIZlibCompressI extends CallbackI {

    FFICIF CIF = apiCreateCIF(
        FFI_DEFAULT_ABI,
        ffi_type_pointer,
        ffi_type_pointer, ffi_type_sint32, ffi_type_pointer, ffi_type_sint32
    );

    @Override
    default FFICIF getCallInterface() { return CIF; }

    @Override
    default void callback(long ret, long args) {
        long __result = invoke(
                memGetAddress(memGetAddress(args)),
                memGetInt(memGetAddress(args + POINTER_SIZE)),
                memGetAddress(memGetAddress(args + 2L * POINTER_SIZE)),
                memGetInt(memGetAddress(args + 3L * POINTER_SIZE))
        );
        apiClosureRetP(ret, __result);
    }

    /**
     * Compresses a block of data using Zlib compression.
     * 
     * <p>The returned data will be freed with {@link MemoryUtil#memFree} so it must be heap allocated with {@link MemoryUtil#memAlloc}.</p>
     *
     * @param data     the data to compress
     * @param data_len the data length, in bytes
     * @param out_len  returns the compressed data length, in bytes
     * @param quality  the compression quality to use
     *
     * @return the compressed data
     */
    @NativeType("unsigned char *") long invoke(@NativeType("unsigned char *") long data, int data_len, @NativeType("int *") long out_len, int quality);

}