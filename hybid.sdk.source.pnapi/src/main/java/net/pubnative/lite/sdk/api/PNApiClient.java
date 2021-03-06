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

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;
import android.text.TextUtils;
import android.webkit.WebView;

import net.pubnative.lite.sdk.ErrorMessages;
import net.pubnative.lite.sdk.models.Ad;
import net.pubnative.lite.sdk.models.AdRequest;
import net.pubnative.lite.sdk.models.AdResponse;
import net.pubnative.lite.sdk.network.PNHttpClient;
import net.pubnative.lite.sdk.source.pnapi.BuildConfig;
import net.pubnative.lite.sdk.utils.AdRequestRegistry;
import net.pubnative.lite.sdk.utils.PNApiUrlComposer;

import org.json.JSONObject;

/**
 * Created by erosgarciaponte on 17.01.18.
 */

public class PNApiClient {

    public interface AdRequestListener {
        void onSuccess(Ad ad);

        void onFailure(Throwable exception);
    }

    public interface TrackUrlListener {
        void onSuccess();

        void onFailure(Throwable throwable);
    }

    public interface TrackJSListener {
        void onSuccess();

        void onFailure(Throwable throwable);
    }

    private final Context mContext;
    private String mApiUrl = BuildConfig.BASE_URL;

    public String getApiUrl() {
        return mApiUrl;
    }

    void setApiUrl(String url) {
        if (!TextUtils.isEmpty(url)) {
            mApiUrl = url;
        }
    }

    public PNApiClient(Context context) {
        this.mContext = context;
    }

    public void getAd(AdRequest request, final AdRequestListener listener) {
        final String url = getAdRequestURL(request);
        getAd(url, listener);
    }

    public void getAd(final String url, final AdRequestListener listener) {
        if (TextUtils.isEmpty(url)) {
            if (listener != null) {
                listener.onFailure(new Exception("PNApiClient - Error: invalid request URL"));
            }
        } else {
            final long initTime = System.currentTimeMillis();

            PNHttpClient.makeRequest(mContext, url, null, null, new PNHttpClient.Listener() {
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
            });
        }
    }

    public Context getContext() {
        return mContext;
    }

    public void trackUrl(String url, final TrackUrlListener listener) {
        sendTrackingRequest(url, listener);
    }

    private void sendTrackingRequest(String url, final TrackUrlListener listener) {
        PNHttpClient.makeRequest(mContext, url, null, null, false, true, new PNHttpClient.Listener() {
            @Override
            public void onSuccess(String response) {
                if (listener != null) {
                    listener.onSuccess();
                }
            }

            @Override
            public void onFailure(Throwable error) {
                if (listener != null) {
                    listener.onFailure(error);
                }
            }
        });
    }

    @SuppressLint("SetJavaScriptEnabled")
    public void trackJS(String js, final TrackJSListener listener) {
        if (TextUtils.isEmpty(js)) {
            if (listener != null) {
                listener.onFailure(new Exception("Empty JS tracking beacon"));
            }
        } else {
            try {
                WebView webView = new WebView(mContext);
                webView.getSettings().setJavaScriptEnabled(true);

                String processedJS = processJS(js);
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
                    webView.loadUrl("javascript:" + processedJS);
                } else {
                    webView.evaluateJavascript(processedJS, null);
                }

                if (listener != null) {
                    listener.onSuccess();
                }
            } catch (RuntimeException exception) {
                if (listener != null) {
                    listener.onFailure(new Exception("Error tracking JS beacon. No webview to evaluate JS."));
                }
            }
        }
    }

    private String processJS(String js) {
        String scriptOpen = "<script>";
        String scriptClose = "</script>";

        String processed = js.replace(scriptOpen, "");
        processed = processed.replace(scriptClose, "");

        return processed;
    }

    protected String getAdRequestURL(AdRequest adRequest) {
        return PNApiUrlComposer.buildUrl(mApiUrl, adRequest);
    }

    protected void processStream(String result, AdRequestListener listener) {
        AdResponse apiResponseModel = null;
        Exception parseException = null;
        try {
            apiResponseModel = new AdResponse(new JSONObject(result));
        } catch (Exception exception) {
            parseException = exception;
        } catch (Error error) {
            parseException = new Exception(ErrorMessages.PARSER_ERROR, error);
        }
        if (parseException != null) {
            listener.onFailure(parseException);
        } else if (apiResponseModel == null) {
            listener.onFailure(new Exception(ErrorMessages.PARSER_ERROR));
        } else if (AdResponse.Status.OK.equals(apiResponseModel.status)) {
            // STATUS 'OK'
            if (apiResponseModel.ads != null && !apiResponseModel.ads.isEmpty()) {
                listener.onSuccess(apiResponseModel.ads.get(0));
            } else {
                listener.onFailure(new Exception(ErrorMessages.NO_FILL));
            }
        } else {
            // STATUS 'ERROR'
            listener.onFailure(new Exception(ErrorMessages.SERVER_ERROR_PREFIX + apiResponseModel.error_message));
        }
    }

    private void registerAdRequest(String url, String response, long initTime) {
        AdRequestRegistry.getInstance().setLastAdRequest(url, response, System.currentTimeMillis() - initTime);
    }
}
