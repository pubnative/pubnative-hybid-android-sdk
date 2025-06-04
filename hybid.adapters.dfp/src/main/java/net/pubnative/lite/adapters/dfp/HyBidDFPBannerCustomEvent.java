// HyBid SDK License
//
// https://github.com/pubnative/pubnative-hybid-android-sdk/blob/main/LICENSE
//
package net.pubnative.lite.adapters.dfp;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.mediation.MediationAdRequest;
import com.google.android.gms.ads.mediation.customevent.CustomEventBanner;
import com.google.android.gms.ads.mediation.customevent.CustomEventBannerListener;

import net.pubnative.lite.sdk.HyBid;
import net.pubnative.lite.sdk.banner.presenter.BannerPresenterFactory;
import net.pubnative.lite.sdk.models.Ad;
import net.pubnative.lite.sdk.models.IntegrationType;
import net.pubnative.lite.sdk.presenter.AdPresenter;
import net.pubnative.lite.sdk.utils.Logger;

public class HyBidDFPBannerCustomEvent implements CustomEventBanner, AdPresenter.Listener {
    private static final String TAG = HyBidDFPBannerCustomEvent.class.getSimpleName();

    private CustomEventBannerListener mBannerListener;
    private AdPresenter mPresenter;

    @Override
    public void requestBannerAd(Context context,
                                CustomEventBannerListener listener,
                                String serverParameter,
                                AdSize size,
                                MediationAdRequest mediationAdRequest,
                                Bundle customEventExtras) {
        if (listener == null) {
            Logger.e(TAG, "customEventBannerListener is null");
            return;
        }
        mBannerListener = listener;

        String zoneIdKey;
        if (!TextUtils.isEmpty(HyBidDFPUtils.getZoneId(serverParameter))) {
            zoneIdKey = HyBidDFPUtils.getZoneId(serverParameter);
        } else if (!TextUtils.isEmpty(HyBidDFPUtils.getZoneId(customEventExtras))) {
            zoneIdKey = HyBidDFPUtils.getZoneId(customEventExtras);
        } else {
            Logger.e(TAG, "Could not find zone id value in CustomEventBanner serverParameter or customEventExtras" +
                    "Required params in CustomEventBanner serverParameter or customEventExtras must be provided as a valid JSON Object. " +
                    "Please consult HyBid documentation and update settings in your dfp publisher dashboard.");
            mBannerListener.onAdFailedToLoad(AdRequest.ERROR_CODE_INVALID_REQUEST);
            return;
        }

        final Ad ad = HyBid.getAdCache().remove(zoneIdKey);
        if (ad == null) {
            Logger.e(TAG, "Could not find an ad in the cache for zone id with key: " + zoneIdKey);
            mBannerListener.onAdFailedToLoad(AdRequest.ERROR_CODE_NETWORK_ERROR);
            return;
        }

        mPresenter = new BannerPresenterFactory(context, IntegrationType.MEDIATION).createPresenter(ad, getAdSize(size), this);
        if (mPresenter == null) {
            Logger.e(TAG, "Could not create valid banner presenter");
            mBannerListener.onAdFailedToLoad(AdRequest.ERROR_CODE_NETWORK_ERROR);
            return;
        }

        mPresenter.load();
    }

    public net.pubnative.lite.sdk.models.AdSize getAdSize(AdSize adSize) {

        return net.pubnative.lite.sdk.models.AdSize.SIZE_320x50;
    }

    @Override
    public void onDestroy() {
        if (mPresenter != null) {
            mPresenter.stopTracking();
            mPresenter.destroy();
            mPresenter = null;
        }
    }

    @Override
    public void onPause() {

    }

    @Override
    public void onResume() {
    }

    @Override
    public void onAdLoaded(AdPresenter adPresenter, View banner) {
        if (mBannerListener != null) {
            mBannerListener.onAdLoaded(banner);
            mPresenter.startTracking();
        }
    }

    @Override
    public void onAdError(AdPresenter adPresenter) {
        if (mBannerListener != null) {
            mBannerListener.onAdFailedToLoad(AdRequest.ERROR_CODE_INTERNAL_ERROR);
        }
    }

    @Override
    public void onAdClicked(AdPresenter adPresenter) {
        if (mBannerListener != null) {
            mBannerListener.onAdClicked();
            mBannerListener.onAdOpened();
            mBannerListener.onAdLeftApplication();
        }
    }
}
