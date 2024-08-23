package org.koishi.launcher.h2co3.resources.component;

import android.content.Context;
import android.util.AttributeSet;

import androidx.annotation.NonNull;

import com.google.android.material.textview.MaterialTextView;

import org.koishi.launcher.h2co3.core.fakefx.beans.property.BooleanProperty;
import org.koishi.launcher.h2co3.core.fakefx.beans.property.BooleanPropertyBase;
import org.koishi.launcher.h2co3.core.fakefx.beans.property.StringProperty;
import org.koishi.launcher.h2co3.core.fakefx.beans.property.StringPropertyBase;
import org.koishi.launcher.h2co3.core.utils.task.Schedulers;

public class H2CO3TextView extends MaterialTextView {
    private StringProperty string;
    private BooleanProperty visibilityProperty;
    public H2CO3TextView(@NonNull Context context) {
        super(context);
    }

    public H2CO3TextView(@NonNull Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public H2CO3TextView(@NonNull Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public final String getString() {
        return string == null ? null : string.get();
    }

    public final void setString(String string) {
        stringProperty().set(string);
    }

    public final StringProperty stringProperty() {
        if (string == null) {
            string = new StringPropertyBase() {

                public void invalidated() {
                    Schedulers.androidUIThread().execute(() -> {
                        String string = get();
                        setText(string);
                    });
                }

                public Object getBean() {
                    return this;
                }

                public String getName() {
                    return "string";
                }
            };
        }

        return string;
    }

    public final boolean getVisibilityValue() {
        return visibilityProperty == null || visibilityProperty.get();
    }

    public final void setVisibilityValue(boolean visibility) {
        visibilityProperty().set(visibility);
    }

    public final BooleanProperty visibilityProperty() {
        if (visibilityProperty == null) {
            visibilityProperty = new BooleanPropertyBase() {

                public void invalidated() {
                    Schedulers.androidUIThread().execute(() -> {
                        boolean visible = get();
                        setVisibility(visible ? VISIBLE : GONE);
                    });
                }

                public Object getBean() {
                    return this;
                }

                public String getName() {
                    return "visibility";
                }
            };
        }

        return visibilityProperty;
    }
}