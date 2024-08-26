package org.koishi.launcher.h2co3.controller.input.screen.button;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;

import androidx.annotation.Nullable;

public class MouseButton extends BaseButton {

    String MouseName;

    public MouseButton(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        TypedArray array = context.obtainStyledAttributes(attrs, org.koishi.launcher.h2co3.library.R.styleable.MouseButton);
        setMouseName(array.getString(org.koishi.launcher.h2co3.library.R.styleable.MouseButton_mouse_name));
        array.recycle();
    }

    public String getMouseName() {
        return MouseName;
    }

    public void setMouseName(String mouseName) {
        MouseName = mouseName;
    }
}
