package net.pubnative.lite.sdk.utils;

import android.content.Context;
import android.media.AudioManager;

public class SoundUtils {
    private static final String TAG = SoundUtils.class.getSimpleName();

    public boolean isSoundMuted(Context context) {
        try {
            if (context != null) {
                AudioManager audio = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
                if (audio != null) {
                    int volume = audio.getStreamVolume(AudioManager.STREAM_RING);
                    return volume == 0;
                }
                return true;
            }
        } catch (RuntimeException exception) {
            Logger.e(TAG, "Error fetching sound state: ", exception);
        }
        return true;
    }

}
