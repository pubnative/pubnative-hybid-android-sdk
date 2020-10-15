package net.pubnative.lite.sdk.rewarded.presenter;

import android.content.Context;
import android.content.Intent;

import net.pubnative.lite.sdk.models.Ad;
import net.pubnative.lite.sdk.rewarded.HyBidRewardedBroadcastReceiver;
import net.pubnative.lite.sdk.rewarded.activity.VastRewardedActivity;
import net.pubnative.lite.sdk.utils.CheckUtils;

public class VastRewardedPresenter implements RewardedPresenter, HyBidRewardedBroadcastReceiver.Listener {
    private final Context mContext;
    private final Ad mAd;
    private final String mZoneId;
    private final HyBidRewardedBroadcastReceiver mBroadcastReceiver;

    private RewardedPresenter.Listener mListener;
    private boolean mIsDestroyed;
    private boolean mReady = false;

    public VastRewardedPresenter(Context context, Ad ad, String zoneId) {
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
            intent.putExtra(VastRewardedActivity.EXTRA_BROADCAST_ID, mBroadcastReceiver.getBroadcastId());
            intent.putExtra(VastRewardedActivity.EXTRA_ZONE_ID, mZoneId);
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

    //------------------------- Rewarded Broadcast Receiver Callbacks ------------------------------
    @Override
    public void onReceivedAction(HyBidRewardedBroadcastReceiver.Action action) {
        mBroadcastReceiver.handleAction(action, this, mListener);
    }
}
