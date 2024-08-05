/*
 * Copyright LWJGL. All rights reserved.
 * License terms: https://www.lwjgl.org/license
 * MACHINE GENERATED FILE, DO NOT EDIT
 */
package org.lwjgl.system.linux;

import static org.lwjgl.system.Checks.CHECKS;
import static org.lwjgl.system.Checks.check;
import static org.lwjgl.system.Checks.checkNT1;
import static org.lwjgl.system.MemoryStack.stackGet;
import static org.lwjgl.system.MemoryUtil.memAddress;

import org.lwjgl.system.Library;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.NativeType;

import java.nio.ByteBuffer;

/** Native bindings to &lt;sys/stat.h&gt;. */
public class Stat {

    static { Library.initialize(); }

    protected Stat() {
        throw new UnsupportedOperationException();
    }

    // --- [ stat ] ---

    public static native int nstat(long __file, long __buf);

    public static int stat(@NativeType("char const *") ByteBuffer __file, @NativeType("struct stat *") long __buf) {
        if (CHECKS) {
            checkNT1(__file);
            check(__buf);
        }
        return nstat(memAddress(__file), __buf);
    }

    public static int stat(@NativeType("char const *") CharSequence __file, @NativeType("struct stat *") long __buf) {
        if (CHECKS) {
            check(__buf);
        }
        MemoryStack stack = stackGet(); int stackPointer = stack.getPointer();
        try {
            stack.nUTF8(__file, true);
            long __fileEncoded = stack.getPointerAddress();
            return nstat(__fileEncoded, __buf);
        } finally {
            stack.setPointer(stackPointer);
        }
    }

    // --- [ fstat ] ---

    public static native int nfstat(int __fd, long __buf);

    public static int fstat(int __fd, @NativeType("struct stat *") long __buf) {
        if (CHECKS) {
            check(__buf);
        }
        return nfstat(__fd, __buf);
    }

}