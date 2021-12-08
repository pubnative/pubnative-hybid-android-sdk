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
package net.pubnative.lite.sdk.utils;

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

        private final int mValue;

        Level(int value) {
            mValue = value;
        }

        public int getValue() {
            return mValue;
        }
    }

    private static final String TAG = "HyBid";
    private static Level sLogLevel = Level.info;

    public static void setLogLevel(Level logLevel) {
        sLogLevel = logLevel;
    }

    public static void d(String subTag, String msg) {
        d(subTag, msg, null);
    }

    public static void w(String subTag, String msg) {
        w(subTag, msg, null);
    }

    public static void e(String subTag, String msg) {
        e(subTag, msg, null);
    }

    public static void d(String subTag, String msg, Throwable throwable) {
        if (sLogLevel != null && sLogLevel.getValue() <= Level.debug.getValue()) {
            Log.d(TAG, "[" + subTag + "] " + msg, throwable);
        }
    }

    public static void w(String subTag, String msg, Throwable throwable) {
        if (sLogLevel != null && sLogLevel.getValue() <= Level.warning.getValue()) {
            Log.w(TAG, "[" + subTag + "] " + msg, throwable);
        }
    }

    public static void e(String subTag, String msg, Throwable throwable) {
        if (sLogLevel != null && sLogLevel.getValue() <= Level.error.getValue()) {
            Log.e(TAG, "[" + subTag + "] " + msg, throwable);
        }
    }
}
