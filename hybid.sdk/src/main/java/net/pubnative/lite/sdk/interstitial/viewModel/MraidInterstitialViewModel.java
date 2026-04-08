// HyBid SDK License
//
// https://github.com/pubnative/pubnative-hybid-android-sdk/blob/main/LICENSE
//
package net.pubnative.lite.sdk.interstitial.viewModel;

import android.content.Context;
import android.view.View;

import net.pubnative.lite.sdk.HyBid;
import net.pubnative.lite.sdk.analytics.Reporting;
import net.pubnative.lite.sdk.analytics.ReportingEvent;
import net.pubnative.lite.sdk.interstitial.HyBidInterstitialBroadcastReceiver;
import net.pubnative.lite.sdk.interstitial.InterstitialActivityInteractor;
import net.pubnative.lite.sdk.models.APIAsset;
import net.pubnative.lite.sdk.models.AuxiliaryAdEventType;
import net.pubnative.lite.sdk.mraid.MRAIDBanner;
import net.pubnative.lite.sdk.mraid.MRAIDNativeFeature;
import net.pubnative.lite.sdk.mraid.MRAIDNativeFeatureListener;
import net.pubnative.lite.sdk.mraid.MRAIDView;
import net.pubnative.lite.sdk.mraid.MRAIDViewCloseLayoutListener;
import net.pubnative.lite.sdk.mraid.MRAIDViewListener;
import net.pubnative.lite.sdk.mraid.model.HTMLAd;
import net.pubnative.lite.sdk.utils.ClickThroughTimerManager;
import net.pubnative.lite.sdk.viewability.FriendlyObstructionReasonConstants;
import net.pubnative.lite.sdk.viewability.baseom.BaseFriendlyObstructionPurpose;

public class MraidInterstitialViewModel extends InterstitialViewModel implements MRAIDViewListener, MRAIDNativeFeatureListener, MRAIDViewCloseLayoutListener, ClickThroughTimerManager.ClickThroughTimerListener {

    private final String[] mSupportedNativeFeatures = new String[]{MRAIDNativeFeature.CALENDAR, MRAIDNativeFeature.INLINE_VIDEO, MRAIDNativeFeature.SMS, MRAIDNativeFeature.STORE_PICTURE, MRAIDNativeFeature.TEL, MRAIDNativeFeature.LOCATION};

    private MRAIDBanner mView;

    private boolean mLoadCustomEndCardTracked = false;
    private boolean mCustomEndCardImpressionTracked = false;
    private boolean mCustomEndCardCloseTracked = false;
    private boolean mCustomEndCardClickTracked = false;
    private boolean mCustomCTAImpressionTracked = false;
    private boolean mCustomCTAClickTracked = false;

    public MraidInterstitialViewModel(Context context, String zoneId, String integrationType, int skipOffset, long broadcastId, InterstitialActivityInteractor listener) {
        super(context, zoneId, integrationType, skipOffset, broadcastId, listener);
        processInterstitialAd();
        listener.setContentLayout();
    }

    @Override
    public void addFriendlyObstruction(View view) {
        if (mView != null) {
            mView.addViewabilityFriendlyObstruction(view, BaseFriendlyObstructionPurpose.OTHER, FriendlyObstructionReasonConstants.WATERMARK_OBSTRUCTION_REASON);
        }
    }

    @Override
    public boolean shouldShowContentInfo() {
        return false;
    }

    @Override
    public void closeButtonClicked() {
        sendBroadcast(HyBidInterstitialBroadcastReceiver.Action.DISMISS);
        mListener.finishActivity();
    }

    @Override
    public void skipButtonClicked() {
        sendBroadcast(HyBidInterstitialBroadcastReceiver.Action.PLAYABLE_SKIP_CLICK);
        mView.skipButtonClicked();
    }

    @Override
    public View getAdView() {
        MRAIDBanner adView = null;
        if (mAd != null) {
            if (mAd.getAssetUrl(APIAsset.HTML_BANNER) != null) {
                adView = new MRAIDBanner(mContext, mAd.getAssetUrl(APIAsset.HTML_BANNER), "", true, false, mSupportedNativeFeatures, this, this, getContentInfoContainer());
            } else if (mAd.getAssetHtml(APIAsset.HTML_BANNER) != null) {
                adView = new MRAIDBanner(mContext, "", mAd.getAssetHtml(APIAsset.HTML_BANNER), true, false, mSupportedNativeFeatures, this, this, getContentInfoContainer());
            }
            if (adView != null) {
                htmlAd = new HTMLAd(mContext, mAd, HTMLAd.AdType.INTERSTITIAL);
                htmlAd.setLink(mAd.getLink());
                htmlAd.setClickThroughTimerListener(this);
                adView.setCloseLayoutListener(this);
                Integer skipDelay = htmlAd.getSkipDelay();
                mIsSkippable = skipDelay != null && skipDelay == 0;
                adView.setHtmlAd(htmlAd);
            }
        }
        mView = adView;
        return adView;
    }

    // ----------------------------------- MRAIDViewListener ---------------------------------------

    @Override
    public void mraidViewLoaded(MRAIDView mraidView) {
        sendBroadcast(HyBidInterstitialBroadcastReceiver.Action.SHOW);
    }

    @Override
    public void mraidViewError(MRAIDView mraidView) {
        sendBroadcast(HyBidInterstitialBroadcastReceiver.Action.ERROR);
        dismiss();
    }

    @Override
    public void mraidViewExpand(MRAIDView mraidView) {

    }

    @Override
    public void mraidViewClose(MRAIDView mraidView) {

    }

    @Override
    public boolean mraidViewResize(MRAIDView mraidView, int width, int height, int offsetX, int offsetY) {
        return true;
    }

    @Override
    public void mraidShowCloseButton() {
        mIsSkippable = true;
        mListener.showInterstitialCloseButton(mCloseListener);
    }

    @Override
    public void mraidShowSkipButton() {
        mIsSkippable = false;
        mListener.showInterstitialSkipButton(mSkipListener);
    }

    @Override
    public void mraidHideSkipButton() {
        mListener.hideInterstitialSkipButton();
    }

    @Override
    public void onExpandedAdClosed() {

    }

    @Override
    public void onReplayClicked() {

    }

    @Override
    public void onCustomEndCardLoadSuccess() {
        if (mLoadCustomEndCardTracked)
            return;
        mLoadCustomEndCardTracked = true;
        if (HyBid.getReportingController() != null && HyBid.isReportingEnabled()) {
            ReportingEvent reportingEvent = new ReportingEvent();
            reportingEvent.setTimestamp(System.currentTimeMillis());
            reportingEvent.setAdFormat(Reporting.AdFormat.BANNER);
            reportingEvent.setPlatform(Reporting.Platform.ANDROID);
            reportingEvent.setSdkVersion(HyBid.getSDKVersionInfo(mIntegrationType));
            if (mAd != null) {
                reportingEvent.setImpId(mAd.getSessionId());
                reportingEvent.setCampaignId(mAd.getCampaignId());
                reportingEvent.setConfigId(mAd.getConfigId());
            }
            reportingEvent.setEventType(Reporting.EventType.CUSTOM_END_CARD_LOAD_SUCCESS);
            reportingEvent.setCustomString(Reporting.Key.END_CARD_TYPE, Reporting.Key.END_CARD_TYPE_CUSTOM);
            HyBid.getReportingController().reportEvent(reportingEvent);
        }
    }

    @Override
    public void onCustomEndCardShow(String endCardType) {
        if (!mCustomEndCardImpressionTracked) {
            if (HyBid.getReportingController() != null && HyBid.isReportingEnabled()) {
                ReportingEvent reportingEvent = new ReportingEvent();
                reportingEvent.setTimestamp(System.currentTimeMillis());
                reportingEvent.setAdFormat(Reporting.AdFormat.BANNER);
                reportingEvent.setPlatform(Reporting.Platform.ANDROID);
                reportingEvent.setSdkVersion(HyBid.getSDKVersionInfo(mIntegrationType));
                if (mAd != null) {
                    reportingEvent.setImpId(mAd.getSessionId());
                    reportingEvent.setCampaignId(mAd.getCampaignId());
                    reportingEvent.setConfigId(mAd.getConfigId());
                }
                reportingEvent.setEventType(Reporting.EventType.CUSTOM_ENDCARD_IMPRESSION);
                reportingEvent.setCustomString(Reporting.Key.END_CARD_TYPE, endCardType);

                HyBid.getReportingController().reportEvent(reportingEvent);
            }
            mCustomEndcardTracker.trackImpression();
            mAdEventTracker.trackCustomEndcardEvent(AuxiliaryAdEventType.IMPRESSION, null);
            mCustomEndCardImpressionTracked = true;
        }
    }

    @Override
    public void onCustomEndCardLoadFail() {
        if (HyBid.getReportingController() != null && HyBid.isReportingEnabled()) {
            ReportingEvent reportingEvent = new ReportingEvent();
            reportingEvent.setTimestamp(System.currentTimeMillis());
            reportingEvent.setAdFormat(Reporting.AdFormat.BANNER);
            reportingEvent.setPlatform(Reporting.Platform.ANDROID);
            reportingEvent.setSdkVersion(HyBid.getSDKVersionInfo(mIntegrationType));
            if (mAd != null) {
                reportingEvent.setImpId(mAd.getSessionId());
                reportingEvent.setCampaignId(mAd.getCampaignId());
                reportingEvent.setConfigId(mAd.getConfigId());
            }
            reportingEvent.setEventType(Reporting.EventType.CUSTOM_END_CARD_LOAD_FAILURE);
            reportingEvent.setCustomString(Reporting.Key.END_CARD_TYPE, Reporting.Key.END_CARD_TYPE_CUSTOM);
            HyBid.getReportingController().reportEvent(reportingEvent);
        }
    }

    @Override
    public void onCustomEndCardClosed() {
        if (mCustomEndCardCloseTracked)
            return;
        mCustomEndCardCloseTracked = true;
        mAdEventTracker.trackCustomEndcardEvent(AuxiliaryAdEventType.CLOSE, null);
        if (HyBid.getReportingController() != null && HyBid.isReportingEnabled()) {
            ReportingEvent reportingEvent = new ReportingEvent();
            reportingEvent.setTimestamp(System.currentTimeMillis());
            reportingEvent.setEventType(Reporting.EventType.CUSTOM_ENDCARD_CLOSE);
            reportingEvent.setCustomString(Reporting.Key.END_CARD_TYPE, Reporting.Key.END_CARD_TYPE_CUSTOM);
            HyBid.getReportingController().reportEvent(reportingEvent);
        }
    }

    @Override
    public void onCustomEndCardClicked() {
        if (!mCustomEndCardClickTracked) {
            if (HyBid.getReportingController() != null && HyBid.isReportingEnabled()) {
                ReportingEvent reportingEvent = new ReportingEvent();
                reportingEvent.setEventType(Reporting.EventType.CUSTOM_ENDCARD_CLICK);
                reportingEvent.setTimestamp(System.currentTimeMillis());
                reportingEvent.setAdFormat(Reporting.AdFormat.BANNER);
                reportingEvent.setPlatform(Reporting.Platform.ANDROID);
                reportingEvent.setSdkVersion(HyBid.getSDKVersionInfo(mIntegrationType));
                if (mAd != null) {
                    reportingEvent.setImpId(mAd.getSessionId());
                    reportingEvent.setCampaignId(mAd.getCampaignId());
                    reportingEvent.setConfigId(mAd.getConfigId());
                }
                reportingEvent.setCustomString(Reporting.Key.END_CARD_TYPE, Reporting.Key.END_CARD_TYPE_CUSTOM);

                HyBid.getReportingController().reportEvent(reportingEvent);
            }
            mAdTracker.trackClick();
            mCustomEndcardTracker.trackClick();
            mAdEventTracker.trackCustomEndcardEvent(AuxiliaryAdEventType.CLICK, null);
            mCustomEndCardClickTracked = true;
        }
    }

    @Override
    public void onCustomCTAShow() {
        if (HyBid.getReportingController() != null && HyBid.isReportingEnabled()) {
            ReportingEvent reportingEvent = new ReportingEvent();
            reportingEvent.setTimestamp(System.currentTimeMillis());
            reportingEvent.setAdFormat(Reporting.AdFormat.BANNER);
            reportingEvent.setPlatform(Reporting.Platform.ANDROID);
            reportingEvent.setSdkVersion(HyBid.getSDKVersionInfo(mIntegrationType));
            if (mAd != null) {
                reportingEvent.setImpId(mAd.getSessionId());
                reportingEvent.setCampaignId(mAd.getCampaignId());
                reportingEvent.setConfigId(mAd.getConfigId());
            }
            reportingEvent.setEventType(Reporting.EventType.CUSTOM_CTA_SHOW);

            HyBid.getReportingController().reportEvent(reportingEvent);
        }
        if (!mCustomCTAImpressionTracked) {
            mCustomCTATracker.trackImpression();
            mCustomCTAImpressionTracked = true;
        }
    }

    @Override
    public void onCustomCTAClick() {
        if (HyBid.getReportingController() != null && HyBid.isReportingEnabled()) {
            ReportingEvent reportingEvent = new ReportingEvent();
            reportingEvent.setEventType(Reporting.EventType.CUSTOM_CTA_CLICK);
            reportingEvent.setTimestamp(System.currentTimeMillis());
            reportingEvent.setAdFormat(Reporting.AdFormat.BANNER);
            reportingEvent.setPlatform(Reporting.Platform.ANDROID);
            reportingEvent.setSdkVersion(HyBid.getSDKVersionInfo(mIntegrationType));
            if (mAd != null) {
                reportingEvent.setImpId(mAd.getSessionId());
                reportingEvent.setCampaignId(mAd.getCampaignId());
                reportingEvent.setConfigId(mAd.getConfigId());
            }

            HyBid.getReportingController().reportEvent(reportingEvent);
        }
        if (!mCustomCTAClickTracked) {
            mCustomCTATracker.trackClick();
            mCustomCTAClickTracked = true;
        }
    }

    @Override
    public void onCustomCTALoadFail() {

    }

    @Override
    public void mraidHideCloseButton() {
        if (mListener != null)
            mListener.hideInterstitialCloseButton();
    }

    // -------------------------------- ClickThroughTimerListener ----------------------------------

    @Override
    public void onClickThroughTriggered() {
        mAdTracker.trackClick();
    }

    // ------------------------------- MRAIDNativeFeatureListener ----------------------------------

    @Override
    public void mraidNativeFeatureCallTel(String url) {
        sendBroadcast(HyBidInterstitialBroadcastReceiver.Action.CLICK);
    }

    @Override
    public void mraidNativeFeatureCreateCalendarEvent(String eventJSON) {

    }

    @Override
    public void mraidNativeFeaturePlayVideo(String url) {

    }

    @Override
    public void mraidNativeFeatureOpenBrowser(String url) {
        sendBroadcast(HyBidInterstitialBroadcastReceiver.Action.CLICK);
        handleURL(url);
    }

    @Override
    public void mraidNativeFeatureStorePicture(String url) {

    }

    @Override
    public void mraidNativeFeatureSendSms(String url) {
        sendBroadcast(HyBidInterstitialBroadcastReceiver.Action.CLICK);
    }

    // ------------------------------ MRAIDViewCloseLayoutListener ---------------------------------

    @Override
    public void onShowCloseLayout() {
        mIsSkippable = true;
        mListener.showInterstitialCloseButton(mCloseListener);
    }

    @Override
    public void onRemoveCloseLayout() {
        mListener.hideInterstitialCloseButton();
    }

    @Override
    public void onClose() {
        dismiss();
    }

    @Override
    public void pauseAd() {
        if (mView != null) {
            mView.pause();
        }
    }

    @Override
    public void resumeAd() {
        if (!isFeedbackFormOpen() && mView != null) {
            mView.resume();
        }
    }

    @Override
    public void destroyAd() {
        if (mView != null) {
            mView.stopAdSession();
            mView.destroy();
        }
    }

    @Override
    public void resetVolumeChangeTracker() {
        //No volume tracker for mraid ads
    }

    @Override
    public Boolean hasReducedCloseSize() {
        return htmlAd.hasReducedCloseSize();
    }
}