// HyBid SDK License
//
// https://github.com/pubnative/pubnative-hybid-android-sdk/blob/main/LICENSE
//
package net.pubnative.lite.demo

import android.content.Context
import android.text.TextUtils
import android.util.Log
import android.webkit.WebView
import androidx.multidex.MultiDex
import androidx.multidex.MultiDexApplication
import com.chartboost.chartboostmediationsdk.ChartboostMediationPreinitializationConfiguration
import com.chartboost.chartboostmediationsdk.ChartboostMediationSdk
import com.chartboost.core.ChartboostCore
import com.chartboost.core.initialization.ModuleInitializationResult
import com.chartboost.core.initialization.ModuleObserver
import com.chartboost.core.initialization.SdkConfiguration
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.initialization.InitializationStatus
import net.pubnative.lite.demo.managers.AdCustomizationPrefs
import net.pubnative.lite.demo.managers.AdCustomizationsManager
import net.pubnative.lite.demo.managers.AnalyticsSubscriber.eventCallback
import net.pubnative.lite.demo.managers.AudioSettings
import net.pubnative.lite.demo.managers.AutoCloseSettings
import net.pubnative.lite.demo.managers.ClickBehaviourSettings
import net.pubnative.lite.demo.managers.CloseButtonSettings
import net.pubnative.lite.demo.managers.ContentInfoSettings
import net.pubnative.lite.demo.managers.CountdownSettings
import net.pubnative.lite.demo.managers.CustomCtaSettings
import net.pubnative.lite.demo.managers.EndCardSettings
import net.pubnative.lite.demo.managers.ImpressionTrackingSettings
import net.pubnative.lite.demo.managers.LandingPageSettings
import net.pubnative.lite.demo.managers.MraidSettings
import net.pubnative.lite.demo.managers.NavigationSettings
import net.pubnative.lite.demo.managers.ReducedButtonsSettings
import net.pubnative.lite.demo.managers.SettingsManager
import net.pubnative.lite.demo.managers.SkipOffsetSettings
import net.pubnative.lite.demo.managers.VisibilitySettings
import net.pubnative.lite.demo.models.*
import net.pubnative.lite.sdk.CountdownStyle
import net.pubnative.lite.sdk.HyBid
import net.pubnative.lite.sdk.api.ApiManager.setApiUrl
import net.pubnative.lite.sdk.models.ContentInfoDisplay
import net.pubnative.lite.sdk.models.ContentInfoIconAction
import net.pubnative.lite.sdk.models.CustomEndCardDisplay
import net.pubnative.lite.sdk.models.ImpressionTrackingMethod
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
            settings.hybidSettings!!.topicsApi?.let { HyBid.setTopicsApiEnabled(it) }
            settings.hybidSettings!!.reportingEnabled?.let { HyBid.setReportingEnabled(it) }

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

        initChartboost()
        MobileAds.initialize(this) { initializationStatus: InitializationStatus? -> }

        manageAdCustomizationParams()
    }

    private fun manageAdCustomizationParams() {
        val prefs = AdCustomizationPrefs(applicationContext)
        if (!prefs.isInitialised()) {
            val adCustomizationsManager = AdCustomizationsManager(
                audioSettings = AudioSettings(
                    enabled = false,
                    value = 1
                ),
                mraidSettings = MraidSettings(
                    expandEnabled = false,
                    expandValue = true
                ),
                autoCloseSettings = AutoCloseSettings(
                    interstitialEnabled = false,
                    interstitialValue = false,
                    rewardedEnabled = false,
                    rewardedValue = true
                ),
                endCardSettings = EndCardSettings(
                    enabled = false,
                    value = true,
                    customEnabled = false,
                    customValue = false,
                    customDisplayEnabled = false,
                    customDisplayValue = CustomEndCardDisplay.FALLBACK.display
                ),
                navigationSettings = NavigationSettings(
                    enabled = false,
                    value = "external"
                ),
                landingPageSettings = LandingPageSettings(
                    enabled = false,
                    value = false
                ),
                skipOffsetSettings = SkipOffsetSettings(
                    html = false to Constants.SKIP_OFFSET_DEFAULT.toString(),
                    video = false to Constants.VIDEO_SKIP_OFFSET_DEFAULT.toString(),
                    playable = false to "5",
                    rewardedHtml = false to "5",
                    rewardedVideo = false to "15",
                    endCardCloseDelay = false to Constants.ENDCARD_CLOSE_BUTTON_DELAY_DEFAULT.toString()
                ),
                clickBehaviourSettings = ClickBehaviourSettings(
                    enabled = false,
                    value = true
                ),
                contentInfoSettings = ContentInfoSettings(
                    urlEnabled = false,
                    urlValue = Constants.CONTENT_INFO_URL,
                    iconUrlEnabled = false,
                    iconUrlValue = Constants.CONTENT_INFO_ICON_URL,
                    iconClickActionEnabled = false,
                    iconClickActionValue = ContentInfoIconAction.EXPAND.action.toString(),
                    displayEnabled = false,
                    displayValue = ContentInfoDisplay.SYSTEM_BROWSER.display.toString()
                ),
                closeButtonSettings = CloseButtonSettings(
                    enabled = false,
                    value = Constants.MRAID_CUSTOM_CLOSE_CLOSE_BUTTON_DELAY_DEFAULT.toString()
                ),
                countdownSettings = CountdownSettings(
                    enabled = false,
                    value = CountdownStyle.PIE_CHART.name
                ),
//                learnMoreSettings = LearnMoreSettings(
//                    sizeEnabled = false,
//                    sizeValue = LearnMoreSize.DEFAULT.name,
//                    locationEnabled = false,
//                    locationValue = LearnMoreLocation.DEFAULT.name
//                ),
                impressionTrackingSettings = ImpressionTrackingSettings(
                    enabled = false,
                    value = ImpressionTrackingMethod.AD_VIEWABLE.methodName
                ),
                visibilitySettings = VisibilitySettings(
                    minTimeEnabled = false,
                    minTimeValue = "0",
                    minPercentEnabled = false,
                    minPercentValue = "0.0"
                ),
                customCtaSettings = CustomCtaSettings(
                    enabled = false,
                    enabledValue = false,
                    delayEnabled = false,
                    delayEnabledValue = "2",
                    typeValue = 0
                ),
                reducedButtonsSettings = ReducedButtonsSettings(
                    enabled = false,
                    value = false
                )
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
                .topicsApi(Constants.TOPICS_API_DEFAULT)
                .reportingEnabled(Constants.REPORTING_ENABLED_DEFAULT)
                .build()

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
                .mRectAdUnitId(Constants.MAXADS_MRECT_AD_UNIT)
                .mRectVideoAdUnitId(Constants.MAXADS_MRECT_VIDEO_AD_UNIT)
                .interstitialAdUnitId(Constants.MAXADS_INTERSTITIAL_HTML_AD_UNIT)
                .interstitialVideoAdUnitId(Constants.MAXADS_INTERSTITIAL_VIDEO_AD_UNIT)
                .rewardedAdUnitId(Constants.MAXADS_REWARDED_HTML_AD_UNIT)
                .rewardedVideoAdUnitId(Constants.MAXADS_REWARDED_VIDEO_AD_UNIT)
                .nativeAdUnitId(Constants.MAXADS_NATIVE_AD_UNIT).build()

            val chartboostSettings =
                ChartboostSettings.Builder().heliumAppId(Constants.CHARTBOOST_APP_ID)
                    .heliumAppSignature(Constants.CHARTBOOST_APP_SIGNATURE)
                    .mediationBannerAdUnitId(Constants.CHARTBOOST_MEDIATION_BANNER_AD_UNIT)
                    .mediationInterstitialAdUnitId(Constants.CHARTBOOST_MEDIATION_INTERSTITIAL_AD_UNIT)
                    .mediationInterstitialVideoAdUnitId(Constants.CHARTBOOST_MEDIATION_INTERSTITIAL_VIDEO_AD_UNIT)
                    .mediationRewardedVideoAdUnitId(Constants.CHARTBOOST_MEDIATION_REWARDED_VIDEO_AD_UNIT)
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
    private fun initChartboost() {
        val skippedPartnerIds = mutableSetOf<String>()
        skippedPartnerIds.add("admob")
        skippedPartnerIds.add("amazon_aps")
        skippedPartnerIds.add("applovin")
        skippedPartnerIds.add("bidmachine")
        skippedPartnerIds.add("facebook")
        skippedPartnerIds.add("fyber")
        skippedPartnerIds.add("google_googlebidding")
        skippedPartnerIds.add("inmobi")
        skippedPartnerIds.add("ironsource")
        skippedPartnerIds.add("mintegral")
        skippedPartnerIds.add("pangle")
        skippedPartnerIds.add("unity")
        skippedPartnerIds.add("vungle")
        skippedPartnerIds.add("mobilefuse")
        skippedPartnerIds.add("hyprmx")

        ChartboostMediationSdk.setPreinitializationConfiguration(
            ChartboostMediationPreinitializationConfiguration(skippedPartnerIds)
        )

        val listener = object : ModuleObserver {
            override fun onModuleInitializationCompleted(result: ModuleInitializationResult) {
                // Use this to action off of a specific module initialization
                val moduleId = result.moduleId
                if (result.exception != null) {
                    Log.d(
                        "initChartboost",
                        "Chartboost Mediation SDK failed to initialize. Reason: " + result.exception?.message
                    )
                } else {
                    //SDK Started
                    Log.d("initChartboost", "Chartboost Mediation SDK initialized successfully")
                    ChartboostMediationSdk.setTestMode(this@HyBidDemoApplication, true)
                }
            }
        }

        ChartboostCore.initializeSdkFromJava(
            this,
            SdkConfiguration(Constants.CHARTBOOST_APP_ID, listOf()),
            listener
        )
    }
}