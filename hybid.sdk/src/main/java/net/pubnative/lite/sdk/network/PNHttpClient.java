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

import net.pubnative.lite.sdk.HyBid;
import net.pubnative.lite.sdk.utils.Logger;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.Executor;
import java.util.concurrent.RejectedExecutionException;

public class PNHttpClient {

    private static final String TAG = PNHttpClient.class.getSimpleName();

    private static final Handler sUiHandler = new Handler(Looper.getMainLooper());

    private static final Object sQueueLock = new Object();
    private static final Queue<PendingRequest> sPendingRequests = new ArrayDeque<>();

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
        makeRequest(context, url, headers, postBody, shouldReturnOnMainThread, shouldRetryIfFail, ApiExecutor.getInstance(), listener);
    }

    public static void makeRequest(final Context context,
                                   final String url,
                                   final Map<String, String> headers,
                                   final String postBody,
                                   final boolean shouldReturnOnMainThread,
                                   final boolean shouldRetryIfFail,
                                   final Executor executor,
                                   final Listener listener) {
        try {
            executor.execute(() -> {
                if (!isConnected(getActiveNetworkInfo(context))) {
                    notifyNoConnection(listener, shouldReturnOnMainThread);
                    return;
                }

                final Response response = sendRequest(url, headers, postBody);
                deliverResponse(response, url, postBody, headers, shouldReturnOnMainThread, shouldRetryIfFail, executor, listener);
                performPendingRequests(context);
            });
        } catch (RejectedExecutionException exception) {
            Logger.e(TAG, url, exception);
            if (shouldRetryIfFail && !TextUtils.isEmpty(url)) {
                enqueueForRetry(new PendingRequest(url, postBody, headers, MAX_RETRIES, RETRY_MULTIPLIER, executor));
            }
            notifyRejected(listener, shouldReturnOnMainThread);
        }
    }

    private static void deliverResponse(Response response,
                                        String url,
                                        String postBody,
                                        Map<String, String> headers,
                                        boolean shouldReturnOnMainThread,
                                        boolean shouldRetryIfFail,
                                        Executor executor,
                                        Listener listener) {
        if (response.exception != null) {
            if (shouldRetryIfFail && !TextUtils.isEmpty(url)) {
                enqueueForRetry(new PendingRequest(url, postBody, headers, MAX_RETRIES, RETRY_MULTIPLIER, executor));
            }
            if (listener != null) {
                deliver(shouldReturnOnMainThread, () -> listener.onFailure(response.exception));
            }
        } else if (listener != null) {
            deliver(shouldReturnOnMainThread, () -> listener.onSuccess(response.response, response.headers));
        }

        if (listener != null) {
            deliver(shouldReturnOnMainThread, () -> listener.onFinally(url, response.responseCode));
        }
    }

    private static void notifyNoConnection(Listener listener, boolean shouldReturnOnMainThread) {
        if (listener == null) {
            return;
        }
        Exception noConnectionException = new Exception("{\"status\": \"error\", \"error_message\": \"Unable to connect to URL. No network connection.\"}");
        deliver(shouldReturnOnMainThread, () -> listener.onFailure(noConnectionException));
    }

    private static void notifyRejected(Listener listener, boolean shouldReturnOnMainThread) {
        if (listener == null) {
            return;
        }
        Exception tooManyRequestsException = new Exception("{\"status\": \"error\", \"error_message\": \"Unable to connect to URL. Too many requests.\"}");
        deliver(shouldReturnOnMainThread, () -> listener.onFailure(tooManyRequestsException));
    }

    private static void deliver(boolean shouldReturnOnMainThread, Runnable action) {
        if (shouldReturnOnMainThread) {
            sUiHandler.post(action);
        } else {
            action.run();
        }
    }

    private static boolean isConnected(NetworkInfo networkInfo) {
        return networkInfo != null && networkInfo.isConnected()
                && (networkInfo.getType() == ConnectivityManager.TYPE_WIFI || networkInfo.getType() == ConnectivityManager.TYPE_MOBILE);
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

        try {
            ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            if (connectivityManager == null) {
                return null;
            }
            return connectivityManager.getActiveNetworkInfo();
        } catch (SecurityException e) {
            // Don't spam exception reporting for missing permission.
            Logger.e(TAG, "Missing ACCESS_NETWORK_STATE permission", e);
            return null;
        } catch (Exception e) {
            HyBid.reportException(e);
            return null;
        }
    }

    // Snapshots and drains the pending queue under sQueueLock, then processes it outside the lock.
    private static void performPendingRequests(Context context) {
        List<PendingRequest> requestsToProcess;
        synchronized (sQueueLock) {
            requestsToProcess = new ArrayList<>(sPendingRequests);
            sPendingRequests.clear();
        }

        for (PendingRequest pendingRequest : requestsToProcess) {
            makePendingRequest(context, pendingRequest);
        }
    }

    // Not synchronized: sQueueLock already guards the shared queue and PendingRequest guards its own state.
    public static void makePendingRequest(final Context context,
                                          final PendingRequest pendingRequest) {
        if (pendingRequest == null) {
            return;
        }

        // Every re-enqueue path below checks isLimitReached() to bound retries.
        if (!pendingRequest.shouldRetry()) {
            pendingRequest.countAttempt();
            if (!pendingRequest.isLimitReached()) {
                enqueueForRetry(pendingRequest);
            }
            return;
        }

        pendingRequest.countRetry();
        try {
            pendingRequest.getExecutor().execute(() -> {
                if (isConnected(getActiveNetworkInfo(context))) {
                    final Response response = sendRequest(pendingRequest.getUrl(), pendingRequest.getHeaders(), pendingRequest.getPostBody());
                    if (response.exception != null
                            && !pendingRequest.isLimitReached()
                            && !TextUtils.isEmpty(pendingRequest.getUrl())) {
                        enqueueForRetry(pendingRequest);
                    }
                } else {
                    requeueIfWithinLimit(pendingRequest);
                }
            });
        } catch (RejectedExecutionException exception) {
            Logger.e(TAG, pendingRequest.getUrl(), exception);
            requeueIfWithinLimit(pendingRequest);
        }
    }

    private static void requeueIfWithinLimit(PendingRequest pendingRequest) {
        if (!pendingRequest.isLimitReached()) {
            enqueueForRetry(pendingRequest);
        }
    }

    private static void enqueueForRetry(PendingRequest pendingRequest) {
        synchronized (sQueueLock) {
            sPendingRequests.add(pendingRequest);
        }
    }
}
