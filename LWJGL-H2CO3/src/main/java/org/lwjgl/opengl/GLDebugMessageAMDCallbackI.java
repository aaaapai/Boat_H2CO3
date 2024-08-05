/*
 * Copyright LWJGL. All rights reserved.
 * License terms: https://www.lwjgl.org/license
 * MACHINE GENERATED FILE, DO NOT EDIT
 */
package org.lwjgl.opengl;

import static org.lwjgl.system.APIUtil.apiCreateCIF;
import static org.lwjgl.system.APIUtil.apiStdcall;
import static org.lwjgl.system.MemoryUtil.memGetAddress;
import static org.lwjgl.system.MemoryUtil.memGetInt;
import static org.lwjgl.system.libffi.LibFFI.ffi_type_pointer;
import static org.lwjgl.system.libffi.LibFFI.ffi_type_sint32;
import static org.lwjgl.system.libffi.LibFFI.ffi_type_uint32;
import static org.lwjgl.system.libffi.LibFFI.ffi_type_void;

import org.lwjgl.system.CallbackI;
import org.lwjgl.system.NativeType;
import org.lwjgl.system.libffi.FFICIF;

/**
 * Instances of this interface may be passed to the {@link AMDDebugOutput#glDebugMessageCallbackAMD DebugMessageCallbackAMD} method.
 * 
 * <h3>Type</h3>
 * 
 * <pre><code>
 * void (*{@link #invoke}) (
 *     GLuint id,
 *     GLenum category,
 *     GLenum severity,
 *     GLsizei length,
 *     GLchar const *message,
 *     void *userParam
 * )</code></pre>
 */
@FunctionalInterface
@NativeType("GLDEBUGPROCAMD")
public interface GLDebugMessageAMDCallbackI extends CallbackI {

    FFICIF CIF = apiCreateCIF(
        apiStdcall(),
        ffi_type_void,
        ffi_type_uint32, ffi_type_uint32, ffi_type_uint32, ffi_type_sint32, ffi_type_pointer, ffi_type_pointer
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

    /**
     * Will be called when a debug message is generated.
     *
     * @param id        the message ID
     * @param category  the message category
     * @param severity  the message severity
     * @param length    the message length, excluding the null-terminator
     * @param message   a pointer to the message string representation
     * @param userParam the user-specified value that was passed when calling {@link AMDDebugOutput#glDebugMessageCallbackAMD DebugMessageCallbackAMD}
     */
    void invoke(@NativeType("GLuint") int id, @NativeType("GLenum") int category, @NativeType("GLenum") int severity, @NativeType("GLsizei") int length, @NativeType("GLchar const *") long message, @NativeType("void *") long userParam);

}