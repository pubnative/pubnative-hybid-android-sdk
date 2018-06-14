package net.pubnative.lite.demo.models

import android.os.Parcel
import android.os.Parcelable
import net.pubnative.lite.demo.createParcel

/**
 * Created by erosgarciaponte on 30.01.18.
 */
data class SettingsModel(var appToken: String,
                         var zoneIds: List<String>,
                         var gender: String,
                         var age: String,
                         var keywords: List<String>,
                         var coppa: Boolean,
                         var testMode: Boolean,
                         var mopubBannerAdUnitId: String,
                         var mopubMediumAdUnitId: String,
                         var mopubInterstitialAdUnitId: String,
                         var mopubMediationBannerAdUnitId: String,
                         var mopubMediationMediumAdUnitId: String,
                         var mopubMediationInterstitialAdUnitId: String,
                         var dfpBannerAdUnitId: String,
                         var dfpMediumAdUnitId: String,
                         var dfpInterstitialAdUnitId: String) : Parcelable {
    companion object {
        @JvmField
        @Suppress("unused")
        val CREATOR = createParcel { SettingsModel(it) }
    }

    protected constructor(parcel: Parcel) : this(
            parcel.readString(),
            mutableListOf<String>().apply {
                parcel.readStringList(this)
            },
            parcel.readString(),
            parcel.readString(),
            mutableListOf<String>().apply {
                parcel.readStringList(this)
            },
            parcel.readInt() != 0,
            parcel.readInt() != 0,
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString())

    override fun writeToParcel(dest: Parcel?, flags: Int) {
        dest?.writeString(appToken)

        dest?.writeStringList(zoneIds)

        dest?.writeString(gender)
        dest?.writeString(age)

        dest?.writeStringList(keywords)

        val coppaByte: Int
        if (coppa) {
            coppaByte = 1
        } else {
            coppaByte = 0
        }
        dest?.writeInt(coppaByte)

        val testModeByte: Int
        if (testMode) {
            testModeByte = 1
        } else {
            testModeByte = 0
        }
        dest?.writeInt(testModeByte)

        dest?.writeString(mopubBannerAdUnitId)
        dest?.writeString(mopubMediumAdUnitId)
        dest?.writeString(mopubInterstitialAdUnitId)

        dest?.writeString(mopubMediationBannerAdUnitId)
        dest?.writeString(mopubMediationMediumAdUnitId)
        dest?.writeString(mopubMediationInterstitialAdUnitId)

        dest?.writeString(dfpBannerAdUnitId)
        dest?.writeString(dfpMediumAdUnitId)
        dest?.writeString(dfpInterstitialAdUnitId)
    }

    override fun describeContents(): Int = 0
}