/*
 * Hello Minecraft! Launcher
 * Copyright (C) 2020  huangyuhui <huanghongxun2008@126.com> and contributors
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either versionMod 3 of the License, or
 * (at your option) any later versionMod.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package org.koishi.launcher.h2co3.core.download.fabric;

import org.koishi.launcher.h2co3.core.download.DefaultDependencyManager;
import org.koishi.launcher.h2co3.core.download.LibraryAnalyzer;
import org.koishi.launcher.h2co3.core.download.RemoteVersion;
import org.koishi.launcher.h2co3.core.download.Version;
import org.koishi.launcher.h2co3.core.game.mod.RemoteMod;
import org.koishi.launcher.h2co3.core.utils.task.Task;

import java.time.Instant;
import java.util.List;

public class FabricAPIRemoteVersion extends RemoteVersion {
    private final String fullVersion;
    private final RemoteMod.VersionMod versionMod;

    /**
     * Constructor.
     *
     * @param gameVersion the Minecraft versionMod that this remote versionMod suits.
     * @param selfVersion the versionMod string of the remote versionMod.
     * @param urls        the installer or universal jar original URL.
     */
    FabricAPIRemoteVersion(String gameVersion, String selfVersion, String fullVersion, Instant datePublished, RemoteMod.VersionMod versionMod, List<String> urls) {
        super(LibraryAnalyzer.LibraryType.FABRIC_API.getPatchId(), gameVersion, selfVersion, datePublished, urls);

        this.fullVersion = fullVersion;
        this.versionMod = versionMod;
    }

    @Override
    public String getFullVersion() {
        return fullVersion;
    }

    public RemoteMod.VersionMod getVersion() {
        return versionMod;
    }

    @Override
    public Task<Version> getInstallTask(DefaultDependencyManager dependencyManager, Version baseVersion) {
        return new FabricAPIInstallTask(dependencyManager, baseVersion, this);
    }

    @Override
    public int compareTo(RemoteVersion o) {
        if (!(o instanceof FabricAPIRemoteVersion)) return 0;
        return -this.getReleaseDate().compareTo(o.getReleaseDate());
    }
}
