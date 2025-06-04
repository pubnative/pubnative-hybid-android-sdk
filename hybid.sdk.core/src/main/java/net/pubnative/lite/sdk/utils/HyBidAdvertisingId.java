// HyBid SDK License
//
// https://github.com/pubnative/pubnative-hybid-android-sdk/blob/main/LICENSE
//
package net.pubnative.lite.sdk.utils;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;

import net.pubnative.lite.sdk.HyBid;
import net.pubnative.lite.sdk.network.ApiExecutor;
import net.pubnative.lite.sdk.utils.reflection.MethodBuilderFactory;
import net.pubnative.lite.sdk.utils.reflection.ReflectionUtils;

import java.lang.ref.WeakReference;

public class HyBidAdvertisingId {

    private static final String TAG = HyBidAdvertisingId.class.getSimpleName();
    private static final String sAdvertisingIdClientClassName = "com.google.android.gms.ads.identifier.AdvertisingIdClient";

    public interface Listener {
        void onHyBidAdvertisingIdFinish(String advertisingId, Boolean limitTracking);
    }

    private final WeakReference<Context> mContextRef;

    public HyBidAdvertisingId(Context context) {
        mContextRef = new WeakReference<>(context);
    }

    public void execute(Listener listener) {
        ApiExecutor.getInstance().execute(() -> {
            Object adInfo;
            String advertisingId = null;
            boolean isLimitAdTrackingEnabled = false;
            if (mContextRef.get() != null) {
                try {
                    ReflectionUtils.MethodBuilder methodBuilder = MethodBuilderFactory.create(null, "getAdvertisingIdInfo").setStatic(Class.forName(sAdvertisingIdClientClassName)).addParam(Context.class, mContextRef.get());
                    adInfo = methodBuilder.execute();
                    advertisingId = reflectedGetAdvertisingId(adInfo, advertisingId);
                    isLimitAdTrackingEnabled = reflectedIsLimitAdTrackingEnabled(adInfo, isLimitAdTrackingEnabled);
                    post(listener, advertisingId, isLimitAdTrackingEnabled);
                } catch (Exception e) {
                    HyBid.reportException(e);
                    Logger.e(TAG, "Unable to obtain Advertising ID.");
                }
            }
        });
    }

    private static void post(Listener listener, String advertisingId, boolean isLimitAdTrackingEnabled) {
        Handler handler = new Handler(Looper.getMainLooper());
        handler.postDelayed(() -> {
            if (listener != null) {
                listener.onHyBidAdvertisingIdFinish(advertisingId, isLimitAdTrackingEnabled);
            }
        }, 0);
    }

    private String reflectedGetAdvertisingId(final Object adInfo, final String defaultValue) {
        try {
            return (String) MethodBuilderFactory.create(adInfo, "getId").execute();
        } catch (Exception exception) {
            HyBid.reportException(exception);
            return defaultValue;
        }
    }

    private boolean reflectedIsLimitAdTrackingEnabled(final Object adInfo, final boolean defaultValue) {
        try {
            Boolean result = (Boolean) MethodBuilderFactory.create(adInfo, "isLimitAdTrackingEnabled").execute();
            return (result != null) ? result : defaultValue;
        } catch (Exception exception) {
            HyBid.reportException(exception);
            return defaultValue;
        }
    }
}