package net.pubnative.lite.sdk.vpaid.utils;

import android.content.Context;

import net.pubnative.lite.sdk.utils.Logger;
import net.pubnative.lite.sdk.vpaid.VpaidConstants;

import java.io.File;

public class FileUtils {

    private static final String LOG_TAG = FileUtils.class.getSimpleName();

    public static void deleteExpiredFiles(Context context) {
        File parentDir = getParentDir(context);
        if (parentDir == null) {
            return;
        }
        int amountOfCachedFiles = 0;
        File[] files = parentDir.listFiles();
        if (files != null && files.length > 0) {
            for (File file : files) {
                if (!file.isDirectory()) {
                    long creationTime = file.lastModified();
                    long currentTime = System.currentTimeMillis();
                    if ((creationTime + VpaidConstants.CACHED_VIDEO_LIFE_TIME < currentTime) ||
                            (file.length() == 0)) {
                        //noinspection ResultOfMethodCallIgnored
                        file.delete();
                        Logger.d(LOG_TAG, "Deleted cached file: " + file.getAbsolutePath());
                    } else {
                        amountOfCachedFiles++;
                    }
                }
            }
        }
        Logger.d(LOG_TAG, "In cache " + amountOfCachedFiles + " file(s)");
        long cacheHours = VpaidConstants.CACHED_VIDEO_LIFE_TIME / (1000 * 60 * 60);
        Logger.d(LOG_TAG, "Cache time: " + cacheHours + " hours");
    }

    public static String obtainHashName(String url) {
        int urlHash = url.hashCode();
        // return positive value
        return Long.toString(urlHash & 0xFFFFFFFFL);
    }

    public static File getParentDir(Context context) {
        return context == null ? null : context.getExternalFilesDir(VpaidConstants.FILE_FOLDER);
    }

    public static void clearCache(Context context) {
        Logger.d(LOG_TAG, "Clear cache");
        File parentDir = getParentDir(context);
        if (parentDir == null) {
            return;
        }
        File[] files = parentDir.listFiles();
        int deletedFilesCounter = 0;
        if (files != null && files.length > 0) {
            for (File file : files) {
                if (!file.isDirectory()) {
                    //noinspection ResultOfMethodCallIgnored
                    file.delete();
                    deletedFilesCounter++;
                }
            }
        }
        Logger.d(LOG_TAG, "Deleted " + deletedFilesCounter + " file(s)");
    }
}
