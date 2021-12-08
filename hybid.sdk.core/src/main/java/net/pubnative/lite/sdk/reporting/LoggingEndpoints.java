package net.pubnative.lite.sdk.reporting;

import android.net.Uri;

public class LoggingEndpoints {
    private static final String SCHEME = "https";
    private static final String AUTHORITY = "api.pubnative.net";

    public static String getLoggingUrl(String appToken) {
        return new Uri.Builder()
                .scheme(SCHEME)
                .authority(AUTHORITY)
                .appendPath("log")
                .appendQueryParameter("apptoken", appToken)
                .build()
                .toString();
    }
}
