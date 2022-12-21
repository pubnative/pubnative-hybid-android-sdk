package net.pubnative.lite.sdk.analytics;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import net.pubnative.lite.sdk.utils.Logger;

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
        synchronized (this) {
            if (callback != null && !mListeners.contains(callback)) {
                mListeners.add(callback);
            }
        }
    }

    public boolean removeCallback(ReportingEventCallback callback) {
        synchronized (this) {
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
    }

    public synchronized void reportEvent(ReportingEvent event) {
        new Handler(Looper.getMainLooper()).post(() -> {
            for (int i = 0; i < mListeners.size(); i++) {
                // Double check to handle multi-thread listener release
                try {
                    if (i < mListeners.size()) {
                        ReportingEventCallback callback = mListeners.get(i);
                        if (callback != null) {
                            callback.onEvent(event);
                        }
                    }
                } catch (Exception e) {
                    //Just to catch exception while
                    Logger.d("exception - " + ReportingController.class.getSimpleName(), e.toString());
                }
            }
        });
    }

    public void cacheAdEventList(List<ReportingEvent> eventList) {
        this.adEventList = eventList;
    }

    public List<ReportingEvent> getAdEventList() {
        return adEventList;
    }

    public void clearAdEventList() {
        if (this.adEventList != null) this.adEventList.clear();
    }
}
