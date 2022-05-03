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
import net.pubnative.lite.sdk.api.PNApiClient;
import net.pubnative.lite.sdk.models.AdData;
import net.pubnative.lite.sdk.utils.json.JsonOperations;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.List;

/**
 * Created by erosgarciaponte on 08.01.18.
 */

public class AdTracker {
    private enum Type {
        IMPRESSION("impression"),
        CLICK("click");

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
    private final PNApiClient mApiClient;
    private final DeviceInfo mDeviceInfo;
    private final List<AdData> mImpressionUrls;
    private final List<AdData> mClickUrls;
    private final JSONObject mPlacementParams;
    private boolean mImpressionTracked;
    private boolean mClickTracked;

    private PNApiClient.TrackUrlListener mTrackUrlListener;
    private final PNApiClient.TrackJSListener mTrackJSListener;

    public AdTracker(List<AdData> impressionUrls,
                     List<AdData> clickUrls) {
        this(HyBid.getApiClient(), HyBid.getDeviceInfo(), impressionUrls, clickUrls);
    }

    AdTracker(PNApiClient apiClient,
              DeviceInfo deviceInfo,
              List<AdData> impressionUrls,
              List<AdData> clickUrls) {
        mApiClient = apiClient;
        mDeviceInfo = deviceInfo;
        mImpressionUrls = impressionUrls;
        mClickUrls = clickUrls;
        mPlacementParams = new JSONObject();

        mTrackUrlListener = new PNApiClient.TrackUrlListener() {
            @Override
            public void onSuccess() {

            }

            @Override
            public void onFailure(Throwable throwable) {

            }
        };

        mTrackJSListener = new PNApiClient.TrackJSListener() {
            @Override
            public void onSuccess() {

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

    private void trackUrls(List<AdData> urls, Type type) {
        if (urls != null) {
            JSONArray beaconsArray = new JSONArray();
            for (final AdData url : urls) {
                if (!TextUtils.isEmpty(url.getURL())) {
                    Logger.d(TAG, "Tracking " + type.toString() + " url: " + url.getURL());
                    JsonOperations.putJsonString(beaconsArray, url.getURL());
                    mApiClient.trackUrl(url.getURL(), mDeviceInfo.getUserAgent(), mTrackUrlListener);
                }

                if (!TextUtils.isEmpty(url.getJS())) {
                    Logger.d(TAG, "Tracking " + type.toString() + " js: " + url.getJS());
                    JsonOperations.putJsonString(beaconsArray, url.getJS());
                    mApiClient.trackJS(url.getJS(), mTrackJSListener);
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
