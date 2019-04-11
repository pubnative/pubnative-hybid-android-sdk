package net.pubnative.lite.sdk.network;

import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;

import net.pubnative.lite.sdk.utils.Logger;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class PNHttpExecutor {
    private static ExecutorService sExecutor = Executors.newCachedThreadPool();
    private static Handler sUiHandler = new Handler(Looper.getMainLooper());

    private static final int READ_TIMEOUT = 10000;
    private static final int CONNECT_TIMEOUT = 10000;

    public interface Listener {
        void onSuccess(String response);

        void onFailure(Throwable error);
    }

    private static class Response {

        private int responseCode;
        private String response;
        private Exception exception;

        private Response() {
        }

        public int getResponseCode() {
            return responseCode;
        }

        public String getResponse() {
            return response;
        }

        public Exception getException() {
            return exception;
        }
    }

    public static void makeRequest(final String url,
                                   final Map<String, String> headers,
                                   final String postBody,
                                   final Listener listener) {
        makeRequest(url, headers, postBody, true, listener);
    }

    public static void makeRequest(final String url,
                                   final Map<String, String> headers,
                                   final String postBody,
                                   final boolean shouldReturnOnMainThread,
                                   final Listener listener) {
        sExecutor.submit(new Runnable() {
            @Override
            public void run() {
                final Response response = sendRequest(url, headers, postBody);
                if (response.exception != null) {
                    if (shouldReturnOnMainThread) {
                        sUiHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                listener.onFailure(response.exception);
                            }
                        });
                    } else {
                        listener.onFailure(response.exception);
                    }
                } else {
                    if (shouldReturnOnMainThread) {
                        sUiHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                listener.onSuccess(response.response);
                            }
                        });
                    } else {
                        listener.onSuccess(response.response);
                    }
                }
            }
        });
    }

    private static Response sendRequest(String url,
                                       Map<String, String> headers,
                                       String request) {
        Response result = new Response();
        HttpURLConnection urlConnection = null;
        try {
            URL requestUrl = new URL(url);
            urlConnection = (HttpURLConnection) requestUrl.openConnection();
            urlConnection.setReadTimeout(READ_TIMEOUT);
            urlConnection.setConnectTimeout(CONNECT_TIMEOUT);
            urlConnection.setRequestMethod("GET"); // optional, GET already by default

            if (headers != null) {
                for (Map.Entry<String, String> entry : headers.entrySet()) {
                    urlConnection.addRequestProperty(entry.getKey(), entry.getValue());
                }
            }

            if (!TextUtils.isEmpty(request)) {
                urlConnection.setDoOutput(true);
                urlConnection.setRequestMethod("POST"); // optional, setDoOutput(true) set value to POST
                DataOutputStream outputStream = new DataOutputStream(urlConnection.getOutputStream());
                outputStream.writeBytes(request);
                outputStream.flush();
                outputStream.close();
            }

            int responseCode = urlConnection.getResponseCode();
            result.responseCode = responseCode;
            Logger.d("HTTP util", "ResponseCode:" + responseCode);

            if (responseCode == HttpURLConnection.HTTP_OK) {
                result.response = getStringFromStream(urlConnection.getInputStream());
            }
        } catch (Exception e) {
            result.exception = e;
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
        }
        return result;
    }

    private static String getStringFromStream(InputStream inputStream) throws IOException {
        final int BUFFER_SIZE = 4096;
        ByteArrayOutputStream resultStream = new ByteArrayOutputStream(BUFFER_SIZE);
        byte[] buffer = new byte[BUFFER_SIZE];
        int length;
        while ((length = inputStream.read(buffer)) != -1) {
            resultStream.write(buffer, 0, length);
        }
        return resultStream.toString("UTF-8");
    }
}
