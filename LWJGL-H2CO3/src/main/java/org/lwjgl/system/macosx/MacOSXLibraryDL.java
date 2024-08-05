/*
 * Copyright LWJGL. All rights reserved.
 * License terms: https://www.lwjgl.org/license
 */
package org.lwjgl.system.macosx;

import static org.lwjgl.system.MemoryStack.stackPush;
import static org.lwjgl.system.MemoryUtil.NULL;
import static org.lwjgl.system.macosx.DynamicLinkLoader.RTLD_LAZY;
import static org.lwjgl.system.macosx.DynamicLinkLoader.RTLD_LOCAL;
import static org.lwjgl.system.macosx.DynamicLinkLoader.dlclose;
import static org.lwjgl.system.macosx.DynamicLinkLoader.dlerror;
import static org.lwjgl.system.macosx.DynamicLinkLoader.dlopen;
import static org.lwjgl.system.macosx.DynamicLinkLoader.dlsym;

import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.SharedLibrary;
import org.lwjgl.system.SharedLibraryUtil;

import java.nio.ByteBuffer;

import javax.annotation.Nullable;

/** Implements a {@link SharedLibrary} on the macOS using {@link DynamicLinkLoader#dlopen dlopen}. */
public class MacOSXLibraryDL extends MacOSXLibrary {

    public MacOSXLibraryDL(String name) {
        this(name, loadLibrary(name));
    }

    public MacOSXLibraryDL(String name, long handle) {
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