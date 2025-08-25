// HyBid SDK License
//
// https://github.com/pubnative/pubnative-hybid-android-sdk/blob/main/LICENSE
//
package net.pubnative.lite.sdk.vpaid.volume;

import java.util.HashSet;
import java.util.Set;

public class VolumeObserver {

    public interface VolumeChangeListener {
        void onVolumeChanged();
    }

    private static VolumeObserver instance;
    private VolumeChangeListener listener;

    private VolumeObserver() {
    }

    public static synchronized VolumeObserver getInstance() {
        if (instance == null) {
            instance = new VolumeObserver();
        }
        return instance;
    }

    public synchronized void setListener(VolumeChangeListener listener) {
        this.listener = listener;
    }

    public synchronized void reset() {
        listener = null;
    }

    public synchronized void notifyObservers() {
        if (this.listener != null)
            listener.onVolumeChanged();
    }
}