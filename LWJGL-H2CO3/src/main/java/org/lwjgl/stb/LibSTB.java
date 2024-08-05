/*
 * Copyright LWJGL. All rights reserved.
 * License terms: https://www.lwjgl.org/license
 * MACHINE GENERATED FILE, DO NOT EDIT
 */
package org.lwjgl.stb;

import static org.lwjgl.system.MemoryUtil.MemoryAllocator;
import static org.lwjgl.system.MemoryUtil.getAllocator;

import org.lwjgl.system.Configuration;
import org.lwjgl.system.Library;
import org.lwjgl.system.Platform;

/** Initializes the stb shared library. */
final class LibSTB {

    static {
        String libName = Platform.mapLibraryNameBundled("lwjgl_stb");
        Library.loadSystem(System::load, System::loadLibrary, LibSTB.class, "org.lwjgl.stb", libName);

        MemoryAllocator allocator = getAllocator(Configuration.DEBUG_MEMORY_ALLOCATOR_INTERNAL.get(true));
        setupMalloc(
            allocator.getMalloc(),
            allocator.getCalloc(),
            allocator.getRealloc(),
            allocator.getFree(),
            allocator.getAlignedAlloc(),
            allocator.getAlignedFree()
        );
    }

    private LibSTB() {
    }

    static void initialize() {
        // intentionally empty to trigger static initializer
    }

    private static native void setupMalloc(
        long malloc,
        long calloc,
        long realloc,
        long free,
        long aligned_alloc,
        long aligned_free
    );

}
