// HyBid SDK License
//
// https://github.com/pubnative/pubnative-hybid-android-sdk/blob/main/LICENSE
//
package net.pubnative.lite.sdk.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import net.pubnative.lite.sdk.utils.Logger;
import net.pubnative.lite.sdk.vpaid.volume.VolumeObserver;

public class VolumeChangedActionReceiver extends BroadcastReceiver {

    private static VolumeChangedActionReceiver instance;
    private boolean isRegistered = false;

    public static VolumeChangedActionReceiver getInstance() {
        if (instance == null) {
            instance = new VolumeChangedActionReceiver();
        }
        return instance;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if ("android.media.VOLUME_CHANGED_ACTION".equals(intent.getAction())) {
            notifyVolumeChange();
        }
    }

    protected void notifyVolumeChange() {
        VolumeObserver volumeObserver = VolumeObserver.getInstance();
        volumeObserver.notifyObservers();
    }

    public void register(Context context) {
        if (!isRegistered) {
            IntentFilter filter = new IntentFilter("android.media.VOLUME_CHANGED_ACTION");
            context.registerReceiver(this, filter);
            isRegistered = true;
        }
    }

    public void unregister(Context context) {
        if (isRegistered) {
            try {
                context.unregisterReceiver(this);
                isRegistered = false;
            } catch (IllegalArgumentException e) {
                Logger.e("VolumeChangedActionReceiver", e.toString());
            }
        }
    }
}