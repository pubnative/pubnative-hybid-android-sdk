package net.pubnative.lite.sdk.utils;

import android.content.Context;
import android.media.AudioManager;

public class SoundUtils {

    public boolean isSoundMuted(Context context) {
        if (context != null) {
            AudioManager audio = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
            if (audio != null) {
                int volume = audio.getStreamVolume(AudioManager.STREAM_RING);
                return volume == 0;
            }
            return true;
        }
        return true;
    }

}
