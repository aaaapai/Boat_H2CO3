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

import org.koishi.launcher.h2co3.core.download.ModLoaderType;
import org.koishi.launcher.h2co3.core.fakefx.beans.property.BooleanProperty;
import org.koishi.launcher.h2co3.core.fakefx.beans.property.SimpleBooleanProperty;
import org.koishi.launcher.h2co3.core.utils.Logging;
import org.koishi.launcher.h2co3.core.utils.file.FileTools;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.logging.Level;
import java.util.stream.Collectors;

public final class LocalModFile implements Comparable<LocalModFile> {

    private final ModManager modManager;
    private final LocalMod mod;
    private final String name;
    private final Description description;
    private final String authors;
    private final String version;
    private final String gameVersion;
    private final String url;
    private final String fileName;
    private final String logoPath;
    private final BooleanProperty activeProperty;
    private Path file;

    public LocalModFile(ModManager modManager, LocalMod mod, Path file, String name, Description description) {
        this(modManager, mod, file, name, description, "", "", "", "", "");
    }

    public LocalModFile(ModManager modManager, LocalMod mod, Path file, String name, Description description, String authors, String version, String gameVersion, String url, String logoPath) {
        this.modManager = modManager;
        this.mod = mod;
        this.file = file;
        this.name = name;
        this.description = description;
        this.authors = authors;
        this.version = version;
        this.gameVersion = gameVersion;
        this.url = url;
        this.logoPath = logoPath;

        activeProperty = new SimpleBooleanProperty(this, "active", !modManager.isDisabled(file)) {
            @Override
            protected void invalidated() {
                if (isOld()) return;

                Path path = LocalModFile.this.file.toAbsolutePath();

                try {
                    if (get())
                        LocalModFile.this.file = modManager.enableMod(path);
                    else
                        LocalModFile.this.file = modManager.disableMod(path);
                } catch (IOException e) {
                    Logging.LOG.log(Level.SEVERE, "Unable to invert state of mod file " + path, e);
                }
            }
        };

        fileName = FileTools.getNameWithoutExtension(ModManager.getModName(file));

        if (isOld()) {
            mod.getOldFiles().add(this);
        } else {
            mod.getFiles().add(this);
        }
    }

    public ModManager getModManager() {
        return modManager;
    }

    public LocalMod getMod() {
        return mod;
    }

    public Path getFile() {
        return file;
    }

    public ModLoaderType getModLoaderType() {
        return mod.getModLoaderType();
    }

    public String getId() {
        return mod.getId();
    }

    public String getName() {
        return name;
    }

    public Description getDescription() {
        return description;
    }

    public String getAuthors() {
        return authors;
    }

    public String getVersion() {
        return version;
    }

    public String getGameVersion() {
        return gameVersion;
    }

    public String getUrl() {
        return url;
    }

    public String getLogoPath() {
        return logoPath;
    }

    public BooleanProperty activeProperty() {
        return activeProperty;
    }

    public boolean isActive() {
        return activeProperty.get();
    }

    public void setActive(boolean active) {
        activeProperty.set(active);
    }

    public String getFileName() {
        return fileName;
    }

    public boolean isOld() {
        return modManager.isOld(file);
    }

    public void setOld(boolean old) throws IOException {
        file = modManager.setOld(this, old);

        if (old) {
            mod.getFiles().remove(this);
            mod.getOldFiles().add(this);
        } else {
            mod.getOldFiles().remove(this);
            mod.getFiles().add(this);
        }
    }

    public void disable() throws IOException {
        file = modManager.disableMod(file);
    }

    public ModUpdate checkUpdates(String gameVersion, RemoteModRepository repository) throws IOException {
        Optional<RemoteMod.VersionMod> currentVersion = repository.getRemoteVersionByLocalFile(this, file);
        if (!currentVersion.isPresent()) return null;
        List<RemoteMod.VersionMod> remoteVersionMods = repository.getRemoteVersionsById(currentVersion.get().modid())
                .filter(version -> version.gameVersions().contains(gameVersion))
                .filter(version -> version.loaders().contains(getModLoaderType()))
                .filter(version -> version.datePublished().compareTo(currentVersion.get().datePublished()) > 0)
                .sorted(Comparator.comparing(RemoteMod.VersionMod::datePublished).reversed())
                .collect(Collectors.toList());
        if (remoteVersionMods.isEmpty()) return null;
        return new ModUpdate(this, currentVersion.get(), remoteVersionMods);
    }

    @Override
    public int compareTo(LocalModFile o) {
        return getFileName().compareTo(o.getFileName());
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof LocalModFile && Objects.equals(getFileName(), ((LocalModFile) obj).getFileName());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getFileName());
    }

    public static class ModUpdate {
        private final LocalModFile localModFile;
        private final RemoteMod.VersionMod currentVersionMod;
        private final List<RemoteMod.VersionMod> candidates;

        public ModUpdate(LocalModFile localModFile, RemoteMod.VersionMod currentVersionMod, List<RemoteMod.VersionMod> candidates) {
            this.localModFile = localModFile;
            this.currentVersionMod = currentVersionMod;
            this.candidates = candidates;
        }

        public LocalModFile getLocalMod() {
            return localModFile;
        }

        public RemoteMod.VersionMod getCurrentVersion() {
            return currentVersionMod;
        }

        public List<RemoteMod.VersionMod> getCandidates() {
            return candidates;
        }
    }

    public static class Description {
        private final List<Part> parts;

        public Description(String text) {
            this.parts = new ArrayList<>();
            this.parts.add(new Part(text, "black"));
        }

        public Description(List<Part> parts) {
            this.parts = parts;
        }

        public List<Part> getParts() {
            return parts;
        }

        @Override
        public String toString() {
            StringBuilder builder = new StringBuilder();
            for (Part part : parts) {
                builder.append(part.text);
            }
            return builder.toString();
        }

        public static class Part {
            private final String text;
            private final String color;

            public Part(String text) {
                this(text, "");
            }

            public Part(String text, String color) {
                this.text = Objects.requireNonNull(text);
                this.color = Objects.requireNonNull(color);
            }

            public String getText() {
                return text;
            }

            public String getColor() {
                return color;
            }
        }
    }
}
