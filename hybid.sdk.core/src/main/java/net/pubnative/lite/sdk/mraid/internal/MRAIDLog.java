// The MIT License (MIT)
//
// Copyright (c) 2018 PubNative GmbH
//
// Permission is hereby granted, free of charge, to any person obtaining a copy
// of this software and associated documentation files (the "Software"), to deal
// in the Software without restriction, including without limitation the rights
// to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
// copies of the Software, and to permit persons to whom the Software is
// furnished to do so, subject to the following conditions:
//
// The above copyright notice and this permission notice shall be included in all
// copies or substantial portions of the Software.
//
// THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
// IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
// FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
// AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
// LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
// OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
// SOFTWARE.
//
package net.pubnative.lite.sdk.mraid.internal;

import android.util.Log;

/**
 * Created by erosgarciaponte on 05.01.18.
 */

public class MRAIDLog {
    private static final String TAG = "HyBid-MRAID";

    public enum LOG_LEVEL {

        verbose(1),
        debug(2),
        info(3),
        warning(4),
        error(5),
        none(6);

        private final int value;

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
