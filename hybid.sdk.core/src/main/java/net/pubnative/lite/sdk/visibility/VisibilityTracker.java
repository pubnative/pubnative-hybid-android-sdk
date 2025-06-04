// HyBid SDK License
//
// https://github.com/pubnative/pubnative-hybid-android-sdk/blob/main/LICENSE
//
package net.pubnative.lite.sdk.visibility;

import android.graphics.Rect;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;

import net.pubnative.lite.sdk.utils.HybidConsumer;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

public class VisibilityTracker {

    private static final String TAG = VisibilityTracker.class.getSimpleName();
    private static final int VISIBILITY_CHECK_DELAY = 100;

    protected WeakReference<View> mDeviceView = null;
    private WeakReference<HybidConsumer<Double>> mPercentageConsumer;
    protected WeakReference<Listener> mListener = null;
    protected final List<PubnativeVisibilityTrackerItem> mTrackedViews = new ArrayList<>();
    protected Handler mHandler = new Handler();
    protected boolean mIsVisibilityCheckScheduled = false;
    protected final VisibilityRunnable mVisibilityRunnable = new VisibilityRunnable();
    protected ViewTreeObserver.OnPreDrawListener mOnPreDrawListener = () -> {

        if (mListener == null || mListener.get() == null) {
            clear();
        } else {
            scheduleVisibilityCheck();
        }
        return true;
    };

    //==============================================================================================
    // CALLBACK
    //==============================================================================================
    public interface Listener {

        void onVisibilityCheck(List<View> visibleViews, List<View> invisibleViews);
    }

    //==============================================================================================
    // PUBLIC
    //==============================================================================================

    /**
     * Sets listener for callbacks
     *
     * @param listener valid listener
     */
    public void setListener(Listener listener) {
        mListener = new WeakReference<>(listener);
    }

    /**
     * Adds the view to the tracking view pool and starts tracking it
     *
     * @param view                 view that you want to start tracking
     * @param minVisibilityPercent min amount percent of the view shown to be considered visible
     *                             from 0 to 1
     */
    public void addView(View view, double minVisibilityPercent, HybidConsumer<Double> percentageConsumer) {
        if (mDeviceView == null) {
            mDeviceView = new WeakReference<>(view);
            ViewTreeObserver observer = view.getViewTreeObserver();
            if (observer.isAlive()) {
                observer.addOnPreDrawListener(mOnPreDrawListener);
            } else {
                Log.d(TAG, "Unable to start tracking, Window ViewTreeObserver is not alive");
            }
        }
        mPercentageConsumer = new WeakReference<>(percentageConsumer);
        if (containsTrackedView(view)) {
            // Already tracking this view, drop the call
            return;
        }

        PubnativeVisibilityTrackerItem item = new PubnativeVisibilityTrackerItem();
        item.mTrackingView = view;
        item.mMinVisibilityPercent = minVisibilityPercent;

        mTrackedViews.add(item);

        scheduleVisibilityCheck();
    }

    /**
     * Removes the view for visibility tracking
     *
     * @param view view that you want to stop tracking
     */
    public void removeView(View view) {
        mTrackedViews.remove(view);
    }

    /**
     * Stops tracking of all views and removes all callbacks
     */
    public void clear() {
        mHandler.removeMessages(0);
        mPercentageConsumer.clear();
        mTrackedViews.clear();
        mIsVisibilityCheckScheduled = false;
        if (mDeviceView != null) {
            View decorView = mDeviceView.get();
            if (decorView != null && mOnPreDrawListener != null) {
                ViewTreeObserver observer = decorView.getViewTreeObserver();
                if (observer.isAlive()) {
                    observer.removeOnPreDrawListener(mOnPreDrawListener);
                }
                mOnPreDrawListener = null;
            }
        }
        mListener = null;
    }

    //==============================================================================================
    // PRIVATE
    //==============================================================================================

    protected void scheduleVisibilityCheck() {
        if (!mIsVisibilityCheckScheduled) {
            mIsVisibilityCheckScheduled = true;
            mHandler.postDelayed(mVisibilityRunnable, VISIBILITY_CHECK_DELAY);
        }
    }

    // View search

    protected boolean containsTrackedView(View view) {
        return indexOfTrackedView(view) >= 0;
    }

    protected int indexOfTrackedView(View view) {
        int result = -1;
        for (int i = 0; i < mTrackedViews.size(); i++) {
            PubnativeVisibilityTrackerItem item = mTrackedViews.get(i);
            if (item.equals(view)) {
                result = i;
                break;
            }
        }
        return result;
    }

    //==============================================================================================
    // INNER CLASSES
    //==============================================================================================
    protected static class PubnativeVisibilityTrackerItem {

        private final String TAG = PubnativeVisibilityTrackerItem.class.getSimpleName();
        public View mTrackingView;
        public double mMinVisibilityPercent; // Expressed from 0 to 1

        @Override
        public boolean equals(Object o) {
            if (o instanceof View) {
                return o.equals(mTrackingView);
            }
            return super.equals(o);
        }
    }

    protected class VisibilityRunnable implements Runnable {

        private final ArrayList<View> mVisibleViews;
        private final ArrayList<View> mInvisibleViews;
        private final Rect mVisibleRect;
        private boolean mHasReportedVisibility = false;

        VisibilityRunnable() {
            mVisibleRect = new Rect();
            mInvisibleViews = new ArrayList<>();
            mVisibleViews = new ArrayList<>();
        }

        @Override
        public void run() {

            mIsVisibilityCheckScheduled = false;

            for (PubnativeVisibilityTrackerItem item : mTrackedViews) {

                if (isVisible(item)) {

                    mVisibleViews.add(item.mTrackingView);
                } else {

                    mInvisibleViews.add(item.mTrackingView);
                }
            }

            if (mListener != null && mListener.get() != null) {
                mListener.get().onVisibilityCheck(mVisibleViews, mInvisibleViews);
            }

            mInvisibleViews.clear();
            mVisibleViews.clear();
        }

        protected boolean isVisible(PubnativeVisibilityTrackerItem item) {

            boolean result = false;

            View view = item.mTrackingView;

            if (view != null
                    && view.isShown() // This is specially useful to ensure visibility in lists
                    && view.getParent() != null
                    && view.getLocalVisibleRect(mVisibleRect)) {
                float visibleArea = mVisibleRect.height() * mVisibleRect.width();
                float viewArea = view.getHeight() * view.getWidth();
                double percentVisible = (double) visibleArea / (double) viewArea;

                result = percentVisible >= item.mMinVisibilityPercent;
                if (result && mPercentageConsumer.get() != null && !mHasReportedVisibility) {
                    mPercentageConsumer.get().accept(percentVisible);
                    mHasReportedVisibility = true;
                }
            }
            return result;
        }
    }
}
