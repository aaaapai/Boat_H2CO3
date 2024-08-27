package org.koishi.launcher.h2co3.core.launch;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.gson.Gson;

import org.koishi.launcher.h2co3.core.H2CO3Settings;
import org.koishi.launcher.h2co3.core.H2CO3Tools;
import org.koishi.launcher.h2co3.core.utils.file.FileTools;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class LaunchVersion {

    public static class AssetsIndex {
        public String id;
        public String sha1;
        public int size;
        public int totalSize;
        public String url;
    }

    public static class Download {
        public String path;
        public String sha1;
        public int size;
        public String url;
    }

    public AssetsIndex assetIndex;
    public String assets;

    public HashMap<String, Download> downloads = new HashMap<>();
    public String id;

    public static class Library {
        public String name;
        public HashMap<String, Download> downloads = new HashMap<>();
    }

    public Library[] libraries = new Library[0];

    public String mainClass;
    public String minecraftArguments;
    public int minimumLauncherVersion;
    public String releaseTime;
    public String time;
    public String type;

    public static class Arguments {
        private Object[] game;
        private Object[] jvm;
    }

    public Arguments arguments;

    public String inheritsFrom;
    public String minecraftPath;

    @NonNull
    public static LaunchVersion fromDirectory(File file) {
        try {
            String json = new String(FileTools.readFile(new File(file, file.getName() + ".json")), "UTF-8");
            LaunchVersion result = new Gson().fromJson(json, LaunchVersion.class);
            result.minecraftPath = new File(file, file.getName() + ".jar").exists() ?
                    new File(file, file.getName() + ".jar").getAbsolutePath() : "";

            if (result.inheritsFrom != null && !result.inheritsFrom.isEmpty()) {
                LaunchVersion self = result;
                result = LaunchVersion.fromDirectory(new File(file.getParentFile(), self.inheritsFrom));
                mergeLaunchVersion(result, self);
            }
            return result;
        } catch (UnsupportedEncodingException e) {
            return null;
        }
    }

    private static void mergeLaunchVersion(LaunchVersion result, LaunchVersion self) {
        if (self.assetIndex != null) result.assetIndex = self.assetIndex;
        if (self.assets != null && !self.assets.isEmpty()) result.assets = self.assets;
        result.downloads.putAll(self.downloads);
        if (self.libraries != null) {
            List<Library> newLibs = new ArrayList<>(List.of(result.libraries));
            newLibs.addAll(List.of(self.libraries));
            result.libraries = newLibs.toArray(new Library[0]);
        }
        if (self.mainClass != null && !self.mainClass.isEmpty()) result.mainClass = self.mainClass;
        if (self.minecraftArguments != null && !self.minecraftArguments.isEmpty()) result.minecraftArguments = self.minecraftArguments;
        result.minimumLauncherVersion = Math.max(result.minimumLauncherVersion, self.minimumLauncherVersion);
        if (self.releaseTime != null && !self.releaseTime.isEmpty()) result.releaseTime = self.releaseTime;
        if (self.time != null && !self.time.isEmpty()) result.time = self.time;
        if (self.type != null && !self.type.isEmpty()) result.type = self.type;
        if (self.minecraftPath != null && !self.minecraftPath.isEmpty()) result.minecraftPath = self.minecraftPath;

        if (result.minimumLauncherVersion >= 21 && self.arguments.game != null) {
            List<Object> newGameArgs = new ArrayList<>(List.of(result.arguments.game));
            newGameArgs.addAll(List.of(self.arguments.game));
            result.arguments.game = newGameArgs.toArray();
        }
    }

    public String getClassPath(H2CO3Settings gameHelper, boolean high, boolean isJava17) {
        StringBuilder cp = new StringBuilder();
        String librariesPath = gameHelper.getGameDirectory() + "/libraries/";
        int count = 0;

        for (Library lib : this.libraries) {
            if (lib.name == null || lib.name.isEmpty() || lib.name.contains("org.lwjgl") ||
                    lib.name.contains("natives") || (!isJava17 && lib.name.contains("java-objc-bridge"))) {
                continue;
            }
            Log.e("boat", lib.name);
            String path = getString(lib, librariesPath);
            Log.e("path", path);
            if (!new File(path).exists()) continue;

            if (count > 0) cp.append(":");
            cp.append(path);
            count++;
        }
        if (high) {
            cp.append(count > 0 ? ":" : "").append(minecraftPath);
        } else {
            cp.insert(0, minecraftPath + (count > 0 ? ":" : ""));
        }
        return cp.toString();
    }

    private static @NonNull String getString(Library lib, String librariesPath) {
        String[] names = lib.name.split(":");
        return librariesPath + names[0].replace(".", "/") + "/" + names[1] + "/" + names[2] + "/" +
                names[1] + "-" + names[2] + ".jar";
    }

    public String[] getJVMArguments(H2CO3Settings gameLaunchSetting) {
        StringBuilder test = new StringBuilder();
        if (arguments != null && arguments.jvm != null) {
            for (Object obj : arguments.jvm) {
                if (obj instanceof String && !((String) obj).startsWith("-Djava.library.path") &&
                        !((String) obj).startsWith("-cp") && !((String) obj).startsWith("${classpath}")) {
                    test.append(obj).append(" ");
                }
            }
        }
        return parseArguments(test.toString(), gameLaunchSetting);
    }

    private String[] parseArguments(String args, H2CO3Settings gameLaunchSetting) {
        StringBuilder result = new StringBuilder();
        int state = 0, start = 0;

        for (int i = 0; i < args.length(); i++) {
            char currentChar = args.charAt(i);
            if (state == 0) {
                if (currentChar != '$') {
                    result.append(currentChar);
                } else if (i + 1 < args.length() && args.charAt(i + 1) == '{') {
                    state = 1;
                    start = i;
                } else {
                    result.append(currentChar);
                }
            } else if (currentChar == '}') {
                String key = args.substring(start + 2, i);
                result.append(getString(gameLaunchSetting, key));
                state = 0;
            }
        }
        return result.toString().split(" ");
    }

    private String getString(H2CO3Settings gameLaunchSetting, String key) {
        return switch (key) {
            case "version_name" -> id;
            case "launcher_name" -> "Boat_H2CO3";
            case "launcher_version" -> "1.0.0";
            case "version_type" -> type;
            case "assets_index_name" -> assetIndex != null ? assetIndex.id : assets;
            case "game_directory" -> gameLaunchSetting.getGameDirectory();
            case "assets_root" -> gameLaunchSetting.getGameAssetsRoot();
            case "user_properties" -> "{}";
            case "auth_player_name" -> gameLaunchSetting.getPlayerName();
            case "auth_session" -> gameLaunchSetting.getAuthSession();
            case "auth_uuid" -> gameLaunchSetting.getAuthUUID();
            case "auth_access_token" -> gameLaunchSetting.getAuthAccessToken();
            case "user_type" -> gameLaunchSetting.getUserType();
            case "primary_jar_name" -> gameLaunchSetting.getGameCurrentVersion() + "/" + id + ".jar";
            case "library_directory" -> gameLaunchSetting.getGameDirectory() + "/libraries";
            case "natives_directory" -> H2CO3Tools.CACHE_DIR;
            case "classpath_separator" -> ":";
            default -> "${" + key + "}";
        };
    }

    public String[] getMinecraftArguments(H2CO3Settings gameLaunchSetting, boolean isHighVer) {
        StringBuilder test = new StringBuilder();
        if (isHighVer) {
            for (Object obj : this.arguments.game) {
                if (obj instanceof String) {
                    test.append(obj).append(" ");
                }
            }
        } else {
            test.append(this.minecraftArguments);
        }
        return parseArguments(test.toString(), gameLaunchSetting);
    }

    public List<String> getLibraries() {
        List<String> libs = new ArrayList<>();
        for (Library lib : this.libraries) {
            if (lib.name == null || lib.name.isEmpty() || lib.name.contains("net.java.jinput") ||
                    lib.name.contains("org.lwjgl") || lib.name.contains("platform")) {
                continue;
            }
            libs.add(parseLibNameToPath(lib.name));
        }
        return libs;
    }

    public String parseLibNameToPath(String libName) {
        String[] tmp = libName.split(":");
        return tmp[0].replace(".", "/") + "/" + tmp[1] + "/" + tmp[2] + "/" + tmp[1] + "-" + tmp[2] + ".jar";
    }
}