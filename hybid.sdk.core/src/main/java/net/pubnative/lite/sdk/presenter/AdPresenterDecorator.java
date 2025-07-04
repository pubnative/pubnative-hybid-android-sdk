// HyBid SDK License
//
// https://github.com/pubnative/pubnative-hybid-android-sdk/blob/main/LICENSE
//
package net.pubnative.lite.sdk.presenter;

import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import net.pubnative.lite.sdk.HyBid;
import net.pubnative.lite.sdk.VideoListener;
import net.pubnative.lite.sdk.analytics.Reporting;
import net.pubnative.lite.sdk.analytics.ReportingController;
import net.pubnative.lite.sdk.analytics.ReportingEvent;
import net.pubnative.lite.sdk.models.Ad;
import net.pubnative.lite.sdk.models.IntegrationType;
import net.pubnative.lite.sdk.mraid.MRAIDView;
import net.pubnative.lite.sdk.mraid.MRAIDViewListener;
import net.pubnative.lite.sdk.utils.AdTracker;
import net.pubnative.lite.sdk.utils.CheckUtils;
import net.pubnative.lite.sdk.utils.HybidConsumer;
import net.pubnative.lite.sdk.utils.Logger;
import net.pubnative.lite.sdk.utils.json.JsonOperations;

import org.json.JSONObject;

/**
 * Created by erosgarciaponte on 08.01.18.
 */

public class AdPresenterDecorator implements AdPresenter, AdPresenter.Listener, AdPresenter.ImpressionListener, VideoListener, MRAIDViewListener {
    private static final String TAG = AdPresenterDecorator.class.getSimpleName();
    private static final String ERROR_DECORATOR_DESTROYED = "AdPresenterDecorator is destroyed";

    private final AdPresenter mAdPresenter;
    private final AdTracker mAdTrackingDelegate;
    private final ReportingController mReportingController;
    private final AdPresenter.Listener mListener;
    private final ImpressionListener mImpressionListener;
    private VideoListener mVideoListener;

    private MRAIDViewListener mMraidListener;
    private boolean mIsDestroyed = false;
    private boolean mImpressionTracked = false;
    private boolean mClickTracked = false;

    private IntegrationType mIntegrationType;

    public AdPresenterDecorator(AdPresenter adPresenter, AdTracker adTrackingDelegate, ReportingController reportingController, AdPresenter.Listener listener, AdPresenter.ImpressionListener impressionListener, IntegrationType integrationType) {
        mAdPresenter = adPresenter;
        mAdTrackingDelegate = adTrackingDelegate;
        mReportingController = reportingController;
        mListener = listener;
        mImpressionListener = impressionListener;
        mIntegrationType = integrationType;
    }

    @Override
    public void setListener(Listener listener) {
        // We set the listener in the constructor instead
    }

    @Override
    public void setImpressionListener(ImpressionListener listener) {
        // We set the listener in the constructor instead
    }

    @Override
    public void setVideoListener(VideoListener listener) {
        mVideoListener = listener;
    }

    @Override
    public void setMRaidListener(MRAIDViewListener listener) {
        mMraidListener = listener;
    }

    @Override
    public Ad getAd() {
        return mAdPresenter.getAd();
    }

    @Override
    public void load() {
        if (!CheckUtils.NoThrow.checkArgument(!mIsDestroyed, ERROR_DECORATOR_DESTROYED)) {
            return;
        }

        mAdPresenter.load();
    }

    @Override
    public void destroy() {
        mAdPresenter.destroy();
        mIsDestroyed = true;
    }

    @Override
    public void startTracking() {
        startTracking(null);
    }

    @Override
    public void startTracking(HybidConsumer<Double> percentageConsumer) {
        if (!CheckUtils.NoThrow.checkArgument(!mIsDestroyed, ERROR_DECORATOR_DESTROYED)) {
            return;
        }
        mAdPresenter.startTracking(percentageConsumer);
    }

    @Override
    public void stopTracking() {
        if (!CheckUtils.NoThrow.checkArgument(!mIsDestroyed, ERROR_DECORATOR_DESTROYED)) {
            return;
        }

        mAdPresenter.stopTracking();
    }

    @Override
    public JSONObject getPlacementParams() {
        JSONObject finalParams = new JSONObject();
        if (mAdPresenter != null) {
            JSONObject presenterParams = mAdPresenter.getPlacementParams();
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
    public void onAdLoaded(AdPresenter adPresenter, View banner) {
        if (mIsDestroyed) {
            return;
        }

        mListener.onAdLoaded(adPresenter, banner);
    }

    @Override
    public void onAdClicked(AdPresenter adPresenter) {
        if (mIsDestroyed) {
            return;
        }

        if (mClickTracked) {
            return;
        }

        if (mReportingController != null && HyBid.isReportingEnabled()) {
            ReportingEvent reportingEvent = new ReportingEvent();
            reportingEvent.setEventType(Reporting.EventType.CLICK);
            reportingEvent.setTimestamp(String.valueOf(System.currentTimeMillis()));
            reportingEvent.setAdFormat(Reporting.AdFormat.BANNER);
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
        mListener.onAdClicked(adPresenter);
        mClickTracked = true;
    }

    @Override
    public void onAdError(AdPresenter adPresenter) {
        if (mIsDestroyed) {
            return;
        }

        if (mReportingController != null) {
            ReportingEvent reportingEvent = new ReportingEvent();
            reportingEvent.setEventType(Reporting.EventType.ERROR);
            reportingEvent.setTimestamp(String.valueOf(System.currentTimeMillis()));
            reportingEvent.setAdFormat(Reporting.AdFormat.BANNER);
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

        String errorMessage = "Banner error for zone id: ";
        Logger.d(TAG, errorMessage);
        mListener.onAdError(adPresenter);
    }

    @Override
    public void onImpression() {
        if (mIsDestroyed) {
            return;
        }

        if (mImpressionTracked) {
            Log.i(TAG, "impression is already confirmed, dropping impression tracking");
            return;
        }

        if (mReportingController != null && HyBid.isReportingEnabled()) {
            ReportingEvent reportingEvent = new ReportingEvent();
            reportingEvent.setEventType(Reporting.EventType.IMPRESSION);
            reportingEvent.setTimestamp(String.valueOf(System.currentTimeMillis()));
            reportingEvent.setAdFormat(Reporting.AdFormat.BANNER);
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

        mImpressionTracked = true;
        mAdTrackingDelegate.trackImpression();
        if (mImpressionListener != null) {
            mImpressionListener.onImpression();
        }
    }

    @Override
    public void onVideoError(int progressPercentage) {
        if (mVideoListener != null) {
            mVideoListener.onVideoDismissed(progressPercentage);
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
        if (mVideoListener != null) {
            mVideoListener.onVideoSkipped();
        }
    }

    @Override
    public void mraidViewLoaded(MRAIDView mraidView) {

    }

    @Override
    public void mraidViewError(MRAIDView mraidView) {

    }

    @Override
    public void mraidViewExpand(MRAIDView mraidView) {

    }

    @Override
    public void mraidViewClose(MRAIDView mraidView) {

    }

    @Override
    public boolean mraidViewResize(MRAIDView mraidView, int width, int height, int offsetX, int offsetY) {
        return false;
    }

    @Override
    public void mraidShowCloseButton() {

    }

    @Override
    public void onExpandedAdClosed() {
        if (mMraidListener != null)
            mMraidListener.onExpandedAdClosed();
    }
}
