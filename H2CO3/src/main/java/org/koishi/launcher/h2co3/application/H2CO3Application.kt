package org.koishi.launcher.h2co3.application

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Application
import android.app.Application.ActivityLifecycleCallbacks
import android.content.res.Configuration
import android.os.Bundle
import cat.ereza.customactivityoncrash.CustomActivityOnCrash
import cat.ereza.customactivityoncrash.config.CaocConfig
import com.orhanobut.logger.Logger
import org.koishi.launcher.h2co3.core.H2CO3Tools
import org.koishi.launcher.h2co3.resources.R
import org.koishi.launcher.h2co3.ui.CrashActivity
import java.util.concurrent.ExecutorService
import java.util.concurrent.LinkedBlockingQueue
import java.util.concurrent.ThreadPoolExecutor
import java.util.concurrent.TimeUnit

class H2CO3Application : Application(), ActivityLifecycleCallbacks {

    override fun onCreate() {
        super.onCreate()
        this.registerActivityLifecycleCallbacks(this)
        H2CO3Tools.loadPaths(this)
        CaocConfig.Builder.create()
            .backgroundMode(CaocConfig.BACKGROUND_MODE_SHOW_CUSTOM)
            .enabled(true)
            .showErrorDetails(false)
            .showRestartButton(false)
            .trackActivities(true)
            .minTimeBetweenCrashesMs(2000)
            .errorDrawable(R.drawable.ic_boat)
            .errorActivity(CrashActivity::class.java)
            .eventListener(CustomEventListener())
            .apply()
    }

    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
        currentActivity = activity
    }

    override fun onActivityStarted(activity: Activity) {
        currentActivity = activity
    }

    override fun onActivityResumed(activity: Activity) {
    }

    override fun onActivityPaused(activity: Activity) {
    }

    override fun onActivityStopped(activity: Activity) {
    }

    override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {
    }

    override fun onActivityDestroyed(activity: Activity) {
    }

    private class CustomEventListener : CustomActivityOnCrash.EventListener {
        override fun onLaunchErrorActivity() {
            Logger.e(TAG, "onLaunchErrorActivity()")
        }

        override fun onRestartAppFromErrorActivity() {
            Logger.e(TAG, "onRestartAppFromErrorActivity()")
        }

        override fun onCloseAppFromErrorActivity() {
            Logger.e(TAG, "onCloseAppFromErrorActivity()")
        }

        companion object {
            private const val TAG = "Boat_H2CO3"
        }
    }

    companion object {
        @SuppressLint("StaticFieldLeak")
        val instance: H2CO3Application = H2CO3Application()
        @JvmField
        val sExecutorService: ExecutorService =
            ThreadPoolExecutor(4, 4, 500, TimeUnit.MILLISECONDS, LinkedBlockingQueue())
        @SuppressLint("StaticFieldLeak")
        var currentActivity: Activity? = null
    }
}