package org.koishi.launcher.h2co3.utils.download;

import org.koishi.launcher.h2co3.dialog.H2CO3DownloadTaskDialog;

import java.util.function.Consumer;

public final class TaskCancellationAction {
    public static TaskCancellationAction NORMAL = new TaskCancellationAction(() -> {
    });

    private final Consumer<H2CO3DownloadTaskDialog> cancellationAction;

    public TaskCancellationAction(Runnable cancellationAction) {
        this.cancellationAction = it -> cancellationAction.run();
    }

    public TaskCancellationAction(Consumer<H2CO3DownloadTaskDialog> cancellationAction) {
        this.cancellationAction = cancellationAction;
    }

    public Consumer<H2CO3DownloadTaskDialog> getCancellationAction() {
        return cancellationAction;
    }
}