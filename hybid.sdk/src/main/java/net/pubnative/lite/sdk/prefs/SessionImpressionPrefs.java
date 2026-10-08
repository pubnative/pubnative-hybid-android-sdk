// HyBid SDK License
//
// https://github.com/pubnative/pubnative-hybid-android-sdk/blob/main/LICENSE
//
package net.pubnative.lite.sdk.prefs;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import net.pubnative.lite.sdk.db.SessionImpression;
import net.pubnative.lite.sdk.utils.HyBidTimeUtils;
import net.pubnative.lite.sdk.utils.Logger;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class SessionImpressionPrefs {

    private SharedPreferences sharedPreferences;
    private Context mContext;
    private SharedPreferences.Editor editor;

    private static final String SESSION_IMPRESSION_LIST_KEY = "session_impression_key";
    private static final String JSON_EXCEPTION = "JSONException";

    // Guards the impression-list read-modify-write across instances
    private static final Object SHARED_LOCK = new Object();

    public SessionImpressionPrefs(Context context) {
        if (context != null) {
            mContext = context;
            sharedPreferences = context.getSharedPreferences("session_prefs_reporting", MODE_PRIVATE);
            if (sharedPreferences != null) editor = sharedPreferences.edit();
        }
    }

    public void insert(String zoneId) {

        SessionImpression sessionImpression = new SessionImpression();

        sessionImpression.setTimestamp(System.currentTimeMillis());
        sessionImpression.setZoneId(zoneId);

        Long ageOfApp = getAgeOfApp();

        Long sessionDuration = new HyBidTimeUtils().calculateSessionDuration(sessionImpression.getTimestamp(), ageOfApp);

        sessionImpression.setSessionDuration(sessionDuration);

        sessionImpression.setAgeOfApp(ageOfApp);

        // One transaction across instances
        synchronized (SHARED_LOCK) {
            manageImpressionSession(sessionImpression.getTimestamp());
            increment(sessionImpression);
        }
    }

    private void increment(SessionImpression sessionImpression) {
        if (editor != null) {
            String jsonList = sharedPreferences.getString(SESSION_IMPRESSION_LIST_KEY, "");
            List<SessionImpression> sessionImpressionList = new ArrayList<>();
            if (!jsonList.isEmpty()) {
                try {
                    JSONArray jsonArray = new JSONArray(jsonList);
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        SessionImpression obj = new SessionImpression(jsonObject);
                        sessionImpressionList.add(obj);
                    }
                    incrementZoneId(sessionImpressionList, sessionImpression);
                } catch (JSONException e) {
                    if (e != null) Logger.e(JSON_EXCEPTION, e.toString());
                }
            } else {
                sessionImpression.setCount(1);
                sessionImpressionList.add(sessionImpression);
            }

            editor.putString(SESSION_IMPRESSION_LIST_KEY, convertListToJson(sessionImpressionList).toString());
            editor.apply();
        }
    }

    private JSONArray convertListToJson(List<SessionImpression> sessionImpressionList) {
        JSONArray jsonArray = new JSONArray();
        for (SessionImpression obj : sessionImpressionList) {
            try {
                jsonArray.put(obj.toJson());
            } catch (Exception e) {
                if (e != null) Logger.e(JSON_EXCEPTION, e.toString());
            }
        }
        return jsonArray;
    }

    private void incrementZoneId(List<SessionImpression> sessionImpressionList, SessionImpression sessionImpression) {
        boolean isFound = false;
        if (sessionImpressionList != null && sessionImpression != null) {
            for (int i = 0; i < sessionImpressionList.size(); i++) {
                SessionImpression fromList = sessionImpressionList.get(i);
                if (fromList != null) {
                    if (!TextUtils.isEmpty(sessionImpression.getZoneId())
                            && !TextUtils.isEmpty(fromList.getZoneId())
                            && sessionImpression.getZoneId().equals(fromList.getZoneId())) {
                        isFound = true;
                        fromList.setCount(fromList.getCount() + 1);
                        break;
                    }
                }
            }


            if (!isFound) {
                sessionImpression.setCount(1);
                sessionImpressionList.add(sessionImpression);
            }
        }
    }

    private Long getAgeOfApp() {
        if (mContext == null) return 0L;
        return new HyBidPreferences(mContext).getSessionTimeStamp();
    }

    private void manageImpressionSession(Long timestamp) {
        if (mContext == null) return;
        new HyBidPreferences(mContext).setSessionTimeStamp(timestamp, this::nukePrefs, HyBidPreferences.TIMESTAMP.AD_REQUEST);
    }

    public void nukePrefs() {
        synchronized (SHARED_LOCK) {
            if (editor != null) {
                editor.putString(SESSION_IMPRESSION_LIST_KEY, "");
                editor.apply();
            }
        }
    }

    // Entry point for callers outside the insert/read paths (e.g. HyBid.initialize) to run the session-boundary check under SHARED_LOCK,
    // so their rollover+reset can't race a concurrent insert()/getImpressionDepth().
    public void applySessionBoundary() {
        synchronized (SHARED_LOCK) {
            manageImpressionSession(System.currentTimeMillis());
        }
    }

    public Integer getImpressionDepth(String zoneId) {
        synchronized (SHARED_LOCK) {
            manageImpressionSession(System.currentTimeMillis());

            String jsonList = sharedPreferences.getString(SESSION_IMPRESSION_LIST_KEY, "");
            List<SessionImpression> sessionImpressionList = new ArrayList<>();
            if (!jsonList.isEmpty()) {
                try {
                    JSONArray jsonArray = new JSONArray(jsonList);
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        SessionImpression obj = new SessionImpression(jsonObject);
                        sessionImpressionList.add(obj);
                    }
                } catch (JSONException e) {
                    if (e != null) Logger.e(JSON_EXCEPTION, e.toString());
                }
                SessionImpression assignedSessionImpression = null;
                for (SessionImpression sessionImpression : sessionImpressionList) {
                    if (!TextUtils.isEmpty(sessionImpression.getZoneId())
                            && !TextUtils.isEmpty(zoneId)
                            && sessionImpression.getZoneId().equals(zoneId)) {
                        assignedSessionImpression = sessionImpression;
                        break;
                    }
                }

                if (assignedSessionImpression != null) return assignedSessionImpression.getCount();
                else return 0;
            } else {
                return 0;
            }
        }
    }
}