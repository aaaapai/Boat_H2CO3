/*
 * //
 * // Created by cainiaohh on 2024-03-31.
 * //
 */

/*
 * //
 * // Created by cainiaohh on 2024-03-31.
 * //
 */

package org.koishi.launcher.h2co3.utils.download;

import java.util.concurrent.atomic.AtomicInteger;

public class DownloadItem {
    private final String name;
    private final String path;
    private final String url;
    private final int size;
    private final AtomicInteger progress;

    public DownloadItem(String name, String path, String url, int size) {
        this.name = name;
        this.path = path;
        this.url = url;
        this.size = size;
        this.progress = new AtomicInteger(0);
    }

    public String getName() {
        return name;
    }

    public String getPath() {
        return path;
    }

    public String getUrl() {
        return url;
    }

    public int getSize() {
        return size;
    }

    public int getProgress() {
        return progress.get();
    }

    public void setProgress(int progress) {
        this.progress.set(progress);
    }

    public boolean isCompleted() {
        return progress.get() == 100;
    }
}