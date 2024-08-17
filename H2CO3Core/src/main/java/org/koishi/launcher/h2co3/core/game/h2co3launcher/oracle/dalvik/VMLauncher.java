package org.koishi.launcher.h2co3.core.game.h2co3launcher.oracle.dalvik;

public final class VMLauncher {
    private VMLauncher() {
    }
    public static native int launchJVM(String[] args);
}
