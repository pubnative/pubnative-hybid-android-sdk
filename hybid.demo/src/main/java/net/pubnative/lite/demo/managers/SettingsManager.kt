package net.pubnative.lite.demo.managers

import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager
import net.pubnative.lite.demo.*
import net.pubnative.lite.demo.models.SettingsModel
import net.pubnative.lite.demo.util.SingletonHolder

/**
 * Created by erosgarciaponte on 30.01.18.
 */
class SettingsManager private constructor(context: Context) {
    private val preferences: SharedPreferences

    init {
        preferences = PreferenceManager.getDefaultSharedPreferences(context)
    }

    companion object : SingletonHolder<SettingsManager, Context>(::SettingsManager)

    fun setInitialised(initialised: Boolean) {
        preferences.edit().putBoolean(SETTINGS_KEY_INITIALISED, initialised).apply()
    }

    fun isInitialised(): Boolean {
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

    fun setMoPubBannerAdUnitId(adUnitId: String) {
        preferences.edit().putString(SETTINGS_KEY_MOPUB_BANNER_AD_UNIT_ID, adUnitId).apply()
    }

    fun setMoPubMediumAdUnitId(adUnitId: String) {
        preferences.edit().putString(SETTINGS_KEY_MOPUB_MEDIUM_AD_UNIT_ID, adUnitId).apply()
    }

    fun setMoPubInterstitialAdUnitId(adUnitId: String) {
        preferences.edit().putString(SETTINGS_KEY_MOPUB_INTERSTITIAL_AD_UNIT_ID, adUnitId).apply()
    }

    fun setMoPubMediationBannerAdUnitId(adUnitId: String) {
        preferences.edit().putString(SETTINGS_KEY_MOPUB_MEDIATION_BANNER_AD_UNIT_ID, adUnitId).apply()
    }

    fun setMoPubMediationMediumAdUnitId(adUnitId: String) {
        preferences.edit().putString(SETTINGS_KEY_MOPUB_MEDIATION_MEDIUM_AD_UNIT_ID, adUnitId).apply()
    }

    fun setMoPubMediationInterstitialAdUnitId(adUnitId: String) {
        preferences.edit().putString(SETTINGS_KEY_MOPUB_MEDIATION_INTERSTITIAL_AD_UNIT_ID, adUnitId).apply()
    }

    fun setDFPBannerAdUnitId(adUnitId: String) {
        preferences.edit().putString(SETTINGS_KEY_DFP_BANNER_AD_UNIT_ID, adUnitId).apply()
    }

    fun setDFPMediumAdUnitId(adUnitId: String) {
        preferences.edit().putString(SETTINGS_KEY_DFP_MEDIUM_AD_UNIT_ID, adUnitId).apply()
    }

    fun setDFPInterstitialAdUnitId(adUnitId: String) {
        preferences.edit().putString(SETTINGS_KEY_DFP_INTERSTITIAL_AD_UNIT_ID, adUnitId).apply()
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
        editor.putString(SETTINGS_KEY_MOPUB_BANNER_AD_UNIT_ID, model.mopubBannerAdUnitId)
        editor.putString(SETTINGS_KEY_MOPUB_MEDIUM_AD_UNIT_ID, model.mopubMediumAdUnitId)
        editor.putString(SETTINGS_KEY_MOPUB_INTERSTITIAL_AD_UNIT_ID, model.mopubInterstitialAdUnitId)
        editor.putString(SETTINGS_KEY_MOPUB_MEDIATION_BANNER_AD_UNIT_ID, model.mopubMediationBannerAdUnitId)
        editor.putString(SETTINGS_KEY_MOPUB_MEDIATION_MEDIUM_AD_UNIT_ID, model.mopubMediationMediumAdUnitId)
        editor.putString(SETTINGS_KEY_MOPUB_MEDIATION_INTERSTITIAL_AD_UNIT_ID, model.mopubMediationInterstitialAdUnitId)
        editor.putString(SETTINGS_KEY_DFP_BANNER_AD_UNIT_ID, model.dfpBannerAdUnitId)
        editor.putString(SETTINGS_KEY_DFP_MEDIUM_AD_UNIT_ID, model.dfpMediumAdUnitId)
        editor.putString(SETTINGS_KEY_DFP_INTERSTITIAL_AD_UNIT_ID, model.dfpInterstitialAdUnitId)

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
        val mopubBannerAdUnitId = preferences.getString(SETTINGS_KEY_MOPUB_BANNER_AD_UNIT_ID, "")
        val mopubMediumAdUnitId = preferences.getString(SETTINGS_KEY_MOPUB_MEDIUM_AD_UNIT_ID, "")
        val mopubInterstitialAdUnitId = preferences.getString(SETTINGS_KEY_MOPUB_INTERSTITIAL_AD_UNIT_ID, "")
        val mopubMediationBannerAdUnitId = preferences.getString(SETTINGS_KEY_MOPUB_MEDIATION_BANNER_AD_UNIT_ID, "")
        val mopubMediationMediumAdUnitId = preferences.getString(SETTINGS_KEY_MOPUB_MEDIATION_MEDIUM_AD_UNIT_ID, "")
        val mopubMediationInterstitialAdUnitId = preferences.getString(SETTINGS_KEY_MOPUB_MEDIATION_INTERSTITIAL_AD_UNIT_ID, "")
        val dfpBannerAdUnitId = preferences.getString(SETTINGS_KEY_DFP_BANNER_AD_UNIT_ID, "")
        val dfpMediumAdUnitId = preferences.getString(SETTINGS_KEY_DFP_MEDIUM_AD_UNIT_ID, "")
        val dfpInterstitialAdUnitId = preferences.getString(SETTINGS_KEY_DFP_INTERSTITIAL_AD_UNIT_ID, "")

        val model = SettingsModel(appToken, zoneIds, gender, age, keywords, coppa, testMode,
                mopubBannerAdUnitId, mopubMediumAdUnitId, mopubInterstitialAdUnitId,
                mopubMediationBannerAdUnitId, mopubMediationMediumAdUnitId, mopubMediationInterstitialAdUnitId,
                dfpBannerAdUnitId, dfpMediumAdUnitId, dfpInterstitialAdUnitId)
        return model
    }
}