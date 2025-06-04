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
import net.pubnative.lite.sdk.interstitial.activity.MraidInterstitialActivity;
import net.pubnative.lite.sdk.models.Ad;
import net.pubnative.lite.sdk.utils.CheckUtils;

import org.json.JSONObject;

/**
 * Created by erosgarciaponte on 09.01.18.
 */

public class MraidInterstitialPresenter implements InterstitialPresenter, HyBidInterstitialBroadcastReceiver.Listener {
    private final Context mContext;
    private final Ad mAd;
    private final String mZoneId;
    private final Integer mSkipOffset;
    private final HyBidInterstitialBroadcastReceiver mBroadcastReceiver;

    private InterstitialPresenter.Listener mListener;
    private boolean mIsDestroyed;
    private boolean mReady = false;

    public MraidInterstitialPresenter(Context context, Ad ad, String zoneId, Integer skipOffset) {
        mContext = context;
        mAd = ad;
        mZoneId = zoneId;
        mSkipOffset = skipOffset;
        if (context != null && context.getApplicationContext() != null) {
            mBroadcastReceiver = new HyBidInterstitialBroadcastReceiver(mContext);
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
    public void setVideoListener(VideoListener listener) {
        //Do nothing. Video listener is not needed for MRAID
    }

    @Override
    public void setCustomEndCardListener(CustomEndCardListener listener) {

    }

    @Override
    public Ad getAd() {
        return mAd;
    }

    @Override
    public void load() {
        if (!CheckUtils.NoThrow.checkArgument(!mIsDestroyed, "MraidInterstitialPresenter is destroyed")) {
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
        if (!CheckUtils.NoThrow.checkArgument(!mIsDestroyed, "MraidInterstitialPresenter is destroyed")) {
            return;
        }
        if (mBroadcastReceiver != null) {
            mBroadcastReceiver.register();
            Intent intent = new Intent(mContext, MraidInterstitialActivity.class);
            intent.putExtra(HyBidInterstitialActivity.EXTRA_SKIP_OFFSET, mSkipOffset);
            intent.putExtra(HyBidInterstitialActivity.EXTRA_BROADCAST_ID, mBroadcastReceiver.getBroadcastId());
            intent.putExtra(HyBidInterstitialActivity.EXTRA_ZONE_ID, mZoneId);
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
        mBroadcastReceiver.handleAction(action, extras, this, mListener);
    }
}
