package net.pubnative.lite.sdk.vpaid.helpers;

public class SimpleTimer extends CountDownTimer {

    private final Listener mListener;

    public interface Listener {
        void onFinish();
    }

    public SimpleTimer(long millisInFuture, Listener listener) {
        super(millisInFuture, 1000 * 60);
        mListener = listener;
    }

    @Override
    public void onTick(long millisUntilFinished) {
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
