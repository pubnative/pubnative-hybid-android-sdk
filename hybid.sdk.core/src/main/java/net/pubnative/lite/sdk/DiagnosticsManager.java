package net.pubnative.lite.sdk;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.text.TextUtils;

import net.pubnative.lite.sdk.analytics.Reporting;
import net.pubnative.lite.sdk.analytics.ReportingController;
import net.pubnative.lite.sdk.analytics.ReportingEvent;
import net.pubnative.lite.sdk.analytics.ReportingEventCallback;
import net.pubnative.lite.sdk.models.RemoteConfigFeature;
import net.pubnative.lite.sdk.utils.Logger;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.Locale;

public class DiagnosticsManager implements ReportingEventCallback {
    private static final String TAG = DiagnosticsManager.class.getSimpleName();

    private final String googleAdsPackageId;

    public DiagnosticsManager(Context context, ReportingController reportingController) {
        if (context != null) {
            googleAdsPackageId = getGoogleAdsAppId(context);
        } else {
            googleAdsPackageId = "";
        }

        if (reportingController != null) {
            reportingController.addCallback(this);
        }
    }

    @Override
    public void onEvent(ReportingEvent event) {
        if (event != null && !TextUtils.isEmpty(event.getEventType())
                && event.getEventType().equals(Reporting.EventType.SDK_INIT)
                && isDiagnosticsEnabled()) {
            reportInitialisation(event);
        }
    }

    private Boolean isDiagnosticsEnabled() {
        Boolean isEnabled = HyBid.isDiagnosticsEnabled();

        if (!isEnabled)
            isEnabled = HyBid.getConfigManager().getFeatureResolver().isDiagnosticsModeEnabled(RemoteConfigFeature.Reporting.DIAGNOSTIC_REPORT);

        return isEnabled;
    }

    private void reportInitialisation(ReportingEvent event) {
        printDiagnosticsLog(event);
    }

    //Ad format classes
    private static final String FORMAT_BANNER_CLASS = "net.pubnative.lite.sdk.views.HyBidAdView";
    private static final String FORMAT_INTERSTITIAL_CLASS = "net.pubnative.lite.sdk.interstitial.HyBidInterstitialAd";
    private static final String FORMAT_REWARDED_CLASS = "net.pubnative.lite.sdk.rewarded.HyBidRewardedAd";
    private static final String FORMAT_NATIVE_CLASS = "net.pubnative.lite.sdk.request.HyBidNativeAdRequest";

    //Mopub mediation classes
    private static final String MOPUB_MEDIATION_BANNER_ADAPTER_CLASS = "net.pubnative.lite.adapters.mopub.mediation.HyBidMediationBannerCustomEvent";
    private static final String MOPUB_MEDIATION_MRECT_ADAPTER_CLASS = "net.pubnative.lite.adapters.mopub.mediation.HyBidMediationMRectCustomEvent";
    private static final String MOPUB_MEDIATION_LEADERBOARD_ADAPTER_CLASS = "net.pubnative.lite.adapters.mopub.mediation.HyBidMediationLeaderboardCustomEvent";
    private static final String MOPUB_MEDIATION_INTERSTITIAL_ADAPTER_CLASS = "net.pubnative.lite.adapters.mopub.mediation.HyBidMediationInterstitialCustomEvent";
    private static final String MOPUB_MEDIATION_REWARDED_ADAPTER_CLASS = "net.pubnative.lite.adapters.mopub.mediation.HyBidMediationRewardedVideoCustomEvent";
    private static final String MOPUB_MEDIATION_NATIVE_ADAPTER_CLASS = "net.pubnative.lite.adapters.mopub.mediation.HyBidMediationNativeCustomEvent";

    //Mopub header bidding classes
    private static final String MOPUB_HEADER_BIDDING_BANNER_ADAPTER_CLASS = "net.pubnative.lite.adapters.mopub.headerbidding.HyBidHeaderBiddingBannerCustomEvent";
    private static final String MOPUB_HEADER_BIDDING_MRECT_ADAPTER_CLASS = "net.pubnative.lite.adapters.mopub.headerbidding.HyBidHeaderBiddingMRectCustomEvent";
    private static final String MOPUB_HEADER_BIDDING_LEADERBOARD_ADAPTER_CLASS = "net.pubnative.lite.adapters.mopub.headerbidding.HyBidHeaderBiddingLeaderboardCustomEvent";
    private static final String MOPUB_HEADER_BIDDING_INTERSTITIAL_ADAPTER_CLASS = "net.pubnative.lite.adapters.mopub.headerbidding.HyBidHeaderBiddingInterstitialCustomEvent";
    private static final String MOPUB_HEADER_BIDDING_REWARDED_ADAPTER_CLASS = "net.pubnative.lite.adapters.mopub.headerbidding.HyBidHeaderBiddingRewardedCustomEvent";

    //Admob mediation classes
    private static final String ADMOB_MEDIATION_BANNER_ADAPTER_CLASS = "net.pubnative.hybid.adapters.admob.mediation.HyBidMediationBannerCustomEvent";
    private static final String ADMOB_MEDIATION_MRECT_ADAPTER_CLASS = "net.pubnative.hybid.adapters.admob.mediation.HyBidMediationMRectCustomEvent";
    private static final String ADMOB_MEDIATION_LEADERBOARD_ADAPTER_CLASS = "net.pubnative.hybid.adapters.admob.mediation.HyBidMediationLeaderboardCustomEvent";
    private static final String ADMOB_MEDIATION_INTERSTITIAL_ADAPTER_CLASS = "net.pubnative.hybid.adapters.admob.mediation.HyBidMediationInterstitialCustomEvent";
    private static final String ADMOB_MEDIATION_REWARDED_ADAPTER_CLASS = "net.pubnative.hybid.adapters.admob.mediation.HyBidMediationRewardedVideoCustomEvent";
    private static final String ADMOB_MEDIATION_NATIVE_ADAPTER_CLASS = "net.pubnative.hybid.adapters.admob.mediation.HyBidMediationNativeCustomEvent";

    //GAM header bidding classes
    private static final String GAM_HEADER_BIDDING_BANNER_ADAPTER_CLASS = "net.pubnative.lite.adapters.dfp.HyBidDFPBannerCustomEvent";
    private static final String GAM_HEADER_BIDDING_MRECT_ADAPTER_CLASS = "net.pubnative.lite.adapters.dfp.HyBidDFPMRectCustomEvent";
    private static final String GAM_HEADER_BIDDING_LEADERBOARD_ADAPTER_CLASS = "net.pubnative.lite.adapters.dfp.HyBidDFPLeaderboardCustomEvent";
    private static final String GAM_HEADER_BIDDING_INTERSTITIAL_ADAPTER_CLASS = "net.pubnative.lite.adapters.dfp.HyBidDFPInterstitialCustomEvent";

    public void printDiagnosticsLog() {
        Logger.d(TAG, getDiagnosticsLog(null));
    }

    public void printDiagnosticsLog(ReportingEvent event) {
        Logger.d(TAG, getDiagnosticsLog(event));
    }

    private synchronized String getDiagnosticsLog(ReportingEvent event) {
        StringBuilder logBuilder = new StringBuilder();

        logBuilder.append("\nHyBid Diagnostics Log:\n\n");

        if (HyBid.isInitialized()) {
            logBuilder.append("Event: ").append(event.getEventType()).append("\n");
            logBuilder.append("Version: ").append(HyBid.getHyBidVersion()).append("\n");
            logBuilder.append("Bundle Id: ").append(HyBid.getBundleId()).append("\n");
            logBuilder.append("App Token: ").append(HyBid.getAppToken()).append("\n");
            logBuilder.append("Test Mode: ").append(HyBid.isTestMode() ? "true" : "false").append("\n");
            logBuilder.append("COPPA: ").append(HyBid.isCoppaEnabled() ? "true" : "false").append("\n");
            logBuilder.append("Video Audio State: ").append(HyBid.getVideoAudioStatus().getStateName()).append("\n");
            logBuilder.append("Location tracking (if permission): ").append(HyBid.isLocationTrackingEnabled() ? "true" : "false").append("\n");
            logBuilder.append("Location updates (if permission): ").append(HyBid.areLocationUpdatesEnabled() ? "true" : "false").append("\n");
            logBuilder.append("Time: ").append(Calendar.getInstance(Locale.ENGLISH).getTime().toString()).append("\n");
            logBuilder.append("Device OS: ").append("Android").append("\n");
            logBuilder.append("Device OS Version: ").append(Build.VERSION.SDK_INT).append("\n");
            logBuilder.append("Device Model: ").append(Build.MODEL).append("\n");
            logBuilder.append("Device Manufacturer: ").append(Build.MANUFACTURER).append("\n");

            if (!TextUtils.isEmpty(googleAdsPackageId)) {
                logBuilder.append("Google Ads Application Id: ").append(googleAdsPackageId).append("\n");
            }

            logBuilder.append("Available formats:\n").append(getAvailableFormats());
            logBuilder.append("Available adapters:\n").append(getAvailableAdapters());
        } else {
            logBuilder.append("HyBid SDK has not been initialised").append("\n");
        }
        logBuilder.append("\n-----------------------------------------------------------------");

        return logBuilder.toString();
    }

    public static synchronized String generatePlacementDiagnosticsLog(Context context, JSONObject placementParams) {
        StringBuilder logBuilder = new StringBuilder();

        logBuilder.append("\nHyBid Placement Diagnostics Log:\n\n");
        if (placementParams != null && placementParams.length() != 0) {
            try {
                logBuilder.append(placementParams.toString(2));
                logBuilder.append("\n-----------------------------------------------------------------");
            } catch (JSONException jsonException) {
                Logger.e(TAG, "Error parsing placement params: ", jsonException);
                logBuilder.append("Placement data could not be loaded");
                logBuilder.append("\n-----------------------------------------------------------------");
            }
        }
        return logBuilder.toString();
    }

    public void printPlacementDiagnosticsLog(Context context, JSONObject placementParams) {
        if (HyBid.isDiagnosticsEnabled())
            Logger.d(TAG, generatePlacementDiagnosticsLog(context, placementParams));
    }

    private String getAvailableFormats() {
        StringBuilder formatsBuilder = new StringBuilder();

        if (checkAvailableClass(FORMAT_BANNER_CLASS)) {
            formatsBuilder.append("\t").append("Banner").append("\n");
        }
        if (checkAvailableClass(FORMAT_INTERSTITIAL_CLASS)) {
            formatsBuilder.append("\t").append("Interstitial").append("\n");
        }
        if (checkAvailableClass(FORMAT_REWARDED_CLASS)) {
            formatsBuilder.append("\t").append("Rewarded").append("\n");
        }
        if (checkAvailableClass(FORMAT_NATIVE_CLASS)) {
            formatsBuilder.append("\t").append("Native").append("\n");
        }

        if (formatsBuilder.length() == 0) {
            formatsBuilder.append("\t").append("No formats available").append("\n");
        }

        return formatsBuilder.toString();
    }

    private String getAvailableAdapters() {
        StringBuilder adaptersBuilder = new StringBuilder();

        if (checkAvailableClass(MOPUB_MEDIATION_BANNER_ADAPTER_CLASS)) {
            adaptersBuilder.append("\t").append(MOPUB_MEDIATION_BANNER_ADAPTER_CLASS).append("\n");
        }
        if (checkAvailableClass(MOPUB_MEDIATION_MRECT_ADAPTER_CLASS)) {
            adaptersBuilder.append("\t").append(MOPUB_MEDIATION_MRECT_ADAPTER_CLASS).append("\n");
        }
        if (checkAvailableClass(MOPUB_MEDIATION_LEADERBOARD_ADAPTER_CLASS)) {
            adaptersBuilder.append("\t").append(MOPUB_MEDIATION_LEADERBOARD_ADAPTER_CLASS).append("\n");
        }
        if (checkAvailableClass(MOPUB_MEDIATION_INTERSTITIAL_ADAPTER_CLASS)) {
            adaptersBuilder.append("\t").append(MOPUB_MEDIATION_INTERSTITIAL_ADAPTER_CLASS).append("\n");
        }
        if (checkAvailableClass(MOPUB_MEDIATION_REWARDED_ADAPTER_CLASS)) {
            adaptersBuilder.append("\t").append(MOPUB_MEDIATION_REWARDED_ADAPTER_CLASS).append("\n");
        }
        if (checkAvailableClass(MOPUB_MEDIATION_NATIVE_ADAPTER_CLASS)) {
            adaptersBuilder.append("\t").append(MOPUB_MEDIATION_NATIVE_ADAPTER_CLASS).append("\n");
        }

        if (checkAvailableClass(MOPUB_HEADER_BIDDING_BANNER_ADAPTER_CLASS)) {
            adaptersBuilder.append("\t").append(MOPUB_HEADER_BIDDING_BANNER_ADAPTER_CLASS).append("\n");
        }
        if (checkAvailableClass(MOPUB_HEADER_BIDDING_MRECT_ADAPTER_CLASS)) {
            adaptersBuilder.append("\t").append(MOPUB_HEADER_BIDDING_MRECT_ADAPTER_CLASS).append("\n");
        }
        if (checkAvailableClass(MOPUB_HEADER_BIDDING_LEADERBOARD_ADAPTER_CLASS)) {
            adaptersBuilder.append("\t").append(MOPUB_HEADER_BIDDING_LEADERBOARD_ADAPTER_CLASS).append("\n");
        }
        if (checkAvailableClass(MOPUB_HEADER_BIDDING_INTERSTITIAL_ADAPTER_CLASS)) {
            adaptersBuilder.append("\t").append(MOPUB_HEADER_BIDDING_INTERSTITIAL_ADAPTER_CLASS).append("\n");
        }
        if (checkAvailableClass(MOPUB_HEADER_BIDDING_REWARDED_ADAPTER_CLASS)) {
            adaptersBuilder.append("\t").append(MOPUB_HEADER_BIDDING_REWARDED_ADAPTER_CLASS).append("\n");
        }

        if (checkAvailableClass(ADMOB_MEDIATION_BANNER_ADAPTER_CLASS)) {
            adaptersBuilder.append("\t").append(ADMOB_MEDIATION_BANNER_ADAPTER_CLASS).append("\n");
        }
        if (checkAvailableClass(ADMOB_MEDIATION_MRECT_ADAPTER_CLASS)) {
            adaptersBuilder.append("\t").append(ADMOB_MEDIATION_MRECT_ADAPTER_CLASS).append("\n");
        }
        if (checkAvailableClass(ADMOB_MEDIATION_LEADERBOARD_ADAPTER_CLASS)) {
            adaptersBuilder.append("\t").append(ADMOB_MEDIATION_LEADERBOARD_ADAPTER_CLASS).append("\n");
        }
        if (checkAvailableClass(ADMOB_MEDIATION_INTERSTITIAL_ADAPTER_CLASS)) {
            adaptersBuilder.append("\t").append(ADMOB_MEDIATION_INTERSTITIAL_ADAPTER_CLASS).append("\n");
        }
        if (checkAvailableClass(ADMOB_MEDIATION_REWARDED_ADAPTER_CLASS)) {
            adaptersBuilder.append("\t").append(ADMOB_MEDIATION_REWARDED_ADAPTER_CLASS).append("\n");
        }
        if (checkAvailableClass(ADMOB_MEDIATION_NATIVE_ADAPTER_CLASS)) {
            adaptersBuilder.append("\t").append(ADMOB_MEDIATION_NATIVE_ADAPTER_CLASS).append("\n");
        }

        if (checkAvailableClass(GAM_HEADER_BIDDING_BANNER_ADAPTER_CLASS)) {
            adaptersBuilder.append("\t").append(GAM_HEADER_BIDDING_BANNER_ADAPTER_CLASS).append("\n");
        }
        if (checkAvailableClass(GAM_HEADER_BIDDING_MRECT_ADAPTER_CLASS)) {
            adaptersBuilder.append("\t").append(GAM_HEADER_BIDDING_MRECT_ADAPTER_CLASS).append("\n");
        }
        if (checkAvailableClass(GAM_HEADER_BIDDING_LEADERBOARD_ADAPTER_CLASS)) {
            adaptersBuilder.append("\t").append(GAM_HEADER_BIDDING_LEADERBOARD_ADAPTER_CLASS).append("\n");
        }
        if (checkAvailableClass(GAM_HEADER_BIDDING_INTERSTITIAL_ADAPTER_CLASS)) {
            adaptersBuilder.append("\t").append(GAM_HEADER_BIDDING_INTERSTITIAL_ADAPTER_CLASS).append("\n");
        }

        if (adaptersBuilder.length() == 0) {
            adaptersBuilder.append("\t").append("No adapters available").append("\n");
        }

        return adaptersBuilder.toString();
    }

    private boolean checkAvailableClass(String className) {
        try {
            Class.forName(className);
            return true;
        } catch (ClassNotFoundException ignored) {
            return false;
        }
    }

    private String getGoogleAdsAppId(Context context) {
        try {
            ApplicationInfo info = context.getPackageManager().getApplicationInfo(context.getPackageName(), PackageManager.GET_META_DATA);
            if (info != null && info.metaData != null) {
                return info.metaData.getString("com.google.android.gms.ads.APPLICATION_ID");
            } else {
                return "";
            }
        } catch (PackageManager.NameNotFoundException exception) {
            return "";
        }
    }
}
