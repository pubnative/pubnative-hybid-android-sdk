package net.pubnative.lite.sdk.vpaid.volumeObserver;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import net.pubnative.lite.sdk.receiver.VolumeChangedActionReceiver;

import java.util.ArrayList;
import java.util.List;

public class VolumeObserver {

    private static VolumeObserver instance;
    private VolumeChangedActionReceiver receiver;

    private final List<IVolumeObserver> observerList;

    private VolumeObserver() {
        observerList = new ArrayList<>();
    }

    public static VolumeObserver getInstance() {
        if (instance == null)
            instance = new VolumeObserver();
        return instance;
    }

    public void registerVolumeObserver(IVolumeObserver iVolumeObserver,Context context) {
        if (!observerList.contains(iVolumeObserver))
            observerList.add(iVolumeObserver);
        registerMediaButtonReceiver(context);
    }

    public void unregisterVolumeObserver(IVolumeObserver iVolumeObserver,Context context) {
         observerList.remove(iVolumeObserver);

        if (observerList.isEmpty()) {
            unregisterMediaButtonReceiver(context);
        }
    }

    public void notifyObservers() {
        for (IVolumeObserver observer : observerList) {
            observer.OnSystemVolumeChanged();
        }
    }

    private void registerMediaButtonReceiver(Context context) {
        if (receiver == null) {
            receiver = new VolumeChangedActionReceiver();
            IntentFilter mediaFilter = new IntentFilter(Intent.ACTION_MEDIA_BUTTON);
            mediaFilter.addAction("android.media.VOLUME_CHANGED_ACTION");
            mediaFilter.addAction("android.intent.action.MEDIA_BUTTON");
            mediaFilter.setPriority(2147483647);
            context.registerReceiver(receiver, mediaFilter);
        }
    }

    private void unregisterMediaButtonReceiver(Context context) {
        if (receiver != null) {
            context.unregisterReceiver(receiver);
            receiver = null;
        }
    }
}