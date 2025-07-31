// HyBid SDK License
//
// https://github.com/pubnative/pubnative-hybid-android-sdk/blob/main/LICENSE
//
package net.pubnative.lite.sdk.rewarded.presenter;

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

public class RewardedPresenterDecorator implements RewardedPresenter, RewardedPresenter.Listener, VideoListener, CustomEndCardListener {
    private static final String TAG = RewardedPresenterDecorator.class.getSimpleName();
    private final RewardedPresenter mRewardedPresenter;
    private final AdTracker mAdTrackingDelegate;
    private final AdTracker mCustomEndCardTrackingDelegate;
    private final ReportingController mReportingController;
    private final Listener mListener;
    private VideoListener mVideoListener;
    private boolean mIsDestroyed = false;
    private boolean mImpressionTracked = false;
    private boolean mClickTracked = false;
    private boolean mFinishTracked = false;

    private boolean mDefaultEndCardImpressionTracked = false;
    private boolean mDefaultEndCardClickTracked = false;

    private boolean mCustomEndCardImpressionTracked = false;
    private boolean mCustomEndCardClickTracked = false;
    private final IntegrationType mIntegrationType;
    private boolean mVideoAdSkipped = false;

    public RewardedPresenterDecorator(RewardedPresenter rewardedPresenter, AdTracker adTrackingDelegate, AdTracker customEndCardTrackingDelegate, ReportingController reportingController, Listener listener, IntegrationType integrationType) {
        mRewardedPresenter = rewardedPresenter;
        mRewardedPresenter.setVideoListener(this);
        mAdTrackingDelegate = adTrackingDelegate;
        mCustomEndCardTrackingDelegate = customEndCardTrackingDelegate;
        mReportingController = reportingController;
        mListener = listener;
        mIntegrationType = integrationType;
    }

    @Override
    public void setListener(Listener listener) {
        // We set the listener in the constructor instead
    }

    @Override
    public Ad getAd() {
        return mRewardedPresenter.getAd();
    }

    @Override
    public void load() {
        if (!CheckUtils.NoThrow.checkArgument(!mIsDestroyed, "RewardedPresenterDecorator is destroyed")) {
            return;
        }

        mRewardedPresenter.load();
    }

    @Override
    public boolean isReady() {
        return mRewardedPresenter.isReady();
    }

    @Override
    public void show() {
        if (!CheckUtils.NoThrow.checkArgument(!mIsDestroyed, "RewardedPresenterDecorator is destroyed")) {
            return;
        }

        mRewardedPresenter.show();
    }

    @Override
    public void destroy() {
        mRewardedPresenter.destroy();
        mIsDestroyed = true;
    }

    @Override
    public JSONObject getPlacementParams() {
        JSONObject finalParams = new JSONObject();
        if (mRewardedPresenter != null) {
            JSONObject presenterParams = mRewardedPresenter.getPlacementParams();
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
    public void onRewardedLoaded(RewardedPresenter rewardedPresenter) {
        if (mIsDestroyed) {
            return;
        }

        mAdTrackingDelegate.trackSdkEvent(SdkEventType.LOAD, null);

        mListener.onRewardedLoaded(rewardedPresenter);
    }

    @Override
    public void onRewardedOpened(RewardedPresenter rewardedPresenter) {
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
            reportingEvent.setAdFormat(Reporting.AdFormat.REWARDED);
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

        mAdTrackingDelegate.trackImpression();
        mAdTrackingDelegate.trackSdkEvent(SdkEventType.SHOW, null);
        mListener.onRewardedOpened(rewardedPresenter);
        mImpressionTracked = true;
    }

    @Override
    public void onRewardedClicked(RewardedPresenter rewardedPresenter) {
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
            reportingEvent.setAdFormat(Reporting.AdFormat.REWARDED);
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
        mListener.onRewardedClicked(rewardedPresenter);
        mClickTracked = true;
    }

    @Override
    public void onRewardedClosed(RewardedPresenter rewardedPresenter) {
        if (mIsDestroyed) {
            return;
        }

        mListener.onRewardedClosed(rewardedPresenter);
    }

    @Override
    public void onRewardedFinished(RewardedPresenter rewardedPresenter) {
        if (mIsDestroyed) {
            return;
        }

        if (mFinishTracked) {
            return;
        }

        if (mReportingController != null && HyBid.isReportingEnabled()) {
            ReportingEvent reportingEvent = new ReportingEvent();
            reportingEvent.setEventType(Reporting.EventType.REWARDED_CLOSED);
            reportingEvent.setTimestamp(System.currentTimeMillis());
            reportingEvent.setAdFormat(Reporting.AdFormat.REWARDED);
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
        mListener.onRewardedFinished(rewardedPresenter);
        mFinishTracked = true;
    }

    @Override
    public void setVideoListener(VideoListener listener) {
        this.mVideoListener = listener;
    }

    @Override
    public void setCustomEndCardListener(CustomEndCardListener listener) {

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
    public void onRewardedError(RewardedPresenter rewardedPresenter) {
        if (mIsDestroyed) {
            return;
        }

        if (mReportingController != null && HyBid.isReportingEnabled()) {
            ReportingEvent reportingEvent = new ReportingEvent();
            reportingEvent.setEventType(Reporting.EventType.ERROR);
            reportingEvent.setTimestamp(System.currentTimeMillis());
            reportingEvent.setAdFormat(Reporting.AdFormat.REWARDED);
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

        String zoneId = getAd().getZoneId();
        String errorMessage;
        if (TextUtils.isEmpty(zoneId)) {
            errorMessage = "Rewarded error";
        } else {
            errorMessage = "Rewarded error for zone id: " + zoneId;
        }

        Logger.d(TAG, errorMessage);
        mAdTrackingDelegate.trackSdkEvent(SdkEventType.LOAD, HyBidErrorCode.UNKNOWN_ERROR.getCode());
        mListener.onRewardedError(rewardedPresenter);
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
            reportingEvent.setAdFormat(Reporting.AdFormat.REWARDED);
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
            reportingEvent.setAdFormat(Reporting.AdFormat.REWARDED);
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
            reportingEvent.setAdFormat(Reporting.AdFormat.REWARDED);
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
            reportingEvent.setAdFormat(Reporting.AdFormat.REWARDED);
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
            reportingEvent.setAdFormat(Reporting.AdFormat.REWARDED);
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
            reportingEvent.setAdFormat(Reporting.AdFormat.REWARDED);
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
