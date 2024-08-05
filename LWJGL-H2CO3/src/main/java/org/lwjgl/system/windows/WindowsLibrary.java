/*
 * Copyright LWJGL. All rights reserved.
 * License terms: https://www.lwjgl.org/license
 */
package org.lwjgl.system.windows;

import static org.lwjgl.system.MemoryStack.stackPush;
import static org.lwjgl.system.MemoryUtil.NULL;
import static org.lwjgl.system.MemoryUtil.memAlloc;
import static org.lwjgl.system.MemoryUtil.memFree;
import static org.lwjgl.system.MemoryUtil.memRealloc;
import static org.lwjgl.system.MemoryUtil.memUTF16;
import static org.lwjgl.system.windows.WinBase.FreeLibrary;
import static org.lwjgl.system.windows.WinBase.GetModuleFileName;
import static org.lwjgl.system.windows.WinBase.GetModuleHandle;
import static org.lwjgl.system.windows.WinBase.GetProcAddress;
import static org.lwjgl.system.windows.WinBase.LoadLibrary;
import static org.lwjgl.system.windows.WinBase.getLastError;
import static org.lwjgl.system.windows.WindowsUtil.windowsThrowException;

import org.lwjgl.system.Library;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.SharedLibrary;

import java.nio.ByteBuffer;

import javax.annotation.Nullable;

/** Implements a {@link SharedLibrary} on the Windows OS. */
public class WindowsLibrary extends SharedLibrary.Default {

    /** The LWJGL dll handle. */
    public static final long HINSTANCE;

    static {
        try (MemoryStack stack = stackPush()) {
            HINSTANCE = GetModuleHandle(stack.UTF16(Library.JNI_LIBRARY_NAME));
            if (HINSTANCE == NULL) {
                throw new RuntimeException("Failed to retrieve LWJGL module handle.");
            }
        }
    }

    public WindowsLibrary(String name) {
        this(name, loadLibrary(name));
    }

    public WindowsLibrary(String name, long handle) {
        super(name, handle);
    }

    private static long loadLibrary(String name) {
        long handle;
        try (MemoryStack stack = stackPush()) {
            handle = LoadLibrary(stack.UTF16(name));
        }
        if (handle == NULL) {
            throw new UnsatisfiedLinkError("Failed to load library: " + name + " (error code = " + getLastError() + ")");
        }
        return handle;
    }

    @Nullable
    @Override
    public String getPath() {
        int maxLen = 256;

        ByteBuffer buffer = memAlloc(maxLen);
        try {
            while (true) {
                int len = GetModuleFileName(address(), buffer);
                int err = getLastError();
                if (err == 0) {
                    return len == 0 ? null : memUTF16(buffer, len);
                }
                if (err != 0x7A/*ERROR_INSUFFICIENT_BUFFER*/) {
                    return null;
                }
                buffer = memRealloc(buffer, maxLen = maxLen * 3 / 2);
            }
        } finally {
            memFree(buffer);
        }
    }

    @Override
    public long getFunctionAddress(ByteBuffer functionName) {
        return GetProcAddress(address(), functionName);
    }

    @Override
    public void free() {
        if (!FreeLibrary(address())) {
            windowsThrowException("Failed to unload library: " + getName());
        }
    }

}