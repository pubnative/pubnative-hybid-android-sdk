package net.pubnative.lite.sdk.utils;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class BitmapDownloaderExecutor {

    private static final ExecutorService sExecutor = Executors.newCachedThreadPool();

    private BitmapDownloaderExecutor() {
    }

    public static ExecutorService getExecutor() {
        return sExecutor;
    }
}
