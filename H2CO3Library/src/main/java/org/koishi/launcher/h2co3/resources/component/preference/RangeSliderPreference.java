package org.koishi.launcher.h2co3.resources.component.preference;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.card.MaterialCardView;
import com.google.android.material.slider.LabelFormatter;
import com.google.android.material.slider.RangeSlider;

import org.koishi.launcher.h2co3.library.R;
import org.koishi.launcher.h2co3.resources.component.H2CO3CardView;

import java.util.List;

public class RangeSliderPreference extends H2CO3CardView {

    private RangeSlider rangeSlider;
    private TextView titleView;
    private TextView valueFromView;
    private TextView valueToView;
    private MaterialCardView rootView;
    private OnRangePreferenceChangeListener onRangeChangeListener;

    public RangeSliderPreference(@NonNull Context context) {
        super(context);
        init(context, null);
    }

    public RangeSliderPreference(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public void setOnRangeChangeListener(OnRangePreferenceChangeListener listener) {
        this.onRangeChangeListener = listener;
    }

    @SuppressLint("SetTextI18n")
    private void init(@NonNull Context context, @Nullable AttributeSet attrs) {
        // Inflate the custom layout
        LayoutInflater.from(context).inflate(R.layout.preference_custom_rangeslider, this, true);
        setBackgroundResource(android.R.color.transparent);
        setStrokeWidth(0);

        // Initialize views
        titleView = findViewById(R.id.textViewTitle);
        rangeSlider = findViewById(R.id.range_slider);
        valueFromView = findViewById(R.id.textViewValueFrom);
        valueToView = findViewById(R.id.textViewValueTo);
        rootView = findViewById(R.id.root_view);

        // Set up the RangeSlider
        rangeSlider.setLabelFormatter(new LabelFormatter() {
            @NonNull
            @Override
            public String getFormattedValue(float value) {
                return String.valueOf((int) value);
            }
        });

        valueFromView.setText("From: " + (float) getValues().get(0));
        valueToView.setText("To: " + (float) getValues().get(1));

        // Update the displayed values when the slider is moved
        rangeSlider.addOnChangeListener(
            (slider, value, fromUser) -> {
                valueFromView.setText("From: " + (float) getValues().get(0));
                valueToView.setText("To: " + (float) getValues().get(1));
                if (onRangeChangeListener != null) {
                    onRangeChangeListener.onPreferenceChange(this, getValues().get(0), getValues().get(1));
                }
            }
        );
    }

    // Set the title of the preference
    public void setTitle(String title) {
        titleView.setText(title);
    }

    // Get the selected range values
    public List<Float> getValues() {
        return rangeSlider.getValues();
    }

    public void setInitValues(float valueFrom, float valueTo) {
        rangeSlider.setValueFrom(valueFrom);
        rangeSlider.setValueTo(valueTo);
    }

    // Set the range values
    public void setValues(float valueFrom, float valueTo) {
        rangeSlider.setValues(valueFrom, valueTo);
        valueFromView.setText(String.valueOf((int) valueFrom));
        valueToView.setText(String.valueOf((int) valueTo));
    }

    public void setStepSize(float size) {
        rangeSlider.setStepSize(size);
    }

    public interface OnRangePreferenceChangeListener {
        void onPreferenceChange(RangeSliderPreference preference, float valueFrom, float valueTo);
    }
}