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
package net.pubnative.lite.sdk.utils;

import android.net.Uri;
import android.text.TextUtils;

import net.pubnative.lite.sdk.models.AdRequest;

/**
 * Created by erosgarciaponte on 22.01.18.
 */

public final class PNApiUrlComposer {
    public static String buildUrl(String baseUrl, AdRequest adRequest) {
        // Base URL
        Uri.Builder uriBuilder = Uri.parse(baseUrl).buildUpon();
        uriBuilder.appendPath("api");
        uriBuilder.appendPath("v3");
        uriBuilder.appendPath("native");

        // Appending parameters
        if (!TextUtils.isEmpty(adRequest.apptoken)) {
            uriBuilder.appendQueryParameter("apptoken", adRequest.apptoken);
        }

        if (!TextUtils.isEmpty(adRequest.os)) {
            uriBuilder.appendQueryParameter("os", adRequest.os);
        }

        if (!TextUtils.isEmpty(adRequest.osver)) {
            uriBuilder.appendQueryParameter("osver", adRequest.osver);
        }

        if (!TextUtils.isEmpty(adRequest.devicemodel)) {
            uriBuilder.appendQueryParameter("devicemodel", adRequest.devicemodel);
        }

        if (!TextUtils.isEmpty(adRequest.dnt)) {
            uriBuilder.appendQueryParameter("dnt", adRequest.dnt);
        }

        if (!TextUtils.isEmpty(adRequest.al)) {
            uriBuilder.appendQueryParameter("al", adRequest.al);
        }

        if (!TextUtils.isEmpty(adRequest.mf)) {
            uriBuilder.appendQueryParameter("mf", adRequest.mf);
        }

        if (!TextUtils.isEmpty(adRequest.af)) {
            uriBuilder.appendQueryParameter("af", adRequest.af);
        }

        if (!TextUtils.isEmpty(adRequest.zoneid)) {
            uriBuilder.appendQueryParameter("zoneid", adRequest.zoneid);
        }

        if (!TextUtils.isEmpty(adRequest.testMode)) {
            uriBuilder.appendQueryParameter("test", adRequest.testMode);
        }

        if (!TextUtils.isEmpty(adRequest.locale)) {
            uriBuilder.appendQueryParameter("locale", adRequest.locale);
        }

        if (!TextUtils.isEmpty(adRequest.latitude)) {
            uriBuilder.appendQueryParameter("lat", adRequest.latitude);
        }

        if (!TextUtils.isEmpty(adRequest.longitude)) {
            uriBuilder.appendQueryParameter("long", adRequest.longitude);
        }

        if (!TextUtils.isEmpty(adRequest.gender)) {
            uriBuilder.appendQueryParameter("gender", adRequest.gender);
        }

        if (!TextUtils.isEmpty(adRequest.age)) {
            uriBuilder.appendQueryParameter("age", adRequest.age);
        }

        if (!TextUtils.isEmpty(adRequest.bundleid)) {
            uriBuilder.appendQueryParameter("bundleid", adRequest.bundleid);
        }

        if (!TextUtils.isEmpty(adRequest.keywords)) {
            uriBuilder.appendQueryParameter("keywords", adRequest.keywords);
        }

        if (!TextUtils.isEmpty(adRequest.coppa)) {
            uriBuilder.appendQueryParameter("coppa", adRequest.coppa);
        }

        if (!TextUtils.isEmpty(adRequest.gid)) {
            uriBuilder.appendQueryParameter("gid", adRequest.gid);
        }

        if (!TextUtils.isEmpty(adRequest.gidmd5)) {
            uriBuilder.appendQueryParameter("gidmd5", adRequest.gidmd5);
        }

        if (!TextUtils.isEmpty(adRequest.gidsha1)) {
            uriBuilder.appendQueryParameter("gidsha1", adRequest.gidsha1);
        }

        return uriBuilder.build().toString();
    }
}