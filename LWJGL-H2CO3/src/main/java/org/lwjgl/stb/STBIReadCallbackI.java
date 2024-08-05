/*
 * Copyright LWJGL. All rights reserved.
 * License terms: https://www.lwjgl.org/license
 * MACHINE GENERATED FILE, DO NOT EDIT
 */
package org.lwjgl.stb;

import static org.lwjgl.system.APIUtil.apiClosureRet;
import static org.lwjgl.system.APIUtil.apiCreateCIF;
import static org.lwjgl.system.MemoryUtil.memGetAddress;
import static org.lwjgl.system.MemoryUtil.memGetInt;
import static org.lwjgl.system.libffi.LibFFI.FFI_DEFAULT_ABI;
import static org.lwjgl.system.libffi.LibFFI.ffi_type_pointer;
import static org.lwjgl.system.libffi.LibFFI.ffi_type_sint32;

import org.lwjgl.system.CallbackI;
import org.lwjgl.system.NativeType;
import org.lwjgl.system.libffi.FFICIF;

/**
 * Instances of this interface may be set to the {@code read} field of the {@link STBIIOCallbacks} struct.
 * 
 * <h3>Type</h3>
 * 
 * <pre><code>
 * int (*{@link #invoke}) (
 *     void *user,
 *     char *data,
 *     int size
 * )</code></pre>
 */
@FunctionalInterface
@NativeType("int (*) (void *, char *, int)")
public interface STBIReadCallbackI extends CallbackI {

    FFICIF CIF = apiCreateCIF(
        FFI_DEFAULT_ABI,
        ffi_type_sint32,
        ffi_type_pointer, ffi_type_pointer, ffi_type_sint32
    );

    @Override
    default FFICIF getCallInterface() { return CIF; }

    @Override
    default void callback(long ret, long args) {
        int __result = invoke(
            memGetAddress(memGetAddress(args)),
            memGetAddress(memGetAddress(args + POINTER_SIZE)),
                memGetInt(memGetAddress(args + 2L * POINTER_SIZE))
        );
        apiClosureRet(ret, __result);
    }

    /**
     * The {@code stbi_io_callbacks.read} callback.
     *
     * @param user a pointer to user data
     * @param data the data buffer to fill
     * @param size the number of bytes to read
     *
     * @return the number of bytes actually read
     */
    int invoke(@NativeType("void *") long user, @NativeType("char *") long data, int size);

}