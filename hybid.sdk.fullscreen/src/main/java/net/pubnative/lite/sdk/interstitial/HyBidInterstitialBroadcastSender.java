package net.pubnative.lite.sdk.interstitial;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import net.pubnative.lite.sdk.utils.PNLocalBroadcastManager;

public class HyBidInterstitialBroadcastSender {
    private final long mBroadcastId;
    private final PNLocalBroadcastManager mLocalBroadcastManager;

    public HyBidInterstitialBroadcastSender(Context context, long broadcastId) {
        this(broadcastId, PNLocalBroadcastManager.getInstance(context));
    }

    HyBidInterstitialBroadcastSender(long broadcastId, PNLocalBroadcastManager localBroadcastManager) {
        mBroadcastId = broadcastId;
        mLocalBroadcastManager = localBroadcastManager;
    }

    public long getBroadcastId() {
        return mBroadcastId;
    }

    public void sendBroadcast(HyBidInterstitialBroadcastReceiver.Action action) {
        sendBroadcast(action, null);
    }

    public void sendBroadcast(HyBidInterstitialBroadcastReceiver.Action action, Bundle extras) {
        final Intent intent = new Intent(action.getId());
        intent.putExtra(HyBidInterstitialBroadcastReceiver.BROADCAST_ID, mBroadcastId);

        if (extras != null && !extras.isEmpty()) {
            intent.putExtras(extras);
        }

        mLocalBroadcastManager.sendBroadcast(intent);
    }
}
