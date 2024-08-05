package org.koishi.launcher.h2co3.ui;

import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;

import org.koishi.launcher.h2co3.R;
import org.koishi.launcher.h2co3.resources.component.activity.H2CO3Activity;

public class WelcomeActivity extends H2CO3Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
            }
        };
        getOnBackPressedDispatcher().addCallback(this, callback);
    }

}