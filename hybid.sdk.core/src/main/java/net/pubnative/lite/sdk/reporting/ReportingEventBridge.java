package net.pubnative.lite.sdk.reporting;

import android.text.TextUtils;

import net.pubnative.lite.sdk.analytics.Reporting;
import net.pubnative.lite.sdk.analytics.ReportingEvent;
import net.pubnative.lite.sdk.models.AdSize;
import net.pubnative.lite.sdk.utils.Logger;


public class ReportingEventBridge {

    private static final String TAG = ReportingEvent.class.getSimpleName();

    ReportingEvent reportingEvent;

    public ReportingEventBridge() {
        reportingEvent = new ReportingEvent();
    }

    public ReportingEventBridge(String adFormat) {
        reportingEvent = new ReportingEvent();
        setCustomString(Reporting.Key.AD_FORMAT, adFormat);
    }

    public ReportingEventBridge(String adFormat, AdSize adSize) {
        reportingEvent = new ReportingEvent();

        if (!TextUtils.isEmpty(adFormat)) {
            setCustomString(Reporting.Key.AD_FORMAT, adFormat);
        }
        setCustomString(Reporting.Key.AD_SIZE, adSize.toString());
    }

    public String getAdFormat() {
        return reportingEvent.getAdFormat();
    }

    public void setAdSize(Object adSize) {
        if (adSize instanceof AdSize) {
            reportingEvent.setAdSize(adSize.toString());
        } else {
            Logger.e(TAG, "object must be an instance of AdSize");
        }
    }

    public String getAdSize() {
        return reportingEvent.getAdSize();
    }

    public void setCustomString(String key, String value) {
        reportingEvent.setCustomString(key, value);
    }

    public void setCustomInteger(String key, long value) {
        reportingEvent.setCustomInteger(key, value);
    }

    public void setCustomDecimal(String key, double value) {
        reportingEvent.setCustomDecimal(key, value);
    }

    public void setCustomBoolean(String key, boolean value) {
        reportingEvent.setCustomBoolean(key, value);
    }

    public ReportingEvent getReportingEvent() {
        return reportingEvent;
    }

    public String getFormattedEventJson() {
        return reportingEvent.toString();
    }
}