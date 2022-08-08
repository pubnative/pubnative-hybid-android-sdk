// The MIT License (MIT)
//
// Copyright (c) 2018 PubNative GmbH
//
// Permission is hereby granted, free of charge, to any person obtaining a copy
// of this software and associated documentation files (the "Software"), to deal
// in the Software without restriction, including without limitation the rights
// to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
// copies of the Software, and to permit persons to whom the Software is
// furnished to do so, subject to the following conditions:
//
// The above copyright notice and this permission notice shall be included in all
// copies or substantial portions of the Software.
//
// THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
// IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
// FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
// AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
// LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
// OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
// SOFTWARE.
//
package net.pubnative.lite.sdk.interstitial.presenter;

import android.text.TextUtils;

import net.pubnative.lite.sdk.VideoListener;
import net.pubnative.lite.sdk.analytics.Reporting;
import net.pubnative.lite.sdk.analytics.ReportingController;
import net.pubnative.lite.sdk.analytics.ReportingEvent;
import net.pubnative.lite.sdk.models.Ad;
import net.pubnative.lite.sdk.utils.AdTracker;
import net.pubnative.lite.sdk.utils.CheckUtils;
import net.pubnative.lite.sdk.utils.Logger;
import net.pubnative.lite.sdk.utils.json.JsonOperations;

import org.json.JSONObject;

/**
 * Created by erosgarciaponte on 09.01.18.
 */

public class InterstitialPresenterDecorator implements InterstitialPresenter, InterstitialPresenter.Listener, VideoListener {
    private static final String TAG = InterstitialPresenterDecorator.class.getSimpleName();
    private final InterstitialPresenter mInterstitialPresenter;
    private final AdTracker mAdTrackingDelegate;
    private final ReportingController mReportingController;
    private final InterstitialPresenter.Listener mListener;
    private VideoListener mVideoListener;
    private boolean mIsDestroyed;

    public InterstitialPresenterDecorator(InterstitialPresenter interstitialPresenter,
                                          AdTracker adTrackingDelegate,
                                          ReportingController reportingController,
                                          InterstitialPresenter.Listener listener) {
        mInterstitialPresenter = interstitialPresenter;
        mAdTrackingDelegate = adTrackingDelegate;
        mReportingController = reportingController;
        mListener = listener;
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

        mListener.onInterstitialLoaded(interstitialPresenter);
    }

    @Override
    public void onInterstitialShown(InterstitialPresenter interstitialPresenter) {
        if (mIsDestroyed) {
            return;
        }

        if (mReportingController != null) {
            ReportingEvent reportingEvent = new ReportingEvent();
            reportingEvent.setEventType(Reporting.EventType.IMPRESSION);
            reportingEvent.setTimestamp(System.currentTimeMillis());
            reportingEvent.setAdFormat(Reporting.AdFormat.FULLSCREEN);
            mReportingController.reportEvent(reportingEvent);
        }

        mAdTrackingDelegate.trackImpression();
        mListener.onInterstitialShown(interstitialPresenter);
    }

    @Override
    public void onInterstitialClicked(InterstitialPresenter interstitialPresenter) {
        if (mIsDestroyed) {
            return;
        }

        if (mReportingController != null) {
            ReportingEvent reportingEvent = new ReportingEvent();
            reportingEvent.setEventType(Reporting.EventType.CLICK);
            reportingEvent.setTimestamp(System.currentTimeMillis());
            reportingEvent.setAdFormat(Reporting.AdFormat.FULLSCREEN);
            mReportingController.reportEvent(reportingEvent);
        }

        mAdTrackingDelegate.trackClick();
        mListener.onInterstitialClicked(interstitialPresenter);
    }

    @Override
    public void onInterstitialDismissed(InterstitialPresenter interstitialPresenter) {
        if (mIsDestroyed) {
            return;
        }

        if (mReportingController != null) {
            ReportingEvent reportingEvent = new ReportingEvent();
            reportingEvent.setEventType(Reporting.EventType.INTERSTITIAL_CLOSED);
            reportingEvent.setTimestamp(System.currentTimeMillis());
            reportingEvent.setAdFormat(Reporting.AdFormat.FULLSCREEN);
            mReportingController.reportEvent(reportingEvent);
        }

        mListener.onInterstitialDismissed(interstitialPresenter);
    }

    @Override
    public void onInterstitialError(InterstitialPresenter interstitialPresenter) {
        if (mIsDestroyed) {
            return;
        }

        if (mReportingController != null) {
            ReportingEvent reportingEvent = new ReportingEvent();
            reportingEvent.setEventType(Reporting.EventType.ERROR);
            reportingEvent.setTimestamp(System.currentTimeMillis());
            reportingEvent.setAdFormat(Reporting.AdFormat.FULLSCREEN);
            if (getAd() != null && !TextUtils.isEmpty(getAd().getVast())) {
                reportingEvent.setVast(getAd().getVast());
            }
            mReportingController.reportEvent(reportingEvent);
        }

        String errorMessage = "Interstitial error for zone id: ";
        Logger.d(TAG, errorMessage);
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
        if (mVideoListener != null) {
            mVideoListener.onVideoSkipped();
        }
    }
}
