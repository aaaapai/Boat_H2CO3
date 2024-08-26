package org.koishi.launcher.h2co3.controller.input.screen.button;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;

public class BaseButton extends AppCompatButton {

    private String button_name;

    public BaseButton(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        TypedArray array = context.obtainStyledAttributes(attrs, org.koishi.launcher.h2co3.library.R.styleable.BaseButton);
        setButtonName(array.getString(org.koishi.launcher.h2co3.library.R.styleable.BaseButton_button_name));
        array.recycle();
    }

    public String getButtonName() {
        return button_name;
    }

    public void setButtonName(String buttonName) {
        button_name = buttonName;
    }
}
