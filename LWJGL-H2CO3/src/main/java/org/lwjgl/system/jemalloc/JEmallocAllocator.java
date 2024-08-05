/*
 * Copyright LWJGL. All rights reserved.
 * License terms: https://www.lwjgl.org/license
 * MACHINE GENERATED FILE, DO NOT EDIT
 */
package org.lwjgl.system.jemalloc;

import static org.lwjgl.system.jemalloc.JEmalloc.Functions;
import static org.lwjgl.system.jemalloc.JEmalloc.nje_aligned_alloc;
import static org.lwjgl.system.jemalloc.JEmalloc.nje_calloc;
import static org.lwjgl.system.jemalloc.JEmalloc.nje_free;
import static org.lwjgl.system.jemalloc.JEmalloc.nje_malloc;
import static org.lwjgl.system.jemalloc.JEmalloc.nje_realloc;

import org.lwjgl.system.MemoryUtil.MemoryAllocator;

/** A {@link MemoryAllocator} implementation using the jemalloc library. */
public class JEmallocAllocator implements MemoryAllocator {

    static {
        // initialize jemalloc
        JEmalloc.getLibrary();
    }

    @Override
    public long getMalloc() { return Functions.malloc; }

    @Override
    public long getCalloc() { return Functions.calloc; }

    @Override
    public long getRealloc() { return Functions.realloc; }

    @Override
    public long getFree() { return Functions.free; }

    @Override
    public long getAlignedAlloc() { return Functions.aligned_alloc; }

    @Override
    public long getAlignedFree() { return Functions.free; }

    @Override
    public long malloc(long size) {
        return nje_malloc(size);
    }

    @Override
    public long calloc(long num, long size) {
        return nje_calloc(num, size);
    }

    @Override
    public long realloc(long ptr, long size) {
        return nje_realloc(ptr, size);
    }

    @Override
    public void free(long ptr) {
        nje_free(ptr);
    }

    @Override
    public long aligned_alloc(long alignment, long size) {
        return nje_aligned_alloc(alignment, size);
    }

    @Override
    public void aligned_free(long ptr) {
        nje_free(ptr);
    }

}
