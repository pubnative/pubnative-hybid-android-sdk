package net.pubnative.lite.sdk.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;

import net.pubnative.lite.sdk.models.AdRequest;
import net.pubnative.lite.sdk.prefs.HyBidPreferences;
import net.pubnative.lite.sdk.utils.HyBidTimeUtils;
import net.pubnative.lite.sdk.utils.Logger;

import java.util.ArrayList;

public class DBManager {
    private static final String TAG = DBManager.class.getSimpleName();

    private DatabaseHelper dbHelper;

    private final Context context;

    private SQLiteDatabase database;

    public DBManager(Context c) {
        context = c;
        dbHelper = new DatabaseHelper(context);
    }

    public synchronized DBManager open() throws SQLException {
        database = dbHelper.getWritableDatabase();
        return this;
    }

    public synchronized void close() {
        if (dbHelper != null)
            dbHelper.close();
    }

    public long insert(SessionImpression sessionImpression) {
        try {
            open();
            ContentValues contentValue = new ContentValues();
            contentValue.put(DatabaseHelper.TIMESTAMP, sessionImpression.getTimestamp());
            contentValue.put(DatabaseHelper.AGE_OF_APP, sessionImpression.getAgeOfApp());
            contentValue.put(DatabaseHelper.ZONE_ID, sessionImpression.getZoneId());
            contentValue.put(DatabaseHelper.EVENT_TYPE, sessionImpression.getEventType());
            contentValue.put(DatabaseHelper.SESSION_DURATION, sessionImpression.getSessionDuration());
            contentValue.put(DatabaseHelper.COUNT, sessionImpression.getCount());
            long i = database.insert(DatabaseHelper.TABLE_NAME, null, contentValue);
            close();
            return i;
        } catch (SQLException exception) {
            Logger.e(TAG, "Error during DB operation: ", exception);
            return 0;
        }
    }

    private ArrayList<SessionImpression> fetch(String zoneId) {
        String[] columns = new String[]{DatabaseHelper.TIMESTAMP, DatabaseHelper.AGE_OF_APP, DatabaseHelper.ZONE_ID, DatabaseHelper.SESSION_DURATION, DatabaseHelper.COUNT};

        String selection = DatabaseHelper.ZONE_ID + " = " + zoneId;
        ArrayList<SessionImpression> sessionImpressionList = new ArrayList<>();

        try {
            open();
            Cursor cursor = database.query(DatabaseHelper.TABLE_NAME, columns, selection, null, null, null, null);
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    SessionImpression sessionImpression = new SessionImpression();
                    sessionImpression.setTimestamp(cursor.getLong(0));
                    sessionImpression.setAgeOfApp(cursor.getLong(1));
                    sessionImpression.setZoneId(cursor.getInt(2));
                    sessionImpression.setSessionDuration(cursor.getLong(3));
                    sessionImpression.setCount(cursor.getInt(4));
                    sessionImpressionList.add(sessionImpression);
                } while (cursor.moveToNext());

                cursor.close();
            }

            close();
        } catch (SQLException exception) {
            Logger.e(TAG, "Error during DB operation: ", exception);
        }

        return sessionImpressionList;
    }

    public long increment(AdRequest adRequest) {

        if (adRequest != null && !TextUtils.isEmpty(adRequest.zoneid)) {
            ArrayList<SessionImpression> sessionImpressionList = fetch(adRequest.zoneid);

            long i = -1L;

            SessionImpression sessionImpression = new SessionImpression();

            sessionImpression.setTimestamp(System.currentTimeMillis());
            sessionImpression.setZoneId(Integer.valueOf(adRequest.zoneid));

            Long age_of_app = getAgeOfApp();

            Long sessionDuration = new HyBidTimeUtils().calculateSessionDuration(sessionImpression.getTimestamp(), age_of_app);

            manageImpressionSession(sessionImpression.getTimestamp());

            if (sessionImpressionList.size() > 0) {
                try {
                    open();
                    ContentValues contentValues = new ContentValues();
                    sessionImpression.setCount(sessionImpressionList.get(0).getCount());
                    contentValues.put(DatabaseHelper.COUNT, sessionImpression.getCount() + 1);
                    contentValues.put(DatabaseHelper.TIMESTAMP, sessionImpression.getTimestamp());
                    contentValues.put(DatabaseHelper.SESSION_DURATION, sessionDuration);
                    contentValues.put(DatabaseHelper.AGE_OF_APP, age_of_app);
                    i = database.update(DatabaseHelper.TABLE_NAME, contentValues, DatabaseHelper.ZONE_ID + " = " + adRequest.zoneid, null);
                    close();
                } catch (SQLException exception) {
                    Logger.e(TAG, "Error during DB operation: ", exception);
                }
            } else {
                sessionImpression.setCount(1);
                sessionImpression.setEventType("session_report_info");
                sessionImpression.setSessionDuration(sessionDuration);
                sessionImpression.setAgeOfApp(age_of_app);
                i = insert(sessionImpression);
            }
            return i;
        }
        return 0;
    }

    public Integer getSessionImpressionSizeForZoneId(String zoneId) {
        return fetch(zoneId).size();
    }

    private void manageImpressionSession(Long timestamp) {
        new HyBidPreferences(context).setSessionTimeStamp(timestamp, () -> new DBManager(context).nukeTable(), HyBidPreferences.TIMESTAMP.AD_REQUEST);
    }

//    public void delete(long _id) {
//        database.delete(DatabaseHelper.TABLE_NAME, DatabaseHelper.ZONE_ID + "=" + _id, null);
//    }

    public void nukeTable() {
        if (database != null)
            database.delete(DatabaseHelper.TABLE_NAME, null, null);
    }

    public Long getAgeOfApp() {
        return new HyBidPreferences(context).getSessionTimeStamp();
    }
}