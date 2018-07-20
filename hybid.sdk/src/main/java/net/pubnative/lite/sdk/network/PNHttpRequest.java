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
package net.pubnative.lite.sdk.network;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;

import net.pubnative.lite.sdk.DeviceInfo;
import net.pubnative.lite.sdk.HyBid;
import net.pubnative.lite.sdk.exception.PNException;
import net.pubnative.lite.sdk.utils.Logger;
import net.pubnative.lite.sdk.utils.PNCrypto;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * Created by erosgarciaponte on 17.01.18.
 */

public class PNHttpRequest {
    private static final String TAG = PNHttpRequest.class.getSimpleName();

    protected final int MAX_RETRIES = 1;
    protected int mTimeoutInMillis = 4000; // 4 seconds
    protected String mPOSTString = null;
    protected Map<String, String> mHeaders = null;
    // Inner
    protected Listener mListener = null;
    protected Handler mHandler = null;
    protected boolean mShouldRetry;
    protected int mRetryCount;

    protected String mMethod;
    protected String mUrl;

    public interface Method {
        String GET = "GET";
        String POST = "POST";
        String DELETE = "DELETE";
    }

    public interface Listener {

        /**
         * Called when the HttpRequest has just finished with a valid String response
         *
         * @param request request that have just finished
         * @param result  string with the given response from the server
         */
        void onPNHttpRequestFinish(PNHttpRequest request, String result);

        /**
         * Called when the HttpRequest fails, after this method the request will be stopped
         *
         * @param request   request that have just failed
         * @param exception exception with more info about the error
         */
        void onPNHttpRequestFail(PNHttpRequest request, Exception exception);
    }

    public PNHttpRequest() {
        mShouldRetry = false;
        mRetryCount = 0;
    }

    /**
     * Sets timeout for connection and reading, if not specified default is 0 ms
     *
     * @param timeoutInMillis time in milliseconds
     */
    public void setTimeout(int timeoutInMillis) {
        mTimeoutInMillis = timeoutInMillis;
    }

    public void shouldRetry(boolean shouldRetry) {
        mShouldRetry = shouldRetry;
    }

    public void setPOSTString(String postString) {
        mPOSTString = postString;
    }

    public void setHeaders(Map<String, String> headers) {
        mHeaders = headers;
    }

    public void start(Context context, final String method, final String urlString, Listener listener) {
        mListener = listener;
        mMethod = method;
        mUrl = urlString;
        mHandler = new Handler(Looper.getMainLooper());
        if (mListener == null) {
            Logger.w(TAG, "Warning: null listener specified, performing request without callbacks");
        }
        if (context == null) {
            invokeFail(new IllegalArgumentException("PNAPIHttpRequest - Error: null context provided, dropping call"), false);
        } else if (TextUtils.isEmpty(urlString)) {
            invokeFail(new IllegalArgumentException("PNAPIHttpRequest - Error: null or empty url, dropping call"), false);
        } else if (!validateMethod(method)) {
            invokeFail(new IllegalArgumentException("HttpRequest - Error: Unsupported HTTP method, dropping call"), false);
        } else if (HyBid.getDeviceInfo().getConnectivity() != DeviceInfo.Connectivity.NONE) {
            executeAsync();
        } else {
            invokeFail(PNException.REQUEST_NO_INTERNET, true);
        }
    }

    private void executeAsync() {
        new Thread(new Runnable() {

            @Override
            public void run() {
                doRequest(mMethod, mUrl);
            }
        }).start();
    }

    private boolean validateMethod(String method) {
        if (TextUtils.isEmpty(method)) {
            return false;
        }
        switch (method.toUpperCase(Locale.ENGLISH)) {
            case Method.GET:
            case Method.POST:
            case Method.DELETE:
                return true;
            default:
                return false;
        }
    }

    protected void doRequest(String method, String urlString) {
        HttpURLConnection connection = null;
        // For avoid changing the POST string
        // during the sending - make a local variable
        String postJson = mPOSTString;
        Map<String, String> headers = mHeaders;
        try {
            // 1. Create connection
            URL url = new URL(urlString);
            connection = (HttpURLConnection) url.openConnection();
            // 2. Set connection properties
            connection.setDoInput(true);
            connection.setConnectTimeout(mTimeoutInMillis);
            connection.setRequestMethod(method);

            if (headers != null && !headers.isEmpty()) {
                for (String header : headers.keySet()) {
                    connection.setRequestProperty(header, headers.get(header));
                }
            }

            if (!TextUtils.isEmpty(postJson)) {
                connection.setUseCaches(false);
                connection.setDoOutput(true);
                connection.setRequestProperty("Content-Length", Integer.toString(postJson.getBytes().length));
                connection.setRequestProperty("Content-MD5", PNCrypto.md5(postJson));
                OutputStream connectionOutputStream = connection.getOutputStream();
                OutputStreamWriter wr = new OutputStreamWriter(connectionOutputStream, "UTF-8");
                wr.write(postJson);
                wr.flush();
                wr.close();
            }
            // 3. Do request
            connection.connect();
            int responseCode = connection.getResponseCode();
            if (isHttpSuccess(responseCode)) {
                try {
                    InputStream inputStream = connection.getInputStream();
                    String result = stringFromInputStream(inputStream);
                    inputStream.close();
                    invokeFinish(result);
                } catch (PNException ex) {
                    invokeFail(ex, false);
                }
            } else {
                Map<String, String> errorData = new HashMap<String, String>();
                errorData.put("statusCode", responseCode + "");
                try {
                    errorData.put("errorString", stringFromInputStream(connection.getErrorStream()));
                } catch (PNException ex) {
                    errorData.put("parsingException", ex.toString());
                }
                invokeFail(PNException.extraException(errorData), false);
            }
        } catch (OutOfMemoryError outOfMemoryError) {
            invokeFail(new Exception("Not enough memory for making request!", outOfMemoryError), true);
        } catch (Exception exception) {
            invokeFail(exception, false);
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
    }

    protected boolean isHttpSuccess(int responseCode) {
        return responseCode / 100 == 2;
    }

    protected String stringFromInputStream(InputStream inputStream) throws PNException {
        if (inputStream == null) {
            return "";
        }
        String result = null;
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        int length;
        try {
            byte[] buffer = new byte[1024];
            while ((length = inputStream.read(buffer)) != -1) {
                byteArrayOutputStream.write(buffer, 0, length);
            }
            byteArrayOutputStream.flush();
            result = byteArrayOutputStream.toString();
            byteArrayOutputStream.close();
        } catch (IOException e) {
            Logger.e(TAG, "stringFromInputStream - Error:" + e);

            Map<String, String> errorData = new HashMap<>();
            if (result == null) {
                result = byteArrayOutputStream.toString();
            }
            errorData.put("serverResponse", result);
            errorData.put("IOException", e.toString());
            throw PNException.extraException(errorData);
        }
        return result;
    }

    protected void invokeFinish(final String result) {
        mHandler.post(new Runnable() {

            @Override
            public void run() {

                if (mListener != null) {
                    mListener.onPNHttpRequestFinish(PNHttpRequest.this, result);
                }
                mListener = null;
            }
        });
    }

    protected void invokeFail(final Exception exception, final boolean attemptRetry) {
        mHandler.post(new Runnable() {

            @Override
            public void run() {
                if (attemptRetry && mShouldRetry && mRetryCount < MAX_RETRIES) {
                    executeAsync();
                    mRetryCount++;
                } else {
                    if (mListener != null) {
                        mListener.onPNHttpRequestFail(PNHttpRequest.this, exception);
                    }
                    mListener = null;
                }
            }
        });
    }
}
