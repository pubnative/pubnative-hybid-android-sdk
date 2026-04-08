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
import net.pubnative.lite.sdk.utils.Logger;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

public class VisibilityTracker {

    private static final String TAG = VisibilityTracker.class.getSimpleName();
    private static final int VISIBILITY_CHECK_DELAY = 100;

    protected WeakReference<View> mDeviceView = null;
    private volatile WeakReference<HybidConsumer<Double>> mPercentageConsumer;
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
        synchronized (mTrackedViews) {
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
    }

    /**
     * Removes the view for visibility tracking
     *
     * @param view view that you want to stop tracking
     */
    public void removeView(View view) {
        synchronized (mTrackedViews) {
            if (view == null) return;
            int index = indexOfTrackedView(view);
            if (index >= 0) {
                mTrackedViews.remove(index);
            }
        }
    }

    /**
     * Stops tracking of all views and removes all callbacks
     */
    public void clear() {
        synchronized (mTrackedViews) {
            mHandler.removeMessages(0);
            if (mPercentageConsumer != null) {
                mPercentageConsumer.clear();
            }
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
        private volatile boolean mHasReportedVisibility = false;

        VisibilityRunnable() {
            mVisibleRect = new Rect();
            mInvisibleViews = new ArrayList<>();
            mVisibleViews = new ArrayList<>();
        }

        @Override
        public void run() {
            mIsVisibilityCheckScheduled = false;

            final List<PubnativeVisibilityTrackerItem> snapshot;
            synchronized (mTrackedViews) {
                snapshot = new ArrayList<>(mTrackedViews);
            }

            for (PubnativeVisibilityTrackerItem item : snapshot) {

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
            if (item == null) {
                return result;
            }

            View view = item.mTrackingView;

            try {
                if (view != null
                        && view.isShown() // This is specially useful to ensure visibility in lists
                        && view.getParent() != null
                        && view.getLocalVisibleRect(mVisibleRect)) {
                    float visibleArea = mVisibleRect.height() * mVisibleRect.width();
                    float viewArea = view.getHeight() * view.getWidth();
                    if (viewArea <= 0) return false;

                    double percentVisible = (double) visibleArea / (double) viewArea;

                    result = percentVisible >= item.mMinVisibilityPercent;
                    if (result && !mHasReportedVisibility && mPercentageConsumer != null) {
                        HybidConsumer<Double> consumer = mPercentageConsumer.get();
                        if (consumer != null) {
                            consumer.accept(percentVisible);
                            mHasReportedVisibility = true;
                        }
                    }
                }
            } catch (Exception e) {
                Logger.e(VisibilityTracker.TAG, "Error calculating visibility: " + e.getMessage());
            }
            return result;
        }
    }
}
