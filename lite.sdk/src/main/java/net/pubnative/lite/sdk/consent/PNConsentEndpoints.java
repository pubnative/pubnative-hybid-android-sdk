package net.pubnative.lite.sdk.consent;

import android.net.Uri;

public class PNConsentEndpoints {
    private static final String SCHEME = "https";
    private static final String AUTHORITY = "backend.pubnative.net";
    private static final String CONSENT_PATH = "consent";
    private static final String API_VERSION = "v1";

    private static final String PARAM_DEVICE_ID = "did";
    private static final String PARAM_DEVICE_ID_TYPE = "did_type";

    public static String getCheckConsentUrl(String deviceId, String deviceIdType) {
        return new Uri.Builder()
                .scheme(SCHEME)
                .authority(AUTHORITY)
                .appendPath(CONSENT_PATH)
                .appendPath(API_VERSION)
                .appendQueryParameter(PARAM_DEVICE_ID, deviceId)
                .appendQueryParameter(PARAM_DEVICE_ID_TYPE, deviceIdType)
                .build()
                .toString();
    }

    public static String getRevokeConsentUrl(String deviceId, String deviceIdType) {
        return new Uri.Builder()
                .scheme(SCHEME)
                .authority(AUTHORITY)
                .appendPath(CONSENT_PATH)
                .appendPath(API_VERSION)
                .appendQueryParameter(PARAM_DEVICE_ID, deviceId)
                .appendQueryParameter(PARAM_DEVICE_ID_TYPE, deviceIdType)
                .build()
                .toString();
    }

    public static String getConsentUrl() {
        return new Uri.Builder()
                .scheme(SCHEME)
                .authority(AUTHORITY)
                .appendPath(CONSENT_PATH)
                .appendPath(API_VERSION)
                .build()
                .toString();
    }
}
