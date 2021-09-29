package net.pubnative.lite.sdk.utils;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

public class EncodingUtils {
    private static final String TAG = EncodingUtils.class.getSimpleName();

    public static String urlEncode(String value) {
        try {
            return URLEncoder.encode(value, "utf-8");
        } catch (UnsupportedEncodingException e) {
            Logger.e(TAG, "Error url encoding string: ", e);
            return "";
        }
    }
}
