// HyBid SDK License
//
// https://github.com/pubnative/pubnative-hybid-android-sdk/blob/main/LICENSE
//
package net.pubnative.lite.sdk.network;

import java.util.Map;
import java.util.concurrent.Executor;

public class PendingRequest {
    private final String url;
    private final Map<String, String> headers;
    private final String postBody;
    private final int maxRetries;
    private final int multiplier;
    private final Executor executor;
    private int retryCount;
    private int offset;

    public PendingRequest(String url, String postBody, Map<String, String> headers, int maxRetries, int multiplier) {
        this(url, postBody, headers, maxRetries, multiplier, ApiExecutor.getInstance());
    }

    public PendingRequest(String url, String postBody, Map<String, String> headers, int maxRetries, int multiplier, Executor executor) {
        this.url = url;
        this.headers = headers;
        this.postBody = postBody;
        this.maxRetries = maxRetries;
        this.multiplier = multiplier;
        this.executor = executor;
        this.retryCount = 1;
        this.offset = 0;
    }

    public String getUrl() {
        return url;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public String getPostBody() {
        return postBody;
    }

    public Executor getExecutor() {
        return executor;
    }

    public synchronized boolean shouldRetry() {
        return offset <= 0;
    }

    public synchronized boolean isLimitReached() {
        return retryCount > maxRetries;
    }

    public synchronized void countAttempt() {
        offset--;
    }

    public synchronized void countRetry() {
        offset = multiplier * retryCount;
        retryCount++;
    }
}
