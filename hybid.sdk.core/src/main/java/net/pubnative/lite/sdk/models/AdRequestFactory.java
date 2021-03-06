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
package net.pubnative.lite.sdk.models;

import android.content.Context;
import android.location.Location;
import android.text.TextUtils;

import net.pubnative.lite.sdk.DeviceInfo;
import net.pubnative.lite.sdk.DisplayManager;
import net.pubnative.lite.sdk.HyBid;
import net.pubnative.lite.sdk.UserDataManager;
import net.pubnative.lite.sdk.core.BuildConfig;
import net.pubnative.lite.sdk.location.HyBidLocationManager;
import net.pubnative.lite.sdk.utils.HyBidAdvertisingId;
import net.pubnative.lite.sdk.utils.Logger;
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

    private DeviceInfo mDeviceInfo;
    private HyBidLocationManager mLocationManager;
    private UserDataManager mUserDataManager;
    private final DisplayManager mDisplayManager;
    private IntegrationType mIntegrationType = IntegrationType.HEADER_BIDDING;
    private boolean mIsRewarded;

    public AdRequestFactory() {
        this(HyBid.getDeviceInfo(), HyBid.getLocationManager(), HyBid.getUserDataManager(), new DisplayManager());
    }

    AdRequestFactory(DeviceInfo deviceInfo, HyBidLocationManager locationManager, UserDataManager userDataManager, DisplayManager displayManager) {
        mDeviceInfo = deviceInfo;
        mLocationManager = locationManager;
        mUserDataManager = userDataManager;
        mDisplayManager = displayManager;
    }

    public void createAdRequest(final String zoneid, final AdSize adSize, final boolean isRewarded, final Callback callback) {
        if (mDeviceInfo == null) {
            mDeviceInfo = HyBid.getDeviceInfo();
        }

        String advertisingId = null;
        boolean limitTracking = false;
        Context context = null;
        if (mDeviceInfo != null) {
            advertisingId = mDeviceInfo.getAdvertisingId();
            limitTracking = mDeviceInfo.limitTracking();
            context = mDeviceInfo.getContext();
        }
        mIsRewarded = isRewarded;
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
            processAdvertisingId(zoneid, adSize, advertisingId, limitTracking, callback);
        }
    }

    private void processAdvertisingId(String zoneId, AdSize adSize, String advertisingId, boolean limitTracking, Callback callback) {
        if (callback != null) {
            callback.onRequestCreated(buildRequest(zoneId, adSize, advertisingId, limitTracking, mIntegrationType));
        }
    }

    public AdRequest buildRequest(final String zoneid, AdSize adSize, final String advertisingId, final boolean limitTracking, final IntegrationType integrationType) {
        if (mUserDataManager == null) {
            mUserDataManager = HyBid.getUserDataManager();
        }
        boolean isCCPAOptOut = mUserDataManager.isCCPAOptOut();
        AdRequest adRequest = new AdRequest();
        adRequest.zoneid = zoneid;
        adRequest.apptoken = HyBid.getAppToken();
        adRequest.os = "android";
        adRequest.osver = mDeviceInfo.getOSVersion();
        adRequest.devicemodel = mDeviceInfo.getModel();
        adRequest.coppa = HyBid.isCoppaEnabled() ? "1" : "0";
        adRequest.omidpn = HyBid.OM_PARTNER_NAME;
        adRequest.omidpv = HyBid.OMSDK_VERSION;

        if (HyBid.isCoppaEnabled() || limitTracking || TextUtils.isEmpty(advertisingId)
                || isCCPAOptOut || mUserDataManager.isConsentDenied()) {
            adRequest.dnt = "1";
        } else {
            adRequest.gid = advertisingId;

            adRequest.gidmd5 = mDeviceInfo.getAdvertisingIdMd5();
            adRequest.gidsha1 = mDeviceInfo.getAdvertisingIdSha1();
        }

        String usPrivacyString = mUserDataManager.getIABUSPrivacyString();
        if (!TextUtils.isEmpty(usPrivacyString)) {
            adRequest.usprivacy = usPrivacyString;
        }

        String gdprConsentString = mUserDataManager.getIABGDPRConsentString();
        if (!TextUtils.isEmpty(gdprConsentString)) {
            adRequest.userconsent = gdprConsentString;
        }

        adRequest.locale = mDeviceInfo.getLocale().getLanguage();

        if (!HyBid.isCoppaEnabled() && !limitTracking && !isCCPAOptOut && !mUserDataManager.isConsentDenied()) {
            adRequest.age = HyBid.getAge();
            adRequest.gender = HyBid.getGender();
            adRequest.keywords = HyBid.getKeywords();
        }

        adRequest.bundleid = HyBid.getBundleId();
        adRequest.testMode = HyBid.isTestMode() ? "1" : "0";

        // If the ad size is empty it means it's a native ad
        if (adSize == null) {
            adRequest.af = getDefaultNativeAssetFields();
        } else {
            adRequest.al = adSize.getAdLayoutSize();

            if (adSize.getWidth() != 0) {
                adRequest.width = String.valueOf(adSize.getWidth());
            }

            if (adSize.getHeight() != 0) {
                adRequest.height = String.valueOf(adSize.getHeight());
            }
        }

        adRequest.mf = getDefaultMetaFields();

        adRequest.displaymanager = mDisplayManager.getDisplayManager();
        adRequest.displaymanagerver = mDisplayManager.getDisplayManagerVersion(integrationType);

        if (mLocationManager != null) {
            Location location = mLocationManager.getUserLocation();
            if (location != null && !HyBid.isCoppaEnabled() && !limitTracking
                    && !mUserDataManager.isConsentDenied() && HyBid.isLocationTrackingEnabled()) {
                adRequest.latitude = String.format(Locale.ENGLISH, "%.6f", location.getLatitude());
                adRequest.longitude = String.format(Locale.ENGLISH, "%.6f", location.getLongitude());
            }
        }

        if (mIsRewarded) {
            adRequest.rv = "1";
        } else {
            adRequest.rv = "0";
        }

        adRequest.deviceHeight = mDeviceInfo.getDeviceHeight();
        adRequest.deviceWidth = mDeviceInfo.getDeviceWidth();

        adRequest.orientation = mDeviceInfo.getOrientation().toString();

        mDeviceInfo.checkSoundSetting();
        adRequest.soundSetting = mDeviceInfo.getSoundSetting();

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
