// The MIT License (MIT)
//
// Copyright (c) 2018 PubNative GmbH
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
package net.pubnative.lite.sdk.utils;

import android.text.TextUtils;

import net.pubnative.lite.sdk.DeviceInfo;
import net.pubnative.lite.sdk.HyBid;
import net.pubnative.lite.sdk.analytics.Reporting;
import net.pubnative.lite.sdk.analytics.tracker.ReportingTracker;
import net.pubnative.lite.sdk.api.PNApiClient;
import net.pubnative.lite.sdk.models.AdData;
import net.pubnative.lite.sdk.utils.json.JsonOperations;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by erosgarciaponte on 08.01.18.
 */

public class AdTracker {
    private enum Type {
        IMPRESSION("impression"),
        CLICK("click"),
        SDK_EVENT("sdk_event"),
        COMPANION_AD_EVENT("companion_ad_event"),
        CUSTOM_ENDCARD_EVENT("custom_endcard_event");

        private final String mType;

        Type(String type) {
            mType = type;
        }

        @Override
        public String toString() {
            return mType;
        }
    }

    private static final String TAG = AdTracker.class.getSimpleName();
    private static final String MACRO_EVENT_TYPE = "[EVENTTYPE]";
    private static final String MACRO_ERROR_CODE = "[ERRORCODE]";

    private final PNApiClient mApiClient;
    private final DeviceInfo mDeviceInfo;
    private final List<AdData> mImpressionUrls;
    private final List<AdData> mClickUrls;
    private final List<AdData> mSdkEventUrls;
    private final List<AdData> mCompanionAdUrls;
    private final List<AdData> mCustomEndcardUrls;
    private final JSONObject mPlacementParams;
    private final Set<Integer> trackedSdkEvents = new HashSet<>();
    private final Set<Integer> trackedCompanionAdEvents = new HashSet<>();
    private final Set<Integer> trackedCustomEndcardEvents = new HashSet<>();
    private boolean mImpressionTracked;
    private boolean mClickTracked;

    private PNApiClient.TrackUrlListener mTrackUrlListener;
    private final PNApiClient.TrackJSListener mTrackJSListener;

    public AdTracker(List<AdData> impressionUrls,
                     List<AdData> clickUrls) {
        this(HyBid.getApiClient(), HyBid.getDeviceInfo(), impressionUrls, clickUrls, null, null, null);
    }

    public AdTracker(List<AdData> impressionUrls,
                     List<AdData> clickUrls, boolean clickTrackedInitial) {
        this(HyBid.getApiClient(), HyBid.getDeviceInfo(), impressionUrls, clickUrls, null, null, null);
        mClickTracked = clickTrackedInitial;
    }

    public AdTracker(List<AdData> impressionUrls,
                     List<AdData> clickUrls,
                     List<AdData> sdkEventUrls,
                     List<AdData> companionAdUrls,
                     List<AdData> customEndcardUrls) {
        this(HyBid.getApiClient(), HyBid.getDeviceInfo(), impressionUrls, clickUrls, sdkEventUrls, companionAdUrls, customEndcardUrls);
    }

    AdTracker(PNApiClient apiClient,
              DeviceInfo deviceInfo,
              List<AdData> impressionUrls,
              List<AdData> clickUrls,
              List<AdData> sdkEventUrls,
              List<AdData> companionAdUrls,
              List<AdData> customEndcardUrls) {
        mApiClient = apiClient;
        mDeviceInfo = deviceInfo;
        mImpressionUrls = impressionUrls;
        mClickUrls = clickUrls;
        mSdkEventUrls = sdkEventUrls;
        mCompanionAdUrls = companionAdUrls;
        mCustomEndcardUrls = customEndcardUrls;
        mPlacementParams = new JSONObject();

        mTrackUrlListener = new PNApiClient.TrackUrlListener() {
            @Override
            public void onSuccess() {

            }

            @Override
            public void onFailure(Throwable throwable) {

            }

            @Override
            public void onFinally(String requestUrl, String trackTypeName, int responseCode) {
                if (HyBid.getReportingController() != null) {
                    HyBid.getReportingController().reportFiredTracker(
                            new ReportingTracker(trackTypeName, requestUrl, responseCode)
                    );
                }
            }
        };

        mTrackJSListener = new PNApiClient.TrackJSListener() {
            @Override
            public void onSuccess(String js) {
                if (HyBid.getReportingController() != null) {
                    HyBid.getReportingController().reportFiredTracker(
                            new ReportingTracker("JavaScript", js)
                    );
                }
            }

            @Override
            public void onFailure(Throwable throwable) {

            }
        };
    }

    public void setTrackUrlListener(PNApiClient.TrackUrlListener trackUrlListener) {
        this.mTrackUrlListener = trackUrlListener;
    }

    public void trackImpression() {
        if (mImpressionTracked) {
            return;
        }

        trackUrls(mImpressionUrls, Type.IMPRESSION);
        mImpressionTracked = true;
    }

    public void trackClick() {
        if (mClickTracked) {
            return;
        }

        trackUrls(mClickUrls, Type.CLICK);
        mClickTracked = true;
    }

    public void trackSdkEvent(Integer eventType, Integer errorCode) {
        if (trackedSdkEvents.add(eventType)) {
            trackUrls(mSdkEventUrls, Type.SDK_EVENT, eventType, errorCode);
        }

    }

    public void trackCompanionAdEvent(Integer eventType, Integer errorCode) {
        if (trackedCompanionAdEvents.add(eventType)) {
            trackUrls(mCompanionAdUrls, Type.COMPANION_AD_EVENT, eventType, errorCode);
        }

    }

    public void trackCustomEndcardEvent(Integer eventType, Integer errorCode) {
        if (trackedCustomEndcardEvents.add(eventType)) {
            trackUrls(mCustomEndcardUrls, Type.CUSTOM_ENDCARD_EVENT, eventType, errorCode);
        }
    }

    private void trackUrls(List<AdData> urls, Type type) {
        trackUrls(urls, type, null, null);
    }

    private void trackUrls(List<AdData> urls, Type type, Integer eventType, Integer errorCode) {
        if (urls != null) {
            JSONArray beaconsArray = new JSONArray();
            for (final AdData adData : urls) {
                if (!TextUtils.isEmpty(adData.getURL()) && URLValidator.isValidURL(adData.getURL())) {
                    Logger.d(TAG, "Tracking " + type.toString() + " url: " + adData.getURL());
                    JsonOperations.putJsonString(beaconsArray, adData.getURL());
                    String url = adData.getURL();

                    if (eventType != null) {
                        url = url.replace(MACRO_EVENT_TYPE, eventType.toString());
                    }

                    if (errorCode != null) {
                        url = url.replace(MACRO_ERROR_CODE, errorCode.toString());
                    }

                    mApiClient.trackUrl(url, mDeviceInfo.getUserAgent(), type.name(), mTrackUrlListener);
                }

                if (!TextUtils.isEmpty(adData.getJS())) {
                    Logger.d(TAG, "Tracking " + type.toString() + " js: " + adData.getJS());
                    JsonOperations.putJsonString(beaconsArray, adData.getJS());
                    mApiClient.trackJS(adData.getJS(), mTrackJSListener);
                }
            }
            if (type == Type.CLICK) {
                JsonOperations.putJsonArray(mPlacementParams, Reporting.Key.FIRED_CLICK_BEACONS, beaconsArray);
            } else if (type == Type.IMPRESSION) {
                JsonOperations.putJsonArray(mPlacementParams, Reporting.Key.FIRED_IMPRESSION_BEACONS, beaconsArray);
            }
        }
    }

    public JSONObject getPlacementParams() {
        return mPlacementParams;
    }
}
