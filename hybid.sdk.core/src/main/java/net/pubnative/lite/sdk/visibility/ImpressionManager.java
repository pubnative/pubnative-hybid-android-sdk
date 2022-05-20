// The MIT License (MIT)
//
// Copyright (c) 2018 PubNative GmbH
//
// Permission is hereby granted, free of charge, to any person obtaining a copy
// of this software and associated documentation files (the "Software"), to deal
// in the Software without restriction, including without limitation the rights
// to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
// copies of the Software, and to permit persons to whom the Software is
// furnished to do so, subject to the following conditions:
//
// The above copyright notice and this permission notice shall be included in all
// copies or substantial portions of the Software.
//
// THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
// IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
// FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
// AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
// LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
// OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
// SOFTWARE.
//
package net.pubnative.lite.sdk.visibility;

import android.util.Log;
import android.view.View;

import net.pubnative.lite.sdk.models.AdSize;

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
    public static void startTrackingView(View view, ImpressionTracker.Listener listener) {
        startTrackingView(view, null, listener);
    }

    /**
     * Starts tracking a view removing any previous reference of this one, so there is not
     * duplicated check, (that could happen when reusing views)
     *
     * @param view      view that we want to start tracking
     * @param adSize    size of the view to be tracked
     * @param listener  valid listener for impressions
     */
    public static void startTrackingView(View view, AdSize adSize, ImpressionTracker.Listener listener) {
        getInstance().addView(view, adSize, listener);
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
    protected void addView(View view, AdSize adSize, ImpressionTracker.Listener listener) {
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
                tracker = new ImpressionTracker();
                if (adSize != null) {
                    tracker.setAdSize(adSize);
                }
                tracker.setListener(listener);
                mTrackers.add(tracker);
            }
            tracker.addView(view);
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
