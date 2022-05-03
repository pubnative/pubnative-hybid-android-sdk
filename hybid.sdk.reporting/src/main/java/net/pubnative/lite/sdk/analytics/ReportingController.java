package net.pubnative.lite.sdk.analytics;

import java.util.ArrayList;
import java.util.List;

public class ReportingController {
    private static final String TAG = ReportingController.class.getSimpleName();

    private final List<ReportingEventCallback> mListeners;
    private List<ReportingEvent> adEventList;

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
        for (int i = 0; i < mListeners.size(); i++) {
            ReportingEventCallback callback = mListeners.get(i);
            if (callback != null) {
                callback.onEvent(event);
            }
        }
    }

    public void cacheAdEventList(List<ReportingEvent> eventList) {
        this.adEventList = eventList;
    }

    public List<ReportingEvent> getAdEventList() {
        return adEventList;
    }

    public void clearAdEventList() {
        if (this.adEventList != null)
            this.adEventList.clear();
    }
}
