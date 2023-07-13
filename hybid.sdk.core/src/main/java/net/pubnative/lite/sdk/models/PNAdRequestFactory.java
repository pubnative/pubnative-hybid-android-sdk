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
import net.pubnative.lite.sdk.location.HyBidLocationManager;
import net.pubnative.lite.sdk.prefs.HyBidPreferences;
import net.pubnative.lite.sdk.prefs.SessionImpressionPrefs;
import net.pubnative.lite.sdk.utils.HyBidAdvertisingId;
import net.pubnative.lite.sdk.utils.HyBidTimeUtils;
import net.pubnative.lite.sdk.utils.Logger;
import net.pubnative.lite.sdk.utils.PNAsyncUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Created by erosgarciaponte on 08.01.18.
 */

public class PNAdRequestFactory implements AdRequestFactory {
    private static final String TAG = PNAdRequestFactory.class.getSimpleName();

    private DeviceInfo mDeviceInfo;
    private HyBidPreferences prefs;
    private final HyBidLocationManager mLocationManager;
    private UserDataManager mUserDataManager;
    private final DisplayManager mDisplayManager;
    private IntegrationType mIntegrationType = IntegrationType.HEADER_BIDDING;
    private String mMediationVendor;
    private boolean mIsRewarded;

    public PNAdRequestFactory() {
        this(HyBid.getDeviceInfo(), HyBid.getLocationManager(), HyBid.getUserDataManager(), new DisplayManager());
    }

    PNAdRequestFactory(DeviceInfo deviceInfo, HyBidLocationManager locationManager, UserDataManager userDataManager, DisplayManager displayManager) {
        mDeviceInfo = deviceInfo;
        mLocationManager = locationManager;
        mUserDataManager = userDataManager;
        mDisplayManager = displayManager;
    }

    @Override
    public void createAdRequest(final String appToken, final String zoneid, final AdSize adSize, final boolean isRewarded, final Callback callback) {
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
//                DBManager dbManager = new DBManager(mDeviceInfo.getContext());
//                dbManager.open();
//                int impDepth = dbManager.getImpressionDepth(zoneid);
//                dbManager.close();

                SessionImpressionPrefs prefs = new SessionImpressionPrefs(mDeviceInfo.getContext());
                int impDepth = prefs.getImpressionDepth(zoneid);
                PNAsyncUtils.safeExecuteOnExecutor(new HyBidAdvertisingId(context, (advertisingId1, limitTracking1) -> processAdvertisingId(appToken, zoneid, adSize, advertisingId1, limitTracking1, callback, impDepth)));
            } catch (Exception exception) {
                Logger.e(TAG, "Error executing HyBidAdvertisingId AsyncTask");
            }
        } else {
            if (mDeviceInfo != null && mDeviceInfo.getContext() != null) {
//                DBManager dbManager = new DBManager(mDeviceInfo.getContext());
//                dbManager.open();
//                int impDepth = dbManager.getImpressionDepth(zoneid);
//                dbManager.close();

                SessionImpressionPrefs prefs = new SessionImpressionPrefs(mDeviceInfo.getContext());
                int impDepth = prefs.getImpressionDepth(zoneid);

                processAdvertisingId(appToken, zoneid, adSize, advertisingId, limitTracking, callback, impDepth);
            }
        }
    }

    private void processAdvertisingId(String appToken, String zoneId, AdSize adSize, String advertisingId, boolean limitTracking, Callback callback, int impDepth) {
        if (callback != null) {
            callback.onRequestCreated(buildRequest(appToken, zoneId, adSize, advertisingId, limitTracking, mIntegrationType, mMediationVendor, impDepth));
        }
    }

    @Override
    public AdRequest buildRequest(final String appToken, final String zoneid, AdSize adSize, final String advertisingId, final boolean limitTracking, final IntegrationType integrationType, final String mediationVendor, Integer impDepth) {
        if (mUserDataManager == null) {
            mUserDataManager = HyBid.getUserDataManager();
        }
        boolean isCCPAOptOut = mUserDataManager.isCCPAOptOut();
        PNAdRequest adRequest = new PNAdRequest();
        adRequest.zoneId = zoneid;
        adRequest.appToken = TextUtils.isEmpty(appToken) ? HyBid.getAppToken() : appToken;
        adRequest.os = "android";
        adRequest.osver = mDeviceInfo.getOSVersion();
        adRequest.devicemodel = mDeviceInfo.getModel();
        adRequest.coppa = HyBid.isCoppaEnabled() ? "1" : "0";
        adRequest.omidpn = HyBid.OM_PARTNER_NAME;
        adRequest.omidpv = HyBid.OMSDK_VERSION;
        adRequest.isInterstitial = adSize == AdSize.SIZE_INTERSTITIAL;

        if (HyBid.isCoppaEnabled() || limitTracking || TextUtils.isEmpty(advertisingId) || isCCPAOptOut || mUserDataManager.isConsentDenied()) {
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

        String protocols = getSupportedProtocols();
        if (!TextUtils.isEmpty(protocols)) {
            adRequest.protocol = protocols;
        }

        String apis = getSupportedApis();
        if (!TextUtils.isEmpty(apis)) {
            adRequest.api = apis;
        }

        adRequest.displaymanager = mDisplayManager.getDisplayManager();
        adRequest.displaymanagerver = mDisplayManager.getDisplayManagerVersion(mediationVendor, integrationType);

        if (mLocationManager != null) {
            Location location = mLocationManager.getUserLocation();
            if (location != null && !HyBid.isCoppaEnabled() && !limitTracking && !mUserDataManager.isConsentDenied() && !isCCPAOptOut && HyBid.isLocationTrackingEnabled()) {
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
        adRequest.soundSetting = mDeviceInfo.getSoundSetting();

        adRequest.impdepth = String.valueOf(impDepth);
        adRequest.ageofapp = new HyBidTimeUtils().getDaysSince(Long.parseLong(getAgeOfApp()));
        adRequest.sessionduration = new HyBidTimeUtils().getSeconds(calculateSessionDuration());

        return adRequest;
    }

    private String getAgeOfApp() {
        if (prefs == null) prefs = new HyBidPreferences(mDeviceInfo.getContext());
        return prefs.getAppFirstInstalledTime();
    }

    private long calculateSessionDuration() {
        if (prefs == null) prefs = new HyBidPreferences(mDeviceInfo.getContext());

        return System.currentTimeMillis() - prefs.getSessionTimeStamp();
    }

    @Override
    public void setMediationVendor(String mediationVendor) {
        this.mMediationVendor = mediationVendor;
    }

    @Override
    public void setIntegrationType(IntegrationType integrationType) {
        this.mIntegrationType = integrationType;
    }

    private String getDefaultMetaFields() {
        String[] metaFields = new String[]{APIMeta.POINTS, APIMeta.REVENUE_MODEL, APIMeta.CONTENT_INFO, APIMeta.CREATIVE_ID};
        return TextUtils.join(",", metaFields);
    }

    private String getDefaultNativeAssetFields() {
        String[] assetFields = new String[]{APIAsset.ICON, APIAsset.TITLE, APIAsset.BANNER, APIAsset.CALL_TO_ACTION, APIAsset.RATING, APIAsset.DESCRIPTION};
        return TextUtils.join(",", assetFields);
    }

    private String getSupportedProtocols() {
        List<String> supportedProtocols = new ArrayList<>();
        supportedProtocols.add(Protocol.VAST_1_0);
        supportedProtocols.add(Protocol.VAST_2_0);
        supportedProtocols.add(Protocol.VAST_3_0);
        supportedProtocols.add(Protocol.VAST_1_0_WRAPPER);
        supportedProtocols.add(Protocol.VAST_2_0_WRAPPER);
        supportedProtocols.add(Protocol.VAST_3_0_WRAPPER);
        supportedProtocols.add(Protocol.VAST_4_0);
        supportedProtocols.add(Protocol.VAST_4_0_WRAPPER);
        supportedProtocols.add(Protocol.VAST_4_1);
        supportedProtocols.add(Protocol.VAST_4_1_WRAPPER);
        supportedProtocols.add(Protocol.VAST_4_2);
        supportedProtocols.add(Protocol.VAST_4_2_WRAPPER);

        return TextUtils.join(",", supportedProtocols.toArray(new String[0]));
    }

    private String getSupportedApis() {
        List<String> supportedApis = new ArrayList<>();
        supportedApis.add(Api.MRAID_1);
        supportedApis.add(Api.MRAID_2);
        supportedApis.add(Api.MRAID_3);
        supportedApis.add(Api.OMID_1);

        return TextUtils.join(",", supportedApis.toArray(new String[0]));
    }
}
