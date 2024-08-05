package org.koishi.launcher.h2co3.utils.download;

import org.koishi.launcher.h2co3.dialog.TaskDialog;

import java.util.function.Consumer;

public final class TaskCancellationAction {
    public static TaskCancellationAction NORMAL = new TaskCancellationAction(() -> {
    });

    private final Consumer<TaskDialog> cancellationAction;

    public TaskCancellationAction(Runnable cancellationAction) {
        this.cancellationAction = it -> cancellationAction.run();
    }

    public TaskCancellationAction(Consumer<TaskDialog> cancellationAction) {
        this.cancellationAction = cancellationAction;
    }

    public Consumer<TaskDialog> getCancellationAction() {
        return cancellationAction;
    }
}