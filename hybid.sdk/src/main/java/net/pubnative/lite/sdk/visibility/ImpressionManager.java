// HyBid SDK License
//
// https://github.com/pubnative/pubnative-hybid-android-sdk/blob/main/LICENSE
//
package net.pubnative.lite.sdk.visibility;

import android.util.Log;
import android.view.View;

import net.pubnative.lite.sdk.models.AdSize;
import net.pubnative.lite.sdk.utils.HybidConsumer;

import java.util.ArrayList;
import java.util.List;

public class ImpressionManager {

    private static final String TAG = ImpressionManager.class.getSimpleName();

    protected List<ImpressionTracker> mTrackers;

    //==============================================================================================
    // SINGLETON
    //==============================================================================================
    private static ImpressionManager instance;

    private ImpressionManager() {
    }

    public static ImpressionManager getInstance() {

        if (instance == null) {
            instance = new ImpressionManager();
            instance.mTrackers = new ArrayList<>();
        }
        return instance;
    }

    //==============================================================================================
    // PUBLIC
    //==============================================================================================

    /**
     * Starts tracking a view removing any previous reference of this one, so there is not
     * duplicated check, (that could happen when reusing views)
     *
     * @param view     view that we want to start tracking
     * @param listener valid listener for impressions
     */
    public static void startTrackingView(View view, Integer visibleTimeMillisecond, Double visiblePercent, ImpressionTracker.Listener listener) {
        startTrackingView(view, null, visibleTimeMillisecond, visiblePercent, listener, null);
    }

    /**
     * Starts tracking a view removing any previous reference of this one, so there is not
     * duplicated check, (that could happen when reusing views)
     *
     * @param view      view that we want to start tracking
     * @param adSize    size of the view to be tracked
     * @param listener  valid listener for impressions
     */
    public static void startTrackingView(View view, AdSize adSize, Integer visibleTimeMillisecond, Double visiblePercent
            , ImpressionTracker.Listener listener, HybidConsumer<Double> hybidConsumer) {
        getInstance().addView(view, adSize, visibleTimeMillisecond, visiblePercent, listener, hybidConsumer);
    }

    /**
     * Stops tracking all views related to the passed listener
     *
     * @param listener valid listener
     */
    public static void stopTrackingAll(ImpressionTracker.Listener listener) {
        getInstance().stopTracking(listener);
    }

    /**
     * Stops tracking the view
     *
     * @param view view that we want to stop tracking
     */
    public static void stopTrackingView(View view) {
        getInstance().removeView(view);
    }

    //==============================================================================================
    // PRIVATE
    //==============================================================================================
    protected void addView(View view, AdSize adSize, Integer visibleTimeMillisecond, Double visiblePercent
            , ImpressionTracker.Listener listener, HybidConsumer<Double> percentageConsumer) {
        // Adds view to tracker, removing any previous instance of the view on other trackers
        // This should also create an independent tracker for each listener
        if (view == null) {
            Log.w(TAG, "trying to start tracking null view, dropping this calll");
        } else if (listener == null) {
            Log.w(TAG, "trying to start tracking with null listener");
        } else {

            // Remove view from previous instances
            // view.equals(item)
            // item.equals(view)
            if (containsTracker(view)) {
                int trackerIndex = indexOfTracker(view);
                ImpressionTracker tracker = mTrackers.get(trackerIndex);
                if (!tracker.equals(listener)) {
                    removeView(view); // First, remove the view from the previous tracker
                }
            }

            // Add the view to a new or currently working tracker
            ImpressionTracker tracker;
            if (containsTracker(listener)) {
                int trackerIndex = indexOfTracker(view);
                tracker = mTrackers.get(trackerIndex);
            } else {
                tracker = new ImpressionTracker(visibleTimeMillisecond, visiblePercent);
                if (adSize != null) {
                    tracker.setAdSize(adSize, visiblePercent);
                }
                tracker.setListener(listener);
                mTrackers.add(tracker);
            }
            tracker.addView(view, percentageConsumer);
        }
    }

    protected void stopTracking(ImpressionTracker.Listener listener) {
        if (listener == null) {
            Log.w(TAG, "trying to remove all views from null listener, dropping this call");
        } else if (containsTracker(listener)) {
            int trackerIndex = indexOfTracker(listener);
            ImpressionTracker tracker = mTrackers.get(trackerIndex);
            tracker.clear();
            mTrackers.remove(listener);
        }
    }

    protected void removeView(View view) {
        // Removes the view from any possible tracker, checking if this tracker is empty after to
        // be removed
        if (view == null) {
            Log.w(TAG, "trying to remove null view, dropping this call");
        } else if (containsTracker(view)) {
            int trackerIndex = indexOfTracker(view);
            ImpressionTracker tracker = mTrackers.get(trackerIndex);
            tracker.removeView(view);
            if (tracker.isEmpty()) {
                tracker.clear();
                mTrackers.remove(tracker);
            }
        }
    }

    //==============================================================================================
    // Trackers inspection
    //==============================================================================================

    // View search
    protected boolean containsTracker(View view) {
        return indexOfTracker(view) >= 0;
    }

    protected int indexOfTracker(View view) {
        int result = -1;
        for (int i = 0; i < mTrackers.size(); i++) {
            ImpressionTracker tracker = mTrackers.get(i);
            if (tracker.equals(view)) {
                result = i;
                break;
            }
        }
        return result;
    }

    // Listener search
    protected boolean containsTracker(ImpressionTracker.Listener listener) {
        return indexOfTracker(listener) >= 0;
    }

    protected int indexOfTracker(ImpressionTracker.Listener listener) {
        int result = -1;
        for (int i = 0; i < mTrackers.size(); i++) {

            ImpressionTracker tracker = mTrackers.get(i);
            if (tracker.equals(listener)) {
                result = i;
                break;
            }
        }
        return result;
    }
}
