// HyBid SDK License
//
// https://github.com/pubnative/pubnative-hybid-android-sdk/blob/main/LICENSE
//
package net.pubnative.lite.sdk.utils;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class BitmapDownloaderExecutor {

    private static final int CORE_POOL_SIZE = 4;
    private static final int MAX_POOL_SIZE = 10;
    private static final long KEEP_ALIVE_TIME = 60L;
    private static final TimeUnit TIME_UNIT = TimeUnit.SECONDS;
    private static final BlockingQueue<Runnable> WORK_QUEUE = new LinkedBlockingQueue<>();

    private static final ExecutorService sExecutor = new ThreadPoolExecutor(
            CORE_POOL_SIZE,
            MAX_POOL_SIZE,
            KEEP_ALIVE_TIME,
            TIME_UNIT,
            WORK_QUEUE,
            new ThreadPoolExecutor.CallerRunsPolicy()
    );

    private BitmapDownloaderExecutor() {
    }

    synchronized public static ExecutorService getExecutor() {
        return sExecutor;
    }
}
