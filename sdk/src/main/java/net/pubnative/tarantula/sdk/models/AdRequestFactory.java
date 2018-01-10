package net.pubnative.tarantula.sdk.models;

import android.support.annotation.NonNull;
import android.support.annotation.VisibleForTesting;

import com.google.android.gms.ads.identifier.AdvertisingIdClient;

import net.pubnative.tarantula.sdk.DeviceInfo;
import net.pubnative.tarantula.sdk.Tarantula;
import net.pubnative.tarantula.sdk.managers.SessionDepthManager;
import net.pubnative.tarantula.sdk.models.api.PNAPIAdRequest;

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
    public Observable<PNAPIAdRequest> createAdRequest(@NonNull final String adUnitId) {
        return mDeviceInfo.getAdvertisingInfo()
                .map(new Function<AdvertisingIdClient.Info, PNAPIAdRequest>() {
                    @Override
                    public PNAPIAdRequest apply(AdvertisingIdClient.Info info) throws Exception {
                        PNAPIAdRequest adRequest = new PNAPIAdRequest();

                        return adRequest;
                        /*return new AdRequest.Builder(
                                adUnitId,
                                Tarantula.API_VERSION,
                                Tarantula.SDK_VERSION,
                                mDeviceInfo.getAppVersion(),
                                info.getId(),
                                info.isLimitAdTrackingEnabled(),
                                "",
                                mDeviceInfo.getTimeZoneShortDisplayName(),
                                mDeviceInfo.getLocale().toString(),
                                mDeviceInfo.getOrientation().toString(),
                                mDeviceInfo.getScreenWidthPx(),
                                mDeviceInfo.getScreenHeightPx(),
                                mDeviceInfo.getBrowserAgent(),
                                mDeviceInfo.getModel(),
                                mDeviceInfo.getConnectivity().toString(),
                                mDeviceInfo.getCarrierName(),
                                mSessionDepthManager.getSessionDepth())
                                .build();*/
                    }
                });
    }
}
