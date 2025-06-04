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
import com.google.android.gms.ads.mediation.MediationRewardedAd;
import com.google.android.gms.ads.mediation.MediationRewardedAdCallback;
import com.google.android.gms.ads.mediation.MediationRewardedAdConfiguration;
import com.google.android.gms.ads.rewarded.RewardItem;

import net.pubnative.hybid.adapters.admob.HyBidAdmobUtils;
import net.pubnative.lite.sdk.HyBid;
import net.pubnative.lite.sdk.rewarded.HyBidRewardedAd;
import net.pubnative.lite.sdk.utils.Logger;

public class HyBidMediationRewardedVideoCustomEvent extends HyBidMediationBaseCustomEvent {
    private static final String TAG = HyBidMediationRewardedVideoCustomEvent.class.getSimpleName();

    @Override
    public void loadRewardedAd(@NonNull MediationRewardedAdConfiguration mediationRewardedAdConfiguration, @NonNull MediationAdLoadCallback<MediationRewardedAd, MediationRewardedAdCallback> callback) {
        if (callback == null) {
            Logger.e(TAG, "MediationAdLoadCallback is null");
            return;
        }

        if (mediationRewardedAdConfiguration == null || mediationRewardedAdConfiguration.getContext() == null) {
            Logger.e(TAG, "Missing context. Dropping call");
            return;
        }

        HyBidRewardedCustomEventLoader mAdLoader = new HyBidRewardedCustomEventLoader(mediationRewardedAdConfiguration, callback);
        mAdLoader.loadAd();
    }

    public class HyBidRewardedCustomEventLoader implements HyBidRewardedAd.Listener, MediationRewardedAd {

        private HyBidRewardedAd mRewardedAd;
        private final MediationRewardedAdConfiguration mAdConfiguration;
        private final MediationAdLoadCallback<MediationRewardedAd, MediationRewardedAdCallback> mAdLoadCallback;
        private MediationRewardedAdCallback mRewardedAdCallback;

        public HyBidRewardedCustomEventLoader(MediationRewardedAdConfiguration mediationRewardedAdConfiguration, MediationAdLoadCallback<MediationRewardedAd, MediationRewardedAdCallback> mediationAdLoadCallback) {
            mAdConfiguration = mediationRewardedAdConfiguration;
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
                Logger.e(TAG, "Could not find the required params in MediationRewardedAdConfiguration. " +
                        "Required params in MediationRewardedAdConfiguration must be provided as a valid JSON Object. " +
                        "Please consult HyBid documentation and update settings in your AdMob publisher dashboard.");
                mAdLoadCallback.onFailure(new AdError(AdRequest.ERROR_CODE_NETWORK_ERROR,
                        "Could not find the required params in MediationRewardedAdConfiguration",
                        AdError.UNDEFINED_DOMAIN
                ));
                return;
            }

            if (HyBid.getAppToken() != null && HyBid.getAppToken().equalsIgnoreCase(appToken) && HyBid.isInitialized()) {
                requestRewardedAd(mAdConfiguration.getContext(), zoneId);
            } else {
                HyBid.initialize(appToken, (Application) mAdConfiguration.getContext().getApplicationContext(), b ->
                        requestRewardedAd(mAdConfiguration.getContext(), zoneId));
            }
        }

        private void requestRewardedAd(Context context, String zoneId) {
            mRewardedAd = new HyBidRewardedAd(context, zoneId, this);
            mRewardedAd.setMediation(true);
            mRewardedAd.load();
        }

        @Override
        public void showAd(@NonNull Context context) {
            if (mRewardedAd != null && mRewardedAd.isReady()) {
                mRewardedAd.show();
            }
        }

        @Override
        public void onRewardedLoaded() {
            if (mAdLoadCallback != null) {
                mRewardedAdCallback = mAdLoadCallback.onSuccess(this);
            }
        }

        @Override
        public void onRewardedLoadFailed(Throwable error) {
            Logger.e(TAG, error.getMessage());
            if (mAdLoadCallback != null) {
                mAdLoadCallback.onFailure(new AdError(AdRequest.ERROR_CODE_NO_FILL,
                        error != null && !TextUtils.isEmpty(error.getMessage()) ? error.getMessage() : "No fill.",
                        AdError.UNDEFINED_DOMAIN));
            }
        }

        @Override
        public void onRewardedOpened() {
            if (mRewardedAdCallback != null) {
                mRewardedAdCallback.reportAdImpression();
                mRewardedAdCallback.onAdOpened();
                mRewardedAdCallback.onVideoStart();
            }
        }

        @Override
        public void onRewardedClosed() {
            if (mRewardedAdCallback != null) {
                mRewardedAdCallback.onAdClosed();
                mRewardedAdCallback.onVideoComplete();
            }
        }

        @Override
        public void onRewardedClick() {
            if (mRewardedAdCallback != null) {
                mRewardedAdCallback.reportAdClicked();
            }
        }

        @Override
        public void onReward() {
            if (mRewardedAdCallback != null) {
                mRewardedAdCallback.onUserEarnedReward(new HyBidReward("hybid_reward", 0));
            }
        }

        private final class HyBidReward implements RewardItem {
            private final String mType;
            private final int mRewardValue;

            public HyBidReward(String type, int value) {
                this.mType = type;
                this.mRewardValue = value;
            }

            @Override
            public String getType() {
                return mType;
            }

            @Override
            public int getAmount() {
                return mRewardValue;
            }
        }
    }
}
