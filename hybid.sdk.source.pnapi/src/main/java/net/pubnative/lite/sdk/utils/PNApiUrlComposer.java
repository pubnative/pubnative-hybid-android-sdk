// HyBid SDK License
//
// https://github.com/pubnative/pubnative-hybid-android-sdk/blob/main/LICENSE
//
package net.pubnative.lite.sdk.utils;

import android.net.Uri;
import android.text.TextUtils;

import net.pubnative.lite.sdk.models.Topic;
import net.pubnative.lite.sdk.models.PNAdRequest;
import net.pubnative.lite.sdk.models.bidstream.BidParam;
import net.pubnative.lite.sdk.models.bidstream.Signal;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Created by erosgarciaponte on 22.01.18.
 */

public final class PNApiUrlComposer {

    public static String getUrlQuery(String baseUrl, PNAdRequest adRequest) {
        Uri uri = buildUri(baseUrl, adRequest);
        return uri.getQuery();
    }

    public static String buildUrl(String baseUrl, PNAdRequest adRequest) {
        return buildUri(baseUrl, adRequest).toString();
    }

    private static Uri buildUri(String baseUrl, PNAdRequest adRequest) {
        Uri.Builder uriBuilder = Uri.parse(baseUrl).buildUpon();
        uriBuilder.appendPath("api");
        uriBuilder.appendPath("v3");
        uriBuilder.appendPath("native");

        // Appending parameters
        if (!TextUtils.isEmpty(adRequest.appToken)) {
            uriBuilder.appendQueryParameter("apptoken", adRequest.appToken);
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

        if (!TextUtils.isEmpty(adRequest.make)) {
            uriBuilder.appendQueryParameter("make", adRequest.make);
        }

        if (!TextUtils.isEmpty(adRequest.deviceHeight)) {
            uriBuilder.appendQueryParameter("dh", adRequest.deviceHeight);
        }

        if (!TextUtils.isEmpty(adRequest.deviceWidth)) {
            uriBuilder.appendQueryParameter("dw", adRequest.deviceWidth);
        }

        if (!TextUtils.isEmpty(adRequest.orientation)) {
            uriBuilder.appendQueryParameter("scro", adRequest.orientation);
        }

        if (!TextUtils.isEmpty(adRequest.ppi)) {
            uriBuilder.appendQueryParameter("ppi", adRequest.ppi);
        }

        if (!TextUtils.isEmpty(adRequest.pxratio)) {
            uriBuilder.appendQueryParameter("pxratio", adRequest.pxratio);
        }

        if (!TextUtils.isEmpty(adRequest.js)) {
            uriBuilder.appendQueryParameter("js", adRequest.js);
        }

        if (!TextUtils.isEmpty(adRequest.soundSetting)) {
            uriBuilder.appendQueryParameter("aud", adRequest.soundSetting);
        }

        if (!TextUtils.isEmpty(adRequest.dnt)) {
            uriBuilder.appendQueryParameter("dnt", adRequest.dnt);
        }

        if (!TextUtils.isEmpty(adRequest.al)) {
            uriBuilder.appendQueryParameter("al", adRequest.al);
        }

        if (!TextUtils.isEmpty(adRequest.width)) {
            uriBuilder.appendQueryParameter("w", adRequest.width);
        }

        if (!TextUtils.isEmpty(adRequest.height)) {
            uriBuilder.appendQueryParameter("h", adRequest.height);
        }

        if (!TextUtils.isEmpty(adRequest.mf)) {
            uriBuilder.appendQueryParameter("mf", adRequest.mf);
        }

        if (!TextUtils.isEmpty(adRequest.af)) {
            uriBuilder.appendQueryParameter("af", adRequest.af);
        }

        if (!TextUtils.isEmpty(adRequest.zoneId)) {
            uriBuilder.appendQueryParameter("zoneid", adRequest.zoneId);
        }

        if (!TextUtils.isEmpty(adRequest.testMode)) {
            uriBuilder.appendQueryParameter("test", adRequest.testMode);
        }

        if (!TextUtils.isEmpty(adRequest.locale)) {
            uriBuilder.appendQueryParameter("locale", adRequest.locale);
        }

        if (!TextUtils.isEmpty(adRequest.language)) {
            uriBuilder.appendQueryParameter("language", adRequest.language);
        }

        if (!TextUtils.isEmpty(adRequest.langb)) {
            uriBuilder.appendQueryParameter("langb", adRequest.langb);
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

        if (!TextUtils.isEmpty(adRequest.displaymanager)) {
            uriBuilder.appendQueryParameter("displaymanager", adRequest.displaymanager);
        }

        if (!TextUtils.isEmpty(adRequest.displaymanagerver)) {
            uriBuilder.appendQueryParameter("displaymanagerver", adRequest.displaymanagerver);
        }

        if (!TextUtils.isEmpty(adRequest.omidpn)) {
            uriBuilder.appendQueryParameter("omidpn", adRequest.omidpn);
        }

        if (!TextUtils.isEmpty(adRequest.omidpv)) {
            uriBuilder.appendQueryParameter("omidpv", adRequest.omidpv);
        }

        if (!TextUtils.isEmpty(adRequest.rv)) {
            uriBuilder.appendQueryParameter("rv", adRequest.rv);
        }

        if (!TextUtils.isEmpty(adRequest.usprivacy)) {
            uriBuilder.appendQueryParameter("usprivacy", adRequest.usprivacy);
        }

        if (!TextUtils.isEmpty(adRequest.userconsent)) {
            uriBuilder.appendQueryParameter("userconsent", adRequest.userconsent);
        }

        if (!TextUtils.isEmpty(adRequest.gppstring)) {
            uriBuilder.appendQueryParameter("gpp", adRequest.gppstring);
        }

        if (!TextUtils.isEmpty(adRequest.gppsid)) {
            uriBuilder.appendQueryParameter("gppsid", adRequest.gppsid);
        }

        if (!TextUtils.isEmpty(adRequest.carrier)) {
            uriBuilder.appendQueryParameter("carrier", adRequest.carrier);
        }

        if (!TextUtils.isEmpty(adRequest.connectiontype)) {
            uriBuilder.appendQueryParameter("connectiontype", adRequest.connectiontype);
        }

        if (!TextUtils.isEmpty(adRequest.mccmnc)) {
            uriBuilder.appendQueryParameter("mccmnc", adRequest.mccmnc);
        }

        if (!TextUtils.isEmpty(adRequest.mccmncsim)) {
            uriBuilder.appendQueryParameter("mccmncsim", adRequest.mccmncsim);
        }

        if (!TextUtils.isEmpty(adRequest.geofetch)) {
            uriBuilder.appendQueryParameter("geofetch", adRequest.geofetch);
        }

        if (!TextUtils.isEmpty(adRequest.sua)) {
            uriBuilder.appendQueryParameter("sua", adRequest.sua);
        }

        if (!TextUtils.isEmpty(adRequest.ae)) {
            uriBuilder.appendQueryParameter("ae", adRequest.ae);
        }

        if (!TextUtils.isEmpty(adRequest.protocol)) {
            uriBuilder.appendQueryParameter("protocol", adRequest.protocol);
        }

        if (!TextUtils.isEmpty(adRequest.api)) {
            uriBuilder.appendQueryParameter("api", adRequest.api);
        }

        if (!TextUtils.isEmpty(adRequest.impdepth)) {
            uriBuilder.appendQueryParameter("impdepth", adRequest.impdepth);
        }

        if (!TextUtils.isEmpty(adRequest.ageofapp)) {
            uriBuilder.appendQueryParameter("ageofapp", adRequest.ageofapp);
        }

        if (!TextUtils.isEmpty(adRequest.sessionduration)) {
            uriBuilder.appendQueryParameter("sessionduration", adRequest.sessionduration);
        }

        if (!adRequest.getSignals().isEmpty()) {
            for (Signal signal : adRequest.getSignals()) {
                for (Field field : signal.getClass().getDeclaredFields()) {
                    final BidParam bidParam = field.getAnnotation(BidParam.class);
                    if (bidParam == null) {
                        continue;
                    }
                    try {
                        Class typeClass = field.getType();
                        String value;
                        if (Iterable.class.isAssignableFrom(typeClass)) {
                            value = String.valueOf(field.get(signal));
                            value = value.substring(1, value.length() - 1);
                            value = value.replaceAll("\\s+", "");
                        } else {
                            value = String.valueOf(field.get(signal));
                        }
                        if (!TextUtils.isEmpty(value) && !value.equals("null") && !TextUtils.isEmpty(bidParam.name())) {
                            uriBuilder.appendQueryParameter(bidParam.name(), value);
                        }
                    } catch (IllegalAccessException e) {

                    }
                }
            }
        }

        if (adRequest.topics != null && !adRequest.topics.isEmpty()) {

            Map<String, ArrayList<String>> sortedTopics = new HashMap<>();
            for (Topic topic : adRequest.topics) {
                Long taxonomyVersion = topic.getTaxonomyVersion();
                String taxonomyVersionName = topic.getTaxonomyVersionName();
                String key = String.valueOf(taxonomyVersion).concat(",").concat(taxonomyVersionName.replaceAll("\\s", "+"));
                if (!sortedTopics.containsKey(key)) {
                    sortedTopics.put(key, new ArrayList<>());
                }
                Objects.requireNonNull(sortedTopics.get(key)).add(String.valueOf(topic.getId()));
            }
            String queryParamValue = "";
            for (Map.Entry<String, ArrayList<String>> entry : sortedTopics.entrySet()) {
                String key = entry.getKey();
                ArrayList<String> value = entry.getValue();
                queryParamValue = queryParamValue.concat(key).concat(",")
                        .concat(String.join(",", value)).concat("_");
            }
            queryParamValue = queryParamValue.substring(0, queryParamValue.length() - 1);
            uriBuilder.appendQueryParameter("psut", queryParamValue);
        }

        if (!TextUtils.isEmpty(adRequest.vg)) {
            uriBuilder.appendQueryParameter("vg", adRequest.vg);
        }

        return uriBuilder.build();
    }
}