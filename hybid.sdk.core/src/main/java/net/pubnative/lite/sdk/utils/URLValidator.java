// HyBid SDK License
//
// https://github.com/pubnative/pubnative-hybid-android-sdk/blob/main/LICENSE
//
package net.pubnative.lite.sdk.utils;

import android.util.Patterns;
import android.webkit.URLUtil;

public class URLValidator {

    public static boolean isValidURL(String stringURL) {
        if (stringURL.trim().isEmpty()) return false;
        // Encode square brackets for validation purposes
        String encodedUrl = stringURL.replace("[", "%5B").replace("]", "%5D");
        return URLUtil.isValidUrl(encodedUrl) && Patterns.WEB_URL.matcher(encodedUrl).matches();
    }
}