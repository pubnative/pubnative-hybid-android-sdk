package net.pubnative.lite.demo.models

class ChartboostSettings private constructor(builder: Builder) {
    val heliumAppId: String?
    val heliumAppSignature: String?
    val mediationBannerAdUnitId: String?
    val mediationMrectAdUnitId: String?
    val mediationMrectVideoAdUnitId: String?
    val mediationLeaderboardAdUnitId: String?
    val mediationInterstitialAdUnitId: String?
    val mediationInterstitialVideoAdUnitId: String?
    val mediationRewardedAdUnitId: String?
    val mediationRewardedHtmlAdUnitId: String?

    init {
        this.heliumAppId = builder.heliumAppId
        this.heliumAppSignature = builder.heliumAppSignature
        this.mediationBannerAdUnitId = builder.mediationBannerAdUnitId
        this.mediationMrectAdUnitId = builder.mediationMrectAdUnitId
        this.mediationMrectVideoAdUnitId = builder.mediationMrectVideoAdUnitId
        this.mediationLeaderboardAdUnitId = builder.mediationLeaderboardAdUnitId
        this.mediationInterstitialAdUnitId = builder.mediationInterstitialAdUnitId
        this.mediationInterstitialVideoAdUnitId = builder.mediationInterstitialVideoAdUnitId
        this.mediationRewardedAdUnitId = builder.mediationRewardedAdUnitId
        this.mediationRewardedHtmlAdUnitId = builder.mediationRewardedHtmlAdUnitId
    }

    class Builder {
        var heliumAppId: String? = null
            private set
        var heliumAppSignature: String? = null
            private set
        var mediationBannerAdUnitId: String? = null
            private set
        var mediationMrectAdUnitId: String? = null
            private set
        var mediationMrectVideoAdUnitId: String? = null
            private set
        var mediationLeaderboardAdUnitId: String? = null
            private set
        var mediationInterstitialAdUnitId: String? = null
            private set
        var mediationInterstitialVideoAdUnitId: String? = null
            private set
        var mediationRewardedAdUnitId: String? = null
            private set
        var mediationRewardedHtmlAdUnitId: String? = null
            private set


        fun heliumAppId(heliumAppId: String) = apply { this.heliumAppId = heliumAppId }

        fun heliumAppSignature(heliumAppSignature: String) = apply { this.heliumAppSignature = heliumAppSignature }

        fun mediationBannerAdUnitId(chartboostMediationBannerAdUnitId: String) =
            apply { this.mediationBannerAdUnitId = chartboostMediationBannerAdUnitId }

        fun mediationMrectAdUnitId(chartboostMediationMrectAdUnitId: String) =
            apply { this.mediationMrectAdUnitId = chartboostMediationMrectAdUnitId }

        fun mediationMrectVideoAdUnitId(chartboostMediationMrectVideoAdUnitId: String) =
            apply { this.mediationMrectVideoAdUnitId = chartboostMediationMrectVideoAdUnitId }

        fun mediationLeaderboardAdUnitId(chartboostMediationLeaderboardAdUnitId: String) =
            apply { this.mediationLeaderboardAdUnitId = chartboostMediationLeaderboardAdUnitId }

        fun mediationInterstitialAdUnitId(chartboostMediationInterstitialAdUnitId: String) =
            apply {
                this.mediationInterstitialAdUnitId = chartboostMediationInterstitialAdUnitId }

        fun mediationInterstitialVideoAdUnitId(chartboostMediationInterstitialVideoAdUnitId: String) =
            apply {
                this.mediationInterstitialVideoAdUnitId = chartboostMediationInterstitialVideoAdUnitId }

        fun mediationRewardedAdUnitId(chartboostMediationRewardedAdUnitId: String) =
            apply { this.mediationRewardedAdUnitId = chartboostMediationRewardedAdUnitId }

        fun mediationRewardedHtmlAdUnitId(chartboostMediationRewardedHtmlAdUnitId: String) =
            apply { this.mediationRewardedHtmlAdUnitId = chartboostMediationRewardedHtmlAdUnitId }

        fun build() = ChartboostSettings(this)
    }
}