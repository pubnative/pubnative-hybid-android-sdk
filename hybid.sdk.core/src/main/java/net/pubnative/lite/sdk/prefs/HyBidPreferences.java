package net.pubnative.lite.sdk.prefs;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.SharedPreferences;

public class HyBidPreferences {

    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    public HyBidPreferences(Context context) {
        sharedPreferences = context.getSharedPreferences("hybid_prefs_reporting", MODE_PRIVATE);
        if (sharedPreferences != null)
            editor = sharedPreferences.edit();
    }

    public void setAppFirstInstalledTime(String timeDifference) {
        if (!isAppFirstInstalledTracked()) {
            editor.putString(Key.App_FIRST_INSTALLED, timeDifference);
            editor.putBoolean(Key.IS_App_FIRST_INSTALLED_TRACKED, true);
            editor.commit();
        }
    }

    public String getAppFirstInstalledTime() {
        return sharedPreferences.getString(Key.App_FIRST_INSTALLED, "");
    }

    private Boolean isAppFirstInstalledTracked() {
        return sharedPreferences.getBoolean(Key.IS_App_FIRST_INSTALLED_TRACKED, false);
    }

    public static class Key {
        public static final String App_FIRST_INSTALLED = "app_first_installed";
        public static final String IS_App_FIRST_INSTALLED_TRACKED = "is_app_first_installed_tracked";
    }
}