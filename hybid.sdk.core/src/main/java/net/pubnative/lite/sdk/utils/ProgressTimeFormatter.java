package net.pubnative.lite.sdk.utils;

import java.util.Locale;

public class ProgressTimeFormatter {
    public static String formatSeconds(int totalSecs) {
        int minutes = (totalSecs % 3600) / 60;
        int seconds = totalSecs % 60;

        return String.format(Locale.ENGLISH, "%02d:%02d", minutes, seconds);
    }
}
