package net.pubnative.tarantula.sdk.models;

import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.VisibleForTesting;
import android.text.TextUtils;

import com.google.android.gms.ads.identifier.AdvertisingIdClient;

import net.pubnative.tarantula.sdk.DeviceInfo;
import net.pubnative.tarantula.sdk.Tarantula;
import net.pubnative.tarantula.sdk.managers.SessionDepthManager;
import net.pubnative.tarantula.sdk.utils.PNCrypto;

import java.util.Locale;

import io.reactivex.Observable;
import io.reactivex.functions.Function;

/**
 * Created by erosgarciaponte on 08.01.18.
 */

public class AdRequestFactory {
    @NonNull private final DeviceInfo mDeviceInfo;
    @NonNull private final SessionDepthManager mSessionDepthManager;

    public AdRequestFactory() {
        this(Tarantula.getDeviceInfo(), Tarantula.getSessionDepthManager());
    }

    @VisibleForTesting
    AdRequestFactory(@NonNull DeviceInfo deviceInfo, @NonNull SessionDepthManager sessionDepthManager) {
        mDeviceInfo = deviceInfo;
        mSessionDepthManager = sessionDepthManager;
    }

    @NonNull
    public Observable<AdRequest> createAdRequest(@NonNull final String zoneid, @NonNull final String adSize) {
        return mDeviceInfo.getAdvertisingInfo()
                .map(new Function<AdvertisingIdClient.Info, AdRequest>() {
                    @Override
                    public AdRequest apply(AdvertisingIdClient.Info info) throws Exception {
                        AdRequest adRequest = new AdRequest();
                        adRequest.zoneid = zoneid;
                        adRequest.apptoken = Tarantula.getAppToken();
                        adRequest.os = "android";
                        adRequest.osver = Build.VERSION.RELEASE;
                        adRequest.devicemodel = Build.MODEL;
                        adRequest.coppa = Tarantula.isCoppaEnabled() ? "1" : "0";

                        if (Tarantula.isCoppaEnabled() || TextUtils.isEmpty(info.getId()) || info.isLimitAdTrackingEnabled()) {
                            adRequest.dnt = "1";
                        } else {
                            adRequest.gid = info.getId();
                            adRequest.gidmd5 = PNCrypto.md5(info.getId());
                            adRequest.gidsha1 = PNCrypto.sha1(info.getId());
                        }

                        adRequest.locale = Locale.getDefault().getLanguage();
                        adRequest.age = Tarantula.getAge();
                        adRequest.gender = Tarantula.getGender();
                        adRequest.keywords = Tarantula.getKeywords();
                        adRequest.bundleid = Tarantula.getBundleId();
                        adRequest.testMode = Tarantula.isTestMode() ? "1" : "0";
                        adRequest.al = adSize;
                        adRequest.mf = "revenuemodel,contentinfo";

                        return adRequest;
                    }
                });
    }
}
