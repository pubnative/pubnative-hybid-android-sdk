package net.pubnative.lite.sdk;

import android.annotation.SuppressLint;
import android.app.Application;

import net.pubnative.lite.sdk.api.PNApiClient;
import net.pubnative.lite.sdk.tracking.PNLiteCrashTracker;

/**
 * Created by erosgarciaponte on 05.01.18.
 */

public class PNLite {
    public static final String HOST = "api.pubnative.net/api/v3/";
    public static final String BASE_URL = "https://api.pubnative.net/api/v3/native";

    private static String sAppToken;
    private static PNApiClient sApiClient;
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
    public static void initialize(String appToken,
                                  Application application) {
        PNLiteCrashTracker.init(application, "9ef9d95d69bd0ec31bfa7806af72dddd");
        sAppToken = appToken;
        sBundleId = application.getPackageName();
        sApiClient = new PNApiClient(application);
        sDeviceInfo = new DeviceInfo(application.getApplicationContext());
        sAdCache = new AdCache();
        sInitialized = true;
    }

    public static String getAppToken() {
        return sAppToken;
    }

    public static void setAppToken(String appToken) {
        sAppToken = appToken;
    }

    public static String getBundleId() {
        return sBundleId;
    }

    public static PNApiClient getApiClient() {
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
