package net.pubnative.lite.sdk.models;

import android.os.Build;
import android.text.TextUtils;

import net.pubnative.lite.sdk.DeviceInfo;
import net.pubnative.lite.sdk.PNLite;
import net.pubnative.lite.sdk.utils.PNCrypto;

/**
 * Created by erosgarciaponte on 08.01.18.
 */

public class AdRequestFactory {
    private final DeviceInfo mDeviceInfo;

    public AdRequestFactory() {
        this(PNLite.getDeviceInfo());
    }

    AdRequestFactory(DeviceInfo deviceInfo) {
        mDeviceInfo = deviceInfo;
    }

    public AdRequest createAdRequest(final String zoneid, final String adSize) {
        String advertisingId = mDeviceInfo.getAdvertisingId();

        AdRequest adRequest = new AdRequest();
        adRequest.zoneid = zoneid;
        adRequest.apptoken = PNLite.getAppToken();
        adRequest.os = "android";
        adRequest.osver = Build.VERSION.RELEASE;
        adRequest.devicemodel = Build.MODEL;
        adRequest.coppa = PNLite.isCoppaEnabled() ? "1" : "0";

        if (PNLite.isCoppaEnabled() || TextUtils.isEmpty(advertisingId)) {
            adRequest.dnt = "1";
        } else {
            adRequest.gid = advertisingId;
            adRequest.gidmd5 = PNCrypto.md5(advertisingId);
            adRequest.gidsha1 = PNCrypto.sha1(advertisingId);
        }

        adRequest.locale = mDeviceInfo.getLocale().getLanguage();
        adRequest.age = PNLite.getAge();
        adRequest.gender = PNLite.getGender();
        adRequest.keywords = PNLite.getKeywords();
        adRequest.bundleid = PNLite.getBundleId();
        adRequest.testMode = PNLite.isTestMode() ? "1" : "0";
        adRequest.al = adSize;
        adRequest.mf = getDefaultMetaFields();

        return adRequest;
    }

    private String getDefaultMetaFields() {
        String[] metaFields = new String[] {APIMeta.POINTS, APIMeta.REVENUE_MODEL, APIMeta.CONTENT_INFO};
        String result = TextUtils.join(",", metaFields);
        return result;
    }
}
