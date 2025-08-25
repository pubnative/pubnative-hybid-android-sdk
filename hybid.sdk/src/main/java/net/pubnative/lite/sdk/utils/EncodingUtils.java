// HyBid SDK License
//
// https://github.com/pubnative/pubnative-hybid-android-sdk/blob/main/LICENSE
//
package net.pubnative.lite.sdk.utils;

import android.text.TextUtils;

import net.pubnative.lite.sdk.HyBid;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

public class EncodingUtils {
    private static final String TAG = EncodingUtils.class.getSimpleName();

    public static String urlEncode(String value) {
        if (TextUtils.isEmpty(value))
            return "";
        try {
            return URLEncoder.encode(value, "utf-8");
        } catch (UnsupportedEncodingException e) {
            HyBid.reportException(e);
            Logger.e(TAG, "Error url encoding string: ", e);
            return "";
        }
    }
}