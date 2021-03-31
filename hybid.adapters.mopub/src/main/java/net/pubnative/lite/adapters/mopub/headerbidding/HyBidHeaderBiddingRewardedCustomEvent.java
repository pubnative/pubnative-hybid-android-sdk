package net.pubnative.lite.adapters.mopub.headerbidding;

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
import net.pubnative.lite.sdk.models.Ad;
import net.pubnative.lite.sdk.rewarded.presenter.RewardedPresenter;
import net.pubnative.lite.sdk.rewarded.presenter.RewardedPresenterFactory;
import net.pubnative.lite.sdk.utils.Logger;

public class HyBidHeaderBiddingRewardedCustomEvent extends BaseAd implements RewardedPresenter.Listener {

    private static final String TAG = HyBidHeaderBiddingRewardedCustomEvent.class.getSimpleName();

    private static final String ZONE_ID_KEY = "pn_zone_id";

    private RewardedPresenter mRewardedPresenter;
    private String mZoneID = "";

    @Override
    protected void load(@NonNull Context context, @NonNull AdData adData) throws Exception {
        if (adData.getExtras().containsKey(ZONE_ID_KEY)) {
            mZoneID = adData.getExtras().get(ZONE_ID_KEY);
        } else {
            Logger.e(TAG, "Could not find zone id value in BaseAd adData");
            mLoadListener.onAdLoadFailed(MoPubErrorCode.ADAPTER_CONFIGURATION_ERROR);
            return;
        }

        final Ad ad = HyBid.getAdCache().inspect(mZoneID);
        if (ad == null) {
            Logger.e(TAG, "Could not find an ad in the cache for zone id with key " + mZoneID);
            mLoadListener.onAdLoadFailed(MoPubErrorCode.ADAPTER_CONFIGURATION_ERROR);
            return;
        }

        mRewardedPresenter = new RewardedPresenterFactory(context, mZoneID).createRewardedPresenter(ad, this);
        if (mRewardedPresenter == null) {
            Logger.e(TAG, "Could not create valid interstitial presenter");
            mLoadListener.onAdLoadFailed(MoPubErrorCode.ADAPTER_CONFIGURATION_ERROR);
            return;
        }

        setAutomaticImpressionAndClickTracking(false);
        mRewardedPresenter.load();
        MoPubLog.log(MoPubLog.AdapterLogEvent.LOAD_ATTEMPTED, TAG);
    }

    @Override
    protected void show() {
        if (mRewardedPresenter != null) {
            mRewardedPresenter.show();
            MoPubLog.log(MoPubLog.AdapterLogEvent.SHOW_ATTEMPTED, TAG);
        }
    }

    @Override
    protected void onInvalidate() {
        if (mRewardedPresenter != null) {
            mRewardedPresenter.destroy();
            mRewardedPresenter = null;
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

    @Override
    protected boolean checkAndInitializeSdk(@NonNull Activity launcherActivity, @NonNull AdData adData) throws Exception {
        return false;
    }

    @Override
    public void onRewardedLoaded(RewardedPresenter rewardedPresenter) {
        MoPubLog.log(MoPubLog.AdapterLogEvent.LOAD_SUCCESS, TAG);
        mLoadListener.onAdLoaded();
    }

    @Override
    public void onRewardedError(RewardedPresenter rewardedPresenter) {
        MoPubLog.log(MoPubLog.AdapterLogEvent.DID_DISAPPEAR, TAG);
        mLoadListener.onAdLoadFailed(MoPubErrorCode.INTERNAL_ERROR);
    }

    @Override
    public void onRewardedOpened(RewardedPresenter rewardedPresenter) {
        MoPubLog.log(MoPubLog.AdapterLogEvent.SHOW_SUCCESS, TAG);
        if (mInteractionListener != null) {
            mInteractionListener.onAdShown();
        }
    }

    @Override
    public void onRewardedClicked(RewardedPresenter rewardedPresenter) {
        MoPubLog.log(MoPubLog.AdapterLogEvent.CLICKED, TAG);
        if (mInteractionListener != null) {
            mInteractionListener.onAdClicked();
        }
    }

    @Override
    public void onRewardedClosed(RewardedPresenter rewardedPresenter) {
        MoPubLog.log(MoPubLog.AdapterLogEvent.DID_DISAPPEAR, TAG);
        if (mInteractionListener != null) {
            mInteractionListener.onAdDismissed();
        }
    }

    @Override
    public void onRewardedFinished(RewardedPresenter rewardedPresenter) {
        MoPubLog.log(MoPubLog.AdapterLogEvent.SHOULD_REWARD, TAG);
        if (mInteractionListener != null) {
            mInteractionListener.onAdComplete(MoPubReward.success("hybid_reward", 0));
        }
    }
}
