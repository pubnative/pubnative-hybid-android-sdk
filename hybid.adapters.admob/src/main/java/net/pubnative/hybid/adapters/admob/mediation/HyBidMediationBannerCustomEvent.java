package net.pubnative.hybid.adapters.admob.mediation;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.mediation.MediationAdRequest;
import com.google.android.gms.ads.mediation.customevent.CustomEventBanner;
import com.google.android.gms.ads.mediation.customevent.CustomEventBannerListener;

import net.pubnative.hybid.adapters.admob.HyBidAdmobUtils;
import net.pubnative.lite.sdk.HyBid;
import net.pubnative.lite.sdk.utils.Logger;
import net.pubnative.lite.sdk.views.HyBidAdView;

public class HyBidMediationBannerCustomEvent implements CustomEventBanner, HyBidAdView.Listener {
    private static final String TAG = HyBidMediationBannerCustomEvent.class.getSimpleName();

    private CustomEventBannerListener mBannerListener;
    private HyBidAdView mBannerView;

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
            Logger.e(TAG, "Could not find the required params in CustomEventBanner serverExtras");
            mBannerListener.onAdFailedToLoad(AdRequest.ERROR_CODE_NETWORK_ERROR);
            return;
        }

        net.pubnative.lite.sdk.models.AdSize hyBidAdSize = getAdSize();

        if (adSize.getWidth() < hyBidAdSize.getWidth() || adSize.getHeight() < hyBidAdSize.getHeight()) {
            Logger.e(TAG, "The requested ad size is smaller than " + hyBidAdSize.toString());
            mBannerListener.onAdFailedToLoad(AdRequest.ERROR_CODE_INVALID_REQUEST);
            return;
        }

        if (appToken == null || !appToken.equals(HyBid.getAppToken())) {
            Logger.e(TAG, "The provided app token doesn't match the one used to initialise HyBid");
            mBannerListener.onAdFailedToLoad(AdRequest.ERROR_CODE_NETWORK_ERROR);
            return;
        }

        mBannerView = new HyBidAdView(context);
        mBannerView.setAdSize(getAdSize());
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

    protected net.pubnative.lite.sdk.models.AdSize getAdSize() {
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
            mBannerListener.onAdFailedToLoad(AdRequest.ERROR_CODE_NO_FILL);
        }
    }

    @Override
    public void onAdImpression() {

    }

    @Override
    public void onAdClick() {
        if (mBannerListener != null) {
            mBannerListener.onAdClicked();
            mBannerListener.onAdLeftApplication();
        }
    }
}
