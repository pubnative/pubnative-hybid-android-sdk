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

import android.view.View;

import net.pubnative.lite.sdk.analytics.Reporting;
import net.pubnative.lite.sdk.analytics.ReportingController;
import net.pubnative.lite.sdk.analytics.ReportingEvent;
import net.pubnative.lite.sdk.models.Ad;
import net.pubnative.lite.sdk.utils.AdTracker;
import net.pubnative.lite.sdk.utils.CheckUtils;
import net.pubnative.lite.sdk.utils.Logger;

/**
 * Created by erosgarciaponte on 08.01.18.
 */

public class AdPresenterDecorator implements AdPresenter, AdPresenter.Listener, AdPresenter.ImpressionListener{
    private static final String TAG = AdPresenterDecorator.class.getSimpleName();
    private final AdPresenter mAdPresenter;
    private final AdTracker mAdTrackingDelegate;
    private final ReportingController mReportingController;
    private final AdPresenter.Listener mListener;
    private boolean mIsDestroyed;

    public AdPresenterDecorator(AdPresenter adPresenter,
                                AdTracker adTrackingDelegate,
                                ReportingController reportingController,
                                AdPresenter.Listener listener) {
        mAdPresenter = adPresenter;
        mAdTrackingDelegate = adTrackingDelegate;
        mReportingController = reportingController;
        mListener = listener;
    }

    @Override
    public void setListener(Listener listener) {
        // We set the listener in the constructor instead
    }

    @Override
    public void setImpressionListener(ImpressionListener listener) {
        // Not needed in the decorator
    }

    @Override
    public Ad getAd() {
        return mAdPresenter.getAd();
    }

    @Override
    public void load() {
        if (!CheckUtils.NoThrow.checkArgument(!mIsDestroyed, "AdPresenterDecorator is destroyed")) {
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
        if (!CheckUtils.NoThrow.checkArgument(!mIsDestroyed, "AdPresenterDecorator is destroyed")) {
            return;
        }
        mAdPresenter.startTracking();
    }

    @Override
    public void stopTracking() {
        if (!CheckUtils.NoThrow.checkArgument(!mIsDestroyed, "AdPresenterDecorator is destroyed")) {
            return;
        }

        mAdPresenter.stopTracking();
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
            mReportingController.reportEvent(reportingEvent);
        }

        String errorMessage = "Banner error for zone id: ";
        Logger.d(TAG, errorMessage);
        mListener.onAdError(adPresenter);
    }

    @Override
    public void onImpression(){
        if (mIsDestroyed) {
            return;
        }

        if (mReportingController != null) {
            ReportingEvent reportingEvent = new ReportingEvent();
            reportingEvent.setEventType(Reporting.EventType.IMPRESSION);
            reportingEvent.setTimestamp(String.valueOf(System.currentTimeMillis()));
            reportingEvent.setAdFormat(Reporting.AdFormat.BANNER);
            mReportingController.reportEvent(reportingEvent);
        }

        mAdTrackingDelegate.trackImpression();
    }
}
