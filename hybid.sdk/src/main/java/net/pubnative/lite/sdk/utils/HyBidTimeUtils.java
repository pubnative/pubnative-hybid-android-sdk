// HyBid SDK License
//
// https://github.com/pubnative/pubnative-hybid-android-sdk/blob/main/LICENSE
//
package net.pubnative.lite.sdk.utils;

import java.util.Calendar;
import java.util.Date;

public class HyBidTimeUtils {

    public static final Long SESSION_RENEWAL = 1800000L;

    public HyBidTimeUtils() {

    }

//    private String calculateFormattedTime(long milliseconds) {
//        int seconds = (int) (milliseconds / 1000) % 60;
//        int minutes = (int) ((milliseconds / (1000 * 60)) % 60);
//        int hours = (int) ((milliseconds / (1000 * 60 * 60)) % 24);
//        return String.format(Locale.ENGLISH, "%02d:%02d:%02d", hours, minutes, seconds);
//    }

    public Boolean IsStartingNewSession(long milliseconds) {
        return calculateTimeInMinutes(milliseconds) > 30;
    }

    private int calculateTimeInMinutes(long milliseconds) {
        return (int) ((milliseconds / (1000 * 60)) % 60);
    }

    public long updateExpirationTimeStamp(long milliseconds) {
        //Add 30 minutes to current session
        return milliseconds + SESSION_RENEWAL;
    }

    public Long calculateSessionDuration(Long timestamp, Long ageOfApp) {
        return timestamp - ageOfApp;
    }

    public String getSeconds(long milliseconds) {
        return String.valueOf(milliseconds / 1000L);
    }

    public String getDaysSince(long milliseconds) {
        if (milliseconds <= 0) {
            return "0";
        } else {
            Date installDate = new Date(milliseconds);
            if (Calendar.getInstance().before(installDate)) {
                return "0";
            } else {
                Date currentDate = new Date(System.currentTimeMillis());
                long difference = currentDate.getTime() - installDate.getTime();
                long dates = difference / (24 * 60 * 60 * 1000);
                return String.valueOf(dates);
            }

        }
    }
}