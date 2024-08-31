package org.koishi.launcher.h2co3.resources.component.preference;

import android.content.Context;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.PopupMenu;

import org.koishi.launcher.h2co3.library.R;
import org.koishi.launcher.h2co3.resources.component.H2CO3CardView;

public class H2CO3ListPreference extends H2CO3CardView {

    private TextView titleView;
    private TextView valueView;
    private H2CO3CardView rootView;
    private PopupMenu popupMenu;
    private String[] entries;
    private int checkedItem;
    private OnListPreferenceChangeListener onListPreferenceChangeListener;

    public H2CO3ListPreference(@NonNull Context context) {
        super(context);
        init(context, null);
    }

    public H2CO3ListPreference(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    private void init(@NonNull Context context, @Nullable AttributeSet attrs) {
        // Inflate the custom layout
        LayoutInflater.from(context).inflate(R.layout.preference_custom_list, this, true);
        setBackgroundResource(android.R.color.transparent);
        setStrokeWidth(0);

        // Initialize views
        titleView = findViewById(R.id.textViewTitle);
        valueView = findViewById(R.id.textViewValue);
        rootView = findViewById(R.id.listPreferenceCard);

        // Set up the click listener for the card to show the popup menu
        rootView.setOnClickListener(v -> showPopupMenu());
    }

    private void showPopupMenu() {
        if (popupMenu != null) {
            popupMenu.show();
        } else {
            popupMenu = new PopupMenu(getContext(), rootView, Gravity.NO_GRAVITY, android.R.attr.listPopupWindowStyle, 0); // 设置样式
            for (int i = 0; i < entries.length; i++) {
                popupMenu.getMenu().add(Menu.NONE, i, i, entries[i]);
            }
            popupMenu.setOnMenuItemClickListener(item -> {
                int index = item.getOrder();
                checkedItem = index;
                valueView.setText(entries[index]);
                if (onListPreferenceChangeListener != null) {
                    onListPreferenceChangeListener.onListPreferenceChange(this, entries[index]);
                }
                return true;
            });
            popupMenu.show();
        }
    }

    public void setEntries(@NonNull String[] entries, @Nullable OnListPreferenceChangeListener listener) {
        this.entries = entries;
        this.onListPreferenceChangeListener = listener;
        popupMenu = new PopupMenu(getContext(), rootView);
        for (int i = 0; i < entries.length; i++) {
            popupMenu.getMenu().add(Menu.NONE, i, i, entries[i]);
        }
        popupMenu.setOnMenuItemClickListener(item -> {
            int index = item.getOrder();
            checkedItem = index;
            valueView.setText(entries[index]);
            if (onListPreferenceChangeListener != null) {
                onListPreferenceChangeListener.onListPreferenceChange(this, entries[index]);
            }
            return true;
        });
    }
    // Set the title of the preference
    public void setTitle(String title) {
        titleView.setText(title);
    }

    public void setValue(String value) {
        valueView.setText(value);
    }

    // Set the preference change listener
    public void setOnListPreferenceChangeListener(OnListPreferenceChangeListener listener) {
        onListPreferenceChangeListener = listener;
    }

    // Define the interface for the preference change listener
    public interface OnListPreferenceChangeListener {
        void onListPreferenceChange(H2CO3ListPreference preference, String newValue);
    }
}