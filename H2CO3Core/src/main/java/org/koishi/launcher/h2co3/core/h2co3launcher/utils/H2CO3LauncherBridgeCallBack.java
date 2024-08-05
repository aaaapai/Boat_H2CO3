/*
 * //
 * // Created by cainiaohh on 2024-03-31.
 * //
 */

package org.koishi.launcher.h2co3.core.h2co3launcher.utils;

import android.graphics.SurfaceTexture;

import java.io.IOException;

public interface H2CO3LauncherBridgeCallBack {
    void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height);

    void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height);

    void onCursorModeChange(int mode);

    void onLog(String log) throws IOException;
    void onStart();

    void onPicOutput();

    void onError(Exception e);

    void onExit(int code);

    void onHitResultTypeChange(int type);
}
