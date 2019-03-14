package net.pubnative.lite.sdk.vpaid.utils;

import android.content.Context;
import android.content.res.AssetManager;
import android.media.AudioManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.view.Gravity;
import android.widget.FrameLayout;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class Utils {

    public enum StretchOption {
        NONE,
        STRETCH,
        NO_STRETCH
    }

    private static Context sContext;

    private static boolean debugMode = true;

    public static void init(Context context) {
        sContext = context;
    }

    public static String getPackageName() {
        if (sContext == null) {
            return "unknown";
        } else {
            return sContext.getPackageName();
        }
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

    public static FrameLayout.LayoutParams calculateNewLayoutParams(
            FrameLayout.LayoutParams lp,
            int mVideoWidth, int mVideoHeight,
            int mResizeWidth, int mResizeHeight,
            StretchOption mStretch) {

        lp.gravity = Gravity.CENTER;

        int blackLines;
        float percent = 0;

        if (mVideoWidth > mVideoHeight) {
            lp.width = mResizeWidth;
            lp.height = (int) ((float) mVideoHeight / (float) mVideoWidth * (float) mResizeWidth);

            blackLines = mResizeHeight - lp.height;
            if (lp.height != 0) {
                percent = blackLines * 100 / lp.height;
            }
        } else {
            lp.height = mResizeHeight;
            lp.width = (int) ((float) mVideoWidth / (float) mVideoHeight * (float) mResizeHeight);

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
        String[] data = duration.split(":");
        int hours = Integer.parseInt(data[0]);
        int minutes = Integer.parseInt(data[1]);
        int seconds = Integer.parseInt(data[2]);
        return seconds + 60 * minutes + 3600 * hours;
    }

    public static int parsePercent(String duration) {
        String progress = duration.replace("%", "").trim();
        return Integer.parseInt(progress);
    }

}
