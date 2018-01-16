package net.pubnative.tarantula.sdk.models;

import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.VisibleForTesting;
import android.text.TextUtils;

import net.pubnative.tarantula.sdk.DeviceInfo;
import net.pubnative.tarantula.sdk.Tarantula;
import net.pubnative.tarantula.sdk.utils.PNCrypto;

/**
 * Created by erosgarciaponte on 08.01.18.
 */

public class AdRequestFactory {
    @NonNull private final DeviceInfo mDeviceInfo;

    public AdRequestFactory() {
        this(Tarantula.getDeviceInfo());
    }

    @VisibleForTesting
    AdRequestFactory(@NonNull DeviceInfo deviceInfo) {
        mDeviceInfo = deviceInfo;
    }

    @NonNull
    public AdRequest createAdRequest(@NonNull final String zoneid, @NonNull final String adSize) {
        String advertisingId = mDeviceInfo.getAdvertisingId();

        AdRequest adRequest = new AdRequest();
        adRequest.zoneid = zoneid;
        adRequest.apptoken = Tarantula.getAppToken();
        adRequest.os = "android";
        adRequest.osver = Build.VERSION.RELEASE;
        adRequest.devicemodel = Build.MODEL;
        adRequest.coppa = Tarantula.isCoppaEnabled() ? "1" : "0";

        if (Tarantula.isCoppaEnabled() || TextUtils.isEmpty(advertisingId)) {
            adRequest.dnt = "1";
        } else {
            adRequest.gid = advertisingId;
            adRequest.gidmd5 = PNCrypto.md5(advertisingId);
            adRequest.gidsha1 = PNCrypto.sha1(advertisingId);
        }

        adRequest.locale = mDeviceInfo.getLocale().getLanguage();
        adRequest.age = Tarantula.getAge();
        adRequest.gender = Tarantula.getGender();
        adRequest.keywords = Tarantula.getKeywords();
        adRequest.bundleid = Tarantula.getBundleId();
        adRequest.testMode = Tarantula.isTestMode() ? "1" : "0";
        adRequest.al = adSize;
        adRequest.mf = "points,revenuemodel,contentinfo";

        return adRequest;
    }
}
