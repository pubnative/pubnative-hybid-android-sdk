// HyBid SDK License
//
// https://github.com/pubnative/pubnative-hybid-android-sdk/blob/main/LICENSE
//
package net.pubnative.lite.demo.managers

import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

@Serializable
data class AdCustomizationsManager(
    val audioSettings: AudioSettings?,
    val mraidSettings: MraidSettings?,
    val autoCloseSettings: AutoCloseSettings?,
    val endCardSettings: EndCardSettings?,
    val navigationSettings: NavigationSettings?,
    val landingPageSettings: LandingPageSettings?,
    val skipOffsetSettings: SkipOffsetSettings?,
    val clickBehaviourSettings: ClickBehaviourSettings?,
    val contentInfoSettings: ContentInfoSettings?,
    val closeButtonSettings: CloseButtonSettings?,
    val countdownSettings: CountdownSettings?,
//    val learnMoreSettings: LearnMoreSettings?,
    val impressionTrackingSettings: ImpressionTrackingSettings?,
    val visibilitySettings: VisibilitySettings?,
    val customCtaSettings: CustomCtaSettings?,
    val reducedButtonsSettings: ReducedButtonsSettings?
) {
    fun toJson(): String = Json.encodeToString(this)

    companion object {
        fun fromJson(json: String?): AdCustomizationsManager? =
            json?.let { Json.decodeFromString<AdCustomizationsManager>(it) }
    }
}

@Serializable
data class AudioSettings(
    var enabled: Boolean,
    var value: Int
)

@Serializable
data class MraidSettings(
    var expandEnabled: Boolean,
    var expandValue: Boolean
)

@Serializable
data class AutoCloseSettings(
    var interstitialEnabled: Boolean,
    var interstitialValue: Boolean,
    var rewardedEnabled: Boolean,
    var rewardedValue: Boolean
)

@Serializable
data class EndCardSettings(
    var enabled: Boolean,
    var value: Boolean,
    var customEnabled: Boolean,
    var customValue: Boolean,
    var customDisplayEnabled: Boolean,
    var customDisplayValue: String
)

@Serializable
data class NavigationSettings(
    var enabled: Boolean,
    var value: String
)

@Serializable
data class LandingPageSettings(
    var enabled: Boolean,
    var value: Boolean
)

@Serializable
data class SkipOffsetSettings(
    var html: Pair<Boolean, String>?,
    var video: Pair<Boolean, String>?,
    var playable: Pair<Boolean, String>?,
    var rewardedHtml: Pair<Boolean, String>?,
    var rewardedVideo: Pair<Boolean, String>?,
    var endCardCloseDelay: Pair<Boolean, String>?
)

@Serializable
data class ClickBehaviourSettings(
    var enabled: Boolean,
    var value: Boolean
)

@Serializable
data class ContentInfoSettings(
    var urlEnabled: Boolean,
    var urlValue: String,
    var iconUrlEnabled: Boolean,
    var iconUrlValue: String,
    var iconClickActionEnabled: Boolean,
    var iconClickActionValue: String,
    var displayEnabled: Boolean,
    var displayValue: String
)

@Serializable
data class CloseButtonSettings(
    var enabled: Boolean,
    var value: String
)

@Serializable
data class CountdownSettings(
    var enabled: Boolean,
    var value: String
)

//@Serializable
//data class LearnMoreSettings(
//    var sizeEnabled: Boolean,
//    var sizeValue: String,
//    var locationEnabled: Boolean,
//    var locationValue: String
//)

@Serializable
data class ImpressionTrackingSettings(
    var enabled: Boolean,
    var value: String
)

@Serializable
data class VisibilitySettings(
    var minTimeEnabled: Boolean,
    var minTimeValue: String,
    var minPercentEnabled: Boolean,
    var minPercentValue: String
)

@Serializable
data class CustomCtaSettings(
    var enabled: Boolean,
    var enabledValue: Boolean,
    var delayEnabled: Boolean,
    var delayEnabledValue: String,
    var typeValue: Int
)

@Serializable
data class ReducedButtonsSettings(
    var enabled: Boolean,
    var value: Boolean
)