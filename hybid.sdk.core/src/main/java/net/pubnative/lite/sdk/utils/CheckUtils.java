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

/**
 * Created by erosgarciaponte on 05.01.18.
 */

public class CheckUtils {
    private static final String TAG = CheckUtils.class.getSimpleName();

    public static void checkArgument(boolean expression, String errorMessage) {
        checkArgumentImpl(expression, true, errorMessage);
    }

    public static class NoThrow {
        private static boolean sStrictMode = false;

        public static void setStrictMode(boolean strictMode) {
            sStrictMode = strictMode;
        }

        public static boolean checkArgument(boolean expression, String errorMessage) {
            return checkArgumentImpl(expression, sStrictMode, errorMessage);
        }

        public static boolean checkNotNull(Object reference, String errorMessage) {
            return checkNotNullImpl(reference, sStrictMode, errorMessage);
        }
    }

    private static boolean checkArgumentImpl(boolean expression, boolean isThrowable, String errorMessage) {
        if (expression) {
            return true;
        }

        if (isThrowable) {
            throw new IllegalArgumentException(errorMessage);
        }

        Logger.e(TAG, errorMessage);
        return false;
    }

    private static boolean checkNotNullImpl(Object reference, boolean isThrowable,
                                            String errorMessage) {
        if (reference != null) {
            return true;
        }

        if (isThrowable) {
            throw new NullPointerException(errorMessage);
        }

        Logger.e(TAG, errorMessage);
        return false;
    }
}
