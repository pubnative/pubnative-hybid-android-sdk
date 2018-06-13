package net.pubnative.lite.sdk.visibility;

import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;
import android.view.View;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ImpressionTracker {

    private static final String TAG                         = ImpressionTracker.class.getSimpleName();
    private static final int    VISIBILITY_CHECK_MILLIS     = 250;
    private static final int    VISIBILITY_TIME_MILLIS      = 1000;
    private static final double DEFAULT_MIN_VISIBLE_PERCENT = 0.5;

    protected WeakReference<Listener> mImpressionListener = null;
    protected List<View> mTrackingViews      = new ArrayList<View>();
    protected HashMap<View, Long> mVisibleViews       = new HashMap<View, Long>();
    protected Handler mHandler            = new Handler(Looper.getMainLooper());
    protected Runnable mImpressionRunnable = new ImpressionRunnable();
    protected VisibilityTracker mVisibilityTracker  = null;
    protected VisibilityTracker.Listener mVisibilityListener = new VisibilityTracker.Listener() {
        @Override
        public void onVisibilityCheck(List<View> visibleViews, List<View> invisibleViews) {

            if (mImpressionListener == null && mImpressionListener.get() == null) {
                clear();
            } else {

                for (View visibleView : visibleViews) {

                    if (mVisibleViews.containsKey(visibleView)) {
                        // View already tracked, leave it there
                        continue;
                    }

                    mVisibleViews.put(visibleView, SystemClock.uptimeMillis());
                }

                for (View invisibleView : invisibleViews) {
                    mVisibleViews.remove(invisibleView);
                }

                if (!mVisibleViews.isEmpty()) {
                    scheduleNextRun();
                }
            }
        }
    };

    //==============================================================================================
    // LISTENER
    //==============================================================================================
    public interface Listener {
        void onImpression(View visibleView);
    }

    //==============================================================================================
    // Object
    //==============================================================================================
    @Override
    public int hashCode() {
        return super.hashCode();
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof View) {
            return mTrackingViews.contains(o);
        } else if (o instanceof Listener) {
            return mImpressionListener.equals(o);
        } else {
            return super.equals(o);
        }
    }

    //==============================================================================================
    // PUBLIC
    //==============================================================================================

    /**
     * Sets listener for callbacks related to the impression of added views
     * @param listener valid listener for callbacks
     */
    public void setListener(Listener listener) {
        mImpressionListener = new WeakReference<Listener>(listener);
    }

    /**
     * Adds a view to the list of views to be tracked
     *
     * @param view view that you want ot start tracking, if the view is already being tracked this
     *             will do nothing and will continue tracking the view as it was.
     */
    public void addView(View view) {
        if (mTrackingViews.contains(view)) {
            return;
        }
        mTrackingViews.add(view);
        getVisibilityTracker().addView(view, DEFAULT_MIN_VISIBLE_PERCENT);
    }

    /**
     * Stops tracking the view
     *
     * @param view view that you want to stop tracking
     */
    public void removeView(View view) {
        mTrackingViews.remove(view);
        mVisibleViews.remove(view);
        getVisibilityTracker().removeView(view);
    }

    /**
     * Tells if the current tracker is tracking something
     *
     * @return true if it's tracking something, false if not
     */
    public boolean isEmpty() {
        return mTrackingViews.isEmpty();
    }

    /**
     * Clears all tracking views from this tracker
     */
    public void clear() {
        for (View view : mTrackingViews) {
            ImpressionManager.stopTrackingView(view);
        }

        mHandler.removeMessages(0);
        mTrackingViews.clear();
        mVisibleViews.clear();
        if (mVisibilityTracker != null) {
            mVisibilityTracker.clear();
            mVisibilityTracker = null;
        }
    }

    //==============================================================================================
    // PRIVATE
    //==============================================================================================

    protected VisibilityTracker getVisibilityTracker() {
        if (mVisibilityTracker == null) {
            mVisibilityTracker = new VisibilityTracker();
            mVisibilityTracker.setListener(mVisibilityListener);
        }
        return mVisibilityTracker;
    }

    protected void scheduleNextRun() {
        if (mHandler.hasMessages(0)) {
            return;
        }
        mHandler.postDelayed(mImpressionRunnable, VISIBILITY_CHECK_MILLIS);
    }

    //==============================================================================================
    // INNER CLASSES
    //==============================================================================================

    protected class ImpressionRunnable implements Runnable {

        private List<View> mRemovedViews;
        ImpressionRunnable() {
            mRemovedViews = new ArrayList<View>();
        }
        @Override
        public void run() {
            for (Map.Entry<View, Long> entry : mVisibleViews.entrySet()) {

                View visibleView = entry.getKey();
                Long addedTimestamp = entry.getValue();

                if (!(SystemClock.uptimeMillis() - addedTimestamp >= VISIBILITY_TIME_MILLIS)) {
                    continue;
                }

                if (mImpressionListener != null && mImpressionListener.get() != null) {
                    mImpressionListener.get().onImpression(visibleView);
                }
                mRemovedViews.add(visibleView);
            }
            for (View view : mRemovedViews) {
                ImpressionManager.stopTrackingView(view);
            }
            mRemovedViews.clear();
            if (!mVisibleViews.isEmpty()) {
                scheduleNextRun();
            }
        }
    }
}
