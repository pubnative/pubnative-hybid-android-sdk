// HyBid SDK License
//
// https://github.com/pubnative/pubnative-hybid-android-sdk/blob/main/LICENSE
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
