package net.pubnative.lite.sdk.vpaid.helpers;

import net.pubnative.lite.sdk.utils.Logger;
import net.pubnative.lite.sdk.vpaid.models.vast.Tracking;
import net.pubnative.lite.sdk.vpaid.utils.HttpUtil;

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

    public static void postEventByType(List<Tracking> events, String eventType) {
        if (events == null) {
            return;
        }
        for (Tracking event : events) {
            if (event.getEvent().equalsIgnoreCase(eventType)) {
                post(event.getText());
            }
        }
    }

    public static synchronized void post(final String url) {
        if (sUsedEvents.contains(url)) {
            return;
        } else {
            sUsedEvents.add(url);
        }

        sExecutor.submit(new Runnable() {
            @Override
            public void run() {
                Logger.d(LOG_TAG, url);
                HttpUtil.sendRequest(url, null, null);
            }
        });
    }

    public static void clear() {
        sUsedEvents.clear();
    }

}
