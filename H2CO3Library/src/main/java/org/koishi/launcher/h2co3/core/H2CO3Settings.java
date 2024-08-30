package org.koishi.launcher.h2co3.core;

import static org.koishi.launcher.h2co3.core.H2CO3Tools.DOWNLOAD_SOURCE;

import org.koishi.launcher.h2co3.core.login.bean.UserBean;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class H2CO3Settings {

    private final List<String> extraJavaFlags;
    private final List<String> extraMinecraftFlags;

    public final ArrayList<UserBean> userList = new ArrayList<>();
    private static final String USER_PROPERTIES = "user_properties";
    private static final String DEFAULT_DOWNLOAD_SOURCE = "balanced";
    private static final String DEFAULT_PLAYER_NAME = null;
    private static final String DEFAULT_SESSION = "0";
    private static final String DEFAULT_USER_TYPE = "mojang";
    private static final String DEFAULT_UUID = UUID.randomUUID().toString();
    private static final String DEFAULT_TOKEN = "0";
    private static final String DEFAULT_RENDER = H2CO3Tools.GL_GL114;
    private static final String DEFAULT_GAME_DIRECTORY = H2CO3Tools.MINECRAFT_DIR;
    private static final String DEFAULT_ASSETS_ROOT = DEFAULT_GAME_DIRECTORY + "/assets/";
    private static final String DEFAULT_RUNTIME_PATH = H2CO3Tools.RUNTIME_DIR;
    private static final String DEFAULT_H2CO3_HOME = H2CO3Tools.PUBLIC_FILE_PATH;

    public static String JAVA_AUTO = "AUTO";

    public File serversFile = new File(H2CO3Tools.H2CO3_SETTING_DIR + "/h2co3_servers.json");
    public File usersFile = new File(H2CO3Tools.H2CO3_SETTING_DIR, "h2co3_users.json");

    public H2CO3Settings() {
        this.extraJavaFlags = List.of();
        this.extraMinecraftFlags = List.of();
    }

    public String getDownloadSource() {
        return H2CO3Tools.getH2CO3Value(DOWNLOAD_SOURCE, DEFAULT_DOWNLOAD_SOURCE, String.class);
    }

    public void setDownloadSource(String type) {
        H2CO3Tools.setH2CO3Value(DOWNLOAD_SOURCE, type);
    }

    public String getPlayerName() {
        return H2CO3Tools.getH2CO3Value(H2CO3Tools.LOGIN_AUTH_PLAYER_NAME, DEFAULT_PLAYER_NAME, String.class);
    }

    public void setPlayerName(String properties) {
        H2CO3Tools.setH2CO3Value(H2CO3Tools.LOGIN_AUTH_PLAYER_NAME, properties);
    }

    public String getAuthSession() {
        return H2CO3Tools.getH2CO3Value(H2CO3Tools.LOGIN_AUTH_SESSION, DEFAULT_SESSION, String.class);
    }

    public void setAuthSession(String session) {
        H2CO3Tools.setH2CO3Value(H2CO3Tools.LOGIN_AUTH_SESSION, session);
    }

    public String getUserProperties() {
        return H2CO3Tools.getH2CO3Value(USER_PROPERTIES, "{}", String.class);
    }

    public void setUserProperties(String properties) {
        H2CO3Tools.setH2CO3Value(USER_PROPERTIES, properties);
    }

    public String getUserType() {
        return H2CO3Tools.getH2CO3Value(H2CO3Tools.LOGIN_USER_TYPE, DEFAULT_USER_TYPE, String.class);
    }

    public void setUserType(String type) {
        H2CO3Tools.setH2CO3Value(H2CO3Tools.LOGIN_USER_TYPE, type);
    }

    public String getAuthUUID() {
        return H2CO3Tools.getH2CO3Value(H2CO3Tools.LOGIN_UUID, DEFAULT_UUID, String.class);
    }

    public void setAuthUUID(String uuid) {
        H2CO3Tools.setH2CO3Value(H2CO3Tools.LOGIN_UUID, uuid);
    }

    public String getAuthAccessToken() {
        return H2CO3Tools.getH2CO3Value(H2CO3Tools.LOGIN_TOKEN, DEFAULT_TOKEN, String.class);
    }

    public void setAuthAccessToken(String token) {
        H2CO3Tools.setH2CO3Value(H2CO3Tools.LOGIN_TOKEN, token);
    }

    public String getDownloadType() {
        return H2CO3Tools.getH2CO3Value("DOWNLOAD_TYPE", "bmclapi", String.class);
    }

    public void setDownloadType(String defaultRawProviderId) {
        H2CO3Tools.setH2CO3Value("DOWNLOAD_TYPE", defaultRawProviderId);
    }

    public String getRender() {
        return H2CO3Tools.getH2CO3LauncherValue("h2co3_launcher_render", DEFAULT_RENDER, String.class);
    }

    public void setRender(String path) {
        H2CO3Tools.setH2CO3LauncherValue("h2co3_launcher_render", path);
    }

    public int getJavaVer() {
        return H2CO3Tools.getH2CO3LauncherValue("h2co3_launcher_java", 0, Integer.class);
    }

    public void setJavaVer(int path) {
        H2CO3Tools.setH2CO3LauncherValue("h2co3_launcher_java", path);
    }

    public String getGameDirectory() {
        return H2CO3Tools.getH2CO3Value("game_directory", DEFAULT_GAME_DIRECTORY, String.class);
    }

    public void setGameDirectory(String directory) {
        H2CO3Tools.setH2CO3Value("game_directory", directory);
    }

    public String getGameAssetsRoot() {
        return H2CO3Tools.getH2CO3Value("game_assets_root", DEFAULT_ASSETS_ROOT, String.class);
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
        return H2CO3Tools.getH2CO3Value("runtime_path", DEFAULT_RUNTIME_PATH, String.class);
    }

    public void setRuntimePath(String path) {
        H2CO3Tools.setH2CO3Value("runtime_path", path);
    }

    public String getH2CO3Home() {
        return H2CO3Tools.getH2CO3Value("h2co3_home", DEFAULT_H2CO3_HOME, String.class);
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
        return H2CO3Tools.getH2CO3Value("game_assets", DEFAULT_GAME_DIRECTORY + "/assets/virtual/legacy/", String.class);
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

    public boolean getIsPriVerDir() {
        return H2CO3Tools.getH2CO3LauncherValue("pri_ver_dir", false, Boolean.class);
    }

    public void setSetPriVerDir(boolean newValue) {
        H2CO3Tools.setH2CO3LauncherValue("pri_ver_dir", newValue);
    }

    public int getGameMemoryMin() {
        return H2CO3Tools.getH2CO3LauncherValue("game_memory_min", 256, Integer.class);
    }

    public void setGameMemoryMin(float memoryMin) {
        H2CO3Tools.setH2CO3LauncherValue("game_memory_min", memoryMin);
    }

    public int getGameMemoryMax() {
        return H2CO3Tools.getH2CO3LauncherValue("game_memory_max", 2048, Integer.class);
    }

    public void setGameMemoryMax(float memoryMax) {
        H2CO3Tools.setH2CO3LauncherValue("game_memory_max", memoryMax);
    }

    public float getWindowResolution() {
        return H2CO3Tools.getH2CO3LauncherValue("window_resolution", 100, Integer.class);
    }

    public void setWindowResolution(int resolution) {
        H2CO3Tools.setH2CO3LauncherValue("window_resolution", resolution);
    }

    public String getJoinServer() {
        return H2CO3Tools.getH2CO3LauncherValue("join_server", "", String.class);
    }

    public void setJoinServer(String server) {
        H2CO3Tools.setH2CO3LauncherValue("join_server", server);
    }
}