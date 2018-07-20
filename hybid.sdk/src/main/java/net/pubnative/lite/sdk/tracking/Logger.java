package net.pubnative.lite.sdk.tracking;

import android.util.Log;

/**
 * Created by erosgarciaponte on 12.02.18.
 */

final class Logger {

    private static final String LOG_TAG = "HyBidCrashTracker";
    private static volatile boolean enabled = true;

    private Logger() {
    }

    static void info(String message) {
        if (enabled) {
            Log.i(LOG_TAG, message);
        }
    }

    static void warn(String message) {
        if (enabled) {
            Log.w(LOG_TAG, message);
        }
    }

    static void warn(String message, Throwable throwable) {
        if (enabled) {
            Log.w(LOG_TAG, message, throwable);
        }
    }

    static void setEnabled(boolean enabled) {
        Logger.enabled = enabled;
    }

}
