// HyBid SDK License
//
// https://github.com/pubnative/pubnative-hybid-android-sdk/blob/main/LICENSE
//
package net.pubnative.lite.sdk.models;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import net.pubnative.lite.sdk.HyBid;
import net.pubnative.lite.sdk.analytics.Reporting;
import net.pubnative.lite.sdk.analytics.ReportingController;
import net.pubnative.lite.sdk.analytics.ReportingEvent;
import net.pubnative.lite.sdk.contentinfo.AdFeedbackFormHelper;
import net.pubnative.lite.sdk.contentinfo.listeners.AdFeedbackLoadListener;
import net.pubnative.lite.sdk.prefs.SessionImpressionPrefs;
import net.pubnative.lite.sdk.utils.Logger;
import net.pubnative.lite.sdk.utils.URLValidator;
import net.pubnative.lite.sdk.views.PNAPIContentInfoView;
import net.pubnative.lite.sdk.views.PNBeaconWebView;
import net.pubnative.lite.sdk.visibility.ImpressionManager;
import net.pubnative.lite.sdk.visibility.ImpressionTracker;
import net.pubnative.lite.sdk.visibility.TrackingManager;
import net.pubnative.lite.sdk.vpaid.helpers.EventTracker;

import java.util.List;
import java.util.Map;

public class NativeAd implements ImpressionTracker.Listener, PNAPIContentInfoView.ContentInfoListener {
    private static final String TAG = NativeAd.class.getSimpleName();
    private ReportingController mReportingController;

    /**
     * Interface definition for callbacks to be invoked when impression confirmed/failed, ad clicked/clickfailed
     */
    public interface Listener {

        /**
         * Called when impression is confirmed
         */
        void onAdImpression(NativeAd ad, View view);

        /**
         * Called when click is confirmed
         */
        void onAdClick(NativeAd ad, View view);
    }

    // Used externally to inject data for tracking
    private Map<String, String> mTrackingExtras;

    protected Ad mAd;
    protected Listener mListener;

    private boolean mIsImpressionConfirmed;
    private View mClickableView;
    private List<String> mUsedAssets;
    private View mAdView;

    private Bitmap bannerBitmap;
    private Bitmap iconBitmap;

    public NativeAd() {
        this.mAd = null;
        mReportingController = HyBid.getReportingController();
    }

    public NativeAd(Ad ad) {
        this.mAd = ad;
        mReportingController = HyBid.getReportingController();
    }

    /**
     * Gets the title string of the ad
     *
     * @return String representation of the ad title, null if not present
     */
    public String getTitle() {
        String result = null;
        AdData data = mAd.getAsset(APIAsset.TITLE);
        if (data != null) {
            result = data.getText();
        }
        return result;
    }

    /**
     * Gets the description string of the ad
     *
     * @return String representation of the ad Description, null if not present
     */
    public String getDescription() {
        String result = null;
        AdData data = mAd.getAsset(APIAsset.DESCRIPTION);
        if (data != null) {
            result = data.getText();
        }
        return result;
    }

    /**
     * Gets the call to action string of the ad
     *
     * @return String representation of the call to action value, null if not present
     */
    public String getCallToActionText() {
        String result = null;
        AdData data = mAd.getAsset(APIAsset.CALL_TO_ACTION);
        if (data != null) {
            result = data.getText();
        }
        return result;
    }

    /**
     * Gets the icon image url of the ad
     *
     * @return valid String with the url value, null if not present
     */
    public String getIconUrl() {
        String result = null;
        AdData data = mAd.getAsset(APIAsset.ICON);
        if (data != null) {
            result = data.getURL();
        }
        return result;
    }

    public Bitmap getIconBitmap() {
        return iconBitmap;
    }

    public void setIconBitmap(Bitmap iconBitmap) {
        this.iconBitmap = iconBitmap;
    }

    /**
     * Gets the banner image url of the ad
     *
     * @return valid String with the url value, null if not present
     */
    public String getBannerUrl() {
        String result = null;
        AdData data = mAd.getAsset(APIAsset.BANNER);
        if (data != null) {
            result = data.getURL();
        }
        return result;
    }

    public Bitmap getBannerBitmap() {
        return bannerBitmap;
    }

    public void setBannerBitmap(Bitmap bannerBitmap) {
        this.bannerBitmap = bannerBitmap;
    }

    /**
     * Gets the click url of the ad
     *
     * @return String value with the url of the click, null if not present
     */
    private String getClickUrl() {
        return injectExtras(mAd.link);
    }

    /**
     * Gets rating of the app in a value from 0 to 5
     *
     * @return int value, 0 if not present
     */
    public int getRating() {
        int result = 0;
        AdData data = mAd.getAsset(APIAsset.RATING);
        if (data != null) {
            Double rating = data.getNumber();
            if (rating != null) {
                result = rating.intValue();
            }
        }
        return result;
    }

    public String getContentInfoIconUrl() {
        return mAd.getContentInfoIconUrl();
    }

    public String getContentInfoClickUrl() {
        return mAd.getContentInfoClickUrl();
    }

    public String getContentInfoText() {
        return mAd.getContentInfoText();
    }

    public View getContentInfo(Context context) {
        return mAd.getContentInfo(context, this);
    }

    public String getImpressionId() {
        return mAd != null ? mAd.getImpressionId() : null;
    }

    public String getCreativeId() {
        return mAd != null ? mAd.getCreativeId() : null;
    }

    public Integer getBidPoints() {
        return mAd != null ? mAd.getECPM() : 0;
    }

    // Used to inject extra data in urls
    private String injectExtras(String url) {
        String result = url;
        if (!TextUtils.isEmpty(url) && mTrackingExtras != null && mTrackingExtras.size() > 0) {
            Uri.Builder builder = Uri.parse(url).buildUpon();
            for (Map.Entry<String, String> entry : mTrackingExtras.entrySet()) {
                builder.appendQueryParameter(entry.getKey(), entry.getKey());
            }
            result = builder.build().toString();
        }
        return result;
    }

    /**
     * Start tracking of ad view to auto confirm impressions and handle clicks
     *
     * @param view     ad view
     * @param listener listener for callbacks
     */
    public void startTracking(View view, Listener listener) {
        startTracking(view, view, listener);
    }

    public void startTracking(View view, View clickableView, Listener listener) {
        startTracking(view, clickableView, null, listener);
    }

    /**
     * Start tracking of ad view to auto confirm impressions and handle clicks
     *
     * @param view          ad view
     * @param clickableView clickable view
     * @param extras        tracking Extras
     * @param listener      listener for callbacks
     */
    public void startTracking(View view, View clickableView, Map<String, String> extras, Listener listener) {
        if (listener == null) {
            Log.w(TAG, "startTracking - listener is null, start tracking without callbacks");
        }

        mListener = listener;
        mTrackingExtras = extras;

        stopTracking();

        startTrackingImpression(view);
        startTrackingClicks(clickableView);
    }

    private void startTrackingImpression(View view) {
        if (view == null) {
            Log.w(TAG, "ad view is null, cannot start tracking");
        } else if (mIsImpressionConfirmed) {
            Log.i(TAG, "impression is already confirmed, dropping impression tracking");
        } else {
            mAdView = view;
            ImpressionManager.startTrackingView(view, mAd.getImpressionMinVisibleTime(), mAd.getImpressionVisiblePercent(), this);
        }
    }

    private void startTrackingClicks(View clickableView) {
        if (TextUtils.isEmpty(getClickUrl())) {
            Log.w(TAG, "click url is empty, clicks won't be tracked");
        } else if (clickableView == null) {
            Log.w(TAG, "click view is null, clicks won't be tracked");
        } else {
            mClickableView = clickableView;
            mClickableView.setOnClickListener(this::onNativeClick);
        }
    }

    /**
     * stop tracking of ad view
     */
    public void stopTracking() {
        stopTrackingImpression();
        stopTrackingClicks();
    }

    private void stopTrackingImpression() {
        ImpressionManager.stopTrackingAll(this);
    }

    private void stopTrackingClicks() {
        if (mClickableView != null) {
            mClickableView.setOnClickListener(null);
        }
    }

    protected void openURL(String urlString, boolean mediationClick) {
        if (TextUtils.isEmpty(urlString)) {
            Log.w(TAG, "Error: ending URL cannot be opened - " + urlString);
        } else if (!mediationClick && mClickableView == null) {
            Log.w(TAG, "Error: clickable view not set");
        } else {
            try {
                Uri uri = Uri.parse(urlString);
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                if (mediationClick && mAdView != null) {
                    mAdView.getContext().startActivity(intent);
                } else {
                    mClickableView.getContext().startActivity(intent);
                }
            } catch (Exception ex) {
                Log.w(TAG, "openURL: Error - " + ex.getMessage());
            }
        }
    }

    private void confirmImpressionBeacons(Context context) {
        // 1. Track assets
        if (mUsedAssets != null) {
            for (String asset : mUsedAssets) {
                TrackingManager.track(context, asset);
            }
        }
        // 2. Track impressions
        confirmBeacons(Ad.Beacon.IMPRESSION, context);
    }

    private void confirmClickBeacons(Context context) {
        confirmBeacons(Ad.Beacon.CLICK, context);
    }

    private void confirmBeacons(String beaconType, Context context) {
        if (mAd == null) {
            Log.w(TAG, "confirmBeacons - Error: ad data not present");
            return;
        }

        List<AdData> beacons = mAd.getBeacons(beaconType);
        if (beacons == null) {
            return;
        }

        for (AdData beaconData : beacons) {
            String beaconURL = injectExtras(beaconData.getURL());
            String beaconJS = beaconData.getStringField("js");
            if (!TextUtils.isEmpty(beaconURL)) {
                // URL
                TrackingManager.track(context, beaconURL);
            } else if (!TextUtils.isEmpty(beaconJS)) {
                try {
                    new PNBeaconWebView(context).loadBeacon(beaconJS);
                } catch (Exception e) {
                    Log.e(TAG, "confirmImpressionBeacons - JS Error: " + e);
                }
            }
        }
    }

    //==============================================================================================
    // Listener helpers
    //==============================================================================================

    public synchronized void invokeOnImpression(View view) {
        mIsImpressionConfirmed = true;
        if (view != null && view.getContext() != null) {
//            DBManager dbManager = new DBManager(view.getContext());
//            dbManager.open();
//            dbManager.insert(mAd.getZoneId());
//            dbManager.close();

            SessionImpressionPrefs prefs = new SessionImpressionPrefs(view.getContext());
            prefs.insert(mAd.getZoneId());

            if (mListener != null) {
                mListener.onAdImpression(NativeAd.this, view);
            }
        }
    }

    protected void invokeOnClick(View view) {
        if (mListener != null) {
            mListener.onAdClick(NativeAd.this, view);
        }
    }

    //==============================================================================================
    // PNAPIImpressionTracker.Listener
    //----------------------------------------------------------------------------------------------

    @Override
    public void onImpression(View visibleView) {
        confirmImpressionBeacons(visibleView.getContext());
        invokeOnImpression(visibleView);
    }

    public void onNativeClick() {
        if (mAdView != null) {
            confirmClickBeacons(mAdView.getContext());
            openURL(getClickUrl(), true);
        }
    }

    public void onNativeClick(View view) {
        invokeOnClick(view);
        confirmClickBeacons(view.getContext());
        openURL(getClickUrl(), false);
    }

    // Content info listener
    @Override
    public void onIconClicked(List<String> clickTrackers) {
        if (clickTrackers != null && !clickTrackers.isEmpty()) {
            for (int i = 0; i < clickTrackers.size(); i++) {
                EventTracker.post(mAdView.getContext(), clickTrackers.get(i), null, false);
            }
        }
        invokeOnContentInfoClick();
    }

    private void invokeOnContentInfoClick() {
        if (mReportingController != null && HyBid.isReportingEnabled()) {
            ReportingEvent reportingEvent = new ReportingEvent();
            reportingEvent.setEventType(Reporting.EventType.CONTENT_INFO_CLICK);
            reportingEvent.setTimestamp(System.currentTimeMillis());
            reportingEvent.setAdFormat(Reporting.AdFormat.NATIVE);
            reportingEvent.setPlatform(Reporting.Platform.ANDROID);
            if (mAd != null) {
                reportingEvent.setImpId(mAd.getSessionId());
                reportingEvent.setCampaignId(mAd.getCampaignId());
                reportingEvent.setConfigId(mAd.getConfigId());
            }
            mReportingController.reportEvent(reportingEvent);
        }
    }

    String processedURL = "";

    @Override
    public synchronized void onLinkClicked(String url) {
        if (mAdView != null && mAdView.getContext() != null && !isLinkClickRunning) {
            isLinkClickRunning = true;
            AdFeedbackFormHelper adFeedbackFormHelper = new AdFeedbackFormHelper();
            if (URLValidator.isValidURL(url)) {
                adFeedbackFormHelper.showFeedbackForm(mAdView.getContext(), url, mAd, Reporting.AdFormat.NATIVE, IntegrationType.STANDALONE, new AdFeedbackLoadListener() {
                    @Override
                    public void onLoad(String url1) {
                        //load simple dialog
                        processedURL = url1;
                    }

                    @Override
                    public void onLoadFinished() {
                        isLinkClickRunning = false;
                    }

                    @Override
                    public void onLoadFailed(Throwable error) {
                        isLinkClickRunning = false;
                        Logger.e(TAG, error.getMessage());
                    }

                    @Override
                    public void onFormClosed() {
                        isLinkClickRunning = false;
                    }
                });
            } else {
                Logger.e(TAG, "Content info url is invalid");
                isLinkClickRunning = false;
            }
        }
    }

    public boolean isLinkClickRunning = false;
}
