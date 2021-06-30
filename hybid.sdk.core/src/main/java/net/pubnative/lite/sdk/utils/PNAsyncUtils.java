package net.pubnative.lite.sdk.utils;

import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;

import java.util.concurrent.Executor;

public class PNAsyncUtils {
    private static final String TAG = PNAsyncUtils.class.getSimpleName();
    private static final Executor sExecutor;
    private static final Handler sUiThreadHandler;

    static {
        sExecutor = AsyncTask.THREAD_POOL_EXECUTOR;
        sUiThreadHandler = new Handler(Looper.getMainLooper());
    }

    @SafeVarargs
    public static <P> void safeExecuteOnExecutor(final AsyncTask<P, ?, ?> asyncTask, final P... params) {
        if (asyncTask != null) {
            if (Looper.getMainLooper() == Looper.myLooper()) {
                asyncTask.executeOnExecutor(sExecutor, params);
            } else {
                Logger.d(TAG, "Posting task for execution on main thread.");
                sUiThreadHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        asyncTask.executeOnExecutor(sExecutor, params);
                    }
                });
            }
        } else {
            Logger.e(TAG, "Error executing an AsyncTask that is null.");
        }
    }
}
