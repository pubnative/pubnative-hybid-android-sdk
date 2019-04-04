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

import net.pubnative.lite.sdk.utils.reflection.MethodBuilderFactory;
import net.pubnative.lite.sdk.utils.reflection.ReflectionUtils;

public class HyBidAdvertisingId {

    private static final String TAG = HyBidAdvertisingId.class.getSimpleName();
    private static final int GOOGLE_PLAY_SUCCESS_CODE = 0;
    private static final String IS_LIMIT_AD_TRACKING_ENABLED_KEY = "isLimitAdTrackingEnabled";
    private static String sAdvertisingIdClientClassName = "com.google.android.gms.ads.identifier.AdvertisingIdClient";

    public interface Listener {
        void onHyBidAdvertisingIdFinish(String advertisingId, Boolean limitTracking);
    }

    protected Listener mListener;
    protected Handler mHadler;

    public void request(Context context, Listener listener) {

        mListener = listener;
        mHadler = new Handler(Looper.getMainLooper());
        getAdvertisingId(context);
    }

    protected void getAdvertisingId(final Context context) {

        new Thread(new Runnable() {
            @Override
            public void run() {
                Object adInfo = null;
                String advertisingId = null;
                boolean isLimitAdTrackingEnabled = false;

                if (context != null) {
                    try {
                        ReflectionUtils.MethodBuilder methodBuilder = MethodBuilderFactory.create(null, "getAdvertisingIdInfo")
                                .setStatic(Class.forName(sAdvertisingIdClientClassName))
                                .addParam(Context.class, context);

                        adInfo = methodBuilder.execute();
                        advertisingId = reflectedGetAdvertisingId(adInfo, advertisingId);
                        isLimitAdTrackingEnabled = reflectedIsLimitAdTrackingEnabled(adInfo, isLimitAdTrackingEnabled);
                    } catch (Exception e) {
                        Logger.e(TAG, "Unable to obtain Advertising ID.");
                    }
                }

                HyBidAdvertisingId.this.invokeOnFinish(advertisingId, isLimitAdTrackingEnabled);
            }
        }).start();
    }

    protected void invokeOnFinish(final String advertisingId, final boolean limitTracking) {
        mHadler.post(new Runnable() {
            @Override
            public void run() {
                if (mListener != null) {
                    mListener.onHyBidAdvertisingIdFinish(advertisingId, limitTracking);
                }
            }
        });
    }

    private String reflectedGetAdvertisingId(final Object adInfo, final String defaultValue) {
        try {
            return (String) MethodBuilderFactory.create(adInfo, "getId").execute();
        } catch (Exception exception) {
            return defaultValue;
        }
    }

    private boolean reflectedIsLimitAdTrackingEnabled(final Object adInfo, final boolean defaultValue) {
        try {
            Boolean result = (Boolean) MethodBuilderFactory.create(adInfo, "isLimitAdTrackingEnabled").execute();
            return (result != null) ? result : defaultValue;
        } catch (Exception exception) {
            return defaultValue;
        }
    }
}
