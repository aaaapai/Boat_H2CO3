package org.koishi.launcher.h2co3.core.utils.io;

import java.io.IOException;

public class ArtifactMalformedException extends IOException {
    public ArtifactMalformedException(String message) {
        super(message);
    }

    public ArtifactMalformedException(String message, Throwable cause) {
        super(message, cause);
    }
}
