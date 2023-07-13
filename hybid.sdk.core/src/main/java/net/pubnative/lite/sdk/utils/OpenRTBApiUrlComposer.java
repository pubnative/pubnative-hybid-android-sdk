// The MIT License (MIT)
//
// Copyright (c) 2020 PubNative GmbH
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