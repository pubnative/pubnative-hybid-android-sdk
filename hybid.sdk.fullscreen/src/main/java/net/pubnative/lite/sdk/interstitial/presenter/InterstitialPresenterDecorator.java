// HyBid SDK License
//
// https://github.com/pubnative/pubnative-hybid-android-sdk/blob/main/LICENSE
//
package net.pubnative.lite.sdk.interstitial.presenter;

import android.text.TextUtils;

import net.pubnative.lite.sdk.CustomEndCardListener;
import net.pubnative.lite.sdk.HyBid;
import net.pubnative.lite.sdk.HyBidErrorCode;
import net.pubnative.lite.sdk.VideoListener;
import net.pubnative.lite.sdk.analytics.Reporting;
import net.pubnative.lite.sdk.analytics.ReportingController;
import net.pubnative.lite.sdk.analytics.ReportingEvent;
import net.pubnative.lite.sdk.models.Ad;
import net.pubnative.lite.sdk.models.AuxiliaryAdEventType;
import net.pubnative.lite.sdk.models.IntegrationType;
import net.pubnative.lite.sdk.models.SdkEventType;
import net.pubnative.lite.sdk.utils.AdTracker;
import net.pubnative.lite.sdk.utils.CheckUtils;
import net.pubnative.lite.sdk.utils.Logger;
import net.pubnative.lite.sdk.utils.json.JsonOperations;

import org.json.JSONObject;

/**
 * Created by erosgarciaponte on 09.01.18.
 */

public class InterstitialPresenterDecorator implements InterstitialPresenter, InterstitialPresenter.Listener, VideoListener, CustomEndCardListener {
    private static final String TAG = InterstitialPresenterDecorator.class.getSimpleName();
    private final InterstitialPresenter mInterstitialPresenter;
    private final AdTracker mAdTrackingDelegate;
    private final AdTracker mCustomEndCardTrackingDelegate;
    private final ReportingController mReportingController;
    private final InterstitialPresenter.Listener mListener;
    private VideoListener mVideoListener;
    private boolean mIsDestroyed = false;

    private boolean mImpressionTracked = false;
    private boolean mClickTracked = false;
    private boolean mDismissTracked = false;

    private boolean mDefaultEndCardImpressionTracked = false;
    private boolean mDefaultEndCardClickTracked = false;

    private boolean mCustomEndCardImpressionTracked = false;
    private boolean mVideoAdSkipped = false;
    private boolean mCustomEndCardClickTracked = false;
    private IntegrationType mIntegrationType;

    public InterstitialPresenterDecorator(InterstitialPresenter interstitialPresenter,
                                          AdTracker adTrackingDelegate,
                                          AdTracker customEndCardTrackingDelegate,
                                          ReportingController reportingController,
                                          Listener listener, IntegrationType integrationType) {
        mInterstitialPresenter = interstitialPresenter;
        mAdTrackingDelegate = adTrackingDelegate;
        mCustomEndCardTrackingDelegate = customEndCardTrackingDelegate;
        mReportingController = reportingController;
        mListener = listener;
        mIntegrationType = integrationType;
    }

    @Override
    public void setListener(InterstitialPresenter.Listener listener) {
        // We set the listener in the constructor instead
    }

    @Override
    public void setVideoListener(VideoListener listener) {
        mVideoListener = listener;
    }

    @Override
    public void setCustomEndCardListener(CustomEndCardListener listener) {
    }

    @Override
    public Ad getAd() {
        return mInterstitialPresenter.getAd();
    }

    @Override
    public void load() {
        if (!CheckUtils.NoThrow.checkArgument(!mIsDestroyed, "InterstitialPresenterDecorator is destroyed")) {
            return;
        }

        mInterstitialPresenter.load();
    }

    @Override
    public boolean isReady() {
        return mInterstitialPresenter.isReady();
    }

    @Override
    public void show() {
        if (!CheckUtils.NoThrow.checkArgument(!mIsDestroyed, "InterstitialPresenterDecorator is destroyed")) {
            return;
        }

        mInterstitialPresenter.show();
    }

    @Override
    public void destroy() {
        mInterstitialPresenter.destroy();
        mIsDestroyed = true;
    }

    @Override
    public JSONObject getPlacementParams() {
        JSONObject finalParams = new JSONObject();
        if (mInterstitialPresenter != null) {
            JSONObject presenterParams = mInterstitialPresenter.getPlacementParams();
            if (presenterParams != null) {
                JsonOperations.mergeJsonObjects(finalParams, presenterParams);
            }
        }
        if (mAdTrackingDelegate != null) {
            JSONObject adTrackedParams = mAdTrackingDelegate.getPlacementParams();
            if (adTrackedParams != null) {
                JsonOperations.mergeJsonObjects(finalParams, adTrackedParams);
            }
        }
        return finalParams;
    }

    @Override
    public void onInterstitialLoaded(InterstitialPresenter interstitialPresenter) {
        if (mIsDestroyed) {
            return;
        }

        mAdTrackingDelegate.trackSdkEvent(SdkEventType.LOAD, null);
        mListener.onInterstitialLoaded(interstitialPresenter);
    }

    @Override
    public void onInterstitialShown(InterstitialPresenter interstitialPresenter) {
        if (mIsDestroyed) {
            return;
        }

        if (mImpressionTracked) {
            return;
        }

        if (mReportingController != null && HyBid.isReportingEnabled()) {
            ReportingEvent reportingEvent = new ReportingEvent();
            reportingEvent.setEventType(Reporting.EventType.IMPRESSION);
            reportingEvent.setTimestamp(System.currentTimeMillis());
            reportingEvent.setAdFormat(Reporting.AdFormat.FULLSCREEN);
            reportingEvent.setPlatform(Reporting.Platform.ANDROID);
            reportingEvent.setSdkVersion(HyBid.getSDKVersionInfo(mIntegrationType));
            Ad ad = getAd();
            if (ad != null) {
                reportingEvent.setImpId(ad.getSessionId());
                reportingEvent.setCampaignId(ad.getCampaignId());
                reportingEvent.setConfigId(ad.getConfigId());
            }
            mReportingController.reportEvent(reportingEvent);
        }

        mAdTrackingDelegate.trackSdkEvent(SdkEventType.SHOW, null);
        mAdTrackingDelegate.trackImpression();
        mListener.onInterstitialShown(interstitialPresenter);
        mImpressionTracked = true;
    }

    @Override
    public void onInterstitialClicked(InterstitialPresenter interstitialPresenter) {
        if (mIsDestroyed) {
            return;
        }

        if (mClickTracked) {
            return;
        }

        if (mReportingController != null && HyBid.isReportingEnabled()) {
            ReportingEvent reportingEvent = new ReportingEvent();
            reportingEvent.setEventType(Reporting.EventType.CLICK);
            reportingEvent.setTimestamp(System.currentTimeMillis());
            reportingEvent.setAdFormat(Reporting.AdFormat.FULLSCREEN);
            reportingEvent.setPlatform(Reporting.Platform.ANDROID);
            reportingEvent.setSdkVersion(HyBid.getSDKVersionInfo(mIntegrationType));
            Ad ad = getAd();
            if (ad != null) {
                reportingEvent.setImpId(ad.getSessionId());
                reportingEvent.setCampaignId(ad.getCampaignId());
                reportingEvent.setConfigId(ad.getConfigId());
            }
            reportingEvent.setCustomString(Reporting.Key.CLICK_SOURCE_TYPE, Reporting.Key.CLICK_SOURCE_TYPE_AD);
            mReportingController.reportEvent(reportingEvent);
        }

        mAdTrackingDelegate.trackClick();
        mListener.onInterstitialClicked(interstitialPresenter);
        mClickTracked = true;
    }

    @Override
    public void onInterstitialDismissed(InterstitialPresenter interstitialPresenter) {
        if (mIsDestroyed) {
            return;
        }

        if (mDismissTracked) {
            return;
        }

        if (mReportingController != null && HyBid.isReportingEnabled()) {
            ReportingEvent reportingEvent = new ReportingEvent();
            reportingEvent.setEventType(Reporting.EventType.INTERSTITIAL_CLOSED);
            reportingEvent.setTimestamp(System.currentTimeMillis());
            reportingEvent.setAdFormat(Reporting.AdFormat.FULLSCREEN);
            reportingEvent.setPlatform(Reporting.Platform.ANDROID);
            reportingEvent.setSdkVersion(HyBid.getSDKVersionInfo(mIntegrationType));
            Ad ad = getAd();
            if (ad != null) {
                reportingEvent.setImpId(ad.getSessionId());
                reportingEvent.setCampaignId(ad.getCampaignId());
                reportingEvent.setConfigId(ad.getConfigId());
            }
            mReportingController.reportEvent(reportingEvent);
        }

        mDismissTracked = true;
        mListener.onInterstitialDismissed(interstitialPresenter);
    }

    @Override
    public void onInterstitialError(InterstitialPresenter interstitialPresenter) {
        if (mIsDestroyed) {
            return;
        }

        if (mReportingController != null && HyBid.isReportingEnabled()) {
            ReportingEvent reportingEvent = new ReportingEvent();
            reportingEvent.setEventType(Reporting.EventType.ERROR);
            reportingEvent.setTimestamp(System.currentTimeMillis());
            reportingEvent.setAdFormat(Reporting.AdFormat.FULLSCREEN);
            reportingEvent.setPlatform(Reporting.Platform.ANDROID);
            reportingEvent.setSdkVersion(HyBid.getSDKVersionInfo(mIntegrationType));
            Ad ad = getAd();
            if (ad != null) {
                reportingEvent.setImpId(ad.getSessionId());
                reportingEvent.setCampaignId(ad.getCampaignId());
                reportingEvent.setConfigId(ad.getConfigId());
            }
            if (getAd() != null && !TextUtils.isEmpty(getAd().getVast())) {
                reportingEvent.setVast(getAd().getVast());
            }
            mReportingController.reportEvent(reportingEvent);
        }

        String errorMessage = "Interstitial error for zone id: ";
        Logger.d(TAG, errorMessage);
        mAdTrackingDelegate.trackSdkEvent(SdkEventType.LOAD, HyBidErrorCode.UNKNOWN_ERROR.getCode());
        mListener.onInterstitialError(interstitialPresenter);
    }

    @Override
    public void onVideoError(int progressPercentage) {
        if (mVideoListener != null) {
            mVideoListener.onVideoError(progressPercentage);
        }
    }

    @Override
    public void onVideoStarted() {
        if (mVideoListener != null) {
            mVideoListener.onVideoStarted();
        }
    }

    @Override
    public void onVideoDismissed(int progressPercentage) {
        if (mVideoListener != null) {
            mVideoListener.onVideoDismissed(progressPercentage);
        }
    }

    @Override
    public void onVideoFinished() {
        if (mVideoListener != null) {
            mVideoListener.onVideoFinished();
        }
    }

    @Override
    public void onVideoSkipped() {
        if (mIsDestroyed || mVideoAdSkipped) {
            return;
        }
        if (mVideoListener != null) {
            mVideoAdSkipped = true;
            mVideoListener.onVideoSkipped();
        }
    }

    @Override
    public void onCustomEndCardShow() {

        if (mIsDestroyed || mCustomEndCardImpressionTracked) {
            return;
        }

        if (mReportingController != null && HyBid.isReportingEnabled()) {
            ReportingEvent reportingEvent = new ReportingEvent();
            reportingEvent.setEventType(Reporting.EventType.CUSTOM_ENDCARD_IMPRESSION);
            reportingEvent.setTimestamp(System.currentTimeMillis());
            reportingEvent.setAdFormat(Reporting.AdFormat.FULLSCREEN);
            reportingEvent.setPlatform(Reporting.Platform.ANDROID);
            reportingEvent.setSdkVersion(HyBid.getSDKVersionInfo(mIntegrationType));
            Ad ad = getAd();
            if (ad != null) {
                reportingEvent.setImpId(ad.getSessionId());
                reportingEvent.setCampaignId(ad.getCampaignId());
                reportingEvent.setConfigId(ad.getConfigId());
            }
            reportingEvent.setCustomString(Reporting.Key.END_CARD_TYPE, Reporting.Key.END_CARD_TYPE_CUSTOM);
            mReportingController.reportEvent(reportingEvent);
        }

        mAdTrackingDelegate.trackCustomEndcardEvent(AuxiliaryAdEventType.IMPRESSION, null);
        mCustomEndCardTrackingDelegate.trackImpression();
        mCustomEndCardImpressionTracked = true;
    }

    @Override
    public void onCustomEndCardClick() {

        if (mIsDestroyed || mCustomEndCardClickTracked) {
            return;
        }

        if (mReportingController != null && HyBid.isReportingEnabled()) {
            ReportingEvent reportingEvent = new ReportingEvent();
            reportingEvent.setEventType(Reporting.EventType.CUSTOM_ENDCARD_CLICK);
            reportingEvent.setTimestamp(System.currentTimeMillis());
            reportingEvent.setAdFormat(Reporting.AdFormat.FULLSCREEN);
            reportingEvent.setPlatform(Reporting.Platform.ANDROID);
            reportingEvent.setSdkVersion(HyBid.getSDKVersionInfo(mIntegrationType));
            Ad ad = getAd();
            if (ad != null) {
                reportingEvent.setImpId(ad.getSessionId());
                reportingEvent.setCampaignId(ad.getCampaignId());
                reportingEvent.setConfigId(ad.getConfigId());
            }
            reportingEvent.setCustomString(Reporting.Key.END_CARD_TYPE, Reporting.Key.END_CARD_TYPE_CUSTOM);
            mReportingController.reportEvent(reportingEvent);
        }

        mAdTrackingDelegate.trackClick();
        mAdTrackingDelegate.trackCustomEndcardEvent(AuxiliaryAdEventType.CLICK, null);
        mCustomEndCardTrackingDelegate.trackClick();
        mCustomEndCardClickTracked = true;
    }

    @Override
    public void onDefaultEndCardShow() {
        if (mIsDestroyed || mDefaultEndCardImpressionTracked) {
            return;
        }

        if (mReportingController != null && HyBid.isReportingEnabled()) {
            reportCompanionView();
            ReportingEvent reportingEvent = new ReportingEvent();
            reportingEvent.setEventType(Reporting.EventType.DEFAULT_ENDCARD_IMPRESSION);
            reportingEvent.setTimestamp(System.currentTimeMillis());
            reportingEvent.setAdFormat(Reporting.AdFormat.FULLSCREEN);
            reportingEvent.setPlatform(Reporting.Platform.ANDROID);
            reportingEvent.setSdkVersion(HyBid.getSDKVersionInfo(mIntegrationType));
            Ad ad = getAd();
            if (ad != null) {
                reportingEvent.setImpId(ad.getSessionId());
                reportingEvent.setCampaignId(ad.getCampaignId());
                reportingEvent.setConfigId(ad.getConfigId());
            }
            reportingEvent.setCustomString(Reporting.Key.END_CARD_TYPE, Reporting.Key.END_CARD_TYPE_DEFAULT);
            mReportingController.reportEvent(reportingEvent);
        }

        mAdTrackingDelegate.trackCompanionAdEvent(AuxiliaryAdEventType.IMPRESSION, null);

        mDefaultEndCardImpressionTracked = true;
    }

    @Override
    public void onDefaultEndCardClick() {
        if (mIsDestroyed || mDefaultEndCardClickTracked) {
            return;
        }

        if (mReportingController != null && HyBid.isReportingEnabled()) {
            ReportingEvent reportingEvent = new ReportingEvent();
            reportingEvent.setEventType(Reporting.EventType.DEFAULT_ENDCARD_CLICK);
            reportingEvent.setTimestamp(System.currentTimeMillis());
            reportingEvent.setAdFormat(Reporting.AdFormat.FULLSCREEN);
            reportingEvent.setPlatform(Reporting.Platform.ANDROID);
            reportingEvent.setSdkVersion(HyBid.getSDKVersionInfo(mIntegrationType));
            Ad ad = getAd();
            if (ad != null) {
                reportingEvent.setImpId(ad.getSessionId());
                reportingEvent.setCampaignId(ad.getCampaignId());
                reportingEvent.setConfigId(ad.getConfigId());
            }
            reportingEvent.setCustomString(Reporting.Key.END_CARD_TYPE, Reporting.Key.END_CARD_TYPE_DEFAULT);
            mReportingController.reportEvent(reportingEvent);
        }

        mAdTrackingDelegate.trackCompanionAdEvent(AuxiliaryAdEventType.CLICK, null);
        mDefaultEndCardClickTracked = true;
    }

    @Override
    public void onEndCardLoadSuccess(boolean isCustomEndCard) {
        if (mIsDestroyed) {
            return;
        }
        String event_type = "";
        if (isCustomEndCard)
            event_type = Reporting.EventType.CUSTOM_END_CARD_LOAD_SUCCESS;
        else
            event_type = Reporting.EventType.DEFAULT_END_CARD_LOAD_SUCCESS;

        if (mReportingController != null && HyBid.isReportingEnabled()) {
            ReportingEvent reportingEvent = new ReportingEvent();
            reportingEvent.setEventType(event_type);
            reportingEvent.setTimestamp(System.currentTimeMillis());
            reportingEvent.setAdFormat(Reporting.AdFormat.FULLSCREEN);
            reportingEvent.setPlatform(Reporting.Platform.ANDROID);
            reportingEvent.setSdkVersion(HyBid.getSDKVersionInfo(mIntegrationType));
            Ad ad = getAd();
            if (ad != null) {
                reportingEvent.setImpId(ad.getSessionId());
                reportingEvent.setCampaignId(ad.getCampaignId());
                reportingEvent.setConfigId(ad.getConfigId());
            }
            mReportingController.reportEvent(reportingEvent);
        }
    }

    @Override
    public void onEndCardLoadFailure(boolean isCustomEndCard) {
        if (mIsDestroyed) {
            return;
        }
        String event_type = "";
        if (isCustomEndCard) {
            event_type = Reporting.EventType.CUSTOM_END_CARD_LOAD_FAILURE;
            mAdTrackingDelegate.trackCustomEndcardEvent(AuxiliaryAdEventType.ERROR, HyBidErrorCode.UNKNOWN_ERROR.getCode());
        } else {
            event_type = Reporting.EventType.DEFAULT_END_CARD_LOAD_FAILURE;
            mAdTrackingDelegate.trackCompanionAdEvent(AuxiliaryAdEventType.ERROR, HyBidErrorCode.UNKNOWN_ERROR.getCode());
        }

        if (mReportingController != null && HyBid.isReportingEnabled()) {
            ReportingEvent reportingEvent = new ReportingEvent();
            reportingEvent.setEventType(event_type);
            reportingEvent.setTimestamp(System.currentTimeMillis());
            reportingEvent.setAdFormat(Reporting.AdFormat.FULLSCREEN);
            reportingEvent.setPlatform(Reporting.Platform.ANDROID);
            reportingEvent.setSdkVersion(HyBid.getSDKVersionInfo(mIntegrationType));
            Ad ad = getAd();
            if (ad != null) {
                reportingEvent.setImpId(ad.getSessionId());
                reportingEvent.setCampaignId(ad.getCampaignId());
                reportingEvent.setConfigId(ad.getConfigId());
            }
            mReportingController.reportEvent(reportingEvent);
        }
    }

    private void reportCompanionView() {
        if (HyBid.getReportingController() != null && HyBid.isReportingEnabled()) {
            ReportingEvent reportingEvent = new ReportingEvent();
            reportingEvent.setEventType(Reporting.EventType.COMPANION_VIEW);
            reportingEvent.setAdFormat(Reporting.AdFormat.BANNER);
            reportingEvent.setCreativeType(Reporting.CreativeType.VIDEO);
            reportingEvent.setPlatform(Reporting.Platform.ANDROID);
            reportingEvent.setSdkVersion(HyBid.getSDKVersionInfo(IntegrationType.STANDALONE));
            Ad ad = getAd();
            if (ad != null) {
                reportingEvent.setImpId(ad.getSessionId());
                reportingEvent.setCampaignId(ad.getCampaignId());
                reportingEvent.setConfigId(ad.getConfigId());
            }
            reportingEvent.setTimestamp(System.currentTimeMillis());

            HyBid.getReportingController().reportEvent(reportingEvent);
        }
    }
}
