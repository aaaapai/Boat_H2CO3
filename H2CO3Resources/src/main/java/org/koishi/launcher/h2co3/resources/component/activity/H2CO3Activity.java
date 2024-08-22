package org.koishi.launcher.h2co3.resources.component.activity;

import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.CookieManager;

import org.koishi.launcher.h2co3.core.H2CO3Tools;
import org.koishi.launcher.h2co3.core.utils.file.FileTools;
import org.koishi.launcher.h2co3.resources.R;

import rikka.material.app.MaterialActivity;

import java.io.File;
import java.io.IOException;

public class H2CO3Activity extends MaterialActivity {
    private View dialogBg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (Build.VERSION.SDK_INT >= 31) {
            boolean spIsAuth = H2CO3Tools.getH2CO3Value("enable_monet", true, Boolean.class);
            setTheme(spIsAuth ? R.style.Theme_H2CO3_DynamicColors : R.style.Theme_H2CO3);
        } else {
            setTheme(R.style.Theme_H2CO3);
        }
    }

    @Override
    public void onApplyTranslucentSystemBars() {
        super.onApplyTranslucentSystemBars();
        if (getWindow() != null) {
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }
    }

    public static void clearCacheFiles(Context context) {

    }

    public static void clearWebViewCache(Context context) throws IOException {
        String WEB_VIEW_CACHE_DIR = context.getDir("webview", 0).getAbsolutePath();
        FileTools.deleteDirectory(new File(WEB_VIEW_CACHE_DIR));
        CookieManager.getInstance().removeAllCookies(null);
    }
}