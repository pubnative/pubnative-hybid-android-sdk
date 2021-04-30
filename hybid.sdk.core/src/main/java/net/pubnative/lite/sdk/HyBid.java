// The MIT License (MIT)
//
// Copyright (c) 2020 PubNative GmbH
//
// Permission is hereby granted, free of charge, to any person obtaining a copy
// of this software and associated documentation files (the "Software"), to deal
// in the Software without restriction, including without limitation the rights
// to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
// copies of the Software, and to permit persons to whom the Software is
// furnished to do so, subject to the following conditions:
//
// The above copyright notice and this permission notice shall be included in all
// copies or substantial portions of the Software.
//
// THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
// IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
// FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
// AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
// LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
// OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
// SOFTWARE.
//
package net.pubnative.lite.sdk;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;

import net.pubnative.lite.sdk.analytics.ReportingController;
import net.pubnative.lite.sdk.analytics.ReportingEventCallback;
import net.pubnative.lite.sdk.browser.BrowserManager;
import net.pubnative.lite.sdk.config.ConfigManager;
import net.pubnative.lite.sdk.core.BuildConfig;
import net.pubnative.lite.sdk.api.PNApiClient;
import net.pubnative.lite.sdk.location.HyBidLocationManager;
import net.pubnative.lite.sdk.reporting.ReportingDelegate;
import net.pubnative.lite.sdk.utils.Logger;
import net.pubnative.lite.sdk.viewability.ViewabilityManager;
import net.pubnative.lite.sdk.vpaid.VideoAdCache;
import net.pubnative.lite.sdk.vpaid.enums.AudioState;

public class HyBid {
    private static final String TAG = HyBid.class.getSimpleName();

    public static final String OMSDK_VERSION = BuildConfig.OMIDPV;
    public static final String OM_PARTNER_NAME = BuildConfig.OMIDPN;
    public static final String HYBID_VERSION = BuildConfig.SDK_VERSION;
    private static final String REPORTING_URL = "https://rta-analytics.pubnative.io/event";

    private static String sAppToken;
    @SuppressLint("StaticFieldLeak")
    private static PNApiClient sApiClient;
    @SuppressLint("StaticFieldLeak")
    private static DeviceInfo sDeviceInfo;
    @SuppressLint("StaticFieldLeak")
    private static UserDataManager sUserDataManager;
    @SuppressLint("StaticFieldLeak")
    private static ConfigManager sConfigManager;
    @SuppressLint("StaticFieldLeak")
    private static ViewabilityManager sViewabilityManager;
    @SuppressLint("StaticFieldLeak")
    private static HyBidLocationManager sLocationManager;
    private static ReportingController sReportingController;
    private static AdCache sAdCache;
    private static VideoAdCache sVideoAdCache;
    private static BrowserManager sBrowserManager;
    private static ReportingDelegate sReportingDelegate;
    private static VgiIdManager sVgiIdManager;
    private static boolean sInitialized;
    private static boolean sCoppaEnabled = false;
    private static boolean sTestMode = false;
    private static boolean sLocationUpdatesEnabled = true;
    private static boolean sLocationTrackingEnabled = true;
    private static String sAge;
    private static String sGender;
    private static String sKeywords;
    private static String sBundleId;
    private static Integer sInterstitialSkipOffset = 0;
    private static String sIabCategory;
    private static String sIabSubcategory;
    private static String sAppVersion;
    private static String sDeveloperDomain;
    private static String sContentAgeRating;

    private static AudioState sVideoAudioState = AudioState.DEFAULT;

    public static void initialize(String appToken,
                                  Application application) {
        initialize(appToken, application, null);
    }

    /**
     * This method must be called to initialize the SDK before request ads.
     */
    public static void initialize(String appToken,
                                  Application application, final InitialisationListener initialisationListener) {

        sAppToken = appToken;
        sBundleId = application.getPackageName();
        sApiClient = new PNApiClient(application);
        if (application.getSystemService(Context.LOCATION_SERVICE) != null) {
            sLocationManager = new HyBidLocationManager(application);
            if (isLocationTrackingEnabled() && areLocationUpdatesEnabled()) {
                sLocationManager.startLocationUpdates();
            }
        }
        sDeviceInfo = new DeviceInfo(application.getApplicationContext());
        sUserDataManager = new UserDataManager(application.getApplicationContext(), appToken);
        sConfigManager = new ConfigManager(application.getApplicationContext(), appToken);
        sAdCache = new AdCache();

        sVideoAdCache = new VideoAdCache();
        sBrowserManager = new BrowserManager();
        sVgiIdManager = new VgiIdManager(application.getApplicationContext());
        sReportingController = new ReportingController();
        sViewabilityManager = new ViewabilityManager(application, sReportingController);
        sReportingDelegate = new ReportingDelegate(application.getApplicationContext(), REPORTING_URL);
        sDeviceInfo.initialize(new DeviceInfo.Listener() {
            @Override
            public void onInfoLoaded() {
                /*sConfigManager.initialize(new ConfigManager.ConfigListener() {
                    @Override
                    public void onConfigFetched() {
                        sUserDataManager.initialize(sDeviceInfo.getAdvertisingId(), new UserDataManager.UserDataInitialisationListener() {
                            @Override
                            public void onDataInitialised(boolean success) {
                                if (initialisationListener != null) {
                                    initialisationListener.onInitialisationFinished(success);
                                }
                            }
                        });
                    }

                    @Override
                    public void onConfigFetchFailed(Throwable error) {
                        Logger.e(TAG, "Error fetching config: ", error);
                        sUserDataManager.initialize(sDeviceInfo.getAdvertisingId(), new UserDataManager.UserDataInitialisationListener() {
                            @Override
                            public void onDataInitialised(boolean success) {
                                if (initialisationListener != null) {
                                    initialisationListener.onInitialisationFinished(success);
                                }
                            }
                        });
                    }
                });*/
                sUserDataManager.initialize(sDeviceInfo.getAdvertisingId(), new UserDataManager.UserDataInitialisationListener() {
                    @Override
                    public void onDataInitialised(boolean success) {
                        if (initialisationListener != null) {
                            initialisationListener.onInitialisationFinished(success);
                        }
                    }
                });
            }
        });
        sInitialized = true;
    }

    public static String getHyBidVersion() {
        return HYBID_VERSION;
    }

    public static String getAppToken() {
        return sAppToken;
    }

    public synchronized static void setAppToken(String appToken) {
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

    public static UserDataManager getUserDataManager() {
        return sUserDataManager;
    }

    public static ConfigManager getConfigManager() {
        return sConfigManager;
    }

    public static ViewabilityManager getViewabilityManager() {
        return sViewabilityManager;
    }

    public static VgiIdManager getVgiIdManager() {
        return sVgiIdManager;
    }

    public static HyBidLocationManager getLocationManager() {
        return sLocationManager;
    }

    public static AdCache getAdCache() {
        return sAdCache;
    }

    public synchronized static VideoAdCache getVideoAdCache() {
        return sVideoAdCache;
    }

    public static BrowserManager getBrowserManager() {
        return sBrowserManager;
    }

    public static boolean isInitialized() {
        return sInitialized;
    }

    public static boolean isViewabilityMeasurementActivated() {
        return sViewabilityManager.isViewabilityMeasurementActivated();
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

    public static void setLocationUpdatesEnabled(boolean isEnabled) {
        sLocationUpdatesEnabled = isEnabled;
    }

    public static boolean areLocationUpdatesEnabled() {
        return sLocationUpdatesEnabled;
    }

    public static void setLocationTrackingEnabled(boolean isEnabled) {
        sLocationTrackingEnabled = isEnabled;
    }

    public static boolean isLocationTrackingEnabled() {
        return sLocationTrackingEnabled;
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

    public static void setLogLevel(Logger.Level level) {
        Logger.setLogLevel(level);
    }

    public static ReportingController getReportingController() {
        return sReportingController;
    }

    public static void addReportingCallback(ReportingEventCallback callback) {
        sReportingController.addCallback(callback);
    }

    public static boolean removeReportingCallback(ReportingEventCallback callback) {
        return sReportingController.removeCallback(callback);
    }

    public static void setInterstitialSkipOffset(Integer seconds) {
        if (seconds >= 0)
            sInterstitialSkipOffset = seconds;
    }

    public static Integer getInterstitialSkipOffset() {
        return sInterstitialSkipOffset;
    }

    public interface InitialisationListener {
        void onInitialisationFinished(boolean success);
    }

    public static void setIabCategory(String iabCategory) {
        sIabCategory = iabCategory;
    }

    public static String getIabCategory() {
        return sIabCategory;
    }

    public static void setIabSubcategory(String iabSubcategory) {
        sIabSubcategory = iabSubcategory;
    }

    public static String getsIabSubcategory() {
        return sIabSubcategory;
    }

    public static void setAppVersion(String appVersion) {
        sAppVersion = appVersion;
    }

    public static String getAppVersion() {
        return sAppVersion;
    }

    public static void setDeveloperDomain(String developerDomain) {
        sDeveloperDomain = developerDomain;
    }

    public static String getDeveloperDomain() {
        return sDeveloperDomain;
    }

    public static void setContentAgeRating(String contentAgeRating) {
        sContentAgeRating = contentAgeRating;
    }

    public static String getContentAgeRating() {
        return sContentAgeRating;
    }

    /**
     * This method used to set audio state as one of three values :
     * MUTED
     * ON
     * Default
     */
    public static void setVideoAudioStatus(AudioState audioState) {
        sVideoAudioState = audioState;
    }

    /**
     * This method used to get audio state
     * MUTED
     * ON
     * Default
     */
    public static AudioState getVideoAudioStatus() {
        return sVideoAudioState;
    }
}
