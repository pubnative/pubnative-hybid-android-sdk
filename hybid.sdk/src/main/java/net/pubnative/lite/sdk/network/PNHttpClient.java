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
import android.os.SystemClock;
import android.text.TextUtils;
import android.util.Log;

import net.pubnative.lite.sdk.HyBid;
import net.pubnative.lite.sdk.utils.Logger;
import net.pubnative.lite.sdk.vpaid.helpers.ExecutorHelper;
import net.pubnative.lite.sdk.vpaid.helpers.RetryScheduler;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Queue;
import java.util.TimeZone;
import java.util.concurrent.Executor;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.TimeUnit;

public class PNHttpClient {

    private static final String TAG = PNHttpClient.class.getSimpleName();

    private static final Handler sUiHandler = new Handler(Looper.getMainLooper());

    private static final Object sQueueLock = new Object();
    private static final Queue<PendingRequest> sPendingRequests = new ArrayDeque<>();
    private static Runnable sWakeUpTask;
    private static long sWakeUpAtMs;
    private static long sNotBeforeMs;

    private static final int READ_TIMEOUT = 10000;
    private static final int CONNECT_TIMEOUT = 10000;
    private static final int MAX_RETRIES = 5;
    private static final int RETRY_MULTIPLIER = 2;
    private static final int HTTP_REQUEST_TIMEOUT = 408;
    private static final int HTTP_TOO_MANY_REQUESTS = 429;
    private static final int HTTP_SERVICE_UNAVAILABLE = 503;
    private static final int HTTP_TEMPORARY_REDIRECT = 307;
    private static final int HTTP_PERMANENT_REDIRECT = 308;
    private static final int MAX_REDIRECTS = 20;
    private static final String HTTP_DATE_FORMAT = "EEE, dd MMM yyyy HH:mm:ss zzz";
    private static final long MIN_WAKE_UP_DELAY_MS = 100L;
    private static final long OFFLINE_RECHECK_DELAY_MS = 30000L;

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
        private boolean transportFailure;
        private boolean failedToConnect;
        private boolean redirected;
        private long retryAfterMs = -1L;

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
        makeRequest(context, url, headers, postBody, shouldReturnOnMainThread, shouldRetryIfFail, RetryPolicy.DEFAULT, executor, listener);
    }

    /**
     * Same as the overload without a policy, but chooses which failures are retried.
     * @param retryPolicy used only when {@code shouldRetryIfFail} is true, null means {@link RetryPolicy#DEFAULT}
     */
    public static void makeRequest(final Context context,
                                   final String url,
                                   final Map<String, String> headers,
                                   final String postBody,
                                   final boolean shouldReturnOnMainThread,
                                   final boolean shouldRetryIfFail,
                                   final RetryPolicy retryPolicy,
                                   final Executor executor,
                                   final Listener listener) {
        try {
            executor.execute(() -> {
                if (!isConnected(getActiveNetworkInfo(context))) {
                    notifyNoConnection(listener, shouldReturnOnMainThread);
                    return;
                }

                final Response response = sendRequest(url, headers, postBody, retryPolicy);
                deliverResponse(context, response, url, postBody, headers, shouldReturnOnMainThread, shouldRetryIfFail, retryPolicy, executor, listener);
                performPendingRequests(context);
            });
        } catch (RejectedExecutionException exception) {
            Logger.w(TAG, "Executor rejected request: " + redactUrl(url), exception);
            if (shouldRetryIfFail && !TextUtils.isEmpty(url)) {
                enqueueForRetry(context, new PendingRequest(url, postBody, headers, MAX_RETRIES, RETRY_MULTIPLIER, executor, retryPolicy));
            }
            notifyRejected(listener, shouldReturnOnMainThread);
        }
    }

    private static void deliverResponse(Context context,
                                        Response response,
                                        String url,
                                        String postBody,
                                        Map<String, String> headers,
                                        boolean shouldReturnOnMainThread,
                                        boolean shouldRetryIfFail,
                                        RetryPolicy retryPolicy,
                                        Executor executor,
                                        Listener listener) {
        if (response.exception != null) {
            if (shouldRetryIfFail && isRetryableFailure(response, retryPolicy) && !TextUtils.isEmpty(url)) {
                PendingRequest pendingRequest = new PendingRequest(url, postBody, headers, MAX_RETRIES, RETRY_MULTIPLIER, executor, retryPolicy);
                applyRetryAfter(pendingRequest, response);
                enqueueForRetry(context, pendingRequest);
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
                                        String request,
                                        RetryPolicy retryPolicy) {

        Response result = new Response();
        boolean followRedirectsManually = followsRedirectsManually(retryPolicy);
        HttpURLConnection urlConnection = null;
        try {
            URL requestUrl = new URL(url);
            String body = request;
            for (int redirects = 0; ; redirects++) {
                urlConnection = openConnection(requestUrl, headers, !TextUtils.isEmpty(body), !followRedirectsManually);

                try {
                    urlConnection.connect();
                } catch (IOException e) {
                    result.failedToConnect = true;
                    throw e;
                }

                if (!TextUtils.isEmpty(body)) {
                    DataOutputStream outputStream = new DataOutputStream(urlConnection.getOutputStream());
                    outputStream.writeBytes(body);
                    outputStream.flush();
                    outputStream.close();
                }

                result.responseCode = urlConnection.getResponseCode();
                Log.d("Response Code: ", String.valueOf(result.getResponseCode()));

                URL redirectUrl = followRedirectsManually ? redirectTarget(urlConnection, requestUrl, result.responseCode) : null;
                if (redirectUrl == null) {
                    break;
                }
                result.redirected = true;
                if (redirects == MAX_REDIRECTS) {
                    throw new ProtocolException("Too many redirects: " + (redirects + 1));
                }
                urlConnection.disconnect();
                requestUrl = redirectUrl;
                if (!keepsRequestBody(result.responseCode)) {
                    body = null;
                }
            }

            int responseCode = result.responseCode;
            if (isHttpSuccess(responseCode)) {
                if (readsResponseBody(retryPolicy)) {
                    InputStream inputStream = urlConnection.getInputStream();
                    result.response = getStringFromStream(inputStream);
                    inputStream.close();
                }
                Map<String, List<String>> responseHeaders = urlConnection.getHeaderFields();
                if (responseHeaders != null && !responseHeaders.isEmpty()) {
                    result.headers = new HashMap<>(responseHeaders);
                }
            } else {
                if (responseCode == HTTP_TOO_MANY_REQUESTS || responseCode == HTTP_SERVICE_UNAVAILABLE) {
                    long serverNowMs = urlConnection.getDate();
                    result.retryAfterMs = parseRetryAfterMs(urlConnection.getHeaderField("Retry-After"),
                            serverNowMs > 0L ? serverNowMs : System.currentTimeMillis());
                }
                result.exception = new Exception(String.format(Locale.ENGLISH, "Network request failed with code: %s", responseCode));
            }
        } catch (Exception e) {
            result.exception = e;
            result.transportFailure = true;
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
        }
        return result;
    }

    private static HttpURLConnection openConnection(URL url,
                                                    Map<String, String> headers,
                                                    boolean hasBody,
                                                    boolean followRedirects) throws IOException {
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        urlConnection.setReadTimeout(READ_TIMEOUT);
        urlConnection.setConnectTimeout(CONNECT_TIMEOUT);
        urlConnection.setInstanceFollowRedirects(followRedirects);
        urlConnection.setRequestMethod("GET"); // optional, GET already by default

        if (headers != null) {
            for (Map.Entry<String, String> entry : headers.entrySet()) {
                urlConnection.addRequestProperty(entry.getKey(), entry.getValue());
            }
        }

        if (hasBody) {
            urlConnection.setDoOutput(true);
            urlConnection.setRequestMethod("POST"); // optional, setDoOutput(true) set value to POST
        }
        return urlConnection;
    }

    private static URL redirectTarget(HttpURLConnection urlConnection, URL requestUrl, int responseCode) throws MalformedURLException {
        if (!isFollowedRedirect(responseCode)) {
            return null;
        }
        String location = urlConnection.getHeaderField("Location");
        if (TextUtils.isEmpty(location)) {
            return null;
        }
        URL target = new URL(requestUrl, location);
        return target.getProtocol().equalsIgnoreCase(requestUrl.getProtocol()) ? target : null;
    }

    private static boolean isFollowedRedirect(int responseCode) {
        switch (responseCode) {
            case HttpURLConnection.HTTP_MULT_CHOICE:
            case HttpURLConnection.HTTP_MOVED_PERM:
            case HttpURLConnection.HTTP_MOVED_TEMP:
            case HttpURLConnection.HTTP_SEE_OTHER:
            case HTTP_TEMPORARY_REDIRECT:
            case HTTP_PERMANENT_REDIRECT:
                return true;
            default:
                return false;
        }
    }

    private static boolean keepsRequestBody(int redirectCode) {
        return redirectCode == HTTP_TEMPORARY_REDIRECT || redirectCode == HTTP_PERMANENT_REDIRECT;
    }

    private static long parseRetryAfterMs(String value, long nowMs) {
        if (TextUtils.isEmpty(value)) {
            return -1L;
        }
        String trimmed = value.trim();
        if (TextUtils.isDigitsOnly(trimmed)) {
            try {
                return TimeUnit.SECONDS.toMillis(Long.parseLong(trimmed));
            } catch (NumberFormatException e) {
                return Long.MAX_VALUE;
            }
        }
        SimpleDateFormat httpDate = new SimpleDateFormat(HTTP_DATE_FORMAT, Locale.US);
        httpDate.setTimeZone(TimeZone.getTimeZone("GMT"));
        httpDate.setLenient(false);
        try {
            return Math.max(0L, httpDate.parse(trimmed).getTime() - nowMs);
        } catch (ParseException e) {
            return -1L;
        }
    }

    private static void applyRetryAfter(PendingRequest pendingRequest, Response response) {
        if (pendingRequest.getRetryPolicy() == RetryPolicy.AVOID_DUPLICATES && response.retryAfterMs >= 0L) {
            pendingRequest.deferAtLeast(response.retryAfterMs);
        }
    }

    private static boolean readsResponseBody(RetryPolicy retryPolicy) {
        return retryPolicy != RetryPolicy.AVOID_DUPLICATES;
    }

    private static boolean followsRedirectsManually(RetryPolicy retryPolicy) {
        return retryPolicy == RetryPolicy.AVOID_DUPLICATES;
    }

    private static boolean isRetryableFailure(Response response, RetryPolicy retryPolicy) {
        if (response == null || response.exception == null) {
            return false;
        }
        if (retryPolicy == RetryPolicy.AVOID_DUPLICATES) {
            return !response.redirected
                    && (response.failedToConnect || isRetryableWithoutDuplicates(response.responseCode));
        }
        if (response.transportFailure || response.responseCode == 0) {
            return true;
        }
        if (response.responseCode == HTTP_REQUEST_TIMEOUT || response.responseCode == HTTP_TOO_MANY_REQUESTS) {
            return true;
        }
        return response.responseCode / 100 == 5;
    }

    private static boolean isRetryableWithoutDuplicates(int responseCode) {
        return responseCode == HTTP_REQUEST_TIMEOUT
                || responseCode == HTTP_TOO_MANY_REQUESTS
                || responseCode == HTTP_SERVICE_UNAVAILABLE;
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
        if (!isConnected(getActiveNetworkInfo(context))) {
            synchronized (sQueueLock) {
                sNotBeforeMs = SystemClock.elapsedRealtime() + OFFLINE_RECHECK_DELAY_MS;
            }
            scheduleQueueWakeUp(context, OFFLINE_RECHECK_DELAY_MS);
            return;
        }

        List<PendingRequest> requestsToProcess;
        synchronized (sQueueLock) {
            sNotBeforeMs = 0L;
            requestsToProcess = new ArrayList<>(sPendingRequests);
            sPendingRequests.clear();
        }

        try {
            for (PendingRequest pendingRequest : requestsToProcess) {
                makePendingRequest(context, pendingRequest);
            }
        } finally {
            scheduleQueueWakeUp(context);
        }
    }

    // Not synchronized: sQueueLock already guards the shared queue and PendingRequest guards its own state.
    public static void makePendingRequest(final Context context,
                                          final PendingRequest pendingRequest) {
        if (pendingRequest == null) {
            return;
        }

        // Filter only: the task claims. Skips a pointless submission for a not-due entry.
        if (!pendingRequest.shouldRetry()) {
            requeueIfWithinLimit(context, pendingRequest);
            return;
        }

        try {
            pendingRequest.getExecutor().execute(() -> {
                // Losing connectivity is not the request's fault, so re-space without claiming.
                if (!isConnected(getActiveNetworkInfo(context))) {
                    pendingRequest.deferWithoutClaiming();
                    requeueIfWithinLimit(context, pendingRequest);
                    return;
                }
                if (!pendingRequest.tryClaimAttempt()) {
                    requeueIfWithinLimit(context, pendingRequest);
                    return;
                }
                final RetryPolicy retryPolicy = pendingRequest.getRetryPolicy();
                final Response response = sendRequest(pendingRequest.getUrl(), pendingRequest.getHeaders(), pendingRequest.getPostBody(), retryPolicy);
                boolean willRetry = isRetryableFailure(response, retryPolicy)
                        && !pendingRequest.isLimitReached()
                        && !TextUtils.isEmpty(pendingRequest.getUrl());
                logRetryOutcome(pendingRequest, response, willRetry);
                if (willRetry) {
                    pendingRequest.scheduleNextAttempt();
                    applyRetryAfter(pendingRequest, response);
                    enqueueForRetry(context, pendingRequest);
                }
            });
        } catch (RejectedExecutionException exception) {
            Logger.w(TAG, "Executor rejected retry: " + redactUrl(pendingRequest.getUrl()), exception);
            // A saturated executor is backpressure, so claim here and let shedding terminate.
            pendingRequest.countRetry();
            requeueIfWithinLimit(context, pendingRequest);
        }
    }

    private static void logRetryOutcome(PendingRequest pendingRequest, Response response, boolean willRetry) {
        int attempts = pendingRequest.getAttemptCount();
        if (response.exception == null) {
            Logger.d(TAG, "Retry succeeded on attempt " + attempts + ": " + redactUrl(pendingRequest.getUrl()));
        } else if (willRetry) {
            Logger.d(TAG, "Retry attempt " + attempts + " failed with code " + response.responseCode
                    + ", re-queueing: " + redactUrl(pendingRequest.getUrl()));
        } else {
            logDrop("Dropping request after " + attempts + " attempt(s), last code " + response.responseCode,
                    pendingRequest, response.exception);
        }
    }

    // Tracker queries carry ad and session IDs, so redact here too even though this is debug only.
    private static void logDrop(String reason, PendingRequest pendingRequest, Exception exception) {
        Logger.w(TAG, reason + ": " + redactUrl(pendingRequest.getUrl()), exception);
    }

    // Keeps scheme, host and path; drops the query and fragment, where the identifiers live.
    private static String redactUrl(String url) {
        if (TextUtils.isEmpty(url)) {
            return "";
        }
        int cut = url.length();
        int queryStart = url.indexOf('?');
        if (queryStart >= 0) {
            cut = queryStart;
        }
        int fragmentStart = url.indexOf('#');
        if (fragmentStart >= 0 && fragmentStart < cut) {
            cut = fragmentStart;
        }
        return cut == url.length() ? url : url.substring(0, cut) + "?[redacted]";
    }

    private static void requeueIfWithinLimit(Context context, PendingRequest pendingRequest) {
        if (pendingRequest.isLimitReached()) {
            logDrop("Dropping unsent request after " + pendingRequest.getAttemptCount() + " attempt(s)",
                    pendingRequest, null);
            return;
        }
        enqueueForRetry(context, pendingRequest);
    }

    private static void enqueueForRetry(Context context, PendingRequest pendingRequest) {
        synchronized (sQueueLock) {
            sPendingRequests.add(pendingRequest);
        }
        scheduleQueueWakeUpFor(context, pendingRequest);
    }

    // An arrival can only pull the wake-up earlier, so min(armed, arrival) needs no queue scan.
    private static void scheduleQueueWakeUpFor(Context context, PendingRequest pendingRequest) {
        final Context appContext = applicationContextOrFallback(context);
        if (appContext == null) {
            return;
        }

        long targetAtMs = pendingRequest.getNextAttemptAtMs();
        synchronized (sQueueLock) {
            armWakeUpLocked(appContext, targetAtMs, MIN_WAKE_UP_DELAY_MS);
        }
    }

    // One wake-up at the queue's earliest deadline; only drains need the full scan.
    private static void scheduleQueueWakeUp(Context context) {
        scheduleQueueWakeUp(context, MIN_WAKE_UP_DELAY_MS);
    }

    private static void scheduleQueueWakeUp(Context context, long minDelayMs) {
        final Context appContext = applicationContextOrFallback(context);
        if (appContext == null) {
            return;
        }

        synchronized (sQueueLock) {
            long earliestAtMs = earliestNextAttemptAtMsLocked();
            if (earliestAtMs < 0L) {
                sNotBeforeMs = 0L;
                if (sWakeUpTask != null) {
                    RetryScheduler.cancel(sWakeUpTask);
                    sWakeUpTask = null;
                    sWakeUpAtMs = 0L;
                }
                return;
            }
            armWakeUpLocked(appContext, earliestAtMs, minDelayMs);
        }
    }

    private static Context applicationContextOrFallback(Context context) {
        Context hyBidContext = HyBid.getApplicationContext();
        if (hyBidContext != null) {
            return hyBidContext;
        }
        return context != null ? context.getApplicationContext() : null;
    }

    private static void armWakeUpLocked(Context context, long targetAtMs, long minDelayMs) {
        long nowMs = SystemClock.elapsedRealtime();
        long delayMs = Math.max(minDelayMs, targetAtMs - nowMs);
        if (sNotBeforeMs > nowMs) {
            delayMs = Math.max(delayMs, sNotBeforeMs - nowMs);
        }
        long wakeUpAtMs = nowMs + delayMs;
        if (sWakeUpTask != null && sWakeUpAtMs <= wakeUpAtMs && sWakeUpAtMs >= sNotBeforeMs) {
            return;
        }
        if (sWakeUpTask != null) {
            RetryScheduler.cancel(sWakeUpTask);
        }
        sWakeUpTask = new QueueWakeUp(context);
        sWakeUpAtMs = wakeUpAtMs;
        RetryScheduler.postDelayed(sWakeUpTask, delayMs);
    }

    private static long earliestNextAttemptAtMsLocked() {
        long earliest = -1L;
        for (PendingRequest pendingRequest : sPendingRequests) {
            long nextAttemptAtMs = pendingRequest.getNextAttemptAtMs();
            if (earliest < 0L || nextAttemptAtMs < earliest) {
                earliest = nextAttemptAtMs;
            }
        }
        return earliest;
    }

    private static final class QueueWakeUp implements Runnable {

        private final Context context;

        QueueWakeUp(Context context) {
            this.context = context.getApplicationContext();
        }

        @Override
        public void run() {
            synchronized (sQueueLock) {
                if (sWakeUpTask == this) {
                    sWakeUpTask = null;
                    sWakeUpAtMs = 0L;
                }
            }
            try {
                ExecutorHelper.getSingleExecutor().execute(() -> performPendingRequests(context));
            } catch (RejectedExecutionException exception) {
                Logger.w(TAG, "Could not hand off the retry drain", exception);
                scheduleQueueWakeUp(context);
            }
        }
    }
}
