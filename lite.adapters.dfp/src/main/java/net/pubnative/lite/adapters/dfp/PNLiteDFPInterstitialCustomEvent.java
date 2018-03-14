package net.pubnative.lite.adapters.dfp;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.mediation.MediationAdRequest;
import com.google.android.gms.ads.mediation.customevent.CustomEventInterstitial;
import com.google.android.gms.ads.mediation.customevent.CustomEventInterstitialListener;

import net.pubnative.lite.sdk.PNLite;
import net.pubnative.lite.sdk.interstitial.presenter.InterstitialPresenter;
import net.pubnative.lite.sdk.interstitial.presenter.InterstitialPresenterFactory;
import net.pubnative.lite.sdk.models.Ad;
import net.pubnative.lite.sdk.utils.Logger;

/**
 * Created by erosgarciaponte on 14.03.18.
 */

public class PNLiteDFPInterstitialCustomEvent implements CustomEventInterstitial, InterstitialPresenter.Listener {
    private static final String TAG = PNLiteDFPInterstitialCustomEvent.class.getSimpleName();

    private static final String ZONE_ID_KEY = "pn_zone_id";
    private CustomEventInterstitialListener mInterstitialListener;
    private InterstitialPresenter mPresenter;

    @Override
    public void requestInterstitialAd(Context context,
                                      CustomEventInterstitialListener listener,
                                      String serverParameter,
                                      MediationAdRequest mediationAdRequest,
                                      Bundle customEventExtras) {
        if (listener == null) {
            Logger.e(TAG, "customEventInterstitialListener is null");
            return;
        }
        mInterstitialListener = listener;

        if (!(context instanceof Activity)) {
            Logger.e(TAG, "PNLite interstitial ad can only be rendered with an Activity context");
            mInterstitialListener.onAdFailedToLoad(AdRequest.ERROR_CODE_INTERNAL_ERROR);
            return;
        }
        final Activity activity = (Activity) context;

        String zoneIdKey;
        if (!TextUtils.isEmpty(PNLiteDFPUtils.getZoneId(serverParameter))) {
            zoneIdKey = PNLiteDFPUtils.getZoneId(serverParameter);
        } else if (!TextUtils.isEmpty(PNLiteDFPUtils.getZoneId(customEventExtras))) {
            zoneIdKey = PNLiteDFPUtils.getZoneId(customEventExtras);
        } else {
            Logger.e(TAG, "Could not find zone id value in CustomEventInterstitial localExtras or serverExtras");
            mInterstitialListener.onAdFailedToLoad(AdRequest.ERROR_CODE_INVALID_REQUEST);
            return;
        }

        final Ad ad = PNLite.getAdCache().remove(zoneIdKey);
        if (ad == null) {
            Logger.e(TAG, "Could not find an ad in the cache for zone id with key " + zoneIdKey);
            mInterstitialListener.onAdFailedToLoad(AdRequest.ERROR_CODE_NETWORK_ERROR);
            return;
        }

        mPresenter = new InterstitialPresenterFactory(activity).createInterstitialPresenter(ad, this);
        if (mPresenter == null) {
            Logger.e(TAG, "Could not create valid interstitial presenter");
            mInterstitialListener.onAdFailedToLoad(AdRequest.ERROR_CODE_NETWORK_ERROR);
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
    public void showInterstitial() {
        if (mPresenter != null) {
            mPresenter.show();
        }
    }

    @Override
    public void onInterstitialLoaded(InterstitialPresenter interstitialPresenter) {
        if (mInterstitialListener != null) {
            mInterstitialListener.onAdLoaded();
        }
    }

    @Override
    public void onInterstitialError(InterstitialPresenter interstitialPresenter) {
        if (mInterstitialListener != null) {
            mInterstitialListener.onAdFailedToLoad(AdRequest.ERROR_CODE_INTERNAL_ERROR);
        }
    }

    @Override
    public void onInterstitialShown(InterstitialPresenter interstitialPresenter) {
        if (mInterstitialListener != null) {
            mInterstitialListener.onAdOpened();
        }
    }

    @Override
    public void onInterstitialClicked(InterstitialPresenter interstitialPresenter) {
        if (mInterstitialListener != null) {
            mInterstitialListener.onAdClicked();
            mInterstitialListener.onAdLeftApplication();
        }
    }

    @Override
    public void onInterstitialDismissed(InterstitialPresenter interstitialPresenter) {
        if (mInterstitialListener != null) {
            mInterstitialListener.onAdClosed();
        }
    }
}
