// HyBid SDK License
//
// https://github.com/pubnative/pubnative-hybid-android-sdk/blob/main/LICENSE
//
package net.pubnative.lite.sdk.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

    public static final String TABLE_NAME = "IMPRESSIION";

    public static final String _ID = "_id";

    public static final String ZONE_ID = "zone_id";
    public static final String TIMESTAMP = "timestamp";
    public static final String AGE_OF_APP = "age_of_app";
    public static final String EVENT_TYPE = "event_type";
    //    public static final String AD_FORMAT = "ad_format";
    public static final String SESSION_DURATION = "session_duration";

    // Database Information
    static final String DB_NAME = "hybid_impression.DB";

    // database version
    static final int DB_VERSION = 1;

    // Creating table query
    private static final String CREATE_TABLE = "create table " + TABLE_NAME + "("
            + _ID + " TEXT PRIMARY KEY , "
            + ZONE_ID + " TEXT , "
            + TIMESTAMP + " LONG , "
            + AGE_OF_APP + " LONG , "
            + EVENT_TYPE + " TEXT , "
            + SESSION_DURATION + " LONG); ";

    public DatabaseHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }
}