package net.pubnative.hybid.adapters.admob.mediation;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;

import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.mediation.Adapter;
import com.google.android.gms.ads.mediation.InitializationCompleteCallback;
import com.google.android.gms.ads.mediation.MediationAdLoadCallback;
import com.google.android.gms.ads.mediation.MediationConfiguration;
import com.google.android.gms.ads.mediation.MediationRewardedAd;
import com.google.android.gms.ads.mediation.MediationRewardedAdCallback;
import com.google.android.gms.ads.mediation.MediationRewardedAdConfiguration;
import com.google.android.gms.ads.mediation.VersionInfo;
import com.google.android.gms.ads.rewarded.RewardItem;

import net.pubnative.hybid.adapters.admob.HyBidAdmobUtils;
import net.pubnative.lite.sdk.HyBid;
import net.pubnative.lite.sdk.rewarded.HyBidRewardedAd;
import net.pubnative.lite.sdk.utils.Logger;

import java.util.List;

public class HyBidMediationRewardedVideoCustomEvent extends Adapter implements HyBidRewardedAd.Listener, MediationRewardedAd {
    private static final String TAG = HyBidMediationRewardedVideoCustomEvent.class.getSimpleName();

    private HyBidRewardedAd mRewardedAd;
    private MediationAdLoadCallback<MediationRewardedAd, MediationRewardedAdCallback> mAdLoadCallback;
    private MediationRewardedAdCallback mRewardedAdCallback;
    private InitializationCompleteCallback mInitializationCallback;

    @Override
    public void initialize(Context context, InitializationCompleteCallback initializationCompleteCallback, List<MediationConfiguration> list) {
        this.mInitializationCallback = initializationCompleteCallback;

        if (HyBid.isInitialized()) {
            this.mInitializationCallback.onInitializationSucceeded();
        }
    }

    @Override
    public void loadRewardedAd(MediationRewardedAdConfiguration adConfiguration,
                               MediationAdLoadCallback<MediationRewardedAd, MediationRewardedAdCallback> mediationAdLoadCallback) {
        if (mediationAdLoadCallback == null) {
            Logger.e(TAG, "mediationAdLoadCallback is null");
            return;
        }
        this.mAdLoadCallback = mediationAdLoadCallback;
        Bundle serverParameters = adConfiguration.getServerParameters();
        String customEventParam = serverParameters.getString("parameter", "");

        String zoneId;
        String appToken;
        if (!TextUtils.isEmpty(HyBidAdmobUtils.getAppToken(customEventParam))
                && !TextUtils.isEmpty(HyBidAdmobUtils.getZoneId(customEventParam))) {
            zoneId = HyBidAdmobUtils.getZoneId(customEventParam);
            appToken = HyBidAdmobUtils.getAppToken(customEventParam);
        } else {
            Logger.e(TAG, "Could not find the required params in MediationRewardedAdConfiguration params");
            mAdLoadCallback.onFailure(new AdError(AdRequest.ERROR_CODE_NETWORK_ERROR,
                    "Could not find the required params in MediationRewardedAdConfiguration params",
                    AdError.UNDEFINED_DOMAIN));
            return;
        }

        if (appToken == null || !appToken.equals(HyBid.getAppToken())) {
            Logger.e(TAG, "The provided app token doesn't match the one used to initialise HyBid");
            mAdLoadCallback.onFailure(new AdError(AdRequest.ERROR_CODE_NETWORK_ERROR,
                    "The provided app token doesn't match the one used to initialise HyBid",
                    AdError.UNDEFINED_DOMAIN));
            return;
        }

        mRewardedAd = new HyBidRewardedAd(adConfiguration.getContext(), zoneId, this);
        mRewardedAd.setMediation(true);
        mRewardedAd.load();
    }

    @Override
    public void showAd(Context context) {
        if (mRewardedAd != null && mRewardedAd.isReady()) {
            mRewardedAd.show();
        } else {
            if (mRewardedAdCallback != null) {
                mRewardedAdCallback.onAdFailedToShow("Rewarded as is not ready to show.");
            }
        }
    }

    @Override
    public VersionInfo getVersionInfo() {
        return new VersionInfo(2, 3, 0);
    }

    @Override
    public VersionInfo getSDKVersionInfo() {
        return new VersionInfo(2, 3, 0);
    }

    //-------------------------------- HyBidRewardedAd Callbacks -----------------------------------

    @Override
    public void onRewardedLoaded() {
        if (mAdLoadCallback != null) {
            this.mRewardedAdCallback = mAdLoadCallback.onSuccess(this);
        }
    }

    @Override
    public void onRewardedLoadFailed(Throwable error) {
        if (mAdLoadCallback != null) {
            mAdLoadCallback.onFailure(new AdError(AdRequest.ERROR_CODE_NO_FILL,
                    "No Fill.",
                    AdError.UNDEFINED_DOMAIN));
        }
    }

    @Override
    public void onRewardedOpened() {
        if (mRewardedAdCallback != null) {
            mRewardedAdCallback.onAdOpened();
            mRewardedAdCallback.onVideoStart();
            mRewardedAdCallback.reportAdImpression();
        }
    }

    @Override
    public void onRewardedClosed() {
        if (mRewardedAdCallback != null) {
            mRewardedAdCallback.onAdClosed();
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

    private static final class HyBidReward implements RewardItem {
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
