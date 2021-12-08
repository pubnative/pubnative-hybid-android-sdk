package net.pubnative.lite.sdk.vpaid.helpers;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

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

    private static final Set<String> sUsedEvents = new HashSet<>();

    private EventTracker() {

    }

    public static synchronized void postEventByType(Context context, List<Tracking> events, String eventType, MacroHelper macroHelper, boolean ignoreIfExist) {
        if (events == null) {
            return;
        }

        for (Tracking event : events) {
            if (event.getEvent().equalsIgnoreCase(eventType)) {
                post(context, event.getText(), macroHelper, ignoreIfExist);
            }
        }
    }

    public static synchronized void post(Context context, final String url, MacroHelper macroHelper, boolean ignoreIfExist) {

        if (ignoreIfExist && sUsedEvents.contains(url)) {
            return;
        }

        if (TextUtils.isEmpty(url)) {
            return;
        }

        String processedUrl = url;
        if (macroHelper != null) {
            processedUrl = macroHelper.processUrl(processedUrl);
        }

        Map<String, String> headers = new HashMap<>();
        String userAgent = HyBid.getDeviceInfo().getUserAgent();
        if (!TextUtils.isEmpty(userAgent)) {
            headers.put("User-Agent", userAgent);
        }

        PNHttpClient.makeRequest(context, processedUrl, headers, null, false, new PNHttpClient.Listener() {
            @Override
            public void onSuccess(String response) {
                Log.d("onSuccess", response);
            }

            @Override
            public void onFailure(Throwable error) {
                Log.d("onFailure", error.toString());
            }
        });

        sUsedEvents.add(url);

    }

    public static void clear() {
        sUsedEvents.clear();
    }
}
