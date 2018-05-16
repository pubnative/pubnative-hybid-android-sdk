package net.pubnative.lite.demo.managers;

import android.content.Context;

import com.mopub.common.MoPub;
import com.mopub.common.SdkConfiguration;
import com.mopub.common.SdkInitializationListener;

public class MoPubManager {
    public static void initMoPubSdk(Context context, String adUnitId) {
        initMoPubSdk(context, adUnitId, null);
    }

    public static void initMoPubSdk(Context context, String adUnitId, final InitialisationListener listener) {
        SdkConfiguration sdkConfiguration = new SdkConfiguration
                .Builder(adUnitId)
                .build();
        MoPub.initializeSdk(context, sdkConfiguration, new SdkInitializationListener() {
            @Override
            public void onInitializationFinished() {
                if (listener != null) {
                    listener.onInitialisationFinished();
                }
            }
        });
    }

    public interface InitialisationListener {
        void onInitialisationFinished();
    }
}
