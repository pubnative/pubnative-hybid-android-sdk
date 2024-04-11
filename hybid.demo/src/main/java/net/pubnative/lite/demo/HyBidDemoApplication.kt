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
package net.pubnative.lite.demo

import android.content.Context
import android.text.TextUtils
import android.util.Log
import android.webkit.WebView
import androidx.multidex.MultiDex
import androidx.multidex.MultiDexApplication
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.initialization.InitializationStatus
import net.pubnative.lite.demo.managers.AdCustomizationPrefs
import net.pubnative.lite.demo.managers.AdCustomizationsManager
import net.pubnative.lite.demo.managers.AnalyticsSubscriber.eventCallback
import net.pubnative.lite.demo.managers.SettingsManager
import net.pubnative.lite.demo.models.*
import net.pubnative.lite.sdk.CountdownStyle
import net.pubnative.lite.sdk.HyBid
import net.pubnative.lite.sdk.api.ApiManager.setApiUrl
import net.pubnative.lite.sdk.models.ContentInfoDisplay
import net.pubnative.lite.sdk.models.ContentInfoIconAction
import net.pubnative.lite.sdk.models.CustomEndCardDisplay
import net.pubnative.lite.sdk.models.ImpressionTrackingMethod
import net.pubnative.lite.sdk.utils.Logger
import net.pubnative.lite.sdk.visibility.ImpressionTracker
import net.pubnative.lite.sdk.vpaid.enums.AudioState


/**
 * Created by erosgarciaponte on 08.01.18.
 */
class HyBidDemoApplication : MultiDexApplication() {
    override fun onCreate() {
        super.onCreate()
        try {
            initSettings()
        } catch (exception: Exception) {
            Log.d("Exception", exception.toString())
            HyBid.reportException(exception)
        }
    }

    override fun attachBaseContext(base: Context) {
        super.attachBaseContext(base)
        MultiDex.install(this)
    }

    private fun initSettings() {
        WebView.setWebContentsDebuggingEnabled(true)

        val settings = fetchSettings()
        HyBid.setLogLevel(Logger.Level.debug)

        if (settings.hybidSettings != null) {
            val appToken = settings.hybidSettings!!.appToken

            HyBid.initialize(appToken, this) { // HyBid SDK has been initialised
                HyBid.addReportingCallback(eventCallback)
            }

            settings.hybidSettings!!.testMode?.let { HyBid.setTestMode(it) }
            settings.hybidSettings!!.coppa?.let { HyBid.setCoppaEnabled(it) }
            settings.hybidSettings!!.age?.let { HyBid.setAge(it) }
            settings.hybidSettings!!.gender?.let { HyBid.setGender(it) }

            settings.hybidSettings!!.keywords?.let {
                val keywordsBuilder = StringBuilder()
                val separator = ","
                for (keyword in it) {
                    keywordsBuilder.append(keyword)
                    keywordsBuilder.append(separator)
                }
                var keywordString = keywordsBuilder.toString()
                if (!TextUtils.isEmpty(keywordString)) {
                    keywordString =
                        keywordString.substring(0, keywordString.length - separator.length)
                }
                HyBid.setKeywords(keywordString)
            }

            settings.hybidSettings!!.browserPriorities?.let {
                if (HyBid.getViewabilityManager() != null) {
                    HyBid.getViewabilityManager().isViewabilityMeasurementEnabled = true
                }
                if (HyBid.getBrowserManager() != null && !it.isEmpty()) {
                    for (packageName in it) {
                        HyBid.getBrowserManager().addBrowser(packageName)
                    }
                }
            }

            settings.hybidSettings!!.apiUrl?.let {
                if (!TextUtils.isEmpty(it)) {
                    setApiUrl(it)
                }
            }
        }

        if (settings.adCustomizationSettings != null) {
            settings.adCustomizationSettings!!.initialAudioState?.let {
                HyBid.setVideoAudioStatus(
                    getAudioStateFromSettings(it)
                )
            }
            settings.adCustomizationSettings!!.locationTracking?.let {
                HyBid.setLocationTrackingEnabled(it)
            }
            settings.adCustomizationSettings!!.locationUpdates?.let {
                HyBid.setLocationUpdatesEnabled(it)
            }
        }

        MobileAds.initialize(this) { initializationStatus: InitializationStatus? -> }

        manageAdCustomizationParams()
    }

    private fun manageAdCustomizationParams() {
        val prefs = AdCustomizationPrefs(applicationContext)
        if (!prefs.isInitialised()) {
            val adCustomizationsManager = AdCustomizationsManager(
                false,
                1,
                false,
                true,
                false,
                false,
                false,
                true,
                false,
                false,
                false,
                CustomEndCardDisplay.FALLBACK.display,
                false,
                false,
                false,
                Constants.SKIP_OFFSET_DEFAULT.toString(),
                false,
                Constants.VIDEO_SKIP_OFFSET_DEFAULT.toString(),
                false,
                "5",
                false,
                "15",
                false,
                Constants.ENDCARD_CLOSE_BUTTON_DELAY_DEFAULT.toString(),
                false,
                true,
                false,
                Constants.CONTENT_INFO_URL,
                false,
                Constants.CONTENT_INFO_ICON_URL,
                false,
                ContentInfoIconAction.EXPAND.action.toString(),
                false,
                ContentInfoDisplay.SYSTEM_BROWSER.display.toString(),
                false,
                Constants.MRAID_CUSTOM_CLOSE_BACK_BUTTON_DELAY_DEFAULT.toString(),
                false,
                Constants.MRAID_CUSTOM_CLOSE_CLOSE_BUTTON_DELAY_DEFAULT.toString(),
                false,
                CountdownStyle.PIE_CHART.name,
                false,
                ImpressionTrackingMethod.AD_VIEWABLE.methodName,
                false,
                "0",
                false,
                "0.0",
                false,
                false,
                false,
                "2"
            )
            prefs.setAdCustomizationData(adCustomizationsManager.toJson())
        }
    }


    private fun fetchSettings(): Settings {
        var model: Settings? = null
        val manager = SettingsManager.getInstance(this)
        if (manager.isInitialised()) {
            model = manager.getSettings()
        } else {

            val hybidSettings = HyBidSettings.Builder().appToken(Constants.APP_TOKEN)
                .zoneIds(Constants.ZONE_ID_LIST).apiUrl(BuildConfig.BASE_URL).age("")
                .keywords(ArrayList()).browserPriorities(ArrayList()).coppa(Constants.COPPA_DEFAULT)
                .testMode(Constants.TEST_MODE_DEFAULT).gender("")
                .topicsApi(Constants.TOPICS_API_DEFAULT).build()

            val adCustomizationSettings = AdCustomizationSettings.Builder()
                .initialAudioState(Constants.INITIAL_AUDIO_STATE_DEFAULT)
                .locationTracking(Constants.LOCATION_TRACKING_DEFAULT)
                .locationUpdates(Constants.LOCATION_UPDATES_DEFAULT)
                .locationTrackingEnabled(true)
                .locationUpdatesEnabled(true)
                .build()

            val dfpSettings =
                DFPSettings.Builder()
                    .mediationBannerAdUnitId(Constants.DFP_MEDIATION_BANNER_AD_UNIT)
                    .mediationMediumAdUnitId(Constants.DFP_MEDIATION_MEDIUM_AD_UNIT)
                    .mediationLeaderboardAdUnitId(Constants.DFP_MEDIATION_LEADERBOARD_AD_UNIT)
                    .mediationInterstitialAdUnitId(Constants.DFP_MEDIATION_INTERSTITIAL_AD_UNIT)
                    .mediationRewardedAdUnitId(Constants.DFP_MEDIATION_REWARDED_AD_UNIT).build()

            val fairbidSettings = FairbidSettings.Builder().appId(Constants.FAIRBID_APP_ID)
                .mediationBannerAdUnitId(Constants.FAIRBID_MEDIATION_BANNER_AD_UNIT)
                .mediationInterstitialAdUnitId(Constants.FAIRBID_MEDIATION_INTERSTITIAL_AD_UNIT)
                .mediationRewardedAdUnitId(Constants.FAIRBID_MEDIATION_REWARDED_AD_UNIT).build()

            val admobSettings = AdmobSettings.Builder().appId(Constants.ADMOB_APP_ID)
                .bannerAdUnitId(Constants.ADMOB_BANNER_AD_UNIT)
                .interstitialAdUnitId(Constants.ADMOB_INTERSTITIAL_AD_UNIT)
                .nativeAdUnitId(Constants.ADMOB_NATIVE_AD_UNIT)
                .interstitialVideoAdUnitId(Constants.ADMOB_INTERSTITIAL_VIDEO_AD_UNIT)
                .mediumAdUnitId(Constants.ADMOB_MEDIUM_AD_UNIT)
                .mediumVideoAdUnitId(Constants.ADMOB_MEDIUM_VIDEO_AD_UNIT)
                .leaderboardAdUnitId(Constants.ADMOB_LEADERBOARD_AD_UNIT)
                .rewardedAdUnitId(Constants.ADMOB_REWARDED_AD_UNIT).build()

            val ironSourceSettings =
                IronSourceSettings.Builder().appKey(Constants.IRONSOURCE_APP_KEY)
                    .bannerAdUnitId(Constants.IRONSOURCE_BANNER_AD_UNIT)
                    .interstitialAdUnitId(Constants.IRONSOURCE_INTERSTITIAL_AD_UNIT)
                    .rewardedAdUnitId(Constants.IRONSOURCE_REWARDED_AD_UNIT).build()

            val maxAdsSettings = MaxAdsSettings.Builder().sdkKey(Constants.MAXADS_SDK_KEY)
                .bannerAdUnitId(Constants.MAXADS_BANNER_AD_UNIT)
                .interstitialAdUnitId(Constants.MAXADS_INTERSTITIAL_AD_UNIT)
                .mRectAdUnitId(Constants.MAXADS_MRECT_AD_UNIT)
                .rewardedAdUnitId(Constants.MAXADS_REWARDED_AD_UNIT)
                .nativeAdUnitId(Constants.MAXADS_NATIVE_AD_UNIT).build()

            val chartboostSettings =
                ChartboostSettings.Builder().heliumAppId(Constants.CHARTBOOST_APP_ID)
                    .heliumAppSignature(Constants.CHARTBOOST_APP_SIGNATURE)
                    .mediationBannerAdUnitId(Constants.CHARTBOOST_MEDIATION_BANNER_AD_UNIT)
                    .mediationMrectAdUnitId(Constants.CHARTBOOST_MEDIATION_MRECT_AD_UNIT)
                    .mediationMrectVideoAdUnitId(Constants.CHARTBOOST_MEDIATION_MRECT_VIDEO_AD_UNIT)
                    .mediationLeaderboardAdUnitId(Constants.CHARTBOOST_MEDIATION_LEADERBOARD_AD_UNIT)
                    .mediationInterstitialAdUnitId(Constants.CHARTBOOST_MEDIATION_INTERSTITIAL_AD_UNIT)
                    .mediationInterstitialVideoAdUnitId(Constants.CHARTBOOST_MEDIATION_INTERSTITIAL_VIDEO_AD_UNIT)
                    .mediationRewardedAdUnitId(Constants.CHARTBOOST_MEDIATION_REWARDED_AD_UNIT)
                    .mediationRewardedHtmlAdUnitId(Constants.CHARTBOOST_MEDIATION_REWARDED_HTML_AD_UNIT)
                    .build()

            model = Settings()

            model.hybidSettings = hybidSettings
            model.adCustomizationSettings = adCustomizationSettings
            model.dfpSettings = dfpSettings
            model.fairbidSettings = fairbidSettings
            model.admobSettings = admobSettings
            model.ironSourceSettings = ironSourceSettings
            model.maxAdsSettings = maxAdsSettings
            model.chartboostSettings = chartboostSettings

            manager.setSettings(model, true)

            manager.setInitialised(true)
        }

        return model
    }

    private fun getAudioStateFromSettings(settingsAudioState: Int): AudioState {
        return when (settingsAudioState) {
            1 -> AudioState.ON
            2 -> AudioState.MUTED
            else -> AudioState.ON
        }
    }

    // todo Initiate chartboost once we have the required IDs
    /*private fun initChartboost() {
        HeliumSdk.start(
            this@HyBidDemoApplication,
            Constants.CHARTBOOST_APP_ID,
            Constants.CHARTBOOST_APP_SIGNATURE,
            HeliumInitializationOptions(),
            object : HeliumSdkListener() {
                fun didInitialize(error: Error?) {
                    if (error != null) {
                        Log.d(
                            "initChartboost",
                            "Chartboost Mediation SDK failed to initialize. Reason: " + error.message
                        )
                    } else {
                        //SDK Started,
                        Log.d("initChartboost", "Chartboost Mediation SDK initialized successfully")
                    }
                }
            }
        )
    }*/

}