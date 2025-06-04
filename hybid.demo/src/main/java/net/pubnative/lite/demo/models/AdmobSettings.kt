// HyBid SDK License
//
// https://github.com/pubnative/pubnative-hybid-android-sdk/blob/main/LICENSE
//
package net.pubnative.lite.demo.models

class AdmobSettings private constructor(builder: Builder) {

    val appId: String?
    val bannerAdUnitId: String?
    val mediumAdUnitId: String?
    val mediumVideoAdUnitId: String?
    val leaderboardAdUnitId: String?
    val rewardedAdUnitId: String?
    val interstitialAdUnitId: String?
    val interstitialVideoAdUnitId: String?
    val nativeAdUnitId: String?

    init {
        this.appId = builder.appId
        this.bannerAdUnitId = builder.bannerAdUnitId
        this.mediumAdUnitId = builder.mediumAdUnitId
        this.mediumVideoAdUnitId = builder.mediumVideoAdUnitId
        this.leaderboardAdUnitId = builder.leaderboardAdUnitId
        this.rewardedAdUnitId = builder.rewardedAdUnitId
        this.interstitialAdUnitId = builder.interstitialAdUnitId
        this.interstitialVideoAdUnitId = builder.interstitialVideoAdUnitId
        this.nativeAdUnitId = builder.nativeAdUnitId
    }

    class Builder {

        var appId: String? = null
        var bannerAdUnitId: String? = null
        var mediumAdUnitId: String? = null
        var mediumVideoAdUnitId: String? = null
        var leaderboardAdUnitId: String? = null
        var rewardedAdUnitId: String? = null
        var interstitialAdUnitId: String? = null
        var interstitialVideoAdUnitId: String? = null
        var nativeAdUnitId: String? = null

        fun appId(admobAppId: String) =
            apply { this.appId = admobAppId }

        fun bannerAdUnitId(admobBannerAdUnitId: String) =
            apply { this.bannerAdUnitId = admobBannerAdUnitId }

        fun mediumAdUnitId(admobMediumAdUnitId: String) =
            apply { this.mediumAdUnitId = admobMediumAdUnitId }

        fun mediumVideoAdUnitId(admobMediumVideoAdUnitId: String) =
            apply { this.mediumVideoAdUnitId = admobMediumVideoAdUnitId }

        fun leaderboardAdUnitId(admobLeaderboardAdUnitId: String) =
            apply { this.leaderboardAdUnitId = admobLeaderboardAdUnitId }

        fun rewardedAdUnitId(admobRewardedAdUnitId: String) =
            apply { this.rewardedAdUnitId = admobRewardedAdUnitId }

        fun interstitialAdUnitId(admobInterstitialAdUnitId: String) =
            apply { this.interstitialAdUnitId = admobInterstitialAdUnitId }

        fun interstitialVideoAdUnitId(admobInterstitialVideoAdUnitId: String) =
            apply { this.interstitialVideoAdUnitId = admobInterstitialVideoAdUnitId }

        fun nativeAdUnitId(admobNativeAdUnitId: String) =
            apply { this.nativeAdUnitId = admobNativeAdUnitId }

        fun build() = AdmobSettings(this)
    }
}