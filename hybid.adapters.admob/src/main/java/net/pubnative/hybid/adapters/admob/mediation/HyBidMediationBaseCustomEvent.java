// HyBid SDK License
//
// https://github.com/pubnative/pubnative-hybid-android-sdk/blob/main/LICENSE
//
package net.pubnative.hybid.adapters.admob.mediation;

import android.content.Context;

import androidx.annotation.NonNull;

import com.google.android.gms.ads.VersionInfo;
import com.google.android.gms.ads.mediation.Adapter;
import com.google.android.gms.ads.mediation.InitializationCompleteCallback;
import com.google.android.gms.ads.mediation.MediationConfiguration;

import java.util.List;

public class HyBidMediationBaseCustomEvent extends Adapter {
    private static final String TAG = HyBidMediationBaseCustomEvent.class.getSimpleName();
    private static final int SDK_VERSION_MAJOR = 3;
    private static final int SDK_VERSION_MINOR = 6;
    private static final int SDK_VERSION_MICRO = 0;

    @Override
    public void initialize(@NonNull Context context, @NonNull InitializationCompleteCallback initializationCompleteCallback, @NonNull List<MediationConfiguration> list) {
        initializationCompleteCallback.onInitializationSucceeded();
    }

    @NonNull
    @Override
    public VersionInfo getSDKVersionInfo() {
        return new VersionInfo(SDK_VERSION_MAJOR, SDK_VERSION_MINOR, SDK_VERSION_MICRO);
    }

    @NonNull
    @Override
    public VersionInfo getVersionInfo() {
        return new VersionInfo(SDK_VERSION_MAJOR, SDK_VERSION_MINOR, SDK_VERSION_MICRO);
    }
}
