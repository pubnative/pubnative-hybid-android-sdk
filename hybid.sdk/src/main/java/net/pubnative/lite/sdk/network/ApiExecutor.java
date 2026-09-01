// HyBid SDK License
//
// https://github.com/pubnative/pubnative-hybid-android-sdk/blob/main/LICENSE
//
package net.pubnative.lite.sdk.network;

import net.pubnative.lite.sdk.utils.Logger;

import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

// Ad-serving executor: CallerRunsPolicy trades a possible main-thread inline run (only if the pool saturates) for guaranteed delivery, since ad requests can originate from the caller's thread.
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
                            new ThreadPoolExecutor.CallerRunsPolicy()
                    );
                }
            }
        }
        return sExecutor;
    }

    @Override
    public void execute(Runnable runnable) {
        getExecutor().execute(() -> {
            try {
                runnable.run();
            } catch (Exception exception) {
                Logger.e(TAG, "Uncaught exception in ApiExecutor task", exception);
            }
        });
    }
}
