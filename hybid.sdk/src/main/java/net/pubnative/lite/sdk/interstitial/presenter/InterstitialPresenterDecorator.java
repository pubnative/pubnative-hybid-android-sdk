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

import net.pubnative.lite.sdk.models.Ad;
import net.pubnative.lite.sdk.utils.AdTracker;
import net.pubnative.lite.sdk.utils.CheckUtils;
import net.pubnative.lite.sdk.utils.Logger;

/**
 * Created by erosgarciaponte on 09.01.18.
 */

public class InterstitialPresenterDecorator implements InterstitialPresenter, InterstitialPresenter.Listener {
    private static final String TAG = InterstitialPresenterDecorator.class.getSimpleName();
    private final InterstitialPresenter mInterstitialPresenter;
    private final AdTracker mAdTrackingDelegate;
    private final InterstitialPresenter.Listener mListener;
    private boolean mIsDestroyed;

    public InterstitialPresenterDecorator(InterstitialPresenter interstitialPresenter,
                                          AdTracker adTrackingDelegate,
                                          InterstitialPresenter.Listener listener) {
        mInterstitialPresenter = interstitialPresenter;
        mAdTrackingDelegate = adTrackingDelegate;
        mListener = listener;
    }

    @Override
    public void setListener(InterstitialPresenter.Listener listener) {
        // We set the listener in the constructor instead
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
    public void hide() {
        if (!CheckUtils.NoThrow.checkArgument(!mIsDestroyed, "InterstitialPresenterDecorator is destroyed")) {
            return;
        }

        mInterstitialPresenter.hide();
    }

    @Override
    public void destroy() {
        //Logger.d(TAG, "Destroying interstitial presenter for ad unit id: " + getAd().getAdUnitId());
        mInterstitialPresenter.destroy();
        mIsDestroyed = true;
    }

    @Override
    public void onInterstitialLoaded(InterstitialPresenter interstitialPresenter) {
        if (mIsDestroyed) {
            return;
        }

        //ogger.d(TAG, "Interstitial loaded for ad unit id: " + getAd().getAdUnitId());
        mListener.onInterstitialLoaded(interstitialPresenter);
    }

    @Override
    public void onInterstitialShown(InterstitialPresenter interstitialPresenter) {
        if (mIsDestroyed) {
            return;
        }

        //Logger.d(TAG, "Interstitial shown for ad unit id: " + getAd().getAdUnitId());
        mAdTrackingDelegate.trackImpression();
        mListener.onInterstitialShown(interstitialPresenter);
    }

    @Override
    public void onInterstitialClicked(InterstitialPresenter interstitialPresenter) {
        if (mIsDestroyed) {
            return;
        }

        //Logger.d(TAG, "Interstitial clicked for ad unit id: " + getAd().getAdUnitId());
        mAdTrackingDelegate.trackClick();
        mListener.onInterstitialClicked(interstitialPresenter);
    }

    @Override
    public void onInterstitialDismissed(InterstitialPresenter interstitialPresenter) {
        if (mIsDestroyed) {
            return;
        }

        //Logger.d(TAG, "Interstitial dismissed for ad unit id: " + getAd().getAdUnitId());
        mListener.onInterstitialDismissed(interstitialPresenter);
    }

    @Override
    public void onInterstitialError(InterstitialPresenter interstitialPresenter) {
        if (mIsDestroyed) {
            return;
        }

        //String errorMessage = "Interstitial error for ad unit id: " + getAd().getAdUnitId();
        String errorMessage = "Interstitial error for zone id: ";
        Logger.d(TAG, errorMessage);
        //mAdTrackingDelegate.trackError(errorMessage);
        mListener.onInterstitialError(interstitialPresenter);
    }
}