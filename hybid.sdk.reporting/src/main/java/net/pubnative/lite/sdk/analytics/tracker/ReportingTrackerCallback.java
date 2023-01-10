package net.pubnative.lite.sdk.analytics.tracker;

import net.pubnative.lite.sdk.analytics.ReportingEvent;

public interface ReportingTrackerCallback {
    void onFire(ReportingTracker firedTracker);
}
