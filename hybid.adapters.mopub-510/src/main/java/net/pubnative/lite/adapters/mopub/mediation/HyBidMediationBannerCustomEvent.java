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
import com.mopub.mobileads.CustomEventBanner;
import com.mopub.mobileads.MoPubErrorCode;

import net.pubnative.lite.sdk.HyBid;
import net.pubnative.lite.sdk.models.AdSize;
import net.pubnative.lite.sdk.utils.Logger;
import net.pubnative.lite.sdk.views.HyBidAdView;

import java.util.Map;

public class HyBidMediationBannerCustomEvent extends CustomEventBanner implements HyBidAdView.Listener {
    private static final String TAG = HyBidMediationBannerCustomEvent.class.getSimpleName();

    protected static final String APP_TOKEN_KEY = "pn_app_token";
    protected static final String ZONE_ID_KEY = "pn_zone_id";

    private CustomEventBanner.CustomEventBannerListener mBannerListener;
    protected HyBidAdView mBannerView;

    @Override
    protected void loadBanner(Context context, CustomEventBannerListener customEventBannerListener, Map<String, Object> localExtras, Map<String, String> serverExtras) {
        if (customEventBannerListener == null) {
            Logger.e(TAG, "customEventBannerListener is null");
            return;
        }

        mBannerListener = customEventBannerListener;

        String appToken;
        String zoneID;
        if (serverExtras.containsKey(ZONE_ID_KEY) && serverExtras.containsKey(APP_TOKEN_KEY)) {
            zoneID = serverExtras.get(ZONE_ID_KEY);
            appToken = serverExtras.get(APP_TOKEN_KEY);
        } else {
            Logger.e(TAG, "Could not find the required params in CustomEventBanner serverExtras");
            mBannerListener.onBannerFailed(MoPubErrorCode.ADAPTER_CONFIGURATION_ERROR);
            return;
        }

        if (appToken == null || !appToken.equals(HyBid.getAppToken())) {
            Logger.e(TAG, "The provided app token doesn't match the one used to initialise HyBid");
            mBannerListener.onBannerFailed(MoPubErrorCode.ADAPTER_CONFIGURATION_ERROR);
            return;
        }

        setAutomaticImpressionAndClickTracking(false);
        mBannerView = new HyBidAdView(context);
        mBannerView.setAdSize(getAdSize());
        mBannerView.setMediation(true);
        mBannerView.load(zoneID, this);
        MoPubLog.log(MoPubLog.AdapterLogEvent.LOAD_ATTEMPTED, TAG);
    }


    @Override
    protected void onInvalidate() {
        if (mBannerView != null) {
            mBannerView.destroy();
            mBannerView = null;
        }
    }

    protected AdSize getAdSize() {
        return AdSize.SIZE_320x50;
    }

    //------------------------------------ PNAdView Callbacks --------------------------------------
    @Override
    public void onAdLoaded() {
        MoPubLog.log(MoPubLog.AdapterLogEvent.LOAD_SUCCESS, TAG);
        if (mBannerListener != null) {
            mBannerListener.onBannerLoaded(mBannerView);
        }
    }

    @Override
    public void onAdLoadFailed(Throwable error) {
        MoPubLog.log(MoPubLog.AdapterLogEvent.LOAD_FAILED, TAG);
        if (mBannerListener != null) {
            mBannerListener.onBannerFailed(MoPubErrorCode.NETWORK_NO_FILL);
        }
    }

    @Override
    public void onAdImpression() {
        MoPubLog.log(MoPubLog.AdapterLogEvent.SHOW_SUCCESS, TAG);
        if (mBannerListener != null) {
            mBannerListener.onBannerImpression();
        }
    }

    @Override
    public void onAdClick() {
        MoPubLog.log(MoPubLog.AdapterLogEvent.CLICKED, TAG);
        if (mBannerListener != null) {
            mBannerListener.onBannerClicked();
        }
    }
}