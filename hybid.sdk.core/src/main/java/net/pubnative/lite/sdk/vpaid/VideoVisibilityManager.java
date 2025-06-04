// HyBid SDK License
//
// https://github.com/pubnative/pubnative-hybid-android-sdk/blob/main/LICENSE
//
package net.pubnative.lite.sdk.vpaid;

import java.util.ArrayList;

public class VideoVisibilityManager {

    ArrayList<VideoVisibilityListener> videoVisibilityListeners;

    private static VideoVisibilityManager instance;

    private VideoVisibilityManager() {
        videoVisibilityListeners = new ArrayList<>();
    }

    public static VideoVisibilityManager getInstance() {
        if (instance == null) {
            instance = new VideoVisibilityManager();
        }
        return instance;
    }

    public void addCallback(VideoVisibilityListener listener) {
        videoVisibilityListeners.add(listener);
    }

    public void removeCallback(VideoVisibilityListener listener) {
        videoVisibilityListeners.remove(listener);
    }

    public void reportChange(VideoAdStatus status) {
        for (VideoVisibilityListener listener : videoVisibilityListeners) {
            if (status == VideoAdStatus.PAUSED) listener.pauseAd();
            else listener.resumeAd();
        }
    }

    public enum VideoAdStatus {
        PAUSED, RESUMED
    }
}
