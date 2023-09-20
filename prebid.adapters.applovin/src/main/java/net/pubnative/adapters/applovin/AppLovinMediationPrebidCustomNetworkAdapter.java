package net.pubnative.adapters.applovin;

import android.app.Activity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.NonNull;

import com.applovin.mediation.MaxAdFormat;
import com.applovin.mediation.MaxReward;
import com.applovin.mediation.adapter.MaxAdViewAdapter;
import com.applovin.mediation.adapter.MaxAdapterError;
import com.applovin.mediation.adapter.MaxInterstitialAdapter;
import com.applovin.mediation.adapter.MaxRewardedAdapter;
import com.applovin.mediation.adapter.listeners.MaxAdViewAdapterListener;
import com.applovin.mediation.adapter.listeners.MaxInterstitialAdapterListener;
import com.applovin.mediation.adapter.listeners.MaxRewardedAdapterListener;
import com.applovin.mediation.adapter.parameters.MaxAdapterInitializationParameters;
import com.applovin.mediation.adapter.parameters.MaxAdapterResponseParameters;
import com.applovin.mediation.adapters.MediationAdapterBase;
import com.applovin.sdk.AppLovinSdk;
import com.applovin.sdk.AppLovinSdkConfiguration;

import org.prebid.mobile.AdSize;
import org.prebid.mobile.Host;
import org.prebid.mobile.PrebidMobile;
import org.prebid.mobile.TargetingParams;
import org.prebid.mobile.api.data.InitializationStatus;
import org.prebid.mobile.api.exceptions.AdException;
import org.prebid.mobile.api.exceptions.InitError;
import org.prebid.mobile.api.rendering.BannerView;
import org.prebid.mobile.api.rendering.InterstitialAdUnit;
import org.prebid.mobile.api.rendering.RewardedAdUnit;
import org.prebid.mobile.api.rendering.listeners.BannerViewListener;
import org.prebid.mobile.api.rendering.listeners.InterstitialAdUnitListener;
import org.prebid.mobile.api.rendering.listeners.RewardedAdUnitListener;
import org.prebid.mobile.rendering.listeners.SdkInitializationListener;

import java.util.concurrent.atomic.AtomicBoolean;

public class AppLovinMediationPrebidCustomNetworkAdapter extends MediationAdapterBase implements MaxAdViewAdapter,
        MaxInterstitialAdapter, MaxRewardedAdapter {

    public static final String PREBID_VERSION = "2.0.4";
    public static final String MAX_ADAPTER_VERSION = PREBID_VERSION + ".0";
    public static final String PARAM_ACCOUNT_ID = "prebid_account_id";
    public static final String PARAM_SERVER_AUCTION_ENDPOINT = "prebid_server_auction_endpoint";
    public static final String PARAM_SERVER_STATUS_ENDPOINT = "prebid_server_status_endpoint";

    private static final AtomicBoolean initialized = new AtomicBoolean();
    private static InitializationStatus status;

    private BannerView mAdView;
    private InterstitialAdUnit mInterstitialAd;
    private RewardedAdUnit mRewardedAd;

    public AppLovinMediationPrebidCustomNetworkAdapter(AppLovinSdk appLovinSdk) {
        super(appLovinSdk);
    }

    @Override
    protected void log(String s) {
        Log.i("PrebidCustomAdapter", s);
    }

    @Override
    public void initialize(MaxAdapterInitializationParameters parameters, Activity activity, OnCompletionListener onCompletionListener) {
        if (initialized.compareAndSet(false, true)) {
            status = InitializationStatus.INITIALIZING;

            final String accountId = parameters.getServerParameters().getString(PARAM_ACCOUNT_ID);
            final String serverAuctionUrl = parameters.getServerParameters().getString(PARAM_SERVER_AUCTION_ENDPOINT);
            final String serverStatusUrl = parameters.getServerParameters().getString(PARAM_SERVER_STATUS_ENDPOINT);

            if (TextUtils.isEmpty(accountId) || TextUtils.isEmpty(serverAuctionUrl) || TextUtils.isEmpty(serverStatusUrl)) {
                status = InitializationStatus.INITIALIZED_FAILURE;
                onCompletionListener.onCompletion(status, "Missing parameters");
            } else {
                log("Initializing PrebidMobile SDK with account id: " + accountId + "...");

                if (parameters.isTesting()) {
                    PrebidMobile.setPbsDebug(true);
                }

                PrebidMobile.setPrebidServerAccountId(accountId);
                PrebidMobile.setPrebidServerHost(Host.createCustomHost(serverAuctionUrl));

                PrebidMobile.initializeSdk(activity, new SdkInitializationListener() {
                    @Override
                    public void onInitializationComplete(@NonNull org.prebid.mobile.api.data.InitializationStatus pStatus) {
                        switch(pStatus) {
                            case FAILED: {
                                status = InitializationStatus.INITIALIZED_FAILURE;
                                onCompletionListener.onCompletion(status, null);
                                break;
                            }
                            case SUCCEEDED: {
                                log("PrebidMobile SDK initialized");
                                status = InitializationStatus.INITIALIZED_SUCCESS;
                                onCompletionListener.onCompletion(status, null);
                                break;
                            }
                            case SERVER_STATUS_WARNING: {
                                log("PrebidMobile SDK SERVER_STATUS_WARNING");
                                status = InitializationStatus.INITIALIZED_FAILURE;
                                onCompletionListener.onCompletion(status, null);
                                break;
                            }
                            default: {
                                status = InitializationStatus.INITIALIZED_FAILURE;
                                onCompletionListener.onCompletion(status, null);
                            }
                        }
                    }
                });
            }
        } else {
            log("PrebidMobile attempted to initialize already - marking initialization as " + status);
            onCompletionListener.onCompletion(status, null);
        }
    }

    @Override
    public String getSdkVersion() {
        return PREBID_VERSION;
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

        String configId;
        if (!TextUtils.isEmpty(parameters.getThirdPartyAdPlacementId())) {
            configId = parameters.getThirdPartyAdPlacementId();
            log("found prebid config id =" + configId);
        } else {
            String errorMessage = "Could not find the required params in MaxAdapterResponseParameters. " +
                    "Required params in MaxAdapterResponseParameters parameters must be provided as a valid JSON Object. " +
                    "Please consult Prebid documentation and update settings in your Applovin publisher dashboard.";
            log(errorMessage);
            adapterListener.onAdViewAdLoadFailed(MaxAdapterError.INVALID_CONFIGURATION);
            return;
        }

        requestBanner(parameters, adFormat, activity, configId, adapterListener);
    }

    private void requestBanner(MaxAdapterResponseParameters parameters, MaxAdFormat adFormat, Activity activity, String configId, MaxAdViewAdapterListener adapterListener) {
        updateUserConsent(parameters);

        mAdView = new BannerView(activity, configId, getSize(adFormat));
        mAdView.setBannerListener(new AdViewListener(adapterListener));
        mAdView.loadAd();
    }

    // Interstitial methods

    @Override
    public void loadInterstitialAd(MaxAdapterResponseParameters parameters, Activity activity, MaxInterstitialAdapterListener adapterListener) {
        log("Loading interstitial ad");

        if (adapterListener == null || parameters == null) {
            log("Adapter error. Null parameters");
            return;
        }

        String configId;
        if (!TextUtils.isEmpty(parameters.getThirdPartyAdPlacementId())) {
            configId = parameters.getThirdPartyAdPlacementId();
            log("found prebid config id =" + configId);
        } else {
            String errorMessage = "Could not find the required params in MaxAdapterResponseParameters. " +
                    "Required params in MaxAdapterResponseParameters parameters must be provided as a valid JSON Object. " +
                    "Please consult Prebid documentation and update settings in your Applovin publisher dashboard.";
            log(errorMessage);
            adapterListener.onInterstitialAdLoadFailed(MaxAdapterError.INVALID_CONFIGURATION);
            return;
        }

        requestInterstitial(parameters, activity, configId, adapterListener);
    }

    private void requestInterstitial(MaxAdapterResponseParameters parameters, Activity activity, String configId, MaxInterstitialAdapterListener adapterListener) {
        updateUserConsent(parameters);

        mInterstitialAd = new InterstitialAdUnit(activity, configId);
        mInterstitialAd.setInterstitialAdUnitListener(new InterstitialListener(adapterListener));
        mInterstitialAd.setIsMuted(getMuteState(parameters));
        mInterstitialAd.loadAd();
    }

    @Override
    public void showInterstitialAd(MaxAdapterResponseParameters parameters, Activity activity, MaxInterstitialAdapterListener listener) {
        log("Showing interstitial ad...");

        if (mInterstitialAd.isLoaded()) {
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

        String configId;
        if (!TextUtils.isEmpty(parameters.getThirdPartyAdPlacementId())) {
            configId = parameters.getThirdPartyAdPlacementId();
            log("found prebid config id =" + configId);
        } else {
            String errorMessage = "Could not find the required params in MaxAdapterResponseParameters. " +
                    "Required params in MaxAdapterResponseParameters parameters must be provided as a valid JSON Object. " +
                    "Please consult Prebid documentation and update settings in your Applovin publisher dashboard.";
            log(errorMessage);
            adapterListener.onRewardedAdLoadFailed(MaxAdapterError.INVALID_CONFIGURATION);
            return;
        }

        requestRewarded(parameters, activity, configId, adapterListener);
    }

    private void requestRewarded(MaxAdapterResponseParameters parameters, Activity activity, String configId, MaxRewardedAdapterListener adapterListener) {
        updateUserConsent(parameters);

        mRewardedAd = new RewardedAdUnit(activity, configId);
        mRewardedAd.setRewardedAdUnitListener(new RewardedListener(adapterListener));
        mRewardedAd.setIsMuted(getMuteState(parameters));
        mRewardedAd.loadAd();
    }

    @Override
    public void showRewardedAd(MaxAdapterResponseParameters parameters, Activity activity, MaxRewardedAdapterListener adapterListener) {
        log("Showing rewarded ad...");

        if (mRewardedAd.isLoaded()) {
            configureReward(parameters);
            mRewardedAd.show();
        } else {
            log("Rewarded ad not ready");
            if (adapterListener != null) {
                adapterListener.onRewardedAdDisplayFailed(MaxAdapterError.AD_NOT_READY);
            }
        }
    }

    // Utility methods

    protected void updateUserConsent(final MaxAdapterResponseParameters parameters) {
        if (getWrappingSdk().getConfiguration().getConsentDialogState() == AppLovinSdkConfiguration.ConsentDialogState.APPLIES) {
            Boolean hasUserConsent = parameters.hasUserConsent();
            if (hasUserConsent != null) {
                if (TextUtils.isEmpty(TargetingParams.getGDPRConsentString())) {
                    TargetingParams.setGDPRConsentString(hasUserConsent ? "1" : "0");
                }
            }
        }

        Boolean isAgeRestrictedUser = parameters.isAgeRestrictedUser();
        if (isAgeRestrictedUser != null) {
            TargetingParams.setSubjectToCOPPA(isAgeRestrictedUser);
        }
    }

    protected static boolean getMuteState(final MaxAdapterResponseParameters parameters) {
        return parameters.getServerParameters().getBoolean("is_muted", false);
    }

    private static AdSize getSize(MaxAdFormat adFormat) {
        return new AdSize(adFormat.getSize().getWidth(), adFormat.getSize().getHeight());
    }

    protected static MaxAdapterError toMaxError(AdException adException) {
        MaxAdapterError adapterError = MaxAdapterError.UNSPECIFIED;

        if (adException.getMessage().equalsIgnoreCase(AdException.INIT_ERROR)) {
            adapterError = MaxAdapterError.NOT_INITIALIZED;
        } else if (adException.getMessage().equalsIgnoreCase(AdException.INTERNAL_ERROR)) {
            adapterError = MaxAdapterError.INTERNAL_ERROR;
        } else if (adException.getMessage().equalsIgnoreCase(AdException.SERVER_ERROR)) {
            adapterError = MaxAdapterError.SERVER_ERROR;
        } else if (adException.getMessage().equalsIgnoreCase(AdException.INVALID_REQUEST)) {
            adapterError = MaxAdapterError.INVALID_CONFIGURATION;
        }

        return new MaxAdapterError(adapterError.getErrorCode(),
                adapterError.getErrorMessage(), 0, adException.getMessage());
    }

    // ---------------------------------- HyBidAdViewListener --------------------------------------
    private class AdViewListener
            implements BannerViewListener {
        private final MaxAdViewAdapterListener listener;

        private AdViewListener(final MaxAdViewAdapterListener listener) {
            this.listener = listener;
        }

        @Override
        public void onAdLoaded(BannerView bannerView) {
            log("AdView ad loaded");
            listener.onAdViewAdLoaded(mAdView);
        }

        @Override
        public void onAdFailed(BannerView bannerView, AdException exception) {
            log("AdView failed to load with error: " + exception.getMessage());
            MaxAdapterError adapterError = toMaxError(exception);
            listener.onAdViewAdLoadFailed(adapterError);
        }

        @Override
        public void onAdDisplayed(BannerView bannerView) {
            log("AdView did track impression");
            listener.onAdViewAdDisplayed();
        }

        @Override
        public void onAdClicked(BannerView bannerView) {
            log("AdView clicked");
            listener.onAdViewAdClicked();
        }

        @Override
        public void onAdClosed(BannerView bannerView) {
            log("AdView closed");
            listener.onAdViewAdCollapsed();
        }
    }

    // ----------------------------- HyBidInterstitialAdListener -----------------------------------
    private class InterstitialListener
            implements InterstitialAdUnitListener {
        private final MaxInterstitialAdapterListener listener;

        private InterstitialListener(final MaxInterstitialAdapterListener listener) {
            this.listener = listener;
        }

        @Override
        public void onAdLoaded(InterstitialAdUnit interstitialAdUnit) {
            log("Interstitial ad loaded");
            listener.onInterstitialAdLoaded();
        }

        @Override
        public void onAdFailed(InterstitialAdUnit interstitialAdUnit, AdException exception) {
            log("Interstitial ad failed to load with error: " + exception.getMessage());
            MaxAdapterError adapterError = toMaxError(exception);
            listener.onInterstitialAdLoadFailed(adapterError);
        }

        @Override
        public void onAdDisplayed(InterstitialAdUnit interstitialAdUnit) {
            log("Interstitial did track impression");
            listener.onInterstitialAdDisplayed();
        }

        @Override
        public void onAdClicked(InterstitialAdUnit interstitialAdUnit) {
            log("Interstitial clicked");
            listener.onInterstitialAdClicked();
        }

        @Override
        public void onAdClosed(InterstitialAdUnit interstitialAdUnit) {
            log("Interstitial hidden");
            listener.onInterstitialAdHidden();
        }
    }

    // -------------------------------- HyBidRewardedAdListener ------------------------------------
    private class RewardedListener
            implements RewardedAdUnitListener {
        private final MaxRewardedAdapterListener listener;
        private boolean hasGrantedReward;

        private RewardedListener(final MaxRewardedAdapterListener listener) {
            this.listener = listener;
        }

        @Override
        public void onAdLoaded(RewardedAdUnit rewardedAdUnit) {
            log("Rewarded ad loaded");
            listener.onRewardedAdLoaded();
        }

        @Override
        public void onAdFailed(RewardedAdUnit rewardedAdUnit, AdException exception) {
            log("Rewarded ad failed to load with error: " + exception.getMessage());
            MaxAdapterError adapterError = toMaxError(exception);
            listener.onRewardedAdLoadFailed(adapterError);
        }

        @Override
        public void onAdDisplayed(RewardedAdUnit rewardedAdUnit) {
            log("Rewarded ad did track impression");
            listener.onRewardedAdDisplayed();
            listener.onRewardedAdVideoStarted();
        }

        @Override
        public void onAdClicked(RewardedAdUnit rewardedAdUnit) {
            log("Rewarded ad clicked");
            listener.onRewardedAdClicked();
        }

        @Override
        public void onAdClosed(RewardedAdUnit rewardedAdUnit) {
            log("Rewarded ad did disappear");
            listener.onRewardedAdVideoCompleted();

            if (hasGrantedReward || shouldAlwaysRewardUser()) {
                MaxReward reward = getReward();
                log("Rewarded user with reward: " + reward);
                listener.onUserRewarded(reward);
            }

            log("Rewarded ad hidden");
            listener.onRewardedAdHidden();
        }

        @Override
        public void onUserEarnedReward(RewardedAdUnit rewardedAdUnit) {
            log("Rewarded ad reward granted");
            hasGrantedReward = true;
        }
    }
}
