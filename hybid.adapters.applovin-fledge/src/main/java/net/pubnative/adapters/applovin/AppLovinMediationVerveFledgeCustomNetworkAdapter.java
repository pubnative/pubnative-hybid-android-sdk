package net.pubnative.adapters.applovin;

import android.app.Activity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.applovin.impl.sdk.utils.BundleUtils;
import com.applovin.mediation.MaxAdFormat;
import com.applovin.mediation.MaxReward;
import com.applovin.mediation.adapter.MaxAdViewAdapter;
import com.applovin.mediation.adapter.MaxAdapterError;
import com.applovin.mediation.adapter.MaxInterstitialAdapter;
import com.applovin.mediation.adapter.MaxNativeAdAdapter;
import com.applovin.mediation.adapter.MaxRewardedAdapter;
import com.applovin.mediation.adapter.listeners.MaxAdViewAdapterListener;
import com.applovin.mediation.adapter.listeners.MaxInterstitialAdapterListener;
import com.applovin.mediation.adapter.listeners.MaxNativeAdAdapterListener;
import com.applovin.mediation.adapter.listeners.MaxRewardedAdapterListener;
import com.applovin.mediation.adapter.parameters.MaxAdapterInitializationParameters;
import com.applovin.mediation.adapter.parameters.MaxAdapterResponseParameters;
import com.applovin.mediation.adapters.MediationAdapterBase;
import com.applovin.mediation.nativeAds.MaxNativeAd;
import com.applovin.mediation.nativeAds.MaxNativeAdView;
import com.applovin.sdk.AppLovinSdk;
import com.applovin.sdk.AppLovinSdkConfiguration;
import com.applovin.sdk.AppLovinSdkUtils;

import net.pubnative.lite.sdk.HyBid;
import net.pubnative.lite.sdk.HyBidError;
import net.pubnative.lite.sdk.interstitial.HyBidInterstitialAd;
import net.pubnative.lite.sdk.models.AdSize;
import net.pubnative.lite.sdk.models.ImpressionTrackingMethod;
import net.pubnative.lite.sdk.models.NativeAd;
import net.pubnative.lite.sdk.request.HyBidNativeAdRequest;
import net.pubnative.lite.sdk.rewarded.HyBidRewardedAd;
import net.pubnative.lite.sdk.views.HyBidAdView;
import net.pubnative.lite.sdk.vpaid.enums.AudioState;

import java.lang.ref.WeakReference;
import java.util.concurrent.atomic.AtomicBoolean;

public class AppLovinMediationVerveFledgeCustomNetworkAdapter extends MediationAdapterBase implements MaxAdViewAdapter {
    public static final String MAX_MEDIATION_VENDOR = "m";
    public static final String MAX_ADAPTER_VERSION = "3.2.1.0";
    public static final String PARAM_APP_TOKEN = "pn_app_token";
    public static final String DUMMY_TOKEN = "dummytoken";

    private static final AtomicBoolean initialized = new AtomicBoolean();
    private static InitializationStatus status;

    private HyBidAdView mAdView;

    public AppLovinMediationVerveFledgeCustomNetworkAdapter(AppLovinSdk appLovinSdk) {
        super(appLovinSdk);
    }

    @Override
    protected void log(String s) {
        Log.i("VerveCustomAdapter", s);
    }

    @Override
    public void initialize(MaxAdapterInitializationParameters parameters, Activity activity, OnCompletionListener onCompletionListener) {
        if (initialized.compareAndSet(false, true)) {
            status = InitializationStatus.INITIALIZING;

            final String appToken = parameters.getServerParameters().getString(PARAM_APP_TOKEN, DUMMY_TOKEN);
            log("Initializing Verve SDK with app token: " + appToken + "...");

            if (parameters.isTesting()) {
                HyBid.setTestMode(true);
            }

            HyBid.setLocationUpdatesEnabled(false);
            HyBid.initialize(appToken, activity.getApplication(), b -> {
                log("Verve SDK initialized");
                status = InitializationStatus.INITIALIZED_SUCCESS;
                onCompletionListener.onCompletion(status, null);
            });
        } else {
            log("Verve attempted to initialize already - marking initialization as " + status);
            onCompletionListener.onCompletion(status, null);
        }
    }

    @Override
    public String getSdkVersion() {
        return HyBid.getSDKVersionInfo();
    }

    @Override
    public String getAdapterVersion() {
        return MAX_ADAPTER_VERSION;
    }

    @Override
    public void onDestroy() {
        if (mAdView != null) {
            mAdView.destroy();
            mAdView = null;
        }
    }

    // Banner methods
    @Override
    public void loadAdViewAd(MaxAdapterResponseParameters parameters, MaxAdFormat adFormat, Activity activity, MaxAdViewAdapterListener adapterListener) {
        log("Loading " + adFormat.getLabel() + " ad view ad...");
        if (adapterListener == null || parameters == null) {
            log("Adapter error. Null parameters");
            return;
        }

        String zoneId;
        String appToken;
        if (!TextUtils.isEmpty(parameters.getThirdPartyAdPlacementId())
                || !TextUtils.isEmpty(parameters.getCustomParameters().getString(PARAM_APP_TOKEN))) {
            zoneId = parameters.getThirdPartyAdPlacementId();
            appToken = parameters.getCustomParameters().getString(PARAM_APP_TOKEN);
            log("found pn_zone_id=" + zoneId + ", pn_app_token=" + appToken);
        } else {
            String errorMessage = "Could not find the required params in MaxAdapterResponseParameters. " +
                    "Required params in MaxAdapterResponseParameters parameters must be provided as a valid JSON Object. " +
                    "Please consult HyBid documentation and update settings in your Applovin publisher dashboard.";
            log(errorMessage);
            adapterListener.onAdViewAdLoadFailed(MaxAdapterError.INVALID_CONFIGURATION);
            return;
        }

        if (HyBid.getAppToken() != null && HyBid.getAppToken().equalsIgnoreCase(appToken) && HyBid.isInitialized()) {
            requestBanner(parameters, adFormat, activity, appToken, zoneId, adapterListener);
        } else {
            HyBid.initialize(appToken, activity.getApplication(), b -> requestBanner(parameters, adFormat, activity, appToken, zoneId, adapterListener));
        }
    }

    private void requestBanner(MaxAdapterResponseParameters parameters, MaxAdFormat adFormat, Activity activity, String appToken, String zoneId, MaxAdViewAdapterListener adapterListener) {
        updateMuteState(parameters);
        updateUserConsent(parameters);

        mAdView = new HyBidAdView(activity, getSize(adFormat));
        mAdView.setMediation(true);
        mAdView.setMediationVendor(AppLovinMediationVerveFledgeCustomNetworkAdapter.MAX_MEDIATION_VENDOR);
        mAdView.setTrackingMethod(ImpressionTrackingMethod.AD_VIEWABLE);
        mAdView.load(appToken, zoneId, new AdViewListener(adapterListener));
    }

    // Utility methods

    protected void updateUserConsent(final MaxAdapterResponseParameters parameters) {
        if (getWrappingSdk().getConfiguration().getConsentDialogState() == AppLovinSdkConfiguration.ConsentDialogState.APPLIES) {
            Boolean hasUserConsent = parameters.hasUserConsent();
            if (hasUserConsent != null && HyBid.getUserDataManager() != null) {
                if (TextUtils.isEmpty(HyBid.getUserDataManager().getIABGDPRConsentString())) {
                    HyBid.getUserDataManager().setIABGDPRConsentString(hasUserConsent ? "1" : "0");
                }
            } else { /* Don't do anything if huc value not set */ }
        }

        Boolean isAgeRestrictedUser = parameters.isAgeRestrictedUser();
        if (isAgeRestrictedUser != null) {
            HyBid.setCoppaEnabled(isAgeRestrictedUser);
        }

        if (AppLovinSdk.VERSION_CODE >= 91100) {
            if (HyBid.getUserDataManager() != null
                    && TextUtils.isEmpty(HyBid.getUserDataManager().getIABUSPrivacyString())) {
                Boolean isDoNotSell = parameters.isDoNotSell();
                if (isDoNotSell != null && isDoNotSell) {
                    HyBid.getUserDataManager().setIABUSPrivacyString("1NYN");
                } else {
                    HyBid.getUserDataManager().removeIABUSPrivacyString();
                }
            }
        }
    }

    protected static void updateMuteState(final MaxAdapterResponseParameters parameters) {
        Bundle serverParameters = parameters.getServerParameters();
        if (serverParameters.containsKey("is_muted")) {
            if (serverParameters.getBoolean("is_muted")) {
                HyBid.setVideoAudioStatus(AudioState.MUTED);
            } else {
                HyBid.setVideoAudioStatus(AudioState.DEFAULT);
            }
        }
    }

    private static AdSize getSize(MaxAdFormat adFormat) {
        if (adFormat == MaxAdFormat.BANNER) {
            return AdSize.SIZE_320x50;
        } else if (adFormat == MaxAdFormat.LEADER) {
            return AdSize.SIZE_728x90;
        } else if (adFormat == MaxAdFormat.MREC) {
            return AdSize.SIZE_300x250;
        } else {
            throw new IllegalArgumentException("Invalid ad format: " + adFormat);
        }
    }

    protected static MaxAdapterError toMaxError(Throwable verveError) {
        MaxAdapterError adapterError = MaxAdapterError.UNSPECIFIED;
        if (verveError instanceof HyBidError) {
            HyBidError hyBidError = (HyBidError) verveError;
            switch (hyBidError.getErrorCode()) {
                case NO_FILL:
                case NULL_AD:
                    adapterError = MaxAdapterError.NO_FILL;
                    break;
                case INVALID_ASSET:
                case UNSUPPORTED_ASSET:
                    adapterError = MaxAdapterError.INVALID_CONFIGURATION;
                    break;
                case PARSER_ERROR:
                case SERVER_ERROR_PREFIX:
                    adapterError = MaxAdapterError.SERVER_ERROR;
                    break;
                case INVALID_AD:
                case INVALID_ZONE_ID:
                case INVALID_SIGNAL_DATA:
                    adapterError = MaxAdapterError.BAD_REQUEST;
                    break;
                case NOT_INITIALISED:
                    adapterError = MaxAdapterError.NOT_INITIALIZED;
                    break;
                case AUCTION_NO_AD:
                case ERROR_RENDERING_BANNER:
                case ERROR_RENDERING_INTERSTITIAL:
                case ERROR_RENDERING_REWARDED:
                    adapterError = MaxAdapterError.AD_NOT_READY;
                    break;
                case INTERNAL_ERROR:
                    adapterError = MaxAdapterError.INTERNAL_ERROR;
                    break;
            }
        }
        return new MaxAdapterError(adapterError.getErrorCode(),
                adapterError.getErrorMessage(), 0, verveError.getMessage());
    }

    // ---------------------------------- HyBidAdViewListener --------------------------------------
    private class AdViewListener
            implements HyBidAdView.Listener {
        private final MaxAdViewAdapterListener listener;

        private AdViewListener(final MaxAdViewAdapterListener listener) {
            this.listener = listener;
        }

        @Override
        public void onAdLoaded() {
            log("AdView ad loaded");
            listener.onAdViewAdLoaded(mAdView);
        }

        @Override
        public void onAdLoadFailed(final Throwable error) {
            log("AdView failed to load with error: " + error);
            MaxAdapterError adapterError = toMaxError(error);
            listener.onAdViewAdLoadFailed(adapterError);
        }

        @Override
        public void onAdImpression() {
            log("AdView did track impression");
            listener.onAdViewAdDisplayed();
        }

        @Override
        public void onAdClick() {
            log("AdView clicked");
            listener.onAdViewAdClicked();
        }
    }
}
