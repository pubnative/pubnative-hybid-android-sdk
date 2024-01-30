package net.pubnative.lite.sdk.utils;

import android.util.Patterns;
import android.webkit.URLUtil;

public class URLValidator {

    public static boolean isValidURL(String stringURL) {
        if (stringURL.trim().isEmpty()) return false;
        return URLUtil.isValidUrl(stringURL) && Patterns.WEB_URL.matcher(stringURL).matches();
    }
}