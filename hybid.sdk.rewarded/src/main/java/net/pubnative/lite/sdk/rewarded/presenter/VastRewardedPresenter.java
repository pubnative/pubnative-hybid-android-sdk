// HyBid SDK License
//
// https://github.com/pubnative/pubnative-hybid-android-sdk/blob/main/LICENSE
//
package net.pubnative.lite.sdk.rewarded.presenter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import net.pubnative.lite.sdk.CustomEndCardListener;
import net.pubnative.lite.sdk.VideoListener;
import net.pubnative.lite.sdk.models.Ad;
import net.pubnative.lite.sdk.models.IntegrationType;
import net.pubnative.lite.sdk.rewarded.HyBidRewardedBroadcastReceiver;
import net.pubnative.lite.sdk.rewarded.activity.HyBidRewardedActivity;
import net.pubnative.lite.sdk.rewarded.activity.VastRewardedActivity;
import net.pubnative.lite.sdk.utils.CheckUtils;

import org.json.JSONObject;

public class VastRewardedPresenter implements RewardedPresenter, HyBidRewardedBroadcastReceiver.Listener, VideoListener {
    private final Context mContext;
    private final Ad mAd;
    private final String mZoneId;
    private final HyBidRewardedBroadcastReceiver mBroadcastReceiver;

    private RewardedPresenter.Listener mListener;
    private VideoListener mVideoListener;
    private CustomEndCardListener mCustomEndCardListener;
    private boolean mIsDestroyed;
    private boolean mReady = false;
    IntegrationType mIntegrationType;

    public VastRewardedPresenter(Context context, Ad ad, String zoneId, IntegrationType integrationType) {
        mContext = context;
        mAd = ad;
        mZoneId = zoneId;
        if (context != null && context.getApplicationContext() != null) {
            mBroadcastReceiver = new HyBidRewardedBroadcastReceiver(context);
            mBroadcastReceiver.setListener(this);
        } else {
            mBroadcastReceiver = null;
        }
        mIntegrationType = integrationType;
    }

    @Override
    public void setListener(RewardedPresenter.Listener listener) {
        mListener = listener;
    }

    @Override
    public Ad getAd() {
        return mAd;
    }

    @Override
    public void load() {
        if (!CheckUtils.NoThrow.checkArgument(!mIsDestroyed, "VastRewardedPresenter is destroyed")) {
            return;
        }

        mReady = true;
        if (mListener != null) {
            mListener.onRewardedLoaded(this);
        }
    }

    @Override
    public boolean isReady() {
        return mReady;
    }

    @Override
    public void show() {
        if (!CheckUtils.NoThrow.checkArgument(!mIsDestroyed, "VastRewardedPresenter is destroyed")) {
            return;
        }

        if (mBroadcastReceiver != null) {
            mBroadcastReceiver.register();

            Intent intent = new Intent(mContext, VastRewardedActivity.class);
            intent.putExtra(HyBidRewardedActivity.EXTRA_BROADCAST_ID, mBroadcastReceiver.getBroadcastId());
            intent.putExtra(HyBidRewardedActivity.EXTRA_ZONE_ID, mZoneId);
            intent.putExtra(HyBidRewardedActivity.INTEGRATION_TYPE, mIntegrationType.getCode());
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            mContext.startActivity(intent);
        }
    }

    @Override
    public void destroy() {
        if (mBroadcastReceiver != null) {
            mBroadcastReceiver.destroy();
        }
        mListener = null;
        mIsDestroyed = true;
        mReady = false;
    }

    @Override
    public JSONObject getPlacementParams() {
        return null;
    }

    @Override
    public void setVideoListener(VideoListener listener) {
        mVideoListener = listener;
    }

    @Override
    public void setCustomEndCardListener(CustomEndCardListener listener) {
        mCustomEndCardListener = listener;
    }

    @Override
    public void onVideoError(int progressPercentage) {
        if (mVideoListener != null) {
            mVideoListener.onVideoError(progressPercentage);
        }
    }

    @Override
    public void onVideoStarted() {
        if (mVideoListener != null) {
            mVideoListener.onVideoStarted();
        }
    }

    @Override
    public void onVideoDismissed(int progressPercentage) {
        if (mVideoListener != null) {
            mVideoListener.onVideoDismissed(progressPercentage);
        }
    }

    @Override
    public void onVideoFinished() {
        if (mVideoListener != null) {
            mVideoListener.onVideoFinished();
        }
    }

    @Override
    public void onVideoSkipped() {
        if (mVideoListener != null) {
            mVideoListener.onVideoSkipped();
        }
    }


    //------------------------- Rewarded Broadcast Receiver Callbacks ------------------------------
    @Override
    public void onReceivedAction(HyBidRewardedBroadcastReceiver.Action action, Bundle extras) {
        mBroadcastReceiver.handleAction(action, this, extras, mListener, this, mCustomEndCardListener);
    }
}
