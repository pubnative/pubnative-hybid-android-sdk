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
package net.pubnative.lite.sdk.nativead;

import net.pubnative.lite.sdk.api.NativeRequestManager;
import net.pubnative.lite.sdk.api.RequestManager;
import net.pubnative.lite.sdk.models.Ad;
import net.pubnative.lite.sdk.models.NativeAd;

public class HyBidNativeAdRequest implements RequestManager.RequestListener {

    public interface RequestListener {
        void onRequestSuccess(NativeAd ad);

        void onRequestFail(Throwable throwable);
    }

    private RequestListener mListener;
    private RequestManager mRequestManager;

    public HyBidNativeAdRequest() {
        this.mRequestManager = new NativeRequestManager();
        this.mRequestManager.setRequestListener(this);
    }

    public void load(String zoneId, RequestListener listener) {
        mListener = listener;
        mRequestManager.setZoneId(zoneId);
        mRequestManager.requestAd();
    }

    @Override
    public void onRequestSuccess(Ad ad) {
        createNativeAd(ad);
    }

    @Override
    public void onRequestFail(Throwable throwable) {
        if (mListener != null) {
            mListener.onRequestFail(throwable);
        }
    }

    private void createNativeAd(Ad ad) {
        if (mListener != null) {
            mListener.onRequestSuccess(new NativeAd(ad));
        }
    }
}
