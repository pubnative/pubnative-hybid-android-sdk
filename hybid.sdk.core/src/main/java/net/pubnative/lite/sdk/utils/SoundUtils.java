package net.pubnative.lite.sdk.utils;

import android.content.Context;
import android.media.AudioManager;

import net.pubnative.lite.sdk.HyBid;

public class SoundUtils {
    private static final String TAG = SoundUtils.class.getSimpleName();

    public static boolean isSoundMuted(Context context) {
        if (context == null) {
            return true;
        }

        try {
            AudioManager audio = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
            if (audio != null) {
                int volume = audio.getStreamVolume(AudioManager.STREAM_RING);
                return volume == 0;
            }
        } catch (SecurityException exception) {
            HyBid.reportException(exception);
            Logger.e(TAG, "Security error fetching sound state: ", exception);
        } catch (Exception exception) {
            HyBid.reportException(exception);
            Logger.e(TAG, "Error fetching sound state: ", exception);
        }

        return true;
    }


}
