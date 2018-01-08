package net.pubnative.tarantula.sdk.managers;

/**
 * Created by erosgarciaponte on 08.01.18.
 */

import android.app.Application;
import android.support.annotation.NonNull;
import android.support.annotation.VisibleForTesting;

import com.jenzz.appstate.AppState;
import com.jenzz.appstate.adapter.rxjava2.RxAppStateMonitor;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import io.reactivex.functions.Consumer;

/**
 * Session depth starts at 0 and is incremented for each ad request attempt regardless of whether a response is received
 * or not. When the app is backgrounded for more than 30 seconds, the session depth counter starts over again.
 */
public class SessionDepthManager {
    private static final long SESSION_RESET_THRESHOLD_MS = TimeUnit.SECONDS.toMillis(30);
    @NonNull
    private final AtomicInteger mSessionDepth;
    private long mTimeBackgroundedMs;

    @VisibleForTesting
    public SessionDepthManager(@NonNull final Application application) {
        mSessionDepth = new AtomicInteger(0);
        RxAppStateMonitor.monitor(application)
                .subscribe(new Consumer<AppState>() {
                    @Override
                    public void accept(AppState appState) throws Exception {
                        handleAppState(appState);
                    }
                });
    }

    @VisibleForTesting
    void handleAppState(AppState appState) {
        if (appState == AppState.BACKGROUND) {
            mTimeBackgroundedMs = System.currentTimeMillis();
        } else if (appState == AppState.FOREGROUND) {
            if (System.currentTimeMillis() - mTimeBackgroundedMs > SESSION_RESET_THRESHOLD_MS) {
                mSessionDepth.set(0);
            }
        }
    }

    public int getSessionDepth() {
        return mSessionDepth.get();
    }

    public void incrementSessionDepth() {
        mSessionDepth.incrementAndGet();
    }
}
