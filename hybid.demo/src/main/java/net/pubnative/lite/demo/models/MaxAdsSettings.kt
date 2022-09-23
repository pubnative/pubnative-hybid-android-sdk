package net.pubnative.lite.demo.models

class MaxAdsSettings private constructor(builder: Builder) {

    val sdkKey: String?
    val bannerAdUnitId: String?
    val mRectAdUnitId: String?
    val interstitialAdUnitId: String?
    val rewardedAdUnitId: String?
    val nativeAdUnitId: String?

    init {
        this.sdkKey = builder.sdkKey
        this.bannerAdUnitId = builder.bannerAdUnitId
        this.mRectAdUnitId = builder.mRectAdUnitId
        this.interstitialAdUnitId = builder.interstitialAdUnitId
        this.rewardedAdUnitId = builder.rewardedAdUnitId
        this.nativeAdUnitId = builder.nativeAdUnitId
    }

    class Builder {
        var sdkKey: String? = null
            private set
        var bannerAdUnitId: String? = null
            private set
        var mRectAdUnitId: String? = null
            private set
        var interstitialAdUnitId: String? = null
            private set
        var rewardedAdUnitId: String? = null
            private set
        var nativeAdUnitId: String? = null
            private set

        fun sdkKey(maxAdsSdkKey: String) =
            apply { this.sdkKey = maxAdsSdkKey }

        fun bannerAdUnitId(maxAdsBannerAdUnitId: String) =
            apply { this.bannerAdUnitId = maxAdsBannerAdUnitId }

        fun mRectAdUnitId(maxAdsMRectAdUnitId: String) =
            apply { this.mRectAdUnitId = maxAdsMRectAdUnitId }

        fun interstitialAdUnitId(maxAdsInterstitialAdUnitId: String) =
            apply { this.interstitialAdUnitId = maxAdsInterstitialAdUnitId }

        fun rewardedAdUnitId(maxAdsRewardedAdUnitId: String) =
            apply { this.rewardedAdUnitId = maxAdsRewardedAdUnitId }

        fun nativeAdUnitId(maxAdsNativeAdUnitId: String) =
            apply { this.nativeAdUnitId = maxAdsNativeAdUnitId }

        fun build() = MaxAdsSettings(this)
    }
}