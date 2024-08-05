package org.koishi.launcher.h2co3.core.shell;

import android.app.Service;
import android.content.Intent;
import android.graphics.SurfaceTexture;
import android.os.Handler;
import android.os.IBinder;

import androidx.annotation.Nullable;

import org.koishi.launcher.h2co3.core.H2CO3Tools;
import org.koishi.launcher.h2co3.core.h2co3launcher.utils.H2CO3LauncherBridge;
import org.koishi.launcher.h2co3.core.h2co3launcher.utils.H2CO3LauncherBridgeCallBack;
import org.koishi.launcher.h2co3.core.h2co3launcher.utils.H2CO3LauncherHelper;
import org.koishi.launcher.h2co3.core.utils.Logging;
import org.koishi.launcher.h2co3.core.utils.file.FileTools;

import java.io.File;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.util.logging.Level;

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
        if (intent.getExtras() == null) {
            return super.onStartCommand(intent, flags, startId);
        }
        String[] command = intent.getExtras().getStringArray("command");
        int java = intent.getExtras().getInt("java");
        String jre = "jre" + java;
        startProcess(command, jre);
        return super.onStartCommand(intent, flags, startId);
    }

    public void startProcess(String[] command, String jre) {
        H2CO3LauncherBridge bridge = H2CO3LauncherHelper.launchAPIInstaller(H2CO3Tools.CONTEXT, command, jre);
        H2CO3LauncherBridgeCallBack callback = new H2CO3LauncherBridgeCallBack() {
            @Override
            public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {}

            @Override
            public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {}

            @Override
            public void onCursorModeChange(int mode) {}

            @Override
            public void onHitResultTypeChange(int type) {}

            @Override
            public void onLog(String log) {
                try {
                    File logFile = new File(bridge.getLogPath());
                    if (firstLog) {
                        FileTools.writeText(logFile, log + "\n");
                        firstLog = false;
                    } else {
                        FileTools.writeTextWithAppendMode(logFile, log + "\n");
                    }
                } catch (IOException e) {
                    Logging.LOG.log(Level.WARNING, "Can't log game log to target file", e);
                }
            }

            @Override
            public void onStart() {}

            @Override
            public void onPicOutput() {}

            @Override
            public void onError(Exception e) {}

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
                Logging.LOG.log(Level.SEVERE, "Execution failed", e);
            }
        }, 1000);
    }

    private void sendCode(int code) {
        try (DatagramSocket socket = new DatagramSocket()) {
            socket.connect(new InetSocketAddress("127.0.0.1", PROCESS_SERVICE_PORT));
            byte[] data = String.valueOf(code).getBytes();
            DatagramPacket packet = new DatagramPacket(data, data.length);
            socket.send(packet);
        } catch (IOException e) {
            Logging.LOG.log(Level.SEVERE, "Failed to send code", e);
        }
        stopSelf();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        android.os.Process.killProcess(android.os.Process.myPid());
    }
}