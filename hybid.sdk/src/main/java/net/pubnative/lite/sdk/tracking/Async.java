package net.pubnative.lite.sdk.tracking;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by erosgarciaponte on 12.02.18.
 */

class Async {
    // This is pretty much the same settings as AsyncTask#THREAD_POOL_EXECUTOR, except that it has
    // a minimum of 1 for the core pool size, instead of 2. We could probably use
    // AsyncTask.THREAD_POOL_EXECUTOR directly, but that requires API >= 11.
    private static final int CPU_COUNT = Runtime.getRuntime().availableProcessors();
    private static final int CORE_POOL_SIZE = Math.max(1, Math.min(CPU_COUNT - 1, 4));
    private static final int MAXIMUM_POOL_SIZE = CPU_COUNT * 2 + 1;
    private static final int KEEP_ALIVE_SECONDS = 30;
    static final BlockingQueue<Runnable> POOL_WORK_QUEUE =
            new LinkedBlockingQueue<>(128);
    private static final ThreadFactory THREAD_FACTORY = new ThreadFactory() {
        private final AtomicInteger count = new AtomicInteger(1);

        public Thread newThread(Runnable runnable) {
            return new Thread(runnable, "PNLiteCrashTracking Thread #" + count.getAndIncrement());
        }
    };
    private static final Executor EXECUTOR = new ThreadPoolExecutor(
            CORE_POOL_SIZE, MAXIMUM_POOL_SIZE, KEEP_ALIVE_SECONDS, TimeUnit.SECONDS,
            POOL_WORK_QUEUE, THREAD_FACTORY);

    static void run(Runnable task) throws RejectedExecutionException {
        EXECUTOR.execute(task);
    }
}
