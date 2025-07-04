// HyBid SDK License
//
// https://github.com/pubnative/pubnative-hybid-android-sdk/blob/main/LICENSE
//
package net.pubnative.lite.sdk.network;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;

import net.pubnative.lite.sdk.utils.Logger;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayDeque;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.RejectedExecutionException;

public class PNHttpClient {

    private static final String TAG = PNHttpClient.class.getSimpleName();

    private static final Handler sUiHandler = new Handler(Looper.getMainLooper());

    private static final Queue<PendingRequest> sPendingRequests = new ArrayDeque<>();
    private static final Queue<PendingRequest> sCurrentRequests = new ArrayDeque<>();

    private static final int READ_TIMEOUT = 10000;
    private static final int CONNECT_TIMEOUT = 10000;
    private static final int MAX_RETRIES = 5;
    private static final int RETRY_MULTIPLIER = 2;

    public interface Listener {
        void onSuccess(String response, Map<String, List<String>> headers);

        void onFailure(Throwable error);

        default void onFinally(String requestUrl, int responseCode) {
        }
    }

    private static class Response {

        private int responseCode;
        private String response;
        private Map<String, List<String>> headers;
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

    public static void makeRequest(final Context context,
                                   final String url,
                                   final Map<String, String> headers,
                                   final String postBody,
                                   final Listener listener) {
        makeRequest(context, url, headers, postBody, true, listener);
    }

    public static void makeRequest(final Context context,
                                   final String url,
                                   final Map<String, String> headers,
                                   final String postBody,
                                   final boolean shouldReturnOnMainThread,
                                   final Listener listener) {

        makeRequest(context, url, headers, postBody, shouldReturnOnMainThread, false, listener);
    }

    public static void makeRequest(final Context context,
                                   final String url,
                                   final Map<String, String> headers,
                                   final String postBody,
                                   final boolean shouldReturnOnMainThread,
                                   final boolean shouldRetryIfFail,
                                   final Listener listener) {
        NetworkInfo networkInfo = getActiveNetworkInfo(context);
        if (networkInfo == null || !networkInfo.isConnected()
                || (networkInfo.getType() != ConnectivityManager.TYPE_WIFI && networkInfo.getType() != ConnectivityManager.TYPE_MOBILE)) {
            if (listener != null) {
                listener.onFailure(new Exception("{\"status\": \"error\", \"error_message\": \"Unable to connect to URL. No network connection.\"}"));
            }
        } else {
            try {
                ApiExecutor.getInstance().execute(() -> {
                    final Response response = sendRequest(url, headers, postBody);
                    if (response.exception != null) {
                        if (shouldRetryIfFail && !TextUtils.isEmpty(url)) {
                            sPendingRequests.add(new PendingRequest(url, postBody, headers, MAX_RETRIES, RETRY_MULTIPLIER));
                        }

                        if (shouldReturnOnMainThread) {
                            sUiHandler.post(() -> {
                                if (listener != null) {
                                    listener.onFailure(response.exception);
                                }
                            });
                        } else {
                            if (listener != null) {
                                listener.onFailure(response.exception);
                            }
                        }
                    } else {
                        if (shouldReturnOnMainThread) {
                            sUiHandler.post(() -> {
                                if (listener != null) {
                                    listener.onSuccess(response.response, response.headers);
                                }
                            });
                        } else {
                            if (listener != null) {
                                listener.onSuccess(response.response, response.headers);
                            }
                        }
                    }
                    if (listener != null) {
                        listener.onFinally(url, response.responseCode);
                    }
                    performPendingRequests(context);
                });
            } catch (RejectedExecutionException exception) {
                Logger.e(TAG, url, exception);
                listener.onFailure(new Exception("{\"status\": \"error\", \"error_message\": \"Unable to connect to URL. Too many requests.\"}"));
            }
        }
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
            urlConnection.setInstanceFollowRedirects(true);
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
            Log.d("Response Code: ", String.valueOf(result.getResponseCode()));

            if (isHttpSuccess(responseCode)) {
                InputStream inputStream = urlConnection.getInputStream();
                result.response = getStringFromStream(inputStream);
                inputStream.close();
                Map<String, List<String>> responseHeaders = urlConnection.getHeaderFields();
                if (responseHeaders != null && !responseHeaders.isEmpty()) {
                    result.headers = new HashMap<>(responseHeaders);
                }
            } else {
                result.exception = new Exception(String.format(Locale.ENGLISH, "Network request failed with code: %s", responseCode));
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

    private static boolean isHttpSuccess(int responseCode) {
        return responseCode / 100 == 2;
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

    private static NetworkInfo getActiveNetworkInfo(Context context) {
        if (context == null) {
            return null;
        }

        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        return connectivityManager.getActiveNetworkInfo();
    }

    private static void performPendingRequests(Context context) {
        if (sCurrentRequests.isEmpty() && !sPendingRequests.isEmpty()) {
            sCurrentRequests.addAll(sPendingRequests);
            sPendingRequests.clear();
        }

        if (!sCurrentRequests.isEmpty()) {
            for (PendingRequest pendingRequest : sCurrentRequests) {
                makePendingRequest(context, pendingRequest);
            }
            sCurrentRequests.clear();
        }
    }

    public static synchronized void makePendingRequest(final Context context,
                                                       final PendingRequest pendingRequest) {
        if (pendingRequest != null) {
            if (pendingRequest.shouldRetry()) {
                NetworkInfo networkInfo = getActiveNetworkInfo(context);
                if (networkInfo != null && networkInfo.isConnected()
                        && (networkInfo.getType() == ConnectivityManager.TYPE_WIFI || networkInfo.getType() == ConnectivityManager.TYPE_MOBILE)) {
                    try {
                        ApiExecutor.getInstance().execute(() -> {
                            pendingRequest.countRetry();
                            final Response response = sendRequest(pendingRequest.getUrl(), pendingRequest.getHeaders(), pendingRequest.getPostBody());
                            if (response.exception != null
                                    && !pendingRequest.isLimitReached()
                                    && !TextUtils.isEmpty(pendingRequest.getUrl())) {
                                sPendingRequests.add(pendingRequest);
                            }
                        });
                    } catch (RejectedExecutionException exception) {
                        Logger.e(TAG, pendingRequest.getUrl(), exception);
                    }
                }
            } else {
                pendingRequest.countAttempt();
                sPendingRequests.add(pendingRequest);
            }
        }
    }
}