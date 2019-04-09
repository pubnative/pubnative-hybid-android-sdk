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
package net.pubnative.lite.sdk.api;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.SystemClock;

import net.pubnative.lite.sdk.HyBid;
import net.pubnative.lite.sdk.models.Ad;
import net.pubnative.lite.sdk.models.AdRequest;
import net.pubnative.lite.sdk.models.AdResponse;
import net.pubnative.lite.sdk.network.PNHttpClient;
import net.pubnative.lite.sdk.network.PNHttpRequest;
import net.pubnative.lite.sdk.utils.AdRequestRegistry;
import net.pubnative.lite.sdk.utils.PNApiUrlComposer;

import org.json.JSONObject;

/**
 * Created by erosgarciaponte on 17.01.18.
 */

public class PNApiClient {
    private static final String TAG = PNApiClient.class.getSimpleName();

    public interface AdRequestListener {
        void onSuccess(Ad ad);

        void onFailure(Throwable exception);
    }

    public interface TrackUrlListener {
        void onSuccess();

        void onFailure(Throwable throwable);
    }

    private Context mContext;

    public PNApiClient(Context context) {
        this.mContext = context;
    }

    public void getAd(AdRequest request, final AdRequestListener listener) {
        final String url = getAdRequestURL(request);
        if (url == null) {
            if (listener != null) {
                listener.onFailure(new Exception("PNApiClient - Error: invalid request URL"));
            }
        } else {
            final long initTime = System.currentTimeMillis();

            PNHttpClient httpClient = new PNHttpClient(PNHttpClient.Method.GET, new PNHttpClient.Listener() {
                @Override
                public void onSuccess(String response) {
                    registerAdRequest(url, response, initTime);
                    processStream(response, listener);
                }

                @Override
                public void onFailure(Throwable error) {
                    registerAdRequest(url, error.getMessage(), initTime);

                    if (listener != null) {
                        listener.onFailure(error);
                    }
                }

                @Override
                public NetworkInfo getActiveNetworkInfo() {
                    ConnectivityManager connectivityManager = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
                    NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
                    return networkInfo;
                }
            });
            httpClient.execute(url);

            /*PNHttpRequest httpRequest = new PNHttpRequest();
            httpRequest.start(mContext, PNHttpRequest.Method.GET, url, new PNHttpRequest.Listener() {
                @Override
                public void onPNHttpRequestFinish(PNHttpRequest request, String result) {
                    registerAdRequest(url, result, initTime);

                    processStream(result, listener);
                }

                @Override
                public void onPNHttpRequestFail(PNHttpRequest request, Exception exception) {
                    registerAdRequest(url, exception.getMessage(), initTime);

                    if (listener != null) {
                        listener.onFailure(exception);
                    }
                }
            });*/
        }
    }

    public void trackUrl(String url, final TrackUrlListener listener) {
        PNHttpRequest httpRequest = new PNHttpRequest();
        httpRequest.start(mContext, PNHttpRequest.Method.GET, url, new PNHttpRequest.Listener() {
            @Override
            public void onPNHttpRequestFinish(PNHttpRequest request, String result) {
                if (listener != null) {
                    listener.onSuccess();
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

    protected String getAdRequestURL(AdRequest adRequest) {
        return PNApiUrlComposer.buildUrl(HyBid.BASE_URL, adRequest);
    }

    protected void processStream(String result, AdRequestListener listener) {
        AdResponse apiResponseModel = null;
        Exception parseException = null;
        try {
            apiResponseModel = new AdResponse(new JSONObject(result));
        } catch (Exception exception) {
            parseException = exception;
        } catch (Error error) {
            parseException = new Exception("Response cannot be parsed", error);
        }
        if (parseException != null) {
            listener.onFailure(parseException);
        } else if (apiResponseModel == null) {
            listener.onFailure(new Exception("PNApiClient - Parse error"));
        } else if (AdResponse.Status.OK.equals(apiResponseModel.status)) {
            // STATUS 'OK'
            if (apiResponseModel.ads != null && !apiResponseModel.ads.isEmpty()) {
                listener.onSuccess(apiResponseModel.ads.get(0));
            } else {
                listener.onFailure(new Exception("HyBid - No fill"));
            }
        } else {
            // STATUS 'ERROR'
            listener.onFailure(new Exception("HyBid - Server error: " + apiResponseModel.error_message));
        }
    }

    private void registerAdRequest(String url, String response, long initTime) {
        AdRequestRegistry.getInstance().setLastAdRequest(url, response, System.currentTimeMillis() - initTime);
    }
}
