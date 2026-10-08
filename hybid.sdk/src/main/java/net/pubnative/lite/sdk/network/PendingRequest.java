// HyBid SDK License
//
// https://github.com/pubnative/pubnative-hybid-android-sdk/blob/main/LICENSE
//
package net.pubnative.lite.sdk.network;

import android.os.SystemClock;

import net.pubnative.lite.sdk.utils.RetryBackoff;

import java.util.Map;
import java.util.Random;
import java.util.concurrent.Executor;
import java.util.concurrent.ThreadLocalRandom;

public class PendingRequest {

    private static final long BASE_RETRY_DELAY_MS = 1000L;
    private static final long MAX_RETRY_DELAY_MS = 60000L;
    private static final double JITTER_FRACTION = 0.2d;

    /** Null in production, where jitter comes from {@link ThreadLocalRandom}; tests pin a deterministic source. */
    static Random jitterRandom;

    private final String url;
    private final Map<String, String> headers;
    private final String postBody;
    private final int maxRetries;
    private final int multiplier;
    private final Executor executor;
    private final RetryPolicy retryPolicy;
    private int retryCount;
    private long nextAttemptAtMs;

    public PendingRequest(String url, String postBody, Map<String, String> headers, int maxRetries, int multiplier) {
        this(url, postBody, headers, maxRetries, multiplier, ApiExecutor.getInstance());
    }

    public PendingRequest(String url, String postBody, Map<String, String> headers, int maxRetries, int multiplier, Executor executor) {
        this(url, postBody, headers, maxRetries, multiplier, executor, RetryPolicy.DEFAULT);
    }

    /**
     * @param retryPolicy which failures of this request are retried, null means {@link RetryPolicy#DEFAULT}
     */
    public PendingRequest(String url, String postBody, Map<String, String> headers, int maxRetries, int multiplier, Executor executor, RetryPolicy retryPolicy) {
        this.url = url;
        this.headers = headers;
        this.postBody = postBody;
        this.maxRetries = maxRetries;
        this.multiplier = multiplier;
        this.executor = executor;
        this.retryPolicy = retryPolicy != null ? retryPolicy : RetryPolicy.DEFAULT;
        this.retryCount = 1;
        this.nextAttemptAtMs = SystemClock.elapsedRealtime() + backoffForAttemptMs(retryCount);
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

    public RetryPolicy getRetryPolicy() {
        return retryPolicy;
    }

    public synchronized boolean shouldRetry() {
        return SystemClock.elapsedRealtime() >= nextAttemptAtMs;
    }

    public synchronized boolean isLimitReached() {
        return retryCount > maxRetries;
    }

    public synchronized int getAttemptCount() {
        return retryCount - 1;
    }

    public synchronized long getNextAttemptAtMs() {
        return nextAttemptAtMs;
    }

    /**
     * @deprecated No-op kept for binary compatibility; retry eligibility is time-based now.
     * Safe to remove in the next major version.
     */
    @Deprecated
    public void countAttempt() {
        // No-op: retry eligibility is time-based now, see shouldRetry() and tryClaimAttempt().
    }

    public synchronized void countRetry() {
        nextAttemptAtMs = SystemClock.elapsedRealtime() + backoffForAttemptMs(retryCount);
        retryCount++;
    }

    /**
     * Claims an attempt and arms a deadline if budget and timing allow. False means not due or spent;
     * {@link #isLimitReached()} tells them apart. Spent is defensive, unreachable from the queue.
     */
    public synchronized boolean tryClaimAttempt() {
        long nowMs = SystemClock.elapsedRealtime();
        if (retryCount > maxRetries || nowMs < nextAttemptAtMs) {
            return false;
        }
        nextAttemptAtMs = nowMs + backoffForAttemptMs(retryCount);
        retryCount++;
        return true;
    }

    /**
     * Re-arms from now after a claimed attempt, so a slow failure keeps its backoff window.
     * Backs off on the attempt that just ran, so it must not be called before a claim.
     */
    public synchronized void scheduleNextAttempt() {
        nextAttemptAtMs = SystemClock.elapsedRealtime() + backoffForAttemptMs(retryCount - 1);
    }

    /**
     * Re-spaces a request that was never sent, without spending an attempt. Backs off on the rung
     * the pending attempt would use, so the delay does not grow across repeats; callers must bound it.
     */
    public synchronized void deferWithoutClaiming() {
        nextAttemptAtMs = SystemClock.elapsedRealtime() + backoffForAttemptMs(retryCount);
    }

    synchronized void deferAtLeast(long delayMs) {
        long boundedDelayMs = Math.min(delayMs, MAX_RETRY_DELAY_MS);
        nextAttemptAtMs = Math.max(nextAttemptAtMs, SystemClock.elapsedRealtime() + boundedDelayMs);
    }

    private long backoffForAttemptMs(int attemptNumber) {
        long delayMs = RetryBackoff.computeBackoffMs(BASE_RETRY_DELAY_MS, multiplier, attemptNumber, MAX_RETRY_DELAY_MS);
        return jitterMs(delayMs);
    }

    /** Spreads by &plusmn;{@link #JITTER_FRACTION}, so an armed delay can exceed {@link #MAX_RETRY_DELAY_MS} by that much. */
    private static long jitterMs(long delayMs) {
        if (delayMs <= 0L) {
            return delayMs;
        }
        Random source = jitterRandom;
        double fraction = source != null ? source.nextDouble() : ThreadLocalRandom.current().nextDouble();
        double factor = 1d + JITTER_FRACTION * (2d * fraction - 1d);
        return Math.max(1L, Math.round(delayMs * factor));
    }
}
