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
import android.os.AsyncTask;

import net.pubnative.lite.sdk.models.AdvertisingInfo;
import net.pubnative.lite.sdk.utils.reflection.MethodBuilderFactory;
import net.pubnative.lite.sdk.utils.reflection.ReflectionUtils;

import java.lang.ref.WeakReference;

public class HyBidAdvertisingId extends AsyncTask<Void, Void, AdvertisingInfo> {

    private static final String TAG = HyBidAdvertisingId.class.getSimpleName();
    private static final String sAdvertisingIdClientClassName = "com.google.android.gms.ads.identifier.AdvertisingIdClient";

    public interface Listener {
        void onHyBidAdvertisingIdFinish(String advertisingId, Boolean limitTracking);
    }

    private final WeakReference<Context> mContextRef;
    private final Listener mListener;

    public HyBidAdvertisingId(Context context, Listener listener) {
        mContextRef = new WeakReference<>(context);
        mListener = listener;
    }

    @Override
    protected AdvertisingInfo doInBackground(Void... params) {
        Object adInfo;
        String advertisingId = null;
        boolean isLimitAdTrackingEnabled = false;

        if (mContextRef.get() != null) {
            try {
                ReflectionUtils.MethodBuilder methodBuilder = MethodBuilderFactory.create(null, "getAdvertisingIdInfo")
                        .setStatic(Class.forName(sAdvertisingIdClientClassName))
                        .addParam(Context.class, mContextRef.get());

                adInfo = methodBuilder.execute();
                advertisingId = reflectedGetAdvertisingId(adInfo, advertisingId);
                isLimitAdTrackingEnabled = reflectedIsLimitAdTrackingEnabled(adInfo, isLimitAdTrackingEnabled);
            } catch (Exception e) {
                Logger.e(TAG, "Unable to obtain Advertising ID.");
            }
        }

        return new AdvertisingInfo(advertisingId, isLimitAdTrackingEnabled);
    }

    @Override
    protected void onPostExecute(AdvertisingInfo advertisingInfo) {
        super.onPostExecute(advertisingInfo);
        if (mListener != null) {
            mListener.onHyBidAdvertisingIdFinish(advertisingInfo.getAdvertisingId(), advertisingInfo.isLimitTrackingEnabled());
        }
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
