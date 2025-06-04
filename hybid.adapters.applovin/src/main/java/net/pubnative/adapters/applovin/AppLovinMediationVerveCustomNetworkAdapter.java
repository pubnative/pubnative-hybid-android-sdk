
// HyBid SDK License
//
// https://github.com/pubnative/pubnative-hybid-android-sdk/blob/main/LICENSE
//
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
import net.pubnative.lite.sdk.UserDataManager;
import net.pubnative.lite.sdk.interstitial.HyBidInterstitialAd;
import net.pubnative.lite.sdk.models.AdSize;
import net.pubnative.lite.sdk.models.ImpressionTrackingMethod;
import net.pubnative.lite.sdk.models.IntegrationType;
import net.pubnative.lite.sdk.models.NativeAd;
import net.pubnative.lite.sdk.request.HyBidNativeAdRequest;
import net.pubnative.lite.sdk.rewarded.HyBidRewardedAd;
import net.pubnative.lite.sdk.views.HyBidAdView;
import net.pubnative.lite.sdk.vpaid.enums.AudioState;

import java.lang.ref.WeakReference;
import java.util.concurrent.atomic.AtomicBoolean;

public class AppLovinMediationVerveCustomNetworkAdapter extends MediationAdapterBase implements MaxAdViewAdapter,
        MaxInterstitialAdapter, MaxRewardedAdapter, MaxNativeAdAdapter {
    public static final String MAX_MEDIATION_VENDOR = "m";
    public static final String MAX_ADAPTER_VERSION = "3.6.0.0";
    public static final String PARAM_APP_TOKEN = "pn_app_token";
    public static final String DUMMY_TOKEN = "dummytoken";

    private static final AtomicBoolean initialized = new AtomicBoolean();
    private static InitializationStatus status;

    private HyBidAdView mAdView;
    private HyBidInterstitialAd mInterstitialAd;
    private HyBidRewardedAd mRewardedAd;
    private NativeAd mNativeAd;

    public AppLovinMediationVerveCustomNetworkAdapter(AppLovinSdk appLovinSdk) {
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
        return HyBid.getSDKVersionInfo(IntegrationType.MEDIATION);
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

        if (mInterstitialAd != null) {
            mInterstitialAd.destroy();
            mInterstitialAd = null;
        }

        if (mRewardedAd != null) {
            mRewardedAd.destroy();
            mRewardedAd = null;
        }

        if (mNativeAd != null) {
            mNativeAd.stopTracking();
            mNativeAd = null;
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
        mAdView.setMediationVendor(AppLovinMediationVerveCustomNetworkAdapter.MAX_MEDIATION_VENDOR);
        mAdView.setTrackingMethod(ImpressionTrackingMethod.AD_VIEWABLE);
        mAdView.load(appToken, zoneId, new AdViewListener(adapterListener));
    }

    // Interstitial methods

    @Override
    public void loadInterstitialAd(MaxAdapterResponseParameters parameters, Activity activity, MaxInterstitialAdapterListener adapterListener) {
        log("Loading interstitial ad");

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
            adapterListener.onInterstitialAdLoadFailed(MaxAdapterError.INVALID_CONFIGURATION);
            return;
        }

        if (HyBid.getAppToken() != null && HyBid.getAppToken().equalsIgnoreCase(appToken) && HyBid.isInitialized()) {
            requestInterstitial(parameters, activity, appToken, zoneId, adapterListener);
        } else {
            HyBid.initialize(appToken, activity.getApplication(), b -> requestInterstitial(parameters, activity, appToken, zoneId, adapterListener));
        }
    }

    private void requestInterstitial(MaxAdapterResponseParameters parameters, Activity activity, String appToken, String zoneId, MaxInterstitialAdapterListener adapterListener) {
        updateMuteState(parameters);
        updateUserConsent(parameters);

        mInterstitialAd = new HyBidInterstitialAd(activity, appToken, zoneId, new InterstitialListener(adapterListener));
        mInterstitialAd.setMediation(true);
        mInterstitialAd.setMediationVendor(AppLovinMediationVerveCustomNetworkAdapter.MAX_MEDIATION_VENDOR);
        mInterstitialAd.load();
    }

    @Override
    public void showInterstitialAd(MaxAdapterResponseParameters parameters, Activity activity, MaxInterstitialAdapterListener listener) {
        log("Showing interstitial ad...");

        if (mInterstitialAd.isReady()) {
            mInterstitialAd.show();
        } else {
            log("Interstitial ad not ready");
            if (listener != null) {
                listener.onInterstitialAdDisplayFailed(MaxAdapterError.AD_NOT_READY);
            }
        }
    }

    // Rewarded methods

    @Override
    public void loadRewardedAd(MaxAdapterResponseParameters parameters, Activity activity, MaxRewardedAdapterListener adapterListener) {
        log("Loading rewarded ad");

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
            adapterListener.onRewardedAdLoadFailed(MaxAdapterError.INVALID_CONFIGURATION);
            return;
        }

        if (HyBid.getAppToken() != null && HyBid.getAppToken().equalsIgnoreCase(appToken) && HyBid.isInitialized()) {
            requestRewarded(parameters, activity, appToken, zoneId, adapterListener);
        } else {
            HyBid.initialize(appToken, activity.getApplication(), b -> requestRewarded(parameters, activity, appToken, zoneId, adapterListener));
        }
    }

    private void requestRewarded(MaxAdapterResponseParameters parameters, Activity activity, String appToken, String zoneId, MaxRewardedAdapterListener adapterListener) {
        updateMuteState(parameters);
        updateUserConsent(parameters);

        mRewardedAd = new HyBidRewardedAd(activity, appToken, zoneId, new RewardedListener(adapterListener));
        mRewardedAd.setMediation(true);
        mRewardedAd.setMediationVendor(AppLovinMediationVerveCustomNetworkAdapter.MAX_MEDIATION_VENDOR);
        mRewardedAd.load();
    }

    @Override
    public void showRewardedAd(MaxAdapterResponseParameters parameters, Activity activity, MaxRewardedAdapterListener adapterListener) {
        log("Showing rewarded ad...");

        if (mRewardedAd.isReady()) {
            configureReward(parameters);
            mRewardedAd.show();
        } else {
            log("Rewarded ad not ready");
            if (adapterListener != null) {
                adapterListener.onRewardedAdDisplayFailed(MaxAdapterError.AD_NOT_READY);
            }
        }
    }

    // Native methods

    @Override
    public void loadNativeAd(MaxAdapterResponseParameters parameters, Activity activity, MaxNativeAdAdapterListener adapterListener) {
        log("Loading native ad");

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
            adapterListener.onNativeAdLoadFailed(MaxAdapterError.INVALID_CONFIGURATION);
            return;
        }

        if (HyBid.getAppToken() != null && HyBid.getAppToken().equalsIgnoreCase(appToken) && HyBid.isInitialized()) {
            requestNative(parameters, activity, appToken, zoneId, adapterListener);
        } else {
            HyBid.initialize(appToken, activity.getApplication(), b -> requestNative(parameters, activity, appToken, zoneId, adapterListener));
        }
    }

    private void requestNative(MaxAdapterResponseParameters parameters, Activity activity, String appToken, String zoneId, MaxNativeAdAdapterListener adapterListener) {
        updateMuteState(parameters);
        updateUserConsent(parameters);

        HyBidNativeAdRequest nativeAdRequest = new HyBidNativeAdRequest();
        nativeAdRequest.setMediation(true);
        nativeAdRequest.setMediationVendor(AppLovinMediationVerveCustomNetworkAdapter.MAX_MEDIATION_VENDOR);
        nativeAdRequest.setPreLoadMediaAssets(true);
        nativeAdRequest.load(appToken, zoneId, new NativeAdListener(activity, parameters, adapterListener));
    }

    // Utility methods

    protected void updateUserConsent(final MaxAdapterResponseParameters parameters) {
        if (getWrappingSdk().getConfiguration().getConsentDialogState() == AppLovinSdkConfiguration.ConsentDialogState.APPLIES) {
            UserDataManager userDataManager = HyBid.getUserDataManager();
            Boolean hasUserConsent = parameters.hasUserConsent();

            // 1. AppLovin revokes consent
            if (hasUserConsent != null && userDataManager != null && !hasUserConsent) {
                userDataManager.setIABGDPRConsentString("0");
            }

            // 2. AppLovin grants consent and Verve doesn't have binary nor CMP consent yet
            if ((hasUserConsent != null && userDataManager != null && hasUserConsent)
                    && (TextUtils.isEmpty(userDataManager.getIABGDPRConsentString()) || userDataManager.getIABGDPRConsentString().equals("0"))) {
                userDataManager.setIABGDPRConsentString("1");
            }

            // 3. all other cases -> no change
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

    // ----------------------------- HyBidInterstitialAdListener -----------------------------------
    private class InterstitialListener
            implements HyBidInterstitialAd.Listener {
        private final MaxInterstitialAdapterListener listener;

        private InterstitialListener(final MaxInterstitialAdapterListener listener) {
            this.listener = listener;
        }

        @Override
        public void onInterstitialLoaded() {
            log("Interstitial ad loaded");
            listener.onInterstitialAdLoaded();
        }

        @Override
        public void onInterstitialLoadFailed(final Throwable error) {
            log("Interstitial ad failed to load with error: " + error);
            MaxAdapterError adapterError = toMaxError(error);
            listener.onInterstitialAdLoadFailed(adapterError);
        }

        @Override
        public void onInterstitialImpression() {
            log("Interstitial did track impression");
            listener.onInterstitialAdDisplayed();
        }

        @Override
        public void onInterstitialClick() {
            log("Interstitial clicked");
            listener.onInterstitialAdClicked();
        }

        @Override
        public void onInterstitialDismissed() {
            log("Interstitial hidden");
            listener.onInterstitialAdHidden();
        }
    }

    // -------------------------------- HyBidRewardedAdListener ------------------------------------
    private class RewardedListener
            implements HyBidRewardedAd.Listener {
        private final MaxRewardedAdapterListener listener;
        private boolean hasGrantedReward;

        private RewardedListener(final MaxRewardedAdapterListener listener) {
            this.listener = listener;
        }

        @Override
        public void onRewardedLoaded() {
            log("Rewarded ad loaded");
            listener.onRewardedAdLoaded();
        }

        @Override
        public void onRewardedLoadFailed(final Throwable error) {
            log("Rewarded ad failed to load with error: " + error);
            MaxAdapterError adapterError = toMaxError(error);
            listener.onRewardedAdLoadFailed(adapterError);
        }

        @Override
        public void onRewardedOpened() {
            log("Rewarded ad did track impression");
            listener.onRewardedAdDisplayed();
        }

        @Override
        public void onRewardedClick() {
            log("Rewarded ad clicked");
            listener.onRewardedAdClicked();
        }

        @Override
        public void onReward() {
            log("Rewarded ad reward granted");
            hasGrantedReward = true;
        }

        @Override
        public void onRewardedClosed() {
            log("Rewarded ad did disappear");

            if (hasGrantedReward || shouldAlwaysRewardUser()) {
                MaxReward reward = getReward();
                log("Rewarded user with reward: " + reward);
                listener.onUserRewarded(reward);
            }

            log("Rewarded ad hidden");
            listener.onRewardedAdHidden();
        }
    }

    private class NativeAdListener implements HyBidNativeAdRequest.RequestListener, NativeAd.Listener {
        final Bundle serverParameters;
        final WeakReference<Activity> activityRef;
        final MaxNativeAdAdapterListener listener;

        public NativeAdListener(final Activity activity, final MaxAdapterResponseParameters parameters, final MaxNativeAdAdapterListener listener) {
            serverParameters = parameters.getServerParameters();
            activityRef = new WeakReference<>(activity);

            this.listener = listener;
        }

        @Override
        public void onRequestSuccess(NativeAd ad) {
            mNativeAd = ad;

            String templateName = BundleUtils.getString("template", "", serverParameters);
            boolean isTemplateAd = AppLovinSdkUtils.isValidString(templateName);

            if (!hasRequiredAssets(isTemplateAd, mNativeAd)) {
                e("Native ad (" + mNativeAd + ") does not have required assets.");
                listener.onNativeAdLoadFailed(MaxAdapterError.MISSING_REQUIRED_NATIVE_AD_ASSETS);
                return;
            }

            processNativeAd();
        }

        @Override
        public void onRequestFail(Throwable throwable) {
            if (listener != null) {
                listener.onNativeAdLoadFailed(toMaxError(throwable));
            }
        }

        @Override
        public void onAdImpression(NativeAd ad, View view) {
            if (listener != null) {
                listener.onNativeAdDisplayed(null);
            }
        }

        @Override
        public void onAdClick(NativeAd ad, View view) {
            if (listener != null) {
                listener.onNativeAdClicked();
            }
        }

        private void processNativeAd() {
            final Activity activity = activityRef.get();
            if (activity == null) {
                log("Native ad failed to load: activity reference is null when ad is loaded");
                listener.onNativeAdLoadFailed(MaxAdapterError.INVALID_LOAD_STATE);
                return;
            }

            AppLovinSdkUtils.runOnUiThread(() -> {
                if (listener != null && mNativeAd != null) {

                    MaxNativeAd.Builder builder = new MaxNativeAd.Builder();
                    builder.setAdFormat(MaxAdFormat.NATIVE);

                    if (mNativeAd.getBannerBitmap() != null) {
                        ImageView bannerView = new ImageView(activity);
                        bannerView.setImageBitmap(mNativeAd.getBannerBitmap());
                        builder.setMediaView(bannerView);
                    }

                    if (mNativeAd.getIconBitmap() != null) {
                        ImageView iconView = new ImageView(activity);
                        iconView.setImageBitmap(mNativeAd.getIconBitmap());
                        builder.setIconView(iconView);
                    }

                    if (!TextUtils.isEmpty(mNativeAd.getTitle())) {
                        builder.setTitle(mNativeAd.getTitle());
                    }

                    if (!TextUtils.isEmpty(mNativeAd.getDescription())) {
                        builder.setBody(mNativeAd.getDescription());
                    }

                    if (!TextUtils.isEmpty(mNativeAd.getCallToActionText())) {
                        builder.setCallToAction(mNativeAd.getCallToActionText());
                    }
                    if (mNativeAd != null) {
                        View contentInfo = mNativeAd.getContentInfo(activity);
                        if (contentInfo != null) {
                            builder.setOptionsView(contentInfo);
                        }
                    }
                    MaxNativeAd maxNativeAd = new MaxVerveNativeAd(builder);
                    listener.onNativeAdLoaded(maxNativeAd, null);
                }
            });
        }

        private boolean hasRequiredAssets(final boolean isTemplateAd, final NativeAd assets) {
            if (isTemplateAd) {
                return AppLovinSdkUtils.isValidString(assets.getTitle());
            } else {
                return AppLovinSdkUtils.isValidString(assets.getTitle())
                        && AppLovinSdkUtils.isValidString(assets.getCallToActionText())
                        && AppLovinSdkUtils.isValidString(assets.getBannerUrl());
            }
        }

        private class MaxVerveNativeAd extends MaxNativeAd {
            private MaxVerveNativeAd(Builder builder) {
                super(builder);
            }

            @Override
            public void prepareViewForInteraction(MaxNativeAdView maxNativeAdView) {
                if (mNativeAd == null) {
                    e("Failed to register native ad view for interaction. Verve native ad is null");
                } else {
                    mNativeAd.startTracking(maxNativeAdView, NativeAdListener.this);
                }
            }
        }
    }
}
