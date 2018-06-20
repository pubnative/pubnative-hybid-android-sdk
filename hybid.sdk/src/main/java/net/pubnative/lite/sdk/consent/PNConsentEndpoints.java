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
