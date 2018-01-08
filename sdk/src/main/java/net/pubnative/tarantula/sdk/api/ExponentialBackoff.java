package net.pubnative.tarantula.sdk.api;

import android.support.annotation.NonNull;

import net.pubnative.tarantula.sdk.utils.Logger;

import java.util.Locale;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.functions.BiFunction;
import io.reactivex.functions.Function;

/**
 * Created by erosgarciaponte on 08.01.18.
 */

public class ExponentialBackoff implements Function<Observable<? extends Throwable>, Observable<Long>> {
    @NonNull
    private static final String TAG = ExponentialBackoff.class.getSimpleName();
    @NonNull
    private final Jitter mJitter;
    private final long mDelay;
    private final long mMaxDelay;
    @NonNull
    private final TimeUnit mTimeUnit;
    private final int mRetries;

    /**
     * Exponential backoff that respects the equation: min(mDelay * mRetries ^ 2 * mJitter, mMaxDelay * mJitter)
     *
     * @param maxDelay The max delay to wait before trying again
     * @param retries  The max number of mRetries or -1 to for 10 times. NOTE: passing in Integer.MAX_VALUE will hang the UI
     */
    public ExponentialBackoff(@NonNull Jitter jitter, long delay, long maxDelay, @NonNull TimeUnit timeUnit, int retries) {
        mJitter = jitter;
        mDelay = delay;
        mMaxDelay = maxDelay;
        mTimeUnit = timeUnit;
        // XXX: passing in Integer.MAX_VALUE will hang the UI since RxJava iterates over this number
        mRetries = retries > 0 ? retries : 10;
    }

    @Override
    public Observable<Long> apply(@NonNull final Observable<? extends Throwable> observable) throws Exception {
        return observable
                .zipWith(Observable.range(1, mRetries), new BiFunction<Throwable, Integer, Integer>() {
                    @Override
                    public Integer apply(Throwable throwable, @NonNull Integer retryCount) throws Exception {
                        Logger.w(TAG, "Request failed, retry count: " + retryCount, throwable);
                        return retryCount;
                    }
                }).flatMap(new Function<Integer, ObservableSource<Long>>() {
                    @Override
                    public ObservableSource<Long> apply(Integer attemptNumber) throws Exception {
                        long newInterval = getNewInterval(attemptNumber);
                        Logger.d(TAG, "Retrying request in " + newInterval + " " + mTimeUnit.toString().toLowerCase(Locale.ROOT));
                        return Observable.timer(newInterval, mTimeUnit);
                    }
                });
    }

    private long getNewInterval(int retryCount) {
        long newInterval = Math.min((long) (mDelay * Math.pow(retryCount, 2) * mJitter.get()),
                (long) (mMaxDelay * mJitter.get()));
        if (newInterval < 0) {
            newInterval = Long.MAX_VALUE;
        }
        return newInterval;
    }
}
