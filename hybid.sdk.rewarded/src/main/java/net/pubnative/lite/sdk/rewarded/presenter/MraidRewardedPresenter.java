// The MIT License (MIT)
//
// Copyright (c) 2020 PubNative GmbH
//
// Permission is hereby granted, free of charge, to any person obtaining a copy
// of this software and associated documentation files (the "Software"), to deal
// in the Software without restriction, including without limitation the rights
// to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
// copies of the Software, and to permit persons to whom the Software is
// furnished to do so, subject to the following conditions:
//
// The above copyright notice and this permission notice shall be included in all
// copies or substantial portions of the Software.
//
// THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
// IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
// FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
// AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
// LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
// OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
// SOFTWARE.
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
