package net.pubnative.lite.sdk.vpaid;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;

public class VideoAdView extends FrameLayout {

    private VisibilityListener mVisibilityListener;
    private GestureDetector gestureDetector;

    private int visibilityLastTrackedValue = 0;

    public interface VisibilityListener {
        void onVisibilityChanged(int visibility);
    }

    public VideoAdView(Context context) {
        super(context);
        init();
    }

    private void init() {
        gestureDetector = new GestureDetector(getContext(), new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
                return true;
            }
        });
    }

    public VideoAdView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public VideoAdView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    @Override
    protected void onVisibilityChanged(View changedView, int visibility) {
        super.onVisibilityChanged(changedView, visibility);
        visibilityLastTrackedValue = visibility;
        if (mVisibilityListener != null) {
            mVisibilityListener.onVisibilityChanged(visibility);
        }
    }

    public void setVisibilityListener(VisibilityListener visibilityListener) {
        if (visibilityListener != null) {
            this.mVisibilityListener = visibilityListener;
            this.mVisibilityListener.onVisibilityChanged(visibilityLastTrackedValue);
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (mVisibilityListener != null) {
            mVisibilityListener.onVisibilityChanged(INVISIBLE);
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (gestureDetector.onTouchEvent(event)) {
            event.setAction(MotionEvent.ACTION_CANCEL);
        }
        return super.onTouchEvent(event);
    }
}
