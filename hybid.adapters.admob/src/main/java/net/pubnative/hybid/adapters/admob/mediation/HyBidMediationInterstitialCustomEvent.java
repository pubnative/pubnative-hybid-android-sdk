package net.pubnative.hybid.adapters.admob.mediation;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;

import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.mediation.MediationAdRequest;
import com.google.android.gms.ads.mediation.customevent.CustomEventInterstitial;
import com.google.android.gms.ads.mediation.customevent.CustomEventInterstitialListener;

import net.pubnative.hybid.adapters.admob.HyBidAdmobUtils;
import net.pubnative.lite.sdk.HyBid;
import net.pubnative.lite.sdk.interstitial.HyBidInterstitialAd;
import net.pubnative.lite.sdk.utils.Logger;

public class HyBidMediationInterstitialCustomEvent implements CustomEventInterstitial, HyBidInterstitialAd.Listener {
    private static final String TAG = HyBidMediationInterstitialCustomEvent.class.getSimpleName();

    protected CustomEventInterstitialListener mInterstitialListener;
    protected HyBidInterstitialAd mInterstitialAd;

    @Override
    public void requestInterstitialAd(Context context,
                                      CustomEventInterstitialListener customEventInterstitialListener,
                                      String serverParameter,
                                      MediationAdRequest mediationAdRequest,
                                      Bundle bundle) {
        if (customEventInterstitialListener == null) {
            Logger.e(TAG, "customEventInterstitialListener is null");
            return;
        }
        mInterstitialListener = customEventInterstitialListener;

        String zoneId;
        String appToken;
        if (!TextUtils.isEmpty(HyBidAdmobUtils.getAppToken(serverParameter))
                && !TextUtils.isEmpty(HyBidAdmobUtils.getZoneId(serverParameter))) {
            zoneId = HyBidAdmobUtils.getZoneId(serverParameter);
            appToken = HyBidAdmobUtils.getAppToken(serverParameter);
        } else {
            Logger.e(TAG, "Could not find the required params in CustomEventInterstitial serverExtras");
            mInterstitialListener.onAdFailedToLoad(new AdError(AdRequest.ERROR_CODE_NETWORK_ERROR,
                    "Could not find the required params in CustomEventInterstitial serverExtras",
                    AdError.UNDEFINED_DOMAIN
            ));
            return;
        }

        if (appToken == null || !appToken.equals(HyBid.getAppToken())) {
            Logger.e(TAG, "The provided app token doesn't match the one used to initialise HyBid");
            mInterstitialListener.onAdFailedToLoad(new AdError(AdRequest.ERROR_CODE_NETWORK_ERROR,
                    "The provided app token doesn't match the one used to initialise HyBid",
                    AdError.UNDEFINED_DOMAIN
            ));
            return;
        }

        mInterstitialAd = new HyBidInterstitialAd(context, zoneId, this);
        mInterstitialAd.setMediation(true);
        mInterstitialAd.load();
    }

    @Override
    public void showInterstitial() {
        if (mInterstitialAd != null) {
            mInterstitialAd.show();
        }
    }

    @Override
    public void onResume() {

    }

    @Override
    public void onPause() {

    }

    @Override
    public void onDestroy() {
        if (mInterstitialAd != null) {
            mInterstitialAd.destroy();
            mInterstitialAd = null;
        }
    }

    //-------------------------------- HyBidInterstitialAd Callbacks -------------------------------
    @Override
    public void onInterstitialLoaded() {
        if (mInterstitialListener != null) {
            mInterstitialListener.onAdLoaded();
        }
    }

    @Override
    public void onInterstitialLoadFailed(Throwable error) {
        Logger.e(TAG, error.getMessage());
        if (mInterstitialListener != null) {
            mInterstitialListener.onAdFailedToLoad(new AdError(AdRequest.ERROR_CODE_NO_FILL,
                    "No fill.",
                    AdError.UNDEFINED_DOMAIN
            ));
        }
    }

    @Override
    public void onInterstitialImpression() {
        if (mInterstitialListener != null) {
            mInterstitialListener.onAdOpened();
        }
    }

    @Override
    public void onInterstitialDismissed() {
        if (mInterstitialListener != null) {
            mInterstitialListener.onAdClosed();
        }
    }

    @Override
    public void onInterstitialClick() {
        if (mInterstitialListener != null) {
            mInterstitialListener.onAdClicked();
            mInterstitialListener.onAdLeftApplication();
        }
    }
}
