// HyBid SDK License
//
// https://github.com/pubnative/pubnative-hybid-android-sdk/blob/main/LICENSE
//
package net.pubnative.lite.sdk.prefs;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.SharedPreferences;

import net.pubnative.lite.sdk.db.OnDatabaseResetListener;
import net.pubnative.lite.sdk.utils.HyBidTimeUtils;

public class HyBidPreferences {

    private final SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    public HyBidPreferences(Context context) {
        sharedPreferences = context.getSharedPreferences("hybid_prefs_reporting", MODE_PRIVATE);
        if (sharedPreferences != null) editor = sharedPreferences.edit();
    }

    public void setAppFirstInstalledTime(String timeDifference) {
        if (!isAppFirstInstalledTracked()) {
            if (sharedPreferences != null) editor = sharedPreferences.edit();
            editor.putString(Key.App_FIRST_INSTALLED, timeDifference);
            editor.putBoolean(Key.IS_App_FIRST_INSTALLED_TRACKED, true);
            editor.commit();
        }
    }

    /**
     * @deprecated The AdServices Topics API integration has been removed.
     * This method is now a no-op and will be removed in a future major release.
     */
    @Deprecated
    public void setTopicsAPIEnabled(Boolean isEnabled) {
        // No-op. Topics API integration has been removed.
    }

    /**
     * @deprecated The AdServices Topics API integration has been removed.
     * Always returns {@code false}. This method will be removed in a future major release.
     */
    @Deprecated
    public Boolean isTopicsAPIEnabled() {
        return false;
    }

    public void setSessionTimeStamp(long milliseconds, OnDatabaseResetListener listener, TIMESTAMP timestamp) {
        long sessionTimestamp = getSessionTimeStamp();
        if (sessionTimestamp != 0L) {
            long gap = milliseconds - sessionTimestamp;
            if (gap < 0) {
                // Stored anchor is in the future (clock moved back / bad RTC): re-anchor to now so rollovers resume.
                // Without this the negative gap never rolls over and impdepth counts forever again.  No reset
                if (sharedPreferences != null) {
                    editor = sharedPreferences.edit();
                    editor.putLong(Key.SESSION_TIMESTAMP, milliseconds);
                    editor.apply();
                }
            } else if (new HyBidTimeUtils().IsStartingNewSession(gap)) {
                if (listener != null) listener.onDatabaseReset();
                if (sharedPreferences != null) {
                    editor = sharedPreferences.edit();
                    editor.putLong(Key.SESSION_TIMESTAMP, milliseconds);
                    // apply() (not commit()) so this anchor write is queued after nukePrefs()'s reset on the shared background writer:
                    // the list clear lands before the anchor and no synchronous disk write runs on the ad-request (main) thread.
                    editor.apply();
                }
            }
        } else {
            if (sharedPreferences != null) {
                editor = sharedPreferences.edit();
                editor.putLong(Key.SESSION_TIMESTAMP, milliseconds);
                editor.apply();
            }
        }
    }

    public String getAppFirstInstalledTime() {
        return sharedPreferences.getString(Key.App_FIRST_INSTALLED, "");
    }

    private Boolean isAppFirstInstalledTracked() {
        return sharedPreferences.getBoolean(Key.IS_App_FIRST_INSTALLED_TRACKED, false);
    }

    public long getSessionTimeStamp() {
        long timestamp = sharedPreferences.getLong(Key.SESSION_TIMESTAMP, 0L);
        return timestamp;
    }

    public static class Key {
        public static final String App_FIRST_INSTALLED = "app_first_installed";
        public static final String SESSION_TIMESTAMP = "session_timestamp";
        public static final String IS_App_FIRST_INSTALLED_TRACKED = "is_app_first_installed_tracked";
        /**
         * @deprecated The AdServices Topics API integration has been removed
         * Retained as an inert constant for source/binary compatibility; will be removed in a future major release.
         */
        @Deprecated
        public static final String TOPICS_API_ENABLED = "topics_api_enabled";
    }

    public enum TIMESTAMP {
        NORMAL, AD_REQUEST
    }
}