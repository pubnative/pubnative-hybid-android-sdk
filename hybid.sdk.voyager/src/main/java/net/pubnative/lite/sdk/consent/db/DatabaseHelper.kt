package net.pubnative.lite.sdk.consent.db

import android.content.Context
import android.database.sqlite.SQLiteDatabase

class DatabaseHelper(context: Context) : SQLiteDatabaseHelper(
        context = context,
        name = "hybid_ad_analytics.db",
        factory = null,
        version = 1) {

    override fun onCreate(db: SQLiteDatabase?) {
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
    }
}