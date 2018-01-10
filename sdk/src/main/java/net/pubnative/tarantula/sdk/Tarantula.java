package net.pubnative.tarantula.sdk;

import android.annotation.SuppressLint;
import android.app.Application;
import android.support.annotation.NonNull;
import android.support.annotation.VisibleForTesting;

import net.pubnative.tarantula.sdk.api.ApiClient;
import net.pubnative.tarantula.sdk.managers.SessionDepthManager;

import java.util.Collections;
import java.util.List;

import okhttp3.Interceptor;

/**
 * Created by erosgarciaponte on 05.01.18.
 */

public class Tarantula {
    @NonNull public static final String API_VERSION = "v3";
    @NonNull public static final String SDK_VERSION = "0.1.0";
    @NonNull public static final String HOST = "api.pubnative.net/api/v3/";

    @NonNull private static ApiClient sApiClient;
    @SuppressLint("StaticFieldLeak")
    @NonNull private static DeviceInfo sDeviceInfo;
    @NonNull private static SessionDepthManager sSessionDepthManager;
    @NonNull private static AdCache sAdCache;
    private static boolean sInitialized;

    /**
     * This method must be called to initialize the SDK before request ads.
     */
    public static void initialize(@NonNull Application application) {
        initialize(application, Collections.<Interceptor>emptyList(), Collections.<Interceptor>emptyList());
    }

    /**
     * For testing and debugging purposes only.
     */
    @VisibleForTesting
    public static void initialize(@NonNull Application application,
                                  @NonNull List<Interceptor> applicationInterceptors,
                                  @NonNull List<Interceptor> networkInterceptors) {
        sApiClient = new ApiClient(applicationInterceptors, networkInterceptors);
        sDeviceInfo = new DeviceInfo(application.getApplicationContext());
        sSessionDepthManager = new SessionDepthManager(application);
        sAdCache = new AdCache();
        sInitialized = true;
    }

    @NonNull
    public static ApiClient getApiClient() {
        return sApiClient;
    }

    @NonNull
    public static DeviceInfo getDeviceInfo() {
        return sDeviceInfo;
    }

    @NonNull
    public static SessionDepthManager getSessionDepthManager() {
        return sSessionDepthManager;
    }

    @NonNull
    public static AdCache getAdCache() {
        return sAdCache;
    }

    public static boolean isInitialized() {
        return sInitialized;
    }
}
