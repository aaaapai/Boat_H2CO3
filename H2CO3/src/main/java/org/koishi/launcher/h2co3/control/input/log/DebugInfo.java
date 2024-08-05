/*
 * //
 * // Created by cainiaohh on 2024-03-31.
 * //
 */

package org.koishi.launcher.h2co3.control.input.log;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import org.koishi.launcher.h2co3.control.controller.Controller;
import org.koishi.launcher.h2co3.control.input.Input;
import org.koishi.launcher.h2co3.core.H2CO3Tools;
import org.koishi.launcher.h2co3.core.h2co3launcher.utils.H2CO3LauncherBridge;
import org.koishi.launcher.h2co3.core.utils.DisplayUtils;
import org.koishi.launcher.h2co3.core.utils.file.FileTools;
import org.koishi.launcher.h2co3.resources.component.LogView;
import org.koishi.launcher.h2co3.ui.H2CO3LauncherClientActivity;

import java.io.File;
import java.io.IOException;
import java.lang.ref.WeakReference;

public class DebugInfo implements Input, View.OnClickListener {
    private final static String TAG = "DebugInfo";
    public H2CO3LauncherBridge.LogReceiver mReceiver;
    private Controller mController;
    private boolean isEnabled;
    private Context mContext;
    //private Button switchButton;
    private LogView mLogView;
    //private boolean isShowInfo = true;
    private boolean firstWrite = true;
    private boolean isWrite = true;

    @Override
    public boolean unload() {
        ViewGroup vg = (ViewGroup) mLogView.getParent();
        vg.removeView(mLogView);
        return true;
    }

    @Override
    public void setGrabCursor(boolean isGrabbed) {

    }

    @Override
    public void runConfigure() {

    }

    @Override
    public void saveConfig() {

    }

    @Override
    public boolean isEnabled() {
        return this.isEnabled;
    }

    @Override
    public void setEnabled(boolean enabled) {
        this.isEnabled = enabled;
        if (enabled) {
            mLogView.setVisibility(View.VISIBLE);
        } else {
            mLogView.setVisibility(View.GONE);
        }

    }

    @Override
    public boolean load(Context context, Controller controller, H2CO3LauncherBridge bridge) {
        this.mContext = context;
        this.mController = controller;
        //this.switchButton = new Button(mContext);

        /*switchButton.setBackground(ContextCompat.getDrawable(mContext, R.drawable.background_floatbutton));
        switchButton.setLayoutParams(new ViewGroup.LayoutParams(DisplayUtils.getPxFromDp(mContext, 30), DisplayUtils.getPxFromDp(mContext, 30)));
        mController.addView(switchButton);
        switchButton.setX(mController.getConfig().getScreenWidth() - switchButton.getLayoutParams().width - DisplayUtils.getPxFromDp(mContext, 30));
        switchButton.setY(0);
        switchButton.setOnClickListener(this);
         */

        mLogView = new LogView(mContext);
        mLogView.setLayoutParams(new ViewGroup.LayoutParams(mController.getConfig().getScreenWidth() - DisplayUtils.getPxFromDp(mContext, 10), mController.getConfig().getScreenHeight() / 2 - DisplayUtils.getPxFromDp(mContext, 30)));
        mController.addView(mLogView);
        mLogView.setX(0);
        mLogView.setY(mController.getConfig().getScreenHeight() - mLogView.getLayoutParams().height);

        if (H2CO3LauncherClientActivity.logReceiver == null || H2CO3LauncherClientActivity.logReceiver.get() == null) {
            mReceiver = new H2CO3LauncherBridge.LogReceiver() {
                final StringBuilder stringBuilder = new StringBuilder();

                @Override
                public void pushLog(String log) {
                    mLogView.appendLog(log);
                    stringBuilder.append(log);
                    writeLog(log);
                }

                @Override
                public String getLogs() {
                    return stringBuilder.toString();
                }
            };
            H2CO3LauncherClientActivity.logReceiver = new WeakReference<>(mReceiver);
        }

        return true;
    }

    @Override
    public void onPaused() {

    }

    @Override
    public void onResumed() {

    }

    @Override
    public Controller getController() {
        return mController;
    }

    @Override
    public void onClick(View v) {
    }

    private void writeLog(String log) {
        if (!isWrite) {
            return;
        }
        File logFile = new File(H2CO3Tools.LOG_DIR + "/client_output.txt");
        if (!logFile.exists()) {
            try {
                if (!logFile.createNewFile()) {
                    isWrite = false;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (firstWrite) {
            FileTools.writeData(logFile.getAbsolutePath(), log);
            firstWrite = false;
        } else {
            FileTools.addStringLineToFile(log, logFile);
        }
    }
}
