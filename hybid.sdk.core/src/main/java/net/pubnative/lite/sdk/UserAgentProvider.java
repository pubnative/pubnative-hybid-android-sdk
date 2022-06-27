package net.pubnative.lite.sdk;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.webkit.WebView;

import net.pubnative.lite.sdk.utils.Logger;

public class UserAgentProvider {
    private static final String TAG = UserAgentProvider.class.getSimpleName();
    private static final String PREFERENCES_USER_AGENT = "net.pubnative.lite.useragent";
    private static final String KEY_USER_AGENT_LAST_VERSION = "hybid_user_agent_last_version";
    private static final String KEY_USER_AGENT = "hybid_user_agent";

    private String mUserAgent;

    public void initialise(Context context) {
        fetchUserAgent(context);
    }

    public String getUserAgent() {
        return mUserAgent;
    }

    public void fetchUserAgent(Context context) {
        SharedPreferences preferences = context.getSharedPreferences(PREFERENCES_USER_AGENT, Context.MODE_PRIVATE);
        String userAgent = preferences.getString(KEY_USER_AGENT, "");
        int userAgentVersion = preferences.getInt(KEY_USER_AGENT_LAST_VERSION, -1);

        if (!TextUtils.isEmpty(userAgent) && isValidUserAgent(userAgentVersion)) {
            mUserAgent = userAgent;
        } else {
            Handler mainHandler = new Handler(Looper.getMainLooper());
            mainHandler.post(() -> {
                try {
                    mUserAgent = new WebView(context).getSettings().getUserAgentString();
                    if (!TextUtils.isEmpty(mUserAgent)) {
                        SharedPreferences.Editor editor = preferences.edit();
                        editor.putString(KEY_USER_AGENT, mUserAgent);
                        editor.putInt(KEY_USER_AGENT_LAST_VERSION, Build.VERSION.SDK_INT);
                        editor.apply();
                    }
                } catch (RuntimeException runtimeException) {
                    Logger.e(TAG, runtimeException.getMessage());
                }
            });
        }
    }

    private boolean isValidUserAgent(int version) {
        if (version == -1) {
            return false;
        }

        return version == Build.VERSION.SDK_INT;
    }
}
