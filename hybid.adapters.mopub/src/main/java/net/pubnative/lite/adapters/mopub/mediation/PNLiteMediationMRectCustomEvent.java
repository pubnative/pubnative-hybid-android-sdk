package net.pubnative.lite.adapters.mopub.mediation;

import android.content.Context;

import com.mopub.mobileads.CustomEventBanner;
import com.mopub.mobileads.MoPubErrorCode;

import net.pubnative.lite.sdk.PNLite;
import net.pubnative.lite.sdk.utils.Logger;
import net.pubnative.lite.sdk.views.PNAdView;
import net.pubnative.lite.sdk.views.PNMRectAdView;

import java.util.Map;

public class PNLiteMediationMRectCustomEvent extends CustomEventBanner implements PNAdView.Listener {
    private static final String TAG = PNLiteMediationMRectCustomEvent.class.getSimpleName();

    private static final String APP_TOKEN_KEY = "pn_app_token";
    private static final String ZONE_ID_KEY = "pn_zone_id";
    private CustomEventBannerListener mBannerListener;
    private PNMRectAdView mMRectView;

    @Override
    protected void loadBanner(Context context,
                              CustomEventBannerListener customEventBannerListener,
                              Map<String, Object> localExtras,
                              Map<String, String> serverExtras) {
        if (customEventBannerListener == null) {
            Logger.e(TAG, "customEventBannerListener is null");
            return;
        }

        mBannerListener = customEventBannerListener;

        String zoneId;
        String appToken;
        if (serverExtras.containsKey(ZONE_ID_KEY) && serverExtras.containsKey(APP_TOKEN_KEY)) {
            zoneId = serverExtras.get(ZONE_ID_KEY);
            appToken = serverExtras.get(APP_TOKEN_KEY);
        } else {
            Logger.e(TAG, "Could not find the required params in CustomEventBanner serverExtras");
            mBannerListener.onBannerFailed(MoPubErrorCode.ADAPTER_CONFIGURATION_ERROR);
            return;
        }

        if (appToken == null || !appToken.equals(PNLite.getAppToken())) {
            Logger.e(TAG, "The provided app token doesn't match the one used to initialise PNLite");
            mBannerListener.onBannerFailed(MoPubErrorCode.ADAPTER_CONFIGURATION_ERROR);
            return;
        }

        mMRectView = new PNMRectAdView(context);
        mMRectView.load(zoneId, this);
    }

    @Override
    protected void onInvalidate() {
        if (mMRectView != null) {
            mMRectView.destroy();
            mMRectView = null;
        }
    }

    //------------------------------------ PNAdView Callbacks --------------------------------------
    @Override
    public void onAdLoaded() {
        if (mBannerListener != null) {
            mBannerListener.onBannerLoaded(mMRectView);
        }
    }

    @Override
    public void onAdLoadFailed(Throwable error) {
        if (mBannerListener != null) {
            mBannerListener.onBannerFailed(MoPubErrorCode.NETWORK_NO_FILL);
        }
    }

    @Override
    public void onAdImpression() {

    }

    @Override
    public void onAdClick() {
        if (mBannerListener != null) {
            mBannerListener.onBannerClicked();
        }
    }
}
