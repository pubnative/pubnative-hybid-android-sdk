// HyBid SDK License
//
// https://github.com/pubnative/pubnative-hybid-android-sdk/blob/main/LICENSE
//
package net.pubnative.lite.sdk.network;

import android.os.Looper;

import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Ad-serving executor. When the pool is saturated, a task submitted off the main thread runs inline
 * on the submitting thread; one submitted from the main thread, or during such an inline run, is
 * rejected. This applies to {@link #execute(Runnable)} and to tasks submitted via {@link #getExecutor()}.
 */
public class ApiExecutor implements Executor {

    private static final String TAG = "ApiExecutor";

    private static volatile ApiExecutor instance;

    private static volatile ExecutorService sExecutor;

    private ApiExecutor() {
    }

    public static ApiExecutor getInstance() {
        if (instance == null) {
            synchronized (ApiExecutor.class) {
                if (instance == null) {
                    instance = new ApiExecutor();
                }
            }
        }
        return instance;
    }

    public ExecutorService getExecutor() {
        if (sExecutor == null) {
            synchronized (ApiExecutor.class) {
                if (sExecutor == null) {
                    sExecutor = new ThreadPoolExecutor(
                            Runtime.getRuntime().availableProcessors(),
                            Runtime.getRuntime().availableProcessors() * 2,
                            60L,
                            TimeUnit.SECONDS,
                            new LinkedBlockingQueue<>(100),
                            new CallerRunsOffMainThreadPolicy()
                    );
                }
            }
        }
        return sExecutor;
    }

    /**
     * @throws RejectedExecutionException if the pool is saturated and this is called from the main
     *                                    thread or during an inline run of another task
     */
    @Override
    public void execute(Runnable runnable) {
        getExecutor().execute(() -> ExecutorTaskGuard.run(TAG, runnable));
    }

    static final class CallerRunsOffMainThreadPolicy implements RejectedExecutionHandler {

        private final ThreadLocal<Boolean> runningInline = new ThreadLocal<>();

        @Override
        public void rejectedExecution(Runnable runnable, ThreadPoolExecutor executor) {
            if (executor.isShutdown()) {
                return;
            }
            if (Looper.myLooper() == Looper.getMainLooper()) {
                throw new RejectedExecutionException("ApiExecutor is saturated, refusing to run the task on the main thread");
            }
            if (runningInline.get() != null) {
                throw new RejectedExecutionException("ApiExecutor is saturated, refusing a nested inline run");
            }
            runningInline.set(Boolean.TRUE);
            try {
                runnable.run();
            } finally {
                runningInline.remove();
            }
        }
    }
}
