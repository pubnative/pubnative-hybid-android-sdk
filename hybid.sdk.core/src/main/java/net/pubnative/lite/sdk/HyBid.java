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
import android.content.pm.PackageManager;
import android.os.Build;
import android.util.Log;

import net.pubnative.lite.sdk.analytics.CrashController;
import net.pubnative.lite.sdk.analytics.Reporting;
import net.pubnative.lite.sdk.analytics.ReportingController;
import net.pubnative.lite.sdk.analytics.ReportingEvent;
import net.pubnative.lite.sdk.analytics.ReportingEventCallback;
import net.pubnative.lite.sdk.api.SDKConfigAPiClient;
import net.pubnative.lite.sdk.api.PNApiClient;
import net.pubnative.lite.sdk.browser.BrowserManager;
import net.pubnative.lite.sdk.core.BuildConfig;
import net.pubnative.lite.sdk.core.R;
import net.pubnative.lite.sdk.location.HyBidLocationManager;
import net.pubnative.lite.sdk.models.AdRequest;
import net.pubnative.lite.sdk.models.AdSize;
import net.pubnative.lite.sdk.models.IntegrationType;
import net.pubnative.lite.sdk.models.PNAdRequest;
import net.pubnative.lite.sdk.models.PNAdRequestFactory;
import net.pubnative.lite.sdk.prefs.HyBidPreferences;
import net.pubnative.lite.sdk.prefs.SessionImpressionPrefs;
import net.pubnative.lite.sdk.utils.AdTopicsAPIManager;
import net.pubnative.lite.sdk.utils.AtomManager;
import net.pubnative.lite.sdk.utils.Logger;
import net.pubnative.lite.sdk.utils.PNApiUrlComposer;
import net.pubnative.lite.sdk.viewability.ViewabilityManager;
import net.pubnative.lite.sdk.vpaid.VideoAdCache;
import net.pubnative.lite.sdk.vpaid.enums.AudioState;
import net.pubnative.lite.sdk.vpaid.utils.FileUtils;

public class HyBid {
    private static final String TAG = HyBid.class.getSimpleName();

    public static final String OMSDK_VERSION = BuildConfig.OMIDPV;
    public static final String OM_PARTNER_NAME = BuildConfig.OMIDPN;
    public static final String HYBID_VERSION = BuildConfig.SDK_VERSION;

    private static String sAppToken;
    @SuppressLint("StaticFieldLeak")
    private static PNApiClient sApiClient;
    @SuppressLint("StaticFieldLeak")
    private static SDKConfigAPiClient sSDKConfigAPiClient;

    private static DeviceInfo sDeviceInfo;
    @SuppressLint("StaticFieldLeak")
    private static UserDataManager sUserDataManager;
    @SuppressLint("StaticFieldLeak")
    private static ViewabilityManager sViewabilityManager;
    @SuppressLint("StaticFieldLeak")
    private static HyBidLocationManager sLocationManager;
    private static ReportingController sReportingController;

    private static CrashController sCrashController;
    private static DiagnosticsManager sDiagnosticsManager;
    private static AdCache sAdCache;
    private static VideoAdCache sVideoAdCache;
    private static BrowserManager sBrowserManager;
    private static VgiIdManager sVgiIdManager;
    private static TopicManager sTopicManager;
    private static boolean sInitialized;
    private static boolean sCoppaEnabled = false;
    private static boolean sTestMode = false;
    private static boolean sLocationUpdatesEnabled = true;
    private static boolean sLocationTrackingEnabled = true;
    private static boolean isDiagnosticsEnabled = false;
    private static boolean sTopicsApiEnabled = false;
    private static boolean sAtomEnabled = false;
    private static boolean sReportingEnabled = false;
    private static String sAge;
    private static String sGender;
    private static String sKeywords;
    private static String sBundleId;
    private static String sIabCategory;
    private static String sIabSubcategory;
    private static String sAppVersion;
    private static String sDeveloperDomain;
    private static String sContentAgeRating;

    private static Integer skipXmlResource = R.mipmap.skip;

    private static Integer normalCloseXmlResource = -1;
    private static Integer pressedCloseXmlResource = -1;

    private static AudioState sVideoAudioState = AudioState.ON;

    private static final boolean sEventLoggingEndpointEnabled = false;
    private static Boolean mIsSDKConfigFetched = false;

    public static void initialize(String appToken, Application application) {
        initialize(appToken, application, null);
    }

    /**
     * This method must be called to initialize the SDK before request ads.
     */
    public static void initialize(final String appToken, final Application application, final InitialisationListener initialisationListener) {

        sAppToken = appToken;

        long installed;

        try {
            installed = application.getApplicationContext().getPackageManager().getPackageInfo(application.getApplicationContext().getPackageName(), 0).firstInstallTime;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            installed = System.currentTimeMillis();
        }

        HyBidPreferences preferences = new HyBidPreferences(application.getApplicationContext());
        preferences.setAppFirstInstalledTime(String.valueOf(installed));

        preferences.setSessionTimeStamp(System.currentTimeMillis(), () -> {
//            DBManager dbManager = new DBManager(application.getApplicationContext());
//            dbManager.open();
//            dbManager.nukeTable();
//            dbManager.close();
            SessionImpressionPrefs prefs = new SessionImpressionPrefs(application.getApplicationContext());
            prefs.nukePrefs();
        }, HyBidPreferences.TIMESTAMP.NORMAL);

        sBundleId = application.getPackageName();
        sApiClient = new PNApiClient(application);

        //sSDKConfigAPiClient = new SDKConfigAPiClient(application.getApplicationContext());
        //sSDKConfigAPiClient.setAppToken(appToken);

        FileUtils.initParentDirAsync(application.getApplicationContext());

        if (application.getSystemService(Context.LOCATION_SERVICE) != null) {
            sLocationManager = new HyBidLocationManager(application);
            if (isLocationTrackingEnabled() && areLocationUpdatesEnabled()) {
                sLocationManager.startLocationUpdates();
            }
        }
        sUserDataManager = new UserDataManager(application.getApplicationContext());
        sAdCache = new AdCache();

        sVideoAdCache = new VideoAdCache();
        sBrowserManager = new BrowserManager();
        sVgiIdManager = new VgiIdManager(application.getApplicationContext());
        sDiagnosticsManager = new DiagnosticsManager(application.getApplicationContext(), getReportingController());
        sViewabilityManager = new ViewabilityManager(application);

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            Boolean topicsEnabled = AdTopicsAPIManager.isTopicsAPIEnabled(application.getApplicationContext());
            if (topicsEnabled != null && topicsEnabled) {
                setTopicsApiEnabled(true);
                sTopicManager = new TopicManager(application.getApplicationContext());
            }
        }

        if (sCrashController == null) sCrashController = new CrashController();
        if (sDeviceInfo == null) {
            sDeviceInfo = new DeviceInfo(application.getApplicationContext());
            sDeviceInfo.initialize(() -> {
                if (getReportingController() != null && HyBid.isReportingEnabled()) {
                    ReportingEvent event = new ReportingEvent();
                    event.setEventType(Reporting.EventType.SDK_INIT);
                    event.setAppToken(appToken);
                    getReportingController().reportEvent(event);
                }

                if (initialisationListener != null) {
                    initialisationListener.onInitialisationFinished(true);
                }
            });
        } else {
            if (initialisationListener != null) {
                initialisationListener.onInitialisationFinished(true);
            }
        }

        //fetchSDKConfig();

        sInitialized = true;
    }

    private static void fetchSDKConfig() {
        if (!mIsSDKConfigFetched) {
            sSDKConfigAPiClient.fetchConfig(isAtomEnabled -> {
                mIsSDKConfigFetched = true;
                AtomManager.setAtomSDKConfig(isAtomEnabled);
            });
        }
    }

    public static String getHyBidVersion() {
        return HYBID_VERSION;
    }

    public static synchronized String getAppToken() {
        if (!isInitialized()) {
            Log.v(TAG, "HyBid SDK is not initiated yet. Please initiate it before using getAppToken()");
        }
        return sAppToken;
    }

    public static synchronized void setAppToken(String appToken) {
        sAppToken = appToken;
    }

    public static String getBundleId() {
        return sBundleId;
    }

    public static PNApiClient getApiClient() {
        if (!isInitialized()) {
            Log.v(TAG, "HyBid SDK is not initiated yet. Please initiate it before using getApiClient()");
        }
        return sApiClient;
    }

    public static DeviceInfo getDeviceInfo() {
        if (!isInitialized()) {
            Log.v(TAG, "HyBid SDK is not initiated yet. Please initiate it before using getDeviceInfo()");
        }
        return sDeviceInfo;
    }

    public static UserDataManager getUserDataManager() {
        if (!isInitialized()) {
            Log.v(TAG, "HyBid SDK is not initiated yet. Please initiate it before using getUserDataManager()");
        }
        return sUserDataManager;
    }

    public static ViewabilityManager getViewabilityManager() {
        if (!isInitialized()) {
            Log.v(TAG, "HyBid SDK is not initiated yet. Please initiate it before using getViewabilityManager()");
        }
        return sViewabilityManager;
    }

    public static VgiIdManager getVgiIdManager() {
        if (!isInitialized()) {
            Log.v(TAG, "HyBid SDK is not initiated yet. Please initiate it before using getVgiIdManager()");
        }
        return sVgiIdManager;
    }

    public static HyBidLocationManager getLocationManager() {
        if (!isInitialized()) {
            Log.v(TAG, "HyBid SDK is not initiated yet. Please initiate it before using getLocationManager()");
        }
        return sLocationManager;
    }

    public static AdCache getAdCache() {
        if (!isInitialized()) {
            Log.v(TAG, "HyBid SDK is not initiated yet. Please initiate it before using getAdCache()");
        }
        return sAdCache;
    }

    public static synchronized VideoAdCache getVideoAdCache() {
        if (!isInitialized()) {
            Log.v(TAG, "HyBid SDK is not initiated yet. Please initiate it before using getVideoAdCache()");
        }
        return sVideoAdCache;
    }

    public static BrowserManager getBrowserManager() {
        if (!isInitialized()) {
            Log.v(TAG, "HyBid SDK is not initiated yet. Please initiate it before using getBrowserManager()");
        }
        return sBrowserManager;
    }

    public static TopicManager getTopicManager() {
        if (!isInitialized()) {
            Log.v(TAG, "HyBid SDK is not initiated yet. Please initiate it before using getTopicManager()");
        }
        return sTopicManager;
    }

    public static boolean isInitialized() {
        return sInitialized;
    }

    public static boolean isViewabilityMeasurementActivated() {
        return sViewabilityManager != null && sViewabilityManager.isViewabilityMeasurementActivated();
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
        if (sReportingController == null) sReportingController = new ReportingController();
        return sReportingController;
    }

    public static DiagnosticsManager getDiagnosticsManager() {
        return sDiagnosticsManager;
    }

    public static void addReportingCallback(ReportingEventCallback callback) {
        getReportingController().addCallback(callback);
    }

    public static boolean removeReportingCallback(ReportingEventCallback callback) {
        return getReportingController().removeCallback(callback);
    }

    public static void setDiagnosticsEnabled(Boolean enabled) {
        isDiagnosticsEnabled = enabled;
    }

    public static Boolean isDiagnosticsEnabled() {
        return isDiagnosticsEnabled;
    }

    public static void setReportingEnabled(Boolean enabled) {
        sReportingEnabled = enabled;
    }

    public static Boolean isReportingEnabled() {
        return sReportingEnabled;
    }

    public static void setTopicsApiEnabled(Boolean enabled) {
        sTopicsApiEnabled = enabled;
    }

    public static Boolean isTopicsApiEnabled() {
        return sTopicsApiEnabled;
    }

    public static void setAtomEnabled(Boolean enabled) {
        sAtomEnabled = enabled;
    }

    public static Boolean isAtomEnabled() {
        return sAtomEnabled;
    }

    public static void reportException(Exception exception) {
        if (sCrashController != null && getReportingController() != null && HyBid.isReportingEnabled()) {
            ReportingEvent event = sCrashController.formatException(exception);
            getReportingController().reportEvent(event);
        }
    }

    public static void reportException(Throwable exception) {
        if (sCrashController != null && getReportingController() != null && HyBid.isReportingEnabled()) {
            ReportingEvent event = sCrashController.formatException(exception);
            getReportingController().reportEvent(event);
        }
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
     *
     * @Deprecated Please note this method will no longer be supported from HyBid SDK v3.0. While we do not recommend changes to this setting, you can reach out to your account managers for customisations.
     */
    @Deprecated
    public static void setVideoAudioStatus(AudioState audioState) {
        sVideoAudioState = audioState;
    }

    /**
     * This method used to get audio state
     * MUTED
     * ON
     * Default*
     *
     * @Deprecated Please note this method will no longer be supported from HyBid SDK v3.0. While we do not recommend changes to this setting, you can reach out to your account managers for customisations.
     */
    @Deprecated
    public static AudioState getVideoAudioStatus() {
        return sVideoAudioState;
    }

    public static String getCustomRequestSignalData() {
        return getCustomRequestSignalData(null);
    }

    public static String getCustomRequestSignalData(String mediationVendorName) {
        return getCustomRequestSignalData(null, mediationVendorName);
    }

    public static String getCustomRequestSignalData(Context context, String mediationVendorName) {
        PNAdRequestFactory adRequestFactory = new PNAdRequestFactory();
        String url;
        if (HyBid.isInitialized()) {
            AdRequest adRequest = adRequestFactory.buildRequest("", "", AdSize.SIZE_INTERSTITIAL, "", true, IntegrationType.IN_APP_BIDDING, mediationVendorName, 0, false);
            url = PNApiUrlComposer.getUrlQuery(HyBid.getApiClient().getApiUrl(), (PNAdRequest) adRequest);
        } else {
            if (context == null) {
                url = "";
            } else {
                AdRequest adRequest = adRequestFactory.buildRequest(context, "", "", AdSize.SIZE_INTERSTITIAL, "", true, IntegrationType.IN_APP_BIDDING, mediationVendorName, 0, false);
                url = PNApiUrlComposer.getUrlQuery(net.pubnative.lite.sdk.source.pnapi.BuildConfig.BASE_URL, (PNAdRequest) adRequest);
            }
        }

        return url;
    }

    public static String getSDKVersionInfo() {
        return new DisplayManager().getDisplayManagerVersion(IntegrationType.IN_APP_BIDDING);
    }

    public static String getSDKVersionInfo(IntegrationType integrationType) {
        if (integrationType == null) integrationType = IntegrationType.IN_APP_BIDDING;
        return new DisplayManager().getDisplayManagerVersion(integrationType);
    }

    public static void setSkipXmlResource(Integer skipResource) {
        skipXmlResource = skipResource;
    }

    public static Integer getSkipXmlResource() {
        return skipXmlResource;
    }

    public static void setCloseXmlResource(Integer normalCloseResource, Integer pressedCloseResource) {
        normalCloseXmlResource = normalCloseResource;
        pressedCloseXmlResource = pressedCloseResource;
    }

    public static Integer getNormalCloseXmlResource() {
        return normalCloseXmlResource;
    }

    public static Integer getPressedCloseXmlResource() {
        return pressedCloseXmlResource;
    }
}
