/*
 * //
 * // Created by cainiaohh on 2024-03-31.
 * //
 */
package org.koishi.launcher.h2co3.resources.component.activity

import android.content.Context
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.webkit.CookieManager
import org.koishi.launcher.h2co3.core.H2CO3Tools
import org.koishi.launcher.h2co3.core.utils.file.FileTools
import org.koishi.launcher.h2co3.resources.R
import rikka.material.app.MaterialActivity
import java.io.File
import java.io.IOException

open class H2CO3Activity : MaterialActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //EdgeToEdge.enable(this);
        if (Build.VERSION.SDK_INT >= 31) {
            val spIsAuth = H2CO3Tools.getH2CO3Value("enable_monet", true, Boolean::class.java)
            if (spIsAuth) {
                setTheme(R.style.Theme_H2CO3_DynamicColors)
            } else {
                setTheme(R.style.Theme_H2CO3)
            }
        } else {
            setTheme(R.style.Theme_H2CO3)
        }
    }

    override fun onApplyTranslucentSystemBars() {
        super.onApplyTranslucentSystemBars()
        if (window != null) {
            window.statusBarColor = Color.TRANSPARENT
        }
    }

    companion object {
        fun clearCacheFiles(context: Context?) {
        }

        @JvmStatic
        @Throws(IOException::class)
        fun clearWebViewCache(context: Context) {
            val WEB_VIEW_CACHE_DIR = context.getDir("webview", 0).absolutePath
            FileTools.deleteDirectory(File(WEB_VIEW_CACHE_DIR))
            CookieManager.getInstance().removeAllCookies(null)
        }
    }
}
