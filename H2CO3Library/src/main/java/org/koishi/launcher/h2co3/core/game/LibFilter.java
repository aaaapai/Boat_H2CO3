package org.koishi.launcher.h2co3.core.game;

import static org.koishi.launcher.h2co3.core.utils.gson.JsonUtils.GSON;

import org.koishi.launcher.h2co3.core.game.download.Library;
import org.koishi.launcher.h2co3.core.game.download.Version;

import java.util.ArrayList;
import java.util.List;

public class LibFilter {

    private static final String ASM_ALL_5_2_STRING = createJsonString("org.ow2.asm:asm-all:5.2");
    private static final String JNA_5_13_STRING = createJsonString("net.java.dev.jna:jna:5.13.0");

    private static final Library ASM_ALL_5_2 = GSON.fromJson(ASM_ALL_5_2_STRING, Library.class);
    private static final Library JNA_5_13 = GSON.fromJson(JNA_5_13_STRING, Library.class);

    public static Version filter(Version version) {
        if (version == null || version.getLibraries() == null) {
            return version;
        }
        return version.setLibraries(filterLibs(version.getLibraries()));
    }

    public static List<Library> filterLibs(List<Library> libraries) {
        ArrayList<Library> newLibraries = new ArrayList<>();
        for (Library library : libraries) {
            if (!library.getName().contains("org.lwjgl") &&
                    !library.getName().contains("jinput-platform") &&
                    !library.getName().contains("twitch-platform")) {

                String[] nameParts = library.getName().split(":");
                if (nameParts.length < 3) continue;

                String[] versionParts = nameParts[2].split("\\.");
                if (library.getArtifactId().equals("asm-all") && library.getVersion().equals("4.1")) {
                    newLibraries.add(ASM_ALL_5_2);
                } else if (library.getName().startsWith("net.java.dev.jna:jna:")) {
                    if (isVersionAtLeast(versionParts, 5, 13)) {
                        newLibraries.add(library);
                    } else {
                        newLibraries.add(JNA_5_13);
                    }
                } else {
                    newLibraries.add(library);
                }
            }
        }
        return newLibraries;
    }

    private static boolean isVersionAtLeast(String[] versionParts, int major, int minor) {
        return Integer.parseInt(versionParts[0]) > major ||
                (Integer.parseInt(versionParts[0]) == major && Integer.parseInt(versionParts[1]) >= minor);
    }

    private static String createJsonString(String name) {
        return String.format("{\n  \"name\": \"%s\"\n}", name);
    }
}