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
import net.pubnative.lite.demo.models.*
import net.pubnative.lite.demo.util.SingletonHolder
import net.pubnative.lite.sdk.CountdownStyle

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

    fun setLocationTrackingEnabled(enabled: Boolean) {
        preferences.edit().putBoolean(SETTINGS_KEY_LOCATION_TRACKING_ENABLED, enabled).apply()
    }

    fun setLocationUpdates(enabled: Boolean) {
        preferences.edit().putBoolean(SETTINGS_KEY_LOCATION_UPDATES, enabled).apply()
    }

    fun setLocationUpdatesEnabled(enabled: Boolean) {
        preferences.edit().putBoolean(SETTINGS_KEY_LOCATION_UPDATES_ENABLED, enabled).apply()
    }

    fun setTopicsApi(enabled: Boolean) {
        preferences.edit().putBoolean(SETTINGS_KEY_TOPICS_API, enabled).apply()
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

    fun setCloseVideoAfterFinishForRewardedVideo(enabled: Boolean) {
        preferences.edit().putBoolean(SETTINGS_KEY_CLOSE_VIDEO_AFTER_FINISH_REWARDED, enabled)
            .apply()
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

    fun setFeedbackFormEnabled(enabled: Boolean) {
        preferences.edit().putBoolean(SETTINGS_KEY_FEEDBACK_FORM_ENABLED, enabled).apply()
    }

    fun setFeedbackFormUrl(url: String) {
        preferences.edit().putString(SETTINGS_KEY_FEEDBACK_FORM_URL, url).apply()
    }

    fun setCountdownStyle(id: String) {
        preferences.edit().putString(SETTINGS_KEY_COUNTDOWN_STYLE, id).apply()
    }

    fun setSettings(model: Settings?, asynchronously: Boolean) {
        if (model != null) {
            val editor = preferences.edit()
            model.hybidSettings?.let {
                editor.putString(SETTINGS_KEY_APP_TOKEN, it.appToken)
                editor.putString(SETTINGS_KEY_API_URL, it.apiUrl)
                it.coppa?.let { it1 -> editor.putBoolean(SETTINGS_KEY_COPPA, it1) }
                it.testMode?.let { it1 -> editor.putBoolean(SETTINGS_KEY_TEST_MODE, it1) }
                it.gender?.let { it1 -> editor.putString(SETTINGS_KEY_GENDER, it1) }
                it.age?.let { it1 -> editor.putString(SETTINGS_KEY_AGE, it1) }
                it.zoneIds?.toSet()
                    ?.let { it1 -> editor.putStringSet(SETTINGS_KEY_ZONE_ID_LIST, it1) }
                it.keywords?.toSet()?.let { it1 -> editor.putStringSet(SETTINGS_KEY_KEYWORDS, it1) }
                it.browserPriorities?.toSet()
                    ?.let { it1 -> editor.putStringSet(SETTINGS_KEY_BROWSER_PRIORITIES, it1) }
            }

            model.adCustomizationSettings?.let {
                it.locationTracking?.let { it1 ->
                    editor.putBoolean(
                        SETTINGS_KEY_LOCATION_TRACKING, it1
                    )
                }

                it.locationUpdates?.let { it1 ->
                    editor.putBoolean(
                        SETTINGS_KEY_LOCATION_UPDATES, it1
                    )
                }

                it.locationTrackingEnabled?.let {
                    editor.putBoolean(
                        SETTINGS_KEY_LOCATION_TRACKING_ENABLED, it
                    )
                }

                it.locationUpdatesEnabled?.let {
                    editor.putBoolean(
                        SETTINGS_KEY_LOCATION_UPDATES_ENABLED, it
                    )
                }
            }

            model.dfpSettings?.let {
                editor.putString(
                    SETTINGS_KEY_DFP_MEDIATION_BANNER_AD_UNIT_ID, it.mediationBannerAdUnitId
                )
                editor.putString(
                    SETTINGS_KEY_DFP_MEDIATION_MEDIUM_AD_UNIT_ID, it.mediationMediumAdUnitId
                )
                editor.putString(
                    SETTINGS_KEY_DFP_MEDIATION_LEADERBOARD_AD_UNIT_ID,
                    it.mediationLeaderboardAdUnitId
                )
                editor.putString(
                    SETTINGS_KEY_DFP_MEDIATION_INTERSTITIAL_AD_UNIT_ID,
                    it.mediationInterstitialAdUnitId
                )
                editor.putString(
                    SETTINGS_KEY_DFP_MEDIATION_REWARDED_AD_UNIT_ID, it.mediationRewardedAdUnitId
                )
            }

            model.admobSettings?.let {
                editor.putString(SETTINGS_KEY_ADMOB_APP_ID, it.appId)
                editor.putString(SETTINGS_KEY_ADMOB_BANNER_AD_UNIT_ID, it.bannerAdUnitId)
                editor.putString(SETTINGS_KEY_ADMOB_MEDIUM_AD_UNIT_ID, it.mediumAdUnitId)
                editor.putString(
                    SETTINGS_KEY_ADMOB_MEDIUM_VIDEO_AD_UNIT_ID, it.mediumVideoAdUnitId
                )
                editor.putString(
                    SETTINGS_KEY_ADMOB_LEADERBOARD_AD_UNIT_ID, it.leaderboardAdUnitId
                )
                editor.putString(SETTINGS_KEY_ADMOB_REWARDED_AD_UNIT_ID, it.rewardedAdUnitId)
                editor.putString(
                    SETTINGS_KEY_ADMOB_INTERSTITIAL_AD_UNIT_ID, it.interstitialAdUnitId
                )
                editor.putString(
                    SETTINGS_KEY_ADMOB_INTERSTITIAL_VIDEO_AD_UNIT_ID, it.interstitialVideoAdUnitId
                )
                editor.putString(SETTINGS_KEY_ADMOB_NATIVE_AD_UNIT_ID, it.nativeAdUnitId)
            }

            model.ironSourceSettings?.let {
                editor.putString(SETTINGS_KEY_IRONSOURCE_APP_KEY, it.appKey)
                editor.putString(
                    SETTINGS_KEY_IRONSOURCE_BANNER_AD_UNIT_ID, it.bannerAdUnitId
                )
                editor.putString(
                    SETTINGS_KEY_IRONSOURCE_INTERSTITIAL_AD_UNIT_ID, it.interstitialAdUnitId
                )
                editor.putString(
                    SETTINGS_KEY_IRONSOURCE_REWARDED_AD_UNIT_ID, it.rewardedAdUnitId
                )
            }

            model.maxAdsSettings?.let {

                editor.putString(SETTINGS_KEY_MAXADS_SDK_KEY, it.sdkKey)
                editor.putString(SETTINGS_KEY_MAXADS_BANNER_AD_UNIT_ID, it.bannerAdUnitId)
                editor.putString(SETTINGS_KEY_MAXADS_MRECT_AD_UNIT_ID, it.mRectAdUnitId)
                editor.putString(
                    SETTINGS_KEY_MAXADS_INTERSTITIAL_AD_UNIT_ID, it.interstitialAdUnitId
                )
                editor.putString(SETTINGS_KEY_MAXADS_REWARDED_AD_UNIT_ID, it.rewardedAdUnitId)
                editor.putString(SETTINGS_KEY_MAXADS_NATIVE_AD_UNIT_ID, it.nativeAdUnitId)
            }

            model.fairbidSettings?.let {
                editor.putString(SETTINGS_KEY_FAIRBID_APP_ID, it.appId)
                editor.putString(
                    SETTINGS_KEY_FAIRBID_MEDIATION_BANNER_AD_UNIT_ID, it.mediationBannerAdUnitId
                )
                editor.putString(
                    SETTINGS_KEY_FAIRBID_MEDIATION_INTERSTITIAL_AD_UNIT_ID,
                    it.mediationInterstitialAdUnitId
                )
                editor.putString(
                    SETTINGS_KEY_FAIRBID_MEDIATION_REWARDED_AD_UNIT_ID, it.mediationRewardedAdUnitId
                )
            }

            editor.putBoolean(SETTINGS_KEY_INITIALISED, true)

            if (asynchronously) {
                editor.apply()
            } else {
                editor.commit()
            }
        }
    }

    fun getSettings(): Settings {
        val appToken = preferences.getString(SETTINGS_KEY_APP_TOKEN, "")!!
        val apiUrl = preferences.getString(SETTINGS_KEY_API_URL, "")!!
        val zoneIds = preferences.getStringSet(SETTINGS_KEY_ZONE_ID_LIST, emptySet())?.toList()!!
        val gender = preferences.getString(SETTINGS_KEY_GENDER, "")!!
        val age = preferences.getString(SETTINGS_KEY_AGE, "")!!
        val keywords = preferences.getStringSet(SETTINGS_KEY_KEYWORDS, emptySet())?.toList()!!
        val browserPriorities =
            preferences.getStringSet(SETTINGS_KEY_BROWSER_PRIORITIES, emptySet())?.toList()!!
        val coppa = preferences.getBoolean(SETTINGS_KEY_COPPA, Constants.COPPA_DEFAULT)
        val testMode = preferences.getBoolean(SETTINGS_KEY_TEST_MODE, Constants.TEST_MODE_DEFAULT)
        val topicsApi = preferences.getBoolean(SETTINGS_KEY_TOPICS_API, Constants.TOPICS_API_DEFAULT)
        val locationTracking = preferences.getBoolean(
            SETTINGS_KEY_LOCATION_TRACKING,
            Constants.LOCATION_TRACKING_DEFAULT
        )
        val locationUpdates = preferences.getBoolean(
            SETTINGS_KEY_LOCATION_UPDATES,
            Constants.LOCATION_UPDATES_DEFAULT
        )
        val locationTrackingEnabled = preferences.getBoolean(
            SETTINGS_KEY_LOCATION_TRACKING_ENABLED,
            true
        )
        val locationUpdatesEnabled = preferences.getBoolean(
            SETTINGS_KEY_LOCATION_UPDATES_ENABLED,
            true
        )
        val initialAudioState = preferences.getInt(
            SETTINGS_KEY_INITIAL_AUDIO_STATE,
            Constants.INITIAL_AUDIO_STATE_DEFAULT
        )
        val mraidExpanded =
            preferences.getBoolean(SETTINGS_KEY_MRAID_EXPANDED, Constants.MRAID_EXPANDED_DEFAULT)
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
        val ironSourceAppKey = preferences.getString(SETTINGS_KEY_IRONSOURCE_APP_KEY, "")!!
        val ironSourceBannerAdUnitId =
            preferences.getString(SETTINGS_KEY_IRONSOURCE_BANNER_AD_UNIT_ID, "")!!
        val ironSourceInterstitialAdUnitId =
            preferences.getString(SETTINGS_KEY_IRONSOURCE_INTERSTITIAL_AD_UNIT_ID, "")!!
        val ironSourceRewardedAdUnitId =
            preferences.getString(SETTINGS_KEY_IRONSOURCE_REWARDED_AD_UNIT_ID, "")!!
        val maxAdsSdkKey = preferences.getString(SETTINGS_KEY_MAXADS_SDK_KEY, "")!!
        val maxAdsBannerAdUnitId =
            preferences.getString(SETTINGS_KEY_MAXADS_BANNER_AD_UNIT_ID, "")!!
        val maxAdsMRectAdUnitId = preferences.getString(SETTINGS_KEY_MAXADS_MRECT_AD_UNIT_ID, "")!!
        val maxAdsInterstitialAdUnitId =
            preferences.getString(SETTINGS_KEY_MAXADS_INTERSTITIAL_AD_UNIT_ID, "")!!
        val maxAdsRewardedAdUnitId =
            preferences.getString(SETTINGS_KEY_MAXADS_REWARDED_AD_UNIT_ID, "")!!
        val maxAdsNativeAdUnitId =
            preferences.getString(SETTINGS_KEY_MAXADS_NATIVE_AD_UNIT_ID, "")!!
        val fairbidAppId = preferences.getString(SETTINGS_KEY_FAIRBID_APP_ID, "")!!
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

        val settings = Settings()

        val hybidSettings =
            HyBidSettings.Builder().appToken(appToken).zoneIds(zoneIds).apiUrl(apiUrl).age(age)
                .keywords(keywords).browserPriorities(browserPriorities).coppa(coppa)
                .testMode(testMode).gender(gender).topicsApi(topicsApi).build()

        val adCustomizationSettings =
            AdCustomizationSettings.Builder()
                .initialAudioState(initialAudioState).locationTracking(locationTracking)
                .locationUpdates(locationUpdates)
                .locationTrackingEnabled(locationTrackingEnabled)
                .locationUpdatesEnabled(locationUpdatesEnabled)
                .build()

        val dfpSettings = DFPSettings.Builder()
            .mediationBannerAdUnitId(dfpMediationBannerAdUnitId)
            .mediationMediumAdUnitId(dfpMediationMediumAdUnitId)
            .mediationLeaderboardAdUnitId(dfpMediationLeaderboardAdUnitId)
            .mediationInterstitialAdUnitId(dfpMediationInterstitialAdUnitId)
            .mediationRewardedAdUnitId(dfpMediationRewardedAdUnitId).build()

        val fairbidSettings =
            FairbidSettings.Builder().appId(fairbidAppId)
                .mediationBannerAdUnitId(fairbidMediationBannerAdUnitId)
                .mediationInterstitialAdUnitId(fairbidMediationInterstitialAdUnitId)
                .mediationRewardedAdUnitId(fairbidMediationRewardedAdUnitId).build()

        val admobSettings =
            AdmobSettings.Builder().appId(admobAppId).bannerAdUnitId(admobBannerAdUnitId)
                .interstitialAdUnitId(admobInterstitialAdUnitId).nativeAdUnitId(admobNativeAdUnitId)
                .interstitialVideoAdUnitId(admobInterstitialVideoAdUnitId)
                .mediumAdUnitId(admobMediumAdUnitId).mediumVideoAdUnitId(admobMediumVideoAdUnitId)
                .leaderboardAdUnitId(admobLeaderboardAdUnitId)
                .rewardedAdUnitId(admobRewardedAdUnitId).build()

        val ironSourceSettings = IronSourceSettings.Builder().appKey(ironSourceAppKey)
            .bannerAdUnitId(ironSourceBannerAdUnitId)
            .interstitialAdUnitId(ironSourceInterstitialAdUnitId)
            .rewardedAdUnitId(ironSourceRewardedAdUnitId).build()

        val maxAdsSettings =
            MaxAdsSettings.Builder().sdkKey(maxAdsSdkKey).bannerAdUnitId(maxAdsBannerAdUnitId)
                .interstitialAdUnitId(maxAdsInterstitialAdUnitId).mRectAdUnitId(maxAdsMRectAdUnitId)
                .rewardedAdUnitId(maxAdsRewardedAdUnitId).nativeAdUnitId(maxAdsNativeAdUnitId)
                .build()

        settings.hybidSettings = hybidSettings
        settings.adCustomizationSettings = adCustomizationSettings
        settings.dfpSettings = dfpSettings
        settings.fairbidSettings = fairbidSettings
        settings.admobSettings = admobSettings
        settings.ironSourceSettings = ironSourceSettings
        settings.maxAdsSettings = maxAdsSettings

        return settings
    }
}