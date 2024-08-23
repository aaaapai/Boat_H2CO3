package org.koishi.launcher.h2co3.core.utils.task;

import static org.koishi.launcher.h2co3.core.utils.Lang.threadPool;

import android.os.Handler;
import android.os.Looper;

import org.koishi.launcher.h2co3.core.utils.Logging;

import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.TimeUnit;

public final class Schedulers {

    private static volatile ExecutorService IO_EXECUTOR;

    private Schedulers() {
    }

    /**
     * Get singleton instance of the thread pool for I/O operations,
     * usually for reading files from disk, or Internet connections.
     * <p>
     * This thread pool has no more than 4 threads, and number of threads will get
     * reduced if concurrency is less than thread number.
     *
     * @return Thread pool for I/O operations.
     */
    public static ExecutorService io() {
        if (IO_EXECUTOR == null) {
            synchronized (Schedulers.class) {
                if (IO_EXECUTOR == null) {
                    IO_EXECUTOR = threadPool("IO", true, 4, 10, TimeUnit.SECONDS);
                }
            }
        }

        return IO_EXECUTOR;
    }

    public static Executor androidUIThread() {
        Handler handler = new Handler(Looper.getMainLooper());
        return handler::post;
    }

    public static Executor defaultScheduler() {
        return ForkJoinPool.commonPool();
    }

    public static synchronized void shutdown() {
        Logging.LOG.info("Shutting down executor services.");

        // shutdownNow will interrupt all threads.
        // So when we want to close the app, no threads need to be waited for finish.
        // Sometimes it resolves the problem that the app does not exit.

        if (IO_EXECUTOR != null)
            IO_EXECUTOR.shutdownNow();
    }

}
