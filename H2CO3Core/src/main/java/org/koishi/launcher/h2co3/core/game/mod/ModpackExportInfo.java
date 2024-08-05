/*
 * Hello Minecraft! Launcher
 * Copyright (C) 2020  huangyuhui <huanghongxun2008@126.com> and contributors
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
package org.koishi.launcher.h2co3.core.game.mod;

import org.jetbrains.annotations.Nullable;
import org.koishi.launcher.h2co3.core.game.mod.mcbbs.McbbsModpackManifest;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ModpackExportInfo {

    private final List<String> whitelist = new ArrayList<>();
    private final List<McbbsModpackManifest.Origin> origins = new ArrayList<>();
    private String name;
    private String author;
    private String version;
    private String description;
    private String url;
    private boolean forceUpdate;
    private boolean packWithLauncher;
    private String fileApi;
    private int minMemory;
    private List<Integer> supportedJavaVersions;
    private String launchArguments;
    private String javaArguments;
    private String authlibInjectorServer;

    public ModpackExportInfo() {
    }

    public List<String> getWhitelist() {
        return whitelist;
    }

    public ModpackExportInfo setWhitelist(List<String> whitelist) {
        this.whitelist.clear();
        this.whitelist.addAll(whitelist);
        return this;
    }

    /**
     * Name of this modpack.
     */
    public String getName() {
        return name;
    }

    public ModpackExportInfo setName(String name) {
        this.name = name;
        return this;
    }

    /**
     * Author of this modpack.
     */
    public String getAuthor() {
        return author;
    }

    public ModpackExportInfo setAuthor(String author) {
        this.author = author;
        return this;
    }

    /**
     * VersionMod of this modpack.
     */
    public String getVersion() {
        return version;
    }

    public ModpackExportInfo setVersion(String version) {
        this.version = version;
        return this;
    }

    /**
     * Description of this modpack.
     * <p>
     * Supports plain HTML text.
     */
    public String getDescription() {
        return description;
    }

    public ModpackExportInfo setDescription(String description) {
        this.description = description;
        return this;
    }

    public String getFileApi() {
        return fileApi;
    }

    public ModpackExportInfo setFileApi(String fileApi) {
        this.fileApi = fileApi;
        return this;
    }

    /**
     * Modpack official introduction webpage link.
     */
    public String getUrl() {
        return url;
    }

    public ModpackExportInfo setUrl(String url) {
        this.url = url;
        return this;
    }

    public boolean isForceUpdate() {
        return forceUpdate;
    }

    public ModpackExportInfo setForceUpdate(boolean forceUpdate) {
        this.forceUpdate = forceUpdate;
        return this;
    }

    public boolean isPackWithLauncher() {
        return packWithLauncher;
    }

    public ModpackExportInfo setPackWithLauncher(boolean packWithLauncher) {
        this.packWithLauncher = packWithLauncher;
        return this;
    }

    public int getMinMemory() {
        return minMemory;
    }

    public ModpackExportInfo setMinMemory(int minMemory) {
        this.minMemory = minMemory;
        return this;
    }

    @Nullable
    public List<Integer> getSupportedJavaVersions() {
        return supportedJavaVersions;
    }

    public ModpackExportInfo setSupportedJavaVersions(List<Integer> supportedJavaVersions) {
        this.supportedJavaVersions = supportedJavaVersions;
        return this;
    }

    public String getLaunchArguments() {
        return launchArguments;
    }

    public ModpackExportInfo setLaunchArguments(String launchArguments) {
        this.launchArguments = launchArguments;
        return this;
    }

    public String getJavaArguments() {
        return javaArguments;
    }

    public ModpackExportInfo setJavaArguments(String javaArguments) {
        this.javaArguments = javaArguments;
        return this;
    }

    public String getAuthlibInjectorServer() {
        return authlibInjectorServer;
    }

    public ModpackExportInfo setAuthlibInjectorServer(String authlibInjectorServer) {
        this.authlibInjectorServer = authlibInjectorServer;
        return this;
    }

    public List<McbbsModpackManifest.Origin> getOrigins() {
        return Collections.unmodifiableList(origins);
    }

    public ModpackExportInfo setOrigins(List<McbbsModpackManifest.Origin> origins) {
        this.origins.clear();
        this.origins.addAll(origins);
        return this;
    }

    public ModpackExportInfo validate() throws NullPointerException {
        return this;
    }

    public static class Options {
        private boolean requireUrl;
        private boolean requireForceUpdate;
        private boolean requireFileApi;
        private boolean validateFileApi;
        private boolean requireMinMemory;
        private boolean requireAuthlibInjectorServer;
        private boolean requireLaunchArguments;
        private boolean requireJavaArguments;
        private boolean requireOrigins;

        public Options() {
        }

        public boolean isRequireUrl() {
            return requireUrl;
        }

        public boolean isRequireForceUpdate() {
            return requireForceUpdate;
        }

        public boolean isRequireFileApi() {
            return requireFileApi;
        }

        public boolean isValidateFileApi() {
            return validateFileApi;
        }

        public boolean isRequireMinMemory() {
            return requireMinMemory;
        }

        public boolean isRequireAuthlibInjectorServer() {
            return requireAuthlibInjectorServer;
        }

        public boolean isRequireLaunchArguments() {
            return requireLaunchArguments;
        }

        public boolean isRequireJavaArguments() {
            return requireJavaArguments;
        }

        public boolean isRequireOrigins() {
            return requireOrigins;
        }

        public Options requireUrl() {
            requireUrl = true;
            return this;
        }

        public Options requireForceUpdate() {
            requireForceUpdate = true;
            return this;
        }

        public Options requireFileApi(boolean optional) {
            requireFileApi = true;
            validateFileApi = !optional;
            return this;
        }

        public Options requireMinMemory() {
            requireMinMemory = true;
            return this;
        }

        public Options requireAuthlibInjectorServer() {
            requireAuthlibInjectorServer = true;
            return this;
        }

        public Options requireLaunchArguments() {
            requireLaunchArguments = true;
            return this;
        }

        public Options requireJavaArguments() {
            requireJavaArguments = true;
            return this;
        }

        public Options requireOrigins() {
            requireOrigins = true;
            return this;
        }

    }
}
