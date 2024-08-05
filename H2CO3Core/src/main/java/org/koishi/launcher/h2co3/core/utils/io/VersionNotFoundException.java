package org.koishi.launcher.h2co3.core.utils.io;

public final class VersionNotFoundException extends RuntimeException {

    public VersionNotFoundException() {
    }

    public VersionNotFoundException(String message) {
        super(message);
    }

    public VersionNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
