package net.pubnative.lite.adapters.mopub.mediation;

import android.app.Activity;
import android.content.Context;

import com.mopub.mobileads.CustomEventInterstitial;
import com.mopub.mobileads.MoPubErrorCode;

import net.pubnative.lite.sdk.PNLite;
import net.pubnative.lite.sdk.api.InterstitialRequestManager;
import net.pubnative.lite.sdk.api.RequestManager;
import net.pubnative.lite.sdk.interstitial.presenter.InterstitialPresenter;
import net.pubnative.lite.sdk.interstitial.presenter.InterstitialPresenterFactory;
import net.pubnative.lite.sdk.models.Ad;
import net.pubnative.lite.sdk.utils.Logger;

import java.util.Map;

public class PNLiteMediationInterstitialCustomEvent extends CustomEventInterstitial implements RequestManager.RequestListener, InterstitialPresenter.Listener {
    private static final String TAG = PNLiteMediationMRectCustomEvent.class.getSimpleName();

    private static final String APP_TOKEN_KEY = "pn_app_token";
    private static final String ZONE_ID_KEY = "pn_zone_id";
    private CustomEventInterstitialListener mInterstitialListener;
    private InterstitialPresenter mInterstitialPresenter;
    private Activity mActivity;

    @Override
    protected void loadInterstitial(Context context, CustomEventInterstitialListener customEventInterstitialListener, Map<String, Object> localExtras, Map<String, String> serverExtras) {
        if (customEventInterstitialListener == null) {
            Logger.e(TAG, "customEventInterstitialListener is null");
            return;
        }
        mInterstitialListener = customEventInterstitialListener;

        if (!(context instanceof Activity)) {
            Logger.e(TAG, "PNLite interstitial ad can only be rendered with an Activity context");
            mInterstitialListener.onInterstitialFailed(MoPubErrorCode.ADAPTER_CONFIGURATION_ERROR);
            return;
        }

        mActivity = (Activity) context;

        String zoneId;
        String appToken;
        if (serverExtras.containsKey(ZONE_ID_KEY) && serverExtras.containsKey(APP_TOKEN_KEY)) {
            zoneId = serverExtras.get(ZONE_ID_KEY);
            appToken = serverExtras.get(APP_TOKEN_KEY);
        } else {
            Logger.e(TAG, "Could not find the required params in CustomEventInterstitial serverExtras");
            mInterstitialListener.onInterstitialFailed(MoPubErrorCode.ADAPTER_CONFIGURATION_ERROR);
            return;
        }

        if (appToken == null || !appToken.equals(PNLite.getAppToken())) {
            Logger.e(TAG, "The provided app token doesn't match the one used to initialise PNLite");
            mInterstitialListener.onInterstitialFailed(MoPubErrorCode.ADAPTER_CONFIGURATION_ERROR);
            return;
        }

        RequestManager requestManager = new InterstitialRequestManager();
        requestManager.setZoneId(zoneId);
        requestManager.setRequestListener(this);
        requestManager.requestAd();
    }

    @Override
    protected void onInvalidate() {
        if (mInterstitialPresenter != null) {
            mInterstitialPresenter.destroy();
            mInterstitialPresenter = null;
        }
    }

    @Override
    protected void showInterstitial() {
        if (mInterstitialPresenter != null) {
            mInterstitialPresenter.show();
        }
    }

    //------------------------------- RequestManager Callbacks -------------------------------------

    @Override
    public void onRequestSuccess(Ad ad) {
        if (mActivity == null) {
            Logger.e(TAG, "Invalid activity. Dropping call.");
            mInterstitialListener.onInterstitialFailed(MoPubErrorCode.ADAPTER_CONFIGURATION_ERROR);
        } else {
            mInterstitialPresenter = new InterstitialPresenterFactory(mActivity).createInterstitialPresenter(ad, this);
            if (mInterstitialPresenter == null) {
                Logger.e(TAG, "Could not create valid interstitial presenter");
                mInterstitialListener.onInterstitialFailed(MoPubErrorCode.ADAPTER_CONFIGURATION_ERROR);
                return;
            }

            mInterstitialPresenter.load();
        }
    }

    @Override
    public void onRequestFail(Throwable throwable) {
        Logger.e(TAG, throwable.getMessage());
        mInterstitialListener.onInterstitialFailed(MoPubErrorCode.NETWORK_NO_FILL);
    }

    //----------------------------- InterstitialPresenter Callbacks --------------------------------

    @Override
    public void onInterstitialLoaded(InterstitialPresenter interstitialPresenter) {
        if (mInterstitialListener != null) {
            mInterstitialListener.onInterstitialLoaded();
        }
    }

    @Override
    public void onInterstitialError(InterstitialPresenter interstitialPresenter) {
        if (mInterstitialListener != null) {
            mInterstitialListener.onInterstitialFailed(MoPubErrorCode.INTERNAL_ERROR);
        }
    }

    @Override
    public void onInterstitialShown(InterstitialPresenter interstitialPresenter) {
        if (mInterstitialListener != null) {
            mInterstitialListener.onInterstitialShown();
        }
    }

    @Override
    public void onInterstitialClicked(InterstitialPresenter interstitialPresenter) {
        if (mInterstitialListener != null) {
            mInterstitialListener.onInterstitialClicked();
        }
    }

    @Override
    public void onInterstitialDismissed(InterstitialPresenter interstitialPresenter) {
        if (mInterstitialListener != null) {
            mInterstitialListener.onInterstitialDismissed();
        }
    }
}
