package net.pubnative.lite.demo.models

class DFPSettings private constructor(builder: Builder) {

    val bannerAdUnitId: String?
    val mediumAdUnitId: String?
    val leaderboardAdUnitId: String?
    val interstitialAdUnitId: String?
    val mediationBannerAdUnitId: String?
    val mediationMediumAdUnitId: String?
    val mediationLeaderboardAdUnitId: String?
    val mediationInterstitialAdUnitId: String?
    val mediationRewardedAdUnitId: String?

    init {
        this.bannerAdUnitId = builder.bannerAdUnitId
        this.mediumAdUnitId = builder.mediumAdUnitId
        this.leaderboardAdUnitId = builder.leaderboardAdUnitId
        this.interstitialAdUnitId = builder.interstitialAdUnitId
        this.mediationBannerAdUnitId = builder.mediationBannerAdUnitId
        this.mediationMediumAdUnitId = builder.mediationMediumAdUnitId
        this.mediationLeaderboardAdUnitId = builder.mediationLeaderboardAdUnitId
        this.mediationInterstitialAdUnitId = builder.mediationInterstitialAdUnitId
        this.mediationRewardedAdUnitId = builder.mediationRewardedAdUnitId
    }

    class Builder {
        var bannerAdUnitId: String? = null
            private set
        var mediumAdUnitId: String? = null
            private set
        var leaderboardAdUnitId: String? = null
            private set
        var interstitialAdUnitId: String? = null
            private set
        var mediationBannerAdUnitId: String? = null
            private set
        var mediationMediumAdUnitId: String? = null
            private set
        var mediationLeaderboardAdUnitId: String? = null
            private set
        var mediationInterstitialAdUnitId: String? = null
            private set
        var mediationRewardedAdUnitId: String? = null
            private set

        fun bannerAdUnitId(dfpBannerAdUnitId: String) =
            apply { this.bannerAdUnitId = dfpBannerAdUnitId }

        fun mediumAdUnitId(dfpMediumAdUnitId: String) =
            apply { this.mediumAdUnitId = dfpMediumAdUnitId }

        fun leaderboardAdUnitId(dfpLeaderboardAdUnitId: String) =
            apply { this.leaderboardAdUnitId = dfpLeaderboardAdUnitId }

        fun interstitialAdUnitId(dfpInterstitialAdUnitId: String) =
            apply { this.interstitialAdUnitId = dfpInterstitialAdUnitId }

        fun mediationBannerAdUnitId(dfpMediationBannerAdUnitId: String) =
            apply { this.mediationBannerAdUnitId = dfpMediationBannerAdUnitId }

        fun mediationMediumAdUnitId(dfpMediationMediumAdUnitId: String) =
            apply { this.mediationMediumAdUnitId = dfpMediationMediumAdUnitId }

        fun mediationLeaderboardAdUnitId(dfpMediationLeaderboardAdUnitId: String) =
            apply { this.mediationLeaderboardAdUnitId = dfpMediationLeaderboardAdUnitId }

        fun mediationInterstitialAdUnitId(dfpMediationInterstitialAdUnitId: String) =
            apply { this.mediationInterstitialAdUnitId = dfpMediationInterstitialAdUnitId }

        fun mediationRewardedAdUnitId(dfpMediationRewardedAdUnitId: String) =
            apply { this.mediationRewardedAdUnitId = dfpMediationRewardedAdUnitId }

        fun build() = DFPSettings(this)
    }
}