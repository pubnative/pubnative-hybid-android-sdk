package net.pubnative.lite.sdk.network;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class ApiExecutor {

    private static volatile ApiExecutor instance;

    private static ExecutorService sExecutor;

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
            sExecutor = new ThreadPoolExecutor(
                    Runtime.getRuntime().availableProcessors(),
                    Runtime.getRuntime().availableProcessors() * 2,
                    60L,
                    TimeUnit.SECONDS,
                    new LinkedBlockingQueue<>(50),
                    new ThreadPoolExecutor.CallerRunsPolicy()
            );
        }
        return sExecutor;
    }

    public void execute(Runnable runnable) {
        getExecutor().submit(runnable);
    }
}