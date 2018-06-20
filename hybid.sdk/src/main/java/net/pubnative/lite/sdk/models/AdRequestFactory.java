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

import android.location.Location;
import android.os.Build;
import android.text.TextUtils;

import net.pubnative.lite.sdk.DeviceInfo;
import net.pubnative.lite.sdk.PNLite;
import net.pubnative.lite.sdk.location.PNLiteLocationManager;
import net.pubnative.lite.sdk.utils.PNCrypto;

import java.util.Locale;

/**
 * Created by erosgarciaponte on 08.01.18.
 */

public class AdRequestFactory {
    private final DeviceInfo mDeviceInfo;
    private final PNLiteLocationManager mLocationManager;

    public AdRequestFactory() {
        this(PNLite.getDeviceInfo(), PNLite.getLocationManager());
    }

    AdRequestFactory(DeviceInfo deviceInfo, PNLiteLocationManager locationManager) {
        mDeviceInfo = deviceInfo;
        mLocationManager = locationManager;
    }

    public AdRequest createAdRequest(final String zoneid, final String adSize) {
        String advertisingId = mDeviceInfo.getAdvertisingId();

        AdRequest adRequest = new AdRequest();
        adRequest.zoneid = zoneid;
        adRequest.apptoken = PNLite.getAppToken();
        adRequest.os = "android";
        adRequest.osver = mDeviceInfo.getOSVersion();
        adRequest.devicemodel = mDeviceInfo.getModel();
        adRequest.coppa = PNLite.isCoppaEnabled() ? "1" : "0";

        if (PNLite.isCoppaEnabled() || mDeviceInfo.limitTracking() || TextUtils.isEmpty(advertisingId)) {
            adRequest.dnt = "1";
        } else {
            adRequest.gid = advertisingId;
            adRequest.gidmd5 = PNCrypto.md5(advertisingId);
            adRequest.gidsha1 = PNCrypto.sha1(advertisingId);
        }

        adRequest.locale = mDeviceInfo.getLocale().getLanguage();

        if (!PNLite.isCoppaEnabled() && !mDeviceInfo.limitTracking()) {
            adRequest.age = PNLite.getAge();
            adRequest.gender = PNLite.getGender();
            adRequest.keywords = PNLite.getKeywords();
        }

        adRequest.bundleid = PNLite.getBundleId();
        adRequest.testMode = PNLite.isTestMode() ? "1" : "0";
        adRequest.al = adSize;
        adRequest.mf = getDefaultMetaFields();

        Location location = mLocationManager.getUserLocation();
        if (location != null && !PNLite.isCoppaEnabled() && !mDeviceInfo.limitTracking()) {
            adRequest.latitude = String.format(Locale.ENGLISH, "%.6f", location.getLatitude());
            adRequest.longitude = String.format(Locale.ENGLISH, "%.6f", location.getLongitude());
        }

        return adRequest;
    }

    private String getDefaultMetaFields() {
        String[] metaFields = new String[]{APIMeta.POINTS, APIMeta.REVENUE_MODEL, APIMeta.CONTENT_INFO};
        return TextUtils.join(",", metaFields);
    }
}
