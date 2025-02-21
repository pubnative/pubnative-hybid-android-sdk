// The MIT License (MIT)
//
// Copyright (c) 2018 PubNative GmbH
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