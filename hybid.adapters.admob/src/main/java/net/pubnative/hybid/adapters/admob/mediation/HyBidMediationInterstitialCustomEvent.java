package net.pubnative.hybid.adapters.admob.mediation;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;

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

    private CustomEventInterstitialListener mInterstitialListener;
    private HyBidInterstitialAd mInterstitialAd;

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

        if (!(context instanceof Activity)) {
            Logger.e(TAG, "HyBid interstitial ad can only be rendered with an Activity context");
            mInterstitialListener.onAdFailedToLoad(AdRequest.ERROR_CODE_NETWORK_ERROR);
            return;
        }

        Activity activity = (Activity) context;

        String zoneId;
        String appToken;
        if (!TextUtils.isEmpty(HyBidAdmobUtils.getAppToken(serverParameter))
                && !TextUtils.isEmpty(HyBidAdmobUtils.getZoneId(serverParameter))) {
            zoneId = HyBidAdmobUtils.getZoneId(serverParameter);
            appToken = HyBidAdmobUtils.getAppToken(serverParameter);
        } else {
            Logger.e(TAG, "Could not find the required params in CustomEventInterstitial serverExtras");
            mInterstitialListener.onAdFailedToLoad(AdRequest.ERROR_CODE_NETWORK_ERROR);
            return;
        }

        if (appToken == null || !appToken.equals(HyBid.getAppToken())) {
            Logger.e(TAG, "The provided app token doesn't match the one used to initialise HyBid");
            mInterstitialListener.onAdFailedToLoad(AdRequest.ERROR_CODE_NETWORK_ERROR);
            return;
        }

        mInterstitialAd = new HyBidInterstitialAd(activity, zoneId, this);
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
            mInterstitialListener.onAdFailedToLoad(AdRequest.ERROR_CODE_NO_FILL);
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
