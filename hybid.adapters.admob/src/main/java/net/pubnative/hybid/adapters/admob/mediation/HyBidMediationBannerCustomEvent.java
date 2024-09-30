// The MIT License (MIT)
//
// Copyright (c) 2023 PubNative GmbH
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
package net.pubnative.hybid.adapters.admob.mediation;

import android.app.Application;
import android.content.Context;
import android.text.TextUtils;
import android.view.View;

import androidx.annotation.NonNull;

import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.mediation.MediationAdLoadCallback;
import com.google.android.gms.ads.mediation.MediationBannerAd;
import com.google.android.gms.ads.mediation.MediationBannerAdCallback;
import com.google.android.gms.ads.mediation.MediationBannerAdConfiguration;
import com.google.android.gms.ads.mediation.MediationConfiguration;

import net.pubnative.hybid.adapters.admob.HyBidAdmobUtils;
import net.pubnative.lite.sdk.HyBid;
import net.pubnative.lite.sdk.utils.Logger;
import net.pubnative.lite.sdk.views.HyBidAdView;

public class HyBidMediationBannerCustomEvent extends HyBidMediationBaseCustomEvent {
    private static final String TAG = HyBidMediationBannerCustomEvent.class.getSimpleName();

    @Override
    public void loadBannerAd(@NonNull MediationBannerAdConfiguration mediationBannerAdConfiguration, @NonNull MediationAdLoadCallback<MediationBannerAd, MediationBannerAdCallback> callback) {
        if (callback == null) {
            Logger.e(TAG, "MediationAdLoadCallback is null");
            return;
        }

        if (mediationBannerAdConfiguration == null || mediationBannerAdConfiguration.getContext() == null) {
            Logger.e(TAG, "Missing context. Dropping call");
            return;
        }

        HyBidAdViewCustomEventLoader mAdLoader = new HyBidAdViewCustomEventLoader(mediationBannerAdConfiguration, callback);
        mAdLoader.loadAd();
    }

    protected net.pubnative.lite.sdk.models.AdSize getAdSize(AdSize adSize) {
        return net.pubnative.lite.sdk.models.AdSize.SIZE_320x50;
    }

    public class HyBidAdViewCustomEventLoader implements HyBidAdView.Listener, MediationBannerAd {
        private HyBidAdView mBannerView;
        private final MediationBannerAdConfiguration mAdConfiguration;
        private final MediationAdLoadCallback<MediationBannerAd, MediationBannerAdCallback> mAdLoadCallback;
        private MediationBannerAdCallback mBannerAdCallback;

        public HyBidAdViewCustomEventLoader(MediationBannerAdConfiguration mediationBannerAdConfiguration, MediationAdLoadCallback<MediationBannerAd, MediationBannerAdCallback> adLoadCallback) {
            mAdConfiguration = mediationBannerAdConfiguration;
            mAdLoadCallback = adLoadCallback;
        }

        public void loadAd() {
            String zoneId;
            String appToken;

            String serverParameter = mAdConfiguration.getServerParameters().getString(MediationConfiguration.CUSTOM_EVENT_SERVER_PARAMETER_FIELD);
            if (!TextUtils.isEmpty(HyBidAdmobUtils.getAppToken(serverParameter))
                    && !TextUtils.isEmpty(HyBidAdmobUtils.getZoneId(serverParameter))) {
                zoneId = HyBidAdmobUtils.getZoneId(serverParameter);
                appToken = HyBidAdmobUtils.getAppToken(serverParameter);
            } else {
                Logger.e(TAG, "Could not find the required params in MediationBannerAdConfiguration. " +
                        "Required params in MediationBannerAdConfiguration must be provided as a valid JSON Object. " +
                        "Please consult HyBid documentation and update settings in your AdMob publisher dashboard.");
                mAdLoadCallback.onFailure(new AdError(AdRequest.ERROR_CODE_NETWORK_ERROR,
                        "Could not find the required params in MediationBannerAdConfiguration",
                        AdError.UNDEFINED_DOMAIN
                ));
                return;
            }

            AdSize adSize = mAdConfiguration.getAdSize();
            net.pubnative.lite.sdk.models.AdSize hyBidAdSize = getAdSize(adSize);

            if (adSize.getWidth() < hyBidAdSize.getWidth() || adSize.getHeight() < hyBidAdSize.getHeight()) {
                Logger.e(TAG, "The requested ad size is smaller than " + hyBidAdSize);
                mAdLoadCallback.onFailure(new AdError(AdRequest.ERROR_CODE_INVALID_REQUEST,
                        "The requested ad size is smaller than " + hyBidAdSize,
                        AdError.UNDEFINED_DOMAIN
                ));
                return;
            }

            if (HyBid.getAppToken() != null && HyBid.getAppToken().equalsIgnoreCase(appToken) && HyBid.isInitialized()) {
                loadBanner(mAdConfiguration.getContext(), hyBidAdSize, zoneId);
            } else {
                HyBid.initialize(appToken, (Application) mAdConfiguration.getContext().getApplicationContext(), b -> loadBanner(mAdConfiguration.getContext(), hyBidAdSize, zoneId));
            }
        }

        private void loadBanner(Context context, net.pubnative.lite.sdk.models.AdSize adSize, String zoneId) {
            mBannerView = new HyBidAdView(context);
            mBannerView.setAdSize(adSize);
            mBannerView.setMediation(true);
            mBannerView.load(zoneId, this);
        }

        @NonNull
        @Override
        public View getView() {
            return mBannerView;
        }

        @Override
        public void onAdLoaded() {
            if (mAdLoadCallback != null) {
                mBannerAdCallback = mAdLoadCallback.onSuccess(this);
            }
        }

        @Override
        public void onAdLoadFailed(Throwable error) {
            Logger.e(TAG, error.getMessage());
            if (mAdLoadCallback != null) {
                mAdLoadCallback.onFailure(new AdError(AdRequest.ERROR_CODE_NO_FILL,
                        error != null && !TextUtils.isEmpty(error.getMessage()) ? error.getMessage() : "No fill.",
                        AdError.UNDEFINED_DOMAIN
                ));
            }
        }

        @Override
        public void onAdImpression() {
            if (mBannerAdCallback != null) {
                mBannerAdCallback.reportAdImpression();
            }
        }

        @Override
        public void onAdClick() {
            if (mBannerAdCallback != null) {
                mBannerAdCallback.reportAdClicked();
                mBannerAdCallback.onAdOpened();
                mBannerAdCallback.onAdLeftApplication();
            }
        }
    }
}
