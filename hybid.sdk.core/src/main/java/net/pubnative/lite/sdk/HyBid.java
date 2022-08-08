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
import android.text.TextUtils;
import android.util.Log;

import net.pubnative.lite.sdk.analytics.Reporting;
import net.pubnative.lite.sdk.analytics.ReportingController;
import net.pubnative.lite.sdk.analytics.ReportingEvent;
import net.pubnative.lite.sdk.analytics.ReportingEventCallback;
import net.pubnative.lite.sdk.browser.BrowserManager;
import net.pubnative.lite.sdk.config.ConfigManager;
import net.pubnative.lite.sdk.core.BuildConfig;
import net.pubnative.lite.sdk.api.PNApiClient;
import net.pubnative.lite.sdk.location.HyBidLocationManager;
import net.pubnative.lite.sdk.models.AdRequest;
import net.pubnative.lite.sdk.models.AdRequestFactory;
import net.pubnative.lite.sdk.models.AdSize;
import net.pubnative.lite.sdk.models.IntegrationType;
import net.pubnative.lite.sdk.reporting.ReportingDelegate;
import net.pubnative.lite.sdk.utils.Logger;
import net.pubnative.lite.sdk.utils.PNApiUrlComposer;
import net.pubnative.lite.sdk.viewability.ViewabilityManager;
import net.pubnative.lite.sdk.vpaid.VideoAdCache;
import net.pubnative.lite.sdk.vpaid.enums.AudioState;

public class HyBid {
    private static final String TAG = HyBid.class.getSimpleName();

    public static final String OMSDK_VERSION = BuildConfig.OMIDPV;
    public static final String OM_PARTNER_NAME = BuildConfig.OMIDPN;
    public static final String HYBID_VERSION = BuildConfig.SDK_VERSION;

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
    private static DiagnosticsManager sDiagnosticsManager;
    private static AdCache sAdCache;
    private static VideoAdCache sVideoAdCache;
    private static BrowserManager sBrowserManager;
    private static VgiIdManager sVgiIdManager;
    private static boolean sInitialized;
    private static boolean sCoppaEnabled = false;
    private static boolean sTestMode = false;
    private static boolean sLocationUpdatesEnabled = true;
    private static boolean sLocationTrackingEnabled = true;
    private static boolean isCloseVideoAfterFinish = false;
    private static boolean isDiagnosticsEnabled = true;
    private static boolean sMraidExpandEnabled = true;
    private static boolean isEndCardEnabled = false;
    private static String sAge;
    private static String sGender;
    private static String sKeywords;
    private static String sBundleId;
    private static Integer sHtmlInterstitialSkipOffset = -1;
    private static Integer sVideoInterstitialSkipOffset = -1;
    private static Integer sEndCardCloseButtonDelay = -1;
    private static String sContentInfoUrl;
    private static boolean sAdFeedbackEnabled = false;
    private static String sIabCategory;
    private static String sIabSubcategory;
    private static String sAppVersion;
    private static String sDeveloperDomain;
    private static String sContentAgeRating;

    private static InterstitialActionBehaviour sInterstitialActionBehaviour =
            InterstitialActionBehaviour.HB_CREATIVE;


    private static AudioState sVideoAudioState = AudioState.DEFAULT;

    private static final boolean sEventLoggingEndpointEnabled = false;

    public static void initialize(String appToken,
                                  Application application) {
        initialize(appToken, application, null);
    }

    /**
     * This method must be called to initialize the SDK before request ads.
     */
    public static void initialize(final String appToken,
                                  final Application application, final InitialisationListener initialisationListener) {

        sAppToken = appToken;
        sBundleId = application.getPackageName();
        sApiClient = new PNApiClient(application);
        if (application.getSystemService(Context.LOCATION_SERVICE) != null) {
            sLocationManager = new HyBidLocationManager(application);
            if (isLocationTrackingEnabled() && areLocationUpdatesEnabled()) {
                sLocationManager.startLocationUpdates();
            }
        }
        sUserDataManager = new UserDataManager(application.getApplicationContext());
        sConfigManager = new ConfigManager(application.getApplicationContext(), appToken);
        sAdCache = new AdCache();

        sVideoAdCache = new VideoAdCache();
        sBrowserManager = new BrowserManager();
        sVgiIdManager = new VgiIdManager(application.getApplicationContext());
        if (sReportingController == null)
            sReportingController = new ReportingController();
        sDiagnosticsManager = new DiagnosticsManager(application.getApplicationContext(), sReportingController);
        sViewabilityManager = new ViewabilityManager(application);
        ReportingDelegate sReportingDelegate = new ReportingDelegate(application.getApplicationContext(),
                sReportingController, sConfigManager, appToken);

        if (sDeviceInfo == null) {
            sDeviceInfo = new DeviceInfo(application.getApplicationContext());
            sDeviceInfo.initialize(() -> {
                ReportingEvent event = new ReportingEvent();
                event.setEventType(Reporting.EventType.SDK_INIT);
                event.setAppToken(appToken);
                sReportingController.reportEvent(event);

                if (initialisationListener != null) {
                    initialisationListener.onInitialisationFinished(true);
                }
                //Remote config is disabled
            /*sConfigManager.initialize(new ConfigManager.ConfigListener() {
                @Override
                public void onConfigFetched() {
                    // The fetched config will be optionally used during the ad request
                }

                @Override
                public void onConfigFetchFailed(Throwable error) {
                    Logger.d(TAG, "Error fetching config: ", error);
                }
            });*/
            });
        } else {
            if (initialisationListener != null) {
                initialisationListener.onInitialisationFinished(true);
            }
        }

        sInitialized = true;
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

    public static ConfigManager getConfigManager() {
        if (!isInitialized()) {
            Log.v(TAG, "HyBid SDK is not initiated yet. Please initiate it before using getConfigManager()");
        }
        return sConfigManager;
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

    public static void setInterstitialClickBehaviour(InterstitialActionBehaviour interstitialActionBehaviour) {
        sInterstitialActionBehaviour = interstitialActionBehaviour;
    }

    public static InterstitialActionBehaviour getInterstitialClickBehaviour() {
        return sInterstitialActionBehaviour;
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

    public static DiagnosticsManager getDiagnosticsManager() {
        return sDiagnosticsManager;
    }

    public static void addReportingCallback(ReportingEventCallback callback) {
        sReportingController.addCallback(callback);
    }

    public static boolean removeReportingCallback(ReportingEventCallback callback) {
        return sReportingController.removeCallback(callback);
    }

    /**
     * @param seconds amount of seconds until the interstitial ad can be dismissed
     * @deprecated This method is not recommended. Use instead setHtmlInterstitialSkipOffset or
     * setVideoInterstitialSkipOffset to define the offset per ad type
     */
    @Deprecated
    public static void setInterstitialSkipOffset(Integer seconds) {
        setHtmlInterstitialSkipOffset(seconds);
        setVideoInterstitialSkipOffset(seconds);
    }

    public static void setDiagnosticsEnabled(Boolean enabled) {
        isDiagnosticsEnabled = enabled;
    }

    public static Boolean isDiagnosticsEnabled() {
        return isDiagnosticsEnabled;
    }

    public static void setHtmlInterstitialSkipOffset(Integer seconds) {
        if (seconds >= 0)
            sHtmlInterstitialSkipOffset = seconds;
    }

    public static void setVideoInterstitialSkipOffset(Integer seconds) {
        if (seconds >= 0)
            sVideoInterstitialSkipOffset = seconds;
    }

    public static Integer getHtmlInterstitialSkipOffset() {
        return sHtmlInterstitialSkipOffset;
    }

    public static Integer getVideoInterstitialSkipOffset() {
        return sVideoInterstitialSkipOffset;
    }

    public static void setEndCardCloseButtonDelay(int seconds) {
        if (seconds >= 0)
            sEndCardCloseButtonDelay = seconds;
    }

    public static Integer getEndCardCloseButtonDelay() {
        return sEndCardCloseButtonDelay;
    }

    public static void setCloseVideoAfterFinish(boolean isClosing) {
        isCloseVideoAfterFinish = isClosing;
    }

    public static boolean getCloseVideoAfterFinish() {
        return isCloseVideoAfterFinish;
    }

    public static void setMraidExpandEnabled(boolean mraidExpandEnabled) {
        sMraidExpandEnabled = mraidExpandEnabled;
    }

    public static boolean isMraidExpandEnabled() {
        return sMraidExpandEnabled;
    }

    public static void setEndCardEnabled(boolean endCardEnabled) {
        isEndCardEnabled = endCardEnabled;
    }

    public static boolean isEndCardEnabled() {
        return isEndCardEnabled;
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

    public static void setContentInfoUrl(String url) {
        sContentInfoUrl = url;
    }

    public static String getContentInfoUrl() {
        return sContentInfoUrl;
    }

    public static void setAdFeedbackEnabled(boolean feedbackEnabled) {
        sAdFeedbackEnabled = feedbackEnabled;
    }

    public static boolean isAdFeedbackEnabled() {
        return sAdFeedbackEnabled;
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

    public static String getCustomRequestSignalData() {
        return getCustomRequestSignalData(null);
    }

    public static String getCustomRequestSignalData(String mediationVendorName) {
        if (!HyBid.isInitialized()) {
            return "";
        }
        AdRequestFactory adRequestFactory = new AdRequestFactory();
        AdRequest adRequest = adRequestFactory.buildRequest("", "", AdSize.SIZE_INTERSTITIAL, "", true, IntegrationType.IN_APP_BIDDING, mediationVendorName);
        return PNApiUrlComposer.getUrlQuery(HyBid.getApiClient().getApiUrl(), adRequest);
    }

    public static String getSDKVersionInfo() {
        return new DisplayManager().getDisplayManagerVersion();
    }
}
