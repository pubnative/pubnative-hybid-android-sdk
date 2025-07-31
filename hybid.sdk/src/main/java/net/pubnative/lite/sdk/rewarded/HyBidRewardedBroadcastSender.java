// HyBid SDK License
//
// https://github.com/pubnative/pubnative-hybid-android-sdk/blob/main/LICENSE
//
package net.pubnative.lite.sdk.rewarded;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import net.pubnative.lite.sdk.utils.PNLocalBroadcastManager;

public class HyBidRewardedBroadcastSender {
    private final long mBroadcastId;
    private final PNLocalBroadcastManager mLocalBroadcastManager;

    public HyBidRewardedBroadcastSender(Context context, long broadcastId) {
        this(broadcastId, PNLocalBroadcastManager.getInstance(context));
    }

    HyBidRewardedBroadcastSender(long broadcastId, PNLocalBroadcastManager localBroadcastManager) {
        mBroadcastId = broadcastId;
        mLocalBroadcastManager = localBroadcastManager;
    }

    public long getBroadcastId() {
        return mBroadcastId;
    }

    public void sendBroadcast(HyBidRewardedBroadcastReceiver.Action action, Bundle extras) {
        final Intent intent = new Intent(action.getId());
        intent.putExtra(HyBidRewardedBroadcastReceiver.BROADCAST_ID, mBroadcastId);

        if (extras != null && !extras.isEmpty()) {
            intent.putExtras(extras);
        }

        mLocalBroadcastManager.sendBroadcast(intent);
    }

    public void sendBroadcast(HyBidRewardedBroadcastReceiver.Action action) {
        final Intent intent = new Intent(action.getId());
        intent.putExtra(HyBidRewardedBroadcastReceiver.BROADCAST_ID, mBroadcastId);

        mLocalBroadcastManager.sendBroadcast(intent);
    }
}
