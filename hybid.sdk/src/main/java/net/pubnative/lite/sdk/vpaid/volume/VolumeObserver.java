// HyBid SDK License
//
// https://github.com/pubnative/pubnative-hybid-android-sdk/blob/main/LICENSE
//
package net.pubnative.lite.sdk.vpaid.volume;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;

import net.pubnative.lite.sdk.HyBid;
import net.pubnative.lite.sdk.receiver.VolumeChangedActionReceiver;
import net.pubnative.lite.sdk.utils.Logger;

import java.util.ArrayList;
import java.util.List;

public class VolumeObserver {
    private static final String TAG = VolumeObserver.class.getSimpleName();

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

    public void registerVolumeObserver(IVolumeObserver iVolumeObserver, Context context) {
        if (!observerList.contains(iVolumeObserver))
            observerList.add(iVolumeObserver);
        registerMediaButtonReceiver(context);
    }

    public void unregisterVolumeObserver(IVolumeObserver iVolumeObserver, Context context) {
        observerList.remove(iVolumeObserver);
        if (observerList.isEmpty()) {
            unregisterMediaButtonReceiver(context);
        }
    }

    public void notifyObservers() {
        for (IVolumeObserver observer : observerList) {
            observer.onSystemVolumeChanged();
        }
    }

    @SuppressLint("UnspecifiedRegisterReceiverFlag")
    private void registerMediaButtonReceiver(Context context) {
        if (receiver == null) {
            receiver = new VolumeChangedActionReceiver();
            IntentFilter mediaFilter = new IntentFilter(Intent.ACTION_MEDIA_BUTTON);
            mediaFilter.addAction("android.media.VOLUME_CHANGED_ACTION");
            mediaFilter.setPriority(2147483647);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                context.registerReceiver(receiver, mediaFilter, Context.RECEIVER_NOT_EXPORTED);
            } else {
                context.registerReceiver(receiver, mediaFilter);
            }
        }
    }

    private void unregisterMediaButtonReceiver(Context context) {
        if (receiver != null) {
            try {
                context.unregisterReceiver(receiver);
                receiver = null;
            } catch (RuntimeException exception) {
                HyBid.reportException(exception);
                Logger.e(TAG, exception.getMessage());
            }
        }
    }
}