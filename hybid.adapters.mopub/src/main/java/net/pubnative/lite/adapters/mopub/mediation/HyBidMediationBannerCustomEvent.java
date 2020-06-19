// The MIT License (MIT)
//
// Copyright (c) 2020 PubNative GmbH
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
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.mopub.common.LifecycleListener;
import com.mopub.common.logging.MoPubLog;
import com.mopub.mobileads.AdData;
import com.mopub.mobileads.BaseAd;
import com.mopub.mobileads.MoPubErrorCode;

import net.pubnative.lite.sdk.HyBid;
import net.pubnative.lite.sdk.utils.Logger;
import net.pubnative.lite.sdk.views.HyBidBannerAdView;
import net.pubnative.lite.sdk.views.PNAdView;

public class HyBidMediationBannerCustomEvent extends BaseAd implements PNAdView.Listener {
    private static final String TAG = HyBidMediationBannerCustomEvent.class.getSimpleName();

    private static final String APP_TOKEN_KEY = "pn_app_token";
    private static final String ZONE_ID_KEY = "pn_zone_id";

    private HyBidBannerAdView mBannerView;
    private String mZoneID = "";

    @Override
    protected boolean checkAndInitializeSdk(@NonNull Activity launcherActivity, @NonNull AdData adData) throws Exception {
        return false;
    }

    @Override
    protected void load(@NonNull Context context, @NonNull AdData adData) throws Exception {

        String appToken;
        if (adData.getExtras().containsKey(ZONE_ID_KEY) && adData.getExtras().containsKey(APP_TOKEN_KEY)) {
            mZoneID = adData.getExtras().get(ZONE_ID_KEY);
            appToken = adData.getExtras().get(APP_TOKEN_KEY);
        } else {
            Logger.e(TAG, "Could not find the required params in CustomEventBanner serverExtras");
            mLoadListener.onAdLoadFailed(MoPubErrorCode.ADAPTER_CONFIGURATION_ERROR);
            return;
        }

        if (appToken == null || !appToken.equals(HyBid.getAppToken())) {
            Logger.e(TAG, "The provided app token doesn't match the one used to initialise HyBid");
            mLoadListener.onAdLoadFailed(MoPubErrorCode.ADAPTER_CONFIGURATION_ERROR);
            return;
        }

        setAutomaticImpressionAndClickTracking(false);
        mBannerView = new HyBidBannerAdView(context);
        mBannerView.setMediation(true);
        mBannerView.load(mZoneID, this);
        MoPubLog.log(MoPubLog.AdapterLogEvent.LOAD_ATTEMPTED, TAG);
    }

    @Override
    protected void show() {
        super.show();
    }

    @Override
    protected void onInvalidate() {
        if (mBannerView != null) {
            mBannerView.destroy();
            mBannerView = null;
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

    @Nullable
    @Override
    protected View getAdView() {
        return mBannerView;
    }

    //------------------------------------ PNAdView Callbacks --------------------------------------
    @Override
    public void onAdLoaded() {
        MoPubLog.log(MoPubLog.AdapterLogEvent.LOAD_SUCCESS, TAG);
        mLoadListener.onAdLoaded();
    }

    @Override
    public void onAdLoadFailed(Throwable error) {
        MoPubLog.log(MoPubLog.AdapterLogEvent.LOAD_FAILED, TAG);
        mLoadListener.onAdLoadFailed(MoPubErrorCode.NETWORK_NO_FILL);
    }

    @Override
    public void onAdImpression() {
        MoPubLog.log(MoPubLog.AdapterLogEvent.SHOW_SUCCESS, TAG);
        mInteractionListener.onAdImpression();
    }

    @Override
    public void onAdClick() {
        MoPubLog.log(MoPubLog.AdapterLogEvent.CLICKED, TAG);
        mInteractionListener.onAdClicked();
    }
}
