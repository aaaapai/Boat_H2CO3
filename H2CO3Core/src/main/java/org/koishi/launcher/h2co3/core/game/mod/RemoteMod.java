package org.koishi.launcher.h2co3.core.game.mod;

import static org.koishi.launcher.h2co3.core.utils.NetworkUtils.encodeLocation;

import org.koishi.launcher.h2co3.core.download.ModLoaderType;
import org.koishi.launcher.h2co3.core.game.mod.curse.CurseForgeRemoteModRepository;
import org.koishi.launcher.h2co3.core.game.mod.modrinth.ModrinthRemoteModRepository;
import org.koishi.launcher.h2co3.core.utils.task.FileDownloadTask;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

public class RemoteMod {
    private static RemoteMod EMPTY = null;
    private final String slug;
    private final String author;
    private final String title;
    private final String description;
    private final List<String> categories;
    private final String pageUrl;
    private final String iconUrl;
    private final IMod data;

    public RemoteMod(String slug, String author, String title, String description, List<String> categories, String pageUrl, String iconUrl, IMod data) {
        this.slug = slug;
        this.author = author;
        this.title = title;
        this.description = description;
        this.categories = categories;
        this.pageUrl = pageUrl;
        this.iconUrl = iconUrl;
        this.data = data;
    }

    public static void registerEmptyRemoteMod(RemoteMod empty) {
        EMPTY = empty;
    }

    public static RemoteMod getEmptyRemoteMod() {
        if (EMPTY == null) {
            throw new NullPointerException();
        }
        return EMPTY;
    }

    public String getSlug() {
        return slug;
    }

    public String getAuthor() {
        return author;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public List<String> getCategories() {
        return categories;
    }

    public String getPageUrl() {
        return pageUrl;
    }

    public String getIconUrl() {
        return iconUrl;
    }

    public IMod getData() {
        return data;
    }

    public enum VersionType {
        Release,
        Beta,
        Alpha
    }

    public enum DependencyType {
        REQUIRED,
        OPTIONAL,
        TOOL,
        INCLUDE,
        EMBEDDED,
        INCOMPATIBLE,
        BROKEN
    }

    public enum Type {
        CURSEFORGE(CurseForgeRemoteModRepository.MODS),
        MODRINTH(ModrinthRemoteModRepository.MODS);

        private final RemoteModRepository remoteModRepository;

        Type(RemoteModRepository remoteModRepository) {
            this.remoteModRepository = remoteModRepository;
        }

        public RemoteModRepository getRemoteModRepository() {
            return this.remoteModRepository;
        }
    }

    public interface IMod {
        List<RemoteMod> loadDependencies(RemoteModRepository modRepository) throws IOException;

        Stream<VersionMod> loadVersions(RemoteModRepository modRepository) throws IOException;
    }

    public interface IVersion {
        Type getType();
    }

    public static final class Dependency {
        private static Dependency BROKEN_DEPENDENCY = null;

        private final DependencyType type;

        private final RemoteModRepository remoteModRepository;

        private final String id;

        private transient RemoteMod remoteMod = null;

        private Dependency(DependencyType type, RemoteModRepository remoteModRepository, String modid) {
            this.type = type;
            this.remoteModRepository = remoteModRepository;
            this.id = modid;
        }

        public static Dependency ofGeneral(DependencyType type, RemoteModRepository remoteModRepository, String modid) {
            if (type == DependencyType.BROKEN) {
                return ofBroken();
            } else {
                return new Dependency(type, remoteModRepository, modid);
            }
        }

        public static Dependency ofBroken() {
            if (BROKEN_DEPENDENCY == null) {
                BROKEN_DEPENDENCY = new Dependency(DependencyType.BROKEN, null, null);
            }
            return BROKEN_DEPENDENCY;
        }

        public DependencyType getType() {
            return this.type;
        }

        public RemoteModRepository getRemoteModRepository() {
            return this.remoteModRepository;
        }

        public String getId() {
            return this.id;
        }

        public RemoteMod load() throws IOException {
            if (this.remoteMod == null) {
                if (this.type == DependencyType.BROKEN) {
                    this.remoteMod = RemoteMod.getEmptyRemoteMod();
                } else {
                    this.remoteMod = this.remoteModRepository.getModById(this.id);
                }
            }
            return this.remoteMod;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            Dependency that = (Dependency) o;

            if (type != that.type) return false;
            if (!remoteModRepository.equals(that.remoteModRepository)) return false;
            return id.equals(that.id);
        }

        @Override
        public int hashCode() {
            int result = type.hashCode();
            result = 31 * result + remoteModRepository.hashCode();
            result = 31 * result + id.hashCode();
            return result;
        }
    }

    public record VersionMod(IVersion self, String modid, String name, String version,
                             String changelog, Instant datePublished, VersionType versionType,
                             File file, List<Dependency> dependencies, List<String> gameVersions,
                             List<ModLoaderType> loaders) {
    }

    public static class File {
        private final Map<String, String> hashes;
        private final String url;
        private final String filename;

        public File(Map<String, String> hashes, String url, String filename) {
            this.hashes = hashes;
            this.url = url;
            this.filename = filename;
        }

        public Map<String, String> getHashes() {
            return hashes;
        }

        public FileDownloadTask.IntegrityCheck getIntegrityCheck() {
            if (hashes.containsKey("md5")) {
                return new FileDownloadTask.IntegrityCheck("MD5", hashes.get("sha1"));
            } else if (hashes.containsKey("sha1")) {
                return new FileDownloadTask.IntegrityCheck("SHA-1", hashes.get("sha1"));
            } else if (hashes.containsKey("sha512")) {
                return new FileDownloadTask.IntegrityCheck("SHA-256", hashes.get("sha1"));
            } else {
                return null;
            }
        }

        public String getUrl() throws UnsupportedEncodingException {
            return encodeLocation(url);
        }

        public String getFilename() {
            return filename;
        }
    }
}
