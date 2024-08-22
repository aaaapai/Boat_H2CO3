package org.koishi.launcher.h2co3.resources.component;

import android.content.Context;
import android.view.MotionEvent;
import android.view.View;

public class CustomScrollBar extends View{
    public CustomScrollBar(Context context) {
        super(context);
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        // 获取拖动的位置
        float y = event.getY();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                break;
            case MotionEvent.ACTION_MOVE:
                break;
            case MotionEvent.ACTION_UP:
                break;
        }

        return true;
    }
}