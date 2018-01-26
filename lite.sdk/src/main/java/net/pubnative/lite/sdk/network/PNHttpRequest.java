package net.pubnative.lite.sdk.network;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;

import net.pubnative.lite.sdk.DeviceInfo;
import net.pubnative.lite.sdk.PNLite;
import net.pubnative.lite.sdk.exception.PNException;
import net.pubnative.lite.sdk.utils.PNCrypto;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by erosgarciaponte on 17.01.18.
 */

public class PNHttpRequest {
    private static final String TAG = PNHttpRequest.class.getSimpleName();

    protected int mTimeoutInMillis = 4000; // 4 seconds
    protected String mPOSTString = null;
    // Inner
    protected Listener mListener = null;
    protected Handler mHandler = null;

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

    /**
     * Sets timeout for connection and reading, if not specified default is 0 ms
     *
     * @param timeoutInMillis time in milliseconds
     */
    public void setTimeout(int timeoutInMillis) {
        mTimeoutInMillis = timeoutInMillis;
    }

    public void setPOSTString(String postString) {
        mPOSTString = postString;
    }

    public void start(Context context, final String urlString, Listener listener) {
        mListener = listener;
        mHandler = new Handler(Looper.getMainLooper());
        if (mListener == null) {
            Log.w(TAG, "Warning: null listener specified, performing request without callbacks");
        }
        if (context == null) {
            invokeFail(new IllegalArgumentException("PNAPIHttpRequest - Error: null context provided, dropping call"));
        } else if (TextUtils.isEmpty(urlString)) {
            invokeFail(new IllegalArgumentException("PNAPIHttpRequest - Error: null or empty url, dropping call"));
        } else if (PNLite.getDeviceInfo().getConnectivity() != DeviceInfo.Connectivity.NONE) {
            new Thread(new Runnable() {

                @Override
                public void run() {
                    doRequest(urlString);
                }
            }).start();
        } else {
            invokeFail(PNException.REQUEST_NO_INTERNET);
        }
    }

    protected void doRequest(String urlString) {
        HttpURLConnection connection = null;
        // For avoid changing the POST string
        // during the sending - make a local variable
        String postJson = mPOSTString;
        try {
            // 1. Create connection
            URL url = new URL(urlString);
            connection = (HttpURLConnection) url.openConnection();
            // 2. Set connection properties
            connection.setDoInput(true);
            connection.setConnectTimeout(mTimeoutInMillis);
            if (TextUtils.isEmpty(postJson)) {
                connection.setRequestMethod("GET");
            } else {
                connection.setRequestMethod("POST");
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
            if (responseCode == HttpURLConnection.HTTP_OK) {
                try {
                    InputStream inputStream = connection.getInputStream();
                    String result = stringFromInputStream(inputStream);
                    inputStream.close();
                    invokeFinish(result);
                } catch (PNException ex) {
                    invokeFail(ex);
                }
            } else {
                Map<String, String> errorData = new HashMap<String, String>();
                errorData.put("statusCode", responseCode + "");
                try {
                    errorData.put("errorString", stringFromInputStream(connection.getErrorStream()));
                } catch (PNException ex) {
                    errorData.put("parsingException", ex.toString());
                }
                invokeFail(PNException.extraException(errorData));
            }
        } catch (OutOfMemoryError outOfMemoryError) {
            invokeFail(new Exception("Not enough memory for making request!", outOfMemoryError));
        } catch (Exception exception) {
            invokeFail(exception);
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
    }

    protected String stringFromInputStream(InputStream inputStream) throws PNException {
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
            Log.e(TAG, "stringFromInputStream - Error:" + e);

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

    protected void invokeFail(final Exception exception) {
        mHandler.post(new Runnable() {

            @Override
            public void run() {

                if (mListener != null) {
                    mListener.onPNHttpRequestFail(PNHttpRequest.this, exception);
                }
                mListener = null;
            }
        });
    }
}
