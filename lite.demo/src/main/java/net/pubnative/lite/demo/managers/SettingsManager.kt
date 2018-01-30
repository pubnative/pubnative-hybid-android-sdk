package net.pubnative.lite.demo.managers

import android.app.Application
import android.content.SharedPreferences
import android.preference.PreferenceManager
import net.pubnative.lite.demo.*
import net.pubnative.lite.demo.models.SettingsModel
import net.pubnative.lite.demo.util.SingletonHolder

/**
 * Created by erosgarciaponte on 30.01.18.
 */
class SettingsManager private constructor(application: Application) {
    private val preferences: SharedPreferences

    init {
        preferences = PreferenceManager.getDefaultSharedPreferences(application)
    }

    companion object : SingletonHolder<SettingsManager, Application>(::SettingsManager)

    fun setInitialised(initialised: Boolean) {
        preferences.edit().putBoolean(SETTINGS_KEY_INITIALISED, initialised).apply()
    }

    fun isInitialised() : Boolean {
        return preferences.getBoolean(SETTINGS_KEY_INITIALISED, false)
    }

    fun setAppToken(appToken: String) {
        preferences.edit().putString(SETTINGS_KEY_APP_TOKEN, appToken).apply()
    }

    fun setCoppa(enabled: Boolean) {
        preferences.edit().putBoolean(SETTINGS_KEY_COPPA, enabled).apply()
    }

    fun setTestMode(enabled: Boolean) {
        preferences.edit().putBoolean(SETTINGS_KEY_TEST_MODE, enabled).apply()
    }

    fun setGender(gender: String) {
        preferences.edit().putString(SETTINGS_KEY_GENDER, gender).apply()
    }

    fun setAge(age: String) {
        preferences.edit().putString(SETTINGS_KEY_AGE, age).apply()
    }

    fun setZoneIds(zoneIds: List<String>) {
        preferences.edit().putStringSet(SETTINGS_KEY_ZONE_ID_LIST, zoneIds.toSet()).apply()
    }

    fun setKeywords(keywords: List<String>) {
        preferences.edit().putStringSet(SETTINGS_KEY_KEYWORDS, keywords.toSet()).apply()
    }

    fun setSettings(model: SettingsModel, asynchronously: Boolean) {
        val editor = preferences.edit()
        editor.putString(SETTINGS_KEY_APP_TOKEN, model.appToken)
        editor.putBoolean(SETTINGS_KEY_COPPA, model.coppa)
        editor.putBoolean(SETTINGS_KEY_TEST_MODE, model.testMode)
        editor.putString(SETTINGS_KEY_GENDER, model.gender)
        editor.putString(SETTINGS_KEY_AGE, model.age)
        editor.putStringSet(SETTINGS_KEY_ZONE_ID_LIST, model.zoneIds.toSet())
        editor.putStringSet(SETTINGS_KEY_KEYWORDS, model.keywords.toSet())

        editor.putBoolean(SETTINGS_KEY_INITIALISED, true)

        if (asynchronously) {
            editor.apply()
        } else {
            editor.commit()
        }
    }

    fun getSettings(): SettingsModel {
        val appToken = preferences.getString(SETTINGS_KEY_APP_TOKEN, "")
        val zoneIds = preferences.getStringSet(SETTINGS_KEY_ZONE_ID_LIST, emptySet()).toList()
        val gender = preferences.getString(SETTINGS_KEY_GENDER, "")
        val age = preferences.getString(SETTINGS_KEY_AGE, "")
        val keywords = preferences.getStringSet(SETTINGS_KEY_KEYWORDS, emptySet()).toList()
        val coppa = preferences.getBoolean(SETTINGS_KEY_COPPA, false)
        val testMode = preferences.getBoolean(SETTINGS_KEY_TEST_MODE, false)

        val model = SettingsModel(appToken, zoneIds, gender, age, keywords, coppa, testMode)
        return model
    }
}