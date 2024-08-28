package org.koishi.launcher.h2co3.core.launch;

public final class H2CO3JVMLauncher {
    private H2CO3JVMLauncher() {
    }
    public static native int launchJVM(String[] args);
}
