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
import android.view.View;

import androidx.annotation.NonNull;

import com.mopub.common.logging.MoPubLog;
import com.mopub.nativeads.CustomEventNative;
import com.mopub.nativeads.ImpressionTracker;
import com.mopub.nativeads.NativeErrorCode;
import com.mopub.nativeads.StaticNativeAd;

import net.pubnative.lite.sdk.HyBid;

import net.pubnative.lite.sdk.models.NativeAd;
import net.pubnative.lite.sdk.request.HyBidNativeAdRequest;
import net.pubnative.lite.sdk.utils.Logger;

import java.util.Map;

public class HyBidMediationNativeCustomEvent extends CustomEventNative implements HyBidNativeAdRequest.RequestListener {
    private static final String TAG = HyBidMediationNativeCustomEvent.class.getSimpleName();

    private static final String APP_TOKEN_KEY = "pn_app_token";
    private static final String ZONE_ID_KEY = "pn_zone_id";

    private CustomEventNativeListener mListener;
    private Context mContext;
    private HyBidNativeAdRequest mAdRequest;

    @Override
    protected void loadNativeAd(@NonNull Context context,
                                @NonNull CustomEventNativeListener customEventNativeListener,
                                @NonNull Map<String, Object> localExtras,
                                @NonNull Map<String, String> serverExtras) {
        mContext = context;
        mListener = customEventNativeListener;

        String zoneId;
        String appToken;
        if (serverExtras.containsKey(ZONE_ID_KEY) && serverExtras.containsKey(APP_TOKEN_KEY)) {
            zoneId = serverExtras.get(ZONE_ID_KEY);
            appToken = serverExtras.get(APP_TOKEN_KEY);
        } else {
            Logger.e(TAG, "Could not find the required params in CustomEventNative serverExtras");
            mListener.onNativeAdFailed(NativeErrorCode.NATIVE_ADAPTER_CONFIGURATION_ERROR);
            return;
        }

        if (appToken == null || !appToken.equals(HyBid.getAppToken())) {
            Logger.e(TAG, "The provided app token doesn't match the one used to initialise HyBid");
            mListener.onNativeAdFailed(NativeErrorCode.NATIVE_ADAPTER_CONFIGURATION_ERROR);
            return;
        }

        mAdRequest = new HyBidNativeAdRequest();
        mAdRequest.setMediation(true);
        mAdRequest.load(zoneId, this);
        MoPubLog.log(MoPubLog.AdapterLogEvent.LOAD_ATTEMPTED, TAG);
    }

    @Override
    public void onRequestSuccess(NativeAd ad) {
        MoPubLog.log(MoPubLog.AdapterLogEvent.LOAD_SUCCESS, TAG);
        if (mListener != null) {
            mListener.onNativeAdLoaded(new HyBidStaticNativeAd(ad, new ImpressionTracker(mContext)));
        }
    }

    @Override
    public void onRequestFail(Throwable throwable) {
        MoPubLog.log(MoPubLog.AdapterLogEvent.LOAD_FAILED, TAG);
        if (mListener != null) {
            mListener.onNativeAdFailed(NativeErrorCode.NETWORK_NO_FILL);
        }
    }

    private static class HyBidStaticNativeAd extends StaticNativeAd implements NativeAd.Listener {
        private final NativeAd mNativeAd;
        private final ImpressionTracker mImpressionTracker;

        public HyBidStaticNativeAd(NativeAd nativeAd,
                                   ImpressionTracker impressionTracker) {
            this.mNativeAd = nativeAd;
            this.mImpressionTracker = impressionTracker;
            fillData();
        }

        private void fillData() {
            setTitle(mNativeAd.getTitle());
            setText(mNativeAd.getDescription());
            setIconImageUrl(mNativeAd.getIconUrl());
            setMainImageUrl(mNativeAd.getBannerUrl());
            setCallToAction(mNativeAd.getCallToActionText());
            setStarRating((double) mNativeAd.getRating());
            setPrivacyInformationIconImageUrl(mNativeAd.getContentInfoIconUrl());
            setPrivacyInformationIconClickThroughUrl(mNativeAd.getContentInfoClickUrl());
        }

        @Override
        public void prepare(@NonNull View view) {
            mImpressionTracker.addView(view, this);
            mNativeAd.startTracking(view, this);
        }

        @Override
        public void clear(@NonNull View view) {
            mImpressionTracker.removeView(view);
            mNativeAd.stopTracking();
        }

        @Override
        public void onAdImpression(NativeAd ad, View view) {
            MoPubLog.log(MoPubLog.AdapterLogEvent.SHOW_SUCCESS, TAG);
            notifyAdImpressed();
        }

        @Override
        public void onAdClick(NativeAd ad, View view) {
            notifyAdClicked();
        }
    }
}