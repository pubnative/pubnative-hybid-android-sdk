package net.pubnative.tarantula.sdk.utils;

import android.net.Uri;
import android.text.TextUtils;

import net.pubnative.tarantula.sdk.models.AdRequest;

/**
 * Created by erosgarciaponte on 22.01.18.
 */

public final class TarantulaApiUrlComposer {
    public static String buildUrl(String baseUrl, AdRequest adRequest) {
        // Base URL
        Uri.Builder uriBuilder = Uri.parse(baseUrl).buildUpon();
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
