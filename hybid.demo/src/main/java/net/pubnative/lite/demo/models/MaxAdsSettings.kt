package net.pubnative.lite.demo.models

class MaxAdsSettings private constructor(builder: Builder) {

    val sdkKey: String?
    val bannerAdUnitId: String?
    val mRectAdUnitId: String?
    val mRectVideoAdUnitId: String?
    val interstitialAdUnitId: String?
    val interstitialVideoAdUnitId: String?
    val rewardedAdUnitId: String?
    val rewardedVideoAdUnitId: String?
    val nativeAdUnitId: String?

    init {
        this.sdkKey = builder.sdkKey
        this.bannerAdUnitId = builder.bannerAdUnitId
        this.mRectAdUnitId = builder.mRectAdUnitId
        this.mRectVideoAdUnitId = builder.mRectVideoAdUnitId
        this.interstitialAdUnitId = builder.interstitialAdUnitId
        this.interstitialVideoAdUnitId = builder.interstitialVideoAdUnitId
        this.rewardedAdUnitId = builder.rewardedAdUnitId
        this.rewardedVideoAdUnitId = builder.rewardedVideoAdUnitId
        this.nativeAdUnitId = builder.nativeAdUnitId
    }

    class Builder {
        var sdkKey: String? = null
            private set
        var bannerAdUnitId: String? = null
            private set
        var mRectAdUnitId: String? = null
            private set
        var mRectVideoAdUnitId: String? = null
            private set
        var interstitialAdUnitId: String? = null
            private set
        var interstitialVideoAdUnitId: String? = null
            private set
        var rewardedAdUnitId: String? = null
            private set
        var rewardedVideoAdUnitId: String? = null
            private set
        var nativeAdUnitId: String? = null
            private set

        fun sdkKey(maxAdsSdkKey: String) =
            apply { this.sdkKey = maxAdsSdkKey }

        fun bannerAdUnitId(maxAdsBannerAdUnitId: String) =
            apply { this.bannerAdUnitId = maxAdsBannerAdUnitId }

        fun mRectAdUnitId(maxAdsMRectAdUnitId: String) =
            apply { this.mRectAdUnitId = maxAdsMRectAdUnitId }

        fun mRectVideoAdUnitId(mRectVideoAdUnitId: String) =
            apply { this.mRectVideoAdUnitId = mRectVideoAdUnitId }

        fun interstitialAdUnitId(maxAdsInterstitialAdUnitId: String) =
            apply { this.interstitialAdUnitId = maxAdsInterstitialAdUnitId }

        fun interstitialVideoAdUnitId(maxAdsInterstitialVideoAdUnitId: String) =
            apply { this.interstitialVideoAdUnitId = maxAdsInterstitialVideoAdUnitId }

        fun rewardedAdUnitId(maxAdsRewardedAdUnitId: String) =
            apply { this.rewardedAdUnitId = maxAdsRewardedAdUnitId }

        fun rewardedVideoAdUnitId(maxAdsRewardedVideoAdUnitId :String) =
            apply { this.rewardedVideoAdUnitId = maxAdsRewardedVideoAdUnitId }

        fun nativeAdUnitId(maxAdsNativeAdUnitId: String) =
            apply { this.nativeAdUnitId = maxAdsNativeAdUnitId }

        fun build() = MaxAdsSettings(this)
    }
}