// The MIT License (MIT)
//
// Copyright (c) 2021 PubNative GmbH
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

import android.content.Context;

import com.mopub.common.logging.MoPubLog;
import com.mopub.mobileads.CustomEventInterstitial;
import com.mopub.mobileads.MoPubErrorCode;

import net.pubnative.lite.sdk.HyBid;
import net.pubnative.lite.sdk.interstitial.HyBidInterstitialAd;
import net.pubnative.lite.sdk.utils.Logger;

import java.util.Map;

public class HyBidMediationInterstitialCustomEvent extends CustomEventInterstitial implements HyBidInterstitialAd.Listener {
    private static final String TAG = HyBidMediationInterstitialCustomEvent.class.getSimpleName();

    private static final String APP_TOKEN_KEY = "pn_app_token";
    private static final String ZONE_ID_KEY = "pn_zone_id";

    private CustomEventInterstitialListener mInterstitialListener;
    private HyBidInterstitialAd mInterstitialAd;

    @Override
    protected void loadInterstitial(Context context, CustomEventInterstitialListener customEventInterstitialListener,
                                    Map<String, Object> localExtras, Map<String, String> serverExtras) {
        if (customEventInterstitialListener == null) {
            Logger.e(TAG, "customEventInterstitialListener is null");
            return;
        }
        mInterstitialListener = customEventInterstitialListener;

        String appToken;
        String zoneID;
        if (serverExtras.containsKey(ZONE_ID_KEY) && serverExtras.containsKey(APP_TOKEN_KEY)) {
            zoneID = serverExtras.get(ZONE_ID_KEY);
            appToken = serverExtras.get(APP_TOKEN_KEY);
        } else {
            Logger.e(TAG, "Could not find the required params in CustomEventBanner serverExtras");
            mInterstitialListener.onInterstitialFailed(MoPubErrorCode.ADAPTER_CONFIGURATION_ERROR);
            return;
        }

        if (appToken == null || !appToken.equals(HyBid.getAppToken())) {
            Logger.e(TAG, "The provided app token doesn't match the one used to initialise HyBid");
            mInterstitialListener.onInterstitialFailed(MoPubErrorCode.ADAPTER_CONFIGURATION_ERROR);
            return;
        }

        setAutomaticImpressionAndClickTracking(false);
        mInterstitialAd = new HyBidInterstitialAd(context, zoneID, this);
        mInterstitialAd.setMediation(true);
        mInterstitialAd.load();
        MoPubLog.log(MoPubLog.AdapterLogEvent.LOAD_ATTEMPTED, TAG);
    }

    @Override
    protected void showInterstitial() {
        if (mInterstitialAd != null) {
            mInterstitialAd.show();
            MoPubLog.log(MoPubLog.AdapterLogEvent.SHOW_ATTEMPTED, TAG);
        }
    }

    @Override
    protected void onInvalidate() {
        if (mInterstitialAd != null) {
            mInterstitialAd.destroy();
            mInterstitialAd = null;
        }
    }

    //--------------------------------- PNInterstitialAd Callbacks ---------------------------------
    @Override
    public void onInterstitialLoaded() {
        MoPubLog.log(MoPubLog.AdapterLogEvent.LOAD_SUCCESS, TAG);
        if (mInterstitialListener != null) {
            mInterstitialListener.onInterstitialLoaded();
        }
    }

    @Override
    public void onInterstitialLoadFailed(Throwable error) {
        MoPubLog.log(MoPubLog.AdapterLogEvent.LOAD_FAILED, TAG);
        Logger.e(TAG, error.getMessage());
        if (mInterstitialListener != null) {
            mInterstitialListener.onInterstitialFailed(MoPubErrorCode.NETWORK_NO_FILL);
        }
    }

    @Override
    public void onInterstitialImpression() {
        MoPubLog.log(MoPubLog.AdapterLogEvent.SHOW_SUCCESS, TAG);
        if (mInterstitialListener != null) {
            mInterstitialListener.onInterstitialShown();
        }
    }

    @Override
    public void onInterstitialDismissed() {
        MoPubLog.log(MoPubLog.AdapterLogEvent.DID_DISAPPEAR, TAG);
        if (mInterstitialListener != null) {
            mInterstitialListener.onInterstitialDismissed();
        }
    }

    @Override
    public void onInterstitialClick() {
        MoPubLog.log(MoPubLog.AdapterLogEvent.CLICKED, TAG);
        if (mInterstitialListener != null) {
            mInterstitialListener.onInterstitialClicked();
        }
    }
}
