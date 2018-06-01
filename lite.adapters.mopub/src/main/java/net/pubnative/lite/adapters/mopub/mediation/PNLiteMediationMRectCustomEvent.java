package net.pubnative.lite.adapters.mopub.mediation;

import android.content.Context;
import android.view.View;

import com.mopub.mobileads.CustomEventBanner;
import com.mopub.mobileads.MoPubErrorCode;

import net.pubnative.lite.sdk.PNLite;
import net.pubnative.lite.sdk.api.BannerRequestManager;
import net.pubnative.lite.sdk.api.RequestManager;
import net.pubnative.lite.sdk.models.Ad;
import net.pubnative.lite.sdk.mrect.presenter.MRectPresenter;
import net.pubnative.lite.sdk.mrect.presenter.MRectPresenterFactory;
import net.pubnative.lite.sdk.utils.Logger;

import java.util.Map;

public class PNLiteMediationMRectCustomEvent extends CustomEventBanner implements RequestManager.RequestListener, MRectPresenter.Listener {
    private static final String TAG = PNLiteMediationMRectCustomEvent.class.getSimpleName();

    private static final String APP_TOKEN_KEY = "pn_app_token";
    private static final String ZONE_ID_KEY = "pn_zone_id";
    private CustomEventBannerListener mBannerListener;
    private MRectPresenter mMRectPresenter;
    private Context mContext;

    @Override
    protected void loadBanner(Context context,
                              CustomEventBannerListener customEventBannerListener,
                              Map<String, Object> localExtras,
                              Map<String, String> serverExtras) {
        if (customEventBannerListener == null) {
            Logger.e(TAG, "customEventBannerListener is null");
            return;
        }

        mContext = context.getApplicationContext();

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

        RequestManager requestManager = new BannerRequestManager();
        requestManager.setZoneId(zoneId);
        requestManager.setRequestListener(this);
        requestManager.requestAd();
    }

    @Override
    protected void onInvalidate() {
        if (mMRectPresenter != null) {
            mMRectPresenter.destroy();
            mMRectPresenter = null;
        }
    }

    //------------------------------ RequestManager Callbacks --------------------------------------
    @Override
    public void onRequestSuccess(Ad ad) {
        if (mContext == null) {
            Logger.e(TAG, "Invalid context. Dropping call.");
            mBannerListener.onBannerFailed(MoPubErrorCode.ADAPTER_CONFIGURATION_ERROR);
        } else {
            mMRectPresenter = new MRectPresenterFactory(mContext).createMRectPresenter(ad, this);
            if (mMRectPresenter == null) {
                Logger.e(TAG, "Could not create valid mrect presenter");
                mBannerListener.onBannerFailed(MoPubErrorCode.ADAPTER_CONFIGURATION_ERROR);
                return;
            }

            mMRectPresenter.load();
        }
    }

    @Override
    public void onRequestFail(Throwable throwable) {
        Logger.e(TAG, throwable.getMessage());
        mBannerListener.onBannerFailed(MoPubErrorCode.NETWORK_NO_FILL);
    }

    //----------------------------- MRectPresenter Callbacks ---------------------------------------
    @Override
    public void onMRectLoaded(MRectPresenter mRectPresenter, View mRect) {
        if (mBannerListener != null) {
            mBannerListener.onBannerLoaded(mRect);
        }
    }

    @Override
    public void onMRectError(MRectPresenter mRectPresenter) {
        if (mBannerListener != null) {
            mBannerListener.onBannerFailed(MoPubErrorCode.INTERNAL_ERROR);
        }
    }

    @Override
    public void onMRectClicked(MRectPresenter mRectPresenter) {
        if (mBannerListener != null) {
            mBannerListener.onBannerClicked();
        }
    }
}
