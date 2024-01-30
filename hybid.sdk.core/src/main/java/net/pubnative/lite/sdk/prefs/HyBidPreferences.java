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

    public void setTopicsAPIEnabled(Boolean isEnabled) {
        if (sharedPreferences != null) {
            editor = sharedPreferences.edit();
            editor.putBoolean(Key.TOPICS_API_ENABLED, isEnabled);
            editor.commit();
        }
    }

    public Boolean isTopicsAPIEnabled() {
        return sharedPreferences.getBoolean(Key.TOPICS_API_ENABLED, false);
    }

    public void setSessionTimeStamp(long milliseconds, OnDatabaseResetListener listener, TIMESTAMP timestamp) {
        long sessionTimestamp = getSessionTimeStamp();
        if (sessionTimestamp != 0L) {
            if (new HyBidTimeUtils().IsStartingNewSession(milliseconds - sessionTimestamp)) {
                if (listener != null) listener.onDatabaseReset();
                if (sharedPreferences != null) {
                    editor = sharedPreferences.edit();
                    editor.putLong(Key.SESSION_TIMESTAMP, sessionTimestamp);
                    editor.commit();
                }
            }
        } else {
            if (sharedPreferences != null) {
                editor = sharedPreferences.edit();
                editor.putLong(Key.SESSION_TIMESTAMP, milliseconds);
                editor.commit();
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
        public static final String TOPICS_API_ENABLED = "topics_api_enabled";
    }

    public enum TIMESTAMP {
        NORMAL, AD_REQUEST
    }
}