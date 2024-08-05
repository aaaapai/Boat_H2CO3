package org.koishi.launcher.h2co3.core;

import static org.koishi.launcher.h2co3.core.H2CO3Tools.DOWNLOAD_SOURCE;

import org.koishi.launcher.h2co3.core.login.bean.UserBean;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class H2CO3Settings {

    public static final List<UserBean> userList = new ArrayList<>();
    /************账户相关
     *
     */
    private static final String USER_PROPERTIES = "user_properties";

    /************游戏相关
     *
     */
    private static final String LOGIN_USER_TYPE = "mojang";
    private static final String LOGIN_UUID = UUID.randomUUID().toString();
    private static final String LOGIN_TOKEN = "0";
    private static final String LOGIN_INFO = "login_info";
    private static final String LOGIN_IS_OFFLINE = "login_is_offline";
    private static final String LOGIN_IS_SELECTED = "login_is_selected";
    public static File serversFile = new File(H2CO3Tools.H2CO3_SETTING_DIR + "/h2co3_servers.json");
    public static File usersFile = new File(H2CO3Tools.H2CO3_SETTING_DIR, "h2co3_users.json");

    /************游戏设置相关
     *
     */
    public static String getDownloadSource() {
        return H2CO3Tools.getH2CO3Value(DOWNLOAD_SOURCE, "balanced", String.class);
    }

    public static void setDownloadSource(String type) {
        H2CO3Tools.setH2CO3Value(DOWNLOAD_SOURCE, type);
    }

    public static String getPlayerName() {
        return H2CO3Tools.getH2CO3Value(H2CO3Tools.LOGIN_AUTH_PLAYER_NAME, null, String.class);
    }

    public static void setPlayerName(String properties) {
        H2CO3Tools.setH2CO3Value(H2CO3Tools.LOGIN_AUTH_PLAYER_NAME, properties);
    }

    public static String getAuthSession() {
        return H2CO3Tools.getH2CO3Value(H2CO3Tools.LOGIN_AUTH_SESSION, "0", String.class);
    }

    public static void setAuthSession(String session) {
        H2CO3Tools.setH2CO3Value(H2CO3Tools.LOGIN_AUTH_SESSION, session);
    }

    public static String getUserProperties() {
        return H2CO3Tools.getH2CO3Value(USER_PROPERTIES, "{}", String.class);
    }

    public static void setUserProperties(String properties) {
        H2CO3Tools.setH2CO3Value(USER_PROPERTIES, properties);
    }

    public static String getUserType() {
        return H2CO3Tools.getH2CO3Value(H2CO3Tools.LOGIN_USER_TYPE, LOGIN_USER_TYPE, String.class);
    }

    public static void setUserType(String type) {
        H2CO3Tools.setH2CO3Value(H2CO3Tools.LOGIN_USER_TYPE, type);
    }

    public static String getAuthUUID() {
        return H2CO3Tools.getH2CO3Value(H2CO3Tools.LOGIN_UUID, LOGIN_UUID, String.class);
    }

    public static void setAuthUUID(String uuid) {
        H2CO3Tools.setH2CO3Value(H2CO3Tools.LOGIN_UUID, uuid);
    }

    public static String getAuthAccessToken() {
        return H2CO3Tools.getH2CO3Value(H2CO3Tools.LOGIN_TOKEN, LOGIN_TOKEN, String.class);
    }

    public static void setAuthAccessToken(String token) {
        H2CO3Tools.setH2CO3Value(H2CO3Tools.LOGIN_TOKEN, token);
    }

    public static String getDownloadType() {
        return H2CO3Tools.getH2CO3Value("DOWNLOAD_TYPE", "bmclapi", String.class);
    }

    public static void setDownloadType(String defaultRawProviderId) {
        H2CO3Tools.setH2CO3Value("DOWNLOAD_TYPE", defaultRawProviderId);
    }
}
