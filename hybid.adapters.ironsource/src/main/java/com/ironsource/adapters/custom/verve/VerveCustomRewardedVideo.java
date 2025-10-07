// HyBid SDK License
//
// https://github.com/pubnative/pubnative-hybid-android-sdk/blob/main/LICENSE
//
package com.ironsource.adapters.custom.verve;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.text.TextUtils;

import com.ironsource.mediationsdk.adunit.adapter.BaseRewardedVideo;
import com.ironsource.mediationsdk.adunit.adapter.listener.RewardedVideoAdListener;
import com.ironsource.mediationsdk.adunit.adapter.utility.AdData;
import com.ironsource.mediationsdk.adunit.adapter.utility.AdapterErrorType;
import com.ironsource.mediationsdk.adunit.adapter.utility.AdapterErrors;
import com.ironsource.mediationsdk.model.NetworkSettings;

import net.pubnative.lite.sdk.HyBid;
import net.pubnative.lite.sdk.VideoListener;
import net.pubnative.lite.sdk.rewarded.HyBidRewardedAd;
import net.pubnative.lite.sdk.utils.Logger;

import org.jetbrains.annotations.NotNull;

public class VerveCustomRewardedVideo extends BaseRewardedVideo<VerveCustomAdapter> implements HyBidRewardedAd.Listener, VideoListener {
    private static final String TAG = VerveCustomRewardedVideo.class.getSimpleName();

    private RewardedVideoAdListener mRewardedVideoAdListener;
    private HyBidRewardedAd mHyBidRewardedAd;

    public VerveCustomRewardedVideo(NetworkSettings networkSettings) {
        super(networkSettings);
    }

    @Override
    public void loadAd(@NotNull AdData adData, @NotNull Context context, @NotNull RewardedVideoAdListener listener) {
        String appToken;
        String zoneID;
        if (!TextUtils.isEmpty(adData.getString(VerveCustomAdapter.KEY_APP_TOKEN))
                && !TextUtils.isEmpty(adData.getString(VerveCustomAdapter.KEY_ZONE_ID))) {
            zoneID = adData.getString(VerveCustomAdapter.KEY_ZONE_ID);
            appToken = adData.getString(VerveCustomAdapter.KEY_APP_TOKEN);
        } else {
            String errorMessage = "Could not find the required params in VerveCustomRewardedVideo ad data. " +
                    "Required params in VerveCustomRewardedVideo ad data must be provided as a valid JSON Object. " +
                    "Please consult HyBid documentation and update settings in your IronSource publisher dashboard.";
            Logger.e(TAG, errorMessage);
            listener.onAdLoadFailed(AdapterErrorType.ADAPTER_ERROR_TYPE_INTERNAL,
                    AdapterErrors.ADAPTER_ERROR_MISSING_PARAMS, errorMessage);
            return;
        }

        mRewardedVideoAdListener = listener;

        if (HyBid.getAppToken() != null && HyBid.getAppToken().equalsIgnoreCase(appToken) && HyBid.isInitialized()) {
            requestRewardedAd(context, zoneID);
        } else {
            HyBid.initialize(appToken, (Application) context.getApplicationContext(), b -> requestRewardedAd(context, zoneID));
        }
    }

    private void requestRewardedAd(Context context, String zoneId) {
        mHyBidRewardedAd = new HyBidRewardedAd(context, zoneId, this);
        mHyBidRewardedAd.setVideoListener(this);
        mHyBidRewardedAd.setMediation(true);
        mHyBidRewardedAd.setMediationVendor(VerveCustomAdapter.IRONSOURCE_MEDIATION_VENDOR);
        mHyBidRewardedAd.load();
    }

    @Override
    public boolean isAdAvailable(AdData adData) {
        if (mHyBidRewardedAd != null) {
            return mHyBidRewardedAd.isReady();
        } else {
            return false;
        }
    }

    @Override
    public void showAd(@NotNull AdData adData, @NotNull Activity activity, @NotNull RewardedVideoAdListener listener) {
        if (mHyBidRewardedAd != null) {
            mHyBidRewardedAd.show();
        }
    }

    @Override
    public void destroyAd(@NotNull AdData adData) {
        if (mHyBidRewardedAd != null) {
            mHyBidRewardedAd.destroy();
        }

        mRewardedVideoAdListener = null;
    }

    //-------------------------------- HyBidRewardedAd Callbacks -----------------------------------
    @Override
    public void onRewardedLoaded() {
        if (mRewardedVideoAdListener != null) {
            mRewardedVideoAdListener.onAdLoadSuccess();
        }
    }

    @Override
    public void onRewardedLoadFailed(Throwable error) {
        if (mRewardedVideoAdListener != null) {
            mRewardedVideoAdListener.onAdLoadFailed(AdapterErrorType.ADAPTER_ERROR_TYPE_NO_FILL,
                    AdapterErrors.ADAPTER_ERROR_INTERNAL, error.getMessage());
        }
    }

    @Override
    public void onRewardedOpened() {
        if (mRewardedVideoAdListener != null) {
            mRewardedVideoAdListener.onAdOpened();
            mRewardedVideoAdListener.onAdVisible();
        }
    }

    @Override
    public void onRewardedClosed() {
        if (mRewardedVideoAdListener != null) {
            mRewardedVideoAdListener.onAdClosed();
        }
    }

    @Override
    public void onRewardedClick() {
        if (mRewardedVideoAdListener != null) {
            mRewardedVideoAdListener.onAdClicked();
        }
    }

    @Override
    public void onReward() {
        if (mRewardedVideoAdListener != null) {
            mRewardedVideoAdListener.onAdRewarded();
        }
    }

    @Override
    public void onVideoStarted() {
        if (mRewardedVideoAdListener != null) {
            mRewardedVideoAdListener.onAdStarted();
        }
    }

    @Override
    public void onVideoError(int progressPercentage) {
        if (mRewardedVideoAdListener != null) {
            mRewardedVideoAdListener.onAdShowFailed(AdapterErrors.ADAPTER_ERROR_INTERNAL, "Error attempting to show HyBid Rewarded Ad");
        }
    }

    @Override
    public void onVideoFinished() {
        if (mRewardedVideoAdListener != null) {
            mRewardedVideoAdListener.onAdEnded();
        }
    }

    @Override
    public void onVideoSkipped() {
    }

    @Override
    public void onVideoDismissed(int progressPercentage) {

    }
}
