package org.koishi.launcher.h2co3.resources.component.activity;

import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.Gravity;
import android.view.ViewGroup;
import android.webkit.CookieManager;
import android.widget.FrameLayout;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.koishi.launcher.h2co3.core.H2CO3Tools;
import org.koishi.launcher.h2co3.core.utils.file.FileTools;
import org.koishi.launcher.h2co3.library.R;
import org.koishi.launcher.h2co3.core.message.H2CO3MessageManager;

import rikka.material.app.MaterialActivity;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class H2CO3Activity extends MaterialActivity {
    public H2CO3MessageManager h2co3MessageManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(Build.VERSION.SDK_INT >= 31 && H2CO3Tools.getH2CO3Value("enable_monet", true, Boolean.class)
                ? R.style.Theme_H2CO3_DynamicColors : R.style.Theme_H2CO3);
    }

    @Override
    public void onApplyTranslucentSystemBars() {
        super.onApplyTranslucentSystemBars();
        getWindow().setStatusBarColor(Color.TRANSPARENT);
    }

    protected void showMessageListView() {
        RecyclerView messageListView = new RecyclerView(this);
        messageListView.setBackgroundColor(Color.TRANSPARENT);

        int screenWidth = getResources().getDisplayMetrics().widthPixels;

        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                (int) (screenWidth / 3.3),
                ViewGroup.LayoutParams.WRAP_CONTENT
        );
        params.gravity = Gravity.BOTTOM | Gravity.END;

        FrameLayout rootLayout = findViewById(android.R.id.content);
        if (rootLayout != null) {
            rootLayout.addView(messageListView, params);
        }
        H2CO3MessageManager.NotificationAdapter adapter = new H2CO3MessageManager.NotificationAdapter(this, new ArrayList<>());
        h2co3MessageManager = new H2CO3MessageManager(adapter, messageListView);
        H2CO3Tools.setH2CO3MessageManager(h2co3MessageManager);
        messageListView.setLayoutManager(new LinearLayoutManager(this));
        messageListView.setAdapter(adapter);
    }

    public static void clearCacheFiles(Context context) {
        // Implement cache clearing logic or remove this method if not needed
    }

    public static void clearWebViewCache(Context context) throws IOException {
        File webViewDir = context.getDir("webview", 0);
        if (webViewDir != null && webViewDir.exists()) {
            FileTools.deleteDirectory(webViewDir);
        }
        CookieManager.getInstance().removeAllCookies(null);
    }
}