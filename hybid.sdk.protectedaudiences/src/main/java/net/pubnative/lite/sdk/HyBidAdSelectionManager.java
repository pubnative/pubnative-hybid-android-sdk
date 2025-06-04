// HyBid SDK License
//
// https://github.com/pubnative/pubnative-hybid-android-sdk/blob/main/LICENSE
//
package net.pubnative.lite.sdk;

import android.adservices.adselection.AdSelectionManager;
import android.adservices.common.AdTechIdentifier;
import android.content.Context;
import android.net.Uri;
import android.os.Build;
import android.os.ext.SdkExtensions;

import net.pubnative.lite.sdk.models.BuyerSignals;
import net.pubnative.lite.sdk.wrappers.AdSelectionWrapper;

import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class HyBidAdSelectionManager {
    private final String TAG = HyBidAdSelectionManager.class.getSimpleName();

    private AdSelectionWrapper mAdSelectionWrapper;
    private final boolean mIsApiAvailable;
    private static final Executor EXECUTOR = Executors.newCachedThreadPool();

    public HyBidAdSelectionManager(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE && SdkExtensions.getExtensionVersion(SdkExtensions.AD_SERVICES) >= 4) {
            mIsApiAvailable = context.getSystemService(AdSelectionManager.class) != null;
        } else {
            mAdSelectionWrapper = null;
            mIsApiAvailable = false;
        }
    }

    public void initialise(Context context, List<AdTechIdentifier> buyers, AdTechIdentifier seller, Uri decisionUri, Uri trustedDataUri, BuyerSignals buyerSignals) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE && SdkExtensions.getExtensionVersion(SdkExtensions.AD_SERVICES) >= 4) {
            mAdSelectionWrapper = new AdSelectionWrapper(buyers, seller, decisionUri, trustedDataUri, buyerSignals, context, EXECUTOR);
        }
    }

    public boolean isApiAvailable() {
        return mIsApiAvailable;
    }
}
