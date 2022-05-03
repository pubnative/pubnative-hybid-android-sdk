package net.pubnative.lite.sdk.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import net.pubnative.lite.sdk.vpaid.volume.VolumeObserver;

public class VolumeChangedActionReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals("android.media.VOLUME_CHANGED_ACTION")) {
            notifyVolumeChange();
        }
    }

    protected void notifyVolumeChange() {
        VolumeObserver volumeObserver = VolumeObserver.getInstance();
        volumeObserver.notifyObservers();
    }
}
