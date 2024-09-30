package net.pubnative.lite.demo.managers

import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager
import net.pubnative.lite.demo.SETTINGS_KEY_AD_CUSTOMIZATION_DATA
import net.pubnative.lite.demo.SETTINGS_KEY_AD_CUSTOMIZATION_INITIALISED
import net.pubnative.lite.demo.SETTINGS_KEY_BUNDLE_ID
import net.pubnative.lite.demo.SETTINGS_KEY_CUSTOM_CTA_APP_NAME
import net.pubnative.lite.demo.SETTINGS_KEY_CUSTOM_CTA_ICON_URL
import net.pubnative.lite.demo.SETTINGS_KEY_CUSTOM_END_CARD_HTML

class AdCustomizationPrefs(context: Context) {

    private val preferences: SharedPreferences

    init {
        preferences = PreferenceManager.getDefaultSharedPreferences(context)
    }

    private fun setInitialised(initialised: Boolean) {
        preferences.edit().putBoolean(SETTINGS_KEY_AD_CUSTOMIZATION_INITIALISED, initialised)
            .apply()
    }

    fun isInitialised(): Boolean {
        return preferences.getBoolean(SETTINGS_KEY_AD_CUSTOMIZATION_INITIALISED, false)
    }

    fun setAdCustomizationData(adCustomizationStr: String) {
        preferences.edit().putString(SETTINGS_KEY_AD_CUSTOMIZATION_DATA, adCustomizationStr)
            .apply()
        setInitialised(true)
    }

    fun setCustomEndCardHTML(customEndCardHTML: String) {
        preferences.edit().putString(SETTINGS_KEY_CUSTOM_END_CARD_HTML, customEndCardHTML)
            .apply()
    }

    fun getCustomEndCardHTML(): String? {
        return preferences.getString(SETTINGS_KEY_CUSTOM_END_CARD_HTML, "")
    }

    fun setCustomCTAIconURL(url: String) {
        preferences.edit().putString(SETTINGS_KEY_CUSTOM_CTA_ICON_URL, url)
            .apply()
    }

    fun getCustomCTAIconURL(): String? {
        return preferences.getString(SETTINGS_KEY_CUSTOM_CTA_ICON_URL, "")
    }

    fun setCustomCTAAppName(name: String) {
        preferences.edit().putString(SETTINGS_KEY_CUSTOM_CTA_APP_NAME, name)
            .apply()
    }

    fun getCustomCTAAppName(): String? {
        return preferences.getString(SETTINGS_KEY_CUSTOM_CTA_APP_NAME, "")
    }

    fun setBundleId(bundleId: String) {
        preferences.edit().putString(SETTINGS_KEY_BUNDLE_ID, bundleId)
            .apply()
    }

    fun getBundleId(): String? {
        return preferences.getString(SETTINGS_KEY_BUNDLE_ID, "")
    }

    fun getAdCustomizationData(): String? {
        if (!isInitialised())
            return null
        return preferences.getString(SETTINGS_KEY_AD_CUSTOMIZATION_DATA, null)
    }
}