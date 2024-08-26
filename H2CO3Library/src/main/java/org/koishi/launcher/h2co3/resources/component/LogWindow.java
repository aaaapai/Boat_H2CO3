package org.koishi.launcher.h2co3.resources.component;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ScrollView;

import androidx.core.widget.NestedScrollView;

import org.koishi.launcher.h2co3.core.fakefx.beans.property.BooleanProperty;
import org.koishi.launcher.h2co3.core.fakefx.beans.property.BooleanPropertyBase;
import org.koishi.launcher.h2co3.core.utils.task.Schedulers;

public class LogWindow extends NestedScrollView {

    private BooleanProperty visibilityProperty;
    private H2CO3TextView textView;
    private int lineCount;

    public LogWindow(Context context) {
        super(context);
        init(context);
    }

    public LogWindow(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public LogWindow(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        textView = createTextView(context);
        addView(textView);
    }

    private H2CO3TextView createTextView(Context context) {
        H2CO3TextView textView = new H2CO3TextView(context);
        textView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        textView.setTextSize(15);
        textView.setLineSpacing(0, 1f);
        textView.setEllipsize(null);
        return textView;
    }

    public final void setVisibilityValue(boolean visibility) {
        visibilityProperty().set(visibility);
    }

    public final boolean getVisibilityValue() {
        return visibilityProperty == null || visibilityProperty.get();
    }

    public final BooleanProperty visibilityProperty() {
        if (visibilityProperty == null) {
            visibilityProperty = new BooleanPropertyBase() {
                @Override
                public void invalidated() {
                    Schedulers.androidUIThread().execute(() -> {
                        boolean visible = get();
                        setVisibility(visible ? VISIBLE : GONE);
                        if (!visible) {
                            cleanLog();
                        }
                    });
                }

                @Override
                public Object getBean() {
                    return this;
                }

                @Override
                public String getName() {
                    return "visibility";
                }
            };
        }
        return visibilityProperty;
    }

    public void appendLog(String str) {
        if (!getVisibilityValue()) {
            return;
        }
        lineCount++;
        this.post(() -> {
            if (lineCount < 100) {
                textView.append(str);
            } else {
                cleanLog();
            }
            fullScroll(View.FOCUS_DOWN);
        });
    }

    public void cleanLog() {
        textView.setText("");
        lineCount = 0;
    }
}