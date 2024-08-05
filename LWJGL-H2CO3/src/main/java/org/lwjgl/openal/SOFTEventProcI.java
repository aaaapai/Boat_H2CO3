/*
 * Copyright LWJGL. All rights reserved.
 * License terms: https://www.lwjgl.org/license
 * MACHINE GENERATED FILE, DO NOT EDIT
 */
package org.lwjgl.openal;

import static org.lwjgl.system.APIUtil.apiCreateCIF;
import static org.lwjgl.system.MemoryUtil.memGetAddress;
import static org.lwjgl.system.MemoryUtil.memGetInt;
import static org.lwjgl.system.libffi.LibFFI.FFI_DEFAULT_ABI;
import static org.lwjgl.system.libffi.LibFFI.ffi_type_pointer;
import static org.lwjgl.system.libffi.LibFFI.ffi_type_sint32;
import static org.lwjgl.system.libffi.LibFFI.ffi_type_uint32;
import static org.lwjgl.system.libffi.LibFFI.ffi_type_void;

import org.lwjgl.system.CallbackI;
import org.lwjgl.system.NativeType;
import org.lwjgl.system.libffi.FFICIF;

/**
 * <h3>Type</h3>
 * 
 * <pre><code>
 * void (*{@link #invoke}) (
 *     ALenum eventType,
 *     ALuint object,
 *     ALuint param,
 *     ALsizei length,
 *     ALchar const *message,
 *     ALvoid *userParam
 * )</code></pre>
 */
@FunctionalInterface
@NativeType("ALEVENTPROCSOFT")
public interface SOFTEventProcI extends CallbackI {

    FFICIF CIF = apiCreateCIF(
        FFI_DEFAULT_ABI,
        ffi_type_void,
        ffi_type_sint32, ffi_type_uint32, ffi_type_uint32, ffi_type_sint32, ffi_type_pointer, ffi_type_pointer
    );

    @Override
    default FFICIF getCallInterface() { return CIF; }

    @Override
    default void callback(long ret, long args) {
        invoke(
                memGetInt(memGetAddress(args)),
                memGetInt(memGetAddress(args + POINTER_SIZE)),
                memGetInt(memGetAddress(args + 2L * POINTER_SIZE)),
                memGetInt(memGetAddress(args + 3L * POINTER_SIZE)),
                memGetAddress(memGetAddress(args + 4L * POINTER_SIZE)),
                memGetAddress(memGetAddress(args + 5L * POINTER_SIZE))
        );
    }

    void invoke(@NativeType("ALenum") int eventType, @NativeType("ALuint") int object, @NativeType("ALuint") int param, @NativeType("ALsizei") int length, @NativeType("ALchar const *") long message, @NativeType("ALvoid *") long userParam);

}