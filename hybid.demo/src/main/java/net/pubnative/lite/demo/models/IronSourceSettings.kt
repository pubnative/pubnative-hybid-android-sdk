package net.pubnative.lite.demo.models

class IronSourceSettings private constructor(builder: Builder) {

    val appKey: String?
    val bannerAdUnitId: String?
    val interstitialAdUnitId: String?
    val rewardedAdUnitId: String?

    init {
        this.appKey = builder.appKey
        this.bannerAdUnitId = builder.bannerAdUnitId
        this.interstitialAdUnitId = builder.interstitialAdUnitId
        this.rewardedAdUnitId = builder.rewardedAdUnitId
    }

    class Builder {
        var appKey: String? = null
            private set
        var bannerAdUnitId: String? = null
            private set
        var interstitialAdUnitId: String? = null
            private set
        var rewardedAdUnitId: String? = null
            private set

        fun appKey(ironSourceAppKey: String) =
            apply { this.appKey = ironSourceAppKey }

        fun bannerAdUnitId(ironSourceBannerAdUnitId: String) =
            apply { this.bannerAdUnitId = ironSourceBannerAdUnitId }

        fun interstitialAdUnitId(ironSourceInterstitialAdUnitId: String) =
            apply { this.interstitialAdUnitId = ironSourceInterstitialAdUnitId }

        fun rewardedAdUnitId(ironSourceRewardedAdUnitId: String) =
            apply { this.rewardedAdUnitId = ironSourceRewardedAdUnitId }

        fun build() = IronSourceSettings(this)
    }
}