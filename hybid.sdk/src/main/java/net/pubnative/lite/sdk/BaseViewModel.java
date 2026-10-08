// HyBid SDK License
//
// https://github.com/pubnative/pubnative-hybid-android-sdk/blob/main/LICENSE
//
package net.pubnative.lite.sdk;

import android.content.Context;

import net.pubnative.lite.sdk.analytics.Reporting;
import net.pubnative.lite.sdk.analytics.ReportingController;
import net.pubnative.lite.sdk.analytics.ReportingEvent;
import net.pubnative.lite.sdk.models.Ad;
import net.pubnative.lite.sdk.models.IntegrationType;
import net.pubnative.lite.sdk.utils.TouchSizeManager;
import net.pubnative.lite.sdk.vpaid.helpers.EventTracker;

import java.util.List;

public class BaseViewModel {

    protected final ReportingController mReportingController;

    public BaseViewModel() {
        mReportingController = HyBid.getReportingController();
    }

    /**
     * The ad this view model is showing, or null before it is set. Subclasses that hold an ad
     * override this so the close-control helpers below can read it.
     */
    protected Ad getAd() {
        return null;
    }

    /** Normal tap area (dp) for the close control, or no growth when there is no ad. */
    public int getTouchSize() {
        Ad ad = getAd();
        return ad != null ? ad.getCloseButtonTouchSize() : TouchSizeManager.NO_GROWTH;
    }

    /** Initial tap area (dp) for the close control, used before the ad is clicked. */
    public int getInitialTouchSize() {
        Ad ad = getAd();
        return ad != null ? ad.getCloseButtonInitialTouchSize() : TouchSizeManager.NO_GROWTH;
    }

    /** True once the ad has been clicked (any surface). Read from the ad so it survives recreation. */
    public boolean hasImpressionBeenClicked() {
        Ad ad = getAd();
        return ad != null && ad.isImpressionClicked();
    }

    public void markImpressionClicked() {
        Ad ad = getAd();
        if (ad != null) ad.setImpressionClicked();
    }

    protected void invokeOnContentInfoClick(IntegrationType integrationType, Ad ad, String adFormat) {
        if (mReportingController != null && HyBid.isReportingEnabled()) {
            ReportingEvent reportingEvent = new ReportingEvent();
            reportingEvent.setEventType(Reporting.EventType.CONTENT_INFO_CLICK);
            reportingEvent.setTimestamp(System.currentTimeMillis());
            reportingEvent.setAdFormat(adFormat);
            reportingEvent.setPlatform(Reporting.Platform.ANDROID);
            reportingEvent.setSdkVersion(HyBid.getSDKVersionInfo(integrationType));
            if (ad != null) {
                reportingEvent.setImpId(ad.getSessionId());
                reportingEvent.setCampaignId(ad.getCampaignId());
                reportingEvent.setConfigId(ad.getConfigId());
            }
            mReportingController.reportEvent(reportingEvent);
        }
    }

    protected void invokeOnEndCardClosed(Boolean defaultEndCardCloseTracked) {
        if (mReportingController != null && HyBid.isReportingEnabled()) {
            ReportingEvent reportingEvent = new ReportingEvent();
            reportingEvent.setTimestamp(System.currentTimeMillis());
            if (defaultEndCardCloseTracked) {
                reportingEvent.setEventType(Reporting.EventType.DEFAULT_ENDCARD_CLOSE);
                reportingEvent.setCustomString(Reporting.Key.END_CARD_TYPE, Reporting.Key.END_CARD_TYPE_DEFAULT);
            } else {
                reportingEvent.setEventType(Reporting.EventType.CUSTOM_ENDCARD_CLOSE);
                reportingEvent.setCustomString(Reporting.Key.END_CARD_TYPE, Reporting.Key.END_CARD_TYPE_CUSTOM);
            }

            mReportingController.reportEvent(reportingEvent);
        }
    }

    protected void invokeOnEndCardSkipped(Boolean defaultEndCardSkipTracked) {
        if (mReportingController != null && HyBid.isReportingEnabled()) {
            ReportingEvent reportingEvent = new ReportingEvent();
            reportingEvent.setTimestamp(System.currentTimeMillis());
            if (defaultEndCardSkipTracked) {
                reportingEvent.setEventType(Reporting.EventType.DEFAULT_ENDCARD_SKIP);
                reportingEvent.setCustomString(Reporting.Key.END_CARD_TYPE, Reporting.Key.END_CARD_TYPE_DEFAULT);
            }

            mReportingController.reportEvent(reportingEvent);
        }
    }

    protected void invokeOnCustomCTAShow(Ad ad, String adFormat) {
        if (mReportingController != null && HyBid.isReportingEnabled()) {
            ReportingEvent reportingEvent = new ReportingEvent();
            reportingEvent.setEventType(Reporting.EventType.CUSTOM_CTA_SHOW);
            reportingEvent.setAdFormat(adFormat);
            reportingEvent.setCreativeType(Reporting.CreativeType.VIDEO);
            reportingEvent.setPlatform(Reporting.Platform.ANDROID);
            reportingEvent.setSdkVersion(HyBid.getSDKVersionInfo(IntegrationType.STANDALONE));
            if (ad != null) {
                reportingEvent.setImpId(ad.getSessionId());
                reportingEvent.setCampaignId(ad.getCampaignId());
                reportingEvent.setConfigId(ad.getConfigId());
            }
            reportingEvent.setTimestamp(System.currentTimeMillis());
            mReportingController.reportEvent(reportingEvent);
        }
    }

    protected void invokeOnCustomCTAClick(String eventType, Ad ad, String adFormat) {
        if (mReportingController != null && HyBid.isReportingEnabled()) {
            ReportingEvent reportingEvent = new ReportingEvent();
            reportingEvent.setEventType(eventType);
            reportingEvent.setAdFormat(adFormat);
            reportingEvent.setCreativeType(Reporting.CreativeType.VIDEO);
            reportingEvent.setPlatform(Reporting.Platform.ANDROID);
            reportingEvent.setSdkVersion(HyBid.getSDKVersionInfo(IntegrationType.STANDALONE));
            if (ad != null) {
                reportingEvent.setImpId(ad.getSessionId());
                reportingEvent.setCampaignId(ad.getCampaignId());
                reportingEvent.setConfigId(ad.getConfigId());
            }
            reportingEvent.setTimestamp(System.currentTimeMillis());
            mReportingController.reportEvent(reportingEvent);
        }
    }

    protected void postTrackerEvents(Context context, List<String> viewTrackers) {
        if (viewTrackers != null && !viewTrackers.isEmpty()) {
            for (String tracker : viewTrackers) {
                EventTracker.post(context, tracker, null, true);
            }
        }
    }
}