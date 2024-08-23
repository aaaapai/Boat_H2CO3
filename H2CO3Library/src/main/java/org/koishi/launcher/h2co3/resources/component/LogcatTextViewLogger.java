/*
 * //
 * // Created by cainiaohh on 2024-03-31.
 * //
 */

package org.koishi.launcher.h2co3.resources.component;

import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class LogcatTextViewLogger implements Runnable {

    private static final String TAG = "LogcatTextViewLogger";
    private static final int BUFFER_SIZE = 4096;

    private final LogcatView textView;
    private Process logcatProcess;
    private boolean running;

    public LogcatTextViewLogger(LogcatView textView) {
        this.textView = textView;
    }

    public static void setupLogger(LogcatTextViewLogger logger) {
        Thread thread = new Thread(logger);
        thread.start();
    }

    @Override
    public void run() {
        try {
            logcatProcess = Runtime.getRuntime().exec("logcat");
            BufferedReader reader = new BufferedReader(new InputStreamReader(logcatProcess.getInputStream()));
            StringBuilder log = new StringBuilder();
            char[] buffer = new char[BUFFER_SIZE];
            int bytesRead;
            running = true;

            while (running && (bytesRead = reader.read(buffer)) != -1) {
                log.append(buffer, 0, bytesRead);
                int newlineIndex;
                while ((newlineIndex = log.indexOf("\n")) != -1) {
                    final String line = log.substring(0, newlineIndex);
                    log.delete(0, newlineIndex + 1);
                    textView.post(() -> appendColoredText(line));
                }
            }
        } catch (IOException e) {
            Log.e(TAG, "Error reading logcat", e);
        }
    }

    private void appendColoredText(String line) {
        textView.addLog(line);
    }

    public void stop() {
        running = false;
        if (logcatProcess != null) {
            logcatProcess.destroy();
        }
    }
}