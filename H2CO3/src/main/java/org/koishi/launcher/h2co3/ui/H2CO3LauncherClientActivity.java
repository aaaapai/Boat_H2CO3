/*
 * //
 * // Created by cainiaohh on 2024-04-04.
 * //
 */

/*
 * //
 * // Created by cainiaohh on 2024-03-31.
 * //
 */

package org.koishi.launcher.h2co3.ui;

import static org.koishi.launcher.h2co3.core.h2co3launcher.utils.H2CO3LauncherHelper.launchMinecraft;

import android.content.Context;
import android.graphics.SurfaceTexture;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;

import org.koishi.launcher.h2co3.control.client.H2CO3ControlClient;
import org.koishi.launcher.h2co3.control.controller.H2CO3VirtualController;
import org.koishi.launcher.h2co3.control.controller.HardwareController;
import org.koishi.launcher.h2co3.core.h2co3launcher.utils.H2CO3GameHelper;
import org.koishi.launcher.h2co3.core.h2co3launcher.utils.H2CO3LauncherBridge;
import org.koishi.launcher.h2co3.core.h2co3launcher.utils.H2CO3LauncherBridgeCallBack;
import org.koishi.launcher.h2co3.core.h2co3launcher.utils.MCOptionUtils;
import org.koishi.launcher.h2co3.core.utils.DisplayUtils;
import org.koishi.launcher.h2co3.core.utils.Logging;
import org.koishi.launcher.h2co3.launcher.H2CO3LauncherActivity;
import org.koishi.launcher.h2co3.launcher.R;
import org.koishi.launcher.h2co3.resources.component.activity.H2CO3Activity;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.logging.Level;

/**
 * @author caini
 */
public class H2CO3LauncherClientActivity extends H2CO3LauncherActivity implements H2CO3ControlClient, TextureView.SurfaceTextureListener {

    private static final int CURSOR_SIZE = 16;
    private static final int[] GRABBED_POINTER = new int[]{0, 0};
    public static WeakReference<H2CO3LauncherBridge.LogReceiver> logReceiver;
    private boolean grabbed = false;
    private ImageView cursorIcon;
    private int screenWidth;
    private int screenHeight;
    private int output = 0;

    public static void receiveLog(String str) throws IOException {
        if (logReceiver == null || logReceiver.get() == null) {
            logReceiver = new WeakReference<>(new H2CO3LauncherBridge.LogReceiver() {
                final StringBuilder builder = new StringBuilder();

                @Override
                public void pushLog(String log) {
                    builder.append(log);
                }

                @Override
                public String getLogs() {
                    return builder.toString();
                }
            });
        } else {
            logReceiver.get().pushLog(str);
        }
    }

    public static void attachControllerInterface() {
        H2CO3LauncherClientActivity.h2co3LauncherInterface = new IH2CO3Launcher() {
            private H2CO3VirtualController virtualController;
            private HardwareController hardwareController;

            @Override
            public void onActivityCreate(H2CO3LauncherActivity H2CO3LauncherActivity) {
                virtualController = new H2CO3VirtualController((H2CO3ControlClient) H2CO3LauncherActivity, H2CO3LauncherActivity.launcherLib, KEYMAP_TO_X);
                hardwareController = new HardwareController((H2CO3ControlClient) H2CO3LauncherActivity, H2CO3LauncherActivity.launcherLib, KEYMAP_TO_X);
            }

            @Override
            public void setGrabCursor(boolean isGrabbed) {
                virtualController.setGrabCursor(isGrabbed);
                hardwareController.setGrabCursor(isGrabbed);
            }

            @Override
            public void onStop() {
                virtualController.onStop();
                hardwareController.onStop();
            }

            @Override
            public void onResume() {
                virtualController.onResumed();
                hardwareController.onResumed();
            }

            @Override
            public void onPause() {
                virtualController.onPaused();
                hardwareController.onPaused();
            }

            @Override
            public boolean dispatchKeyEvent(KeyEvent event) {
                return hardwareController.dispatchKeyEvent(event);
            }

            @Override
            public boolean dispatchGenericMotionEvent(MotionEvent event) {
                return hardwareController.dispatchMotionKeyEvent(event);
            }
        };
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.overlay);
        mainTextureView = findViewById(R.id.main_game_render_view);
        mainTextureView.setSurfaceTextureListener(this);
        baseLayout = findViewById(R.id.main_base);
        screenWidth = getResources().getDisplayMetrics().widthPixels;
        screenHeight = getResources().getDisplayMetrics().heightPixels;
        cursorIcon = new ImageView(this);
        cursorIcon.setLayoutParams(new ViewGroup.LayoutParams(DisplayUtils.getPxFromDp(this, CURSOR_SIZE), DisplayUtils.getPxFromDp(this, CURSOR_SIZE)));
        cursorIcon.setImageResource(org.koishi.launcher.h2co3.resources.R.drawable.cursor5);
        this.addView(cursorIcon);
        try {
            launcherLib = launchMinecraft(this, screenWidth, screenHeight);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        h2co3LauncherCallback = launcherLib.getCallback();
        init();
    }

    private void init() {
        h2co3LauncherInterface.onActivityCreate(this);
        h2co3LauncherCallback = new H2CO3LauncherBridgeCallBack() {
            @Override
            public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {

            }

            @Override
            public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {

            }

            @Override
            public void onCursorModeChange(int mode) {
                runOnUiThread(() -> setGrabCursor(mode == H2CO3LauncherBridge.CursorEnabled));
            }

            @Override
            public void onLog(String log) throws IOException {
                if (log.contains("OR:") || log.contains("ERROR:") || log.contains("INTERNAL ERROR:")) {
                    return;
                }
                receiveLog(log);
            }

            @Override
            public void onStart() {

            }

            @Override
            public void onPicOutput() {

            }

            @Override
            public void onError(Exception e) {

            }

            @Override
            public void onExit(int code) {
                ExitActivity.showExitMessage(H2CO3LauncherClientActivity.this, code);
            }

            @Override
            public void onHitResultTypeChange(int type) {

            }
        };
    }

    @Override
    public void onSurfaceTextureSizeChanged(@NonNull SurfaceTexture surfaceTexture, int i, int i1) {
        int width = screenWidth;
        int height = screenHeight;
        surfaceTexture.setDefaultBufferSize(width, height);
        launcherLib.pushEventWindow(width, height);
    }

    @Override
    public boolean onSurfaceTextureDestroyed(@NonNull SurfaceTexture surfaceTexture) {
        launcherLib.setSurfaceDestroyed(true);
        return false;
    }

    @Override
    public void onSurfaceTextureUpdated(@NonNull SurfaceTexture surfaceTexture) {
        if (mainTextureView != null && mainTextureView.getSurfaceTexture() != null) {
            mainTextureView.post(() -> onSurfaceTextureSizeChanged(mainTextureView.getSurfaceTexture(), mainTextureView.getWidth(), mainTextureView.getHeight()));
        }
        if (output < 1) {
            output++;
        }
    }

    private void configureSurfaceTexture(SurfaceTexture surface, int width, int height) {
        int scaleFactor = 1;
        surface.setDefaultBufferSize(width * scaleFactor, height * scaleFactor);
        MCOptionUtils.saveOptions(H2CO3GameHelper.getGameDirectory());
        MCOptionUtils.setOption("overrideWidth", String.valueOf(width * scaleFactor));
        MCOptionUtils.setOption("overrideHeight", String.valueOf(height * scaleFactor));
        MCOptionUtils.setOption("fullscreen", "true");
        MCOptionUtils.saveOptions(H2CO3GameHelper.getGameDirectory());
    }

    @Override
    public void onClick(View p1) {
    }

    @Override
    public void exit(Context context, int code) {
        super.exit(context, code);
        ExitActivity.showExitMessage(context, code);
    }

    @Override
    public void setKey(int keyCode, boolean pressed) {
        this.setKey(keyCode, 0, pressed);
    }

    @Override
    public void onSurfaceTextureAvailable(@NonNull SurfaceTexture surfaceTexture, int i, int i1) {
        Logging.LOG.log(Level.INFO, "surface ready, start jvm now!");
        launcherLib.setSurfaceDestroyed(false);
        int width = screenWidth;
        int height = screenHeight;
        configureSurfaceTexture(surfaceTexture, width, height);
        surfaceTexture.setDefaultBufferSize(width, height);
        try {
            launcherLib.execute(new Surface(surfaceTexture), h2co3LauncherCallback);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        launcherLib.pushEventWindow(width, height);
    }

    @Override
    public void setPointerInc(int xInc, int yInc) {
        if (!grabbed) {
            int x = GRABBED_POINTER[0] + xInc;
            int y = GRABBED_POINTER[1] + yInc;
            if (x >= 0 && x <= screenWidth) {
                GRABBED_POINTER[0] += xInc;
            }
            if (y >= 0 && y <= screenHeight) {
                GRABBED_POINTER[1] += yInc;
            }
            setPointer(GRABBED_POINTER[0], GRABBED_POINTER[1]);
            cursorIcon.setX(GRABBED_POINTER[0]);
            cursorIcon.setY(GRABBED_POINTER[1]);
        } else {
            setPointer(getPointer()[0] + xInc, getPointer()[1] + yInc);
        }
    }

    @Override
    public void setPointer(int x, int y) {
        super.setPointer(x, y);
        if (!grabbed) {
            cursorIcon.setX(x);
            cursorIcon.setY(y);
            GRABBED_POINTER[0] = x;
            GRABBED_POINTER[1] = y;
        }
    }

    @Override
    public void addView(View v) {
        this.addContentView(v, v.getLayoutParams());
    }

    @Override
    public H2CO3Activity getActivity() {
        return this;
    }

    @Override
    public void typeWords(String str) {
        if (str == null) {
            return;
        }
        for (int i = 0; i < str.length(); i++) {
            setKey(0, str.charAt(i), true);
            setKey(0, str.charAt(i), false);
        }
    }

    @Override
    public int[] getLoosenPointer() {
        return getPointer().clone();
    }

    @Override
    public ViewGroup getViewsParent() {
        return (ViewGroup) findViewById(android.R.id.content).getRootView();
    }

    @Override
    public View getSurfaceLayerView() {
        return mainTextureView;
    }

    @Override
    public boolean isGrabbed() {
        return grabbed;
    }

    @Override
    public int[] getGrabbedPointer() {
        return GRABBED_POINTER.clone();
    }

    @Override
    public void setGrabCursor(boolean isGrabbed) {
        super.setGrabCursor(isGrabbed);
        grabbed = isGrabbed;
        if (!isGrabbed) {
            setPointer(GRABBED_POINTER[0], GRABBED_POINTER[1]);
            cursorIcon.setVisibility(View.VISIBLE);
        } else if (cursorIcon.getVisibility() == View.VISIBLE) {
            cursorIcon.setVisibility(View.INVISIBLE);
        }
    }
}