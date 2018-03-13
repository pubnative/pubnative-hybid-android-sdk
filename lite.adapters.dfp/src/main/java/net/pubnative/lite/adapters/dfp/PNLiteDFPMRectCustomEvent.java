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
import net.pubnative.lite.sdk.mrect.presenter.MRectPresenter;
import net.pubnative.lite.sdk.mrect.presenter.MRectPresenterFactory;
import net.pubnative.lite.sdk.utils.Logger;

/**
 * Created by erosgarciaponte on 13.03.18.
 */

public class PNLiteDFPMRectCustomEvent implements CustomEventBanner, MRectPresenter.Listener {
    private static final String TAG = PNLiteDFPMRectCustomEvent.class.getSimpleName();

    private static final String ZONE_ID_KEY = "pn_zone_id";
    private CustomEventBannerListener mMRectListener;
    private MRectPresenter mPresenter;

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
        mMRectListener = listener;

        String zoneIdKey;
        if (!TextUtils.isEmpty(PNLiteDFPUtils.getZoneId(serverParameter))) {
            zoneIdKey = PNLiteDFPUtils.getZoneId(serverParameter);
        } else if (!TextUtils.isEmpty(PNLiteDFPUtils.getZoneId(customEventExtras))) {
            zoneIdKey = PNLiteDFPUtils.getZoneId(customEventExtras);
        } else {
            Logger.e(TAG, "Could not find zone id value in CustomEventBanner serverParameter or customEventExtras");
            mMRectListener.onAdFailedToLoad(AdRequest.ERROR_CODE_INVALID_REQUEST);
            return;
        }

        final Ad ad = PNLite.getAdCache().remove(zoneIdKey);
        if (ad == null) {
            Logger.e(TAG, "Could not find an ad in the cache for zone id with key: " + zoneIdKey);
            mMRectListener.onAdFailedToLoad(AdRequest.ERROR_CODE_NETWORK_ERROR);
            return;
        }

        mPresenter = new MRectPresenterFactory(context).createMRectPresenter(ad, this);
        if (mPresenter == null) {
            Logger.e(TAG, "Could not create valid mrect presenter");
            mMRectListener.onAdFailedToLoad(AdRequest.ERROR_CODE_NETWORK_ERROR);
            return;
        }

        mPresenter.load();
    }

    @Override
    public void onDestroy() {
        if (mPresenter != null) {
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
    public void onMRectLoaded(MRectPresenter mRectPresenter, View mRect) {
        if (mMRectListener != null) {
            mMRectListener.onAdLoaded(mRect);
        }
    }

    @Override
    public void onMRectError(MRectPresenter mRectPresenter) {
        if (mMRectListener != null) {
            mMRectListener.onAdFailedToLoad(AdRequest.ERROR_CODE_INTERNAL_ERROR);
        }
    }

    @Override
    public void onMRectClicked(MRectPresenter mRectPresenter) {
        if (mMRectListener != null) {
            mMRectListener.onAdClicked();
            mMRectListener.onAdLeftApplication();
        }
    }
}
