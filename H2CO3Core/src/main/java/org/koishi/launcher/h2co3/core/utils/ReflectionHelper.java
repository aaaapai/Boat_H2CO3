package org.koishi.launcher.h2co3.core.utils;

import androidx.core.util.Predicate;

import org.apache.commons.lang3.ClassUtils;

public final class ReflectionHelper {
    private ReflectionHelper() {
    }

    /**
     * Get caller, this method is caller sensitive.
     *
     * @param packageFilter returns false if we consider the given package is internal calls, not the caller
     * @return the caller, method name, source file, line number
     */
    public static StackTraceElement getCaller(Predicate<String> packageFilter) {
        StackTraceElement[] elements = Thread.currentThread().getStackTrace();
        // element[0] is Thread.currentThread().getStackTrace()
        // element[1] is ReflectionHelper.getCaller(packageFilter)
        // so element[2] is caller of this method.
        StackTraceElement caller = elements[2];
        String callerPackage = ClassUtils.getPackageName(caller.getClassName());
        for (int i = 3; i < elements.length; ++i) {
            String elementPackage = ClassUtils.getPackageName(elements[i].getClassName());
            if (packageFilter.test(elementPackage) && !callerPackage.equals(elementPackage))
                return elements[i];
        }
        return caller;
    }

}
