// HyBid SDK License
//
// https://github.com/pubnative/pubnative-hybid-android-sdk/blob/main/LICENSE
//
package net.pubnative.lite.sdk.utils;

import android.net.Uri;
import android.text.TextUtils;

import net.pubnative.lite.sdk.models.PNAdRequest;
import net.pubnative.lite.sdk.models.bidstream.BidParam;
import net.pubnative.lite.sdk.models.bidstream.Signal;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by erosgarciaponte on 22.01.18.
 */

public final class PNApiUrlComposer {
    private static final String TAG = PNApiUrlComposer.class.getSimpleName();
    private static final Map<Class<?>, Map<Field, BidParam>> SIGNAL_FIELD_CACHE = new ConcurrentHashMap<>();

    private interface ParamAccessor {
        String get(PNAdRequest adRequest);
    }

    private static final class Param {
        final String name;
        final ParamAccessor accessor;

        Param(String name, ParamAccessor accessor) {
            this.name = name;
            this.accessor = accessor;
        }
    }

    private static Param param(String name, ParamAccessor accessor) {
        return new Param(name, accessor);
    }

    // Order matters: it is the order of the query string sent to the ad server.
    private static final List<Param> LEADING_PARAMS = Arrays.asList(
            param("apptoken", r -> r.appToken),
            param("os", r -> r.os),
            param("osver", r -> r.osver),
            param("devicemodel", r -> r.devicemodel),
            param("make", r -> r.make),
            param("dh", r -> r.deviceHeight),
            param("dw", r -> r.deviceWidth),
            param("devicetype", r -> r.deviceType),
            param("scro", r -> r.orientation),
            param("ppi", r -> r.ppi),
            param("pxratio", r -> r.pxratio),
            param("js", r -> r.js),
            param("aud", r -> r.soundSetting),
            param("dnt", r -> r.dnt),
            param("al", r -> r.al),
            param("w", r -> r.width),
            param("h", r -> r.height),
            param("mf", r -> r.mf),
            param("af", r -> r.af),
            param("zoneid", r -> r.zoneId),
            param("test", r -> r.testMode),
            param("locale", r -> r.locale),
            param("language", r -> r.language),
            param("langb", r -> r.langb),
            param("lat", r -> r.latitude),
            param("long", r -> r.longitude),
            param("gender", r -> r.gender),
            param("age", r -> r.age),
            param("bundleid", r -> r.bundleid),
            param("keywords", r -> r.keywords),
            param("coppa", r -> r.coppa),
            param("gid", r -> r.gid),
            param("gidmd5", r -> r.gidmd5),
            param("gidsha1", r -> r.gidsha1),
            param("displaymanager", r -> r.displaymanager),
            param("displaymanagerver", r -> r.displaymanagerver),
            param("omidpn", r -> r.omidpn),
            param("omidpv", r -> r.omidpv),
            param("rv", r -> r.rv),
            param("usprivacy", r -> r.usprivacy),
            param("userconsent", r -> r.userconsent),
            param("gpp", r -> r.gppstring),
            param("gppsid", r -> r.gppsid),
            param("carrier", r -> r.carrier),
            param("connectiontype", r -> r.connectiontype),
            param("mccmnc", r -> r.mccmnc),
            param("mccmncsim", r -> r.mccmncsim),
            param("geofetch", r -> r.geofetch),
            param("sua", r -> r.sua),
            param("ae", r -> r.ae),
            param("protocol", r -> r.protocol),
            param("api", r -> r.api),
            param("impdepth", r -> r.impdepth),
            param("ageofapp", r -> r.ageofapp),
            param("sessionduration", r -> r.sessionduration),
            param("mediationadunitid", r -> r.mediationAdUnitId)
    );

    private static final List<Param> TRAILING_PARAMS = Arrays.asList(
            param("vg", r -> r.vg),
            param("hver", r -> r.hver)
    );

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

        appendParams(uriBuilder, adRequest, LEADING_PARAMS);
        appendSignalParams(uriBuilder, adRequest.getSignals());
        appendParams(uriBuilder, adRequest, TRAILING_PARAMS);

        return uriBuilder.build();
    }

    private static void appendParams(Uri.Builder uriBuilder, PNAdRequest adRequest, List<Param> params) {
        for (Param param : params) {
            String value = param.accessor.get(adRequest);
            if (!TextUtils.isEmpty(value)) {
                uriBuilder.appendQueryParameter(param.name, value);
            }
        }
    }

    private static void appendSignalParams(Uri.Builder uriBuilder, List<Signal> signals) {
        for (Signal signal : signals) {
            Map<Field, BidParam> fieldCache = getSignalFieldCache(signal.getClass());

            for (Map.Entry<Field, BidParam> entry : fieldCache.entrySet()) {
                appendSignalParam(uriBuilder, signal, entry.getKey(), entry.getValue());
            }
        }
    }

    private static void appendSignalParam(Uri.Builder uriBuilder, Signal signal, Field field, BidParam bidParam) {
        try {
            String value = String.valueOf(field.get(signal));
            if (Iterable.class.isAssignableFrom(field.getType())) {
                value = value.substring(1, value.length() - 1).replaceAll("\\s+", "");
            }
            if (!TextUtils.isEmpty(value) && !value.equals("null") && !TextUtils.isEmpty(bidParam.name())) {
                uriBuilder.appendQueryParameter(bidParam.name(), value);
            }
        } catch (IllegalAccessException e) {
            // Field is made accessible when cached; nothing to send if access still fails.
        }
    }

    private static Map<Field, BidParam> getSignalFieldCache(Class<?> signalClass) {
        Map<Field, BidParam> fieldMap = SIGNAL_FIELD_CACHE.get(signalClass);
        if (fieldMap == null) {
            synchronized (SIGNAL_FIELD_CACHE) {
                fieldMap = SIGNAL_FIELD_CACHE.get(signalClass);
                if (fieldMap == null) {
                    fieldMap = new HashMap<>();
                    try {
                        for (Field field : signalClass.getDeclaredFields()) {
                            BidParam bidParam = field.getAnnotation(BidParam.class);
                            if (bidParam != null) {
                                field.setAccessible(true);
                                fieldMap.put(field, bidParam);
                            }
                        }
                        // Cache on successful reflection
                        SIGNAL_FIELD_CACHE.put(signalClass, fieldMap);
                    } catch (SecurityException e) {
                        Logger.e(TAG, "Cannot reflect signal fields for " + signalClass.getSimpleName() + " - security exception: " + e.getMessage());
                    } catch (Exception e) {
                        Logger.e(TAG, "Unexpected error reflecting signal fields for " + signalClass.getSimpleName(), e);
                    }
                }
            }
        }
        return fieldMap;
    }
}