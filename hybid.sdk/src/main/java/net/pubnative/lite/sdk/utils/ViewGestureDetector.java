package net.pubnative.lite.sdk.utils;

import android.content.Context;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

public class ViewGestureDetector extends GestureDetector {
    private static final String TAG = ViewGestureDetector.class.getSimpleName();

    private final View mView;
    private final SimpleOnGestureListener mOnGestureListener;

    public interface UserClickListener {
        void onUserClick();

        void onResetUserClick();

        boolean wasClicked();
    }

    private UserClickListener mUserClickListener;

    public ViewGestureDetector(Context context, View view, SimpleOnGestureListener onGestureListener) {
        super(context, onGestureListener);

        mOnGestureListener = onGestureListener;
        mView = view;

        setIsLongpressEnabled(false);
    }

    public void sendTouchEvent(MotionEvent motionEvent) {
        switch (motionEvent.getAction()) {
            case MotionEvent.ACTION_UP:
                if (mUserClickListener != null) {
                    mUserClickListener.onUserClick();
                } else {
                    Logger.d(TAG, "View's onUserClick() is not registered.");
                }

                break;

            case MotionEvent.ACTION_DOWN:
                onTouchEvent(motionEvent);
                break;

            case MotionEvent.ACTION_MOVE:
                if (isMotionEventInView(motionEvent, mView)) {
                    onTouchEvent(motionEvent);
                }
                break;

            default:
                break;
        }
    }

    public void setUserClickListener(UserClickListener listener) {
        mUserClickListener = listener;
    }


    private boolean isMotionEventInView(MotionEvent motionEvent, View view) {
        if (motionEvent == null || view == null) {
            return false;
        }

        float x = motionEvent.getX();
        float y = motionEvent.getY();

        return (x >= 0 && x <= view.getWidth())
                && (y >= 0 && y <= view.getHeight());
    }
}
