package org.koishi.launcher.h2co3.core.launch.utils;

import android.content.Context;
import android.os.Build;

import org.jetbrains.annotations.NotNull;
import org.koishi.launcher.h2co3.core.H2CO3Settings;
import org.koishi.launcher.h2co3.core.H2CO3Tools;
import org.koishi.launcher.h2co3.core.game.download.H2CO3GameRepository;
import org.koishi.launcher.h2co3.core.game.download.MaintainTask;
import org.koishi.launcher.h2co3.core.game.download.Version;
import org.koishi.launcher.h2co3.core.launch.H2CO3LauncherBridge;
import org.koishi.launcher.h2co3.core.message.H2CO3MessageManager;
import org.koishi.launcher.h2co3.core.utils.Architecture;
import org.koishi.launcher.h2co3.core.utils.CommandBuilder;
import org.koishi.launcher.h2co3.core.utils.OperatingSystem;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.TimeZone;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Stream;

public class H2CO3LaunchUtils {

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
        String jreArchitecture = Optional.ofNullable(readJREReleaseProperties(javaPath).get("OS_ARCH"))
                .orElseThrow(() -> new IOException("Unsupported architecture!"));
        jreArchitecture = jreArchitecture.equals("x86") ? "i386/i486/i586" : jreArchitecture;

        for (String arch : jreArchitecture.split("/")) {
            File file = new File(javaPath, "lib/" + arch);
            if (file.exists() && file.isDirectory()) {
                return "/lib/" + arch;
            }
        }
        return "/lib";
    }

    public static String getJvmLibDir(String javaPath) throws IOException {
        String jreLibDir = getJreLibDir(javaPath);
        return new File(javaPath + jreLibDir + "/server/libjvm.so").exists() ? "/server" : "/client";
    }

    public static String getLibraryPath(Context context, String javaPath) throws IOException {
        String nativeDir = context.getApplicationInfo().nativeLibraryDir;
        String libDirName = Architecture.is64BitsDevice() ? "lib64" : "lib";
        String jreLibDir = getJreLibDir(javaPath);
        String jvmLibDir = getJvmLibDir(javaPath);
        String jliLibDir = "/jli";

        return String.join(":",
                javaPath + jreLibDir,
                javaPath + jreLibDir + jliLibDir,
                javaPath + jreLibDir + jvmLibDir,
                "/system/" + libDirName,
                "/vendor/" + libDirName,
                "/vendor/" + libDirName + "/hw:" + nativeDir);
    }

    public static void addCommonEnv(Context context, H2CO3Settings settings, Map<String, String> envMap) throws Exception {
        envMap.put("HOME", H2CO3Tools.LOG_DIR);
        String javaPath = getJavaPath(settings);
        envMap.put("JAVA_HOME", javaPath);
        envMap.put("H2CO3LAUNCHER_NATIVEDIR", context.getApplicationInfo().nativeLibraryDir);
        envMap.put("TMPDIR", context.getCacheDir().getAbsolutePath());
    }

    public static String getJavaPath(H2CO3Settings settings) throws Exception {
        LaunchVersion version = LaunchVersion.fromDirectory(new File(settings.getGameCurrentVersion()));
        int setGetJavaPath = settings.getJavaVer();

        if (setGetJavaPath == 0) {
            return version.getMajorVersion(settings) == 8 ? H2CO3Tools.JAVA_8_PATH :
                    version.getMajorVersion(settings) >= 21 ? H2CO3Tools.JAVA_21_PATH :
                            H2CO3Tools.JAVA_17_PATH;
        } else {
            return switch (setGetJavaPath) {
                case 1 -> H2CO3Tools.JAVA_8_PATH;
                case 2 -> H2CO3Tools.JAVA_11_PATH;
                case 3 -> H2CO3Tools.JAVA_17_PATH;
                default -> H2CO3Tools.JAVA_21_PATH;
            };
        }
    }

    public static void addRendererEnv(Context context, Map<String, String> envMap, String render) {
        envMap.put("LIBGL_STRING", render);
        if (render.equals(H2CO3Tools.GL_GL114)) {
            setGLValues(envMap, "libgl4es_114.so", "libEGL.so", "2", "3", "1", "1", "1", "1");
        } else if (render.equals(H2CO3Tools.GL_VGPU)) {
            setGLValues(envMap, "libvgpu.so", "libEGL.so", "2", "3", "1", "1", "1", "1");
        } else if (render.equals(H2CO3Tools.GL_VIRGL) || render.equals(H2CO3Tools.GL_ZINK)) {
            setGLValues(envMap, "libOSMesa_81.so", "libEGL.so", "2", "3", "1", "1", "1", "1");
            setVirglEnv(context, envMap);
        } else if (render.equals(H2CO3Tools.GL_ANGLE)) {
            setGLValues(envMap, "libtinywrapper.so", "libEGL_angle.so", "3");
        }
    }

    private static void setGLValues(Map<String, String> envMap, String libglName, String libEglName, String... values) {
        envMap.put("LIBGL_NAME", libglName);
        envMap.put("LIBEGL_NAME", libEglName);
        if (values.length >= 6) {
            envMap.put("LIBGL_ES", values[0]);
            envMap.put("LIBGL_MIPMAP", values[1]);
            envMap.put("LIBGL_NORMALIZE", values[2]);
            envMap.put("LIBGL_VSYNC", values[3]);
            envMap.put("LIBGL_NOINTOVLHACK", values[4]);
            envMap.put("LIBGL_NOERROR", values[5]);
        }
    }

    private static void setVirglEnv(Context context, Map<String, String> envMap) {
        envMap.put("MESA_GLSL_CACHE_DIR", context.getCacheDir().getAbsolutePath());
        envMap.put("MESA_GL_VERSION_OVERRIDE", "4.3");
        envMap.put("MESA_GLSL_VERSION_OVERRIDE", "430");
        envMap.put("force_glsl_extensions_warn", "true");
        envMap.put("allow_higher_compat_version", "true");
        envMap.put("allow_glsl_extension_directive_midshader", "true");
        envMap.put("MESA_LOADER_DRIVER_OVERRIDE", "zink");
        envMap.put("VTEST_SOCKET_NAME", new File(context.getCacheDir(), ".virgl_test").getAbsolutePath());
        envMap.put("GALLIUM_DRIVER", "virpipe");
        envMap.put("OSMESA_NO_FLUSH_FRONTBUFFER", "1");
    }

    public static void setUpJavaRuntime(Context context, H2CO3Settings settings, H2CO3LauncherBridge bridge) throws Exception {
        LaunchVersion version = LaunchVersion.fromDirectory(new File(settings.getGameCurrentVersion()));
        String javaPath = getJavaPath(settings);
        String jreLibDir = javaPath + getJreLibDir(javaPath);
        String jliLibDir = new File(jreLibDir + "/jli/libjli.so").exists() ? jreLibDir + "/jli" : jreLibDir;
        String jvmLibDir = jreLibDir + getJvmLibDir(javaPath);

        dlopenLibraries(bridge, jliLibDir, jvmLibDir, jreLibDir, context);

        for (File file : locateLibs(new File(javaPath))) {
            bridge.dlopen(file.getAbsolutePath());
        }
    }

    private static void dlopenLibraries(H2CO3LauncherBridge bridge, String jliLibDir, String jvmLibDir, String jreLibDir, Context context) {
        bridge.dlopen(jliLibDir + "/libjli.so");
        bridge.dlopen(jvmLibDir + "/libjvm.so");
        String[] libs = {"libfreetype.so", "libverify.so", "libjava.so", "libnet.so", "libnio.so", "libawt.so", "libawt_headless.so", "libfontmanager.so", "libtinyiconv.so", "libinstrument.so"};
        for (String lib : libs) {
            bridge.dlopen(jreLibDir + "/" + lib);
        }
        bridge.dlopen(context.getApplicationInfo().nativeLibraryDir + "/libopenal.so");
        bridge.dlopen(context.getApplicationInfo().nativeLibraryDir + "/libglfw.so");
        bridge.dlopen(context.getApplicationInfo().nativeLibraryDir + "/liblwjgl.so");
    }

    public static ArrayList<File> locateLibs(File path) throws IOException {
        ArrayList<File> returnValue = new ArrayList<>();
        try (Stream<Path> walk = Files.walk(path.toPath())) {
            walk.filter(Files::isRegularFile)
                    .filter(p -> p.toString().endsWith(".so"))
                    .forEach(p -> returnValue.add(p.toFile()));
        }
        return returnValue;
    }

    public static void setupGraphicAndSoundEngine(Context context, H2CO3Settings settings, H2CO3LauncherBridge bridge) {
        String nativeDir = context.getApplicationInfo().nativeLibraryDir;
        bridge.dlopen(nativeDir + "/libopenal.so");
    }

    public static CommandBuilder getMcArgs(Context context, H2CO3Settings settings, int width, int height) throws Exception {
        H2CO3Tools.loadPaths(context);
        CommandBuilder args = new CommandBuilder();
        settings.setRender(H2CO3Tools.GL_GL114);

        LaunchVersion version = LaunchVersion.fromDirectory(new File(settings.getGameCurrentVersion()));
        String javaPath = getJavaPath(settings);
        boolean highVersion = version.minimumLauncherVersion >= 21;

        H2CO3GameRepository repository = new H2CO3GameRepository(new File(settings.getGameDirectory()));
        repository.refreshVersions();
        AtomicReference<Version> versions = new AtomicReference<>(MaintainTask.maintain(repository, repository.getResolvedVersion(version.id)));
        Set<String> classpath = repository.getClasspath(versions.get());

        classpath.add(H2CO3Tools.PLUGIN_DIR + "/H2CO3LaunchWrapper.jar");
        File jar = new File(repository.getVersionRoot(version.id), version.id + ".jar");
        classpath.add(jar.getAbsolutePath());

        addCacioOptions(args, height, width, javaPath);
        args.add("-cp", String.join(File.pathSeparator, classpath));
        setDefaultArgs(args, context, javaPath, settings, width, height);

        args.addDefault("-Xms", settings.getGameMemoryMin() + "M");
        args.addDefault("-Xmx", settings.getGameMemoryMax() + "M");
        args.add("h2co3.Wrapper");
        args.add(version.mainClass);
        args.add(version.getMinecraftArguments(settings, highVersion));
        args.add("--width", String.valueOf(width));
        args.add("--height", String.valueOf(height));

        return TouchInjector.rebaseArguments(args, settings);
    }

    private static void setDefaultArgs(CommandBuilder args, Context context, String javaPath, H2CO3Settings settings, int width, int height) throws IOException {
        args.addDefault("-Djava.library.path=", getLibraryPath(context, javaPath));
        args.addDefault("-Djna.boot.library.path=", H2CO3Tools.NATIVE_LIB_DIR);
        args.addDefault("-Dfml.earlyprogresswindow=", "false");
        args.addDefault("-Dorg.lwjgl.util.DebugLoader=", "true");
        args.addDefault("-Dorg.lwjgl.util.Debug=", "true");
        args.addDefault("-Dos.name=", "Linux");
        args.addDefault("-Dos.version=Android-", Build.VERSION.RELEASE);
        args.addDefault("-Dlwjgl.platform=", "H2CO3Launcher");
        args.addDefault("-Duser.language=", System.getProperty("user.language"));
        args.addDefault("-Dwindow.width=", String.valueOf(width));
        args.addDefault("-Dwindow.height=", String.valueOf(height));
        args.addDefault("-Dminecraft.client.jar=", settings.getGameCurrentVersion() + "/" + new File(settings.getGameCurrentVersion()).getName() + ".jar");
        args.addDefault("-Djava.rmi.server.useCodebaseOnly=", "true");
        args.addDefault("-Dcom.sun.jndi.rmi.object.trustURLCodebase=", "false");
        args.addDefault("-Dcom.sun.jndi.cosnaming.object.trustURLCodebase=", "false");

        Charset encoding = OperatingSystem.NATIVE_CHARSET;
        String fileEncoding = args.addDefault("-Dfile.encoding=", encoding.name());
        if (fileEncoding != null && !"-Dfile.encoding=COMPAT".equals(fileEncoding)) {
            try {
                encoding = Charset.forName(fileEncoding.substring("-Dfile.encoding=".length()));
            } catch (Throwable ex) {
                H2CO3Tools.showMessage(H2CO3MessageManager.NotificationItem.Type.ERROR, "Bad file encoding" + ex);
            }
        }

        args.addDefault("-Dsun.stdout.encoding=", encoding.name());
        args.addDefault("-Dsun.stderr.encoding=", encoding.name());
        args.addDefault("-Dfml.ignoreInvalidMinecraftCertificates=", "true");
        args.addDefault("-Dfml.ignorePatchDiscrepancies=", "true");
        args.addDefault("-Duser.timezone=", TimeZone.getDefault().getID());
        args.addDefault("-Duser.home=", settings.getGameDirectory());
        args.addDefault("-Dorg.lwjgl.vulkan.libname=", "libvulkan.so");
        args.addDefault("-Dorg.lwjgl.opengl.libname=", settings.getRender().equals(H2CO3Tools.GL_VIRGL) ? "libGL.so.1" : "libgl4es_114.so");
        args.addDefault("-Djava.io.tmpdir=", H2CO3Tools.CACHE_DIR);
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
            addJavaExports(args);
        }

        StringBuilder cacioClasspath = getStringBuilder(isJava8, isJava11);
        args.add(cacioClasspath.toString());
    }

    private static void addJavaExports(CommandBuilder args) {
        String[] exports = {
                "java.desktop/java.awt=ALL-UNNAMED",
                "java.desktop/java.awt.peer=ALL-UNNAMED",
                "java.desktop/sun.awt.image=ALL-UNNAMED",
                "java.desktop/sun.java2d=ALL-UNNAMED",
                "java.desktop/java.awt.dnd.peer=ALL-UNNAMED",
                "java.desktop/sun.awt=ALL-UNNAMED",
                "java.desktop/sun.awt.event=ALL-UNNAMED",
                "java.desktop/sun.awt.datatransfer=ALL-UNNAMED",
                "java.desktop/sun.font=ALL-UNNAMED",
                "java.base/sun.security.action=ALL-UNNAMED"
        };

        for (String exp : exports) {
            args.add("--add-exports=" + exp);
        }

        String[] opens = {
                "java.base/java.util=ALL-UNNAMED",
                "java.desktop/java.awt=ALL-UNNAMED",
                "java.desktop/sun.font=ALL-UNNAMED",
                "java.desktop/sun.java2d=ALL-UNNAMED",
                "java.base/java.lang.reflect=ALL-UNNAMED",
                "java.base/java.net=ALL-UNNAMED"
        };

        for (String open : opens) {
            args.add("--add-opens=" + open);
        }
    }

    @NotNull
    private static StringBuilder getStringBuilder(boolean isJava8, boolean isJava11) {
        StringBuilder cacioClasspath = new StringBuilder("-Xbootclasspath/").append(isJava8 ? "p" : "a");

        File cacioDir = new File(isJava8 ? H2CO3Tools.CACIOCAVALLO_8_DIR : isJava11 ? H2CO3Tools.CACIOCAVALLO_11_DIR : H2CO3Tools.CACIOCAVALLO_17_DIR);
        if (cacioDir.exists() && cacioDir.isDirectory()) {
            Arrays.stream(Objects.requireNonNull(cacioDir.listFiles()))
                    .filter(file -> file.getName().endsWith(".jar"))
                    .forEach(file -> cacioClasspath.append(":").append(file.getAbsolutePath()));
        }
        return cacioClasspath;
    }
}