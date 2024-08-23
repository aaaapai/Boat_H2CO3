package org.koishi.launcher.h2co3.core.utils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Point;
import android.graphics.Rect;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Build;
import android.view.WindowManager;
import android.webkit.CookieManager;

import org.koishi.launcher.h2co3.core.utils.file.FileTools;

import java.io.File;
import java.util.Objects;

@SuppressLint("DiscouragedApi")
public class AndroidUtils {

    private static final String WEBVIEW_CACHE_DIR = "webview";
    private static final String THEME_SHARED_PREFS = "theme";
    private static final String FULLSCREEN_KEY = "fullscreen";

    public static void openLink(Context context, String link) {
        Uri uri = Uri.parse(link);
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        context.startActivity(intent);
    }

    public static void copyText(Context context, String text) {
        ClipboardManager clip = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData data = ClipData.newPlainText(null, text);
        clip.setPrimaryClip(data);
    }

    public static void clearWebViewCache(Context context) {
        File cache = context.getDir(WEBVIEW_CACHE_DIR, Context.MODE_PRIVATE);
        FileTools.deleteDirectoryQuietly(cache);
        CookieManager.getInstance().removeAllCookies(null);
    }

    public static String getLocalizedText(Context context, String key, Object... formatArgs) {
        return String.format(getLocalizedText(context, key), formatArgs);
    }

    public static String getLocalizedText(Context context, String key) {
        int resId = context.getResources().getIdentifier(key, "string", context.getPackageName());
        if (resId != 0) {
            return context.getString(resId);
        } else {
            return key;
        }
    }

    public static boolean hasStringId(Context context, String key) {
        int resId = context.getResources().getIdentifier(key, "string", context.getPackageName());
        return resId != 0;
    }

    public static int getScreenHeight(Context context) {
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Point point = new Point();
        wm.getDefaultDisplay().getRealSize(point);
        return point.y;
    }

    public static int getScreenWidth(Activity context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(THEME_SHARED_PREFS, Context.MODE_PRIVATE);
        boolean fullscreen = sharedPreferences.getBoolean(FULLSCREEN_KEY, false);
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Point point = new Point();
        wm.getDefaultDisplay().getRealSize(point);
        if (fullscreen) {
            return point.x;
        } else {
            try {
                Rect notchRect;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    notchRect = Objects.requireNonNull(wm.getCurrentWindowMetrics().getWindowInsets().getDisplayCutout()).getBoundingRects().get(0);
                } else {
                    notchRect = Objects.requireNonNull(context.getWindow().getDecorView().getRootWindowInsets().getDisplayCutout()).getBoundingRects().get(0);
                }
                return point.x - Math.min(notchRect.width(), notchRect.height());
            } catch (Exception e) {
                return point.x;
            }
        }
    }

    public static String getMimeType(String filePath) {
        MediaMetadataRetriever mmr = new MediaMetadataRetriever();
        String mime = "*/*";
        if (filePath != null) {
            mmr.setDataSource(filePath);
            mime = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_MIMETYPE);
        }
        return mime;
    }
}