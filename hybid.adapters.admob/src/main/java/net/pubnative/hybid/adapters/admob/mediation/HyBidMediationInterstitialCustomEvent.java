// HyBid SDK License
//
// https://github.com/pubnative/pubnative-hybid-android-sdk/blob/main/LICENSE
//
package net.pubnative.hybid.adapters.admob.mediation;

import android.app.Application;
import android.content.Context;
import android.text.TextUtils;

import androidx.annotation.NonNull;

import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.mediation.MediationAdLoadCallback;
import com.google.android.gms.ads.mediation.MediationConfiguration;
import com.google.android.gms.ads.mediation.MediationInterstitialAd;
import com.google.android.gms.ads.mediation.MediationInterstitialAdCallback;
import com.google.android.gms.ads.mediation.MediationInterstitialAdConfiguration;

import net.pubnative.hybid.adapters.admob.HyBidAdmobUtils;
import net.pubnative.lite.sdk.HyBid;
import net.pubnative.lite.sdk.interstitial.HyBidInterstitialAd;
import net.pubnative.lite.sdk.utils.Logger;

public class HyBidMediationInterstitialCustomEvent extends HyBidMediationBaseCustomEvent {
    private static final String TAG = HyBidMediationInterstitialCustomEvent.class.getSimpleName();

    @Override
    public void loadInterstitialAd(@NonNull MediationInterstitialAdConfiguration mediationInterstitialAdConfiguration, @NonNull MediationAdLoadCallback<MediationInterstitialAd, MediationInterstitialAdCallback> callback) {
        if (callback == null) {
            Logger.e(TAG, "MediationAdLoadCallback is null");
            return;
        }

        if (mediationInterstitialAdConfiguration == null || mediationInterstitialAdConfiguration.getContext() == null) {
            Logger.e(TAG, "Missing context. Dropping call");
            return;
        }

        HyBidInterstitialCustomEventLoader mAdLoader = new HyBidInterstitialCustomEventLoader(mediationInterstitialAdConfiguration, callback);
        mAdLoader.loadAd();
    }

    public class HyBidInterstitialCustomEventLoader implements HyBidInterstitialAd.Listener, MediationInterstitialAd {
        private HyBidInterstitialAd mInterstitialAd;
        private final MediationInterstitialAdConfiguration mAdConfiguration;
        private final MediationAdLoadCallback<MediationInterstitialAd, MediationInterstitialAdCallback> mAdLoadCallback;
        private MediationInterstitialAdCallback mInterstitialAdCallback;

        public HyBidInterstitialCustomEventLoader(MediationInterstitialAdConfiguration mediationInterstitialAdConfiguration, MediationAdLoadCallback<MediationInterstitialAd, MediationInterstitialAdCallback> mediationAdLoadCallback) {
            mAdConfiguration = mediationInterstitialAdConfiguration;
            mAdLoadCallback = mediationAdLoadCallback;
        }

        public void loadAd() {
            String zoneId;
            String appToken;

            String serverParameter = mAdConfiguration.getServerParameters().getString(MediationConfiguration.CUSTOM_EVENT_SERVER_PARAMETER_FIELD);
            if (!TextUtils.isEmpty(HyBidAdmobUtils.getAppToken(serverParameter))
                    && !TextUtils.isEmpty(HyBidAdmobUtils.getZoneId(serverParameter))) {
                zoneId = HyBidAdmobUtils.getZoneId(serverParameter);
                appToken = HyBidAdmobUtils.getAppToken(serverParameter);
            } else {
                Logger.e(TAG, "Could not find the required params in MediationInterstitialAdConfiguration. " +
                        "Required params in MediationInterstitialAdConfiguration must be provided as a valid JSON Object. " +
                        "Please consult HyBid documentation and update settings in your AdMob publisher dashboard.");
                mAdLoadCallback.onFailure(new AdError(AdRequest.ERROR_CODE_NETWORK_ERROR,
                        "Could not find the required params in MediationInterstitialAdConfiguration",
                        AdError.UNDEFINED_DOMAIN
                ));
                return;
            }

            if (HyBid.getAppToken() != null && HyBid.getAppToken().equalsIgnoreCase(appToken) && HyBid.isInitialized()) {
                loadInterstitial(mAdConfiguration.getContext(), zoneId);
            } else {
                HyBid.initialize(appToken, (Application) mAdConfiguration.getContext().getApplicationContext(), b ->
                        loadInterstitial(mAdConfiguration.getContext(), zoneId));
            }
        }

        private void loadInterstitial(Context context, String zoneId) {
            mInterstitialAd = new HyBidInterstitialAd(context, zoneId, this);
            mInterstitialAd.setMediation(true);
            mInterstitialAd.load();
        }

        @Override
        public void showAd(@NonNull Context context) {
            if (mInterstitialAd != null && mInterstitialAd.isReady()) {
                mInterstitialAd.show();
            }
        }

        @Override
        public void onInterstitialLoaded() {
            if (mAdLoadCallback != null) {
                mInterstitialAdCallback = mAdLoadCallback.onSuccess(this);
            }
        }

        @Override
        public void onInterstitialLoadFailed(Throwable error) {
            Logger.e(TAG, error.getMessage());
            if (mAdLoadCallback != null) {
                mAdLoadCallback.onFailure(new AdError(AdRequest.ERROR_CODE_NO_FILL,
                        error != null && !TextUtils.isEmpty(error.getMessage()) ? error.getMessage() : "No fill.",
                        AdError.UNDEFINED_DOMAIN
                ));
            }
        }

        @Override
        public void onInterstitialImpression() {
            if (mInterstitialAdCallback != null) {
                mInterstitialAdCallback.reportAdImpression();
                mInterstitialAdCallback.onAdOpened();
            }
        }

        @Override
        public void onInterstitialDismissed() {
            if (mInterstitialAdCallback != null) {
                mInterstitialAdCallback.onAdClosed();
            }
        }

        @Override
        public void onInterstitialClick() {
            if (mInterstitialAdCallback != null) {
                mInterstitialAdCallback.reportAdClicked();
                mInterstitialAdCallback.onAdOpened();
                mInterstitialAdCallback.onAdLeftApplication();
            }
        }
    }
}
