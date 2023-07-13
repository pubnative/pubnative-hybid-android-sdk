package com.ironsource.adapters.custom.verve;

import android.app.Activity;
import android.text.TextUtils;
import android.view.Gravity;
import android.widget.FrameLayout;

import com.ironsource.mediationsdk.ISBannerSize;
import com.ironsource.mediationsdk.adunit.adapter.BaseBanner;
import com.ironsource.mediationsdk.adunit.adapter.listener.BannerAdListener;
import com.ironsource.mediationsdk.adunit.adapter.utility.AdData;
import com.ironsource.mediationsdk.adunit.adapter.utility.AdapterErrorType;
import com.ironsource.mediationsdk.adunit.adapter.utility.AdapterErrors;
import com.ironsource.mediationsdk.model.NetworkSettings;

import net.pubnative.lite.sdk.HyBid;
import net.pubnative.lite.sdk.models.AdSize;
import net.pubnative.lite.sdk.utils.Logger;
import net.pubnative.lite.sdk.views.HyBidAdView;

public class VerveCustomBanner extends BaseBanner<VerveCustomAdapter> implements HyBidAdView.Listener {
    private static final String TAG = VerveCustomBanner.class.getSimpleName();

    private BannerAdListener mBannerAdListener;
    private HyBidAdView mAdView;

    public VerveCustomBanner(NetworkSettings networkSettings) {
        super(networkSettings);
    }

    @Override
    public void loadAd(AdData adData, Activity activity, ISBannerSize isBannerSize, BannerAdListener listener) {
        if (listener == null) {
            Logger.e(TAG, "VerveCustomBanner listener is null");
            return;
        }

        if (activity == null) {
            String errorMessage = "VerveCustomBanner activity is null";
            Logger.e(TAG, errorMessage);
            listener.onAdLoadFailed(AdapterErrorType.ADAPTER_ERROR_TYPE_INTERNAL,
                    AdapterErrors.ADAPTER_ERROR_INTERNAL, errorMessage);
            return;
        }

        String appToken;
        String zoneID;
        if (adData != null
                && !TextUtils.isEmpty(adData.getString(VerveCustomAdapter.KEY_APP_TOKEN))
                && !TextUtils.isEmpty(adData.getString(VerveCustomAdapter.KEY_ZONE_ID))) {
            zoneID = adData.getString(VerveCustomAdapter.KEY_ZONE_ID);
            appToken = adData.getString(VerveCustomAdapter.KEY_APP_TOKEN);
        } else {
            String errorMessage = "Could not find the required params in VerveCustomInterstitial ad data. " +
                    "Required params in VerveCustomInterstitial ad data must be provided as a valid JSON Object. " +
                    "Please consult HyBid documentation and update settings in your IronSource publisher dashboard.";
            Logger.e(TAG, errorMessage);

            listener.onAdLoadFailed(AdapterErrorType.ADAPTER_ERROR_TYPE_INTERNAL,
                    AdapterErrors.ADAPTER_ERROR_MISSING_PARAMS, errorMessage);
            return;
        }

        if (appToken == null || !appToken.equals(HyBid.getAppToken())) {
            String errorMessage = "The provided app token doesn't match the one used to initialise HyBid";
            Logger.e(TAG, errorMessage);
            listener.onAdLoadFailed(AdapterErrorType.ADAPTER_ERROR_TYPE_INTERNAL,
                    AdapterErrors.ADAPTER_ERROR_MISSING_PARAMS, errorMessage);
            return;
        }

        AdSize hyBidAdSize = getAdSize(isBannerSize);

        mBannerAdListener = listener;
        mAdView = new HyBidAdView(activity, hyBidAdSize);
        mAdView.setMediation(true);
        mAdView.setMediationVendor(VerveCustomAdapter.IRONSOURCE_MEDIATION_VENDOR);
        mAdView.load(zoneID, this);
    }

    protected AdSize getAdSize(ISBannerSize isBannerSize) {
        if (isBannerSize == ISBannerSize.LARGE) {
            return AdSize.SIZE_320x100;
        } else if (isBannerSize == ISBannerSize.RECTANGLE) {
            return AdSize.SIZE_300x250;
        } else {
            return AdSize.SIZE_320x50;
        }
    }

    @Override
    public void destroyAd(AdData adData) {
        if (mAdView != null) {
            mAdView.destroy();
        }
    }

    //----------------------------------- HyBidAdView Callbacks ------------------------------------
    @Override
    public void onAdLoaded() {
        if (mBannerAdListener != null) {
            FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT,
                    FrameLayout.LayoutParams.WRAP_CONTENT);
            layoutParams.gravity = Gravity.CENTER;
            mBannerAdListener.onAdLoadSuccess(mAdView, layoutParams);
        }
    }

    @Override
    public void onAdLoadFailed(Throwable error) {
        if (mBannerAdListener != null) {
            mBannerAdListener.onAdLoadFailed(AdapterErrorType.ADAPTER_ERROR_TYPE_NO_FILL,
                    AdapterErrors.ADAPTER_ERROR_INTERNAL, error.getMessage());
        }
    }

    @Override
    public void onAdImpression() {
        if (mBannerAdListener != null) {
            mBannerAdListener.onAdOpened();
        }
    }

    @Override
    public void onAdClick() {
        if (mBannerAdListener != null) {
            mBannerAdListener.onAdClicked();
            mBannerAdListener.onAdLeftApplication();
        }
    }
}
