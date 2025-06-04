// HyBid SDK License
//
// https://github.com/pubnative/pubnative-hybid-android-sdk/blob/main/LICENSE
//
package net.pubnative.lite.sdk;

import android.content.Context;
import android.os.Build;
import android.os.ext.SdkExtensions;

import net.pubnative.lite.sdk.wrappers.CustomAudienceWrapper;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class HyBidCustomAudienceManager {
    private final String TAG = HyBidCustomAudienceManager.class.getSimpleName();

    private final CustomAudienceWrapper mCustomAudienceWrapper;
    private final boolean mIsApiAvailable;
    private static final Executor EXECUTOR = Executors.newCachedThreadPool();

    public HyBidCustomAudienceManager(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE && SdkExtensions.getExtensionVersion(SdkExtensions.AD_SERVICES) >= 4) {
            mCustomAudienceWrapper = new CustomAudienceWrapper(context, EXECUTOR);
            mIsApiAvailable = mCustomAudienceWrapper.isApiAvailable();
        } else {
            mCustomAudienceWrapper = null;
            mIsApiAvailable = false;
        }
    }

    public boolean isApiAvailable() {
        return mIsApiAvailable;
    }
}
