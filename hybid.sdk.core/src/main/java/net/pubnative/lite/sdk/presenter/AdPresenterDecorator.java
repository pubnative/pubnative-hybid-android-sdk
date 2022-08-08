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
package net.pubnative.lite.sdk.presenter;

import android.text.TextUtils;
import android.util.Log;
import android.view.View;

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
 * Created by erosgarciaponte on 08.01.18.
 */

public class AdPresenterDecorator implements AdPresenter, AdPresenter.Listener, AdPresenter.ImpressionListener, VideoListener {
    private static final String TAG = AdPresenterDecorator.class.getSimpleName();
    private static final String ERROR_DECORATOR_DESTROYED = "AdPresenterDecorator is destroyed";

    private final AdPresenter mAdPresenter;
    private final AdTracker mAdTrackingDelegate;
    private final ReportingController mReportingController;
    private final AdPresenter.Listener mListener;
    private final ImpressionListener mImpressionListener;
    private VideoListener mVideoListener;
    private boolean mIsDestroyed = false;
    private boolean mImpressionConfirmed = false;

    public AdPresenterDecorator(AdPresenter adPresenter,
                                AdTracker adTrackingDelegate,
                                ReportingController reportingController,
                                AdPresenter.Listener listener,
                                AdPresenter.ImpressionListener impressionListener) {
        mAdPresenter = adPresenter;
        mAdTrackingDelegate = adTrackingDelegate;
        mReportingController = reportingController;
        mListener = listener;
        mImpressionListener = impressionListener;
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
        if (!CheckUtils.NoThrow.checkArgument(!mIsDestroyed, ERROR_DECORATOR_DESTROYED)) {
            return;
        }
        mAdPresenter.startTracking();
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

        if (mReportingController != null) {
            ReportingEvent reportingEvent = new ReportingEvent();
            reportingEvent.setEventType(Reporting.EventType.CLICK);
            reportingEvent.setTimestamp(String.valueOf(System.currentTimeMillis()));
            reportingEvent.setAdFormat(Reporting.AdFormat.BANNER);
            mReportingController.reportEvent(reportingEvent);
        }

        mAdTrackingDelegate.trackClick();
        mListener.onAdClicked(adPresenter);
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

        if (mImpressionConfirmed) {
            Log.i(TAG, "impression is already confirmed, dropping impression tracking");
            return;
        }

        if (mReportingController != null) {
            ReportingEvent reportingEvent = new ReportingEvent();
            reportingEvent.setEventType(Reporting.EventType.IMPRESSION);
            reportingEvent.setTimestamp(String.valueOf(System.currentTimeMillis()));
            reportingEvent.setAdFormat(Reporting.AdFormat.BANNER);
            mReportingController.reportEvent(reportingEvent);
        }

        mImpressionConfirmed = true;
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
}
