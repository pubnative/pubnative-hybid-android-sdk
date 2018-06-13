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

import net.pubnative.lite.sdk.PNLite;
import net.pubnative.lite.sdk.banner.presenter.BannerPresenter;
import net.pubnative.lite.sdk.banner.presenter.BannerPresenterFactory;
import net.pubnative.lite.sdk.models.Ad;
import net.pubnative.lite.sdk.utils.Logger;

/**
 * Created by erosgarciaponte on 13.03.18.
 */

public class PNLiteDFPBannerCustomEvent implements CustomEventBanner, BannerPresenter.Listener {
    private static final String TAG = PNLiteDFPBannerCustomEvent.class.getSimpleName();

    private static final String ZONE_ID_KEY = "pn_zone_id";
    private CustomEventBannerListener mBannerListener;
    private BannerPresenter mPresenter;

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
        if (!TextUtils.isEmpty(PNLiteDFPUtils.getZoneId(serverParameter))) {
            zoneIdKey = PNLiteDFPUtils.getZoneId(serverParameter);
        } else if (!TextUtils.isEmpty(PNLiteDFPUtils.getZoneId(customEventExtras))) {
            zoneIdKey = PNLiteDFPUtils.getZoneId(customEventExtras);
        } else {
            Logger.e(TAG, "Could not find zone id value in CustomEventBanner serverParameter or customEventExtras");
            mBannerListener.onAdFailedToLoad(AdRequest.ERROR_CODE_INVALID_REQUEST);
            return;
        }

        final Ad ad = PNLite.getAdCache().remove(zoneIdKey);
        if (ad == null) {
            Logger.e(TAG, "Could not find an ad in the cache for zone id with key: " + zoneIdKey);
            mBannerListener.onAdFailedToLoad(AdRequest.ERROR_CODE_NETWORK_ERROR);
            return;
        }

        mPresenter = new BannerPresenterFactory(context).createBannerPresenter(ad, this);
        if (mPresenter == null) {
            Logger.e(TAG, "Could not create valid banner presenter");
            mBannerListener.onAdFailedToLoad(AdRequest.ERROR_CODE_NETWORK_ERROR);
            return;
        }

        mPresenter.load();
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
    public void onBannerLoaded(BannerPresenter bannerPresenter, View banner) {
        if (mBannerListener != null) {
            mBannerListener.onAdLoaded(banner);
            mPresenter.startTracking();
        }
    }

    @Override
    public void onBannerError(BannerPresenter bannerPresenter) {
        if (mBannerListener != null) {
            mBannerListener.onAdFailedToLoad(AdRequest.ERROR_CODE_INTERNAL_ERROR);
        }
    }

    @Override
    public void onBannerClicked(BannerPresenter bannerPresenter) {
        if (mBannerListener != null) {
            mBannerListener.onAdClicked();
            mBannerListener.onAdLeftApplication();
        }
    }
}
