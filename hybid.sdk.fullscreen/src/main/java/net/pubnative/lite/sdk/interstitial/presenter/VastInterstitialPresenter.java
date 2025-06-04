// HyBid SDK License
//
// https://github.com/pubnative/pubnative-hybid-android-sdk/blob/main/LICENSE
//
package net.pubnative.lite.sdk.interstitial.presenter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import net.pubnative.lite.sdk.CustomEndCardListener;
import net.pubnative.lite.sdk.VideoListener;
import net.pubnative.lite.sdk.interstitial.HyBidInterstitialBroadcastReceiver;
import net.pubnative.lite.sdk.interstitial.activity.HyBidInterstitialActivity;
import net.pubnative.lite.sdk.interstitial.activity.VastInterstitialActivity;
import net.pubnative.lite.sdk.models.Ad;
import net.pubnative.lite.sdk.models.IntegrationType;
import net.pubnative.lite.sdk.utils.CheckUtils;

import org.json.JSONObject;

public class VastInterstitialPresenter implements InterstitialPresenter, HyBidInterstitialBroadcastReceiver.Listener {
    private final Context mContext;
    private final Ad mAd;
    private final String mZoneId;
    private final int mSkipOffset;
    private final HyBidInterstitialBroadcastReceiver mBroadcastReceiver;

    private InterstitialPresenter.Listener mListener;
    private VideoListener mVideoListener;
    private CustomEndCardListener mCustomEndCardListener;
    private boolean mIsDestroyed;
    private boolean mReady = false;

    private IntegrationType mIntegrationType;

    public VastInterstitialPresenter(Context context, Ad ad, String zoneId, int skipOffset, IntegrationType integrationType) {
        mContext = context;
        mAd = ad;
        mZoneId = zoneId;
        mSkipOffset = skipOffset;
        if (context != null && context.getApplicationContext() != null) {
            mBroadcastReceiver = new HyBidInterstitialBroadcastReceiver(context);
            mBroadcastReceiver.setListener(this);
        } else {
            mBroadcastReceiver = null;
        }
        mIntegrationType = integrationType;
    }

    @Override
    public void setListener(Listener listener) {
        mListener = listener;
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
    public Ad getAd() {
        return mAd;
    }

    @Override
    public void load() {
        if (!CheckUtils.NoThrow.checkArgument(!mIsDestroyed, "VastInterstitialPresenter is destroyed")) {
            return;
        }

        mReady = true;
        if (mListener != null) {
            mListener.onInterstitialLoaded(this);
        }
    }

    @Override
    public boolean isReady() {
        return mReady;
    }

    @Override
    public void show() {
        if (!CheckUtils.NoThrow.checkArgument(!mIsDestroyed, "VastInterstitialPresenter is destroyed")) {
            return;
        }

        if (mBroadcastReceiver != null) {
            mBroadcastReceiver.register();
            Intent intent = new Intent(mContext, VastInterstitialActivity.class);
            intent.putExtra(HyBidInterstitialActivity.EXTRA_BROADCAST_ID, mBroadcastReceiver.getBroadcastId());
            intent.putExtra(HyBidInterstitialActivity.EXTRA_ZONE_ID, mZoneId);
            intent.putExtra(HyBidInterstitialActivity.EXTRA_SKIP_OFFSET, mSkipOffset);
            intent.putExtra(HyBidInterstitialActivity.INTEGRATION_TYPE, mIntegrationType.getCode());
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

    //----------------------- Interstitial Broadcast Receiver Callbacks ----------------------------
    @Override
    public void onReceivedAction(HyBidInterstitialBroadcastReceiver.Action action, Bundle extras) {
        mBroadcastReceiver.handleAction(action, extras, this, mListener, mVideoListener, mCustomEndCardListener);
    }
}
