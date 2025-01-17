package org.koishi.launcher.h2co3.controller.ckb.support;

public class KeyboardRecorder {

    public final static int VERSION_UNKNOWN = 0;
    public final static int VERSION_0_1_3 = 1;
    public final static int VERSION_0_1_4_P = 2;
    public final static int VERSION_THIS = VERSION_0_1_4_P;

    private int screenWidth;
    private int screenHeight;
    private int versionCode;
    private GameButtonRecorder[] games;

    public void setScreenArgs(int sw, int sh) {
        this.screenWidth = sw;
        this.screenHeight = sh;
    }

    public GameButtonRecorder[] getRecorderDatas() {
        return games;
    }

    public void setRecorderDatas(GameButtonRecorder[] data) {
        this.games = data;
    }

    public int[] getScreenData() {
        return new int[]{screenWidth, screenHeight};
    }

    public int getVersionCode() {
        return this.versionCode;
    }

    public void setVersionCode(int version) {
        this.versionCode = version;
    }
}
