package net.pubnative.lite.sdk.vpaid.helpers;

import android.content.Context;
import android.text.TextUtils;

import net.pubnative.lite.sdk.HyBid;
import net.pubnative.lite.sdk.network.PNHttpClient;
import net.pubnative.lite.sdk.vpaid.macros.MacroHelper;
import net.pubnative.lite.sdk.vpaid.models.vast.Tracking;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class EventTracker {

    private static Set<String> sUsedEvents = new HashSet<>();

    private EventTracker() {
    }

    public static void postEventByType(Context context, List<Tracking> events, String eventType, MacroHelper macroHelper) {
        if (events == null) {
            return;
        }
        for (Tracking event : events) {
            if (event.getEvent().equalsIgnoreCase(eventType)) {
                post(context, event.getText(), macroHelper);
            }
        }
    }

    public static synchronized void post(Context context, final String url, MacroHelper macroHelper) {
        if (sUsedEvents.contains(url)) {
            return;
        } else {
            sUsedEvents.add(url);
        }

        String processedUrl = url;
        if (macroHelper != null) {
            processedUrl = macroHelper.processUrl(processedUrl);
        }

        Map<String, String> headers = new HashMap<>();
        String userAgent = HyBid.getDeviceInfo().getUserAgent();
        if (!TextUtils.isEmpty(userAgent)){
            headers.put("User-Agent", userAgent);
        }

        PNHttpClient.makeRequest(context, processedUrl, headers, null, false, new PNHttpClient.Listener() {
            @Override
            public void onSuccess(String response) {

            }

            @Override
            public void onFailure(Throwable error) {

            }
        });
    }

    public static void clear() {
        sUsedEvents.clear();
    }

}
