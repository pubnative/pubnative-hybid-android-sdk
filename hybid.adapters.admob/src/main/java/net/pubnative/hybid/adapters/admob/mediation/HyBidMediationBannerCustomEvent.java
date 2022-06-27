package net.pubnative.hybid.adapters.admob.mediation;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;

import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.mediation.MediationAdRequest;
import com.google.android.gms.ads.mediation.customevent.CustomEventBanner;
import com.google.android.gms.ads.mediation.customevent.CustomEventBannerListener;

import net.pubnative.hybid.adapters.admob.HyBidAdmobUtils;
import net.pubnative.lite.sdk.HyBid;
import net.pubnative.lite.sdk.models.ImpressionTrackingMethod;
import net.pubnative.lite.sdk.utils.Logger;
import net.pubnative.lite.sdk.views.HyBidAdView;

public class HyBidMediationBannerCustomEvent implements CustomEventBanner, HyBidAdView.Listener {
    private static final String TAG = HyBidMediationBannerCustomEvent.class.getSimpleName();

    protected CustomEventBannerListener mBannerListener;
    protected HyBidAdView mBannerView;

    @Override
    public void requestBannerAd(Context context,
                                CustomEventBannerListener customEventBannerListener,
                                String serverParameter,
                                AdSize adSize,
                                MediationAdRequest mediationAdRequest,
                                Bundle bundle) {
        if (customEventBannerListener == null) {
            Logger.e(TAG, "customEventBannerListener is null");
            return;
        }

        mBannerListener = customEventBannerListener;

        String zoneId;
        String appToken;
        if (!TextUtils.isEmpty(HyBidAdmobUtils.getAppToken(serverParameter))
                && !TextUtils.isEmpty(HyBidAdmobUtils.getZoneId(serverParameter))) {
            zoneId = HyBidAdmobUtils.getZoneId(serverParameter);
            appToken = HyBidAdmobUtils.getAppToken(serverParameter);
        } else {
            Logger.e(TAG, "Could not find the required params in CustomEventBanner serverExtras. " +
                    "Required params in CustomEventBanner serverExtras must be provided as a valid JSON Object. " +
                    "Please consult HyBid documentation and update settings in your AdMob publisher dashboard.");
            mBannerListener.onAdFailedToLoad(new AdError(AdRequest.ERROR_CODE_NETWORK_ERROR,
                    "Could not find the required params in CustomEventBanner serverExtras",
                    AdError.UNDEFINED_DOMAIN
            ));
            return;
        }

        net.pubnative.lite.sdk.models.AdSize hyBidAdSize = getAdSize(adSize);

        if (adSize.getWidth() < hyBidAdSize.getWidth() || adSize.getHeight() < hyBidAdSize.getHeight()) {
            Logger.e(TAG, "The requested ad size is smaller than " + hyBidAdSize.toString());
            mBannerListener.onAdFailedToLoad(new AdError(AdRequest.ERROR_CODE_INVALID_REQUEST,
                    "The requested ad size is smaller than " + hyBidAdSize.toString(),
                    AdError.UNDEFINED_DOMAIN
            ));
            return;
        }

        if (HyBid.isInitialized()) {
            if (TextUtils.isEmpty(appToken) || !appToken.equals(HyBid.getAppToken())) {
                Logger.e(TAG, "The provided app token doesn't match the one used to initialise HyBid");
                mBannerListener.onAdFailedToLoad(new AdError(AdRequest.ERROR_CODE_NETWORK_ERROR,
                        "The provided app token doesn't match the one used to initialise HyBid",
                        AdError.UNDEFINED_DOMAIN
                ));
            } else {
                loadBanner(context, hyBidAdSize, zoneId);
            }
        } else {
            HyBid.initialize(appToken, (Application) context.getApplicationContext(), b ->
                    loadBanner(context, hyBidAdSize, zoneId));
        }
    }

    private void loadBanner(Context context, net.pubnative.lite.sdk.models.AdSize adSize, String zoneId) {
        mBannerView = new HyBidAdView(context);
        mBannerView.setAdSize(adSize);
        mBannerView.setMediation(true);
        mBannerView.load(zoneId, this);
    }

    @Override
    public void onResume() {

    }

    @Override
    public void onPause() {

    }

    @Override
    public void onDestroy() {
        if (mBannerView != null) {
            mBannerView.destroy();
            mBannerView = null;
        }
    }

    protected net.pubnative.lite.sdk.models.AdSize getAdSize(AdSize adSize) {
        return net.pubnative.lite.sdk.models.AdSize.SIZE_320x50;
    }

    //------------------------------------ PNAdView Callbacks --------------------------------------
    @Override
    public void onAdLoaded() {
        if (mBannerListener != null) {
            mBannerListener.onAdLoaded(mBannerView);
        }
    }

    @Override
    public void onAdLoadFailed(Throwable error) {
        if (mBannerListener != null) {
            mBannerListener.onAdFailedToLoad(new AdError(AdRequest.ERROR_CODE_NO_FILL,
                    "No fill.",
                    AdError.UNDEFINED_DOMAIN
            ));
        }
    }

    @Override
    public void onAdImpression() {

    }

    @Override
    public void onAdClick() {
        if (mBannerListener != null) {
            mBannerListener.onAdClicked();
            mBannerListener.onAdOpened();
            mBannerListener.onAdLeftApplication();
        }
    }
}
