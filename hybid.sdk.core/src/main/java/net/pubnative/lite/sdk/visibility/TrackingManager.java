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

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;
import android.util.Log;

import net.pubnative.lite.sdk.HyBid;
import net.pubnative.lite.sdk.models.TrackingUrlModel;
import net.pubnative.lite.sdk.network.PNHttpClient;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TrackingManager {
    private static final String TAG = TrackingManager.class.getSimpleName();
    private static final String SHARED_PREFERENCES = "TrackingManager";
    protected static final String SHARED_PENDING_LIST = "pending";
    protected static final String SHARED_FAILED_LIST = "failed";
    private static final long ITEM_VALIDITY_TIME = 1800000; // 30 minutes
    private static boolean sIsTracking = false;

    //==============================================================================================
    // PUBLIC
    //==============================================================================================

    /**
     * This method is used to send impression request
     *
     * @param context valid context
     * @param url     Url to track
     */
    public static synchronized void track(Context context, String url) {
        if (context == null) {
            Log.w(TAG, "track - ERROR: Context parameter is null");
        } else if (TextUtils.isEmpty(url)) {
            Log.w(TAG, "track - ERROR: url parameter is null");
        } else {
            // 1. Enqueue failed items
            enqueueFailedList(context);
            // 2. Enqueue current item
            TrackingUrlModel model = new TrackingUrlModel();
            model.url = url;
            model.startTimestamp = System.currentTimeMillis();
            enqueueItem(context, SHARED_PENDING_LIST, model);
            // 3. Try doing next item
            trackNextItem(context);
        }
    }

    //==============================================================================================
    // PRIVATE
    //==============================================================================================

    protected static synchronized void trackNextItem(final Context context) {
        if (sIsTracking) {
            Log.w(TAG, "trackNextItem - Currently tracking, dropping the call, will be resumed soon");
        } else {
            sIsTracking = true;
            // Extract pending item
            final TrackingUrlModel model = dequeueItem(context, SHARED_PENDING_LIST);
            if (model == null) {
                sIsTracking = false;
            } else {
                if (model.startTimestamp + ITEM_VALIDITY_TIME < System.currentTimeMillis()) {
                    sIsTracking = false;
                    trackNextItem(context);
                } else {
                    Map<String, String> headers = new HashMap<>();
                    String userAgent = HyBid.getDeviceInfo().getUserAgent();
                    if (!TextUtils.isEmpty(userAgent)){
                        headers.put("User-Agent", userAgent);
                    }

                    PNHttpClient.makeRequest(context, model.url, headers, null, new PNHttpClient.Listener() {
                        @Override
                        public void onSuccess(String response) {
                            sIsTracking = false;
                            trackNextItem(context);
                        }

                        @Override
                        public void onFailure(Throwable error) {
                            // Since this failed, we re-enqueue it
                            enqueueItem(context, SHARED_FAILED_LIST, model);
                            sIsTracking = false;
                            trackNextItem(context);
                        }
                    });
                }
            }
        }
    }

    //==============================================================================================
    // QUEUE
    //==============================================================================================

    protected static void enqueueFailedList(Context context) {
        List<TrackingUrlModel> failedList = getList(context, SHARED_FAILED_LIST);
        List<TrackingUrlModel> pendingList = getList(context, SHARED_PENDING_LIST);
        pendingList.addAll(failedList);
        setList(context, SHARED_PENDING_LIST, pendingList);
        failedList.clear();
        setList(context, SHARED_FAILED_LIST, failedList);
    }

    protected static void enqueueItem(Context context, String listKey, TrackingUrlModel model) {
        List<TrackingUrlModel> list = getList(context, listKey);
        list.add(model);
        setList(context, listKey, list);
    }

    protected static TrackingUrlModel dequeueItem(Context context, String listKey) {
        TrackingUrlModel result = null;
        List<TrackingUrlModel> list = getList(context, listKey);
        if (!list.isEmpty()) {
            result = list.get(0);
            list.remove(0);
            setList(context, listKey, list);
        }
        return result;
    }

    //==============================================================================================
    // SHARED PREFERENCES
    //==============================================================================================
    // List helper
    //----------------------------------------------------------------------------------------------

    protected static List<TrackingUrlModel> getList(Context context, String key) {
        List<TrackingUrlModel> result = new ArrayList<>();
        SharedPreferences preferences = getSharedPreferences(context);
        String sharedPendingString = preferences.getString(key, null);
        if (sharedPendingString != null) {

            try {
                JSONArray list = new JSONArray(sharedPendingString);
                for (int i = 0; i < list.length(); i++) {
                    result.add(new TrackingUrlModel(list.getJSONObject(i)));
                }
            } catch (Exception exception) {
                exception.printStackTrace();
            }

        }
        return result;
    }

    protected static void setList(Context context, String key, List<TrackingUrlModel> value) {
        SharedPreferences preferences = getSharedPreferences(context);
        SharedPreferences.Editor preferencesEditor = preferences.edit();
        if (value == null) {
            preferencesEditor.remove(key);
        } else {
            JSONArray result = new JSONArray();

            for (TrackingUrlModel urlModel : value) {
                try {
                    result.put(urlModel.toJson());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            preferencesEditor.putString(key, result.toString());
        }
        preferencesEditor.apply();
    }

    //----------------------------------------------------------------------------------------------
    // Base shared preferences
    //----------------------------------------------------------------------------------------------

    protected static SharedPreferences getSharedPreferences(Context context) {
        return context.getSharedPreferences(SHARED_PREFERENCES, Context.MODE_PRIVATE);
    }
}
