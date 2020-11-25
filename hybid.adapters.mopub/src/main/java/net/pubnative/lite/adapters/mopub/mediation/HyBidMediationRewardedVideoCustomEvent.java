package net.pubnative.lite.adapters.mopub.mediation;

import android.app.Activity;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.mopub.common.LifecycleListener;
import com.mopub.common.MoPubReward;
import com.mopub.common.logging.MoPubLog;
import com.mopub.mobileads.AdData;
import com.mopub.mobileads.BaseAd;
import com.mopub.mobileads.MoPubErrorCode;

import net.pubnative.lite.sdk.HyBid;
import net.pubnative.lite.sdk.rewarded.HyBidRewardedAd;
import net.pubnative.lite.sdk.utils.Logger;

public class HyBidMediationRewardedVideoCustomEvent extends BaseAd implements HyBidRewardedAd.Listener {
    private static final String TAG = HyBidMediationRewardedVideoCustomEvent.class.getSimpleName();

    private static final String APP_TOKEN_KEY = "pn_app_token";
    private static final String ZONE_ID_KEY = "pn_zone_id";

    private HyBidRewardedAd mRewardedAd;
    private String mZoneID = "";

    @Override
    protected boolean checkAndInitializeSdk(@NonNull Activity launcherActivity, @NonNull AdData adData) throws Exception {
        return false;
    }

    @Override
    protected void load(@NonNull Context context, @NonNull AdData adData) throws Exception {
        String appToken;
        if (adData.getExtras().containsKey(ZONE_ID_KEY) && adData.getExtras().containsKey(APP_TOKEN_KEY)) {
            mZoneID = adData.getExtras().get(ZONE_ID_KEY);
            appToken = adData.getExtras().get(APP_TOKEN_KEY);
        } else {
            Logger.e(TAG, "Could not find the required params in CustomRewardedVideo serverExtras");
            mLoadListener.onAdLoadFailed(MoPubErrorCode.ADAPTER_CONFIGURATION_ERROR);
            return;
        }

        if (appToken == null || !appToken.equals(HyBid.getAppToken())) {
            Logger.e(TAG, "The provided app token doesn't match the one used to initialise HyBid");
            mLoadListener.onAdLoadFailed(MoPubErrorCode.ADAPTER_CONFIGURATION_ERROR);
            return;
        }

        setAutomaticImpressionAndClickTracking(false);
        mRewardedAd = new HyBidRewardedAd(context, mZoneID, this);
        mRewardedAd.setMediation(true);
        mRewardedAd.load();
        MoPubLog.log(MoPubLog.AdapterLogEvent.LOAD_ATTEMPTED, TAG);
    }

    @Override
    protected void show() {
        if (mRewardedAd != null) {
            mRewardedAd.show();
            MoPubLog.log(MoPubLog.AdapterLogEvent.SHOW_ATTEMPTED, TAG);
        }
    }

    @Override
    protected void onInvalidate() {
        if (mRewardedAd != null) {
            mRewardedAd.destroy();
            mRewardedAd = null;
        }
    }

    @Nullable
    @Override
    protected LifecycleListener getLifecycleListener() {
        return null;
    }

    @NonNull
    @Override
    protected String getAdNetworkId() {
        return mZoneID;
    }





    //--------------------------------- PNRewardedAd Callbacks -------------------------------------
    @Override
    public void onRewardedLoaded() {
        MoPubLog.log(MoPubLog.AdapterLogEvent.LOAD_SUCCESS, TAG);
        mLoadListener.onAdLoaded();
    }

    @Override
    public void onRewardedLoadFailed(Throwable error) {
        MoPubLog.log(MoPubLog.AdapterLogEvent.LOAD_FAILED, TAG);
        mLoadListener.onAdLoadFailed(MoPubErrorCode.NETWORK_NO_FILL);
    }

    @Override
    public void onRewardedOpened() {
        MoPubLog.log(MoPubLog.AdapterLogEvent.SHOW_SUCCESS);
        if (mInteractionListener != null) {
            mInteractionListener.onAdShown();
            mInteractionListener.onAdImpression();
        }
    }

    @Override
    public void onRewardedClosed() {
        MoPubLog.log(MoPubLog.AdapterLogEvent.DID_DISAPPEAR);
        if (mInteractionListener != null) {
            mInteractionListener.onAdDismissed();
        }
    }

    @Override
    public void onRewardedClick() {
        MoPubLog.log(MoPubLog.AdapterLogEvent.CLICKED, TAG);
        if (mInteractionListener != null) {
            mInteractionListener.onAdClicked();
        }
    }

    @Override
    public void onReward() {
        MoPubLog.log(MoPubLog.AdapterLogEvent.SHOULD_REWARD);
        if (mInteractionListener != null) {
            mInteractionListener.onAdComplete(MoPubReward.success("hybid_reward", 0));
        }
    }
}
