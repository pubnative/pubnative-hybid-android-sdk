package net.pubnative.lite.sdk.utils;

import android.content.Context;
import android.media.AudioManager;

import net.pubnative.lite.sdk.HyBid;

public class SoundUtils {
    private static final String TAG = SoundUtils.class.getSimpleName();

    private static long lastCheckedTime = 0;
    private static boolean lastMutedState = true;
    private static final long CACHE_DURATION_MS = 2000;

    public static boolean isSoundMuted(Context context) {
        if (context == null) {
            return true;
        }
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastCheckedTime < CACHE_DURATION_MS) {
            Logger.d(TAG, "Returning lastMutedState");
            return lastMutedState;
        }

        try {
            AudioManager audio = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
            if (audio != null) {
                int volume = audio.getStreamVolume(AudioManager.STREAM_RING);
                lastMutedState = volume == 0;
                lastCheckedTime = currentTime;
                Logger.d(TAG, "Update and return lastMutedState");
                return lastMutedState;
            }
        } catch (SecurityException exception) {
            HyBid.reportException(exception);
            Logger.e(TAG, "Security Error fetching sound state: ", exception);
        } catch (Exception exception) {
            HyBid.reportException(exception);
            Logger.e(TAG, "Error fetching sound state: ", exception);
        }

        return true;
    }
}
