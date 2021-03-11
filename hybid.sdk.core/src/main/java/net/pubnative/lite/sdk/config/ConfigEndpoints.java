package net.pubnative.lite.sdk.config;

import android.net.Uri;

import net.pubnative.lite.sdk.HyBid;

public class ConfigEndpoints {
    private static final String SCHEME = "https";
    private static final String AUTHORITY = "hbrc.pubnative.net";

    public static String getConfigUrl() {
        return new Uri.Builder()
                .scheme(SCHEME)
                .authority(AUTHORITY)
                .appendPath("config")
                .appendPath("v1")
                .appendPath("default")
                .appendPath(HyBid.getAppToken())
                .build()
                .toString();
    }
}
