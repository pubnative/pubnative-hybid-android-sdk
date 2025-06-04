// HyBid SDK License
//
// https://github.com/pubnative/pubnative-hybid-android-sdk/blob/main/LICENSE
//
package net.pubnative.lite.demo.models

class ChartboostSettings private constructor(builder: Builder) {
    val heliumAppId: String?
    val heliumAppSignature: String?
    val mediationBannerAdUnitId: String?
    val mediationInterstitialAdUnitId: String?
    val mediationInterstitialVideoAdUnitId: String?
    val mediationRewardedVideoAdUnitId: String?
    val mediationRewardedHtmlAdUnitId: String?

    init {
        this.heliumAppId = builder.heliumAppId
        this.heliumAppSignature = builder.heliumAppSignature
        this.mediationBannerAdUnitId = builder.mediationBannerAdUnitId
        this.mediationInterstitialAdUnitId = builder.mediationInterstitialAdUnitId
        this.mediationInterstitialVideoAdUnitId = builder.mediationInterstitialVideoAdUnitId
        this.mediationRewardedVideoAdUnitId = builder.mediationRewardedVideoAdUnitId
        this.mediationRewardedHtmlAdUnitId = builder.mediationRewardedHtmlAdUnitId
    }

    class Builder {
        var heliumAppId: String? = null
            private set
        var heliumAppSignature: String? = null
            private set
        var mediationBannerAdUnitId: String? = null
            private set
        var mediationInterstitialAdUnitId: String? = null
            private set
        var mediationInterstitialVideoAdUnitId: String? = null
            private set
        var mediationRewardedVideoAdUnitId: String? = null
            private set
        var mediationRewardedHtmlAdUnitId: String? = null
            private set


        fun heliumAppId(heliumAppId: String) = apply { this.heliumAppId = heliumAppId }

        fun heliumAppSignature(heliumAppSignature: String) = apply { this.heliumAppSignature = heliumAppSignature }

        fun mediationBannerAdUnitId(chartboostMediationBannerAdUnitId: String) =
            apply { this.mediationBannerAdUnitId = chartboostMediationBannerAdUnitId }

        fun mediationInterstitialAdUnitId(chartboostMediationInterstitialAdUnitId: String) =
            apply {
                this.mediationInterstitialAdUnitId = chartboostMediationInterstitialAdUnitId }

        fun mediationInterstitialVideoAdUnitId(chartboostMediationInterstitialVideoAdUnitId: String) =
            apply {
                this.mediationInterstitialVideoAdUnitId = chartboostMediationInterstitialVideoAdUnitId }

        fun mediationRewardedVideoAdUnitId(chartboostMediationRewardedVideoAdUnitId: String) =
            apply { this.mediationRewardedVideoAdUnitId = chartboostMediationRewardedVideoAdUnitId }

        fun mediationRewardedHtmlAdUnitId(chartboostMediationRewardedHtmlAdUnitId: String) =
            apply { this.mediationRewardedHtmlAdUnitId = chartboostMediationRewardedHtmlAdUnitId }

        fun build() = ChartboostSettings(this)
    }
}