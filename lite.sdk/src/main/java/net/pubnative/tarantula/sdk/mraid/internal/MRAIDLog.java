package net.pubnative.tarantula.sdk.mraid.internal;

import android.util.Log;

/**
 * Created by erosgarciaponte on 05.01.18.
 */

public class MRAIDLog {
    private static final String TAG = "Tarantula-MRAID";

    public enum LOG_LEVEL {

        verbose(1),
        debug(2),
        info(3),
        warning(4),
        error(5),
        none(6);

        private int value;

        LOG_LEVEL(int value) {
            this.value = value;

        }

        public int getValue() {
            return value;
        }

    }

    private static LOG_LEVEL LEVEL = LOG_LEVEL.warning;

    public static void d(String msg) {
        if (LEVEL.getValue() <= LOG_LEVEL.debug.getValue()) {
            Log.d(TAG, msg);
        }
    }

    public static void e(String msg) {
        if (LEVEL.getValue() <= LOG_LEVEL.error.getValue()) {
            Log.e(TAG, msg);
        }
    }

    public static void i(String msg) {
        if (LEVEL.getValue() <= LOG_LEVEL.info.getValue()) {
            Log.i(TAG, msg);
        }
    }

    public static void v(String msg) {
        if (LEVEL.getValue() <= LOG_LEVEL.verbose.getValue()) {
            Log.v(TAG, msg);
        }
    }

    public static void w(String msg) {
        if (LEVEL.getValue() <= LOG_LEVEL.warning.getValue()) {
            Log.w(TAG, msg);
        }
    }

    public static void d(String subTag, String msg) {
        if (LEVEL.getValue() <= LOG_LEVEL.debug.getValue()) {
            msg = "[" + subTag + "] " + msg;
            Log.d(TAG, msg);
        }
    }

    public static void e(String subTag, String msg) {
        if (LEVEL.getValue() <= LOG_LEVEL.error.getValue()) {
            msg = "[" + subTag + "] " + msg;
            Log.e(TAG, msg);
        }
    }

    public static void i(String subTag, String msg) {
        if (LEVEL.getValue() <= LOG_LEVEL.info.getValue()) {
            msg = "[" + subTag + "] " + msg;
            Log.i(TAG, msg);
        }
    }

    public static void v(String subTag, String msg) {
        if (LEVEL.getValue() <= LOG_LEVEL.verbose.getValue()) {
            msg = "[" + subTag + "] " + msg;
            Log.v(TAG, msg);
        }
    }

    public static void w(String subTag, String msg) {
        if (LEVEL.getValue() <= LOG_LEVEL.warning.getValue()) {
            msg = "[" + subTag + "] " + msg;
            Log.w(TAG, msg);
        }
    }

    public static void setLoggingLevel(LOG_LEVEL logLevel) {
        Log.i(TAG, "Changing logging level from :" + LEVEL + ". To:" + logLevel);
        LEVEL = logLevel;
    }

    public static LOG_LEVEL getLoggingLevel() {
        return LEVEL;
    }
}
