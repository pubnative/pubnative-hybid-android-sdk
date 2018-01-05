package net.pubnative.tarantula.sdk.utils;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

/**
 * Created by erosgarciaponte on 05.01.18.
 */

public class Logger {
    public enum Level {
        verbose(1),
        debug(2),
        info(3),
        warning(4),
        error(5),
        none(6);

        private int mValue;

        Level(int value) {
            mValue = value;
        }

        public int getValue() {
            return mValue;
        }
    }

    @NonNull
    private static final String TAG = "Tarantula";
    @Nullable
    private static Level sLogLevel = Level.debug;

    public static void setLogLevel(@NonNull Level logLevel) {
        sLogLevel = logLevel;
    }

    public static void d(@Nullable String subTag, @Nullable String msg) {
        d(subTag, msg, null);
    }

    public static void w(@Nullable String subTag, @Nullable String msg) {
        w(subTag, msg, null);
    }

    public static void e(@Nullable String subTag, @Nullable String msg) {
        e(subTag, msg, null);
    }

    public static void d(@Nullable String subTag, @Nullable String msg, @Nullable Throwable throwable) {
        if (sLogLevel != null && sLogLevel.getValue() <= Level.debug.getValue()) {
            Log.d(TAG, "[" + subTag + "] " + msg, throwable);
        }
    }

    public static void w(@Nullable String subTag, @Nullable String msg, @Nullable Throwable throwable) {
        if (sLogLevel != null && sLogLevel.getValue() <= Level.warning.getValue()) {
            Log.w(TAG, "[" + subTag + "] " + msg, throwable);
        }
    }

    public static void e(@Nullable String subTag, @Nullable String msg, @Nullable Throwable throwable) {
        if (sLogLevel != null && sLogLevel.getValue() <= Level.error.getValue()) {
            Log.e(TAG, "[" + subTag + "] " + msg, throwable);
        }
    }
}
