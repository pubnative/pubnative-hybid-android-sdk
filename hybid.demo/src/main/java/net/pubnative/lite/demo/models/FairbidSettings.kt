package net.pubnative.lite.demo.models

class FairbidSettings private constructor(builder: Builder) {

    val appId: String?
    val mediationBannerAdUnitId: String?
    val mediationInterstitialAdUnitId: String?
    val mediationRewardedAdUnitId: String?
    val bannerAdUnitId: String?
    val interstitialAdUnitId: String?
    val rewardedAdUnitId: String?

    init {
        this.appId = builder.appId
        this.mediationBannerAdUnitId = builder.mediationBannerAdUnitId
        this.mediationInterstitialAdUnitId = builder.mediationInterstitialAdUnitId
        this.mediationRewardedAdUnitId = builder.mediationRewardedAdUnitId
        this.bannerAdUnitId = builder.bannerAdUnitId
        this.interstitialAdUnitId = builder.interstitialAdUnitId
        this.rewardedAdUnitId = builder.rewardedAdUnitId
    }

    class Builder {
        var appId: String? = null
            private set
        var mediationBannerAdUnitId: String? = null
            private set
        var mediationInterstitialAdUnitId: String? = null
            private set
        var mediationRewardedAdUnitId: String? = null
            private set
        var bannerAdUnitId: String? = null
            private set
        var interstitialAdUnitId: String? = null
            private set
        var rewardedAdUnitId: String? = null
            private set

        fun appId(fairbidAppId: String) = apply { this.appId = fairbidAppId }

        fun mediationBannerAdUnitId(fairbidMediationBannerAdUnitId: String) =
            apply { this.mediationBannerAdUnitId = fairbidMediationBannerAdUnitId }

        fun mediationInterstitialAdUnitId(fairbidMediationInterstitialAdUnitId: String) =
            apply {
                this.mediationInterstitialAdUnitId = fairbidMediationInterstitialAdUnitId
            }

        fun mediationRewardedAdUnitId(fairbidMediationRewardedAdUnitId: String) =
            apply { this.mediationRewardedAdUnitId = fairbidMediationRewardedAdUnitId }

        fun bannerAdUnitId(fairbidBannerAdUnitId: String) =
            apply { this.bannerAdUnitId = fairbidBannerAdUnitId }

        fun interstitialAdUnitId(fairbidInterstitialAdUnitId: String) =
            apply { this.interstitialAdUnitId = fairbidInterstitialAdUnitId }

        fun rewardedAdUnitId(fairbidRewardedAdUnitId: String) =
            apply { this.rewardedAdUnitId = fairbidRewardedAdUnitId }

        fun build() = FairbidSettings(this)
    }
}