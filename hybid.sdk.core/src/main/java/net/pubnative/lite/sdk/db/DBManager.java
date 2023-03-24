package net.pubnative.lite.sdk.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import net.pubnative.lite.sdk.HyBid;
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
    }

    public DBManager open() throws SQLException {
        dbHelper = new DatabaseHelper(context);
        database = dbHelper.getWritableDatabase();
        return this;
    }

    public void close() {
        if (dbHelper != null)
            dbHelper.close();
    }

    private void insert(SessionImpression sessionImpression) {
        ContentValues contentValue = new ContentValues();
        contentValue.put(DatabaseHelper.TIMESTAMP, sessionImpression.getTimestamp());
        contentValue.put(DatabaseHelper.AGE_OF_APP, sessionImpression.getAgeOfApp());
        contentValue.put(DatabaseHelper.ZONE_ID, sessionImpression.getZoneId());
        contentValue.put(DatabaseHelper.EVENT_TYPE, sessionImpression.getEventType());
        contentValue.put(DatabaseHelper.SESSION_DURATION, sessionImpression.getSessionDuration());
        database.insert(DatabaseHelper.TABLE_NAME, null, contentValue);
    }

    public void insert(String zoneId) {

        SessionImpression sessionImpression = new SessionImpression();

        sessionImpression.setTimestamp(System.currentTimeMillis());
        sessionImpression.setZoneId(zoneId);

        Long age_of_app = getAgeOfApp();

        Long sessionDuration = new HyBidTimeUtils().calculateSessionDuration(sessionImpression.getTimestamp(), age_of_app);

        sessionImpression.setSessionDuration(sessionDuration);

        manageImpressionSession(sessionImpression.getTimestamp());

        insert(sessionImpression);
    }

    private synchronized ArrayList<SessionImpression> fetch(String zoneId) {
        String[] columns = new String[]{DatabaseHelper.TIMESTAMP, DatabaseHelper.AGE_OF_APP, DatabaseHelper.ZONE_ID, DatabaseHelper.SESSION_DURATION};

        String selection = DatabaseHelper.ZONE_ID + " = " + zoneId;
        ArrayList<SessionImpression> sessionImpressionList = new ArrayList<>();

        try {
            Cursor cursor = database.query(DatabaseHelper.TABLE_NAME, columns, selection, null, null, null, null);
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    SessionImpression sessionImpression = new SessionImpression();
                    sessionImpression.setTimestamp(cursor.getLong(0));
                    sessionImpression.setAgeOfApp(cursor.getLong(1));
                    sessionImpression.setZoneId(cursor.getString(2));
                    sessionImpression.setSessionDuration(cursor.getLong(3));
                    sessionImpressionList.add(sessionImpression);
                } while (cursor.moveToNext());
                cursor.close();
            }
        } catch (SQLException exception) {
            HyBid.reportException(exception);
            Logger.e(TAG, "Error during DB operation: ", exception);
        }

        return sessionImpressionList;
    }

    public Integer getImpressionDepth(String zoneId) {
        ArrayList<SessionImpression> impressions = fetch(zoneId);
        return impressions.size();
    }

    private void manageImpressionSession(Long timestamp) {
        new HyBidPreferences(context).setSessionTimeStamp(timestamp, this::nukeTable, HyBidPreferences.TIMESTAMP.AD_REQUEST);
    }

    public void nukeTable() {
        if (database != null)
            database.delete(DatabaseHelper.TABLE_NAME, null, null);
    }

    public Long getAgeOfApp() {
        return new HyBidPreferences(context).getSessionTimeStamp();
    }
}