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

    fun setApiUrl(apiUrl: String) {
        preferences.edit().putString(SETTINGS_KEY_API_URL, apiUrl).apply()
    }

    fun setCoppa(enabled: Boolean) {
        preferences.edit().putBoolean(SETTINGS_KEY_COPPA, enabled).apply()
    }

    fun setTestMode(enabled: Boolean) {
        preferences.edit().putBoolean(SETTINGS_KEY_TEST_MODE, enabled).apply()
    }

    fun setLocationTracking(enabled: Boolean) {
        preferences.edit().putBoolean(SETTINGS_KEY_LOCATION_TRACKING, enabled).apply()
    }

    fun setLocationUpdates(enabled: Boolean) {
        preferences.edit().putBoolean(SETTINGS_KEY_LOCATION_UPDATES, enabled).apply()
    }

    fun setMraidExpanded(enabled: Boolean) {
        preferences.edit().putBoolean(SETTINGS_KEY_MRAID_EXPANDED, enabled).apply()
    }

    fun setInitialAudioState(state: Int) {
        preferences.edit().putInt(SETTINGS_KEY_INITIAL_AUDIO_STATE, state).apply()
    }

    fun setCloseVideoAfterFinish(enabled: Boolean) {
        preferences.edit().putBoolean(SETTINGS_KEY_CLOSE_VIDEO_AFTER_FINISH, enabled).apply()
    }

    fun setEnableEndcard(enabled: Boolean) {
        preferences.edit().putBoolean(SETTINGS_KEY_ENABLE_ENDCARD, enabled).apply()
    }

    fun setSkipOffset(skipOffset: Int) {
        preferences.edit().putInt(SETTINGS_KEY_SKIP_OFFSET, skipOffset).apply()
    }

    fun setVideoSkipOffset(videoSkipOffset: Int) {
        preferences.edit().putInt(SETTINGS_KEY_VIDEO_SKIP_OFFSET, videoSkipOffset).apply()
    }

    fun setEndCardCloseButtonDelay(endCardCloseButtonDelay: Int) {
        preferences.edit().putInt(SETTINGS_KEY_ENDCARD_CLOSE_BUTTON_DELAY, endCardCloseButtonDelay)
            .apply()
    }

    fun setVideoClickBehaviour(isCreative: Boolean) {
        preferences.edit().putBoolean(SETTINGS_KEY_VIDEO_CLICK_BEHAVIOUR, isCreative).apply()
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

    fun setBrowserPriorities(browserPriorities: List<String>) {
        preferences.edit().putStringSet(SETTINGS_KEY_BROWSER_PRIORITIES, browserPriorities.toSet())
            .apply()
    }

    fun setDFPBannerAdUnitId(adUnitId: String) {
        preferences.edit().putString(SETTINGS_KEY_DFP_BANNER_AD_UNIT_ID, adUnitId).apply()
    }

    fun setDFPMediumAdUnitId(adUnitId: String) {
        preferences.edit().putString(SETTINGS_KEY_DFP_MEDIUM_AD_UNIT_ID, adUnitId).apply()
    }

    fun setDFPLeaderboardAdUnitId(adUnitId: String) {
        preferences.edit().putString(SETTINGS_KEY_DFP_LEADERBOARD_AD_UNIT_ID, adUnitId).apply()
    }

    fun setDFPInterstitialAdUnitId(adUnitId: String) {
        preferences.edit().putString(SETTINGS_KEY_DFP_INTERSTITIAL_AD_UNIT_ID, adUnitId).apply()
    }

    fun setDFPMediationBannerAdUnitId(adUnitId: String) {
        preferences.edit().putString(SETTINGS_KEY_DFP_MEDIATION_BANNER_AD_UNIT_ID, adUnitId).apply()
    }

    fun setDFPMediationMediumAdUnitId(adUnitId: String) {
        preferences.edit().putString(SETTINGS_KEY_DFP_MEDIATION_MEDIUM_AD_UNIT_ID, adUnitId).apply()
    }

    fun setDFPMediationLeaderboardAdUnitId(adUnitId: String) {
        preferences.edit().putString(SETTINGS_KEY_DFP_MEDIATION_LEADERBOARD_AD_UNIT_ID, adUnitId)
            .apply()
    }

    fun setDFPMediationInterstitialAdUnitId(adUnitId: String) {
        preferences.edit().putString(SETTINGS_KEY_DFP_MEDIATION_INTERSTITIAL_AD_UNIT_ID, adUnitId)
            .apply()
    }

    fun setDFPMediationRewardedAdUnitId(adUnitId: String) {
        preferences.edit().putString(SETTINGS_KEY_DFP_MEDIATION_REWARDED_AD_UNIT_ID, adUnitId)
            .apply()
    }

    fun setAdmobAppId(appId: String) {
        preferences.edit().putString(SETTINGS_KEY_ADMOB_APP_ID, appId).apply()
    }

    fun setAdmobBannerAdUnitId(adUnitId: String) {
        preferences.edit().putString(SETTINGS_KEY_ADMOB_BANNER_AD_UNIT_ID, adUnitId).apply()
    }

    fun setAdmobMediumAdUnitId(adUnitId: String) {
        preferences.edit().putString(SETTINGS_KEY_ADMOB_MEDIUM_AD_UNIT_ID, adUnitId).apply()
    }

    fun setAdmobMediumVideoAdUnitId(adUnitId: String) {
        preferences.edit().putString(SETTINGS_KEY_ADMOB_MEDIUM_VIDEO_AD_UNIT_ID, adUnitId).apply()
    }

    fun setAdmobLeaderboardAdUnitId(adUnitId: String) {
        preferences.edit().putString(SETTINGS_KEY_ADMOB_LEADERBOARD_AD_UNIT_ID, adUnitId).apply()
    }

    fun setAdmobRewardedAdUnitId(adUnitId: String) {
        preferences.edit().putString(SETTINGS_KEY_ADMOB_REWARDED_AD_UNIT_ID, adUnitId).apply()
    }

    fun setAdmobInterstitialAdUnitId(adUnitId: String) {
        preferences.edit().putString(SETTINGS_KEY_ADMOB_INTERSTITIAL_AD_UNIT_ID, adUnitId).apply()
    }

    fun setAdmobInterstitialVideoAdUnitId(adUnitId: String) {
        preferences.edit().putString(SETTINGS_KEY_ADMOB_INTERSTITIAL_VIDEO_AD_UNIT_ID, adUnitId)
            .apply()
    }

    fun setAdmobNativeAdUnitId(adUnitId: String) {
        preferences.edit().putString(SETTINGS_KEY_ADMOB_NATIVE_AD_UNIT_ID, adUnitId).apply()
    }

    fun setIronSourceAppKey(appKey: String) {
        preferences.edit().putString(SETTINGS_KEY_IRONSOURCE_APP_KEY, appKey).apply()
    }

    fun setIronSourceBannerAdUnitId(adUnitId: String) {
        preferences.edit().putString(SETTINGS_KEY_IRONSOURCE_BANNER_AD_UNIT_ID, adUnitId).apply()
    }

    fun setIronSourceInterstitialAdUnitId(adUnitId: String) {
        preferences.edit().putString(SETTINGS_KEY_IRONSOURCE_INTERSTITIAL_AD_UNIT_ID, adUnitId)
            .apply()
    }

    fun setIronSourceRewardedAdUnitId(adUnitId: String) {
        preferences.edit().putString(SETTINGS_KEY_IRONSOURCE_REWARDED_AD_UNIT_ID, adUnitId).apply()
    }

    fun setMaxAdsSdkKey(sdkKey: String) {
        preferences.edit().putString(SETTINGS_KEY_MAXADS_SDK_KEY, sdkKey).apply()
    }

    fun setMaxAdsBannerAdUnitId(adUnitId: String) {
        preferences.edit().putString(SETTINGS_KEY_MAXADS_BANNER_AD_UNIT_ID, adUnitId).apply()
    }

    fun setMaxAdsMRectAdUnitId(adUnitId: String) {
        preferences.edit().putString(SETTINGS_KEY_MAXADS_MRECT_AD_UNIT_ID, adUnitId).apply()
    }

    fun setMaxAdsInterstitialAdUnitId(adUnitId: String) {
        preferences.edit().putString(SETTINGS_KEY_MAXADS_INTERSTITIAL_AD_UNIT_ID, adUnitId).apply()
    }

    fun setMaxAdsRewardedAdUnitId(adUnitId: String) {
        preferences.edit().putString(SETTINGS_KEY_MAXADS_REWARDED_AD_UNIT_ID, adUnitId).apply()
    }

    fun setMaxAdsNativeAdUnitId(adUnitId: String) {
        preferences.edit().putString(SETTINGS_KEY_MAXADS_NATIVE_AD_UNIT_ID, adUnitId).apply()
    }

    fun setFairbidAppId(appId: String) {
        preferences.edit().putString(SETTINGS_KEY_FAIRBID_APP_ID, appId).apply()
    }

    fun setFairbidMediationBannerAdUnitId(adUnitId: String) {
        preferences.edit().putString(SETTINGS_KEY_FAIRBID_MEDIATION_BANNER_AD_UNIT_ID, adUnitId)
            .apply()
    }

    fun setFairbidMediationInterstitialAdUnitId(adUnitId: String) {
        preferences.edit()
            .putString(SETTINGS_KEY_FAIRBID_MEDIATION_INTERSTITIAL_AD_UNIT_ID, adUnitId).apply()
    }

    fun setFairbidMediationRewardedAdUnitId(adUnitId: String) {
        preferences.edit().putString(SETTINGS_KEY_FAIRBID_MEDIATION_REWARDED_AD_UNIT_ID, adUnitId)
            .apply()
    }

    fun setFairbidBannerAdUnitId(adUnitId: String) {
        preferences.edit().putString(SETTINGS_KEY_FAIRBID_BANNER_AD_UNIT_ID, adUnitId).apply()
    }

    fun setFairbidInterstitialAdUnitId(adUnitId: String) {
        preferences.edit().putString(SETTINGS_KEY_FAIRBID_INTERSTITIAL_AD_UNIT_ID, adUnitId).apply()
    }

    fun setFairbidRewardedAdUnitId(adUnitId: String) {
        preferences.edit().putString(SETTINGS_KEY_FAIRBID_REWARDED_AD_UNIT_ID, adUnitId).apply()
    }


    fun setSettings(model: SettingsModel, asynchronously: Boolean) {
        val editor = preferences.edit()
        editor.putString(SETTINGS_KEY_APP_TOKEN, model.appToken)
        editor.putString(SETTINGS_KEY_API_URL, model.apiUrl)
        editor.putBoolean(SETTINGS_KEY_COPPA, model.coppa)
        editor.putBoolean(SETTINGS_KEY_TEST_MODE, model.testMode)
        editor.putBoolean(SETTINGS_KEY_LOCATION_TRACKING, model.locationTracking)
        editor.putString(SETTINGS_KEY_GENDER, model.gender)
        editor.putString(SETTINGS_KEY_AGE, model.age)
        editor.putStringSet(SETTINGS_KEY_ZONE_ID_LIST, model.zoneIds.toSet())
        editor.putStringSet(SETTINGS_KEY_KEYWORDS, model.keywords.toSet())
        editor.putStringSet(SETTINGS_KEY_BROWSER_PRIORITIES, model.browserPriorities.toSet())
        editor.putString(SETTINGS_KEY_DFP_BANNER_AD_UNIT_ID, model.dfpBannerAdUnitId)
        editor.putString(SETTINGS_KEY_DFP_MEDIUM_AD_UNIT_ID, model.dfpMediumAdUnitId)
        editor.putString(SETTINGS_KEY_DFP_LEADERBOARD_AD_UNIT_ID, model.dfpLeaderboardAdUnitId)
        editor.putString(SETTINGS_KEY_DFP_INTERSTITIAL_AD_UNIT_ID, model.dfpInterstitialAdUnitId)
        editor.putString(
            SETTINGS_KEY_DFP_MEDIATION_BANNER_AD_UNIT_ID,
            model.dfpMediationBannerAdUnitId
        )
        editor.putString(
            SETTINGS_KEY_DFP_MEDIATION_MEDIUM_AD_UNIT_ID,
            model.dfpMediationMediumAdUnitId
        )
        editor.putString(
            SETTINGS_KEY_DFP_MEDIATION_LEADERBOARD_AD_UNIT_ID,
            model.dfpMediationLeaderboardAdUnitId
        )
        editor.putString(
            SETTINGS_KEY_DFP_MEDIATION_INTERSTITIAL_AD_UNIT_ID,
            model.dfpMediationInterstitialAdUnitId
        )
        editor.putString(
            SETTINGS_KEY_DFP_MEDIATION_REWARDED_AD_UNIT_ID,
            model.dfpMediationRewardedAdUnitId
        )
        editor.putString(SETTINGS_KEY_ADMOB_APP_ID, model.admobAppId)
        editor.putString(SETTINGS_KEY_ADMOB_BANNER_AD_UNIT_ID, model.admobBannerAdUnitId)
        editor.putString(SETTINGS_KEY_ADMOB_MEDIUM_AD_UNIT_ID, model.admobMediumAdUnitId)
        editor.putString(SETTINGS_KEY_ADMOB_MEDIUM_VIDEO_AD_UNIT_ID, model.admobMediumVideoAdUnitId)
        editor.putString(SETTINGS_KEY_ADMOB_LEADERBOARD_AD_UNIT_ID, model.admobLeaderboardAdUnitId)
        editor.putString(SETTINGS_KEY_ADMOB_REWARDED_AD_UNIT_ID, model.admobRewardedAdUnitId)
        editor.putString(
            SETTINGS_KEY_ADMOB_INTERSTITIAL_AD_UNIT_ID,
            model.admobInterstitialAdUnitId
        )
        editor.putString(
            SETTINGS_KEY_ADMOB_INTERSTITIAL_VIDEO_AD_UNIT_ID,
            model.admobInterstitialVideoAdUnitId
        )
        editor.putString(SETTINGS_KEY_ADMOB_NATIVE_AD_UNIT_ID, model.admobNativeAdUnitId)
        editor.putString(SETTINGS_KEY_IRONSOURCE_APP_KEY, model.ironSourceAppKey)
        editor.putString(SETTINGS_KEY_IRONSOURCE_BANNER_AD_UNIT_ID, model.ironSourceBannerAdUnitId)
        editor.putString(
            SETTINGS_KEY_IRONSOURCE_INTERSTITIAL_AD_UNIT_ID,
            model.ironSourceInterstitialAdUnitId
        )
        editor.putString(
            SETTINGS_KEY_IRONSOURCE_REWARDED_AD_UNIT_ID,
            model.ironSourceRewardedAdUnitId
        )
        editor.putString(SETTINGS_KEY_MAXADS_SDK_KEY, model.maxAdsSdkKey)
        editor.putString(SETTINGS_KEY_MAXADS_BANNER_AD_UNIT_ID, model.maxAdsBannerAdUnitId)
        editor.putString(SETTINGS_KEY_MAXADS_MRECT_AD_UNIT_ID, model.maxAdsMRectAdUnitId)
        editor.putString(
            SETTINGS_KEY_MAXADS_INTERSTITIAL_AD_UNIT_ID,
            model.maxAdsInterstitialAdUnitId
        )
        editor.putString(SETTINGS_KEY_MAXADS_REWARDED_AD_UNIT_ID, model.maxAdsRewardedAdUnitId)
        editor.putString(SETTINGS_KEY_MAXADS_NATIVE_AD_UNIT_ID, model.maxAdsNativeAdUnitId)
        editor.putString(SETTINGS_KEY_FAIRBID_APP_ID, model.fairbidAppId)
        editor.putString(
            SETTINGS_KEY_FAIRBID_MEDIATION_BANNER_AD_UNIT_ID,
            model.fairbidMediationBannerAdUnitId
        )
        editor.putString(
            SETTINGS_KEY_FAIRBID_MEDIATION_INTERSTITIAL_AD_UNIT_ID,
            model.fairbidMediationInterstitialAdUnitId
        )
        editor.putString(
            SETTINGS_KEY_FAIRBID_MEDIATION_REWARDED_AD_UNIT_ID,
            model.fairbidMediationRewardedAdUnitId
        )

        editor.putString(SETTINGS_KEY_FAIRBID_BANNER_AD_UNIT_ID, model.fairbidBannerAdUnitId)
        editor.putString(
            SETTINGS_KEY_FAIRBID_INTERSTITIAL_AD_UNIT_ID,
            model.fairbidInterstitialAdUnitId
        )
        editor.putString(SETTINGS_KEY_FAIRBID_REWARDED_AD_UNIT_ID, model.fairbidRewardedAdUnitId)

        editor.putBoolean(SETTINGS_KEY_INITIALISED, true)

        if (asynchronously) {
            editor.apply()
        } else {
            editor.commit()
        }
    }

    fun getSettings(): SettingsModel {
        val appToken = preferences.getString(SETTINGS_KEY_APP_TOKEN, "")!!
        val apiUrl = preferences.getString(SETTINGS_KEY_API_URL, "")!!
        val zoneIds = preferences.getStringSet(SETTINGS_KEY_ZONE_ID_LIST, emptySet())?.toList()!!
        val gender = preferences.getString(SETTINGS_KEY_GENDER, "")!!
        val age = preferences.getString(SETTINGS_KEY_AGE, "")!!
        val keywords = preferences.getStringSet(SETTINGS_KEY_KEYWORDS, emptySet())?.toList()!!
        val browserPriorities =
            preferences.getStringSet(SETTINGS_KEY_BROWSER_PRIORITIES, emptySet())?.toList()!!
        val coppa = preferences.getBoolean(SETTINGS_KEY_COPPA, false)
        val testMode = preferences.getBoolean(SETTINGS_KEY_TEST_MODE, false)
        val locationTracking = preferences.getBoolean(SETTINGS_KEY_LOCATION_TRACKING, false)
        val locationUpdates = preferences.getBoolean(SETTINGS_KEY_LOCATION_UPDATES, false)
        val initialAudioState = preferences.getInt(SETTINGS_KEY_INITIAL_AUDIO_STATE, 0)
        val mraidExpanded = preferences.getBoolean(SETTINGS_KEY_MRAID_EXPANDED, true)
        val closeVideoAfterFinish =
            preferences.getBoolean(SETTINGS_KEY_CLOSE_VIDEO_AFTER_FINISH, false)
        val enableEndcard = preferences.getBoolean(SETTINGS_KEY_ENABLE_ENDCARD, false)
        val skipOffset = preferences.getInt(SETTINGS_KEY_SKIP_OFFSET, 3)
        val videoSkipOffset = preferences.getInt(SETTINGS_KEY_VIDEO_SKIP_OFFSET, 8)
        val endcardCloseButtonDelay = preferences.getInt(SETTINGS_KEY_ENDCARD_CLOSE_BUTTON_DELAY, 5)
        val videoClickBehaviour = preferences.getBoolean(SETTINGS_KEY_VIDEO_CLICK_BEHAVIOUR, true)
        val dfpBannerAdUnitId = preferences.getString(SETTINGS_KEY_DFP_BANNER_AD_UNIT_ID, "")!!
        val dfpMediumAdUnitId = preferences.getString(SETTINGS_KEY_DFP_MEDIUM_AD_UNIT_ID, "")!!
        val dfpLeaderboardAdUnitId =
            preferences.getString(SETTINGS_KEY_DFP_LEADERBOARD_AD_UNIT_ID, "")!!
        val dfpInterstitialAdUnitId =
            preferences.getString(SETTINGS_KEY_DFP_INTERSTITIAL_AD_UNIT_ID, "")!!
        val dfpMediationBannerAdUnitId =
            preferences.getString(SETTINGS_KEY_DFP_MEDIATION_BANNER_AD_UNIT_ID, "")!!
        val dfpMediationMediumAdUnitId =
            preferences.getString(SETTINGS_KEY_DFP_MEDIATION_MEDIUM_AD_UNIT_ID, "")!!
        val dfpMediationLeaderboardAdUnitId =
            preferences.getString(SETTINGS_KEY_DFP_MEDIATION_LEADERBOARD_AD_UNIT_ID, "")!!
        val dfpMediationInterstitialAdUnitId =
            preferences.getString(SETTINGS_KEY_DFP_MEDIATION_INTERSTITIAL_AD_UNIT_ID, "")!!
        val dfpMediationRewardedAdUnitId =
            preferences.getString(SETTINGS_KEY_DFP_MEDIATION_REWARDED_AD_UNIT_ID, "")!!
        val admobAppId = preferences.getString(SETTINGS_KEY_ADMOB_APP_ID, "")!!
        val admobBannerAdUnitId = preferences.getString(SETTINGS_KEY_ADMOB_BANNER_AD_UNIT_ID, "")!!
        val admobMediumAdUnitId = preferences.getString(SETTINGS_KEY_ADMOB_MEDIUM_AD_UNIT_ID, "")!!
        val admobMediumVideoAdUnitId =
            preferences.getString(SETTINGS_KEY_ADMOB_MEDIUM_VIDEO_AD_UNIT_ID, "")!!
        val admobLeaderboardAdUnitId =
            preferences.getString(SETTINGS_KEY_ADMOB_LEADERBOARD_AD_UNIT_ID, "")!!
        val admobRewardedAdUnitId =
            preferences.getString(SETTINGS_KEY_ADMOB_REWARDED_AD_UNIT_ID, "")!!
        val admobInterstitialAdUnitId =
            preferences.getString(SETTINGS_KEY_ADMOB_INTERSTITIAL_AD_UNIT_ID, "")!!
        val admobInterstitialVideoAdUnitId =
            preferences.getString(SETTINGS_KEY_ADMOB_INTERSTITIAL_VIDEO_AD_UNIT_ID, "")!!
        val admobNativeAdUnitId = preferences.getString(SETTINGS_KEY_ADMOB_NATIVE_AD_UNIT_ID, "")!!
        val ironSourceAppKey =
            preferences.getString(SETTINGS_KEY_IRONSOURCE_APP_KEY, "")!!
        val ironSourceBannerAdUnitId =
            preferences.getString(SETTINGS_KEY_IRONSOURCE_BANNER_AD_UNIT_ID, "")!!
        val ironSourceInterstitialAdUnitId =
            preferences.getString(SETTINGS_KEY_IRONSOURCE_INTERSTITIAL_AD_UNIT_ID, "")!!
        val ironSourceRewardedAdUnitId =
            preferences.getString(SETTINGS_KEY_IRONSOURCE_REWARDED_AD_UNIT_ID, "")!!
        val maxAdsSdkKey =
            preferences.getString(SETTINGS_KEY_MAXADS_SDK_KEY, "")!!
        val maxAdsBannerAdUnitId =
            preferences.getString(SETTINGS_KEY_MAXADS_BANNER_AD_UNIT_ID, "")!!
        val maxAdsMRectAdUnitId =
            preferences.getString(SETTINGS_KEY_MAXADS_MRECT_AD_UNIT_ID, "")!!
        val maxAdsInterstitialAdUnitId =
            preferences.getString(SETTINGS_KEY_MAXADS_INTERSTITIAL_AD_UNIT_ID, "")!!
        val maxAdsRewardedAdUnitId =
            preferences.getString(SETTINGS_KEY_MAXADS_REWARDED_AD_UNIT_ID, "")!!
        val maxAdsNativeAdUnitId =
            preferences.getString(SETTINGS_KEY_MAXADS_NATIVE_AD_UNIT_ID, "")!!
        val fairbidAppId =
            preferences.getString(SETTINGS_KEY_FAIRBID_APP_ID, "")!!
        val fairbidMediationBannerAdUnitId =
            preferences.getString(SETTINGS_KEY_FAIRBID_MEDIATION_BANNER_AD_UNIT_ID, "")!!
        val fairbidMediationInterstitialAdUnitId =
            preferences.getString(SETTINGS_KEY_FAIRBID_MEDIATION_INTERSTITIAL_AD_UNIT_ID, "")!!
        val fairbidMediationRewardedAdUnitId =
            preferences.getString(SETTINGS_KEY_FAIRBID_MEDIATION_REWARDED_AD_UNIT_ID, "")!!

        val fairbidBannerAdUnitId =
            preferences.getString(SETTINGS_KEY_FAIRBID_BANNER_AD_UNIT_ID, "")!!
        val fairbidInterstitialAdUnitId =
            preferences.getString(SETTINGS_KEY_FAIRBID_INTERSTITIAL_AD_UNIT_ID, "")!!
        val fairbidRewardedAdUnitId =
            preferences.getString(SETTINGS_KEY_FAIRBID_REWARDED_AD_UNIT_ID, "")!!

        return SettingsModel(
            appToken,
            zoneIds,
            apiUrl,
            gender,
            age,
            keywords,
            browserPriorities,
            coppa,
            testMode,
            locationTracking,
            locationUpdates,
            initialAudioState,
            mraidExpanded,
            closeVideoAfterFinish,
            enableEndcard,
            skipOffset,
            videoSkipOffset,
            endcardCloseButtonDelay,
            videoClickBehaviour,
            dfpBannerAdUnitId,
            dfpMediumAdUnitId,
            dfpLeaderboardAdUnitId,
            dfpInterstitialAdUnitId,
            dfpMediationBannerAdUnitId,
            dfpMediationMediumAdUnitId,
            dfpMediationLeaderboardAdUnitId,
            dfpMediationInterstitialAdUnitId,
            dfpMediationRewardedAdUnitId,
            admobAppId,
            admobBannerAdUnitId,
            admobMediumAdUnitId,
            admobMediumVideoAdUnitId,
            admobLeaderboardAdUnitId,
            admobRewardedAdUnitId,
            admobInterstitialAdUnitId,
            admobInterstitialVideoAdUnitId,
            admobNativeAdUnitId,
            ironSourceAppKey,
            ironSourceBannerAdUnitId,
            ironSourceInterstitialAdUnitId,
            ironSourceRewardedAdUnitId,
            maxAdsSdkKey,
            maxAdsBannerAdUnitId,
            maxAdsMRectAdUnitId,
            maxAdsInterstitialAdUnitId,
            maxAdsRewardedAdUnitId,
            maxAdsNativeAdUnitId,
            fairbidAppId,
            fairbidMediationBannerAdUnitId,
            fairbidMediationInterstitialAdUnitId,
            fairbidMediationRewardedAdUnitId,
            fairbidBannerAdUnitId,
            fairbidInterstitialAdUnitId,
            fairbidRewardedAdUnitId,
        )
    }
}