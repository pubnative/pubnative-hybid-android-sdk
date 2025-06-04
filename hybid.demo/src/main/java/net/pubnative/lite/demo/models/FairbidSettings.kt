// HyBid SDK License
//
// https://github.com/pubnative/pubnative-hybid-android-sdk/blob/main/LICENSE
//
package net.pubnative.lite.demo.models

class FairbidSettings private constructor(builder: Builder) {

    val appId: String?
    val mediationBannerAdUnitId: String?
    val mediationInterstitialAdUnitId: String?
    val mediationRewardedAdUnitId: String?

    init {
        this.appId = builder.appId
        this.mediationBannerAdUnitId = builder.mediationBannerAdUnitId
        this.mediationInterstitialAdUnitId = builder.mediationInterstitialAdUnitId
        this.mediationRewardedAdUnitId = builder.mediationRewardedAdUnitId
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

        fun appId(fairbidAppId: String) = apply { this.appId = fairbidAppId }

        fun mediationBannerAdUnitId(fairbidMediationBannerAdUnitId: String) =
            apply { this.mediationBannerAdUnitId = fairbidMediationBannerAdUnitId }

        fun mediationInterstitialAdUnitId(fairbidMediationInterstitialAdUnitId: String) =
            apply {
                this.mediationInterstitialAdUnitId = fairbidMediationInterstitialAdUnitId
            }

        fun mediationRewardedAdUnitId(fairbidMediationRewardedAdUnitId: String) =
            apply { this.mediationRewardedAdUnitId = fairbidMediationRewardedAdUnitId }


        fun build() = FairbidSettings(this)
    }
}