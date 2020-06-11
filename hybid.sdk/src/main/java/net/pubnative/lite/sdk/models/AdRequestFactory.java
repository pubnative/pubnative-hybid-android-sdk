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
package net.pubnative.lite.sdk.models;

import android.content.Context;
import android.location.Location;
import android.provider.Settings;
import android.text.TextUtils;

import net.pubnative.lite.sdk.BuildConfig;
import net.pubnative.lite.sdk.DeviceInfo;
import net.pubnative.lite.sdk.HyBid;
import net.pubnative.lite.sdk.location.HyBidLocationManager;
import net.pubnative.lite.sdk.utils.HyBidAdvertisingId;
import net.pubnative.lite.sdk.utils.Logger;
import net.pubnative.lite.sdk.utils.PNAdvertisingIdClient;
import net.pubnative.lite.sdk.utils.PNAsyncUtils;

import java.util.Locale;

/**
 * Created by erosgarciaponte on 08.01.18.
 */

public class AdRequestFactory {
    private static final String TAG = AdRequestFactory.class.getSimpleName();

    public interface Callback {
        void onRequestCreated(AdRequest adRequest);
    }

    private final DeviceInfo mDeviceInfo;
    private final HyBidLocationManager mLocationManager;
    private IntegrationType mIntegrationType = IntegrationType.HEADER_BIDDING;

    public AdRequestFactory() {
        this(HyBid.getDeviceInfo(), HyBid.getLocationManager());
    }

    AdRequestFactory(DeviceInfo deviceInfo, HyBidLocationManager locationManager) {
        mDeviceInfo = deviceInfo;
        mLocationManager = locationManager;
    }

    public void createAdRequest(final String zoneid, final String adSize, final Callback callback) {
        String advertisingId = mDeviceInfo.getAdvertisingId();
        boolean limitTracking = mDeviceInfo.limitTracking();
        Context context = mDeviceInfo.getContext();
        if (TextUtils.isEmpty(advertisingId) && context != null) {
            try {
                PNAsyncUtils.safeExecuteOnExecutor(new HyBidAdvertisingId(context, new HyBidAdvertisingId.Listener() {
                    @Override
                    public void onHyBidAdvertisingIdFinish(String advertisingId, Boolean limitTracking) {
                        processAdvertisingId(zoneid, adSize, advertisingId, limitTracking, callback);
                    }
                }));
            } catch (Exception exception) {
                Logger.e(TAG, "Error executing HyBidAdvertisingId AsyncTask");
            }
        } else {
            if (callback != null) {
                callback.onRequestCreated(buildRequest(zoneid, adSize, advertisingId, limitTracking, mIntegrationType));
            }
        }
    }

    private void processAdvertisingId(String zoneId, String adSize, String advertisingId, boolean limitTracking, Callback callback) {
        if (callback != null) {
            callback.onRequestCreated(buildRequest(zoneId, adSize, advertisingId, limitTracking, mIntegrationType));
        }
    }

    AdRequest buildRequest(final String zoneid, final String adSize, final String advertisingId, final boolean limitTracking, final IntegrationType integrationType) {
        boolean isCCPAOptOut = HyBid.getUserDataManager().isCCPAOptOut();
        AdRequest adRequest = new AdRequest();
        adRequest.zoneid = zoneid;
        adRequest.apptoken = HyBid.getAppToken();
        adRequest.os = "android";
        adRequest.osver = mDeviceInfo.getOSVersion();
        adRequest.devicemodel = mDeviceInfo.getModel();
        adRequest.coppa = HyBid.isCoppaEnabled() ? "1" : "0";

        if (HyBid.isCoppaEnabled() || limitTracking || TextUtils.isEmpty(advertisingId)
                || isCCPAOptOut) {
            adRequest.dnt = "1";
        } else {
            adRequest.gid = advertisingId;

            adRequest.gidmd5 = mDeviceInfo.getAdvertisingIdMd5();
            adRequest.gidsha1 = mDeviceInfo.getAdvertisingIdSha1();
        }

        adRequest.locale = mDeviceInfo.getLocale().getLanguage();

        if (!HyBid.isCoppaEnabled() && !limitTracking && !isCCPAOptOut) {
            adRequest.age = HyBid.getAge();
            adRequest.gender = HyBid.getGender();
            adRequest.keywords = HyBid.getKeywords();
        }

        adRequest.bundleid = HyBid.getBundleId();
        adRequest.testMode = HyBid.isTestMode() ? "1" : "0";

        // If the ad size is empty it means it's a native ad
        if (TextUtils.isEmpty(adSize)) {
            adRequest.af = getDefaultNativeAssetFields();
        } else {
            adRequest.al = adSize;
        }

        adRequest.mf = getDefaultMetaFields();

        adRequest.displaymanager = "HyBid";
        adRequest.displaymanagerver = String.format(Locale.ENGLISH, "%s_%s_%s",
                "sdkandroid", integrationType.getCode(), BuildConfig.VERSION_NAME);

        Location location = mLocationManager.getUserLocation();
        if (location != null && !HyBid.isCoppaEnabled() && !limitTracking) {
            adRequest.latitude = String.format(Locale.ENGLISH, "%.6f", location.getLatitude());
            adRequest.longitude = String.format(Locale.ENGLISH, "%.6f", location.getLongitude());
        }

        return adRequest;
    }

    public void setIntegrationType(IntegrationType integrationType) {
        this.mIntegrationType = integrationType;
    }

    private String getDefaultMetaFields() {
        String[] metaFields = new String[]{
                APIMeta.POINTS,
                APIMeta.REVENUE_MODEL,
                APIMeta.CONTENT_INFO,
                APIMeta.CREATIVE_ID};
        return TextUtils.join(",", metaFields);
    }

    private String getDefaultNativeAssetFields() {
        String[] assetFields = new String[]{
                APIAsset.ICON,
                APIAsset.TITLE,
                APIAsset.BANNER,
                APIAsset.CALL_TO_ACTION,
                APIAsset.RATING,
                APIAsset.DESCRIPTION};
        return TextUtils.join(",", assetFields);
    }
}
