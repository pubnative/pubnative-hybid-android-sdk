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
import net.pubnative.lite.sdk.rewarded.HyBidRewardedBroadcastReceiver;
import net.pubnative.lite.sdk.rewarded.activity.HyBidRewardedActivity;
import net.pubnative.lite.sdk.rewarded.activity.MraidRewardedActivity;
import net.pubnative.lite.sdk.utils.CheckUtils;

import org.json.JSONObject;

public class MraidRewardedPresenter implements RewardedPresenter, HyBidRewardedBroadcastReceiver.Listener {
    private final Context mContext;
    private final Ad mAd;
    private final String mZoneId;
    private final HyBidRewardedBroadcastReceiver mBroadcastReceiver;

    private Listener mListener;
    private boolean mIsDestroyed;
    private boolean mReady = false;

    public MraidRewardedPresenter(Context context, Ad ad, String zoneId) {
        mContext = context;
        mAd = ad;
        mZoneId = zoneId;
        if (context != null && context.getApplicationContext() != null) {
            mBroadcastReceiver = new HyBidRewardedBroadcastReceiver(context);
            mBroadcastReceiver.setListener(this);
        } else {
            mBroadcastReceiver = null;
        }
    }

    @Override
    public void setListener(Listener listener) {
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

            Intent intent = new Intent(mContext, MraidRewardedActivity.class);
            intent.putExtra(HyBidRewardedActivity.EXTRA_BROADCAST_ID, mBroadcastReceiver.getBroadcastId());
            intent.putExtra(HyBidRewardedActivity.EXTRA_ZONE_ID, mZoneId);
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
        // Not need video listener for Mraid
    }

    @Override
    public void setCustomEndCardListener(CustomEndCardListener listener) {
        // Not need custom end card listener for Mraid
    }

    //------------------------- Rewarded Broadcast Receiver Callbacks ------------------------------
    @Override
    public void onReceivedAction(HyBidRewardedBroadcastReceiver.Action action, Bundle extras) {
        mBroadcastReceiver.handleAction(action, this, extras, mListener, null, null);
    }
}
