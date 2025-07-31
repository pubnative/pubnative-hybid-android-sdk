// HyBid SDK License
//
// https://github.com/pubnative/pubnative-hybid-android-sdk/blob/main/LICENSE
//
package net.pubnative.lite.sdk.vpaid.helpers;

public class SimpleTimer extends CountDownTimer {

    private final Listener mListener;

    public interface Listener {
        void onFinish();
        void onTick(long millisUntilFinished);
    }

    public SimpleTimer(long millisInFuture, Listener listener) {
        super(millisInFuture, 1000 * 60);
        mListener = listener;
    }

    public SimpleTimer(long millisInFuture, Listener listener, long countDownInterval) {
        super(millisInFuture, countDownInterval);
        mListener = listener;
    }

    @Override
    public void onTick(long millisUntilFinished) {
        mListener.onTick(millisUntilFinished);
    }

    @Override
    public void onFinish() {
        if (mListener != null) {
            mListener.onFinish();
        }
    }

    public void pauseTimer() {
        pause();
    }

    public void resumeTimer() {
        resume();
    }
}
