// HyBid SDK License
//
// https://github.com/pubnative/pubnative-hybid-android-sdk/blob/main/LICENSE
//
package net.pubnative.lite.sdk.analytics;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;

import net.pubnative.lite.sdk.analytics.tracker.ReportingTracker;
import net.pubnative.lite.sdk.analytics.tracker.ReportingTrackerCallback;
import net.pubnative.lite.sdk.utils.Logger;

import java.util.ArrayList;
import java.util.List;

public class ReportingController {
    private static final String TAG = ReportingController.class.getSimpleName();

    private final List<ReportingEventCallback> mListeners;
    private List<ReportingEvent> adEventList;

    private final List<ReportingTrackerCallback> mTrackerListeners;
    private List<ReportingTracker> adFiredTrackers;

    public ReportingController() {
        mListeners = new ArrayList<>();
        mTrackerListeners = new ArrayList<>();
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

    public synchronized void reportFiredTracker(ReportingTracker firedTracker) {
        new Handler(Looper.getMainLooper()).post(() -> {
            for (int i = 0; i < mTrackerListeners.size(); i++) {
                // Double check to handle multi-thread listener release
                try {
                    if (i < mTrackerListeners.size()) {
                        ReportingTrackerCallback callback = mTrackerListeners.get(i);
                        if (callback != null) {
                            callback.onFire(firedTracker);
                        }
                    }
                } catch (Exception e) {
                    //Just to catch exception while
                    Logger.d("exception - " + ReportingController.class.getSimpleName(), e.toString());
                }
            }
        });
    }

    public void addTrackerCallback(ReportingTrackerCallback callback) {
        synchronized (this) {
            if (callback != null && !mTrackerListeners.contains(callback)) {
                mTrackerListeners.add(callback);
            }
        }
    }

    public boolean removeTrackerCallback(ReportingTrackerCallback callback) {
        synchronized (this) {
            if (callback == null) {
                return false;
            }

            int index = mTrackerListeners.indexOf(callback);
            if (index == -1) {
                return false;
            } else {
                mTrackerListeners.remove(index);
                return true;
            }
        }
    }

    public List<ReportingTracker> getFiredTrackersList() {
        return adFiredTrackers;
    }

    public void clearFiredTrackerstList() {
        if (this.adFiredTrackers != null) this.adFiredTrackers.clear();
    }
}
