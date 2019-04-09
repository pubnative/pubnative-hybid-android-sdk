package net.pubnative.lite.sdk.network;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.lang.ref.WeakReference;
import java.net.URL;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;

public class PNHttpClient extends AsyncTask<String, Void, PNHttpClient.Result> {
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

        NetworkInfo getActiveNetworkInfo();
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

    private Listener mListener;

    private final Method mMethod;
    private final Map<String, String> mHeaders;
    private String mPostBody;

    public PNHttpClient(Method method, Listener listener) {
        this.mMethod = method;
        this.mListener = listener;
        this.mHeaders = new LinkedHashMap<>();
    }

    public void addHeader(String name, String value) {
        this.mHeaders.put(name, value);
    }

    @Override
    protected void onPreExecute() {
        if (mListener != null) {
            NetworkInfo networkInfo = mListener.getActiveNetworkInfo();
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

    private String fetchUrl(URL url) throws IOException {
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

            connection.connect();

            int responseCode = connection.getResponseCode();
            if (!isHttpSuccess(responseCode)) {
                throw new IOException("HTTP error code: " + responseCode);
            }

            stream = connection.getInputStream();
            if (stream != null) {
                result = readStream(stream, 500);
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

    private String readStream(InputStream stream, int maxReadSize) throws IOException, UnsupportedEncodingException {
        Reader reader = null;
        reader = new InputStreamReader(stream, "UTF-8");
        char[] rawbuffer = new char[maxReadSize];
        int readSize;
        StringBuffer buffer = new StringBuffer();
        while (((readSize = reader.read(rawbuffer)) != -1) && maxReadSize > 0) {
            if (readSize > maxReadSize) {
                readSize = maxReadSize;
            }
            buffer.append(rawbuffer, 0, readSize);
            maxReadSize -= readSize;
        }
        return buffer.toString();
    }

    private boolean isHttpSuccess(int responseCode) {
        return responseCode / 100 == 2;
    }
}
