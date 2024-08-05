/*
 * Copyright LWJGL. All rights reserved.
 * License terms: https://www.lwjgl.org/license
 */
package org.lwjgl.system;

import static org.lwjgl.system.MemoryUtil.memAddress;
import static org.lwjgl.system.MemoryUtil.memAlloc;
import static org.lwjgl.system.MemoryUtil.memFree;
import static org.lwjgl.system.MemoryUtil.memRealloc;
import static org.lwjgl.system.MemoryUtil.memUTF8;

import java.nio.ByteBuffer;

import javax.annotation.Nullable;

public final class SharedLibraryUtil {

    private static native int getLibraryPath(long pLib, long sOut, int bufSize);

    @Nullable
    public static String getLibraryPath(long pLib) {
        int maxLen = 256;

        ByteBuffer buffer = memAlloc(maxLen);
        try {
            while (true) {
                int len = getLibraryPath(pLib, memAddress(buffer), maxLen);
                if (len == 0) {
                    return null;
                }
                if (len < maxLen) {
                    return memUTF8(buffer, len - 1); // drop the null-terminator
                }
                buffer = memRealloc(buffer, maxLen = maxLen * 3 / 2);
            }
        } finally {
            memFree(buffer);
        }
    }

}