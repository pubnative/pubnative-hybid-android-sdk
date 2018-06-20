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
package net.pubnative.lite.adapters.mopub.mediation;

import android.app.Activity;
import android.content.Context;

import com.mopub.mobileads.CustomEventInterstitial;
import com.mopub.mobileads.MoPubErrorCode;

import net.pubnative.lite.sdk.PNLite;
import net.pubnative.lite.sdk.interstitial.PNInterstitialAd;
import net.pubnative.lite.sdk.utils.Logger;

import java.util.Map;

public class PNLiteMediationInterstitialCustomEvent extends CustomEventInterstitial implements PNInterstitialAd.Listener {
    private static final String TAG = PNLiteMediationMRectCustomEvent.class.getSimpleName();

    private static final String APP_TOKEN_KEY = "pn_app_token";
    private static final String ZONE_ID_KEY = "pn_zone_id";
    private CustomEventInterstitialListener mInterstitialListener;
    private PNInterstitialAd mInterstitialAd;

    @Override
    protected void loadInterstitial(Context context, CustomEventInterstitialListener customEventInterstitialListener, Map<String, Object> localExtras, Map<String, String> serverExtras) {
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

        Activity activity = (Activity) context;

        String zoneId;
        String appToken;
        if (serverExtras.containsKey(ZONE_ID_KEY) && serverExtras.containsKey(APP_TOKEN_KEY)) {
            zoneId = serverExtras.get(ZONE_ID_KEY);
            appToken = serverExtras.get(APP_TOKEN_KEY);
        } else {
            Logger.e(TAG, "Could not find the required params in CustomEventInterstitial serverExtras");
            mInterstitialListener.onInterstitialFailed(MoPubErrorCode.ADAPTER_CONFIGURATION_ERROR);
            return;
        }

        if (appToken == null || !appToken.equals(PNLite.getAppToken())) {
            Logger.e(TAG, "The provided app token doesn't match the one used to initialise PNLite");
            mInterstitialListener.onInterstitialFailed(MoPubErrorCode.ADAPTER_CONFIGURATION_ERROR);
            return;
        }

        mInterstitialAd = new PNInterstitialAd(activity, zoneId, this);
        mInterstitialAd.load();
    }

    @Override
    protected void onInvalidate() {
        if (mInterstitialAd != null) {
            mInterstitialAd.destroy();
            mInterstitialAd = null;
        }
    }

    @Override
    protected void showInterstitial() {
        if (mInterstitialAd != null) {
            mInterstitialAd.show();
        }
    }

    //--------------------------------- PNInterstitialAd Callbacks ---------------------------------
    @Override
    public void onInterstitialLoaded() {
        if (mInterstitialListener != null) {
            mInterstitialListener.onInterstitialLoaded();
        }
    }

    @Override
    public void onInterstitialLoadFailed(Throwable error) {
        Logger.e(TAG, error.getMessage());
        mInterstitialListener.onInterstitialFailed(MoPubErrorCode.NETWORK_NO_FILL);
    }

    @Override
    public void onInterstitialImpression() {
        if (mInterstitialListener != null) {
            mInterstitialListener.onInterstitialShown();
        }
    }

    @Override
    public void onInterstitialDismissed() {
        if (mInterstitialListener != null) {
            mInterstitialListener.onInterstitialDismissed();
        }
    }

    @Override
    public void onInterstitialClick() {
        if (mInterstitialListener != null) {
            mInterstitialListener.onInterstitialClicked();
        }
    }
}
