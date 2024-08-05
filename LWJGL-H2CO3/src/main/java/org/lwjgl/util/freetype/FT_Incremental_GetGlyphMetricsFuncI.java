/*
 * Copyright LWJGL. All rights reserved.
 * License terms: https://www.lwjgl.org/license
 * MACHINE GENERATED FILE, DO NOT EDIT
 */
package org.lwjgl.util.freetype;

import static org.lwjgl.system.APIUtil.apiClosureRet;
import static org.lwjgl.system.APIUtil.apiCreateCIF;
import static org.lwjgl.system.MemoryUtil.memGetAddress;
import static org.lwjgl.system.MemoryUtil.memGetByte;
import static org.lwjgl.system.MemoryUtil.memGetInt;
import static org.lwjgl.system.libffi.LibFFI.FFI_DEFAULT_ABI;
import static org.lwjgl.system.libffi.LibFFI.ffi_type_pointer;
import static org.lwjgl.system.libffi.LibFFI.ffi_type_sint32;
import static org.lwjgl.system.libffi.LibFFI.ffi_type_uint32;
import static org.lwjgl.system.libffi.LibFFI.ffi_type_uint8;

import org.lwjgl.system.CallbackI;
import org.lwjgl.system.NativeType;
import org.lwjgl.system.libffi.FFICIF;

/**
 * <h3>Type</h3>
 *
 * <pre><code>
 * FT_Error (*{@link #invoke}) (
 *     FT_Incremental incremental,
 *     FT_UInt glyph_index,
 *     FT_Bool vertical,
 *     FT_Incremental_MetricsRec *ametrics
 * )</code></pre>
 */
@FunctionalInterface
@NativeType("FT_Incremental_GetGlyphMetricsFunc")
public interface FT_Incremental_GetGlyphMetricsFuncI extends CallbackI {

    FFICIF CIF = apiCreateCIF(
            FFI_DEFAULT_ABI,
            ffi_type_sint32,
            ffi_type_pointer, ffi_type_uint32, ffi_type_uint8, ffi_type_pointer
    );

    @Override
    default FFICIF getCallInterface() {
        return CIF;
    }

    @Override
    default void callback(long ret, long args) {
        int __result = invoke(
                memGetAddress(memGetAddress(args)),
                memGetInt(memGetAddress(args + POINTER_SIZE)),
                memGetByte(memGetAddress(args + 2L * POINTER_SIZE)) != 0,
                memGetAddress(memGetAddress(args + 3L * POINTER_SIZE))
        );
        apiClosureRet(ret, __result);
    }

    /**
     * A function used to retrieve the basic metrics of a given glyph index before accessing its data.
     */
    @NativeType("FT_Error")
    int invoke(@NativeType("FT_Incremental") long incremental, @NativeType("FT_UInt") int glyph_index, @NativeType("FT_Bool") boolean vertical, @NativeType("FT_Incremental_MetricsRec *") long ametrics);

}