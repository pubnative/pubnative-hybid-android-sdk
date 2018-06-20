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

import com.mopub.mobileads.CustomEventInterstitial;
import com.mopub.mobileads.MoPubErrorCode;

import net.pubnative.lite.sdk.PNLite;
import net.pubnative.lite.sdk.interstitial.presenter.InterstitialPresenter;
import net.pubnative.lite.sdk.interstitial.presenter.InterstitialPresenterFactory;
import net.pubnative.lite.sdk.models.Ad;
import net.pubnative.lite.sdk.utils.Logger;

import java.util.Map;

/**
 * Created by erosgarciaponte on 12.01.18.
 */

public class PNLiteMoPubInterstitialCustomEvent extends CustomEventInterstitial implements InterstitialPresenter.Listener {
    private static final String TAG = PNLiteMoPubInterstitialCustomEvent.class.getSimpleName();

    private static final String ZONE_ID_KEY = "pn_zone_id";
    private CustomEventInterstitialListener mInterstitialListener;
    private InterstitialPresenter mInterstitialPresenter;

    @Override
    protected void loadInterstitial(Context context,
                                    CustomEventInterstitialListener customEventInterstitialListener,
                                    Map<String, Object> localExtras,
                                    Map<String, String> serverExtras) {

        if (customEventInterstitialListener == null) {
            Logger.e(TAG, "customEventInterstitialListener is null");
            return;
        }
        mInterstitialListener = customEventInterstitialListener;

        if (!(context instanceof Activity)) {
            Logger.e(TAG, "PNLite interstitial ad can only be rendered with an Activity context");
            mInterstitialListener.onInterstitialFailed(MoPubErrorCode.ADAPTER_CONFIGURATION_ERROR);
            return;
        }
        final Activity activity = (Activity) context;

        String zoneIdKey;
        if (localExtras.containsKey(ZONE_ID_KEY)) {
            zoneIdKey = (String) localExtras.get(ZONE_ID_KEY);
        } else if (serverExtras.containsKey(ZONE_ID_KEY)) {
            zoneIdKey = serverExtras.get(ZONE_ID_KEY);
        } else {
            Logger.e(TAG, "Could not find zone id value in CustomEventInterstitial localExtras or serverExtras");
            mInterstitialListener.onInterstitialFailed(MoPubErrorCode.ADAPTER_CONFIGURATION_ERROR);
            return;
        }

        final Ad ad = PNLite.getAdCache().remove(zoneIdKey);
        if (ad == null) {
            Logger.e(TAG, "Could not find an ad in the cache for zone id with key " + zoneIdKey);
            mInterstitialListener.onInterstitialFailed(MoPubErrorCode.ADAPTER_CONFIGURATION_ERROR);
            return;
        }

        mInterstitialPresenter = new InterstitialPresenterFactory(activity).createInterstitialPresenter(ad, this);
        if (mInterstitialPresenter == null) {
            Logger.e(TAG, "Could not create valid interstitial presenter");
            mInterstitialListener.onInterstitialFailed(MoPubErrorCode.ADAPTER_CONFIGURATION_ERROR);
            return;
        }

        mInterstitialPresenter.load();
    }

    @Override
    protected void showInterstitial() {
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

    @Override
    public void onInterstitialLoaded(InterstitialPresenter interstitialPresenter) {
        if (mInterstitialListener != null) {
            mInterstitialListener.onInterstitialLoaded();
        }
    }

    @Override
    public void onInterstitialShown(InterstitialPresenter interstitialPresenter) {
        if (mInterstitialListener != null) {
            mInterstitialListener.onInterstitialShown();
        }
    }

    @Override
    public void onInterstitialClicked(InterstitialPresenter interstitialPresenter) {
        if (mInterstitialListener != null) {
            mInterstitialListener.onInterstitialClicked();
        }
    }

    @Override
    public void onInterstitialDismissed(InterstitialPresenter interstitialPresenter) {
        if (mInterstitialListener != null) {
            mInterstitialListener.onInterstitialDismissed();
        }
    }

    @Override
    public void onInterstitialError(InterstitialPresenter interstitialPresenter) {
        if (mInterstitialListener != null) {
            mInterstitialListener.onInterstitialFailed(MoPubErrorCode.INTERNAL_ERROR);
        }
    }
}
