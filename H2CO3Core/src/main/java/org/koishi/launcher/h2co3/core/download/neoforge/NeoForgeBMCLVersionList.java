/*
 * Hello Minecraft! Launcher
 * Copyright (C) 2021  huangyuhui <huanghongxun2008@126.com> and contributors
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package org.koishi.launcher.h2co3.core.download.neoforge;


import static org.koishi.launcher.h2co3.core.utils.Lang.wrap;

import com.google.gson.JsonParseException;
import com.google.gson.annotations.SerializedName;
import com.google.gson.reflect.TypeToken;

import org.koishi.launcher.h2co3.core.download.VersionList;
import org.koishi.launcher.h2co3.core.download.VersionNumber;
import org.koishi.launcher.h2co3.core.utils.HttpRequest;
import org.koishi.launcher.h2co3.core.utils.Lang;
import org.koishi.launcher.h2co3.core.utils.StringUtils;
import org.koishi.launcher.h2co3.core.utils.gson.tools.Validation;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public final class NeoForgeBMCLVersionList extends VersionList<NeoForgeRemoteVersion> {
    private final String apiRoot;

    /**
     * @param apiRoot API Root of BMCLAPI implementations
     */
    public NeoForgeBMCLVersionList(String apiRoot) {
        this.apiRoot = apiRoot;
    }

    @Override
    public boolean hasType() {
        return false;
    }

    @Override
    public CompletableFuture<?> loadAsync() {
        throw new UnsupportedOperationException("NeoForgeBMCLVersionList does not support loading the entire NeoForge remote version list.");
    }

    @Override
    public CompletableFuture<?> refreshAsync() {
        throw new UnsupportedOperationException("NeoForgeBMCLVersionList does not support loading the entire NeoForge remote version list.");
    }

    @Override
    public Optional<NeoForgeRemoteVersion> getVersion(String gameVersion, String remoteVersion) {
        if (gameVersion.equals("1.20.1")) {
            remoteVersion = NeoForgeRemoteVersion.fixInvalidVersion(remoteVersion);
            remoteVersion = VersionNumber.compare(remoteVersion, "47.1.85") >= 0 ? "1.20.1-" + remoteVersion : remoteVersion;
        }
        return super.getVersion(gameVersion, remoteVersion);
    }

    @Override
    public CompletableFuture<?> refreshAsync(String gameVersion) {
        return CompletableFuture.completedFuture((Void) null)
                .thenApplyAsync(wrap(unused -> HttpRequest.GET(apiRoot + "/neoforge/list/" + gameVersion).<List<NeoForgeVersion>>getJson(new TypeToken<List<NeoForgeVersion>>() {
                }.getType())))
                .thenAcceptAsync(neoForgeVersions -> {
                    lock.writeLock().lock();

                    try {
                        versions.clear(gameVersion);
                        for (NeoForgeVersion neoForgeVersion : neoForgeVersions) {
                            String nf = StringUtils.removePrefix(
                                    neoForgeVersion.version,
                                    "1.20.1".equals(gameVersion) ? "1.20.1-forge-" : "neoforge-" // Som of the version numbers for 1.20.1 are like forge.
                            );
                            versions.put(gameVersion, new NeoForgeRemoteVersion(
                                    neoForgeVersion.mcVersion,
                                    nf,
                                    Lang.immutableListOf(
                                            apiRoot + "/neoforge/version/" + neoForgeVersion.version + "/download/installer.jar"
                                    )
                            ));
                        }
                    } finally {
                        lock.writeLock().unlock();
                    }
                });
    }

    private record NeoForgeVersion(String rawVersion, String version,
                                   @SerializedName("mcversion") String mcVersion) implements Validation {

        @Override
        public void validate() throws JsonParseException {
            if (this.rawVersion == null) {
                throw new JsonParseException("NeoForgeVersion rawVersion cannot be null.");
            }
            if (this.version == null) {
                throw new JsonParseException("NeoForgeVersion version cannot be null.");
            }
            if (this.mcVersion == null) {
                throw new JsonParseException("NeoForgeVersion mcversion cannot be null.");
            }
        }
    }
}
