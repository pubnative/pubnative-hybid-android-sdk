package net.pubnative.tarantula.sdk.models;

import android.support.annotation.NonNull;

import com.google.android.gms.ads.identifier.AdvertisingIdClient;

import net.pubnative.tarantula.sdk.DeviceInfo;
import net.pubnative.tarantula.sdk.Tarantula;
import net.pubnative.tarantula.sdk.managers.SessionDepthManager;

import io.reactivex.Observable;
import io.reactivex.functions.Function;

/**
 * Created by erosgarciaponte on 08.01.18.
 */

public class ErrorRequestFactory {
    @NonNull
    private final DeviceInfo mDeviceInfo;
    @NonNull
    private final SessionDepthManager mSessionDepthManager;

    public ErrorRequestFactory() {
        mDeviceInfo = Tarantula.getDeviceInfo();
        mSessionDepthManager = Tarantula.getSessionDepthManager();
    }

    @NonNull
    public Observable<ErrorRequest> createErrorRequest(@NonNull final String message) {
        return mDeviceInfo.getAdvertisingInfo()
                .map(new Function<AdvertisingIdClient.Info, ErrorRequest>() {
                    @Override
                    public ErrorRequest apply(AdvertisingIdClient.Info info) throws Exception {
                        return new ErrorRequest.Builder(
                                message,
                                Tarantula.API_VERSION,
                                Tarantula.SDK_VERSION,
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
                                .build();
                    }
                });
    }
}
