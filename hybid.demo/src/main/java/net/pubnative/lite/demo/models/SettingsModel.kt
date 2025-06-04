// HyBid SDK License
//
// https://github.com/pubnative/pubnative-hybid-android-sdk/blob/main/LICENSE
//
package net.pubnative.lite.demo.models

import android.os.Parcel
import android.os.Parcelable
import net.pubnative.lite.demo.createParcel

/**
 * Created by erosgarciaponte on 30.01.18.
 */
data class SettingsModel(
    var appToken: String,
    var zoneIds: List<String>,
    var apiUrl: String,
    var gender: String,
    var age: String,
    var keywords: List<String>,
    var browserPriorities: List<String>,
    var coppa: Boolean,
    var testMode: Boolean,
    var locationTracking: Boolean,
    var locationUpdates: Boolean,
    var initialAudioState: Int,
    var mraidExpanded: Boolean,
    var closeVideoAfterFinish: Boolean,
    var closeVideoAfterFinishForRewardedVideo: Boolean,
    var enableEndcard: Boolean,
    var skipOffset: Int,
    var videoSkipOffset: Int,
    var endCardCloseButtonDelay: Int,
    var videoClickBehaviour: Boolean,
    var feedbackEnabled: Boolean,
    var feedbackFormUrl: String,
    var dfpBannerAdUnitId: String,
    var dfpMediumAdUnitId: String,
    var dfpLeaderboardAdUnitId: String,
    var dfpInterstitialAdUnitId: String,
    var dfpMediationBannerAdUnitId: String,
    var dfpMediationMediumAdUnitId: String,
    var dfpMediationLeaderboardAdUnitId: String,
    var dfpMediationInterstitialAdUnitId: String,
    var dfpMediationRewardedAdUnitId: String,
    var admobAppId: String,
    var admobBannerAdUnitId: String,
    var admobMediumAdUnitId: String,
    var admobMediumVideoAdUnitId: String,
    var admobLeaderboardAdUnitId: String,
    var admobRewardedAdUnitId: String,
    var admobInterstitialAdUnitId: String,
    var admobInterstitialVideoAdUnitId: String,
    var admobNativeAdUnitId: String,
    var ironSourceAppKey: String,
    var ironSourceBannerAdUnitId: String,
    var ironSourceInterstitialAdUnitId: String,
    var ironSourceRewardedAdUnitId: String,
    var maxAdsSdkKey: String,
    var maxAdsBannerAdUnitId: String,
    var maxAdsMRectAdUnitId: String,
    var maxAdsInterstitialAdUnitId: String,
    var maxAdsRewardedAdUnitId: String,
    var maxAdsNativeAdUnitId: String,
    var fairbidAppId: String,
    var fairbidMediationBannerAdUnitId: String,
    var fairbidMediationInterstitialAdUnitId: String,
    var fairbidMediationRewardedAdUnitId: String,
    var fairbidBannerAdUnitId: String,
    var fairbidInterstitialAdUnitId: String,
    var fairbidRewardedAdUnitId: String
) : Parcelable {

    companion object {
        @JvmField
        @Suppress("unused")
        val CREATOR = createParcel { SettingsModel(it) }
    }

    private constructor(parcel: Parcel) : this(
        parcel.readString()!!,
        mutableListOf<String>().apply {
            parcel.readStringList(this)
        },
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        mutableListOf<String>().apply {
            parcel.readStringList(this)
        },
        mutableListOf<String>().apply {
            parcel.readStringList(this)
        },
        parcel.readInt() != 0,
        parcel.readInt() != 0,
        parcel.readInt() != 0,
        parcel.readInt() != 0,
        parcel.readInt(),
        parcel.readInt() != 0,
        parcel.readInt() != 0,
        parcel.readInt() != 0,
        parcel.readInt() != 0,
        parcel.readInt(),
        parcel.readInt(),
        parcel.readInt(),
        parcel.readInt() != 0,
        parcel.readInt() != 0,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!
    )

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeString(appToken)
        dest.writeStringList(zoneIds)
        dest.writeString(apiUrl)

        dest.writeString(gender)
        dest.writeString(age)

        dest.writeStringList(keywords)
        dest.writeStringList(browserPriorities)

        val coppaByte: Int
        if (coppa) {
            coppaByte = 1
        } else {
            coppaByte = 0
        }
        dest.writeInt(coppaByte)

        val testModeByte: Int = if (testMode) {
            1
        } else {
            0
        }
        dest.writeInt(testModeByte)

        val locationTrackingByte: Int = if (locationTracking) {
            1
        } else {
            0
        }
        dest.writeInt(locationTrackingByte)

        val locationUpdatesByte: Int = if (locationUpdates) {
            1
        } else {
            0
        }
        dest.writeInt(locationUpdatesByte)

        dest.writeInt(initialAudioState)

        val mraidExpandedByte: Int = if (mraidExpanded) {
            1
        } else {
            0
        }
        dest.writeInt(mraidExpandedByte)

        val closeVideoAfterFinishByte: Int = if (closeVideoAfterFinish) {
            1
        } else {
            0
        }
        dest.writeInt(closeVideoAfterFinishByte)

        val enableEndcardByte: Int = if (enableEndcard) {
            1
        } else {
            0
        }
        dest.writeInt(enableEndcardByte)

        dest.writeInt(skipOffset)
        dest.writeInt(videoSkipOffset)
        dest.writeInt(endCardCloseButtonDelay)

        val videoClickBehaviourByte: Int = if (videoClickBehaviour) {
            1
        } else {
            0
        }
        dest.writeInt(videoClickBehaviourByte)

        val feedbackFormEnabled = if(feedbackEnabled) 1 else 0
        dest.writeInt(feedbackFormEnabled)
        dest.writeString(feedbackFormUrl)

        dest.writeString(dfpBannerAdUnitId)
        dest.writeString(dfpMediumAdUnitId)
        dest.writeString(dfpLeaderboardAdUnitId)
        dest.writeString(dfpInterstitialAdUnitId)

        dest.writeString(dfpMediationBannerAdUnitId)
        dest.writeString(dfpMediationMediumAdUnitId)
        dest.writeString(dfpMediationLeaderboardAdUnitId)
        dest.writeString(dfpMediationInterstitialAdUnitId)
        dest.writeString(dfpMediationRewardedAdUnitId)

        dest.writeString(admobAppId)
        dest.writeString(admobBannerAdUnitId)
        dest.writeString(admobMediumAdUnitId)
        dest.writeString(admobMediumVideoAdUnitId)
        dest.writeString(admobLeaderboardAdUnitId)
        dest.writeString(admobRewardedAdUnitId)
        dest.writeString(admobInterstitialAdUnitId)
        dest.writeString(admobInterstitialVideoAdUnitId)
        dest.writeString(admobNativeAdUnitId)

        dest.writeString(ironSourceAppKey)
        dest.writeString(ironSourceBannerAdUnitId)
        dest.writeString(ironSourceInterstitialAdUnitId)
        dest.writeString(ironSourceRewardedAdUnitId)

        dest.writeString(maxAdsSdkKey)
        dest.writeString(maxAdsBannerAdUnitId)
        dest.writeString(maxAdsMRectAdUnitId)
        dest.writeString(maxAdsInterstitialAdUnitId)
        dest.writeString(maxAdsRewardedAdUnitId)
        dest.writeString(maxAdsNativeAdUnitId)

        dest.writeString(fairbidAppId)

        dest.writeString(fairbidMediationBannerAdUnitId)
        dest.writeString(fairbidMediationInterstitialAdUnitId)
        dest.writeString(fairbidMediationRewardedAdUnitId)

        dest.writeString(fairbidBannerAdUnitId)
        dest.writeString(fairbidInterstitialAdUnitId)
        dest.writeString(fairbidRewardedAdUnitId)
    }

    override fun describeContents(): Int = 0
}