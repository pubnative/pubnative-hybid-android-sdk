package net.pubnative.tarantula.sdk;

import android.annotation.SuppressLint;
import android.app.Application;

import net.pubnative.tarantula.sdk.api.ApiClient;

import java.util.Collections;
import java.util.List;

import okhttp3.Interceptor;

/**
 * Created by erosgarciaponte on 05.01.18.
 */

public class Tarantula {
    public static final String HOST = "api.pubnative.net/api/v3/";

    private static String sAppToken;
    private static ApiClient sApiClient;
    @SuppressLint("StaticFieldLeak")
    private static DeviceInfo sDeviceInfo;
    private static AdCache sAdCache;
    private static boolean sInitialized;
    private static boolean sCoppaEnabled = false;
    private static boolean sTestMode = false;
    private static String sAge;
    private static String sGender;
    private static String sKeywords;
    private static String sBundleId;

    /**
     * This method must be called to initialize the SDK before request ads.
     */
    public static void initialize(String appToken, Application application) {
        initialize(appToken, application, Collections.<Interceptor>emptyList(), Collections.<Interceptor>emptyList());
    }

    /**
     * For testing and debugging purposes only.
     */
    public static void initialize(String appToken,
                                  Application application,
                                  List<Interceptor> applicationInterceptors,
                                  List<Interceptor> networkInterceptors) {
        sAppToken = appToken;
        sBundleId = application.getPackageName();
        sApiClient = new ApiClient(applicationInterceptors, networkInterceptors);
        sDeviceInfo = new DeviceInfo(application.getApplicationContext());
        sAdCache = new AdCache();
        sInitialized = true;
    }

    public static String getAppToken() {
        return sAppToken;
    }

    public static String getBundleId() {
        return sBundleId;
    }

    public static ApiClient getApiClient() {
        return sApiClient;
    }

    public static DeviceInfo getDeviceInfo() {
        return sDeviceInfo;
    }

    public static AdCache getAdCache() {
        return sAdCache;
    }

    public static boolean isInitialized() {
        return sInitialized;
    }

    public static void setCoppaEnabled(boolean isEnabled) {
        sCoppaEnabled = isEnabled;
    }

    public static boolean isCoppaEnabled() {
        return sCoppaEnabled;
    }

    public static void setTestMode(boolean isEnabled) {
        sTestMode = isEnabled;
    }

    public static boolean isTestMode() {
        return sTestMode;
    }

    public static void setAge(String age) {
        sAge = age;
    }

    public static String getAge() {
        return sAge;
    }

    public static void setGender(String gender) {
        sGender = gender;
    }

    public static String getGender() {
        return sGender;
    }

    public static void setKeywords(String keywords) {
        sKeywords = keywords;
    }

    public static String getKeywords() {
        return sKeywords;
    }
}
