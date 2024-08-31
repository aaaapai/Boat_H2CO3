package org.koishi.launcher.h2co3.resources.component.preference;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.slider.Slider;

import org.koishi.launcher.h2co3.library.R;
import org.koishi.launcher.h2co3.resources.component.H2CO3CardView;

public class H2CO3SliderPreference extends H2CO3CardView {

    private Slider slider;
    private TextView titleView;
    private TextView valueView;
    private OnPreferenceChangeListener listener;

    public H2CO3SliderPreference(@NonNull Context context) {
        super(context);
        init(context, null);
    }

    public H2CO3SliderPreference(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    private void init(@NonNull Context context, @Nullable AttributeSet attrs) {
        // Inflate the custom layout
        LayoutInflater.from(context).inflate(R.layout.preference_custom_slider, this, true);
        setBackgroundResource(android.R.color.transparent);
        setStrokeWidth(0);

        // Initialize views
        slider = findViewById(R.id.slider);
        titleView = findViewById(R.id.textViewTitle);

        valueView = findViewById(R.id.textViewValue);

        // Set up the slider change listener
        slider.addOnChangeListener((slider1, value, fromUser) -> {
            valueView.setText(String.valueOf((int)value));

            if (listener != null) {
                listener.onPreferenceChange(H2CO3SliderPreference.this, value);
            }
        });
    }

    public void setOnPreferenceChangeListener(OnPreferenceChangeListener listener) {
        this.listener = listener;
    }

    // Set the title of the preference
    public void setTitle(String title) {
        titleView.setText(title);
    }

    // Get the current value of the slider
    public float getSliderValue() {
        return slider.getValue();
    }

    // Set the value of the slider
    public void setSliderValue(float value) {
        slider.setValue(value);
        valueView.setText(String.valueOf((int) value));
    }

    public void setStepSize(int size){
        slider.setStepSize(size);
    }

    public void initValue(float from, float to){
        slider.setValueFrom(from);
        slider.setValueTo(to);
    }

    public interface OnPreferenceChangeListener {
        void onPreferenceChange(H2CO3SliderPreference preference, float newValue);
    }
}