package net.pubnative.lite.sdk.vpaid.utils;

import android.content.Context;
import android.content.res.AssetManager;
import android.media.AudioManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.view.Gravity;
import android.widget.FrameLayout;

import net.pubnative.lite.sdk.utils.Logger;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class Utils {

    public enum StretchOption {
        NONE,
        STRETCH,
        NO_STRETCH
    }

    private static final String TAG = Utils.class.getSimpleName();

    private static Context sContext;

    private static boolean debugMode = true;

    public static void init(Context context) {
        sContext = context;
    }

    public static void setDebugMode(boolean mode) {
        debugMode = mode;
    }

    public static boolean isDebug() {
        return debugMode;
    }

    public static boolean isOnline() {
        try {
            ConnectivityManager connectivityManager =
                    (ConnectivityManager) sContext.getSystemService(Context.CONNECTIVITY_SERVICE);
            if (connectivityManager == null) {
                return false;
            }
            NetworkInfo activeNetwork = connectivityManager.getActiveNetworkInfo();
            return activeNetwork != null && activeNetwork.isConnected() && activeNetwork.isAvailable();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean isEmulator() {
        return Build.MODEL.contains("google_sdk")
                || Build.MODEL.contains("Emulator")
                || Build.MODEL.contains("Android SDK")
                || Build.MANUFACTURER.contains("Genymotion");
    }

    public static float getSystemVolume() {
        if (sContext == null) {
            return 1.0f;
        }
        AudioManager am = (AudioManager) sContext.getSystemService(Context.AUDIO_SERVICE);
        if (am != null) {
            int volume_level = am.getStreamVolume(AudioManager.STREAM_MUSIC);
            int max = am.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
            int percent = Math.round(volume_level * 100 / max);
            return (float) percent / 100;
        } else {
            return 1.0f;
        }
    }

    public static boolean isPhoneMuted() {
        if (sContext == null) {
            return false;
        }
        AudioManager am = (AudioManager) sContext.getSystemService(Context.AUDIO_SERVICE);
        if (am == null) {
            return false;
        }
        return am.getRingerMode() == AudioManager.RINGER_MODE_SILENT;
    }

    public static FrameLayout.LayoutParams calculateNewLayoutParams(
            FrameLayout.LayoutParams lp,
            int mVideoWidth, int mVideoHeight,
            int mResizeWidth, int mResizeHeight,
            StretchOption mStretch) {

        lp.gravity = Gravity.CENTER;

        int blackLines;
        float percent = 0;

        if (mVideoWidth == mVideoHeight) {
            if (mResizeWidth == mResizeHeight) {
                lp.width = mResizeWidth;
                lp.height = mResizeHeight;
                percent = 0.0f;
            } else if (mResizeWidth > mResizeHeight) {
                lp.height = mResizeHeight;
                lp.width = (int) ((float) mVideoWidth / (float) mVideoHeight * (float) mResizeHeight);

                blackLines = mResizeWidth - lp.width;
                if (lp.width != 0) {
                    percent = blackLines * 100 / lp.width;
                }
            } else {
                lp.width = mResizeWidth;
                lp.height = (int) ((float) mVideoHeight / (float) mVideoWidth * (float) mResizeWidth);

                blackLines = mResizeHeight - lp.height;
                if (lp.height != 0) {
                    percent = blackLines * 100 / lp.height;
                }
            }
        } else if (mVideoWidth > mVideoHeight) {
            lp.width = mResizeWidth;
            lp.height = (int) ((float) mVideoHeight / (float) mVideoWidth * (float) mResizeWidth);

            if (lp.height > mResizeHeight) {
                float factor = (float) mResizeHeight / (float) lp.height;
                lp.height = mResizeHeight;
                lp.width = (int)(lp.width * factor);
            }

            blackLines = mResizeHeight - lp.height;
            if (lp.height != 0) {
                percent = blackLines * 100 / lp.height;
            }
        } else {
            lp.height = mResizeHeight;
            lp.width = (int) ((float) mVideoWidth / (float) mVideoHeight * (float) mResizeHeight);

            if (lp.width > mResizeWidth) {
                float factor = (float) mResizeWidth / (float) lp.width;
                lp.width = mResizeWidth;
                lp.height = (int)(lp.height * factor);
            }

            blackLines = mResizeWidth - lp.width;
            if (lp.width != 0) {
                percent = blackLines * 100 / lp.width;
            }
        }

        switch (mStretch) {
            case NONE:
                if (percent < 11) {
                    lp.width = mResizeWidth;
                    lp.height = mResizeHeight;
                }
                break;

            case STRETCH:
                lp.width = mResizeWidth;
                lp.height = mResizeHeight;
                break;

            case NO_STRETCH:
                //
                break;
        }
        return lp;
    }

    public static String readAssets(AssetManager assetManager, String filename) throws IOException {
        return getStringFromStream(assetManager.open(filename));
    }

    public static String getStringFromStream(InputStream inputStream) throws IOException {
        final int BUFFER_SIZE = 4096;
        ByteArrayOutputStream resultStream = new ByteArrayOutputStream(BUFFER_SIZE);
        byte[] buffer = new byte[BUFFER_SIZE];
        int length;
        while ((length = inputStream.read(buffer)) != -1) {
            resultStream.write(buffer, 0, length);
        }
        return resultStream.toString("UTF-8");
    }

    /**
     * @param duration in format hh:mm:ss
     * @return in seconds
     */
    public static int parseDuration(String duration) {
        try {
            String[] data = duration.split(":");
            int hours = Integer.parseInt(data[0]);
            int minutes = Integer.parseInt(data[1]);
            int seconds = Double.valueOf(data[2]).intValue();
            return seconds + 60 * minutes + 3600 * hours;
        } catch (RuntimeException e) {
            Logger.e(TAG, "Error while parsing ad duration");
        }
        return 10;
    }

    public static int parsePercent(String duration) {
        String progress = duration.replace("%", "").trim();
        return Integer.parseInt(progress);
    }

}
