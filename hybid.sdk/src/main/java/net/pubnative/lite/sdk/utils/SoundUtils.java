// HyBid SDK License
//
// https://github.com/pubnative/pubnative-hybid-android-sdk/blob/main/LICENSE
//
package net.pubnative.lite.sdk.utils;

import android.content.Context;
import android.media.AudioManager;
import android.os.Build;

import net.pubnative.lite.sdk.HyBid;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SoundUtils {
    private static final String TAG = SoundUtils.class.getSimpleName();

    private static long lastCheckedTime = 0;
    private static boolean lastMutedState = true;
    private static final long CACHE_DURATION_MS = 2000;
    private static final ExecutorService executor = Executors.newSingleThreadExecutor();
    private static volatile boolean isRefreshing = false;

    public static boolean isSoundMuted(Context context) {
        if (context == null) {
            return true;
        }

        long currentTime = System.currentTimeMillis();
        if (currentTime - lastCheckedTime >= CACHE_DURATION_MS && !isRefreshing) {
            refreshInBackground(context.getApplicationContext());
        }

        return lastMutedState;
    }

    private static void refreshInBackground(Context context) {
        isRefreshing = true;
        executor.execute(() -> {
            try {
                AudioManager audio = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
                if (audio != null) {
                    lastMutedState = checkMuteState(audio);
                    Logger.d(TAG, "Update and return lastMutedState");
                }
            } catch (SecurityException exception) {
                HyBid.reportException(exception);
                Logger.e(TAG, "Security Error fetching sound state: ", exception);
            } catch (Exception exception) {
                HyBid.reportException(exception);
                Logger.e(TAG, "Error fetching sound state: ", exception);
            } catch (OutOfMemoryError error) {
                Logger.e(TAG, "OutOfMemoryError fetching sound state: ", error);
            } finally {
                lastCheckedTime = System.currentTimeMillis();
                isRefreshing = false;
            }
        });
    }

    private static boolean checkMuteState(AudioManager audio) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return audio.isStreamMute(AudioManager.STREAM_RING);
        }

        int ringerMode = audio.getRingerMode();
        return ringerMode == AudioManager.RINGER_MODE_SILENT
                || ringerMode == AudioManager.RINGER_MODE_VIBRATE;
    }
}
