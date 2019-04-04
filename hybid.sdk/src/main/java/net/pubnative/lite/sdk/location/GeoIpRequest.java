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
package net.pubnative.lite.sdk.location;

import android.content.Context;
import android.net.Uri;

import net.pubnative.lite.sdk.network.PNHttpRequest;

public class GeoIpRequest {

    public interface GeoIpRequestListener {
        void onSuccess(String countryCode);

        void onFailure(Throwable exception);
    }

    public void fetchGeoIp(Context context, final GeoIpRequestListener listener) {
        PNHttpRequest request = new PNHttpRequest();
        request.start(context, PNHttpRequest.Method.GET, getEndpointUrl(), new PNHttpRequest.Listener() {
            @Override
            public void onPNHttpRequestFinish(PNHttpRequest request, String result) {
                if (listener != null) {
                    listener.onSuccess(result);
                }
            }

            @Override
            public void onPNHttpRequestFail(PNHttpRequest request, Exception exception) {
                if (listener != null) {
                    listener.onFailure(exception);
                }
            }
        });
    }

    private String getEndpointUrl() {
        return new Uri.Builder()
                .scheme("https")
                .authority("pubnative.info")
                .appendPath("country")
                .build()
                .toString();
    }
}
