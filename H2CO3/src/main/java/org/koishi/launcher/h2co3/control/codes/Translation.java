/*
 * //
 * // Created by cainiaohh on 2024-03-31.
 * //
 */

package org.koishi.launcher.h2co3.control.codes;

/**
 * @author caini
 */
public class Translation {
    private final CoKeyMap xKeyMap = new XKeyMap();

    public Translation(int i) {
    }

    public int trans(String str) {
        return (Integer) this.xKeyMap.translate(str);
    }
}
