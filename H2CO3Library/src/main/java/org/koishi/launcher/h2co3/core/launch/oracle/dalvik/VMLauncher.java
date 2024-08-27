package org.koishi.launcher.h2co3.core.launch.oracle.dalvik;

public final class VMLauncher {
    private VMLauncher() {
    }
    public static native int launchJVM(String[] args);
}
