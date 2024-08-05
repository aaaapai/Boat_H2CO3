package org.koishi.launcher.h2co3.core.game;

import org.koishi.launcher.h2co3.core.download.LibrariesDownloadInfo;
import org.koishi.launcher.h2co3.core.download.Library;
import org.koishi.launcher.h2co3.core.download.LibraryDownloadInfo;
import org.koishi.launcher.h2co3.core.download.ReleaseType;
import org.koishi.launcher.h2co3.core.download.Version;
import org.koishi.launcher.h2co3.core.utils.Artifact;

import java.io.File;
import java.time.Instant;
import java.util.Arrays;

public class ClassicVersion extends Version {

    public ClassicVersion() {
        super(true, "Classic", null, null, "${auth_player_name} ${auth_session} --workDir ${game_directory}",
                null, "net.minecraft.client.Minecraft", null, null, null, null, null, null,
                Arrays.asList(new ClassicLibrary("lwjgl"), new ClassicLibrary("jinput"), new ClassicLibrary("lwjgl_util")),
                null, null, null, ReleaseType.UNKNOWN, Instant.now(), Instant.now(), 0, false, false, null);
    }

    public static boolean hasClassicVersion(File baseDirectory) {
        File bin = new File(baseDirectory, "bin");
        return bin.exists()
                && new File(bin, "lwjgl.jar").exists()
                && new File(bin, "jinput.jar").exists()
                && new File(bin, "lwjgl_util.jar").exists();
    }

    private static class ClassicLibrary extends Library {

        public ClassicLibrary(String name) {
            super(new Artifact("", "", ""), null,
                    new LibrariesDownloadInfo(new LibraryDownloadInfo("bin/" + name + ".jar"), null),
                    null, null, null, null, null, null);
        }
    }
}
