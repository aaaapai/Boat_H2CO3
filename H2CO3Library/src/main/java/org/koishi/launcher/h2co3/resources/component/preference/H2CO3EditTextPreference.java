package org.koishi.launcher.h2co3.resources.component.preference;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.textfield.TextInputEditText;

import org.koishi.launcher.h2co3.library.R;
import org.koishi.launcher.h2co3.resources.component.H2CO3CardView;
import org.koishi.launcher.h2co3.resources.component.H2CO3TextView;

public class H2CO3EditTextPreference extends H2CO3CardView {

    private TextInputEditText editText;
    private H2CO3TextView title;
    private String key;
    private String text;
    private H2CO3CardView rootView;
    private OnPreferenceChangeListener onPreferenceChangeListener;

    public H2CO3EditTextPreference(@NonNull Context context) {
        super(context);
        init(context, null);
    }

    public H2CO3EditTextPreference(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public H2CO3EditTextPreference(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(@NonNull Context context, @Nullable AttributeSet attrs) {
        // Inflate the custom layout
        LayoutInflater.from(context).inflate(R.layout.preference_custom_edit, this, true);
        setBackgroundResource(android.R.color.transparent);
        setStrokeWidth(0);

        // Initialize views
        editText = findViewById(R.id.editText);
        title = findViewById(R.id.title);
        rootView = findViewById(R.id.root_view);

        // Set up the click listener for the card to give focus to EditText
        rootView.setOnClickListener(v -> editText.requestFocus());

        // Set up the text change listener
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                text = s.toString();
                if (onPreferenceChangeListener != null) {
                    onPreferenceChangeListener.onPreferenceChange(H2CO3EditTextPreference.this, text);
                }
            }
        });
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            // 当用户点击卡片时，EditText 获得焦点
            if (!editText.isFocused()) {
                editText.requestFocus();
            }
        } else if (event.getAction() == MotionEvent.ACTION_UP) {
            // 点击抬起时，检查点击位置是否在 EditText 外部
            if (!isViewUnder(rootView, event.getX(), event.getY())) {
                // 如果点击位置不在 EditText 上，使 EditText 失去焦点
                rootView.clearFocus();
            }
        }
        return super.dispatchTouchEvent(event);
    }

    // Set the title of the preference
    public void setTitle(String title) {
        this.title.setText(title);
    }

    public void setHint(String hint) {
        editText.setHint(hint);
    }

    // Set the key for the preference
    public void setKey(String key) {
        this.key = key;
    }

    // Get the text of the EditText
    public String getText() {
        return text;
    }

    // Set the text of the EditText
    public void setText(String text) {
        editText.setText(text);
        this.text = text;
    }

    // Set the preference change listener
    public void setOnPreferenceChangeListener(OnPreferenceChangeListener listener) {
        onPreferenceChangeListener = listener;
    }

    private boolean isViewUnder(View view, float x, float y) {
        int[] location = new int[2];
        view.getLocationOnScreen(location);
        return x >= location[0] && x <= location[0] + view.getWidth() &&
                y >= location[1] && y <= location[1] + view.getHeight();
    }

    // Define the interface for the preference change listener
    public interface OnPreferenceChangeListener {
        void onPreferenceChange(H2CO3EditTextPreference preference, String newValue);
    }
}