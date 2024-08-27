package org.koishi.launcher.h2co3.core.launch.utils;

import android.content.Context;
import android.os.Build;
import android.util.Log;

import org.jetbrains.annotations.NotNull;
import org.koishi.launcher.h2co3.core.H2CO3Settings;
import org.koishi.launcher.h2co3.core.H2CO3Tools;
import org.koishi.launcher.h2co3.core.launch.H2CO3LauncherBridge;
import org.koishi.launcher.h2co3.core.launch.LaunchVersion;
import org.koishi.launcher.h2co3.core.launch.oracle.dalvik.VMLauncher;
import org.koishi.launcher.h2co3.core.message.H2CO3MessageManager;
import org.koishi.launcher.h2co3.core.utils.Architecture;
import org.koishi.launcher.h2co3.core.utils.CommandBuilder;
import org.koishi.launcher.h2co3.core.utils.OperatingSystem;
import org.koishi.launcher.h2co3.core.utils.StringUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.TimeZone;
import java.util.stream.Stream;

public class BaseLaunch {

    private static final String TAG = BaseLaunch.class.getSimpleName();

    // Public methods for launching and setting up the environment
    public static H2CO3LauncherBridge launchMinecraft(Context context, H2CO3Settings gameHelper, int width, int height) throws IOException {
        H2CO3LauncherBridge bridge = new H2CO3LauncherBridge();
        bridge.setLogPath(H2CO3Tools.LOG_DIR + "/latest_game.txt");
        bridge.receiveLog("surface ready, start jvm now!");
        Thread gameThread = new Thread(() -> {
            try {
                logStartInfo(bridge, "Minecraft");
                setEnv(context, gameHelper, bridge, true);
                setUpJavaRuntime(context, bridge, gameHelper);
                setupGraphicAndSoundEngine(context, bridge);
                bridge.getCallback().onLog("Working directory: " + gameHelper.getGameDirectory() + "\n");
                bridge.chdir(gameHelper.getGameDirectory());
                launch(context, bridge, gameHelper, width, height, "Minecraft");
            } catch (IOException e) {
                Log.e(TAG, "Error launching Minecraft", e);
            }
        });

        gameThread.setPriority(Thread.MAX_PRIORITY);
        bridge.setThread(gameThread);
        return bridge;
    }

    public static H2CO3LauncherBridge launchJarExecutor(Context context, H2CO3Settings gameHelper, int width, int height) {
        H2CO3LauncherBridge bridge = new H2CO3LauncherBridge();
        bridge.setLogPath(H2CO3Tools.LOG_FILE_PATH + "/latest_jar_executor.log");
        Thread javaGUIThread = new Thread(() -> {
            try {
                logStartInfo(bridge, "Jar Executor");
                setEnv(context, gameHelper, bridge, true);
                setUpJavaRuntime(context, bridge, gameHelper);
                setupGraphicAndSoundEngine(context, bridge);
                bridge.getCallback().onLog("Working directory: " + gameHelper.getGameDirectory() + "\n");
                bridge.chdir(gameHelper.getGameDirectory());
                launch(context, bridge, gameHelper, width, height, "Jar Executor");
            } catch (IOException e) {
                Log.e(TAG, "Error launching Jar Executor", e);
            }
        });

        bridge.setThread(javaGUIThread);
        return bridge;
    }

    public static void apiLaunch(Context context, H2CO3LauncherBridge bridge, String[] command, String jre, String task) throws IOException {
        printTaskTitle(bridge, task + " Arguments");
        String[] args = rebaseApiArgs(command, jre);
        for (String arg : args) {
            bridge.getCallback().onLog(task + " argument: " + arg + "\n");
        }
        bridge.setLdLibraryPath(getLibraryPath(context, H2CO3Tools.JAVA_PATH + "/" + jre));
        printTaskTitle(bridge, task + " Arguments");
        bridge.getCallback().onLog("");
        printTaskTitle(bridge, task + " Logs");
        bridge.setupExitTrap(bridge);
        bridge.getCallback().onLog("Hook success");
        int exitCode = VMLauncher.launchJVM(args);
        bridge.onExit(exitCode);
        printTaskTitle(bridge, task + " Logs");
    }

    public static H2CO3LauncherBridge launchAPIInstaller(Context context, H2CO3Settings gameHelper, String[] command, String jre) {
        H2CO3LauncherBridge bridge = new H2CO3LauncherBridge();
        bridge.setLogPath(H2CO3Tools.LOG_DIR + "/latest_api_installer.log");
        Thread apiInstallerThread = new Thread(() -> {
            try {
                logStartInfo(bridge, "API Installer");
                setEnv(context, gameHelper, bridge, false);
                setUpJavaRuntime(context, bridge, gameHelper);
                bridge.getCallback().onLog("Working directory: " + gameHelper.getGameDirectory() + "\n");
                bridge.chdir(gameHelper.getGameDirectory());
                apiLaunch(context, bridge, command, jre, "API Installer");
            } catch (IOException e) {
                Log.e(TAG, "Error launching API Installer", e);
            }
        });

        bridge.setThread(apiInstallerThread);
        return bridge;
    }

    // Environment setup methods
    public static void setEnv(Context context, H2CO3Settings gameHelper, H2CO3LauncherBridge bridge, boolean isRender) throws IOException {
        Map<String, String> envMap = new HashMap<>(8);
        addCommonEnv(context, gameHelper, envMap);
        if (isRender) {
            addRendererEnv(context, envMap, gameHelper.getRender());
        }
        printTaskTitle(bridge, "Env Map");
        envMap.forEach((key, value) -> {
            try {
                bridge.getCallback().onLog("Env: " + key + "=" + value + "\n");
                bridge.setenv(key, value);
            } catch (IOException e) {
                Log.e(TAG, "Error setting environment variable", e);
            }
        });
        printTaskTitle(bridge, "Env Map");
    }

    public static void addCommonEnv(Context context, H2CO3Settings gameHelper, Map<String, String> envMap) {
        envMap.put("HOME", H2CO3Tools.LOG_DIR + "\n");
        envMap.put("JAVA_HOME", gameHelper.getJavaPath() + "\n");
        envMap.put("H2CO3LAUNCHER_NATIVEDIR", context.getApplicationInfo().nativeLibraryDir + "\n");
        envMap.put("TMPDIR", context.getCacheDir().getAbsolutePath() + "\n");
    }

    public static void addRendererEnv(Context context, Map<String, String> envMap, String render) {
        if (context == null || envMap == null || render == null) {
            throw new IllegalArgumentException("Context, envMap, and render must not be null");
        }

        envMap.put("LIBGL_STRING", render);
        String libGLName = null;

        if (render.equals(H2CO3Tools.GL_GL114)) {
            libGLName = "libgl4es_114.so";
        } else if (render.equals(H2CO3Tools.GL_VGPU)) {
            libGLName = "libvgpu.so";
        } else if (render.equals(H2CO3Tools.GL_VIRGL)) {
            setAdditionalEnv(envMap, context.getCacheDir().getAbsolutePath(), "4.3", "430");
            libGLName = "libOSMesa_81.so";
        } else if (render.equals(H2CO3Tools.GL_ZINK)) {
            setAdditionalEnv(envMap, context.getCacheDir().getAbsolutePath(), "4.6", "460");
            libGLName = "libOSMesa_8.so";
        } else if (render.equals(H2CO3Tools.GL_ANGLE)) {
            envMap.put("LIBGL_ES", "3");
            libGLName = "libtinywrapper.so";
        }

        if (libGLName != null) {
            envMap.put("LIBGL_NAME", libGLName);
            envMap.put("LIBEGL_NAME", "libEGL.so");
            setGLValues(envMap, "2", "3", "1", "1", "1", "1");
        }
    }

    private static void setAdditionalEnv(Map<String, String> envMap, String mesaGLSLCacheDir, String glVersionOverride, String glslVersionOverride) {
        envMap.put("MESA_GLSL_CACHE_DIR", mesaGLSLCacheDir);
        envMap.put("MESA_GL_VERSION_OVERRIDE", glVersionOverride);
        envMap.put("MESA_GLSL_VERSION_OVERRIDE", glslVersionOverride);
        envMap.put("force_glsl_extensions_warn", "true");
        envMap.put("allow_higher_compat_version", "true");
        envMap.put("allow_glsl_extension_directive_midshader", "true");
        envMap.put("MESA_LOADER_DRIVER_OVERRIDE", "zink");
        envMap.put("VTEST_SOCKET_NAME", new File(mesaGLSLCacheDir, ".virgl_test").getAbsolutePath());
        envMap.put("GALLIUM_DRIVER", "zink");
        envMap.put("OSMESA_NO_FLUSH_FRONTBUFFER", "1");
    }

    public static void setGLValues(Map<String, String> envMap, String libglEs, String libglMipmap, String libglNormalize, String libglVsync, String libglNointovlhack, String libglNoerror) {
        envMap.put("LIBGL_ES", libglEs);
        envMap.put("LIBGL_MIPMAP", libglMipmap);
        envMap.put("LIBGL_NORMALIZE", libglNormalize);
        envMap.put("LIBGL_VSYNC", libglVsync);
        envMap.put("LIBGL_NOINTOVLHACK", libglNointovlhack);
        envMap.put("LIBGL_NOERROR", libglNoerror);
    }

    // Java runtime setup methods
    public static void setUpJavaRuntime(Context context, H2CO3LauncherBridge bridge, H2CO3Settings gameHelper) throws IOException {
        String jreLibDir = gameHelper.getJavaPath() + getJreLibDir(gameHelper.getJavaPath());
        String jliLibDir = new File(jreLibDir + "/jli/libjli.so").exists() ? jreLibDir + "/jli" : jreLibDir;
        String jvmLibDir = jreLibDir + getJvmLibDir(gameHelper.getJavaPath());

        // dlopen jre
        bridge.dlopen(jliLibDir + "/libjli.so");
        bridge.dlopen(jvmLibDir + "/libjvm.so");
        bridge.dlopen(jreLibDir + "/libfreetype.so");
        bridge.dlopen(jreLibDir + "/libverify.so");
        bridge.dlopen(jreLibDir + "/libjava.so");
        bridge.dlopen(jreLibDir + "/libnet.so");
        bridge.dlopen(jreLibDir + "/libnio.so");
        bridge.dlopen(jreLibDir + "/libawt.so");
        bridge.dlopen(jreLibDir + "/libawt_headless.so");
        bridge.dlopen(jreLibDir + "/libfontmanager.so");
        bridge.dlopen(jreLibDir + "/libtinyiconv.so");
        bridge.dlopen(jreLibDir + "/libinstrument.so");
        bridge.dlopen(context.getApplicationInfo().nativeLibraryDir + "/libopenal.so");
        bridge.dlopen(context.getApplicationInfo().nativeLibraryDir + "/libglfw.so");
        bridge.dlopen(context.getApplicationInfo().nativeLibraryDir + "/liblwjgl.so");

        File javaPath = new File(gameHelper.getJavaPath());
        for (File file : locateLibs(javaPath)) {
            bridge.dlopen(file.getAbsolutePath());
        }
    }

    public static List<File> locateLibs(File path) throws IOException {
        List<File> returnValue = new ArrayList<>();
        try (Stream<Path> walk = Files.walk(path.toPath())) {
            walk.filter(Files::isRegularFile)
                    .filter(p -> p.toString().endsWith(".so"))
                    .forEach(p -> returnValue.add(p.toFile()));
        }
        return returnValue;
    }

    // Launching methods
    public static void launch(Context context, H2CO3LauncherBridge bridge, H2CO3Settings gameHelper, int width, int height, String task) throws IOException {
        printTaskTitle(bridge, task + " Arguments");
        String[] args = rebaseArgs(context, gameHelper, width, height);
        for (String arg : args) {
            bridge.getCallback().onLog(task + " argument: " + arg + "\n");
        }
        bridge.setLdLibraryPath(getLibraryPath(context, gameHelper.getJavaPath()));
        printTaskTitle(bridge, task + " Arguments");
        bridge.getCallback().onLog("");
        printTaskTitle(bridge, task + " Logs");
        bridge.setupExitTrap(bridge);
        bridge.getCallback().onLog("Hook success");
        int exitCode = VMLauncher.launchJVM(args);
        bridge.onExit(exitCode);
        printTaskTitle(bridge, task + " Logs");
    }

    // Argument handling methods
    public static String[] rebaseArgs(Context context, H2CO3Settings gameHelper, int width, int height) throws IOException {
        final CommandBuilder command = getMcArgs(context, gameHelper, width, height);
        List<String> rawCommandLine = command.asList();

        Log.d(TAG, "Raw command line: " + rawCommandLine);

        if (rawCommandLine.stream().anyMatch(StringUtils::isBlank)) {
            Log.e(TAG, "Illegal command line: " + rawCommandLine);
            throw new IllegalStateException("Illegal command line " + rawCommandLine);
        }

        List<String> argList = new ArrayList<>(rawCommandLine);
        argList.add(0, gameHelper.getJavaPath() + "/bin/java");
        return argList.toArray(new String[0]);
    }

    public static String[] rebaseApiArgs(String[] command, String jre) throws IOException {
        String javaPath = H2CO3Tools.JAVA_PATH + "/" + jre + "/bin/java";
        List<String> argList = new ArrayList<>(Arrays.asList(command));
        argList.add(0, javaPath);
        return argList.toArray(new String[0]);
    }

    public static CommandBuilder getMcArgs(Context context, H2CO3Settings gameHelper, int width, int height) throws IOException {
        H2CO3Tools.loadPaths(context);

        CommandBuilder args = new CommandBuilder();

        gameHelper.setRender(H2CO3Tools.GL_GL114);

        LaunchVersion version = LaunchVersion.fromDirectory(new File(gameHelper.getGameCurrentVersion()));

        String lwjglPath = H2CO3Tools.RUNTIME_DIR + "/h2co3Launcher/lwjgl";
        String javaPath = gameHelper.getJavaPath();

        boolean highVersion = version.minimumLauncherVersion >= 21;
        boolean isJava8 = javaPath.equals(H2CO3Tools.JAVA_8_PATH);

        String classPath;
        classPath = lwjglPath + "/lwjgl.jar:" + version.getClassPath(gameHelper, highVersion, isJava8) + ":" + H2CO3Tools.PLUGIN_DIR + "/H2CO3LaunchWrapper.jar" + ":" + gameHelper.getGameCurrentVersion() + "/" + new File(gameHelper.getGameCurrentVersion()).getName() + ".jar";

        addCacioOptions(args, height, width, javaPath);

        args.add("-Xms" + "1024" + "M");
        args.add("-Xmx" + "6000" + "M");

        Charset encoding = OperatingSystem.NATIVE_CHARSET;
        String fileEncoding = args.addDefault("-Dfile.encoding=", encoding.name());
        if (fileEncoding != null && !"-Dfile.encoding=COMPAT".equals(fileEncoding)) {
            try {
                encoding = Charset.forName(fileEncoding.substring("-Dfile.encoding=".length()));
            } catch (Throwable ex) {
                H2CO3Tools.showMessage(H2CO3MessageManager.NotificationItem.Type.WARNING, "Bad file encoding" + ex);
            }
        }
        args.addDefault("-Dsun.stdout.encoding=", encoding.name());
        args.addDefault("-Dsun.stderr.encoding=", encoding.name());

        args.addDefault("-Djava.rmi.server.useCodebaseOnly=", "true");
        args.addDefault("-Dcom.sun.jndi.rmi.object.trustURLCodebase=", "false");
        args.addDefault("-Dcom.sun.jndi.cosnaming.object.trustURLCodebase=", "false");

        args.addDefault("-Dminecraft.client.jar=", gameHelper.getGameCurrentVersion() + "/" + new File(gameHelper.getGameCurrentVersion()).getName() + ".jar");

        if (Architecture.is32BitsDevice()) {
            args.addDefault("-Xss", "1m");
        }

        args.addDefault("-Dfml.ignoreInvalidMinecraftCertificates=", "true");
        args.addDefault("-Dfml.ignorePatchDiscrepancies=", "true");

        args.addDefault("-Dext.net.resolvPath=", gameHelper.getJavaPath() + "/resolv.conf");

        args.add("-cp");
        args.add(classPath);

        args.addDefault("-Djava.library.path=", getLibraryPath(context, javaPath));
        args.addDefault("-Djna.boot.library.path=", H2CO3Tools.NATIVE_LIB_DIR);
        args.addDefault("-Dfml.earlyprogresswindow=", "false");
        args.addDefault("-Dos.name=", "Linux");
        args.addDefault("-Dos.version=Android-", Build.VERSION.RELEASE);
        args.addDefault("-Dlwjgl.platform=", "H2CO3Launcher");
        args.addDefault("-Duser.language=", System.getProperty("user.language"));
        args.addDefault("-Dwindow.width=", String.valueOf(width));
        args.addDefault("-Dwindow.height=", String.valueOf(height));

        args.addDefault("-Djava.rmi.server.useCodebaseOnly=", "true");
        args.addDefault("-Dcom.sun.jndi.rmi.object.trustURLCodebase=", "false");
        args.addDefault("-Dcom.sun.jndi.cosnaming.object.trustURLCodebase=", "false");

        args.addDefault("-Dfml.ignoreInvalidMinecraftCertificates=", "true");
        args.addDefault("-Dfml.ignorePatchDiscrepancies=", "true");
        args.addDefault("-Duser.timezone=", TimeZone.getDefault().getID());
        args.addDefault("-Duser.home=", gameHelper.getGameDirectory());
        args.addDefault("-Dorg.lwjgl.vulkan.libname=", "libvulkan.so");

        if (gameHelper.getRender().equals(H2CO3Tools.GL_VIRGL)) {
            args.addDefault("-Dorg.lwjgl.opengl.libname=", "libGL.so.1");
        } else {
            args.addDefault("-Dorg.lwjgl.opengl.libname=", "libgl4es_114.so");
        }
        args.addDefault("-Djava.io.tmpdir=", H2CO3Tools.CACHE_DIR);

        String[] accountArgs = new String[0];
        Collections.addAll(args.asList(), accountArgs);
        String[] JVMArgs = version.getJVMArguments(gameHelper);
        for (String JVMArg : JVMArgs) {
            if (JVMArg.startsWith("-DignoreList") && !JVMArg.endsWith("," + new File(gameHelper.getGameCurrentVersion()).getName() + ".jar")) {
                JVMArg = JVMArg + "," + new File(gameHelper.getGameCurrentVersion()).getName() + ".jar";
            }
            if (!JVMArg.startsWith("-DFabricMcEmu") && !JVMArg.startsWith("net.minecraft.client.main.Main")) {
                args.add(JVMArg);
            }
        }

        args.add("h2co3.Wrapper");
        args.add(version.mainClass);
        String[] minecraftArgs = version.getMinecraftArguments(gameHelper, highVersion);
        args.add(minecraftArgs);
        args.add("--width");
        args.add(String.valueOf(width));
        args.add("--height");
        args.add(String.valueOf(height));
        return TouchInjector.rebaseArguments(args, gameHelper);
    }

    public static void addCacioOptions(CommandBuilder args, int height, int width, String javaPath) {
        boolean isJava8 = javaPath.equals(H2CO3Tools.JAVA_8_PATH);
        boolean isJava11 = javaPath.equals(H2CO3Tools.JAVA_11_PATH);
        args.addDefault("-Djava.awt.headless=", "false");
        args.addDefault("-Dcacio.managed.screensize=", width + "x" + height);
        args.addDefault("-Dcacio.font.fontmanager=", "sun.awt.X11FontManager");
        args.addDefault("-Dcacio.font.fontscaler=", "sun.font.FreetypeFontScaler");
        args.addDefault("-Dswing.defaultlaf=", "javax.swing.plaf.metal.MetalLookAndFeel");

        if (isJava8) {
            args.addDefault("-Dawt.toolkit=", "net.java.openjdk.cacio.ctc.CTCToolkit");
            args.addDefault("-Djava.awt.graphicsenv=", "net.java.openjdk.cacio.ctc.CTCGraphicsEnvironment");
        } else {
            args.addDefault("-Dawt.toolkit=", "com.github.caciocavallosilano.cacio.ctc.CTCToolkit");
            args.addDefault("-Djava.awt.graphicsenv=", "com.github.caciocavallosilano.cacio.ctc.CTCGraphicsEnvironment");
            args.addDefault("-Djava.system.class.loader=", "com.github.caciocavallosilano.cacio.ctc.CTCPreloadClassLoader");

            args.add("--add-exports=java.desktop/java.awt=ALL-UNNAMED");
            args.add("--add-exports=java.desktop/java.awt.peer=ALL-UNNAMED");
            args.add("--add-exports=java.desktop/sun.awt.image=ALL-UNNAMED");
            args.add("--add-exports=java.desktop/sun.java2d=ALL-UNNAMED");
            args.add("--add-exports=java.desktop/java.awt.dnd.peer=ALL-UNNAMED");
            args.add("--add-exports=java.desktop/sun.awt=ALL-UNNAMED");
            args.add("--add-exports=java.desktop/sun.awt.event=ALL-UNNAMED");
            args.add("--add-exports=java.desktop/sun.awt.datatransfer=ALL-UNNAMED");
            args.add("--add-exports=java.desktop/sun.font=ALL-UNNAMED");
            args.add("--add-exports=java.base/sun.security.action=ALL-UNNAMED");
            args.add("--add-opens=java.base/java.util=ALL-UNNAMED");
            args.add("--add-opens=java.desktop/java.awt=ALL-UNNAMED");
            args.add("--add-opens=java.desktop/sun.font=ALL-UNNAMED");
            args.add("--add-opens=java.desktop/sun.java2d=ALL-UNNAMED");
            args.add("--add-opens=java.base/java.lang.reflect=ALL-UNNAMED");
            args.add("--add-opens=java.base/java.net=ALL-UNNAMED");
        }

        StringBuilder cacioClasspath = getStringBuilder(isJava8, isJava11);
        args.add(cacioClasspath.toString());
    }

    @NotNull
    private static StringBuilder getStringBuilder(boolean isJava8, boolean isJava11) {
        StringBuilder cacioClasspath = new StringBuilder();
        cacioClasspath.append("-Xbootclasspath/").append(isJava8 ? "p" : "a");
        File cacioDir = new File(isJava8 ? H2CO3Tools.CACIOCAVALLO_8_DIR : isJava11 ? H2CO3Tools.CACIOCAVALLO_11_DIR : H2CO3Tools.CACIOCAVALLO_17_DIR);
        if (cacioDir.exists() && cacioDir.isDirectory()) {
            for (File file : Objects.requireNonNull(cacioDir.listFiles())) {
                if (file.getName().endsWith(".jar")) {
                    cacioClasspath.append(":").append(file.getAbsolutePath());
                }
            }
        }
        return cacioClasspath;
    }

    // Utility methods
    public static void printTaskTitle(H2CO3LauncherBridge bridge, String task) throws IOException {
        if (bridge != null && bridge.getCallback() != null) {
            bridge.getCallback().onLog("==================== " + task + " ====================\n");
        }
    }

    public static void logStartInfo(H2CO3LauncherBridge bridge, String task) throws IOException {
        printTaskTitle(bridge, "Start " + task);
        if (bridge.getCallback() != null) {
            bridge.getCallback().onLog("Architecture: " + Architecture.archAsString(Architecture.getDeviceArchitecture()) + "\n");
            bridge.getCallback().onLog("CPU:" + Build.HARDWARE + "\n");
            bridge.getCallback().onLog("Device info:" + Build.DEVICE + "\n " + Build.MODEL + "\n " + Build.PRODUCT + "\n " + Build.BOARD + "\n " + Build.BRAND + "\n " + Build.FINGERPRINT + "\n");
        }
    }

    public static Map<String, String> readJREReleaseProperties(String javaPath) throws IOException {
        Map<String, String> jreReleaseMap = new HashMap<>();
        Path releaseFilePath = Paths.get(javaPath, "release");
        try (BufferedReader jreReleaseReader = Files.newBufferedReader(releaseFilePath)) {
            String currLine;
            while ((currLine = jreReleaseReader.readLine()) != null) {
                if (currLine.contains("=")) {
                    String[] keyValue = currLine.split("=");
                    jreReleaseMap.put(keyValue[0], keyValue[1].replace("\"", ""));
                }
            }
        }
        return jreReleaseMap;
    }

    public static String getJreLibDir(String javaPath) throws IOException {
        String jreArchitecture = readJREReleaseProperties(javaPath).get("OS_ARCH");
        if (jreArchitecture == null) {
            throw new IOException("Unsupported architecture!");
        }
        jreArchitecture = jreArchitecture.equals("x86") ? "i386/i486/i586" : jreArchitecture;
        for (String arch : jreArchitecture.split("/")) {
            File file = new File(javaPath, "lib/" + arch);
            if (file.exists() && file.isDirectory()) {
                return "/lib/" + arch;
            }
        }
        return "/lib"; // Default fallback
    }

    public static String getJvmLibDir(String javaPath) throws IOException {
        String jreLibDir = getJreLibDir(javaPath);
        File jvmFile = new File(javaPath + jreLibDir + "/server/libjvm.so");
        return jvmFile.exists() ? "/server" : "/client";
    }

    public static String getLibraryPath(Context context, String javaPath) throws IOException {
        String nativeDir = context.getApplicationInfo().nativeLibraryDir;
        String libDirName = Architecture.is64BitsDevice() ? "lib64" : "lib";
        String jreLibDir = getJreLibDir(javaPath);
        String jvmLibDir = getJvmLibDir(javaPath);
        return String.join(":",
                javaPath + jreLibDir,
                javaPath + jreLibDir + "/jli",
                javaPath + jreLibDir + jvmLibDir,
                "/system/" + libDirName,
                "/vendor/" + libDirName,
                "/vendor/" + libDirName + "/hw",
                nativeDir
        );
    }

    public static void setupGraphicAndSoundEngine(Context context, H2CO3LauncherBridge bridge) {
        String nativeDir = context.getApplicationInfo().nativeLibraryDir;
        bridge.dlopen(nativeDir + "/libopenal.so");
    }
}