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
package net.pubnative.lite.adapters.mopub;

import android.app.Activity;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.mopub.common.LifecycleListener;
import com.mopub.mobileads.AdData;
import com.mopub.mobileads.BaseAd;
import com.mopub.mobileads.MoPubErrorCode;

import net.pubnative.lite.sdk.HyBid;
import net.pubnative.lite.sdk.interstitial.presenter.InterstitialPresenter;
import net.pubnative.lite.sdk.interstitial.presenter.InterstitialPresenterFactory;
import net.pubnative.lite.sdk.models.Ad;
import net.pubnative.lite.sdk.utils.Logger;

public class HyBidMoPubInterstitialCustomEvent extends BaseAd implements InterstitialPresenter.Listener {
    private static final String TAG = HyBidMoPubInterstitialCustomEvent.class.getSimpleName();

    private static final String ZONE_ID_KEY = "pn_zone_id";

    private InterstitialPresenter mInterstitialPresenter;
    private String mZoneID = "";

    @Override
    protected boolean checkAndInitializeSdk(@NonNull Activity launcherActivity, @NonNull AdData adData) throws Exception {
        return false;
    }

    @Override
    protected void load(@NonNull Context context, @NonNull AdData adData) throws Exception {
        if (adData.getExtras().containsKey(ZONE_ID_KEY)) {
            mZoneID = adData.getExtras().get(ZONE_ID_KEY);
        } else {
            Logger.e(TAG, "Could not find zone id value in BaseAd adData");
            mLoadListener.onAdLoadFailed(MoPubErrorCode.ADAPTER_CONFIGURATION_ERROR);
            return;
        }

        final Ad ad = HyBid.getAdCache().inspect(mZoneID);
        if (ad == null) {
            Logger.e(TAG, "Could not find an ad in the cache for zone id with key " + mZoneID);
            mLoadListener.onAdLoadFailed(MoPubErrorCode.ADAPTER_CONFIGURATION_ERROR);
            return;
        }

        mInterstitialPresenter = new InterstitialPresenterFactory(context, mZoneID).createInterstitialPresenter(ad, this);
        if (mInterstitialPresenter == null) {
            Logger.e(TAG, "Could not create valid interstitial presenter");
            mLoadListener.onAdLoadFailed(MoPubErrorCode.ADAPTER_CONFIGURATION_ERROR);
            return;
        }

        setAutomaticImpressionAndClickTracking(false);
        mInterstitialPresenter.load();
    }

    @Override
    protected void show() {
        if (mInterstitialPresenter != null) {
            mInterstitialPresenter.show();
        }
    }

    @Override
    protected void onInvalidate() {
        if (mInterstitialPresenter != null) {
            mInterstitialPresenter.destroy();
            mInterstitialPresenter = null;
        }
    }

    @Nullable
    @Override
    protected LifecycleListener getLifecycleListener() {
        return null;
    }

    @NonNull
    @Override
    protected String getAdNetworkId() {
        return mZoneID;
    }

    @Override
    public void onInterstitialLoaded(InterstitialPresenter interstitialPresenter) {
        mLoadListener.onAdLoaded();
    }

    @Override
    public void onInterstitialError(InterstitialPresenter interstitialPresenter) {
        mLoadListener.onAdLoadFailed(MoPubErrorCode.INTERNAL_ERROR);
    }

    @Override
    public void onInterstitialShown(InterstitialPresenter interstitialPresenter) {
        mInteractionListener.onAdShown();
    }

    @Override
    public void onInterstitialClicked(InterstitialPresenter interstitialPresenter) {
        mInteractionListener.onAdClicked();
    }

    @Override
    public void onInterstitialDismissed(InterstitialPresenter interstitialPresenter) {
        mInteractionListener.onAdDismissed();
    }
}
