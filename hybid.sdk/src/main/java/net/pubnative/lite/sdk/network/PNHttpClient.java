package net.pubnative.lite.sdk.network;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.text.TextUtils;

import net.pubnative.lite.sdk.exception.PNException;
import net.pubnative.lite.sdk.utils.Logger;
import net.pubnative.lite.sdk.utils.PNCrypto;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.lang.ref.WeakReference;
import java.net.URL;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;

public class PNHttpClient extends AsyncTask<String, Void, PNHttpClient.Result> {
    private static final String TAG = PNHttpClient.class.getSimpleName();

    public enum Method {
        GET("GET"),
        POST("POST"),
        UPDATE("UPDATE"),
        DELETE("DELETE");

        private String mValue;

        private Method(String value) {
            this.mValue = value;
        }

        @Override
        public String toString() {
            return mValue;
        }
    }

    public interface Listener {
        void onSuccess(String response);

        void onFailure(Throwable error);
    }

    static final class Result {
        private String mResultValue;
        private Exception mException;

        public Result(String resultValue) {
            mResultValue = resultValue;
        }

        public Result(Exception exception) {
            mException = exception;
        }

        public String getResultValue() {
            return mResultValue;
        }

        public Exception getException() {
            return mException;
        }
    }

    private WeakReference<Context> mContextRef;
    private Listener mListener;

    private final Method mMethod;
    private final Map<String, String> mHeaders;
    private String mPostBody;

    public PNHttpClient(Context context, Method method, Listener listener) {
        this.mContextRef = new WeakReference<>(context);
        this.mMethod = method;
        this.mListener = listener;
        this.mHeaders = new LinkedHashMap<>();
    }

    public void addHeader(String name, String value) {
        if (!TextUtils.isEmpty(name) && !TextUtils.isEmpty(value)) {
            this.mHeaders.put(name, value);
        }
    }

    public void setPostBody(String postBody) {
        if (!TextUtils.isEmpty(postBody)) {
            mPostBody = postBody;
        }
    }

    @Override
    protected void onPreExecute() {
        if (mListener != null) {
            NetworkInfo networkInfo = getActiveNetworkInfo();
            if (networkInfo == null || !networkInfo.isConnected()
                    || (networkInfo.getType() != ConnectivityManager.TYPE_WIFI && networkInfo.getType() != ConnectivityManager.TYPE_MOBILE)) {
                mListener.onFailure(new Exception("Unable to connect to URL. No network connection."));
                cancel(true);
            }
        }
    }

    @Override
    protected Result doInBackground(String... urls) {
        Result result = null;

        if (!isCancelled() && urls != null && urls.length > 0) {
            String urlString = urls[0];
            try {
                URL url = new URL(urlString);
                String resultString = fetchUrl(url);
                if (resultString != null) {
                    result = new Result(resultString);
                } else {
                    throw new IOException("No response received.");
                }
            } catch (Exception exception) {
                result = new Result(exception);
            }
        }

        return result;
    }

    @Override
    protected void onPostExecute(Result result) {
        if (result != null && mListener != null) {
            if (result.getException() != null) {
                mListener.onFailure(result.getException());
            } else if (result.getResultValue() != null) {
                mListener.onSuccess(result.getResultValue());
            } else {
                mListener.onFailure(new Exception("An error has occurred trying to fetch the data from the network."));
            }
        }
    }

    private String fetchUrl(URL url) throws Exception {
        InputStream stream = null;
        HttpsURLConnection connection = null;
        String result = null;

        try {
            connection = (HttpsURLConnection) url.openConnection();
            //connection.setReadTimeout(4000);
            connection.setConnectTimeout(4000);
            connection.setRequestMethod(mMethod.toString());

            if (!mHeaders.isEmpty()) {
                for (String header : mHeaders.keySet()) {
                    connection.setRequestProperty(header, mHeaders.get(header));
                }
            }

            connection.setDoInput(true);

            if (!TextUtils.isEmpty(mPostBody)) {
                connection.setUseCaches(false);
                connection.setDoOutput(true);
                connection.setRequestProperty("Content-Length", Integer.toString(mPostBody.getBytes().length));
                connection.setRequestProperty("Content-MD5", PNCrypto.md5(mPostBody));
                OutputStream connectionOutputStream = connection.getOutputStream();
                OutputStreamWriter wr = new OutputStreamWriter(connectionOutputStream, "UTF-8");
                wr.write(mPostBody);
                wr.flush();
                wr.close();
            }

            connection.connect();

            int responseCode = connection.getResponseCode();
            if (!isHttpSuccess(responseCode)) {
                throw new IOException("HTTP error code: " + responseCode);
            }

            stream = connection.getInputStream();
            if (stream != null) {
                result = stringFromInputStream(stream);
            }

        } finally {
            if (stream != null) {
                stream.close();
            }

            if (connection != null) {
                connection.disconnect();
            }
        }

        return result;
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

    private boolean isHttpSuccess(int responseCode) {
        return responseCode / 100 == 2;
    }

    private NetworkInfo getActiveNetworkInfo() {
        if (mContextRef.get() == null) {
            return null;
        }

        ConnectivityManager connectivityManager = (ConnectivityManager) mContextRef.get().getSystemService(Context.CONNECTIVITY_SERVICE);
        return connectivityManager.getActiveNetworkInfo();
    }
}
