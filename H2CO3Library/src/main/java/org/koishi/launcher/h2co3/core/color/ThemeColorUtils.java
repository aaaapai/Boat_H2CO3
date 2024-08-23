package org.koishi.launcher.h2co3.core.color;

import android.content.Context;
import android.graphics.Color;
import android.util.TypedValue;

public class ThemeColorUtils {
    public static int getColorFromTheme(Context context, int attr) {
        TypedValue typedValue = new TypedValue();
        if (context.getTheme().resolveAttribute(attr, typedValue, true)) {
            if (typedValue.type == TypedValue.TYPE_INT_COLOR_ARGB8 || typedValue.type == TypedValue.TYPE_INT_COLOR_RGB8) {
                return typedValue.data;
            } else if (typedValue.type == TypedValue.TYPE_STRING) {
                return Color.parseColor(typedValue.string.toString());
            }
        }
        return Color.RED;
    }


}
