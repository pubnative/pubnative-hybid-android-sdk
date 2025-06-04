// HyBid SDK License
//
// https://github.com/pubnative/pubnative-hybid-android-sdk/blob/main/LICENSE
//
package net.pubnative.lite.sdk.utils;

import android.net.Uri;
import android.text.TextUtils;

import net.pubnative.lite.sdk.models.request.OpenRTBAdRequest;

/**
 * Created by erosgarciaponte on 22.01.18.
 */

public final class OpenRTBApiUrlComposer {
    public static String buildUrl(String baseUrl, OpenRTBAdRequest adRequest) {
        // Base URL
        Uri.Builder uriBuilder = Uri.parse(baseUrl).buildUpon();
        uriBuilder.appendPath("bid");
        uriBuilder.appendPath("v1");
        uriBuilder.appendPath("request");

        // Appending parameters
        if (!TextUtils.isEmpty(adRequest.appToken)) {
            uriBuilder.appendQueryParameter("apptoken", adRequest.appToken);
        }

        if (!TextUtils.isEmpty(adRequest.zoneId)) {
            uriBuilder.appendQueryParameter("zoneid", adRequest.zoneId);
        }

        return uriBuilder.build().toString();
    }
}