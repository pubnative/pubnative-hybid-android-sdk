// HyBid SDK License
//
// https://github.com/pubnative/pubnative-hybid-android-sdk/blob/main/LICENSE
//
package net.pubnative.lite.demo.models

class DFPSettings private constructor(builder: Builder) {

    val mediationBannerAdUnitId: String?
    val mediationMediumAdUnitId: String?
    val mediationLeaderboardAdUnitId: String?
    val mediationInterstitialAdUnitId: String?
    val mediationRewardedAdUnitId: String?

    init {
        this.mediationBannerAdUnitId = builder.mediationBannerAdUnitId
        this.mediationMediumAdUnitId = builder.mediationMediumAdUnitId
        this.mediationLeaderboardAdUnitId = builder.mediationLeaderboardAdUnitId
        this.mediationInterstitialAdUnitId = builder.mediationInterstitialAdUnitId
        this.mediationRewardedAdUnitId = builder.mediationRewardedAdUnitId
    }

    class Builder {
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