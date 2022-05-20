package net.pubnative.lite.sdk.vpaid.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.AssetManager;
import android.media.AudioManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.text.TextUtils;
import android.view.Gravity;
import android.widget.FrameLayout;

import net.pubnative.lite.sdk.models.ContentInfo;
import net.pubnative.lite.sdk.models.PositionX;
import net.pubnative.lite.sdk.models.PositionY;
import net.pubnative.lite.sdk.utils.Logger;
import net.pubnative.lite.sdk.vpaid.models.vast.Icon;
import net.pubnative.lite.sdk.vpaid.models.vast.IconViewTracking;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class Utils {

    public enum StretchOption {
        NONE,
        STRETCH,
        NO_STRETCH
    }

    private static final String TAG = Utils.class.getSimpleName();

    private static boolean debugMode = true;

    public static void setDebugMode(boolean mode) {
        debugMode = mode;
    }

    public static boolean isDebug() {
        return debugMode;
    }

    @SuppressLint("MissingPermission")
    public static boolean isOnline(Context context) {
        if (context == null) {
            return false;
        }

        try {
            ConnectivityManager connectivityManager =
                    (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
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

    public static float getSystemVolume(Context context) {
        if (context == null) {
            return 1.0f;
        }
        AudioManager am = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        if (am != null) {
            int volume_level = am.getStreamVolume(AudioManager.STREAM_MUSIC);
            int max = am.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
            int percent = Math.round(volume_level * 100.0f / max);
            return (float) percent / 100;
        } else {
            return 1.0f;
        }
    }

    public static boolean isPhoneMuted(Context context) {
        if (context == null) {
            return false;
        }
        AudioManager am = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
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
                    percent = blackLines * 100.0f / lp.width;
                }
            } else {
                lp.width = mResizeWidth;
                lp.height = (int) ((float) mVideoHeight / (float) mVideoWidth * (float) mResizeWidth);

                blackLines = mResizeHeight - lp.height;
                if (lp.height != 0) {
                    percent = blackLines * 100.0f / lp.height;
                }
            }
        } else if (mVideoWidth > mVideoHeight) {
            lp.width = mResizeWidth;
            lp.height = (int) ((float) mVideoHeight / (float) mVideoWidth * (float) mResizeWidth);

            if (lp.height > mResizeHeight) {
                float factor = (float) mResizeHeight / (float) lp.height;
                lp.height = mResizeHeight;
                lp.width = (int) (lp.width * factor);
            }

            blackLines = mResizeHeight - lp.height;
            if (lp.height != 0) {
                percent = blackLines * 100.0f / lp.height;
            }
        } else {
            lp.height = mResizeHeight;
            lp.width = (int) ((float) mVideoWidth / (float) mVideoHeight * (float) mResizeHeight);

            if (lp.width > mResizeWidth) {
                float factor = (float) mResizeWidth / (float) lp.width;
                lp.width = mResizeWidth;
                lp.height = (int) (lp.height * factor);
            }

            blackLines = mResizeWidth - lp.width;
            if (lp.width != 0) {
                percent = blackLines * 100.0f / lp.width;
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

    public static ContentInfo parseContentInfo(Icon icon) {
        if (icon == null) {
            return null;
        }

        String iconUrl = "";
        if (icon.getStaticResources() != null
                && !icon.getStaticResources().isEmpty()
                && !TextUtils.isEmpty(icon.getStaticResources().get(0).getText())) {
            iconUrl = icon.getStaticResources().get(0).getText();
        }

        String clickUrl = "";
        if (icon.getIconClicks() != null
                && icon.getIconClicks().getIconClickThrough() != null
                && !TextUtils.isEmpty(icon.getIconClicks().getIconClickThrough().getText())) {
            clickUrl = icon.getIconClicks().getIconClickThrough().getText();
        }

        List<String> viewTrackers = new ArrayList<>();
        if (icon.getIconViewTrackingList() != null && !icon.getIconViewTrackingList().isEmpty()) {
            for (IconViewTracking tracking : icon.getIconViewTrackingList()) {
                if (!TextUtils.isEmpty(tracking.getText())) {
                    viewTrackers.add(tracking.getText());
                }
            }
        }

        PositionX positionX = PositionX.LEFT;
        PositionY positionY = PositionY.TOP;

        if (!TextUtils.isEmpty(icon.getXPosition()) && icon.getXPosition().equals(PositionX.RIGHT.getValue())) {
            positionX = PositionX.RIGHT;
        }

        if (!TextUtils.isEmpty(icon.getYPosition()) && icon.getYPosition().equals(PositionY.BOTTOM.getValue())) {
            positionY = PositionY.BOTTOM;
        }

        int width = -1;
        int height = -1;

        if (!TextUtils.isEmpty(icon.getWidth()) && !TextUtils.isEmpty(icon.getHeight())) {
            int tempWidth = -1;
            int tempHeight = -1;
            try {
                tempWidth = Integer.parseInt(icon.getWidth());
                tempHeight = Integer.parseInt(icon.getHeight());
            } catch (RuntimeException ignored) {

            }

            // Only use the values if both could be parsed
            if (tempWidth != -1 && tempHeight != -1) {
                width = tempWidth;
                height = tempHeight;
            }
        }

        return TextUtils.isEmpty(iconUrl) || TextUtils.isEmpty(clickUrl) ? null :
                new ContentInfo(iconUrl, clickUrl, "", width, height, positionX, positionY, viewTrackers);
    }
}
