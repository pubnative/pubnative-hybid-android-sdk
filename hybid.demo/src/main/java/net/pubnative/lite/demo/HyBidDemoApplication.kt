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
import android.os.Build
import android.text.TextUtils
import android.util.Log
import android.webkit.WebView
import androidx.multidex.MultiDex
import androidx.multidex.MultiDexApplication
import com.applovin.sdk.AppLovinSdk
import com.applovin.sdk.AppLovinSdkConfiguration
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.initialization.InitializationStatus
import com.ogury.sdk.Ogury
import com.ogury.sdk.OguryConfiguration
import net.pubnative.lite.demo.managers.AnalyticsSubscriber.eventCallback
import net.pubnative.lite.demo.managers.SettingsManager
import net.pubnative.lite.demo.models.*
import net.pubnative.lite.sdk.CountdownStyle
import net.pubnative.lite.sdk.HyBid
import net.pubnative.lite.sdk.InterstitialActionBehaviour
import net.pubnative.lite.sdk.api.ApiManager.setApiUrl
import net.pubnative.lite.sdk.utils.Logger
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
        }
    }

    override fun attachBaseContext(base: Context) {
        super.attachBaseContext(base)
        MultiDex.install(this)
    }

    private fun initSettings() {
        WebView.setWebContentsDebuggingEnabled(true)

        val settings = fetchSettings()
        if (settings != null) {
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
                settings.adCustomizationSettings!!.mraidExpanded?.let {
                    HyBid.setMraidExpandEnabled(it)
                }
                settings.adCustomizationSettings!!.closeVideoAfterFinish?.let {
                    HyBid.setCloseVideoAfterFinish(it)
                }
                settings.adCustomizationSettings!!.skipOffset?.let {
                    HyBid.setHtmlInterstitialSkipOffset(it)
                }
                settings.adCustomizationSettings!!.videoSkipOffset?.let {
                    HyBid.setVideoInterstitialSkipOffset(it)
                }
                settings.adCustomizationSettings!!.enableEndcard?.let {
                    HyBid.setEndCardEnabled(it)
                }
                settings.adCustomizationSettings!!.endCardCloseButtonDelay?.let {
                    HyBid.setEndCardCloseButtonDelay(it)
                }
                settings.adCustomizationSettings!!.feedbackEnabled?.let {
                    HyBid.setAdFeedbackEnabled(it)
                }
                settings.adCustomizationSettings!!.feedbackFormUrl?.let {
                    HyBid.setContentInfoUrl(it)
                }
                settings.adCustomizationSettings!!.videoClickBehaviour?.let {
                    HyBid.setInterstitialClickBehaviour(
                        getInterstitialActionBehaviourFromSettings(
                            it
                        )
                    )
                }
                settings.adCustomizationSettings!!.countdownStyle?.let {
                    HyBid.setCountdownStyle(CountdownStyle.from(it))
                }
            }

            MobileAds.initialize(this) { initializationStatus: InitializationStatus? -> }
            AppLovinSdk.getInstance(this).mediationProvider = "max"
            AppLovinSdk.initializeSdk(this) { config: AppLovinSdkConfiguration? -> }
            val oguryConfigBuilder = OguryConfiguration.Builder(this, Constants.OGURY_KEY)
            Ogury.start(oguryConfigBuilder.build())

            //todo : if we want to see customized skip and close button icons ,we have to decomment lines below to see one or both of them
//            HyBid.setCloseXmlResource(R.mipmap.close_sample, R.mipmap.close_sample)
//            HyBid.setSkipXmlResource(R.mipmap.skip_sample)

//        // NumberEight SDK crashes below API level 26.
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            val apiToken: APIToken = NumberEight.start(
//                Constants.NUMBEREIGHT_API_TOKEN, this, ConsentOptions.withDefault()
//            )
//            startRecording(apiToken)
//        }
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
                .testMode(Constants.TEST_MODE_DEFAULT).gender("").build()

            val adCustomizationSettings = AdCustomizationSettings.Builder()
                .initialAudioState(Constants.INITIAL_AUDIO_STATE_DEFAULT)
                .closeVideoAfterFinish(Constants.CLOSE_VIDEO_AFTER_FINISH_DEFAULT)
                .closeVideoAfterFinishForRewardedVideo(Constants.CLOSE_VIDEO_AFTER_FINISH_DEFAULT_FOR_REWARDED)
                .enableEndcard(Constants.ENABLE_ENDCARD_DEFAULT)
                .feedbackEnabled(Constants.FEEDBACK_ENABLED)
                .feedbackFormUrl(Constants.FEEDBACK_FORM_URL)
                .mraidExpanded(Constants.MRAID_EXPANDED_DEFAULT)
                .locationTracking(Constants.LOCATION_TRACKING_DEFAULT)
                .locationUpdates(Constants.LOCATION_UPDATES_DEFAULT)
                .videoClickBehaviour(Constants.VIDEO_CLICK_BEHAVIOUR_DEFAULT)
                .skipOffset(Constants.SKIP_OFFSET_DEFAULT)
                .videoSkipOffset(Constants.VIDEO_SKIP_OFFSET_DEFAULT)
                .endCardCloseButtonDelay(Constants.ENDCARD_CLOSE_BUTTON_DELAY_DEFAULT)
                .countdownStyle(Constants.COUNTDOWN_STYLE_DEFAULT).build()


            val dfpSettings =
                DFPSettings.Builder().mediumAdUnitId(Constants.DFP_MRAID_MEDIUM_AD_UNIT)
                    .interstitialAdUnitId(Constants.DFP_MRAID_INTERSTITIAL_AD_UNIT)
                    .leaderboardAdUnitId(Constants.DFP_MRAID_LEADERBOARD_AD_UNIT)
                    .mediationBannerAdUnitId(Constants.DFP_MEDIATION_BANNER_AD_UNIT)
                    .mediationMediumAdUnitId(Constants.DFP_MEDIATION_MEDIUM_AD_UNIT)
                    .mediationLeaderboardAdUnitId(Constants.DFP_MEDIATION_LEADERBOARD_AD_UNIT)
                    .mediationInterstitialAdUnitId(Constants.DFP_MEDIATION_INTERSTITIAL_AD_UNIT)
                    .mediationRewardedAdUnitId(Constants.DFP_MEDIATION_REWARDED_AD_UNIT).build()

            val fairbidSettings = FairbidSettings.Builder().appId(Constants.FAIRBID_APP_ID)
                .bannerAdUnitId(Constants.FAIRBID_BANNER_AD_UNIT)
                .interstitialAdUnitId(Constants.FAIRBID_INTERSTITIAL_AD_UNIT)
                .mediationBannerAdUnitId(Constants.FAIRBID_MEDIATION_BANNER_AD_UNIT)
                .mediationInterstitialAdUnitId(Constants.FAIRBID_MEDIATION_INTERSTITIAL_AD_UNIT)
                .rewardedAdUnitId(Constants.FAIRBID_REWARDED_AD_UNIT)
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

            model = Settings()

            model.hybidSettings = hybidSettings
            model.adCustomizationSettings = adCustomizationSettings
            model.dfpSettings = dfpSettings
            model.fairbidSettings = fairbidSettings
            model.admobSettings = admobSettings
            model.ironSourceSettings = ironSourceSettings
            model.maxAdsSettings = maxAdsSettings

            manager.setSettings(model, true)
        }

        return model
    }

    private fun getAudioStateFromSettings(settingsAudioState: Int): AudioState {
        return when (settingsAudioState) {
            1 -> AudioState.ON
            2 -> AudioState.MUTED
            else -> AudioState.DEFAULT
        }
    }

    private fun getInterstitialActionBehaviourFromSettings(settingsActionBehaviour: Boolean): InterstitialActionBehaviour {
        return if (settingsActionBehaviour) {
            InterstitialActionBehaviour.HB_CREATIVE
        } else {
            InterstitialActionBehaviour.HB_ACTION_BUTTON
        }
    }
}