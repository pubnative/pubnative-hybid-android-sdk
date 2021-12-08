package net.pubnative.lite.sdk.vpaid.helpers;

import android.annotation.SuppressLint;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.SystemClock;

/**
 * extends {@link android.os.CountDownTimer}
 */
public abstract class TimerWithPause {

    private static final int MSG = 1;

    private long mStopTimeInFuture;
    private long mMillisInFuture;
    private final long mTotalCountdown;
    private final long mCountdownInterval;
    private long mPauseTimeRemaining;
    private final boolean mRunAtStart;

    @SuppressLint("HandlerLeak")
    private final Handler mHandler = new Handler(Looper.myLooper()) {
        @Override
        public void handleMessage(Message msg) {
            handleTimerMessage();
        }
    };

    protected TimerWithPause(long millisOnTimer,
                             long countDownInterval) {
        mMillisInFuture = millisOnTimer;
        mTotalCountdown = mMillisInFuture;
        mCountdownInterval = countDownInterval;
        mRunAtStart = true;
    }

    public abstract void onTick(long millisUntilFinished);

    public abstract void onFinish();

    public final void cancel() {
        mHandler.removeMessages(MSG);
    }

    public synchronized final TimerWithPause create() {
        if (mMillisInFuture <= 0) {
            onFinish();
        } else {
            mPauseTimeRemaining = mMillisInFuture;
        }
        if (mRunAtStart) {
            resume();
        }
        return this;
    }

    public void pause() {
        if (isRunning()) {
            mPauseTimeRemaining = timeLeft();
            cancel();
        }
    }

    public void resume() {
        if (isPaused()) {
            mMillisInFuture = mPauseTimeRemaining;
            mStopTimeInFuture = SystemClock.elapsedRealtime() + mMillisInFuture;
            mHandler.sendMessage(mHandler.obtainMessage(MSG));
            mPauseTimeRemaining = 0;
        }
    }

    public boolean isPaused() {
        return (mPauseTimeRemaining > 0);
    }

    public boolean isRunning() {
        return (!isPaused());
    }

    public long timeLeft() {
        long millisUntilFinished;
        if (isPaused()) {
            millisUntilFinished = mPauseTimeRemaining;
        } else {
            millisUntilFinished = mStopTimeInFuture - SystemClock.elapsedRealtime();
            if (millisUntilFinished < 0) millisUntilFinished = 0;
        }
        return millisUntilFinished;
    }

    public long totalCountdown() {
        return mTotalCountdown;
    }

    public long timePassed() {
        return mTotalCountdown - timeLeft();
    }

    public boolean hasBeenStarted() {
        return (mPauseTimeRemaining <= mMillisInFuture);
    }

    private synchronized void handleTimerMessage() {
        long millisLeft = timeLeft();
        if (millisLeft <= 0) {
            cancel();
            onFinish();
        } else if (millisLeft < mCountdownInterval) {
            // no tick, just delay until done
            mHandler.sendMessageDelayed(mHandler.obtainMessage(MSG), millisLeft);
        } else {
            long lastTickStart = SystemClock.elapsedRealtime();
            onTick(millisLeft);

            // take into account user's onTick taking time to execute
            long delay = mCountdownInterval - (SystemClock.elapsedRealtime() - lastTickStart);

            // special case: user's onTick took more than mCountdownInterval to
            // complete, skip to next interval
            while (delay < 0) delay += mCountdownInterval;

            mHandler.sendMessageDelayed(mHandler.obtainMessage(MSG), delay);
        }
    }

}