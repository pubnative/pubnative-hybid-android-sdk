package net.pubnative.lite.sdk.analytics;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ReportingController {
    private static final String TAG = ReportingController.class.getSimpleName();

    private final List<ReportingEventCallback> mListeners;

    public ReportingController() {
        mListeners = new ArrayList<>();
    }

    public void addCallback(ReportingEventCallback callback) {
        if (callback != null && !mListeners.contains(callback)) {
            mListeners.add(callback);
        }
    }

    public boolean removeCallback(ReportingEventCallback callback) {
        if (callback == null) {
            return false;
        }

        int index = mListeners.indexOf(callback);
        if (index == -1) {
            return false;
        } else {
            mListeners.remove(index);
            return true;
        }
    }

    public void reportEvent(ReportingEvent event) {
        for (ReportingEventCallback callback : mListeners) {
            if (callback != null) {
                callback.onEvent(event);
            }
        }
    }
}
