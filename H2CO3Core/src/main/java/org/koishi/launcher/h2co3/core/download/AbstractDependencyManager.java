package org.koishi.launcher.h2co3.core.download;

public abstract class AbstractDependencyManager implements DependencyManager {

    public abstract DownloadProvider getDownloadProvider();

    @Override
    public abstract DefaultCacheRepository getCacheRepository();

    @Override
    public VersionList<?> getVersionList(String id) {
        return getDownloadProvider().getVersionListById(id);
    }
}