// HyBid SDK License
//
// https://github.com/pubnative/pubnative-hybid-android-sdk/blob/main/LICENSE
//
package com.ironsource.adapters.custom.verve;

import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;

import com.ironsource.mediationsdk.adunit.adapter.BaseInterstitial;
import com.ironsource.mediationsdk.adunit.adapter.listener.InterstitialAdListener;
import com.ironsource.mediationsdk.adunit.adapter.utility.AdData;
import com.ironsource.mediationsdk.adunit.adapter.utility.AdapterErrorType;
import com.ironsource.mediationsdk.adunit.adapter.utility.AdapterErrors;
import com.ironsource.mediationsdk.model.NetworkSettings;

import net.pubnative.lite.sdk.HyBid;
import net.pubnative.lite.sdk.VideoListener;
import net.pubnative.lite.sdk.interstitial.HyBidInterstitialAd;
import net.pubnative.lite.sdk.utils.Logger;

public class VerveCustomInterstitial extends BaseInterstitial<VerveCustomAdapter> implements HyBidInterstitialAd.Listener, VideoListener {
    private static final String TAG = VerveCustomInterstitial.class.getSimpleName();

    private InterstitialAdListener mInterstitialAdListener;
    private HyBidInterstitialAd mInterstitialAd;

    public VerveCustomInterstitial(NetworkSettings networkSettings) {
        super(networkSettings);
    }

    @Override
    public void loadAd(AdData adData, Activity activity, InterstitialAdListener interstitialAdListener) {
        String appToken;
        String zoneID;
        if (!TextUtils.isEmpty(adData.getString(VerveCustomAdapter.KEY_APP_TOKEN))
                && !TextUtils.isEmpty(adData.getString(VerveCustomAdapter.KEY_ZONE_ID))) {
            zoneID = adData.getString(VerveCustomAdapter.KEY_ZONE_ID);
            appToken = adData.getString(VerveCustomAdapter.KEY_APP_TOKEN);
        } else {
            String errorMessage = "Could not find the required params in VerveCustomInterstitial ad data. " +
                    "Required params in VerveCustomInterstitial ad data must be provided as a valid JSON Object. " +
                    "Please consult HyBid documentation and update settings in your IronSource publisher dashboard.";
            Logger.e(TAG, errorMessage);
            interstitialAdListener.onAdLoadFailed(AdapterErrorType.ADAPTER_ERROR_TYPE_INTERNAL,
                    AdapterErrors.ADAPTER_ERROR_MISSING_PARAMS, errorMessage);
            return;
        }

        mInterstitialAdListener = interstitialAdListener;

        if (HyBid.getAppToken() != null && HyBid.getAppToken().equalsIgnoreCase(appToken) && HyBid.isInitialized()) {
            loadInterstitial(activity, zoneID);
        } else {
            HyBid.initialize(appToken, activity.getApplication(), b -> loadInterstitial(activity, zoneID));
        }
    }

    private void loadInterstitial(Activity activity, String zoneId) {
        mInterstitialAd = new HyBidInterstitialAd(activity, zoneId, this);
        mInterstitialAd.setVideoListener(this);
        mInterstitialAd.setMediation(true);
        mInterstitialAd.setMediationVendor(VerveCustomAdapter.IRONSOURCE_MEDIATION_VENDOR);
        mInterstitialAd.load();
    }

    @Override
    public boolean isAdAvailable(AdData adData) {
        if (mInterstitialAd != null) {
            return mInterstitialAd.isReady();
        } else {
            return false;
        }
    }

    @Override
    public void showAd(AdData adData, InterstitialAdListener listener) {
        if (mInterstitialAd != null) {
            mInterstitialAd.show();
        }
    }

    //------------------------------- HyBidInterstitialAd Callbacks --------------------------------
    @Override
    public void onInterstitialLoaded() {
        if (mInterstitialAdListener != null) {
            mInterstitialAdListener.onAdLoadSuccess();
        }
    }

    @Override
    public void onInterstitialLoadFailed(Throwable error) {
        if (mInterstitialAdListener != null) {
            mInterstitialAdListener.onAdLoadFailed(AdapterErrorType.ADAPTER_ERROR_TYPE_NO_FILL,
                    AdapterErrors.ADAPTER_ERROR_INTERNAL, error.getMessage());
        }
    }

    @Override
    public void onInterstitialImpression() {
        if (mInterstitialAdListener != null) {
            mInterstitialAdListener.onAdOpened();
            mInterstitialAdListener.onAdShowSuccess();
            mInterstitialAdListener.onAdVisible();
        }
    }

    @Override
    public void onInterstitialClick() {
        if (mInterstitialAdListener != null) {
            mInterstitialAdListener.onAdClicked();
        }
    }

    @Override
    public void onInterstitialDismissed() {
        if (mInterstitialAdListener != null) {
            mInterstitialAdListener.onAdClosed();
        }
    }

    @Override
    public void onVideoStarted() {
        if (mInterstitialAdListener != null) {
            mInterstitialAdListener.onAdStarted();
        }
    }

    @Override
    public void onVideoError(int progressPercentage) {
        if (mInterstitialAdListener != null) {
            mInterstitialAdListener.onAdShowFailed(AdapterErrors.ADAPTER_ERROR_INTERNAL, "Error attempting to show HyBid Interstitial Ad");
        }
    }

    @Override
    public void onVideoFinished() {
        if (mInterstitialAdListener != null) {
            mInterstitialAdListener.onAdEnded();
        }
    }

    @Override
    public void onVideoSkipped() {
    }

    @Override
    public void onVideoDismissed(int progressPercentage) {

    }
}
