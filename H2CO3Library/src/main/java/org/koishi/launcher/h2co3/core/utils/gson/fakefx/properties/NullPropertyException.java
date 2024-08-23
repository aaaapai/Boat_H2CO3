package org.koishi.launcher.h2co3.core.utils.gson.fakefx.properties;

public class NullPropertyException extends RuntimeException {

    public NullPropertyException() {
        super("Null properties are forbidden");
    }
}