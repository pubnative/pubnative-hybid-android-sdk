package net.pubnative.tarantula.adapters.mopub;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.mopub.mobileads.CustomEventInterstitial;
import com.mopub.mobileads.MoPubErrorCode;

import net.pubnative.tarantula.sdk.Tarantula;
import net.pubnative.tarantula.sdk.interstitial.presenter.InterstitialPresenter;
import net.pubnative.tarantula.sdk.interstitial.presenter.InterstitialPresenterFactory;
import net.pubnative.tarantula.sdk.models.Ad;
import net.pubnative.tarantula.sdk.utils.Logger;

import java.util.Map;

/**
 * Created by erosgarciaponte on 12.01.18.
 */

public class TarantulaMoPubInterstitialCustomEvent extends CustomEventInterstitial implements InterstitialPresenter.Listener {
    @NonNull
    private static final String TAG = TarantulaMoPubInterstitialCustomEvent.class.getSimpleName();

    @NonNull
    private static final String ZONE_ID_KEY = "zone_id";
    @Nullable
    private CustomEventInterstitialListener mInterstitialListener;
    @Nullable
    private InterstitialPresenter mInterstitialPresenter;

    @Override
    protected void loadInterstitial(Context context,
                                    CustomEventInterstitialListener customEventInterstitialListener,
                                    Map<String, Object> localExtras,
                                    Map<String, String> serverExtras) {

        if (customEventInterstitialListener == null) {
            Logger.e(TAG, "customEventInterstitialListener is null");
            return;
        }
        mInterstitialListener = customEventInterstitialListener;

        if (!(context instanceof Activity)) {
            Logger.e(TAG, "Tarantula interstitial ad can only be rendered with an Activity context");
            mInterstitialListener.onInterstitialFailed(MoPubErrorCode.ADAPTER_CONFIGURATION_ERROR);
            return;
        }
        final Activity activity = (Activity) context;

        String zoneIdKey;
        if (localExtras.containsKey(ZONE_ID_KEY)) {
            zoneIdKey = (String) localExtras.get(ZONE_ID_KEY);
        } else if (serverExtras.containsKey(ZONE_ID_KEY)) {
            zoneIdKey = serverExtras.get(ZONE_ID_KEY);
        } else {
            Logger.e(TAG, "Could not find zone id value in CustomEventInterstitial localExtras or serverExtras");
            mInterstitialListener.onInterstitialFailed(MoPubErrorCode.ADAPTER_CONFIGURATION_ERROR);
            return;
        }

        final Ad ad = Tarantula.getAdCache().remove(zoneIdKey);
        if (ad == null) {
            Logger.e(TAG, "Could not find an ad in the cache for zone id with key " + zoneIdKey);
            mInterstitialListener.onInterstitialFailed(MoPubErrorCode.ADAPTER_CONFIGURATION_ERROR);
            return;
        }

        mInterstitialPresenter = new InterstitialPresenterFactory(activity).createInterstitialPresenter(ad, this);
        if (mInterstitialPresenter == null) {
            Logger.e(TAG, "Could not create valid interstitial presenter");
            mInterstitialListener.onInterstitialFailed(MoPubErrorCode.ADAPTER_CONFIGURATION_ERROR);
            return;
        }

        mInterstitialPresenter.load();
    }

    @Override
    protected void showInterstitial() {
        if (mInterstitialPresenter != null) {
            mInterstitialPresenter.show();
        }
    }

    @Override
    protected void onInvalidate() {
        if (mInterstitialPresenter != null) {
            mInterstitialPresenter.destroy();
            mInterstitialPresenter = null;
        }
    }

    @Override
    public void onInterstitialLoaded(@NonNull InterstitialPresenter interstitialPresenter) {
        if (mInterstitialListener != null) {
            mInterstitialListener.onInterstitialLoaded();
        }
    }

    @Override
    public void onInterstitialShown(@NonNull InterstitialPresenter interstitialPresenter) {
        if (mInterstitialListener != null) {
            mInterstitialListener.onInterstitialShown();
        }
    }

    @Override
    public void onInterstitialClicked(@NonNull InterstitialPresenter interstitialPresenter) {
        if (mInterstitialListener != null) {
            mInterstitialListener.onInterstitialClicked();
        }
    }

    @Override
    public void onInterstitialDismissed(@NonNull InterstitialPresenter interstitialPresenter) {
        if (mInterstitialListener != null) {
            mInterstitialListener.onInterstitialDismissed();
        }
    }

    @Override
    public void onInterstitialError(@NonNull InterstitialPresenter interstitialPresenter) {
        if (mInterstitialListener != null) {
            mInterstitialListener.onInterstitialFailed(MoPubErrorCode.INTERNAL_ERROR);
        }
    }
}
