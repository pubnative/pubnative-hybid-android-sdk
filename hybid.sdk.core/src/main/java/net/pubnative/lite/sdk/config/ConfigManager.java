package net.pubnative.lite.sdk.config;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import net.pubnative.lite.sdk.models.RemoteConfigModel;

public class ConfigManager {
    private static final String TAG = ConfigManager.class.getSimpleName();

    public interface ConfigListener {
        void onConfigFetched();

        void onConfigFetchFailed(Throwable error);
    }

    private static final String PREFERENCES_CONFIG = "net.pubnative.lite.config";
    private static final String KEY_CONFIG_TIMESTAMP = "config_timestamp";
    private final Context mContext;
    private final String mAppToken;
    private final SharedPreferences mPreferences;
    private RemoteConfigModel mConfigModel;


    public ConfigManager(Context context, String appToken) {
        mContext = context.getApplicationContext();
        mAppToken = appToken;
        mPreferences = mContext.getSharedPreferences(PREFERENCES_CONFIG, Context.MODE_PRIVATE);
    }

    public void initialize(final ConfigListener configListener) {
        fetchConfig(configListener);
    }

    public void refreshConfig() {
        if (isConfigOutdated()) {
            fetchConfig(new ConfigListener() {
                @Override
                public void onConfigFetched() {
                    Log.d(TAG, "Config refreshed");
                }

                @Override
                public void onConfigFetchFailed(Throwable error) {
                    Log.d(TAG, "Config refresh failed");
                }
            });
        }
    }

    private void fetchConfig(final ConfigListener configListener) {
        ConfigRequest configRequest = new ConfigRequest();
        configRequest.doRequest(mContext, mAppToken, new ConfigRequest.Listener() {
            @Override
            public void onConfigFetched(RemoteConfigModel configModel) {
                if (configModel == null) {
                    if (configListener != null) {
                        configListener.onConfigFetchFailed(new Exception("The server returned an empty config file."));
                    }
                } else {
                    if (configListener != null) {
                        mConfigModel = configModel;
                        configListener.onConfigFetched();
                        storeConfigTimestamp();
                    }
                }
            }

            @Override
            public void onConfigError(Throwable error) {
                if (configListener != null) {
                    configListener.onConfigFetchFailed(error);
                }
            }
        });
    }

    public RemoteConfigModel getConfig() {
        return mConfigModel;
    }

    public boolean isConfigOutdated() {
        if (mConfigModel != null) {
            long ttlInMillis = (long) mConfigModel.ttl * 1000;
            long configTimestamp = mPreferences.getLong(KEY_CONFIG_TIMESTAMP, 0);
            long timeToUpdate = ttlInMillis + configTimestamp;
            long currentTimestamp = System.currentTimeMillis();

            return currentTimestamp >= timeToUpdate;
        } else {
            return false;
        }
    }

    private void storeConfigTimestamp() {
        long configTimestamp = System.currentTimeMillis();

        SharedPreferences.Editor editor = mPreferences.edit();
        editor.putLong(KEY_CONFIG_TIMESTAMP, configTimestamp);
        editor.apply();
    }

    public FeatureResolver getFeatureResolver() {
        return new FeatureResolver(mConfigModel);
    }
}
