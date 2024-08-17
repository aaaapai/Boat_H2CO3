package org.koishi.launcher.h2co3.core.game.h2co3launcher;

import org.koishi.launcher.h2co3.core.H2CO3Tools;

import java.util.List;
import java.util.Arrays;

public class H2CO3GameHelper {

    private final List<String> extraJavaFlags;
    private final List<String> extraMinecraftFlags;

    public H2CO3GameHelper() {
        this.extraJavaFlags = List.of();
        this.extraMinecraftFlags = List.of();
    }

    public String getRender() {
        return H2CO3Tools.getH2CO3Value("h2co3_launcher_render", H2CO3Tools.GL_GL114, String.class);
    }

    public void setRender(String path) {
        H2CO3Tools.setH2CO3Value("h2co3_launcher_render", path);
    }

    public String getJavaPath() {
        return H2CO3Tools.getH2CO3LauncherValue("h2co3_launcher_java", H2CO3Tools.JAVA_8_PATH, String.class);
    }

    public void setJavaPath(String path) {
        H2CO3Tools.setH2CO3LauncherValue("h2co3_launcher_java", path);
    }

    public String getGameDirectory() {
        return H2CO3Tools.getH2CO3Value("game_directory", H2CO3Tools.MINECRAFT_DIR, String.class);
    }

    public void setGameDirectory(String directory) {
        H2CO3Tools.setH2CO3Value("game_directory", directory);
    }

    public String getGameAssetsRoot() {
        return H2CO3Tools.getH2CO3Value("game_assets_root", H2CO3Tools.MINECRAFT_DIR + "/assets/", String.class);
    }

    public void setGameAssetsRoot(String assetsRoot) {
        H2CO3Tools.setH2CO3Value("game_assets_root", assetsRoot);
    }

    public String getExtraMinecraftFlags() {
        return H2CO3Tools.getH2CO3LauncherValue("extra_minecraft_flags", "", String.class);
    }

    public void setExtraMinecraftFlags(String minecraftFlags) {
        H2CO3Tools.setH2CO3LauncherValue("extra_minecraft_flags", minecraftFlags);
    }

    public String getGameCurrentVersion() {
        return H2CO3Tools.getH2CO3Value("current_version", "null", String.class);
    }

    public void setGameCurrentVersion(String version) {
        H2CO3Tools.setH2CO3Value("current_version", version);
    }

    public String getRuntimePath() {
        return H2CO3Tools.getH2CO3Value("runtime_path", H2CO3Tools.RUNTIME_DIR, String.class);
    }

    public void setRuntimePath(String path) {
        H2CO3Tools.setH2CO3Value("runtime_path", path);
    }

    public String getH2CO3Home() {
        return H2CO3Tools.getH2CO3Value("h2co3_home", H2CO3Tools.PUBLIC_FILE_PATH, String.class);
    }

    public void setGameAssets(String assets) {
        H2CO3Tools.setH2CO3Value("game_assets", assets);
    }

    public String getBackground() {
        return H2CO3Tools.getH2CO3Value("background", "", String.class);
    }

    public void setH2CO3Home(String home) {
        H2CO3Tools.setH2CO3Value("h2co3_home", home);
    }

    public String getGameAssets() {
        return H2CO3Tools.getH2CO3Value("game_assets", H2CO3Tools.MINECRAFT_DIR + "/assets/virtual/legacy/", String.class);
    }

    public void setBackground(String background) {
        H2CO3Tools.setH2CO3Value("background", background);
    }

    public String getExtraJavaFlags() {
        return H2CO3Tools.getH2CO3LauncherValue("extra_java_flags", "", String.class);
    }

    public void setExtraJavaFlags(String javaFlags) {
        H2CO3Tools.setH2CO3LauncherValue("extra_java_flags", javaFlags);
    }

    public void setDir(String dir) {
        setGameDirectory(dir);
        setGameAssets(dir + "/assets/virtual/legacy");
        setGameAssetsRoot(dir + "/assets");
        setGameCurrentVersion(dir + "/versions");
    }
}