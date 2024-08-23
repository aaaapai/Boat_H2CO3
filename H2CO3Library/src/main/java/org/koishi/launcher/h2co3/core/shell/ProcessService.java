package org.koishi.launcher.h2co3.core.shell;

import android.app.Service;
import android.content.Intent;
import android.graphics.SurfaceTexture;
import android.os.Handler;
import android.os.IBinder;

import androidx.annotation.Nullable;

import org.koishi.launcher.h2co3.core.H2CO3Tools;
import org.koishi.launcher.h2co3.core.game.h2co3launcher.H2CO3GameHelper;
import org.koishi.launcher.h2co3.core.game.h2co3launcher.H2CO3LauncherBridge;
import org.koishi.launcher.h2co3.core.game.h2co3launcher.H2CO3LauncherBridgeCallBack;
import org.koishi.launcher.h2co3.core.game.h2co3launcher.H2CO3LauncherHelper;
import org.koishi.launcher.h2co3.core.message.H2CO3MessageManager;
import org.koishi.launcher.h2co3.core.utils.file.FileTools;

import java.io.File;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;

public class ProcessService extends Service {

    public static final int PROCESS_SERVICE_PORT = 29118;
    private boolean firstLog = true;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        String[] command = intent.getExtras().getStringArray("command");
        int java = intent.getExtras().getInt("java");
        String jre = "jre" + java;
        startProcess(command, new H2CO3GameHelper(), jre);
        return super.onStartCommand(intent, flags, startId);
    }

    public void startProcess(String[] command, H2CO3GameHelper gameHelper, String jre) {
        H2CO3LauncherBridge bridge = H2CO3LauncherHelper.launchAPIInstaller(H2CO3Tools.CONTEXT, gameHelper, command, jre);
        H2CO3LauncherBridgeCallBack callback = new H2CO3LauncherBridgeCallBack() {
            /**
             */
            @Override
            public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {

            }

            /**
             */
            @Override
            public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {

            }

            @Override
            public void onCursorModeChange(int mode) {
                // Ignore
            }

            @Override
            public void onHitResultTypeChange(int type) {
                // Ignore
            }

            @Override
            public void onLog(String log) {
                try {
                    if (firstLog) {
                        FileTools.writeText(new File(bridge.getLogPath()), log + "\n");
                        firstLog = false;
                    } else {
                        FileTools.writeTextWithAppendMode(new File(bridge.getLogPath()), log + "\n");
                    }
                } catch (IOException e) {
                    H2CO3Tools.showError(H2CO3MessageManager.NotificationItem.Type.ERROR, e.getMessage());
                }
            }

            /**
             *
             */
            @Override
            public void onStart() {

            }

            /**
             *
             */
            @Override
            public void onPicOutput() {

            }

            /**
             */
            @Override
            public void onError(Exception e) {
                H2CO3Tools.showError(H2CO3MessageManager.NotificationItem.Type.ERROR, e.getMessage());
            }

            @Override
            public void onExit(int code) {
                sendCode(code);
            }
        };
        Handler handler = new Handler();
        handler.postDelayed(() -> {
            try {
                bridge.execute(null, callback);
            } catch (IOException e) {
                H2CO3Tools.showError(H2CO3MessageManager.NotificationItem.Type.ERROR, e.getMessage());
            }
        }, 1000);
    }

    private void sendCode(int code) {
        try {
            DatagramSocket socket = new DatagramSocket();
            socket.connect(new InetSocketAddress("127.0.0.1", PROCESS_SERVICE_PORT));
            byte[] data = (code + "").getBytes();
            DatagramPacket packet = new DatagramPacket(data, data.length);
            socket.send(packet);
            socket.close();
        } catch (Exception e) {
            H2CO3Tools.showError(H2CO3MessageManager.NotificationItem.Type.ERROR, e.getMessage());
        }
        stopSelf();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        android.os.Process.killProcess(android.os.Process.myPid());
    }
}
