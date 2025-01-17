package org.koishi.launcher.h2co3.core.game.download;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.jetbrains.annotations.NotNull;
import org.koishi.launcher.h2co3.core.utils.task.Task;

import java.time.Instant;
import java.util.List;
import java.util.Objects;

/**
 * The remote version.
 */
public class RemoteVersion implements Comparable<RemoteVersion> {

    private final String libraryId;
    private final String gameVersion;
    private final String selfVersion;
    private final Instant releaseDate;
    private final List<String> urls;
    private final Type type;

    /**
     * Constructor.
     *
     * @param gameVersion the Minecraft version that this remote version suits.
     * @param selfVersion the version string of the remote version.
     * @param urls        the installer or universal jar original URL.
     */
    public RemoteVersion(String libraryId, String gameVersion, String selfVersion, Instant releaseDate, List<String> urls) {
        this(libraryId, gameVersion, selfVersion, releaseDate, Type.UNCATEGORIZED, urls);
    }

    /**
     * Constructor.
     *
     * @param gameVersion the Minecraft version that this remote version suits.
     * @param selfVersion the version string of the remote version.
     * @param urls        the installer or universal jar URL.
     */
    public RemoteVersion(String libraryId, String gameVersion, String selfVersion, Instant releaseDate, Type type, List<String> urls) {
        this.libraryId = Objects.requireNonNull(libraryId);
        this.gameVersion = Objects.requireNonNull(gameVersion);
        this.selfVersion = Objects.requireNonNull(selfVersion);
        this.releaseDate = releaseDate;
        this.urls = Objects.requireNonNull(urls);
        this.type = Objects.requireNonNull(type);
    }

    public String getLibraryId() {
        return libraryId;
    }

    public String getGameVersion() {
        return gameVersion;
    }

    public String getSelfVersion() {
        return selfVersion;
    }

    public String getFullVersion() {
        return getSelfVersion();
    }

    public Instant getReleaseDate() {
        return releaseDate;
    }

    public List<String> getUrls() {
        return urls;
    }

    public Type getVersionType() {
        return type;
    }

    public Task<Version> getInstallTask(DefaultDependencyManager dependencyManager, Version baseVersion) {
        throw new UnsupportedOperationException(this + " cannot be installed yet");
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof RemoteVersion && Objects.equals(selfVersion, ((RemoteVersion) obj).selfVersion);
    }

    @Override
    public int hashCode() {
        return selfVersion.hashCode();
    }

    @Override
    public @NotNull String toString() {
        return new ToStringBuilder(this)
                .append("selfVersion", selfVersion)
                .append("gameVersion", gameVersion)
                .toString();
    }

    @Override
    public int compareTo(RemoteVersion o) {
        // newer versions are smaller than older versions
        return VersionNumber.asVersion(o.selfVersion).compareTo(VersionNumber.asVersion(selfVersion));
    }

    public enum Type {
        UNCATEGORIZED,
        RELEASE,
        SNAPSHOT,
        OLD
    }
}
