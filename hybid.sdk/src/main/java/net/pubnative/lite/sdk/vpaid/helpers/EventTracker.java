package net.pubnative.lite.sdk.vpaid.helpers;

import android.content.Context;

import net.pubnative.lite.sdk.network.PNHttpClient;
import net.pubnative.lite.sdk.vpaid.models.vast.Tracking;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class EventTracker {

    private static final String LOG_TAG = EventTracker.class.getSimpleName();

    private static ExecutorService sExecutor = Executors.newCachedThreadPool();
    private static Set<String> sUsedEvents = new HashSet<>();

    private EventTracker() {
    }

    public static void postEventByType(Context context, List<Tracking> events, String eventType) {
        if (events == null) {
            return;
        }
        for (Tracking event : events) {
            if (event.getEvent().equalsIgnoreCase(eventType)) {
                post(context, event.getText());
            }
        }
    }

    public static synchronized void post(Context context, final String url) {
        if (sUsedEvents.contains(url)) {
            return;
        } else {
            sUsedEvents.add(url);
        }

        PNHttpClient.makeRequest(url, null, null, false, new PNHttpClient.Listener() {
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
