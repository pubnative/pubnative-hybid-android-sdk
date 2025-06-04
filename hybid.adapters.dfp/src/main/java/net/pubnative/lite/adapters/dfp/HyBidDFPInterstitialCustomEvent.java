// HyBid SDK License
//
// https://github.com/pubnative/pubnative-hybid-android-sdk/blob/main/LICENSE
//
package net.pubnative.lite.adapters.dfp;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.mediation.MediationAdRequest;
import com.google.android.gms.ads.mediation.customevent.CustomEventInterstitial;
import com.google.android.gms.ads.mediation.customevent.CustomEventInterstitialListener;

import net.pubnative.lite.sdk.HyBid;
import net.pubnative.lite.sdk.interstitial.presenter.InterstitialPresenter;
import net.pubnative.lite.sdk.interstitial.presenter.InterstitialPresenterFactory;
import net.pubnative.lite.sdk.models.Ad;
import net.pubnative.lite.sdk.models.IntegrationType;
import net.pubnative.lite.sdk.models.SkipOffset;
import net.pubnative.lite.sdk.utils.Logger;
import net.pubnative.lite.sdk.utils.SkipOffsetManager;

public class HyBidDFPInterstitialCustomEvent implements CustomEventInterstitial, InterstitialPresenter.Listener {
    private static final String TAG = HyBidDFPInterstitialCustomEvent.class.getSimpleName();

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

        String zoneIdKey;
        if (!TextUtils.isEmpty(HyBidDFPUtils.getZoneId(serverParameter))) {
            zoneIdKey = HyBidDFPUtils.getZoneId(serverParameter);
        } else if (!TextUtils.isEmpty(HyBidDFPUtils.getZoneId(customEventExtras))) {
            zoneIdKey = HyBidDFPUtils.getZoneId(customEventExtras);
        } else {
            Logger.e(TAG, "Could not find zone id value in CustomEventInterstitial localExtras or serverExtras" +
                    "Required params in CustomEventInterstitial localExtras or serverExtras must be provided as a valid JSON Object. " +
                    "Please consult HyBid documentation and update settings in your dfp publisher dashboard.");
            mInterstitialListener.onAdFailedToLoad(AdRequest.ERROR_CODE_INVALID_REQUEST);
            return;
        }

        final Ad ad = HyBid.getAdCache().remove(zoneIdKey);
        if (ad == null) {
            Logger.e(TAG, "Could not find an ad in the cache for zone id with key " + zoneIdKey);
            mInterstitialListener.onAdFailedToLoad(AdRequest.ERROR_CODE_NETWORK_ERROR);
            return;
        }


        Integer htmlSkipOffsetInt = SkipOffsetManager.getHTMLSkipOffset(ad.getHtmlSkipOffset(), true);
        SkipOffset htmlSkipOffset;
        Integer videoSkipOffsetInt = SkipOffsetManager.getVideoSkipOffset(ad.getVideoSkipOffset(), null, null, false, true);
        SkipOffset videoSkipOffset;

        htmlSkipOffset = new SkipOffset(htmlSkipOffsetInt, SkipOffsetManager.isCustomInterstitialHTMLSkipOffset());
        videoSkipOffset = new SkipOffset(videoSkipOffsetInt, SkipOffsetManager.isCustomInterstitialVideoSkipOffset());

        if (htmlSkipOffset.getOffset() > 0 || videoSkipOffset.getOffset() > 0) {
            mPresenter = new InterstitialPresenterFactory(context, zoneIdKey)
                    .createInterstitialPresenter(ad, htmlSkipOffset,
                            videoSkipOffset, this, IntegrationType.MEDIATION);
        } else {
            mPresenter = new InterstitialPresenterFactory(context, zoneIdKey)
                    .createInterstitialPresenter(ad, this, IntegrationType.MEDIATION);
        }

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
            mInterstitialListener.onAdOpened();
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
