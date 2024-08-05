/*
 * Copyright LWJGL. All rights reserved.
 * License terms: https://www.lwjgl.org/license
 */
package org.lwjgl.system.h2co3launcher;

import static org.lwjgl.system.MemoryStack.stackPush;
import static org.lwjgl.system.MemoryUtil.NULL;
import static org.lwjgl.system.h2co3launcher.DynamicLinkLoader.RTLD_LAZY;
import static org.lwjgl.system.h2co3launcher.DynamicLinkLoader.RTLD_LOCAL;
import static org.lwjgl.system.h2co3launcher.DynamicLinkLoader.dlclose;
import static org.lwjgl.system.h2co3launcher.DynamicLinkLoader.dlerror;
import static org.lwjgl.system.h2co3launcher.DynamicLinkLoader.dlopen;
import static org.lwjgl.system.h2co3launcher.DynamicLinkLoader.dlsym;

import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.SharedLibrary;
import org.lwjgl.system.SharedLibraryUtil;

import java.nio.ByteBuffer;

import javax.annotation.Nullable;

/**
 * Implements a {@link SharedLibrary} on the Linux OS.
 */
public class H2CO3LauncherLibrary extends SharedLibrary.Default {

    public H2CO3LauncherLibrary(String name) {
        this(name, loadLibrary(name));
    }

    public H2CO3LauncherLibrary(String name, long handle) {
        super(name, handle);
    }

    private static long loadLibrary(String name) {
        long handle;
        try (MemoryStack stack = stackPush()) {
            handle = dlopen(stack.UTF8(name), RTLD_LAZY | RTLD_LOCAL);
        }
        if (handle == NULL) {
            throw new UnsatisfiedLinkError("Failed to dynamically load library: " + name + "(error = " + dlerror() + ")");
        }
        return handle;
    }

    @Nullable
    @Override
    public String getPath() {
        return SharedLibraryUtil.getLibraryPath(address());
    }

    @Override
    public long getFunctionAddress(ByteBuffer functionName) {
        return dlsym(address(), functionName);
    }

    @Override
    public void free() {
        dlclose(address());
    }

}